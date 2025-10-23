package com.example.resiliency.non_resilient;

public class UnreliableService {

    /**
     * Custom unchecked exception to simulate a transient network or service failure.
     */
    public static class RemoteServiceException extends RuntimeException {
        public RemoteServiceException(String message) {
            super(message);
        }
    }

    private int apiCallCounter = 0;

    /**
     * Method that attempts to call a remote API without any retry logic.
     * Fails immediately if the external service is unavailable.
     */
    public String fetchDataFromRemoteAPI(String endpoint) {
        apiCallCounter++;
        System.out.println("Attempt " + apiCallCounter + ": Calling remote API on " + endpoint);

       
            // Simulate a failure 50% of the time, representing a transient issue.
            if (System.currentTimeMillis() % 2 == 0) {
                System.out.println("API call succeeded.");
                return "Data for " + endpoint;
            } else {
                // Throws an exception which is not handled by a retry mechanism.
                throw new RemoteServiceException("Network connection lost or API is busy.");
            }
        
		
    }

    /**
     * Another critical operation that is prone to transient failures 
     * but lacks a retry mechanism.
     */
    public void updateDatabaseRecord(int recordId) {
        System.out.println("Starting database update for record ID: " + recordId);
        
        // Simulate a database deadlock or transient connection issue
        if (Math.random() < 0.3) { // 30% chance of transient failure
            System.err.println("Database operation failed due to connection error.");
            // Immediately fails and propagates the exception.
            throw new RemoteServiceException("Database connection timed out during update.");
        }
        
        System.out.println("Database update successful.");
    }
}