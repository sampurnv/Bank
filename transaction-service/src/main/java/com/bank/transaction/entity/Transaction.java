package com.bank.transaction.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    
    private String accountId;
    
    private String toAccountId; // For transfers
    
    private TransactionType type;
    
    private BigDecimal amount;
    
    private String currency = "USD";
    
    private String description;
    
    private TransactionStatus status = TransactionStatus.COMPLETED;
    
    private LocalDateTime createdAt = LocalDateTime.now();
}
