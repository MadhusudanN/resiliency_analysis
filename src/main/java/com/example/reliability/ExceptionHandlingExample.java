package com.example.reliability;

import java.io.IOException;

public class ExceptionHandlingExample {

    // SonarQube should flag: "Catching 'Exception' is not allowed." (Rule S2222)
    public void unreliableCatchBlock() {
        try {
            // Some operation that can throw a checked exception (e.g., IOException)
            // Or an unchecked error (e.g., OutOfMemoryError)
            System.out.println("Operation in progress...");
        } catch (Exception e) {
            // This catches checked exceptions, unchecked runtime exceptions,
            // and potentially serious errors (like OutOfMemoryError, which should
            // generally not be caught). This hides critical failures.
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    // SonarQube should flag: "Replace this use of 'printStackTrace()' with a logger." (Rule S131)
    public void poorLogging() {
        try {
            if (true) {
                throw new IOException("Simulated IO Failure");
            }
        } catch (IOException e) {
            e.printStackTrace(); // Bad practice: output is non-standard and often missed.
        }
    }
}