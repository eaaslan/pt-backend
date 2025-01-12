package pt.attendancetracking.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pt.attendancetracking.dto.AppointmentDTO;
import pt.attendancetracking.model.Appointment;
import pt.attendancetracking.model.Member;
import pt.attendancetracking.service.AppointmentService;
import pt.attendancetracking.service.MemberService;
import pt.attendancetracking.service.PersonalTrainerService;
import pt.attendancetracking.util.CheckInRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
    private final MemberService memberService;
    private final AppointmentService appointmentService;
    private final PersonalTrainerService personalTrainerService;

    /**
     * Get appointments for the currently authenticated user
     * Works for both members and PTs, returning appropriate appointments based on role
     */
    @GetMapping("/member/current")
    public ResponseEntity<?> getCurrentUserAppointments(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String username = userDetails.getUsername();
            List<AppointmentDTO> appointments = appointmentService.getCurrentMemberAppointments(username);

            return ResponseEntity.ok(Map.of(
                    "appointments", appointments,
                    "total", appointments.size()
            ));
        } catch (Exception e) {
            logger.error("Error fetching appointments: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    /**
     * Get PT appointments for the assigned member
     */
    @GetMapping("/pt/current")
    public ResponseEntity<?> getCurrentMembersPtAppointments(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            Member member = memberService.getMemberByUserName(userDetails.getUsername());
            Long ptId = member.getAssignedPt().getId();
            List<AppointmentDTO> appointments = appointmentService.getPtAppointments(ptId);
            return appointments.isEmpty()
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.ok(appointments);
        } catch (Exception e) {
            logger.error("Error fetching PT appointments: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get appointments for a specific member (requires PT or Admin role)
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<?> getMemberAppointments(
            @PathVariable Long memberId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<Appointment> appointments = appointmentService.getMemberAllAppointment(memberId);
            return appointments.isEmpty()
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.ok(appointments);
        } catch (Exception e) {
            logger.error("Error fetching member appointments: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get appointments for a specific PT (requires PT or Admin role)
     */
    @GetMapping("/pt/{ptId}")
    public ResponseEntity<?> getPtAppointments(@PathVariable Long ptId) {
        try {
            List<AppointmentDTO> appointments = appointmentService.getPtAppointments(ptId);
            return appointments.isEmpty()
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.ok(appointments);
        } catch (Exception e) {
            logger.error("Error fetching PT appointments: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get a specific appointment by ID
     */
    @GetMapping("/{appointmentId}")
    public ResponseEntity<?> getAppointment(@PathVariable Long appointmentId) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);
            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            logger.error("Error fetching appointment: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Schedule a new appointment
     */
    @PostMapping("/schedule/{memberId}")
    public ResponseEntity<?> scheduleAppointment(
            @PathVariable Long memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentTime) {
        try {
            return appointmentService.scheduleAppointment(memberId, appointmentTime)
                    .map(appointment -> ResponseEntity.ok(Map.of(
                            "message", "Appointment scheduled successfully",
                            "appointment", appointment
                    )))
                    .orElse(ResponseEntity.badRequest().body(Map.of(
                            "error", "Failed to schedule appointment"
                    )));
        } catch (RuntimeException e) {
            logger.error("Error scheduling appointment: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Check in for an appointment (requires PT or Admin role)
     */
    @PostMapping("/check-in/{memberId}")
    public ResponseEntity<?> checkIn(
            @PathVariable Long memberId,
            @RequestBody CheckInRequest request) {
        try {
            return appointmentService.checkIn(memberId, request.getCheckInTime())
                    .map(appointment -> ResponseEntity.ok(Map.of(
                            "message", "Check-in successful",
                            "appointment", appointment
                    )))
                    .orElse(ResponseEntity.badRequest().body(Map.of(
                            "error", "Check-in failed"
                    )));
        } catch (RuntimeException e) {
            logger.error("Error during check-in: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    // TODO  check if it works

    /**
     * Check in for an appointment
     */
    @PostMapping("/check-in/member/{memberId}")
    public ResponseEntity<?> checkIn(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CheckInRequest request) {
        try {
            //todo check which one is
            // or Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); just add this

            String userName = userDetails.getUsername();
            Long memberId = memberService.getMemberByUserName(userName).getId();
            return appointmentService.checkIn(memberId, request.getCheckInTime())
                    .map(appointment -> ResponseEntity.ok(Map.of(
                            "message", "Check-in successful",
                            "appointment", appointment
                    )))
                    .orElse(ResponseEntity.badRequest().body(Map.of(
                            "error", "Check-in failed"
                    )));
        } catch (RuntimeException e) {
            logger.error("Error during check-in: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}


