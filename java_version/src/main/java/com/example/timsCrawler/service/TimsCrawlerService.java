package com.example.timsCrawler.service;

import com.example.timsCrawler.domain.Member;
import com.example.timsCrawler.domain.dto.WorkTimeResponseDto;
import jakarta.servlet.http.Cookie;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TimsCrawlerService {
    final String timsUrl = "https://tims.tmax.co.kr";
    final String timsLoginUrl = "https://otims.tmax.co.kr/checkUserInfo.tmv?tmaxsso_nsso=no";
    String userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36";

    public void tryLogin(Member member) throws IOException {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("userId", member.getUsername());
        loginData.put("passwd", member.getPassword());
        loginData.put("company", member.getCompany());

        Connection.Response loginPageResponse = Jsoup.connect(timsLoginUrl)
                .userAgent(userAgent)
                .method(Connection.Method.POST)
                .data(loginData)
                .header("Content-Type","application/x-www-form-urlencoded")
                .header("Referer","https://tims.tmax.co.kr/")
                .header("Origin","https://tims.tmax.co.kr")
//                .header("Host","sso.tmax.co.kr")
                .execute();

        member.nullifyLoginData();
        member.setLoginCookie(loginPageResponse.cookies());
        System.out.println("cookie = " + loginPageResponse.cookies());
    }

    public void getYearAttendanceList(Cookie[] cookies) throws IOException {
        Map<String, String> loginCookie = new HashMap<>();
        for (Cookie cookie : cookies) {
            loginCookie.put(cookie.getName(), cookie.getValue());
        }

        SimpleDateFormat timsDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        Date today = new Date();
        String dateToday = timsDateFormat.format(today);

        String attendanceUrl = "https://otims.tmax.co.kr/insa/attend/findAttdDailyConfirm.screen";

        Map<String,String> attendanceForm = new HashMap<>();
        attendanceForm.put("retStDate",dateToday.substring(0,4)+".01.01");
        attendanceForm.put("retEdDate",dateToday);

        Connection.Response attendanceResponse = Jsoup.connect(attendanceUrl)
                .method(Connection.Method.POST)
                .cookies(loginCookie)
                .userAgent(userAgent)
                .data(attendanceForm)
                .header("Content-Type","application/x-www-form-urlencoded")
                .header("Referer","https://tims.tmax.co.kr/")
                .header("Origin","https://tims.tmax.co.kr")
                .execute();

        Document attendanceYearDocument= attendanceResponse.parse();
        getLateTime(attendanceYearDocument);
    }

    public WorkTimeResponseDto getWeekAttendanceList(Cookie[] cookies) throws IOException {
        Map<String, String> loginCookie = new HashMap<>();
        for (Cookie cookie : cookies) {
            loginCookie.put(cookie.getName(), cookie.getValue());
        }

        SimpleDateFormat timsDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        Date today = new Date();
        String dateToday = timsDateFormat.format(today);
        String dateMonday = getDateMonday();

        String attendanceUrl = "https://otims.tmax.co.kr/insa/attend/findAttdDailyConfirm.screen";

        Map<String,String> attendanceForm = new HashMap<>();
        attendanceForm.put("retStDate",dateMonday);
        attendanceForm.put("retEdDate",dateToday);

        Connection.Response attendanceResponse = Jsoup.connect(attendanceUrl)
                .method(Connection.Method.POST)
                .cookies(loginCookie)
                .userAgent(userAgent)
                .data(attendanceForm)
                .header("Content-Type","application/x-www-form-urlencoded")
                .header("Referer","https://tims.tmax.co.kr/")
                .header("Origin","https://tims.tmax.co.kr")
                .execute();

        Document attendanceWeekDocument = attendanceResponse.parse();
        return setWorkTimeResponseDto(getWorkTimeForWeek(attendanceWeekDocument));
    }
    public WorkTimeResponseDto setWorkTimeResponseDto(int workTime){
        return WorkTimeResponseDto.builder()
                .totalMin(workTime)
                .min(workTime%60)
                .hour(workTime/60)
                .build();
    }

    public void getLateTime(Document attendanceYearDocument) {
        Elements lateElements = attendanceYearDocument.select("table tr:has(td:contains(지각))");
        int totalLateTime = 0;
        for (Element row : lateElements) {
            Element lateTimeCell = row.select("td:nth-of-type(10)").first();

            if (lateTimeCell != null) {
                String lateTimeHour = lateTimeCell.text().substring(0,2);
                String lateTimeMin = lateTimeCell.text().substring(3,5);

                int lateHourInt = Integer.parseInt(lateTimeHour);
                int lateMinInt = Integer.parseInt(lateTimeMin);

                totalLateTime += 60*(lateHourInt - 9) + lateMinInt;
            }
        }
        System.out.println("지각 몇 분 ?: "+ totalLateTime + "분");
    }

    public int getWorkTimeForWeek(Document attendanceWeekDocument) {
        int workTime = 0;

        //정상 근무시간 합
        Elements normalElements = attendanceWeekDocument.select("table tr:has(td:contains(정상))");
        workTime += getWorkTimeToday(normalElements,9,18);
        //지각 근무시간 합
        Elements lateElements = attendanceWeekDocument.select("table tr:has(td:contains(지각))");
        workTime += getWorkTimeToday(lateElements,9,18);
        //반차 근무시간 합
        Elements foreOffElements = attendanceWeekDocument.select("table tr:has(td:contains(오전))");
        workTime += getWorkTimeToday(foreOffElements, 9,14);
        Elements afterOffElements = attendanceWeekDocument.select("table tr:has(td:contains(오후))");
        workTime += getWorkTimeToday(afterOffElements, 14,18);
        //휴가 근무시간 합
        Elements offElements = attendanceWeekDocument.select("table tr:has(td:contains(휴가))");
        System.out.println("offElements.size() = " + offElements.size());
        for(Element row : offElements){
            if(row == null){
                System.out.println("offElements is null");
            }
            workTime += 60*8;
        }

        System.out.println("workTime = " + workTime);
        return workTime;
    }

    private static int getWorkTimeToday(Elements elements, int defaultInitHour, int defaultExitHour) {
        int workTime = 0;
        for(Element row : elements){
            Element initTimeCell = row.select("td:nth-of-type(10)").first();
            int initTimeMin = 0;
            int initTimeHour = defaultInitHour;

            if (initTimeCell != null){
                String initTimeHourStr = initTimeCell.text().substring(0,2);
                String initTimeMinStr = initTimeCell.text().substring(3,5);

                initTimeHour = Integer.parseInt(initTimeHourStr);
                initTimeMin = Integer.parseInt(initTimeMinStr);
            }

            Element exitTimeCell = row.select("td:nth-of-type(13)").first();

            int exitTimeMin = 0;
            int exitTimeHour = defaultExitHour;

            if (exitTimeCell != null){
                String exitTimeHourStr = exitTimeCell.text().substring(0,2);
                String exitTimeMinStr = exitTimeCell.text().substring(3,5);

                exitTimeHour = Integer.parseInt(exitTimeHourStr);
                exitTimeMin = Integer.parseInt(exitTimeMinStr);
            }

            if(initTimeCell==null && exitTimeCell == null){
                continue;
            }

            workTime += (exitTimeHour - initTimeHour -1)*60 + exitTimeMin - initTimeMin;
            //점심식사 시간 제외
            //점심시간 이전에 퇴근하는 경우 1시간이 더 빠지는 예외 발생
            if(defaultInitHour == 9){
                workTime -= 1;
            }
            //저녁식사 시간 제외
            if(exitTimeHour >= 19){
                workTime -= 1;
            }
            System.out.println(row.select("td:nth-of-type(8)").first().text());
            System.out.println("workTimeToday = " + workTime);
        }
        return workTime;
    }

    private String getDateMonday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        Date date = calendar.getTime();
        SimpleDateFormat timsDateFormat = new SimpleDateFormat("yyyy.MM.dd");

        return timsDateFormat.format(date);
    }

}