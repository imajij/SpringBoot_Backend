package com.financetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financetracker.dto.ApiResponse;
import com.financetracker.dto.ExpenseDto;
import com.financetracker.dto.MonthlyExpenseStatsDto;
import com.financetracker.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ExpenseDto>>> getAllExpenses(
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Fetching all expenses");
        Page<ExpenseDto> expenses = expenseService.getAllExpenses(pageable);
        return ResponseEntity.ok(ApiResponse.success(expenses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseDto>> getExpenseById(@PathVariable String id) {
        log.info("Fetching expense by ID: {}", id);
        ExpenseDto expense = expenseService.getExpenseById(id);
        return ResponseEntity.ok(ApiResponse.success(expense));
    }


    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<ExpenseDto>> createExpenseWithFile(
            @RequestPart("expense") String expenseJson,
            @RequestPart(value = "billPhoto", required = false) org.springframework.web.multipart.MultipartFile billPhoto) {
        log.info("Creating new expense with file upload");
        try {
            ExpenseDto expenseDto = objectMapper.readValue(expenseJson, ExpenseDto.class);
            ExpenseDto createdExpense = expenseService.createExpenseWithFile(expenseDto, billPhoto);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Expense created successfully", createdExpense));
        } catch (Exception e) {
            log.error("Error parsing expense JSON: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid expense data: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseDto>> updateExpense(
            @PathVariable String id,
            @Valid @RequestBody ExpenseDto expenseDto) {
        log.info("Updating expense: {}", id);
        ExpenseDto updatedExpense = expenseService.updateExpense(id, expenseDto);
        return ResponseEntity.ok(ApiResponse.success("Expense updated successfully", updatedExpense));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(@PathVariable String id) {
        log.info("Deleting expense: {}", id);
        expenseService.deleteExpense(id);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully"));
    }

    @GetMapping("/stats/monthly")
    public ResponseEntity<ApiResponse<MonthlyExpenseStatsDto>> getMonthlyStats(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        log.info("Fetching monthly stats for {}/{}", month, year);
        MonthlyExpenseStatsDto stats = expenseService.getMonthlyStats(month, year);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        log.info("Fetching expense categories");
        List<String> categories = expenseService.getCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<ExpenseDto>>> getExpensesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Fetching expenses from {} to {}", startDate, endDate);
        List<ExpenseDto> expenses = expenseService.getExpensesByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(expenses));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<ExpenseDto>>> getExpensesByCategory(@PathVariable String category) {
        log.info("Fetching expenses by category: {}", category);
        List<ExpenseDto> expenses = expenseService.getExpensesByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(expenses));
    }
}
