package com.budget.health;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class UserDatabaseReadinessCheck implements HealthCheck {

    @Inject
    AgroalDataSource dataSource;

    @Override
    public HealthCheckResponse call() {
        try (var connection = dataSource.getConnection()) {
            return HealthCheckResponse.up("User database is ready");
        } catch (Exception e) {
            return HealthCheckResponse.down("User database is not ready: " + e.getMessage());
        }
    }
}
