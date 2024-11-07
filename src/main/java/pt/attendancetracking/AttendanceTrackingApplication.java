package pt.attendancetracking;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AttendanceTrackingApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttendanceTrackingApplication.class, args);
    }



}
