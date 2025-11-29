package com.budget.budget.domain.model;

import com.budget.budget.domain.enums.BudgetStatus;
import java.util.UUID;

public class Budget {
    private UUID id;
    private UUID userId;
    private String category;
    private Money limit;
    private Money spent;
    private BudgetStatus status;
    private BudgetPeriod period;

    public Budget(UUID id, UUID userId, String category, Money limit, Money spent, BudgetStatus status, BudgetPeriod period) {
        this.id = id;
        this.userId = userId;
        this.category = category;
        this.limit = limit;
        this.spent = spent;
        this.status = status;
        this.period = period;
    }

    public void registerSpending(Money amount) {
        this.spent = this.spent.add(amount);
        if (this.spent.amount().compareTo(this.limit.amount()) > 0) {
            this.status = BudgetStatus.EXCEEDED;
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getCategory() {
        return category;
    }

    public Money getLimit() {
        return limit;
    }

    public Money getSpent() {
        return spent;
    }

    public BudgetStatus getStatus() {
        return status;
    }

    public BudgetPeriod getPeriod() {
        return period;
    }
}