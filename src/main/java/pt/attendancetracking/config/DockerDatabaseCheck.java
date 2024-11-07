package pt.attendancetracking.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;

@Configuration
@Profile("!prod")
public class DockerDatabaseCheck {

    private static final String PROJECT_ROOT = "G:\\spring projects\\attendance-tracking";

    @Bean
    public CommandLineRunner checkDockerDatabase() {
        return args -> {
            if (!isDatabaseRunning()) {
                startDockerCompose();
            }
        };
    }

    private boolean isDatabaseRunning() {
        try {
            String scriptPath = Paths.get(PROJECT_ROOT, "check_docker_db.ps1").toString();
            ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", "-File", scriptPath);
            processBuilder.directory(Paths.get(PROJECT_ROOT).toFile());
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void startDockerCompose() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("docker-compose", "up", "-d");
            processBuilder.directory(Paths.get(PROJECT_ROOT).toFile());
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Docker Compose started successfully");
            } else {
                System.err.println("Failed to start Docker Compose");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}