package pt.attendancetracking.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class PostgresConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    private static String staticDbUrl;
    private static String staticDbUsername;
    private static String staticDbPassword;

    @PostConstruct
    private void initStaticFields() { //we can not assign values with @Value annotation to static directly
        staticDbUrl = this.dbUrl;
        staticDbUsername = this.dbUsername;
        staticDbPassword = this.dbPassword;
    }

    public static void ensureDatabaseIsRunning() {
        if (!isPostgresRunning()) {
            startDockerCompose();
        }
    }

    private static boolean isPostgresRunning() {
        try (Connection connection = DriverManager.getConnection(staticDbUrl, staticDbUsername, staticDbPassword)) {
            return connection.isValid(2);
        } catch (SQLException e) {
            System.out.println("PostgreSQL is not running. Attempting to start it...");
            return false;
        }
    }

    private static void startDockerCompose() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("docker-compose", "-f", "docker-compose.test.yml", "up", "-d");
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            process.waitFor();
            System.out.println("Docker Compose started for test database.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to start Docker Compose for test database.", e);
        }
    }
}
