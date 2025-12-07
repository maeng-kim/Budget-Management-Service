package com.budget.transaction.interfaces.dto;

import java.math.BigDecimal;

public class TransactionSummaryResponse {
    
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netAmount;

    public TransactionSummaryResponse() {
    }

    public TransactionSummaryResponse(BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal netAmount) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.netAmount = netAmount;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }
}