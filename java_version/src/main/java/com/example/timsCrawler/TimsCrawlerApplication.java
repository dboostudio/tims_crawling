package com.example.timsCrawler;

import com.example.timsCrawler.service.TimsCrawlerService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class TimsCrawlerApplication {

	public static void main(String[] args) throws InterruptedException, IOException {
		SpringApplication.run(TimsCrawlerApplication.class, args);
		TimsCrawlerService timsCrawlerService = new TimsCrawlerService();

		timsCrawlerService.tryLogin();
		timsCrawlerService.getYearAttendanceList();
		timsCrawlerService.getLateTime();
		timsCrawlerService.getWeekAttendanceList();
		timsCrawlerService.getWorkTime();

	}

}
