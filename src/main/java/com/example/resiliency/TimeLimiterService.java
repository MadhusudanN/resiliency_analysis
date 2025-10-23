import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TimeLimiterService {

    private final TimeLimiter timeLimiter;
    private final ScheduledExecutorService scheduler;

    public TimeLimiterService() {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofMillis(500)) // Set timeout to 500ms
            .cancelRunningFuture(true) // Attempt to interrupt the running task
            .build();
        
        this.timeLimiter = TimeLimiter.of(config);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Executes a potentially long-running task with a hard timeout.
     */
    public String executeWithTimeout(long sleepMillis) throws Exception {
        Callable<String> task = () -> {
            System.out.println("Task started, waiting for " + sleepMillis + "ms.");
            Thread.sleep(sleepMillis); // Simulate work
            System.out.println("Task finished successfully.");
            return "Task completed in time.";
        };

        // Decorate the task with the Time Limiter
        Callable<String> restrictedCall = timeLimiter.decorateFutureSupplier(
            () -> scheduler.submit(task)
        );

        // This blocks until the result is available or the timeout is reached
        try {
            return restrictedCall.call();
        } catch (Exception e) {
            System.err.println("Timeout exceeded: " + e.getMessage());
            throw e;
        }
    }
}