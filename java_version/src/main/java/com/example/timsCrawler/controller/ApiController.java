package com.example.timsCrawler.controller;

import com.example.timsCrawler.domain.Member;
import com.example.timsCrawler.domain.dto.WorkTimeResponseDto;
import com.example.timsCrawler.service.TimsCrawlerService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ApiController {

    private final TimsCrawlerService timsCrawlerService;

    @PostMapping(path="/login")
    public void login(@RequestBody Member member, HttpServletResponse response) throws IOException {
        timsCrawlerService.tryLogin(member);
        member.setResponseCookie();
        response.addCookie(member.getCookie());
    }

    @GetMapping(path = "/late-time")
    public void getTotalLateTime(HttpServletRequest request) throws IOException {
        Cookie[] cookies = request.getCookies();
        timsCrawlerService.getYearAttendanceList(cookies);
    }

    @GetMapping(path="/work-time")
    public ResponseEntity<WorkTimeResponseDto> getWorkTime(HttpServletRequest request) throws IOException{
        Cookie[] cookies = request.getCookies();
        return ResponseEntity.ok(timsCrawlerService.getWeekAttendanceList(cookies));
    }
}