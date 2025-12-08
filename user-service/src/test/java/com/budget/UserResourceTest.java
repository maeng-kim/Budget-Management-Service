package com.budget;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restasswured.RestAssured.given;
import static javax.swing.text.DefaultStyledDocument.ElementSpec.ContentType;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
public class UserResourceTest {

    @Test
    public void testListUsersEndpoint() {
        given()
                .when().get("/api/users")
                .then()
                .statusCode(200);
    }

    @Test
    public void testCreateUserSuccess() {
        String requestBody = """
            {
                "email": "Fortest@example.com",
                "password": "testPassword123",
                "fullName": "Test User"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when().post("/api/users")
                .then()
                .statusCode(201)
                .body("email", equalTo("Fortest@example.com"))
                .body("fullName", equalTo("Test User"));
    }

    @Test
    public void testCreateUserInvalidEmail() {
        String requestBody = """
            {
                "email": "",
                "password": "testPassword123",
                "fullName": "Test User"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when().post("/api/users")
                .then()
                .statusCode(400);
    }

    @Test
    public void testCreateUserShortPassword() {
        String requestBody = """
            {
                "email": "Fortest@example.com",
                "password": "123",
                "fullName": "Test User"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when().post("/api/users")
                .then()
                .statusCode(400);
    }

    @Test
    public void testLoginSuccess() {

        String createBody = """
                {
                "email": "login@example.com",
                "password": "password123",
                "fullName": "Login User"
                }""";
        given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when().post("/api/users")
                .then()
                .statusCode(201);

        String loginBody = """
            {
                "email": "login@example.com",
                "password": "password123"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(loginBody)
                .when().post("/api/users/login")
                .then()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    public void testLoginInvalidCredentials() {
        String loginBody = """
            {
                "email": "nonexistent@example.com",
                "password": "wrongpassword"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(loginBody)
                .when().post("/api/users/login")
                .then()
                .statusCode(401);
    }
}
