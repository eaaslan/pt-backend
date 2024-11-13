package pt.attendancetracking.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import pt.attendancetracking.model.Appointment;
import pt.attendancetracking.service.AppointmentService;
import pt.attendancetracking.util.CheckInRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"})

public class AppointmentController {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    private final AppointmentService appointmentService;

    //    @GetMapping("/member/appointments")
//    @PreAuthorize("hasAuthority('ROLE_MEMBER')")
//    public ResponseEntity<?> getMemberAppointments(@AuthenticationPrincipal UserDetails userDetails) {
//        System.out.println("User authorities: " + userDetails.getAuthorities());
//        try {
//            String username = userDetails.getUsername();
//            List<Appointment> appointments = appointmentService.getCurrentMemberAppointments(username);
//            return appointments.isEmpty()
//                    ? ResponseEntity.noContent().build()
//                    : ResponseEntity.ok(appointments);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Map.of(
//                    "error", "Error retrieving appointments: " + e.getMessage()
//            ));
//        }
//    }
    @GetMapping("/member/appointments")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<?> getMemberAppointments(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Attempting to get appointments for user: {}",
                userDetails != null ? userDetails.getUsername() : "null");
        logger.info("User authorities: {}",
                userDetails != null ? userDetails.getAuthorities() : "null");

        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authentication required"));
            }

            String username = userDetails.getUsername();
            List<Appointment> appointments = appointmentService.getCurrentMemberAppointments(username);

            if (appointments.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "message", "No appointments found",
                        "appointments", Collections.emptyList()
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Appointments retrieved successfully",
                    "appointments", appointments,
                    "totalAppointments", appointments.size(),
                    "member", username
            ));

        } catch (UsernameNotFoundException e) {
            logger.error("Member not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Member not found"));
        } catch (Exception e) {
            logger.error("Error retrieving appointments: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving appointments: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Appointment>> getAppointments(
            @RequestParam(required = false) Long memberId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            if (memberId != null) {
                List<Appointment> appointments = appointmentService.getMemberAllAppointment(memberId);
                return appointments.isEmpty()
                        ? ResponseEntity.noContent().build()
                        : ResponseEntity.ok(appointments);
            }

            String username = userDetails.getUsername();

            // Could add getAllAppointments() here if needed
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/appointments/{appointmentId}
    @GetMapping("/{appointmentId}")
    public ResponseEntity<Appointment> getAppointment(@PathVariable Long appointmentId) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);
            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/check-in/{memberId}")
    public ResponseEntity<?> checkIn(@PathVariable Long memberId, @RequestBody CheckInRequest request) {
        LocalDateTime checkInTime = request.getCheckInTime();

        System.out.println(checkInTime);
        try {
            Optional<Appointment> appointment = appointmentService.checkIn(memberId, checkInTime);

            return appointment
                    .map(a -> ResponseEntity.ok(Map.of(
                            "message", "Check-in successful",
                            "appointment", a,
                            "checkInTime", a.getCheckInTime()
                    )))
                    .orElse(ResponseEntity.badRequest().body(Map.of(
                            "error", "Check-in failed"
                    )));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{memberId}/book")
    public ResponseEntity<?> scheduleAppointment(
            @PathVariable Long memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentTime) {
        try {
            Optional<Appointment> appointment = appointmentService.scheduleAppointment(memberId, appointmentTime);
            return appointment
                    .map(a ->
                            ResponseEntity.ok(Map.of(
                                    "message", "schedule is successful"
                                    , "appointment", a,
                                    "appointmentTime", a.getAppointmentTime()

                            ))
                    ).orElse(ResponseEntity.badRequest().body(Map.of(
                                    "error", "check-in failed"
                            ))

                    );

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }
}












