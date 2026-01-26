package com.financetracker.service.impl;

import com.financetracker.dto.BudgetDto;
import com.financetracker.entity.Budget;
import com.financetracker.entity.Expense;
import com.financetracker.exception.ResourceNotFoundException;
import com.financetracker.mapper.BudgetMapper;
import com.financetracker.repository.BudgetRepository;
import com.financetracker.repository.ExpenseRepository;
import com.financetracker.security.SecurityUtils;
import com.financetracker.service.BudgetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetMapper budgetMapper;
    private final SecurityUtils securityUtils;

    @Override
    public BudgetDto getCurrentBudget() {
        YearMonth currentMonth = YearMonth.now();
        return getBudgetByMonthAndYear(currentMonth.getMonthValue(), currentMonth.getYear());
    }

    @Override
    public BudgetDto getBudgetByMonthAndYear(Integer month, Integer year) {
        String userId = securityUtils.getCurrentUserId();
        log.info("Fetching budget for {}/{} for user: {}", month, year, userId);

        Budget budget = budgetRepository.findByUserIdAndMonthAndYear(userId, month, year)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Budget not found for %d/%d", month, year)));

        return enrichBudgetWithSpending(budget);
    }

    @Override
    public BudgetDto createOrUpdateBudget(BudgetDto budgetDto) {
        String userId = securityUtils.getCurrentUserId();
        log.info("Creating/updating budget for {}/{} for user: {}",
                budgetDto.getMonth(), budgetDto.getYear(), userId);

        Budget budget = budgetRepository
                .findByUserIdAndMonthAndYear(userId, budgetDto.getMonth(), budgetDto.getYear())
                .orElse(Budget.builder()
                        .userId(userId)
                        .month(budgetDto.getMonth())
                        .year(budgetDto.getYear())
                        .build());

        budget.setMonthlyLimit(budgetDto.getMonthlyLimit());
        Budget savedBudget = budgetRepository.save(budget);

        log.info("Budget saved with ID: {}", savedBudget.getId());
        return enrichBudgetWithSpending(savedBudget);
    }

    private BudgetDto enrichBudgetWithSpending(Budget budget) {
        String userId = budget.getUserId();

        LocalDate startDate = LocalDate.of(budget.getYear(), budget.getMonth(), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Expense> expenses = expenseRepository.findByUserIdAndDateBetween(userId, startDate, endDate);

        BigDecimal spent = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remaining = budget.getMonthlyLimit().subtract(spent);
        BigDecimal percentUsed = budget.getMonthlyLimit().compareTo(BigDecimal.ZERO) > 0
                ? spent.multiply(BigDecimal.valueOf(100)).divide(budget.getMonthlyLimit(), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BudgetDto dto = budgetMapper.toDto(budget);
        dto.setSpent(spent);
        dto.setRemaining(remaining);
        dto.setPercentUsed(percentUsed);

        return dto;
    }
}
