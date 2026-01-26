package com.financetracker.controller;

import com.financetracker.dto.ApiResponse;
import com.financetracker.dto.BudgetDto;
import com.financetracker.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public ResponseEntity<ApiResponse<BudgetDto>> getCurrentBudget() {
        log.info("Fetching current budget");
        BudgetDto budget = budgetService.getCurrentBudget();
        return ResponseEntity.ok(ApiResponse.success(budget));
    }

    @GetMapping("/{month}/{year}")
    public ResponseEntity<ApiResponse<BudgetDto>> getBudgetByMonthAndYear(
            @PathVariable Integer month,
            @PathVariable Integer year) {
        log.info("Fetching budget for {}/{}", month, year);
        BudgetDto budget = budgetService.getBudgetByMonthAndYear(month, year);
        return ResponseEntity.ok(ApiResponse.success(budget));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BudgetDto>> createOrUpdateBudget(@Valid @RequestBody BudgetDto budgetDto) {
        log.info("Creating/updating budget for {}/{}", budgetDto.getMonth(), budgetDto.getYear());
        BudgetDto savedBudget = budgetService.createOrUpdateBudget(budgetDto);
        return ResponseEntity.ok(ApiResponse.success("Budget saved successfully", savedBudget));
    }
}
