package com.budget.budget.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.Liveness;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheckResponse;

@Liveness
@ApplicationScoped
public class BudgetServiceLivenessCheck implements HealthCheck{

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("Budget service is alive");
    }
}
