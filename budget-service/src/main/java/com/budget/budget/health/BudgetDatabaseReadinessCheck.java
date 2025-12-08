package com.budget.budget.health;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.Readiness;
import io.agroal.api.AgroalDataSource;
import org.eclipse.microprofile.health.HealthCheckResponse;

@Readiness
@ApplicationScoped
public class BudgetDatabaseReadinessCheck implements HealthCheck {

    @Inject
    AgroalDataSource dataSource;

    @Override
    public HealthCheckResponse call() {
        try (var connection = dataSource.getConnection()) {
            return HealthCheckResponse.up("Budget database is ready");
        } catch (Exception e) {
            return HealthCheckResponse.down("Budget database is not ready");
        }
    }

}
