//package pt.attendancetracking.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import pt.attendancetracking.model.Appointment;
//import pt.attendancetracking.service.AppointmentService;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/appointments")  // Updated path to reflect new model
//@RequiredArgsConstructor
//public class AppointmentController {  // Renamed to Controller
//
//    private final AppointmentService appointmentService;  // Inject the service, not repository
//
//    @GetMapping
//    public ResponseEntity<List<Appointment>> getAllAppointments() {
//        List<Appointment> appointments = appointmentService.getAppointments();
//        return appointments.isEmpty()
//                ? ResponseEntity.noContent().build()
//                : ResponseEntity.ok(appointments);
//    }

//    @PostMapping("/check-in/{memberId}")
//    public ResponseEntity<?> checkIn(@PathVariable Long memberId) {
//        try {
//            Appointment appointment = appointmentService.checkIn(memberId);  // Use service method
//            return ResponseEntity.ok(Map.of(
//                    "message", "Check-in successful",
//                    "checkInTime", appointment.getCheckInTime()
//            ));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(Map.of(
//                    "error", e.getMessage()
//            ));
//        }
//    }
//}