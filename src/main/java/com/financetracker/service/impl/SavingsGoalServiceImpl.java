package com.financetracker.service.impl;

import com.financetracker.dto.DepositRequest;
import com.financetracker.dto.SavingsGoalDto;
import com.financetracker.dto.SavingsProgressDto;
import com.financetracker.entity.SavingsGoal;
import com.financetracker.exception.BadRequestException;
import com.financetracker.exception.ResourceNotFoundException;
import com.financetracker.mapper.SavingsGoalMapper;
import com.financetracker.repository.SavingsGoalRepository;
import com.financetracker.security.SecurityUtils;
import com.financetracker.service.SavingsGoalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavingsGoalServiceImpl implements SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final SavingsGoalMapper savingsGoalMapper;
    private final SecurityUtils securityUtils;

    @Override
    public SavingsGoalDto createGoal(SavingsGoalDto goalDto) {
        String userId = securityUtils.getCurrentUserId();
        log.info("Creating savings goal for user: {}", userId);

        SavingsGoal goal = savingsGoalMapper.toEntity(goalDto);
        goal.setUserId(userId);
        goal.setCurrentAmount(BigDecimal.ZERO);
        goal.setCompleted(false);

        SavingsGoal savedGoal = savingsGoalRepository.save(goal);
        log.info("Savings goal created with ID: {}", savedGoal.getId());

        return savingsGoalMapper.toDto(savedGoal);
    }

    @Override
    public SavingsGoalDto getGoalById(String id) {
        String userId = securityUtils.getCurrentUserId();
        log.info("Fetching savings goal {} for user: {}", id, userId);

        SavingsGoal goal = savingsGoalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Savings Goal", "id", id));

        return savingsGoalMapper.toDto(goal);
    }

    @Override
    public List<SavingsGoalDto> getAllGoals() {
        String userId = securityUtils.getCurrentUserId();
        log.info("Fetching all savings goals for user: {}", userId);

        List<SavingsGoal> goals = savingsGoalRepository.findByUserId(userId);
        return savingsGoalMapper.toDtoList(goals);
    }

    @Override
    public SavingsGoalDto updateGoal(String id, SavingsGoalDto goalDto) {
        String userId = securityUtils.getCurrentUserId();
        log.info("Updating savings goal {} for user: {}", id, userId);

        SavingsGoal goal = savingsGoalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Savings Goal", "id", id));

        savingsGoalMapper.updateEntity(goalDto, goal);
        SavingsGoal updatedGoal = savingsGoalRepository.save(goal);
        log.info("Savings goal {} updated successfully", id);

        return savingsGoalMapper.toDto(updatedGoal);
    }

    @Override
    public void deleteGoal(String id) {
        String userId = securityUtils.getCurrentUserId();
        log.info("Deleting savings goal {} for user: {}", id, userId);

        SavingsGoal goal = savingsGoalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Savings Goal", "id", id));

        savingsGoalRepository.delete(goal);
        log.info("Savings goal {} deleted successfully", id);
    }

    @Override
    public SavingsGoalDto deposit(String id, DepositRequest request) {
        String userId = securityUtils.getCurrentUserId();
        log.info("Depositing {} to savings goal {} for user: {}", request.getAmount(), id, userId);

        SavingsGoal goal = savingsGoalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Savings Goal", "id", id));

        if (goal.isCompleted()) {
            throw new BadRequestException("Cannot deposit to a completed goal");
        }

        BigDecimal newAmount = goal.getCurrentAmount().add(request.getAmount());
        goal.setCurrentAmount(newAmount);

        // Check if goal is completed
        if (newAmount.compareTo(goal.getTargetAmount()) >= 0) {
            goal.setCompleted(true);
            log.info("Savings goal {} completed!", id);
        }

        SavingsGoal updatedGoal = savingsGoalRepository.save(goal);
        log.info("Deposit successful. New amount: {}", newAmount);

        return savingsGoalMapper.toDto(updatedGoal);
    }

    @Override
    public SavingsProgressDto getProgress() {
        String userId = securityUtils.getCurrentUserId();
        log.info("Fetching savings progress for user: {}", userId);

        List<SavingsGoal> goals = savingsGoalRepository.findByUserId(userId);

        BigDecimal totalTarget = goals.stream()
                .map(SavingsGoal::getTargetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSaved = goals.stream()
                .map(SavingsGoal::getCurrentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal overallProgress = totalTarget.compareTo(BigDecimal.ZERO) > 0
                ? totalSaved.multiply(BigDecimal.valueOf(100)).divide(totalTarget, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        int completedGoals = (int) goals.stream().filter(SavingsGoal::isCompleted).count();
        int activeGoals = goals.size() - completedGoals;

        return SavingsProgressDto.builder()
                .totalTargetAmount(totalTarget)
                .totalSavedAmount(totalSaved)
                .overallProgress(overallProgress)
                .totalGoals(goals.size())
                .completedGoals(completedGoals)
                .activeGoals(activeGoals)
                .build();
    }
}
