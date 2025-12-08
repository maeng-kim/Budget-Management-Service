package com.budget;


import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserResourceTest {

    @Test
    @Order(1)
    public void testListUsersEndpointRequiresAuth() {
        given()
                .when().get("/api/users")
                .then()
                .statusCode(401);
    }

    @Test
    @Order(2)
    public void testCreateUserSuccess() {
        String uniqueEmail = "test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        given()
                .contentType("application/json")
                .body("{\"email\":\"" + uniqueEmail + "\",\"password\":\"password123\",\"fullName\":\"Test User\"}")
                .when().post("/api/users")
                .then()
                .log().ifValidationFails()
                .statusCode(201)
                .body("email", notNullValue())
                .body("fullName", equalTo("Test User"));
    }

    @Test
    @Order(3)
    public void testCreateUserInvalidEmail() {
        given()
                .contentType("application/json")
                .body("{\"email\":\"\",\"password\":\"password123\",\"fullName\":\"Test User\"}")
                .when().post("/api/users")
                .then()
                .statusCode(400)
                .body("error", notNullValue());
    }

    @Test
    @Order(4)
    public void testCreateUserShortPassword() {
        String uniqueEmail = "test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        given()
                .contentType("application/json")
                .body("{\"email\":\"" + uniqueEmail + "\",\"password\":\"123\",\"fullName\":\"Test User\"}")
                .when().post("/api/users")
                .then()
                .statusCode(400)
                .body("error", notNullValue());
    }

    @Test
    @Order(5)
    public void testLoginSuccess() {
        String uniqueEmail = "login" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        given()
                .contentType("application/json")
                .body("{\"email\":\"" + uniqueEmail + "\",\"password\":\"password123\",\"fullName\":\"Login User\"}")
                .when().post("/api/users")
                .then()
                .log().ifValidationFails()
                .statusCode(201);

        given()
                .contentType("application/json")
                .body("{\"email\":\"" + uniqueEmail + "\",\"password\":\"password123\"}")
                .when().post("/api/users/login")
                .then()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("userId", notNullValue())
                .body("email", equalTo(uniqueEmail))
                .body("fullName", equalTo("Login User"));
    }

    @Test
    @Order(6)
    public void testLoginInvalidCredentials() {
        given()
                .contentType("application/json")
                .body("{\"email\":\"nonexistent@example.com\",\"password\":\"wrongpassword\"}")
                .when().post("/api/users/login")
                .then()
                .statusCode(401)
                .body("error", notNullValue());
    }

    @Test
    @Order(7)
    public void testCreateUserMissingFullName() {
        String uniqueEmail = "test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        given()
                .contentType("application/json")
                .body("{\"email\":\"" + uniqueEmail + "\",\"password\":\"password123\",\"fullName\":\"\"}")
                .when().post("/api/users")
                .then()
                .statusCode(400)
                .body("error", notNullValue());
    }

    @Test
    @Order(8)
    public void testCreateUserDuplicateEmail() {
        String uniqueEmail = "duplicate" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        given()
                .contentType("application/json")
                .body("{\"email\":\"" + uniqueEmail + "\",\"password\":\"password123\",\"fullName\":\"First User\"}")
                .when().post("/api/users")
                .then()
                .statusCode(201);

        given()
                .contentType("application/json")
                .body("{\"email\":\"" + uniqueEmail + "\",\"password\":\"password456\",\"fullName\":\"Second User\"}")
                .when().post("/api/users")
                .then()
                .statusCode(400)
                .body("error", containsString("already exists"));
    }
}