package pt.attendancetracking.repository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import pt.attendancetracking.model.Appointment;
import pt.attendancetracking.model.AppointmentStatus;
import pt.attendancetracking.model.Member;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Slf4j
class AppointmentRepositoryTest {

    @BeforeAll
    static void setUpDatabase() {
        PostgresConfig.ensureDatabaseIsRunning();
    }

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member testMember;
    private Member otherMember;
    private LocalDateTime baseDateTime;


    @BeforeEach
    void setUp() {
        // Create test members
        testMember = Member.builder()
                .name("Test Member")
                .email("test@example.com")
                .build();
        memberRepository.save(testMember);

        otherMember = Member.builder()
                .name("Other Member")
                .email("other@example.com")
                .build();
        memberRepository.save(otherMember);

        // Set base datetime for tests
        baseDateTime = LocalDateTime.of(2024, 1, 1, 10, 0);
    }

    @Test
    @Transactional
    @Commit
    void createMember(){
        testMember = Member.builder()
                .name("Test Member")
                .email("test@example.com")
                .build();
        memberRepository.save(testMember);

        otherMember = Member.builder()
                .name("Other Member")
                .email("other@example.com")
                .build();
        memberRepository.save(otherMember);
    }

    @Nested
    @DisplayName("Appointment Time Query Tests")
    class AppointmentTimeTests {
        @Test
        @DisplayName("Should return all appointment times")
        void appointmentsByDateTime_ShouldReturnAllTimes()  {
            // Arrange
            LocalDateTime time1 = baseDateTime;
            LocalDateTime time2 = baseDateTime.plusHours(5);
            log.info("Creating test appointments at times: {} and {}",
                    time1.format(DateTimeFormatter.ISO_LOCAL_TIME),
                    time2.format(DateTimeFormatter.ISO_LOCAL_TIME));

            createAppointment(testMember, time1, AppointmentStatus.SCHEDULED);
            createAppointment(testMember, time2, AppointmentStatus.SCHEDULED);

            // Act
            log.info("Fetching appointments for datetime: {}",
                    baseDateTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
            List<LocalDateTime> times = appointmentRepository.appointmentsByDateTime(baseDateTime);

            // Assert
            log.info("Retrieved {} appointments at times: {}",
                    times.size(),
                    times.stream()
                            .map(t -> t.format(DateTimeFormatter.ISO_LOCAL_TIME))
                            .collect(Collectors.joining(", ")));

            assertThat(times).hasSize(2)
                    .contains(time1, time2);

        }
    }

    @Nested
    @DisplayName("Find Appointment Tests")
    class FindAppointmentTests {

        @Test
        @DisplayName("Should find appointment by member and time when exists")
        void findAppointmentByMemberAndTime_WhenExists() {
            // Arrange
            Appointment saved = createAppointment(testMember, baseDateTime, AppointmentStatus.SCHEDULED);

            // Act
            Optional<Appointment> found = appointmentRepository
                    .findAppointmentByMemberAndTime(testMember.getId(), baseDateTime);

            // Assert
            assertThat(found)
                    .isPresent()
                    .hasValueSatisfying(appointment -> {
                        assertThat(appointment.getId()).isEqualTo(saved.getId());
                        assertThat(appointment.getAppointmentTime()).isEqualTo(baseDateTime);
                    });
        }

        @Test
        @DisplayName("Should return empty when appointment doesn't exist")
        void findAppointmentByMemberAndTime_WhenNotExists() {
            // Act
            Optional<Appointment> found = appointmentRepository
                    .findAppointmentByMemberAndTime(testMember.getId(), baseDateTime);

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Should find scheduled appointment by member and time")
        void findAppointmentByMemberAndTimeScheduledStatus_WhenScheduled() {
            // Arrange
            Appointment saved = createAppointment(testMember, baseDateTime, AppointmentStatus.SCHEDULED);

            // Act
            Optional<Appointment> found = appointmentRepository
                    .findAppointmentByMemberAndTimeScheduledStatus(testMember.getId(), baseDateTime);

            // Assert
            assertThat(found)
                    .isPresent()
                    .hasValueSatisfying(appointment ->
                            assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED)
                    );
        }

        @Test
        @DisplayName("Should not find non-scheduled appointment")
        void findAppointmentByMemberAndTimeScheduledStatus_WhenNotScheduled() {
            // Arrange
            createAppointment(testMember, baseDateTime, AppointmentStatus.CHECKED_IN);

            // Act
            Optional<Appointment> found = appointmentRepository
                    .findAppointmentByMemberAndTimeScheduledStatus(testMember.getId(), baseDateTime);

            // Assert
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("Exists Query Tests")
    class ExistsQueryTests {

        @Test
        @DisplayName("Should confirm appointment exists for time")
        void existsByAppointmentTime() {
            // Arrange
            createAppointment(testMember, baseDateTime, AppointmentStatus.SCHEDULED);

            // Act & Assert
            assertThat(appointmentRepository.existsByAppointmentTime(baseDateTime)).isTrue();
            assertThat(appointmentRepository.existsByAppointmentTime(baseDateTime.plusHours(1))).isFalse();
        }

        @Test
        @DisplayName("Should confirm checked-in appointment exists")
        void existsCheckedInAppointmentForTime() {
            // Arrange
            createAppointment(testMember, baseDateTime, AppointmentStatus.CHECKED_IN);

            // Act & Assert
            assertThat(appointmentRepository.existsCheckedInAppointmentForTime(baseDateTime)).isTrue();
            assertThat(appointmentRepository.existsCheckedInAppointmentForTime(baseDateTime.plusHours(1))).isFalse();
        }

        @Test
        @DisplayName("Should confirm appointment exists for other member")
        void existsAppointmentForTimeByOtherMember() {
            // Arrange
            createAppointment(otherMember, baseDateTime, AppointmentStatus.SCHEDULED);

            // Act & Assert
            assertThat(appointmentRepository
                    .existsAppointmentForTimeByOtherMember(baseDateTime, testMember.getId())).isTrue();
            assertThat(appointmentRepository
                    .existsAppointmentForTimeByOtherMember(baseDateTime, otherMember.getId())).isFalse();
        }
    }

    @Nested
    @DisplayName("Find Member Appointments Tests")
    class FindMemberAppointmentsTests {

        @Test
        @DisplayName("Should find all appointments for member")
        void findAppointmentsByMemberId() {
            // Arrange
            createAppointment(testMember, baseDateTime, AppointmentStatus.SCHEDULED);
            createAppointment(testMember, baseDateTime.plusHours(1), AppointmentStatus.CHECKED_IN);
            createAppointment(otherMember, baseDateTime, AppointmentStatus.SCHEDULED);

            // Act
            List<Appointment> appointments = appointmentRepository.findAppointmentsByMemberId(testMember.getId());

            // Assert
            assertThat(appointments)
                    .hasSize(2)
                    .allMatch(appointment -> appointment.getMember().getId().equals(testMember.getId()));
        }

        @Test
        @DisplayName("Should return empty list when member has no appointments")
        void findAppointmentsByMemberId_WhenNoAppointments() {
            // Act
            List<Appointment> appointments = appointmentRepository.findAppointmentsByMemberId(testMember.getId());

            // Assert
            assertThat(appointments).isEmpty();
        }
    }

    // Helper method to create appointments
    private Appointment createAppointment(Member member, LocalDateTime time, AppointmentStatus status) {
        Appointment appointment = Appointment.builder()
                .member(member)
                .appointmentTime(time)
                .status(status)
                .build();
        return appointmentRepository.save(appointment);
    }
}