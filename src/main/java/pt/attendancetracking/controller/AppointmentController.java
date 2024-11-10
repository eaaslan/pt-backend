package pt.attendancetracking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.attendancetracking.model.Appointment;
import pt.attendancetracking.service.AppointmentService;
import pt.attendancetracking.util.CheckInRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/appointments")
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"})
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<List<Appointment>> getAppointments(
            @RequestParam(required = false) Long memberId) {
        try {
            if (memberId != null) {
                List<Appointment> appointments = appointmentService.getMemberAllAppointment(memberId);
                return appointments.isEmpty()
                        ? ResponseEntity.noContent().build()
                        : ResponseEntity.ok(appointments);
            }
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












