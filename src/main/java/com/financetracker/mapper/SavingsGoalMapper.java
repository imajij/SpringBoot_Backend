package com.financetracker.mapper;

import com.financetracker.dto.SavingsGoalDto;
import com.financetracker.entity.SavingsGoal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SavingsGoalMapper {

    @Mapping(target = "progress", expression = "java(savingsGoal.getProgress())")
    SavingsGoalDto toDto(SavingsGoal savingsGoal);

    SavingsGoal toEntity(SavingsGoalDto savingsGoalDto);

    List<SavingsGoalDto> toDtoList(List<SavingsGoal> savingsGoals);

    void updateEntity(SavingsGoalDto savingsGoalDto, @MappingTarget SavingsGoal savingsGoal);
}
