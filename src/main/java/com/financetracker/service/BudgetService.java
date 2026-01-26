package com.financetracker.service;

import com.financetracker.dto.BudgetDto;

public interface BudgetService {

    BudgetDto getCurrentBudget();

    BudgetDto getBudgetByMonthAndYear(Integer month, Integer year);

    BudgetDto createOrUpdateBudget(BudgetDto budgetDto);
}
