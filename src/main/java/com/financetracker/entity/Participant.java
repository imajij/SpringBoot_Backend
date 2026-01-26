package com.financetracker.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

    private String id;

    private String name;

    private String email;

    private BigDecimal amountOwed;

    @Builder.Default
    private boolean paid = false;
}
