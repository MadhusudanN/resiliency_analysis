import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import java.time.Duration;

public class RateLimiterService {

    private final RateLimiter rateLimiter;

    public RateLimiterService() {
        RateLimiterConfig config = RateLimiterConfig.custom()
            .limitForPeriod(2) // Max two calls
            .limitRefreshPeriod(Duration.ofSeconds(5)) // per 5 seconds
            .timeoutDuration(Duration.ofMillis(50)) // Wait max 50ms for permission
            .build();

        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        this.rateLimiter = registry.rateLimiter("requestRateLimiter");
    }

    /**
     * Attempts to acquire a permission from the Rate Limiter before processing.
     */
    public String processRequest(String data) {
        try {
            // Try to acquire permission, will block briefly or throw if the limit is exceeded
            rateLimiter.acquirePermission();
            
            // Critical section/workload is only executed if permission is acquired
            System.out.println("Processing request for: " + data);
            Thread.sleep(100); // Simulate processing time
            return "Processed: " + data;
        } catch (RequestNotPermitted e) {
            System.out.println("Request rejected by Rate Limiter for: " + data);
            return "Too Many Requests, please try later.";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Interrupted during processing.";
        }
    }
}