package com.financetracker.mapper;

import com.financetracker.dto.BudgetDto;
import com.financetracker.entity.Budget;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    BudgetDto toDto(Budget budget);

    Budget toEntity(BudgetDto budgetDto);

    List<BudgetDto> toDtoList(List<Budget> budgets);

    void updateEntity(BudgetDto budgetDto, @MappingTarget Budget budget);
}
