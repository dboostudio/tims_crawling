package com.example.timsCrawler.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class TimsCrawlerController {
    @GetMapping("/tims-crawler/main")
    public String mainPage(){
        return "crawler_user_info";
    }
}
