package pt.attendancetracking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.attendancetracking.model.*;
import pt.attendancetracking.repository.AppointmentRepository;
import pt.attendancetracking.repository.MemberRepository;
import pt.attendancetracking.repository.PackageRepository;
import pt.attendancetracking.model.Package;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/test-data")
@RequiredArgsConstructor
public class TestDataController {

    private final MemberRepository memberRepository;
    private final PackageRepository packageRepository;
    private final AppointmentRepository appointmentRepository;

    @PostMapping("/initialize")
    public ResponseEntity<Map<String, Object>> initializeTestData() {
        try {
            // Create 10 members with packages
            List<Member> members = createMembers();

            // Create appointments for next week
            List<Appointment> appointments = createAppointments(members);

            return ResponseEntity.ok(Map.of(
                    "message", "Test data initialized successfully",
                    "membersCreated", members.size(),
                    "appointmentsCreated", appointments.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Failed to initialize test data: " + e.getMessage()
            ));
        }
    }

    private List<Member> createMembers() {
        List<Member> members = new ArrayList<>();
        String[] names = {
                "John Smith", "Emma Wilson", "Michael Brown", "Sarah Davis",
                "James Johnson", "Lisa Anderson", "David Miller", "Jennifer Taylor",
                "Robert Wilson", "Maria Garcia"
        };

        for (int i = 0; i < 10; i++) {
            Member member = Member.builder()
                    .name(names[i])
                    .email(names[i].toLowerCase().replace(" ", ".") + "@example.com")
                    .build();

            // Create package for member
            Package memberPackage = Package.builder()
                    .totalSessions(20)
                    .usedSessions(0)
                    .remainingSessions(20)
                    .remainingCancellations(3)
                    .status(PackageStatus.ACTIVE)
                    .build();

            member.setActivePackage(memberPackage);
            members.add(memberRepository.save(member));
        }

        return members;
    }

    private List<Appointment> createAppointments(List<Member> members) {
        List<Appointment> appointments = new ArrayList<>();
        Random random = new Random();
        LocalDateTime nextMonday = LocalDateTime.now().plusWeeks(1)
                .withHour(9).withMinute(0).withSecond(0).withNano(0)
                .with(java.time.DayOfWeek.MONDAY);

        // Create 30 appointments for next week
        for (int i = 0; i < 30; i++) {
            // Random member
            Member member = members.get(random.nextInt(members.size()));

            // Random time next week (Mon-Fri, 9 AM - 4 PM)
            int dayOffset = random.nextInt(5); // Monday to Friday
            int hourOffset = random.nextInt(8); // 9 AM to 4 PM

            LocalDateTime appointmentTime = nextMonday
                    .plusDays(dayOffset)
                    .plusHours(hourOffset);

            // Check if slot is available
            if (!appointmentRepository.existsByAppointmentTime(appointmentTime)) {
                Appointment appointment = Appointment.builder()
                        .member(member)
                        .appointmentTime(appointmentTime)
                        .status(AppointmentStatus.SCHEDULED)
                        .build();

                appointments.add(appointmentRepository.save(appointment));
            } else {
                // If slot is taken, try again
                i--;
            }
        }

        return appointments;
    }
}