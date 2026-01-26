package com.financetracker.repository;

import com.financetracker.entity.Budget;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends MongoRepository<Budget, String> {

    List<Budget> findByUserId(String userId);

    Optional<Budget> findByUserIdAndMonthAndYear(String userId, Integer month, Integer year);

    Optional<Budget> findByIdAndUserId(String id, String userId);
}
