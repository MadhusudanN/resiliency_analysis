package com.example.reliability;

public class ConcurrencyIssueExample {

    private int counter = 0; // Shared state

    // SonarQube should flag: "Make sure that this state is safe to be used in a multithreaded environment." (Rule S2184)
    // Access to 'counter' is not synchronized, leading to a race condition and data corruption under concurrent load.
    public void incrementUnsafe() {
        counter++;
    }

    // SonarQube should flag: "Refactor this method to reduce its Cognitive Complexity from X to 15 or less." (Rule S3776)
    // This is an example of an overly complex method reducing resiliency.
    public int highlyComplexMethod(int a, int b) {
        int result = 0; // Complexity 1

        if (a > 10) { // Complexity +1
            if (b > 10) { // Complexity +2
                result = a + b;
            } else if (b < 0) { // Complexity +3
                result = a - b;
            } else {
                result = a;
            }
        } else if (a < 0) { // Complexity +4
            for (int i = 0; i < 5; i++) { // Complexity +5
                if (b == i) { // Complexity +6
                    result = i;
                } else {
                    // ... more logic...
                }
            }
        }
        // This is a simplified example; a real-world complex method would be much longer
        return result;
    }
}