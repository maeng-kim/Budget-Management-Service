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
class BudgetResourceTest {

    private String createBudgetJson(UUID userId, LocalDate startDate, LocalDate endDate, double limitAmount) {
        return String.format(
                Locale.ROOT,"""
                {
                    "userId": "%s",
                    "category": "Food",
                    "limitAmount": %.2f,
                    "limitCurrency": "EUR",
                    "startDate": "%s",
                    "endDate": "%s",
                    "periodType": "MONTHLY"
                }
                """, userId, limitAmount, startDate, endDate);
    }
    
    private String createBudgetAndReturnId(UUID userId, LocalDate startDate, LocalDate endDate, double limitAmount) {
        String budgetJson = createBudgetJson(userId, startDate, endDate, limitAmount);

        return given()
                .contentType(ContentType.JSON)
                .body(budgetJson)
                .when()
                .post("/api/budgets")
                .then()
                .log().all()
                .statusCode(201)
                .body("id", notNullValue())
                .body("userId", equalTo(userId.toString()))
                .body("category", equalTo("Food"))
                .body("limitAmount", equalTo((float) limitAmount))
                .body("limitCurrency", equalTo("EUR"))
                .extract()
                .path("id");
    }

    @Test
    @TestSecurity(user = "test-user", roles = {"User"})
    void testCreateBudget() {
        UUID userId = UUID.randomUUID();
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        double limitAmount = 500.0;

        String body = createBudgetJson(userId, startDate, endDate, limitAmount);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/budgets")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("userId", equalTo(userId.toString()))
                .body("category", equalTo("Food"))
                .body("limitAmount", equalTo((float) limitAmount))
                .body("limitCurrency", equalTo("EUR"));
    }
    
    @Test
    @TestSecurity(user = "test-user", roles = {"User"})
    void testGetAllBudgets() {
        UUID userId = UUID.randomUUID();
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        double limitAmount = 500.0;

        createBudgetAndReturnId(userId, startDate, endDate, limitAmount);

        given()
                .when()
                .get("/api/budgets")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1))
                .body("userId", hasItem(userId.toString()));
    }

    @Test
    @TestSecurity(user = "test-user", roles = {"User"})
    void testGetBudgetStatus() {
        UUID userId = UUID.randomUUID();
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        double limitAmount = 500.0;

        String id = createBudgetAndReturnId(userId, startDate, endDate, limitAmount);

        given()
                .when()
                .get("/api/budgets/{id}/status", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("status", equalTo("ACTIVE"))
                .body("limitAmount", equalTo((float) limitAmount))
                .body("limitCurrency", equalTo("EUR"))
                .body("startDate", equalTo(startDate.toString()))
                .body("endDate", equalTo(endDate.toString()))
                .body("periodType", equalTo("MONTHLY"));
    }

    @Test
    @TestSecurity(user = "test-user", roles = {"User"})
    void testUpdateBudget() {
        UUID userId = UUID.randomUUID();
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        double limitAmount = 500.0;

        String id = createBudgetAndReturnId(userId, startDate, endDate, limitAmount);

        LocalDate newStartDate = startDate.plusWeeks(1);
        LocalDate newEndDate = endDate.plusWeeks(5);
        double newLimitAmount = 600.0;

        String updateBody = createBudgetJson(userId, newStartDate, newEndDate, newLimitAmount);
        
        given()
                .contentType(ContentType.JSON)
                .body(updateBody)
                .when()
                .put("/api/budgets/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("userId", equalTo(userId.toString()))
                .body("category", equalTo("Food"))
                .body("limitAmount", equalTo((float) newLimitAmount))
                .body("limitCurrency", equalTo("EUR"))
                .body("startDate", equalTo(newStartDate.toString()))
                .body("endDate", equalTo(newEndDate.toString()))
                .body("periodType", equalTo("MONTHLY"));
    }

    @Test
    @TestSecurity(user = "test-user", roles = {"User"})
    void testDeleteBudget() {
        UUID userId = UUID.randomUUID();
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        double limitAmount = 500.0;

        String id = createBudgetAndReturnId(userId, startDate, endDate, limitAmount);

        given()
                .when()
                .delete("/api/budgets/{id}", id)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/api/budgets/{id}/status", id)
                .then()
                .statusCode(404);
    }
}