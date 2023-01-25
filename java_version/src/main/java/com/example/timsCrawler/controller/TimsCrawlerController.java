package com.example.timsCrawler.controller;

import com.example.timsCrawler.domain.dto.WorkTimeResponseDto;
import com.example.timsCrawler.service.TimsCrawlerService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class TimsCrawlerController {
    private final TimsCrawlerService timsCrawlerService;
    @GetMapping("/tims-crawler/main")
    public String mainPage(){
        return "crawler_user_info";
    }

    @GetMapping("/tims-crawler/dashboard")
    public String dashboardPage(Model model) throws IOException {
//            Cookie[] cookies = new Cookie[1];
//        WorkTimeResponseDto response = timsCrawlerService.getWeekAttendanceList(cookies);
        // todo 이름 넘겨주는 로직 추가되면 testResponse 없애고 실제 response를 넘겨줘야 함
        WorkTimeResponseDto testResponse = WorkTimeResponseDto.builder()
                .hour(10)
                .min(34)
                .totalMin(634)
                .name("아작체")
                .build();
        model.addAttribute("response", testResponse);
        return "crawler_dashboard";
    }

}