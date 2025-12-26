package com.bank.transaction.repository;

import com.bank.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    Page<Transaction> findByAccountId(String accountId, Pageable pageable);
    Page<Transaction> findByAccountIdOrToAccountId(String accountId, String toAccountId, Pageable pageable);
}
