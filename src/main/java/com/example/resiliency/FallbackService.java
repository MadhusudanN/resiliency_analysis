import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;
import java.util.function.Supplier;

public class FallbackService {

    private final CircuitBreaker circuitBreaker;

    public FallbackService() {
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();
        this.circuitBreaker = registry.circuitBreaker("dataFetcherCB");
    }

    /**
     * Primary method that can fail and uses a fallback.
     */
    public String getPrimaryData(boolean shouldFail) {
        // 1. Define the core business logic using a standard Java Supplier
        Supplier<String> primarySupplier = () -> {
            if (shouldFail) {
                System.err.println("Primary call failed.");
                throw new RuntimeException("Primary Data Source Down");
            }
            return "Real-time Primary Data";
        };

        // 2. Decorate with the Circuit Breaker to apply resilience logic.
        //    This returns a java.util.function.Supplier<String>
        Supplier<String> decoratedSupplier = circuitBreaker.decorateSupplier(primarySupplier);

        // 3. Use Resilience4j's helper to execute with Vavr's Try and Fallback.
        //    The Resilience4j module has an extension for Vavr (io.vavr.control.Try) 
        //    that correctly handles the execution and recovery.
        return Try.of(decoratedSupplier::get) // Use method reference to wrap the standard supplier's 'get()' method
            .recover(throwable -> getFallbackData(throwable)) // Fallback method called on failure
            .get();
    }

    /**
     * The designated fallback method.
     */
    public String getFallbackData(Throwable t) {
        System.out.println("Executing Fallback: Returning cached or default data due to: " + t.getMessage());
        return "Cached or Default Fallback Data";
    }
}