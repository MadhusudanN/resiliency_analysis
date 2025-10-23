import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

// This annotation would typically be on a Spring Boot Application class
// @EnableRetry 

@Service
public class RetryableService {

    private int attemptCount = 0;

    /**
     * An operation that is retried if a specific exception is thrown.
     */
    @Retryable(
        value = { IllegalStateException.class },
        maxAttempts = 3, // Total attempts (1 initial + 2 retries)
        backoff = @Backoff(delay = 1000) // Wait 1 second between retries
    )
    public String performPotentiallyFailingOperation() {
        attemptCount++;
        System.out.println("Attempt #" + attemptCount + ": Calling external resource...");
        
        // Fails on the first two attempts, succeeds on the third
        if (attemptCount < 3) {
            throw new IllegalStateException("Transient failure encountered.");
        }
        
        System.out.println("Attempt #" + attemptCount + ": Successfully retrieved data.");
        attemptCount = 0; // Reset for next test
        return "Final Success Data";
    }

    /**
     * The recovery method is called if all retry attempts fail.
     * Must have the same return type and match arguments of the @Retryable method + exception.
     */
    @Recover
    public String recover(IllegalStateException e) {
        System.err.println("All retry attempts failed. Executing recovery logic.");
        attemptCount = 0; // Reset for next test
        return "Fallback/Recovery Data";
    }
}