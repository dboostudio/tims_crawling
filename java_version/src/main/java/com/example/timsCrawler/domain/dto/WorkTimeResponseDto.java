package com.example.timsCrawler.domain.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkTimeResponseDto {
    String name;
    Integer hour;
    int min;
    int totalMin;
}
