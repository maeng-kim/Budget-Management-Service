package com.budget.transaction.interfaces.rest;

import java.math.BigDecimal;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import com.budget.transaction.domain.enums.TransactionType;
import com.budget.transaction.domain.model.Transaction;
import com.budget.transaction.domain.model.Money;
import com.budget.transaction.domain.repository.TransactionRepository;
import com.budget.transaction.interfaces.dto.TransactionRequest;
import com.budget.transaction.interfaces.dto.TransactionResponse;
import com.budget.transaction.interfaces.dto.TransactionSummaryResponse;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

@Path("/api/transactions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"User"})
public class TransactionResource {
    @Inject
    TransactionRepository transactionRepository;

    @POST
    @Transactional
    public Response createTransaction(@Valid TransactionRequest request) {
        Transaction tx = new Transaction();

        tx.setUserId(request.getUserId());
        tx.setBudgetId(request.getBudgetId());
        tx.setType(request.getType());

        Money amount = new Money();
        amount.setAmount(request.getAmount());
        amount.setCurrency(request.getCurrency());
        tx.setAmount(amount);

        tx.setCategory(request.getCategory());
        tx.setDescription(request.getDescription());
        tx.setDate(request.getDate());
        transactionRepository.persist(tx);
        TransactionResponse transaction = TransactionResponse.fromEntity(tx);
        URI uri = URI.create("/api/transactions/" + tx.getId());
        return Response.created(uri).entity(transaction).build();
    }

    @GET
    public List<TransactionResponse> getAllTransactions(
            @QueryParam("userId") UUID userId,
            @QueryParam("budgetId") UUID budgetId) {

        List<Transaction> transactions;
        if (userId != null && budgetId != null) {
            transactions = transactionRepository.findByUserIdAndBudgetId(userId, budgetId);
        } else if (userId != null) {
            transactions = transactionRepository.findByUserId(userId);
        } else if (budgetId != null) {
            transactions = transactionRepository.findByBudgetId(budgetId);
        } else {
            transactions = transactionRepository.listAll();
        }
        return transactions.stream()
                .map(TransactionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/summary")
    public TransactionSummaryResponse getTransactionSummary(
            @QueryParam("userId") UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId query parameter is required");

        }
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction tx : transactions) {
            if (tx.getAmount() == null || tx.getAmount().getAmount() == null) {
                continue;
            }
            BigDecimal amt = tx.getAmount().getAmount();
            if (tx.getType() == TransactionType.INCOME) {
                totalIncome = totalIncome.add(amt);
            } else if (tx.getType() == TransactionType.EXPENSE) {
                totalExpense = totalExpense.add(amt);
            }
        }
        BigDecimal netTotal = totalIncome.subtract(totalExpense);
        return new TransactionSummaryResponse(totalIncome, totalExpense, netTotal);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteTransaction(@PathParam("id") UUID id) {
        boolean deleted = transactionRepository.deleteById(id);
        if (!deleted) {
            throw new NotFoundException("Transaction not found with id: " + id);
        }
        return Response.noContent().build();
    }
}