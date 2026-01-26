package com.financetracker.controller;

import com.financetracker.dto.ApiResponse;
import com.financetracker.dto.CategoryBreakdownDto;
import com.financetracker.dto.DashboardStatsDto;
import com.financetracker.dto.MonthlyTrendDto;
import com.financetracker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getStats() {
        log.info("Fetching dashboard stats");
        DashboardStatsDto stats = dashboardService.getStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/spending-breakdown")
    public ResponseEntity<ApiResponse<List<CategoryBreakdownDto>>> getSpendingBreakdown() {
        log.info("Fetching spending breakdown");
        List<CategoryBreakdownDto> breakdown = dashboardService.getSpendingBreakdown();
        return ResponseEntity.ok(ApiResponse.success(breakdown));
    }

    @GetMapping("/spending-trends")
    public ResponseEntity<ApiResponse<List<MonthlyTrendDto>>> getSpendingTrends(
            @RequestParam(defaultValue = "6") Integer months) {
        log.info("Fetching spending trends for last {} months", months);
        List<MonthlyTrendDto> trends = dashboardService.getSpendingTrends(months);
        return ResponseEntity.ok(ApiResponse.success(trends));
    }
}
