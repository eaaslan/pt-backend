package pt.attendancetracking.test;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.attendancetracking.model.Package;
import pt.attendancetracking.model.*;
import pt.attendancetracking.repository.AppointmentRepository;
import pt.attendancetracking.repository.MemberRepository;
import pt.attendancetracking.repository.PersonalTrainerRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DataSeederService {
    private final MemberRepository memberRepository;
    private final PersonalTrainerRepository ptRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    @Transactional
    public void seedData() {
        // Create admin member
        Member admin = createAdminMember();

        // Create PT member
        PersonalTrainer pt = createPersonalTrainer();

        // Create regular members with packages
        List<Member> members = createMembersWithPackages(pt);

        // Create appointments for members with PT
        createAppointments(members, pt);
    }

    private Member createAdminMember() {
        if (!memberRepository.existsByUsername("admin")) {
            Member admin = Member.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .name("Admin User")
                    .email("admin@gym.com")
                    .build();
            admin.setRole(UserRole.ROLE_ADMIN);
            return memberRepository.save(admin);
        }
        return memberRepository.findByUsername("admin").orElseThrow();
    }

    private PersonalTrainer createPersonalTrainer() {
        if (!memberRepository.existsByUsername("trainer")) {
            PersonalTrainer pt = PersonalTrainer.builder()
                    .username("trainer")
                    .password(passwordEncoder.encode("trainer123"))
                    .name("Personal Trainer")
                    .email("trainer@gym.com")
                    .build();
            return ptRepository.save(pt);
        }
        return ptRepository.findByUsername("trainer").orElseThrow();
    }

    private List<Member> createMembersWithPackages(PersonalTrainer pt) {
        List<Member> members = new ArrayList<>();

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

            if (!memberRepository.existsByUsername(username)) {
                // Create member
                Member member = Member.builder()
                        .username(username)
                        .password(passwordEncoder.encode("member123"))
                        .name(fullName)
                        .email(username + "@example.com")
                        .assignedPt(pt)
                        .build();
                member.setRole(UserRole.ROLE_MEMBER);

                // Create and associate package
                Package trainingPackage = Package.builder()
                        .totalSessions(12)
                        .usedSessions(0)
                        .remainingSessions(12)
                        .remainingCancellations(3)
                        .status(PackageStatus.ACTIVE)
                        .member(member)
                        .build();

                member.setActivePackage(trainingPackage);
                member = memberRepository.save(member);
                members.add(member);
            }
        }

        return members;
    }

    private void createAppointments(List<Member> members, PersonalTrainer pt) {
        LocalDateTime startDate = LocalDateTime.now().plusDays(1)
                .withHour(9).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDate = startDate.plusDays(14);

        for (Member member : members) {
            // Create at least 2 appointments per member
            int appointmentsCount = 2 + random.nextInt(3); // 2-4 appointments per member

            for (int i = 0; i < appointmentsCount; i++) {
                LocalDateTime appointmentTime = generateRandomAppointmentTime(startDate, endDate);

                // Keep generating new time until we find an available slot
                while (appointmentRepository.isPtBookedForTimeSlot(pt.getId(), appointmentTime) ||
                        appointmentRepository.existsByAppointmentTime(appointmentTime)) {
                    appointmentTime = generateRandomAppointmentTime(startDate, endDate);
                }

                Appointment appointment = Appointment.builder()
                        .member(member)
                        .personalTrainer(pt)
                        .appointmentTime(appointmentTime)
                        .status(AppointmentStatus.SCHEDULED)
                        .build();

                member.addAppointment(appointment);
                pt.addAppointment(appointment);
                appointmentRepository.save(appointment);

                // Update package used sessions if package exists
                if (member.getActivePackage() != null) {
                    Package pkg = member.getActivePackage();
                    pkg.setUsedSessions(pkg.getUsedSessions() + 1);
                    pkg.setRemainingSessions(pkg.getTotalSessions() - pkg.getUsedSessions());
                }
            }

            memberRepository.save(member);
        }
    }

    private LocalDateTime generateRandomAppointmentTime(LocalDateTime startDate, LocalDateTime endDate) {
        long hoursBetween = ChronoUnit.HOURS.between(startDate, endDate);
        long randomHours = random.nextInt((int) hoursBetween);
        LocalDateTime randomTime = startDate.plusHours(randomHours);

        // Ensure it's between 9 AM and 8 PM
        if (randomTime.getHour() < 9) {
            randomTime = randomTime.withHour(9);
        } else if (randomTime.getHour() >= 20) {
            randomTime = randomTime.withHour(19);
        }

        return randomTime.withMinute(0).withSecond(0).withNano(0);
    }
}