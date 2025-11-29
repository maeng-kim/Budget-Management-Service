package com.budget.transaction.domain.model;

import com.budget.transaction.domain.enums.TransactionType;
import java.time.LocalDate;
import java.util.UUID;

public class Transaction {

    private UUID id;
    private UUID userId;
    private UUID budgetId;
    private Money amount;
    private String category;
    private TransactionType type;
    private String description;
    private LocalDate date;

    public Transaction(UUID id, UUID userId, UUID budgetId, Money amount, String category, TransactionType type, String description, LocalDate date) {
        this.id = id;
        this.userId = userId;
        this.budgetId = budgetId;
        this.amount = amount;
        this.category = category;
        this.type = type;
        this.description = description;
        this.date = date;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getBudgetId() {
        return budgetId;
    }

    public Money getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public TransactionType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }
}