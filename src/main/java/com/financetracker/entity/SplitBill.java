package com.financetracker.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "split_bills")
public class SplitBill {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String name;

    private String description;

    private BigDecimal totalAmount;

    @Builder.Default
    private List<Participant> participants = new ArrayList<>();

    private String category;

    @Builder.Default
    private boolean settled = false;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public BigDecimal getPaidAmount() {
        return participants.stream()
                .filter(Participant::isPaid)
                .map(Participant::getAmountOwed)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getRemainingAmount() {
        return totalAmount.subtract(getPaidAmount());
    }
}
