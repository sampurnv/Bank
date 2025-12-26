package com.bank.account.service;

import com.bank.account.dto.AccountResponse;
import com.bank.account.dto.CreateAccountRequest;
import com.bank.account.entity.Account;
import com.bank.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {
    
    private final AccountRepository accountRepository;
    
    public AccountResponse createAccount(CreateAccountRequest request) {
        Account account = new Account();
        account.setUserId(request.getUserId());
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setBalance(BigDecimal.ZERO);
        account.setCurrency(request.getCurrency());
        account.setActive(true);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        
        Account savedAccount = accountRepository.save(account);
        return mapToResponse(savedAccount);
    }
    
    public List<AccountResponse> getAccountsByUserId(String userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        return accounts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public AccountResponse getAccountById(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return mapToResponse(account);
    }
    
    public AccountResponse getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return mapToResponse(account);
    }
    
    public BigDecimal getBalance(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return account.getBalance();
    }
    
    public void updateBalance(String accountId, BigDecimal newBalance) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setBalance(newBalance);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
    }
    
    private String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = String.format("%010d", new Random().nextInt(1000000000));
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
    
    private AccountResponse mapToResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getUserId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getCurrency(),
                account.isActive(),
                account.getCreatedAt()
        );
    }
}
