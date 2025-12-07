package com.budget.budget.interfaces.dto;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.budget.budget.domain.model.Budget;

public class BudgetResponse {

    private UUID id;
    private UUID userId;
    private String category;
    private BigDecimal limitAmount;
    private String limitCurrency;
    private BigDecimal spentAmount;
    private String spentCurrency;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String periodType;

    public static BudgetResponse fromEntity(Budget budget) {
        BudgetResponse dto = new BudgetResponse();

        dto.id = budget.getId();
        dto.userId = budget.getUserId();
        dto.category = budget.getCategory();

        if(budget.getLimit() != null) {
            dto.limitAmount = budget.getLimit().getAmount();
            dto.limitCurrency = budget.getLimit().getCurrency();
        }
        if(budget.getSpent() != null) {
            dto.spentAmount = budget.getSpent().getAmount();
            dto.spentCurrency = budget.getSpent().getCurrency();
        }
        if(budget.getStatus() != null) {
            dto.status = budget.getStatus().name();
        }
        if(budget.getPeriod() != null) {
            dto.startDate = budget.getStartDate();
            dto.endDate = budget.getEndDate();
            dto.periodType = budget.getPeriod().getPeriodType().name();
        }
        return dto;
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

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public String getLimitCurrency() {
        return limitCurrency;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public String getSpentCurrency() {
        return spentCurrency;
    }

    public String getStatus() {
        return status;
    }

    public String getPeriodType() {
        return periodType;
    }
    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
    //setters
    public void setId(UUID id) {
        this.id = id;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }
    public void setLimitCurrency(String limitCurrency) {
        this.limitCurrency = limitCurrency;
    }
    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
    }
    public void setSpentCurrency(String spentCurrency) {
        this.spentCurrency = spentCurrency;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}