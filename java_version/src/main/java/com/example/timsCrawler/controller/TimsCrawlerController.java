package com.example.timsCrawler.controller;

import com.example.timsCrawler.domain.Member;
import com.example.timsCrawler.service.TimsCrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class TimsCrawlerController {

    private final TimsCrawlerService timsCrawlerService;

    @GetMapping("/tims-crawler/main")
    public String mainPage(){
        return "crawler_user_info";
    }

    @PostMapping(path="/login")
    public void login(@RequestBody Member member) throws IOException {
        timsCrawlerService.tryLogin(member);
    }
}
