package com.example.timsCrawler.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkTimeResponseDto {
    String name;
    int hour;
    int min;
    int totalMin;
}
