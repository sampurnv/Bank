package com.bank.transaction.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "account-service")
public interface AccountClient {
    
    @GetMapping("/api/accounts/{accountId}/balance")
    BigDecimal getBalance(@PathVariable("accountId") String accountId);
    
    @PutMapping("/api/accounts/{accountId}/balance")
    void updateBalance(@PathVariable("accountId") String accountId, @RequestParam("balance") BigDecimal balance);
}
