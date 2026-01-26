package com.financetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsProgressDto {

    private BigDecimal totalTargetAmount;
    private BigDecimal totalSavedAmount;
    private BigDecimal overallProgress;
    private Integer totalGoals;
    private Integer completedGoals;
    private Integer activeGoals;
}
