package com.example.timsCrawler.service;

import com.example.timsCrawler.domain.Member;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class TimsCrawlerService {
    final String timsUrl = "https://tims.tmax.co.kr";
    final String timsLoginUrl = "https://otims.tmax.co.kr/checkUserInfo.tmv?tmaxsso_nsso=no";
    Map<String, String> loginData = new HashMap<>();
    String userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36";
    Map<String, String> loginCookie;
    Document attendanceYearDocument, attendanceWeekDocument;

    public Map<String, String> getLoginCookie() {
        return loginCookie;
    }

    public void tryLogin(Member member) throws IOException {
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

        loginCookie = loginPageResponse.cookies();
    }

    public void getYearAttendanceList() throws IOException {
        SimpleDateFormat timsDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        Date today = new Date();
        String dateToday = timsDateFormat.format(today);

        String attendanceUrl = "https://otims.tmax.co.kr/insa/attend/findAttdDailyConfirm.screen";

        Map<String,String> attendanceForm = new HashMap<>();
        attendanceForm.put("retStDate",dateToday.substring(0,3)+".01.01");
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

        attendanceYearDocument= attendanceResponse.parse();
    }

    public void getWeekAttendanceList() throws IOException {
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

        attendanceWeekDocument = attendanceResponse.parse();
    }

    public void getLateTime() {
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

    public void getWorkTime() {
        Elements normalElements = attendanceWeekDocument.select("table tr:has(td:contains(정상))");
        Elements halfOffElements = attendanceWeekDocument.select("table tr:has(td:contains(반차))");
        Elements offElements = attendanceWeekDocument.select("table tr:has(td:contains(휴가))");
    }

    private String getDateMonday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        Date date = calendar.getTime();
        SimpleDateFormat timsDateFormat = new SimpleDateFormat("yyyy.MM.dd");

        return timsDateFormat.format(date);
    }

}