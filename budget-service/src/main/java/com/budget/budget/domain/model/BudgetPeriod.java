package com.budget.budget.domain.model;

import com.budget.budget.domain.enums.PeriodType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import java.time.LocalDate;

@Embeddable
public class BudgetPeriod {

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private PeriodType periodType;

    protected BudgetPeriod() {
    }

    public BudgetPeriod(LocalDate startDate, LocalDate endDate, PeriodType periodType) {
        if (startDate == null || endDate == null || periodType == null) {
            throw new IllegalArgumentException("Start date, end date, and period type must not be null");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must not be before start date");
        }
        this.startDate = startDate;
        this.endDate = endDate;
        this.periodType = periodType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

}
