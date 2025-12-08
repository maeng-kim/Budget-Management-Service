package com.budget.interfaces;

import com.budget.domain.Email;
import com.budget.domain.User;
import com.budget.infrastructure.security.TokenService;
import com.budget.interfaces.dto.request.*;
import com.budget.interfaces.dto.response.*;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    TokenService tokenService;

    @ConfigProperty(name = "jwt.duration", defaultValue = "86400")
    Long jwtDuration;

    @GET
    @RolesAllowed("User")
    public List<UserResponse> listUsers() {
        return User.<User>listAll()
                .stream()
                .map(UserResponse::new)
                .toList();
    }

    @POST
    @Transactional
    @PermitAll
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
    @PermitAll
    public Response getUser(@PathParam("id") String id) {
        User user = User.findById(id);
        if (user == null) {
            return Response.status(404).entity(Map.of("error", "User not found")).build();
        }
        return Response.ok(new UserResponse(user)).build();
    }

    @POST
    @Path("/login")
    @PermitAll
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

        String accessToken = tokenService.generateToken(
                user.getId(),
                user.getEmail().getValue(),
                user.getFullName()
        );

        String refreshToken = tokenService.generateRefreshToken(
                user.getId(),
                user.getEmail().getValue()
        );

        LoginResponse response = new LoginResponse(
                user.getId(),
                user.getEmail().getValue(),
                user.getFullName(),
                user.getCurrency(),
                accessToken,
                refreshToken,
                jwtDuration
        );

        return Response.ok(response).build();
    }

    @GET
    @Path("/me")
    @RolesAllowed("User")
    public Response getCurrentUser(@Context SecurityContext ctx) {
        String email = ctx.getUserPrincipal().getName();

        User user = User.findByEmail(email);
        if (user == null) {
            return Response.status(404).entity(Map.of("error", "User not found")).build();
        }

        return Response.ok(new UserResponse(user)).build();
    }

    @PUT
    @Path("/me")
    @RolesAllowed("User")
    @Transactional
    public Response updateCurrentUser(@Context SecurityContext ctx, UpdateProfileRequest request) {
        String email = ctx.getUserPrincipal().getName();

        User user = User.findByEmail(email);
        if (user == null) {
            return Response.status(404).entity(Map.of("error", "User not found")).build();
        }

        user.updateProfile(request.fullName, request.currency);
        user.persist();

        return Response.ok(new UserResponse(user)).build();
    }

    @PUT
    @Path("/me/password")
    @RolesAllowed("User")
    @Transactional
    public Response changePassword(@Context SecurityContext ctx, ChangePasswordRequest request) {
        String email = ctx.getUserPrincipal().getName();

        User user = User.findByEmail(email);
        if (user == null) {
            return Response.status(404).entity(Map.of("error", "User not found")).build();
        }

        try {
            user.changePassword(request.currentPassword, request.newPassword);
            user.persist();

            return Response.ok(Map.of("message", "Password changed successfully")).build();
        } catch (IllegalArgumentException e) {
            return Response.status(400).entity(Map.of("error", e.getMessage())).build();
        }
    }
}
