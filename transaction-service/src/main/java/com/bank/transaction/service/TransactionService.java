package com.bank.transaction.service;

import com.bank.transaction.client.AccountClient;
import com.bank.transaction.dto.*;
import com.bank.transaction.entity.Transaction;
import com.bank.transaction.entity.TransactionStatus;
import com.bank.transaction.entity.TransactionType;
import com.bank.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;
    private static final String ACCOUNT_SERVICE_URL = "http://account-service/api/accounts";
    
    @Transactional
    public TransactionResponse deposit(DepositRequest request) {
        // Get current balance
        BigDecimal currentBalance = getAccountBalance(request.getAccountId());
        
        // Calculate new balance
        BigDecimal newBalance = currentBalance.add(request.getAmount());
        
        // Update account balance
        updateAccountBalance(request.getAccountId(), newBalance);
        
        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setAccountId(request.getAccountId());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setCreatedAt(LocalDateTime.now());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToResponse(savedTransaction);
    }
    
    @Transactional
    public TransactionResponse withdraw(WithdrawRequest request) {
        // Get current balance
        BigDecimal currentBalance = getAccountBalance(request.getAccountId());
        
        // Validate sufficient balance
        if (currentBalance.compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        
        // Calculate new balance
        BigDecimal newBalance = currentBalance.subtract(request.getAmount());
        
        // Update account balance
        updateAccountBalance(request.getAccountId(), newBalance);
        
        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setAccountId(request.getAccountId());
        transaction.setType(TransactionType.WITHDRAW);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setCreatedAt(LocalDateTime.now());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToResponse(savedTransaction);
    }
    
    @Transactional
    public TransactionResponse transfer(TransferRequest request) {
        // Validate from and to accounts are different
        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new RuntimeException("Cannot transfer to the same account");
        }
        
        // Get current balances
        BigDecimal fromBalance = getAccountBalance(request.getFromAccountId());
        BigDecimal toBalance = getAccountBalance(request.getToAccountId());
        
        // Validate sufficient balance
        if (fromBalance.compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        
        // Calculate new balances
        BigDecimal newFromBalance = fromBalance.subtract(request.getAmount());
        BigDecimal newToBalance = toBalance.add(request.getAmount());
        
        // Update account balances (atomic operation)
        updateAccountBalance(request.getFromAccountId(), newFromBalance);
        updateAccountBalance(request.getToAccountId(), newToBalance);
        
        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setAccountId(request.getFromAccountId());
        transaction.setToAccountId(request.getToAccountId());
        transaction.setType(TransactionType.TRANSFER);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setCreatedAt(LocalDateTime.now());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToResponse(savedTransaction);
    }
    
    public Page<TransactionResponse> getTransactionHistory(String accountId, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByAccountIdOrToAccountId(
                accountId, accountId, pageable);
        return transactions.map(this::mapToResponse);
    }
    
    private BigDecimal getAccountBalance(String accountId) {
        try {
            return restTemplate.getForObject(
                    ACCOUNT_SERVICE_URL + "/" + accountId + "/balance",
                    BigDecimal.class
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch account balance: " + e.getMessage());
        }
    }
    
    private void updateAccountBalance(String accountId, BigDecimal newBalance) {
        try {
            restTemplate.put(
                    ACCOUNT_SERVICE_URL + "/" + accountId + "/balance?balance=" + newBalance,
                    null
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to update account balance: " + e.getMessage());
        }
    }
    
    private TransactionResponse mapToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getToAccountId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getDescription(),
                transaction.getStatus(),
                transaction.getCreatedAt()
        );
    }
}
