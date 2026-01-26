package com.financetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {

    private BigDecimal totalExpenses;
    private BigDecimal totalSavings;
    private BigDecimal currentMonthExpenses;
    private BigDecimal budgetRemaining;
    private BigDecimal budgetLimit;
    private BigDecimal budgetPercentUsed;
    private Integer activeGoals;
    private Integer completedGoals;
    private Integer pendingSplitBills;
}
