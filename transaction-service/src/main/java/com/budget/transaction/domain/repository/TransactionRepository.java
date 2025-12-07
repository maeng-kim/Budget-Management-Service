package com.budget.transaction.domain.repository;

import java.util.List;
import java.util.UUID;

import com.budget.transaction.domain.model.Transaction;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;

@ApplicationScoped
public class TransactionRepository implements PanacheRepositoryBase<Transaction, UUID> {

    public List<Transaction> findByUserId(UUID userId) {
        return list("userId", userId);
    }

    public List<Transaction> findByBudgetId(UUID budgetId) {
        return list("budgetId", budgetId);
    }

    public List<Transaction> findByUserIdAndBudgetId(UUID userId, UUID budgetId) {
        if(userId != null && budgetId != null) {
            return list("userId = ?1 and budgetId = ?2", userId, budgetId);
        } else if(userId != null) {
            return findByUserId(userId);
        } else if(budgetId != null) {
            return findByBudgetId(budgetId);
        } else {
            return listAll();
        }
    }

    public List<Transaction> findByUserIdAndDateRange(UUID userId, LocalDate startDate, LocalDate endDate) {
        return list("userId = ?1 and date between ?2 and ?3", userId, startDate, endDate);
    }

    public List<Transaction> findByUserIdAndDate(UUID userId, LocalDate date) {
        return list("userId = ?1 and date = ?2", userId, date);
    }

    public List<Transaction> findByTypeAndUserId(String type, UUID userId) {
        return list("type = ?1 and userId = ?2", type, userId);
    }

    public Transaction findByIdAndUserId(UUID id, UUID userId) {
        return find("id = ?1 and userId = ?2", id, userId).firstResult();
    }

    public List<Transaction> findByUserIdAndTypeAndDate(UUID userId, String type, LocalDate date) {
        return list("userId = ?1 and type = ?2 and date = ?3", userId, type, date);
    }

}
