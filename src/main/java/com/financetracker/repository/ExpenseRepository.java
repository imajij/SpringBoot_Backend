package com.financetracker.repository;

import com.financetracker.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends MongoRepository<Expense, String> {

    Page<Expense> findByUserId(String userId, Pageable pageable);

    List<Expense> findByUserId(String userId);

    List<Expense> findByUserIdAndCategory(String userId, String category);

    List<Expense> findByUserIdAndDateBetween(String userId, LocalDate startDate, LocalDate endDate);

    @Query("{'userId': ?0, 'category': ?1, 'date': {$gte: ?2, $lte: ?3}}")
    List<Expense> findByUserIdAndCategoryAndDateBetween(String userId, String category, LocalDate startDate, LocalDate endDate);

    @Aggregation(pipeline = {
            "{ $match: { userId: ?0, date: { $gte: ?1, $lte: ?2 } } }",
            "{ $group: { _id: '$category', total: { $sum: '$amount' }, count: { $sum: 1 } } }",
            "{ $sort: { total: -1 } }"
    })
    List<CategoryStats> getCategoryStatsByUserIdAndDateBetween(String userId, LocalDate startDate, LocalDate endDate);

    @Aggregation(pipeline = {
            "{ $match: { userId: ?0 } }",
            "{ $group: { _id: '$category' } }"
    })
    List<String> findDistinctCategoriesByUserId(String userId);

    interface CategoryStats {
        String get_id();
        Double getTotal();
        Integer getCount();
    }
}
