package com.financetracker.controller;

import com.financetracker.dto.ApiResponse;
import com.financetracker.dto.DepositRequest;
import com.financetracker.dto.SavingsGoalDto;
import com.financetracker.dto.SavingsProgressDto;
import com.financetracker.service.SavingsGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/savings")
@RequiredArgsConstructor
public class SavingsController {

    private final SavingsGoalService savingsGoalService;

    @GetMapping("/goals")
    public ResponseEntity<ApiResponse<List<SavingsGoalDto>>> getAllGoals() {
        log.info("Fetching all savings goals");
        List<SavingsGoalDto> goals = savingsGoalService.getAllGoals();
        return ResponseEntity.ok(ApiResponse.success(goals));
    }

    @GetMapping("/goals/{id}")
    public ResponseEntity<ApiResponse<SavingsGoalDto>> getGoalById(@PathVariable String id) {
        log.info("Fetching savings goal: {}", id);
        SavingsGoalDto goal = savingsGoalService.getGoalById(id);
        return ResponseEntity.ok(ApiResponse.success(goal));
    }

    @PostMapping("/goals")
    public ResponseEntity<ApiResponse<SavingsGoalDto>> createGoal(@Valid @RequestBody SavingsGoalDto goalDto) {
        log.info("Creating new savings goal");
        SavingsGoalDto createdGoal = savingsGoalService.createGoal(goalDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Savings goal created successfully", createdGoal));
    }

    @PutMapping("/goals/{id}")
    public ResponseEntity<ApiResponse<SavingsGoalDto>> updateGoal(
            @PathVariable String id,
            @Valid @RequestBody SavingsGoalDto goalDto) {
        log.info("Updating savings goal: {}", id);
        SavingsGoalDto updatedGoal = savingsGoalService.updateGoal(id, goalDto);
        return ResponseEntity.ok(ApiResponse.success("Savings goal updated successfully", updatedGoal));
    }

    @DeleteMapping("/goals/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGoal(@PathVariable String id) {
        log.info("Deleting savings goal: {}", id);
        savingsGoalService.deleteGoal(id);
        return ResponseEntity.ok(ApiResponse.success("Savings goal deleted successfully"));
    }

    @PostMapping("/goals/{id}/deposit")
    public ResponseEntity<ApiResponse<SavingsGoalDto>> deposit(
            @PathVariable String id,
            @Valid @RequestBody DepositRequest request) {
        log.info("Depositing to savings goal: {}", id);
        SavingsGoalDto updatedGoal = savingsGoalService.deposit(id, request);
        return ResponseEntity.ok(ApiResponse.success("Deposit successful", updatedGoal));
    }

    @GetMapping("/progress")
    public ResponseEntity<ApiResponse<SavingsProgressDto>> getProgress() {
        log.info("Fetching overall savings progress");
        SavingsProgressDto progress = savingsGoalService.getProgress();
        return ResponseEntity.ok(ApiResponse.success(progress));
    }
}
