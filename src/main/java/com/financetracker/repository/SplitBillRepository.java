package com.financetracker.repository;

import com.financetracker.entity.SplitBill;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SplitBillRepository extends MongoRepository<SplitBill, String> {

    List<SplitBill> findByUserId(String userId);

    Optional<SplitBill> findByIdAndUserId(String id, String userId);

    List<SplitBill> findByUserIdAndSettled(String userId, boolean settled);
}
