package com.budget.budget.interfaces.dto;

import java.util.UUID;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import com.budget.budget.domain.model.Budget;

public class BudgetStatusResponse {
    private UUID id;
    private String status;
    private BigDecimal limitAmount;
    private String limitCurrency;
    private BigDecimal spentAmount;
    private String spentCurrency;
    private BigDecimal remainingAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String periodType;
    private BigDecimal utilizationPercentage;

    public static BudgetStatusResponse fromEntity(Budget budget) {
        BudgetStatusResponse dto = new BudgetStatusResponse();

        dto.id = budget.getId();

        if(budget.getStatus() != null) {
            dto.status = budget.getStatus().name();
        }
        if(budget.getLimit() != null) {
            dto.limitAmount = budget.getLimit().getAmount();
            dto.limitCurrency = budget.getLimit().getCurrency();
        }
        if(budget.getSpent() != null) {
            dto.spentAmount = budget.getSpent().getAmount();
            dto.spentCurrency = budget.getSpent().getCurrency();
        }
        if(budget.getPeriod() != null) {
            dto.startDate = budget.getStartDate();
            dto.endDate = budget.getEndDate();
            dto.periodType = budget.getPeriod().getPeriodType().name();
        }
        if(dto.limitAmount != null && dto.spentAmount != null) {
            dto.remainingAmount = dto.limitAmount.subtract(dto.spentAmount);
            if(dto.limitAmount.compareTo(BigDecimal.ZERO)>0) {
                dto.utilizationPercentage = dto.spentAmount
                    .multiply(BigDecimal.valueOf(100))
                    .divide(dto.limitAmount, 2, RoundingMode.HALF_UP);  
            }
        }
        return dto;
    }

    public UUID getId() {
        return id;
    }

    public String getStatus() {
        return status;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getPeriodType() {
        return periodType;
    }

    // setters
    public void setId(UUID id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }
    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }
    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }
    public BigDecimal getUtilizationPercentage() {
        return utilizationPercentage;
    }
    public void setUtilizationPercentage(BigDecimal utilizationPercentage) {
        this.utilizationPercentage = utilizationPercentage;
    }
}