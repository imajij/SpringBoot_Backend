package com.financetracker.service.impl;

import com.financetracker.dto.CategoryBreakdownDto;
import com.financetracker.dto.DashboardStatsDto;
import com.financetracker.dto.MonthlyTrendDto;
import com.financetracker.entity.Budget;
import com.financetracker.entity.Expense;
import com.financetracker.entity.SavingsGoal;
import com.financetracker.entity.SplitBill;
import com.financetracker.repository.BudgetRepository;
import com.financetracker.repository.ExpenseRepository;
import com.financetracker.repository.SavingsGoalRepository;
import com.financetracker.repository.SplitBillRepository;
import com.financetracker.security.SecurityUtils;
import com.financetracker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ExpenseRepository expenseRepository;
    private final SavingsGoalRepository savingsGoalRepository;
    private final SplitBillRepository splitBillRepository;
    private final BudgetRepository budgetRepository;
    private final SecurityUtils securityUtils;

    @Override
    public DashboardStatsDto getStats() {
        String userId = securityUtils.getCurrentUserId();
        log.info("Fetching dashboard stats for user: {}", userId);

        YearMonth currentMonth = YearMonth.now();
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        // Calculate total expenses
        List<Expense> allExpenses = expenseRepository.findByUserId(userId);
        BigDecimal totalExpenses = allExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Current month expenses
        List<Expense> currentMonthExpenses = expenseRepository
                .findByUserIdAndDateBetween(userId, startOfMonth, endOfMonth);
        BigDecimal currentMonthTotal = currentMonthExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Savings
        List<SavingsGoal> goals = savingsGoalRepository.findByUserId(userId);
        BigDecimal totalSavings = goals.stream()
                .map(SavingsGoal::getCurrentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int activeGoals = (int) goals.stream().filter(g -> !g.isCompleted()).count();
        int completedGoals = goals.size() - activeGoals;

        // Budget
        BigDecimal budgetLimit = BigDecimal.ZERO;
        BigDecimal budgetRemaining = BigDecimal.ZERO;
        BigDecimal budgetPercentUsed = BigDecimal.ZERO;

        var budgetOpt = budgetRepository.findByUserIdAndMonthAndYear(
                userId, currentMonth.getMonthValue(), currentMonth.getYear());
        if (budgetOpt.isPresent()) {
            Budget budget = budgetOpt.get();
            budgetLimit = budget.getMonthlyLimit();
            budgetRemaining = budgetLimit.subtract(currentMonthTotal);
            budgetPercentUsed = budgetLimit.compareTo(BigDecimal.ZERO) > 0
                    ? currentMonthTotal.multiply(BigDecimal.valueOf(100))
                    .divide(budgetLimit, 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
        }

        // Pending split bills
        List<SplitBill> pendingSplitBills = splitBillRepository.findByUserIdAndSettled(userId, false);

        return DashboardStatsDto.builder()
                .totalExpenses(totalExpenses)
                .totalSavings(totalSavings)
                .currentMonthExpenses(currentMonthTotal)
                .budgetLimit(budgetLimit)
                .budgetRemaining(budgetRemaining)
                .budgetPercentUsed(budgetPercentUsed)
                .activeGoals(activeGoals)
                .completedGoals(completedGoals)
                .pendingSplitBills(pendingSplitBills.size())
                .build();
    }

    @Override
    public List<CategoryBreakdownDto> getSpendingBreakdown() {
        String userId = securityUtils.getCurrentUserId();
        log.info("Fetching spending breakdown for user: {}", userId);

        YearMonth currentMonth = YearMonth.now();
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        var categoryStats = expenseRepository
                .getCategoryStatsByUserIdAndDateBetween(userId, startOfMonth, endOfMonth);

        BigDecimal totalAmount = categoryStats.stream()
                .map(s -> BigDecimal.valueOf(s.getTotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return categoryStats.stream()
                .map(stat -> {
                    BigDecimal amount = BigDecimal.valueOf(stat.getTotal());
                    BigDecimal percentage = totalAmount.compareTo(BigDecimal.ZERO) > 0
                            ? amount.multiply(BigDecimal.valueOf(100))
                            .divide(totalAmount, 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    return CategoryBreakdownDto.builder()
                            .category(stat.get_id())
                            .amount(amount)
                            .count(stat.getCount())
                            .percentage(percentage)
                            .build();
                })
                .toList();
    }

    @Override
    public List<MonthlyTrendDto> getSpendingTrends(Integer months) {
        String userId = securityUtils.getCurrentUserId();
        int numMonths = months != null ? months : 6;
        log.info("Fetching spending trends for last {} months for user: {}", numMonths, userId);

        List<MonthlyTrendDto> trends = new ArrayList<>();
        YearMonth currentMonth = YearMonth.now();

        for (int i = numMonths - 1; i >= 0; i--) {
            YearMonth targetMonth = currentMonth.minusMonths(i);
            LocalDate startDate = targetMonth.atDay(1);
            LocalDate endDate = targetMonth.atEndOfMonth();

            List<Expense> expenses = expenseRepository
                    .findByUserIdAndDateBetween(userId, startDate, endDate);

            BigDecimal amount = expenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            trends.add(MonthlyTrendDto.builder()
                    .month(targetMonth.getMonthValue())
                    .year(targetMonth.getYear())
                    .monthName(targetMonth.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                    .amount(amount)
                    .build());
        }

        return trends;
    }
}
