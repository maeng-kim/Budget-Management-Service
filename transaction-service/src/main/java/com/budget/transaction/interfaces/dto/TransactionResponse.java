package com.budget.transaction.interfaces.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.budget.transaction.domain.enums.TransactionType;
import com.budget.transaction.domain.model.Transaction;

public class TransactionResponse {
    private UUID id;
    private UUID userId;
    private UUID budgetId;
    private TransactionType type;
    private BigDecimal amount;
    private String currency;
    private String category;
    private LocalDate date;
    private String description;

    public TransactionResponse() {
    }

    public static TransactionResponse fromEntity(Transaction transaction) {
        TransactionResponse dto = new TransactionResponse();

        dto.id = transaction.getId();
        dto.userId = transaction.getUserId();
        dto.budgetId = transaction.getBudgetId();
        dto.type = transaction.getType();
        if(transaction.getAmount() != null) {
            dto.amount = transaction.getAmount().getAmount();
            dto.currency = transaction.getAmount().getCurrency();
        }
        dto.category = transaction.getCategory();
        dto.date = transaction.getDate();
        dto.description = transaction.getDescription();

        return dto;
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

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
    //setters
    public void setId(UUID id) {
        this.id = id;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    public void setBudgetId(UUID budgetId) {
        this.budgetId = budgetId;
    }   
    public void setType(TransactionType type) {
        this.type = type;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}