package pt.attendancetracking.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pt.attendancetracking.model.Appointment;
import pt.attendancetracking.model.AppointmentStatus;
import pt.attendancetracking.model.Member;
import pt.attendancetracking.repository.AppointmentRepository;
import pt.attendancetracking.repository.MemberRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@SpringBootTest // veya @SpringBootTest
class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member member1;
    private Member member2;
    private LocalDateTime appointmentTime;

    @BeforeEach
    void setUp() {
        // Test verileri oluştur
        member1 = memberRepository.save(Member.builder()
                .name("Test Member 1")
                .email("test1@test.com")
                .build());

        member2 = memberRepository.save(Member.builder()
                .name("Test Member 2")
                .email("test2@test.com")
                .build());

        appointmentTime = LocalDateTime.of(2024, 1, 1, 12, 0);
    }

    @Test
    void appointmentsByDateTime_ShouldReturnAllAppointmentTimes() {
        // Given
        Appointment appointment1 = createAppointment(member1, appointmentTime, AppointmentStatus.SCHEDULED);
        Appointment appointment2 = createAppointment(member2, appointmentTime.plusHours(1), AppointmentStatus.SCHEDULED);
        appointmentRepository.saveAll(List.of(appointment1, appointment2));

        // When
        List<LocalDateTime> times = appointmentRepository.appointmentsByDateTime(appointmentTime);

        // Then
        Assertions.assertEquals(2, times.size());
        Assertions.assertTrue(times.contains(appointmentTime));
        Assertions.assertTrue(times.contains(appointmentTime.plusHours(1)));
    }

    @Test
    void findAppointmentByMemberAndTime_ShouldReturnAppointment_WhenExists() {
        // Given
        Appointment savedAppointment = createAndSaveAppointment(member1, appointmentTime, AppointmentStatus.SCHEDULED);

        // When
        Optional<Appointment> found = appointmentRepository
                .findAppointmentByMemberAndTime(member1.getId(), appointmentTime);

        // Then
        assertTrue(found.isPresent());
        assertEquals(savedAppointment.getId(), found.get().getId());
    }

    @Test
    void findAppointmentByMemberAndTime_ShouldReturnEmpty_WhenNotExists() {
        // When
        Optional<Appointment> found = appointmentRepository
                .findAppointmentByMemberAndTime(member1.getId(), appointmentTime);

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void findAppointmentByMemberAndTimeScheduledStatus_ShouldReturnAppointment_WhenScheduled() {
        // Given
        Appointment savedAppointment = createAndSaveAppointment(member1, appointmentTime, AppointmentStatus.SCHEDULED);

        // When
        Optional<Appointment> found = appointmentRepository
                .findAppointmentByMemberAndTimeScheduledStatus(member1.getId(), appointmentTime);

        // Then
        assertTrue(found.isPresent());
        assertEquals(savedAppointment.getId(), found.get().getId());
    }

    @Test
    void findAppointmentByMemberAndTimeScheduledStatus_ShouldReturnEmpty_WhenNotScheduled() {
        // Given
        createAndSaveAppointment(member1, appointmentTime, AppointmentStatus.CHECKED_IN);

        // When
        Optional<Appointment> found = appointmentRepository
                .findAppointmentByMemberAndTimeScheduledStatus(member1.getId(), appointmentTime);

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void existsByAppointmentTime_ShouldReturnTrue_WhenExists() {
        // Given
        createAndSaveAppointment(member1, appointmentTime, AppointmentStatus.SCHEDULED);

        // When
        boolean exists = appointmentRepository.existsByAppointmentTime(appointmentTime);

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByAppointmentTime_ShouldReturnFalse_WhenNotExists() {
        // When
        boolean exists = appointmentRepository.existsByAppointmentTime(appointmentTime);

        // Then
        assertFalse(exists);
    }

    @Test
    void existsCheckedInAppointmentForTime_ShouldReturnTrue_WhenCheckedInExists() {
        // Given
        createAndSaveAppointment(member1, appointmentTime, AppointmentStatus.CHECKED_IN);

        // When
        boolean exists = appointmentRepository.existsCheckedInAppointmentForTime(appointmentTime);

        // Then
        assertTrue(exists);
    }

    @Test
    void existsCheckedInAppointmentForTime_ShouldReturnFalse_WhenOnlyScheduledExists() {
        // Given
        createAndSaveAppointment(member1, appointmentTime, AppointmentStatus.SCHEDULED);

        // When
        boolean exists = appointmentRepository.existsCheckedInAppointmentForTime(appointmentTime);

        // Then
        assertFalse(exists);
    }

    @Test
    void existsAppointmentForTimeByOtherMember_ShouldReturnTrue_WhenOtherMemberHasAppointment() {
        // Given
        createAndSaveAppointment(member2, appointmentTime, AppointmentStatus.SCHEDULED);

        // When
        boolean exists = appointmentRepository
                .existsAppointmentForTimeByOtherMember(appointmentTime, member1.getId());

        // Then
        assertTrue(exists);
    }

    @Test
    void existsAppointmentForTimeByOtherMember_ShouldReturnFalse_WhenOnlySameMemberHasAppointment() {
        // Given
        createAndSaveAppointment(member1, appointmentTime, AppointmentStatus.SCHEDULED);

        // When
        boolean exists = appointmentRepository
                .existsAppointmentForTimeByOtherMember(appointmentTime, member1.getId());

        // Then
        assertFalse(exists);
    }

    private Appointment createAppointment(Member member, LocalDateTime time, AppointmentStatus status) {
        return Appointment.builder()
                .member(member)
                .appointmentTime(time)
                .status(status)
                .build();
    }

    private Appointment createAndSaveAppointment(Member member, LocalDateTime time, AppointmentStatus status) {
        Appointment appointment = createAppointment(member, time, status);
        return appointmentRepository.save(appointment);
    }
}