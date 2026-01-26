package com.financetracker.repository;

import com.financetracker.entity.SavingsGoal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavingsGoalRepository extends MongoRepository<SavingsGoal, String> {

    List<SavingsGoal> findByUserId(String userId);

    Optional<SavingsGoal> findByIdAndUserId(String id, String userId);

    List<SavingsGoal> findByUserIdAndCompleted(String userId, boolean completed);
}
