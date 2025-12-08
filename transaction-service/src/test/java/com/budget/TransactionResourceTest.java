package com.budget;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

@QuarkusTest
class TransactionResourceTest {

    private String createTransactionJson(UUID userId, UUID budgetId, LocalDate date, double amount, String type,
            String category, String description) {
        return String.format(
                Locale.ROOT, """
                        {
                            "userId": "%s",
                            "budgetId": "%s",
                            "type": "%s",
                            "amount": %.2f,
                            "currency": "EUR",
                            "category": "%s",
                            "date": "%s",
                            "description": "%s"
                        }
                        """, userId, budgetId, type, amount, category, date, description);
    }

    private String createTransactionAndReturnId(UUID userId, UUID budgetID, LocalDate date, double amount, String type,
            String category, String description) {
        String transactionJson = createTransactionJson(userId, budgetID, date, amount, type, category, description);

        return given()
                .contentType(ContentType.JSON)
                .body(transactionJson)
                .when()
                .post("/api/transactions")
                .then()
                .log().all()
                .statusCode(201)
                .body("id", notNullValue())
                .body("userId", equalTo(userId.toString()))
                .body("budgetId", equalTo(budgetID.toString()))
                .body("type", equalTo(type))
                .body("amount", equalTo((float) amount))
                .body("currency", equalTo("EUR"))
                .body("category", equalTo(category))
                .body("date", equalTo(date.toString()))
                .body("description", equalTo(description))
                .extract()
                .path("id");
    }

    @Test
    @TestSecurity(user = "test-user", roles = {"User"})
    void testCreateTransaction() {
        UUID userId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        double amount = 150.0;
        String currency = "EUR";
        String type = "EXPENSE";
        String category = "Groceries";
        String description = "Weekly grocery shopping";   

        String body = createTransactionJson(userId, budgetId, date, amount, type, category, description);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/transactions")
                .then()
                .log().all()
                .statusCode(201)
                .body("id", notNullValue())
                .body("userId", equalTo(userId.toString()))
                .body("budgetId", equalTo(budgetId.toString()))
                .body("type", equalTo(type))
                .body("amount", equalTo((float) amount))
                .body("currency", equalTo(currency))
                .body("category", equalTo(category))
                .body("date", equalTo(date.toString()))
                .body("description", equalTo(description));
    }

    @Test
    @TestSecurity(user = "test-user", roles = {"User"})
    void testGetAllTransactions() {
        UUID userId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        double amount = 200.0;

        createTransactionAndReturnId(userId, budgetId, date, amount, "EXPENSE", "Food", "Dinner");

        when()
                .get("/api/transactions")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1))
                .body("userId", hasItem(userId.toString()));
    }
    @Test
    @TestSecurity(user = "test-user", roles = {"User"})
    void testGetAllTransactionsFilteredByUserId() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        double amount = 200.0;

        createTransactionAndReturnId(userId1, budgetId, date, amount, "EXPENSE", "Food", "Dinner");
        createTransactionAndReturnId(userId2, budgetId, date, amount, "INCOME", "Salary", "Monthly salary");
        
        given()
                .queryParam("userId", userId1.toString())
                .when()
                .get("/api/transactions")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1))
                .body("userId", everyItem(equalTo(userId1.toString())));
    }

    @Test
    @TestSecurity(user = "test-user", roles = {"User"})
    void testGetTransactionSummary() {
        UUID userId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        createTransactionAndReturnId(userId, budgetId, date, 500.0, "INCOME", "Salary", "Monthly salary");
        createTransactionAndReturnId(userId, budgetId, date, 150.0, "EXPENSE", "Groceries", "Weekly groceries");
        UUID userId2 = UUID.randomUUID();
        createTransactionAndReturnId(userId2, budgetId, date, 200.0, "INCOME", "Salary", "Monthly salary");

        given()
                .queryParam("userId", userId.toString())
                .when()
                .get("/api/transactions/summary")
                .then()
                .statusCode(200)
                .body("totalIncome", equalTo(500.0f))
                .body("totalExpense", equalTo(150.0f))
                .body("netAmount", equalTo(350.0f));
    }   
    @Test
    @TestSecurity(user = "test-user", roles = {"User"})
    void testDeleteTransaction() {
        UUID userId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        double amount = 100.0;

        String transactionId = createTransactionAndReturnId(userId, budgetId, date, amount, "EXPENSE", "Entertainment", "Movie tickets");

        given()
                .when()
                .delete("/api/transactions/{id}", transactionId)
                .then()
                .statusCode(204);

        given() 
                .queryParam("userId", userId.toString())
                .when()
                .get("/api/transactions")
                .then()
                .statusCode(200)    
                .body("id", not(hasItem(transactionId)));       
    }
}