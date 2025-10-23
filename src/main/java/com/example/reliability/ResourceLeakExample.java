package com.example.reliability;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ResourceLeakExample {

    // SonarQube should flag: "Close this 'FileInputStream'." (Rule S2093)
    public void unreliableFileRead(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        int data = fis.read();
        // Resource 'fis' not closed.
        System.out.println("Read data: " + data);
    }

    // SonarQube should flag: "Close this 'Connection' and 'Statement'." (Rule S2095)
    public void unreliableDatabaseOperation(String sql) throws SQLException {
        // Using H2 database for a simple, in-memory connection
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        Statement stmt = conn.createStatement();

        try {
            stmt.execute(sql);
        } finally {
            // Resources are not closed in the finally block. This is the source of the leak.
        }
    }
}