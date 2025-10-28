package com.example.reliability;

public class NullPointerExample {

    private String getClientName(Client client) {
        // SonarQube should flag: "A 'NullPointerException' could be thrown here." (Rule S2259)
        // 'client' is not checked for null before calling getName().
        // If client.getName() returns null, .toUpperCase() will also throw an NPE.


        return client.getName().toUpperCase();
    }

    // Auxiliary class
    private static class Client {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static void main(String[] args) {

        // If we uncomment the line below, we'll see the NPE at getClientName(clientB)
         Client clientA = null;

        NullPointerExample example = new NullPointerExample();

        try {
            // This is just to satisfy compilation and demonstrate the flaw.
            // SonarQube will find the potential bug in the method logic itself.
            example.getClientName(clientA);
        } catch (Exception e) {
            // Code is written to fail if 'client' is null.
        }
    }
}