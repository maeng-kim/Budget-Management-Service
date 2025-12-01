package com.budget.budget.domain.model;

import com.budget.budget.domain.enums.BudgetStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "budgets")
public class Budget extends PanacheEntityBase {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(nullable = false, length = 50)
    private String category;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "limit_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "limit_currency"))
    })
    private Money limit;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "spent_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "spent_currency"))
    })
    private Money spent;

    @Enumerated(EnumType.STRING)
    private BudgetStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startDate", column = @Column(name = "start_date")),
            @AttributeOverride(name = "endDate", column = @Column(name = "end_date")),
            @AttributeOverride(name = "periodType", column = @Column(name = "period_type"))
    })
    private BudgetPeriod period;

    protected Budget() {
    }

    public Budget(UUID userId, String category, Money limit, Money spent, BudgetStatus status,
            BudgetPeriod period) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        if (category == null || category.isEmpty()) {
            throw new IllegalArgumentException("Category must not be null or empty");
        }
        if (limit == null) {
            throw new IllegalArgumentException("Limit must not be null");
        }
        if (period == null) {
            throw new IllegalArgumentException("Period must not be null");
        }
        this.userId = userId;
        this.category = category;
        this.limit = limit;
        this.spent = spent != null ? spent : new Money(BigDecimal.ZERO, limit.getCurrency());
        this.status = status != null ? status : BudgetStatus.ACTIVE;
        this.period = period;
    }

    public void registerSpending(Money amount) {
        this.spent = this.spent.add(amount);
        if (this.spent.getAmount().compareTo(this.limit.getAmount()) > 0) {
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

    public LocalDate getStartDate() {
        return period != null ? period.getStartDate() : null;
    }

    public LocalDate getEndDate() {
        return period != null ? period.getEndDate() : null;
    }

}