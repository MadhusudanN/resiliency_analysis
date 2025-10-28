import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Duration;
import java.util.function.Supplier;

public class CircuitBreakerService {

    private final CircuitBreaker circuitBreaker;

    public CircuitBreakerService() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50) // Percentage of calls that must fail to open the circuit
            .slowCallRateThreshold(50)
            .waitDurationInOpenState(Duration.ofSeconds(5)) // Time the circuit stays open
            .slidingWindowSize(10) // Number of calls to record when determining failure rate
            .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        this.circuitBreaker = registry.circuitBreaker("externalServiceCB");
    }

    /**
     * Tries to call a potentially unstable external service using the Circuit Breaker.
     */
    public String callExternalService() {
        Supplier<String> decoratedSupplier = circuitBreaker.decorateSupplier(() -> {
            // Simulate an external call that occasionally fails
            if (System.currentTimeMillis() % 2 == 0) {
                System.out.println("External service call successful.");
                return "Success Data";
            } else {
                System.err.println("External service call failed.");
                throw new RuntimeException("Service Unavailable");
            }
        });

        try {
            return decoratedSupplier.get();
        } catch (Exception e) {
            System.out.println("Circuit Breaker is handling an error: " + e.getMessage());
            // This is where the circuit breaker logic is engaged (fast-fail)
            throw new RuntimeException("Circuit is open or call failed/slow.");
        }
    }
}        