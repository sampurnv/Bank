package com.bank.transaction.dto;

import com.bank.transaction.entity.TransactionStatus;
import com.bank.transaction.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private String id;
    private String accountId;
    private String toAccountId;
    private TransactionType type;
    private BigDecimal amount;
    private String currency;
    private String description;
    private TransactionStatus status;
    private LocalDateTime createdAt;
}
