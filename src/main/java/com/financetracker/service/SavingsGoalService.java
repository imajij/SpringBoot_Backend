package com.financetracker.service;

import com.financetracker.dto.DepositRequest;
import com.financetracker.dto.SavingsGoalDto;
import com.financetracker.dto.SavingsProgressDto;

import java.util.List;

public interface SavingsGoalService {

    SavingsGoalDto createGoal(SavingsGoalDto goalDto);

    SavingsGoalDto getGoalById(String id);

    List<SavingsGoalDto> getAllGoals();

    SavingsGoalDto updateGoal(String id, SavingsGoalDto goalDto);

    void deleteGoal(String id);

    SavingsGoalDto deposit(String id, DepositRequest request);

    SavingsProgressDto getProgress();
}
