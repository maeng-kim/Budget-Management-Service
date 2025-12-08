package com.budget.transaction.health;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.Readiness;
import io.agroal.api.AgroalDataSource;
import org.eclipse.microprofile.health.HealthCheckResponse;


@Readiness
@ApplicationScoped
public class TransactionDatabaseReadinessCheck implements HealthCheck {
    
    @Inject
    AgroalDataSource dataSource;

    @Override
    public HealthCheckResponse call() {
        try (var connection = dataSource.getConnection()) {
            return HealthCheckResponse.up("Transaction database is ready");
        } catch (Exception e) {
            return HealthCheckResponse.down("Transaction database is not ready");
        }
    }
    
}
