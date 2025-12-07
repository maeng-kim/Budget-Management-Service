package com.budget.budget.interfaces.rest;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.budget.budget.domain.enums.BudgetStatus;
import com.budget.budget.domain.enums.PeriodType;
import com.budget.budget.domain.model.Budget;
import com.budget.budget.domain.model.BudgetPeriod;
import com.budget.budget.domain.model.Money;
import com.budget.budget.domain.repository.BudgetRepository;
import com.budget.budget.interfaces.dto.BudgetRequest;
import com.budget.budget.interfaces.dto.BudgetResponse;
import com.budget.budget.interfaces.dto.BudgetStatusResponse;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

@Path("/api/budgets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)  
@RolesAllowed({"User"}) 
public class BudgetResource {

    @Inject
    BudgetRepository budgetRepository;

    @POST
    @Transactional
    public Response createBudget(@Valid BudgetRequest request) {
        Budget budget = new Budget();
        budget.setUserId(request.getUserId());
        budget.setCategory(request.getCategory());

        Money limit = new Money();
        limit.setAmount(request.getLimitAmount());
        limit.setCurrency(request.getLimitCurrency());
        budget.setLimit(limit);

        Money spent = new Money();
        spent.setAmount(BigDecimal.ZERO);
        spent.setCurrency(request.getLimitCurrency());
        budget.setSpent(spent);

        BudgetPeriod period = new BudgetPeriod();
        period.setStartDate(request.getStartDate());
        period.setEndDate(request.getEndDate());
        period.setPeriodType(request.getPeriodType());
        budget.setPeriod(period);

        budget.setStatus(BudgetStatus.ACTIVE);  

        budgetRepository.persist(budget);

        BudgetResponse response = BudgetResponse.fromEntity(budget);
        URI location = URI.create("/api/budgets/" + budget.getId());
        return Response.created(location).entity(response).build();
    }
    
    @GET
    public List<BudgetResponse> getAllBudgets() {
        List<Budget> budgets = budgetRepository.listAll();
        return budgets.stream()
                .map(BudgetResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}/status")
    public BudgetStatusResponse getBudgetStatus(@PathParam("id") UUID id) {
        Budget budget = budgetRepository.findById(id);
        if (budget == null) {
            throw new NotFoundException("Budget not found with id: " + id);
        }
        return BudgetStatusResponse.fromEntity(budget);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateBudget(@PathParam("id") UUID id, @Valid BudgetRequest request) {
        Budget budget = budgetRepository.findById(id);
        if (budget == null) {
            throw new NotFoundException("Budget not found with id: " + id);
        }
        
        Money limit = budget.getLimit();
        if (limit == null) {
            limit = new Money();
        }
        limit.setAmount(request.getLimitAmount());
        limit.setCurrency(request.getLimitCurrency());
        budget.setLimit(limit);

        Money spent = budget.getSpent();
        if (spent == null) {
            spent = new Money();
            spent.setAmount(BigDecimal.ZERO);
        }
        if(spent.getCurrency() == null) {
            spent.setCurrency(request.getLimitCurrency());
        }
        budget.setSpent(spent);

        BudgetPeriod period = budget.getPeriod();
        if (period == null) {
            period = new BudgetPeriod();    
        }
        period.setStartDate(request.getStartDate());
        period.setEndDate(request.getEndDate());
        period.setPeriodType(request.getPeriodType());
        budget.setPeriod(period);

        BigDecimal spentAmount = spent.getAmount() != null ? spent.getAmount() : BigDecimal.ZERO;
        BigDecimal limitAmount = limit.getAmount() != null ? limit.getAmount() : BigDecimal.ZERO;

        if(limitAmount.compareTo(BigDecimal.ZERO) > 0 && spentAmount.compareTo(limitAmount) >= 0) {
            budget.setStatus(BudgetStatus.EXCEEDED);
        } else if (period.getEndDate() != null && period.getEndDate().isBefore(LocalDate.now())) {
            budget.setStatus(BudgetStatus.INACTIVE);
        } else {
            budget.setStatus(BudgetStatus.ACTIVE);
        }

        BudgetResponse response = BudgetResponse.fromEntity(budget);
        return Response.ok(response).build();

    }
    
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteBudget(@PathParam("id") UUID id) {
        boolean deleted = budgetRepository.deleteById(id);
        if (!deleted) {
            throw new NotFoundException("Budget not found with id: " + id);
        }
        return Response.noContent().build();
    }
}