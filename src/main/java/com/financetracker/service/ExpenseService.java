package com.financetracker.service;

import com.financetracker.dto.ExpenseDto;
import com.financetracker.dto.MonthlyExpenseStatsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {

    ExpenseDto createExpense(ExpenseDto expenseDto);

    ExpenseDto getExpenseById(String id);

    Page<ExpenseDto> getAllExpenses(Pageable pageable);

    List<ExpenseDto> getExpensesByCategory(String category);

    List<ExpenseDto> getExpensesByDateRange(LocalDate startDate, LocalDate endDate);

    ExpenseDto updateExpense(String id, ExpenseDto expenseDto);

    void deleteExpense(String id);

    MonthlyExpenseStatsDto getMonthlyStats(Integer month, Integer year);

    List<String> getCategories();
}
