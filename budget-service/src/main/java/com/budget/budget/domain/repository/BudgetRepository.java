package com.budget.budget.domain.repository;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate;
import com.budget.budget.domain.model.Budget;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BudgetRepository implements PanacheRepositoryBase<Budget, UUID> {

    public List<Budget> findByUserId(UUID userId) {
        return list("userId", userId);
    }

    public List<Budget> findActiveBudgetsByUserId(UUID userId) {
        return list("userId = ?1 and status = 'ACTIVE'", userId);
    }

    public List<Budget> findInactiveBudgetsByUserId(UUID userId) {
        return list("userId = ?1 and status = 'INACTIVE'", userId);
    }

    public List<Budget> findExceededBudgetsByUserId(UUID userId) {
        return list("userId = ?1 and status = 'EXCEEDED'", userId);
    }

    public List<Budget> findByCategoryAndUserId(String category, UUID userId) {
        return list("category = ?1 and userId = ?2", category, userId);
    }

    public List<Budget> findByUserIdAndPeriod(UUID userId, String periodType) {
        return list("userId = ?1 and period.periodType = ?2", userId, periodType);
    }

    public List<Budget> findByUserIdAndDateRange(UUID userId, java.time.LocalDate startDate,
            java.time.LocalDate endDate) {
        return list("userId = ?1 and period.startDate <= ?3 and period.endDate >= ?2", userId, startDate, endDate);
    }

    public Budget findByIdAndUserId(UUID id, UUID userId) {
        return find("id = ?1 and userId = ?2", id, userId).firstResult();
    }

    public List<Budget> findCurrentBudgetsByUserId(UUID userId, LocalDate currentDate) {
        return list("userId = ?1 and period.startDate <= ?2 and period.endDate >= ?2", userId, currentDate);
    }

    public boolean existsForCategoryAndPeriod(UUID userId, String category, String period) {
        return count("userId = ?1 and category = ?2 and period.periodType = ?3", userId, category, period) > 0;
    }


}
