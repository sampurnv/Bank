package com.bank.account.entity;

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
@Document(collection = "accounts")
public class Account {
    @Id
    private String id;
    
    private String userId;
    
    private String accountNumber;
    
    private String accountType; // SAVINGS, CHECKING, etc.
    
    private BigDecimal balance = BigDecimal.ZERO;
    
    private String currency = "USD";
    
    private boolean active = true;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
}
