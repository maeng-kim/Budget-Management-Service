package com.budget.interfaces;

import com.budget.domain.Email;
import com.budget.domain.User;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @GET
    public List<User> listUsers() {
        return User.listAll();
    }

    @POST
    @Transactional
    public Response createUser(CreateUserRequest request) {
        if (request.email == null || request.email.isBlank()) {
            return Response.status(400).entity(Map.of("error", "Email is required")).build();
        }

        if (request.password == null || request.password.length() < 8) {
            return Response.status(400).entity(Map.of("error", "Password must be at least 8 characters")).build();
        }

        if (request.fullName == null || request.fullName.isBlank()) {
            return Response.status(400).entity(Map.of("error", "Full name is required")).build();
        }

        if (User.existsByEmail(request.email)) {
            return Response.status(400).entity(Map.of("error", "Email already exists")).build();
        }

        try {
            Email email = new Email(request.email);

            User user = new User(email, request.password, request.fullName);
            user.persist();

            return Response.status(201).entity(new UserResponse(user)).build();
        } catch (IllegalAccessError e) {
            return Response.status(400).entity(Map.of("error", e.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") String id) {
        User user = User.findById(id);
        if (user == null) {
            return Response.status(404).entity(Map.of("error", "User not found")).build();
        }
        return Response.ok(new UserResponse(user)).build();
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {

        if (request.email == null || request.password == null) {
            return Response.status(400).entity(Map.of("error", "Email and password are required")).build();
        }

        User user = User.findByEmail(request.email);
        if (user == null) {
            return Response.status(401).entity(Map.of("error", "Invalid credentials")).build();
        }

        if (!user.authenticate(request.password)) {
            return Response.status(401).entity(Map.of("error", "Invalid credentials")).build();
        }

        // Return user info (TODO: Add JWT token later)
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("email", user.getEmail());
        response.put("fullName", user.getFullName());
        response.put("currency", user.getCurrency());
        response.put("message", "Login successful");

        return Response.ok(response).build();
    }

    public static class CreateUserRequest {
        public String email;
        public String password;
        public String fullName;
    }

    public static class LoginRequest {
        public String email;
        public String password;
    }

    public static class UserResponse {
        public UUID id;
        public String email;
        public String fullName;
        public String currency;
        public String createdAt;

        public UserResponse(User user) {
            this.id = user.getId();
            this.email = user.getEmail().getValue();
            this.fullName = user.getFullName();
            this.currency = user.getCurrency();
            this.createdAt = user.getCreatedAt().toString();
        }
    }
}
