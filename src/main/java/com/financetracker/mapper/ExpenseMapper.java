package com.financetracker.mapper;

import com.financetracker.dto.ExpenseDto;
import com.financetracker.entity.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    ExpenseDto toDto(Expense expense);

    Expense toEntity(ExpenseDto expenseDto);

    List<ExpenseDto> toDtoList(List<Expense> expenses);

    void updateEntity(ExpenseDto expenseDto, @MappingTarget Expense expense);
}
