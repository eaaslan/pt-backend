//
//package pt.attendancetracking.test;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import pt.attendancetracking.model.*;
//import pt.attendancetracking.model.Package;
//import pt.attendancetracking.repository.AppointmentRepository;
//import pt.attendancetracking.repository.MemberRepository;
//import pt.attendancetracking.repository.PackageRepository;
//import pt.attendancetracking.service.AppointmentService;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//
//@Component
//@RequiredArgsConstructor
//public class StandaloneTest implements CommandLineRunner {
//
//    private final AppointmentRepository appointmentRepository;
//    private final MemberRepository memberRepository;
//    private final PackageRepository packageRepository;
//    private final AppointmentService appointmentService;
//
//
//    @Override
//    public void run(String... args) {
//
//        List<Member> members = new ArrayList<>();
//        for (int i = 1; i <= 5; i++) {
//            Member member = Member.builder()
//                    .name("Member " + i)
//                    .email("member" + i + "@example.com")
//                    .build();
//            members.add(memberRepository.save(member));
//        }
//        // Create Packages and assign to members
//        for (int i = 0; i < 5; i++) {
//            Package packageEntity = Package.builder()
//                    .totalSessions(10)
//                    .usedSessions(0)
//                    .remainingSessions(10)
//                    .remainingCancellations(2)
//                    .status(PackageStatus.ACTIVE)
//                    .build();
//            packageEntity.setMember(members.get(i));
//            packageRepository.save(packageEntity);
//        }
//        LocalDateTime appointmentTime = LocalDateTime.of(2025, 11, 1, 9, 0);
//
//        // Create Appointments for each member
//        for (int i = 0; i < 10; i++) {
//            Appointment appointment = Appointment.builder()
//                    .member(members.get(i % 5)) // Assign appointments to members in a round-robin way
//                    .appointmentTime(appointmentTime.plusHours(i))
//                    .checkInTime(null)
//                    .status(AppointmentStatus.SCHEDULED)
//                    .build();
//
//                appointmentService.scheduleAppointment(appointment.getMember().getId(), appointment.getAppointmentTime());
//
//        }
//
//        System.out.println("Database initialized with 5 Members, 10 Appointments, and 5 Packages.");
//    }
//}