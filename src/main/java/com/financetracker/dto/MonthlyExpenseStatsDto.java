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
public class MonthlyExpenseStatsDto {

    private Integer month;
    private Integer year;
    private BigDecimal totalAmount;
    private Integer totalTransactions;
    private BigDecimal averageAmount;
    private String topCategory;
    private BigDecimal topCategoryAmount;
}
