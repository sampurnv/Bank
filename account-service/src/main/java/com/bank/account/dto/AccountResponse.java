package com.bank.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private String id;
    private String userId;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private String currency;
    private boolean active;
    private LocalDateTime createdAt;
}
