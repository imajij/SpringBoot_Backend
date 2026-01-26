package com.financetracker.service;

import com.financetracker.dto.CategoryBreakdownDto;
import com.financetracker.dto.DashboardStatsDto;
import com.financetracker.dto.MonthlyTrendDto;

import java.util.List;

public interface DashboardService {

    DashboardStatsDto getStats();

    List<CategoryBreakdownDto> getSpendingBreakdown();

    List<MonthlyTrendDto> getSpendingTrends(Integer months);
}
