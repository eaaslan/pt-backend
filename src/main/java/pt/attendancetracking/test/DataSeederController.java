package pt.attendancetracking.test;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class DataSeederController {

    private final DataSeederService dataSeederService;

    @PostMapping("/initialize")
    public ResponseEntity<?> seedData() {
        try {
            dataSeederService.seedData();
            return ResponseEntity.ok().body(Map.of(
                    "message", "Test data initialized successfully",
                    "details", "Created admin, PT, 10 members with packages and appointments"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Failed to initialize test data",
                    "details", e.getMessage()
            ));
        }
    }
}