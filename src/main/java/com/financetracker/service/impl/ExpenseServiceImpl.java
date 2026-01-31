package com.financetracker.service.impl;

import com.financetracker.dto.ExpenseDto;
import com.financetracker.dto.MonthlyExpenseStatsDto;
import com.financetracker.entity.Expense;
import com.financetracker.exception.ResourceNotFoundException;
import com.financetracker.mapper.ExpenseMapper;
import com.financetracker.repository.ExpenseRepository;
import com.financetracker.security.SecurityUtils;
import com.financetracker.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final SecurityUtils securityUtils;
    private final com.financetracker.service.FileStorageService fileStorageService;

    private static final List<String> DEFAULT_CATEGORIES = Arrays.asList(
            "Food & Dining", "Transportation", "Shopping", "Entertainment",
            "Bills & Utilities", "Healthcare", "Education", "Travel", "Other"
    );


    @Override
    public ExpenseDto createExpense(ExpenseDto expenseDto) {
        String userId = securityUtils.getCurrentUserId();
        log.info("Creating expense for user: {}", userId);

        Expense expense = expenseMapper.toEntity(expenseDto);
        expense.setUserId(userId);

        Expense savedExpense = expenseRepository.save(expense);
        log.info("Expense created with ID: {}", savedExpense.getId());

        return expenseMapper.toDto(savedExpense);
    }

    @Override
    public ExpenseDto createExpenseWithFile(ExpenseDto expenseDto, org.springframework.web.multipart.MultipartFile billPhoto) {
        String userId = securityUtils.getCurrentUserId();
        log.info("Creating expense with file for user: {}", userId);

        Expense expense = expenseMapper.toEntity(expenseDto);
        expense.setUserId(userId);

        if (billPhoto != null && !billPhoto.isEmpty()) {
            String fileName = fileStorageService.storeFile(billPhoto);
            expense.setBillPhoto(fileName);
        }

        Expense savedExpense = expenseRepository.save(expense);
        log.info("Expense created with ID: {} and bill photo: {}", savedExpense.getId(), savedExpense.getBillPhoto());

        return expenseMapper.toDto(savedExpense);
    }

    @Override
    public ExpenseDto getExpenseById(String id) {
        String userId = securityUtils.getCurrentUserId();
        log.info("Fetching expense {} for user: {}", id, userId);

        Expense expense = expenseRepository.findById(id)
                .filter(e -> e.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));

        return expenseMapper.toDto(expense);
    }

    @Override
    public Page<ExpenseDto> getAllExpenses(Pageable pageable) {
        String userId = securityUtils.getCurrentUserId();
        log.info("Fetching all expenses for user: {}", userId);

        return expenseRepository.findByUserId(userId, pageable)
                .map(expenseMapper::toDto);
    }

    @Override
    public List<ExpenseDto> getExpensesByCategory(String category) {
        String userId = securityUtils.getCurrentUserId();
        log.info("Fetching expenses by category {} for user: {}", category, userId);

        List<Expense> expenses = expenseRepository.findByUserIdAndCategory(userId, category);
        return expenseMapper.toDtoList(expenses);
    }

    @Override
    public List<ExpenseDto> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        String userId = securityUtils.getCurrentUserId();
        log.info("Fetching expenses from {} to {} for user: {}", startDate, endDate, userId);

        List<Expense> expenses = expenseRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
        return expenseMapper.toDtoList(expenses);
    }

    @Override
    public ExpenseDto updateExpense(String id, ExpenseDto expenseDto) {
        String userId = securityUtils.getCurrentUserId();
        log.info("Updating expense {} for user: {}", id, userId);

        Expense expense = expenseRepository.findById(id)
                .filter(e -> e.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));

        expenseMapper.updateEntity(expenseDto, expense);
        Expense updatedExpense = expenseRepository.save(expense);
        log.info("Expense {} updated successfully", id);

        return expenseMapper.toDto(updatedExpense);
    }

    @Override
    public void deleteExpense(String id) {
        String userId = securityUtils.getCurrentUserId();
        log.info("Deleting expense {} for user: {}", id, userId);

        Expense expense = expenseRepository.findById(id)
                .filter(e -> e.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));

        expenseRepository.delete(expense);
        log.info("Expense {} deleted successfully", id);
    }

    @Override
    public MonthlyExpenseStatsDto getMonthlyStats(Integer month, Integer year) {
        String userId = securityUtils.getCurrentUserId();

        if (month == null || year == null) {
            YearMonth currentMonth = YearMonth.now();
            month = currentMonth.getMonthValue();
            year = currentMonth.getYear();
        }

        log.info("Fetching monthly stats for {}/{} for user: {}", month, year, userId);

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Expense> expenses = expenseRepository.findByUserIdAndDateBetween(userId, startDate, endDate);

        BigDecimal totalAmount = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalTransactions = expenses.size();

        BigDecimal averageAmount = totalTransactions > 0
                ? totalAmount.divide(BigDecimal.valueOf(totalTransactions), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Find top category
        String topCategory = null;
        BigDecimal topCategoryAmount = BigDecimal.ZERO;

        var categoryStats = expenseRepository.getCategoryStatsByUserIdAndDateBetween(userId, startDate, endDate);
        if (!categoryStats.isEmpty()) {
            var top = categoryStats.get(0);
            topCategory = top.get_id();
            topCategoryAmount = BigDecimal.valueOf(top.getTotal());
        }

        return MonthlyExpenseStatsDto.builder()
                .month(month)
                .year(year)
                .totalAmount(totalAmount)
                .totalTransactions(totalTransactions)
                .averageAmount(averageAmount)
                .topCategory(topCategory)
                .topCategoryAmount(topCategoryAmount)
                .build();
    }

    @Override
    public List<String> getCategories() {
        String userId = securityUtils.getCurrentUserId();
        log.info("Fetching categories for user: {}", userId);

        List<String> userCategories = expenseRepository.findDistinctCategoriesByUserId(userId);

        // Combine with default categories
        for (String defaultCategory : DEFAULT_CATEGORIES) {
            if (!userCategories.contains(defaultCategory)) {
                userCategories.add(defaultCategory);
            }
        }

        return userCategories;
    }
}
