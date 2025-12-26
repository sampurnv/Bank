package com.bank.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAccountRequest {
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Account type is required")
    private String accountType;
    
    private String currency = "USD";
}
