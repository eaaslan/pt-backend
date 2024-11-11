package pt.attendancetracking.test;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/seed")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://127.0.0.1:5500", "https://pt-frontend-gtju.vercel.app"})
public class DataSeederController {

    private final pt.attendancetracking.test.DataSeederService dataSeederService;

    @PostMapping("/initialize")
    public ResponseEntity<?> seedData() {
        try {
            dataSeederService.seedData();
            return ResponseEntity.ok().body(Map.of(
                    "message", "Test data initialized successfully",
                    "details", "Created admin, PT, members, packages and appointments"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Failed to initialize test data",
                    "message", e.getMessage()
            ));
        }
    }
}