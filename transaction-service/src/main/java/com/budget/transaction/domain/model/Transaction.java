package com.budget.transaction.domain.model;

import com.budget.transaction.domain.enums.TransactionType;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction extends PanacheEntityBase {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "budget_id", nullable = false, columnDefinition = "uuid")
    private UUID budgetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money amount;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(length = 255)
    private String description;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate date;

    public Transaction() {
    }

    public Transaction(UUID userId, UUID budgetId, TransactionType type, Money amount, String category,
            String description, LocalDate date) {

        this.userId = userId;
        this.budgetId = budgetId;
        this.type = type;
        this.amount = amount;
        this.category = category;
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
    // setters
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
    public void setAmount(Money amount) {
        this.amount = amount;
    }           
    public void setCategory(String category) {
        this.category = category;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setDate(LocalDate date) {
        this.date = date;   
    }
}