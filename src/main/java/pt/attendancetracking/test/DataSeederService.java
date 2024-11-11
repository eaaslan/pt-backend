package pt.attendancetracking.test;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.attendancetracking.model.Package;
import pt.attendancetracking.model.*;
import pt.attendancetracking.repository.AppointmentRepository;
import pt.attendancetracking.repository.MemberRepository;
import pt.attendancetracking.repository.PackageRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DataSeederService {
    private final MemberRepository memberRepository;
    private final PackageRepository packageRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    @Transactional
    public void seedData() {
        // Create admin and PT if they don't exist
        createAdminAndPTIfNotExist();

        // Create members with packages
        List<Member> members = createMembersWithPackages();

        // Create appointments for members
        createAppointments(members);
    }

    private void createAdminAndPTIfNotExist() {
        // Check and create Admin
        if (!memberRepository.existsByUsername("admin")) {
            Member admin = Member.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .name("Admin User")
                    .email("admin@gym.com")
                    .role(UserRole.ROLE_ADMIN)
                    .build();
            memberRepository.save(admin);
        }

        // Check and create PT
        if (!memberRepository.existsByUsername("trainer")) {
            Member pt = Member.builder()
                    .username("trainer")
                    .password(passwordEncoder.encode("trainer123"))
                    .name("Personal Trainer")
                    .email("trainer@gym.com")
                    .role(UserRole.ROLE_PT)
                    .build();
            memberRepository.save(pt);
        }
    }

    private List<Member> createMembersWithPackages() {
        List<Member> members = new ArrayList<>();

        // Names for more realistic test data
        List<String> firstNames = Arrays.asList("John", "Emma", "Michael", "Sophia", "William",
                "Olivia", "James", "Ava", "Alexander", "Isabella");
        List<String> lastNames = Arrays.asList("Smith", "Johnson", "Williams", "Brown", "Jones",
                "Garcia", "Miller", "Davis", "Rodriguez", "Martinez");

        Collections.shuffle(firstNames);
        Collections.shuffle(lastNames);

        for (int i = 0; i < 10; i++) {
            String firstName = firstNames.get(i);
            String lastName = lastNames.get(i);
            String fullName = firstName + " " + lastName;
            String username = (firstName + lastName).toLowerCase();

            // Check if member already exists
            if (!memberRepository.existsByUsername(username)) {
                Member member = Member.builder()
                        .username(username)
                        .password(passwordEncoder.encode("member123"))
                        .name(fullName)
                        .email(username + "@example.com")
                        .role(UserRole.ROLE_MEMBER)
                        .build();

                // Create package with random initial values
                Package trainingPackage = Package.builder()
                        .totalSessions(12)
                        .usedSessions(random.nextInt(5)) // Random number of used sessions 0-4
                        .remainingSessions(12) // Will be calculated later
                        .remainingCancellations(3)
                        .status(PackageStatus.ACTIVE)
                        .build();

                // Update remaining sessions
                trainingPackage.setRemainingSessions(
                        trainingPackage.getTotalSessions() - trainingPackage.getUsedSessions());

                // Set bidirectional relationship
                member.setActivePackage(trainingPackage);
                trainingPackage.setMember(member);

                members.add(memberRepository.save(member));
            }
        }

        return members;
    }

    private void createAppointments(List<Member> members) {
        // Create appointments starting from tomorrow
        LocalDateTime startDate = LocalDateTime.now().plusDays(1)
                .withHour(9).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDate = startDate.plusDays(7);

        for (Member member : members) {
            // Create 2 appointments per member
            for (int i = 0; i < 2; i++) {
                LocalDateTime appointmentTime = generateRandomAppointmentTime(startDate, endDate);

                // Check if the time slot is available
                while (appointmentRepository.existsByAppointmentTime(appointmentTime)) {
                    appointmentTime = generateRandomAppointmentTime(startDate, endDate);
                }

                // Create appointment
                Appointment appointment = Appointment.builder()
                        .member(member)
                        .appointmentTime(appointmentTime)
                        .status(AppointmentStatus.SCHEDULED)
                        .build();

                member.addAppointments(appointment);
                appointmentRepository.save(appointment);

                // Update package used sessions if needed
                if (member.getActivePackage() != null) {
                    Package pkg = member.getActivePackage();
                    pkg.setUsedSessions(pkg.getUsedSessions() + 1);
                    pkg.setRemainingSessions(pkg.getTotalSessions() - pkg.getUsedSessions());
                    packageRepository.save(pkg);
                }
            }
        }
    }

    private LocalDateTime generateRandomAppointmentTime(LocalDateTime startDate, LocalDateTime endDate) {
        long hoursBetween = ChronoUnit.HOURS.between(startDate, endDate);
        long randomHours = random.nextInt((int) hoursBetween);
        LocalDateTime randomTime = startDate.plusHours(randomHours);

        // Ensure it's between 9 AM and 5 PM
        if (randomTime.getHour() < 9) {
            randomTime = randomTime.withHour(9);
        } else if (randomTime.getHour() >= 17) {
            randomTime = randomTime.withHour(16);
        }

        return randomTime.withMinute(0).withSecond(0).withNano(0);
    }
}