package pt.attendancetracking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import pt.attendancetracking.model.Appointment;
import pt.attendancetracking.model.AppointmentStatus;
import pt.attendancetracking.model.Member;
import pt.attendancetracking.repository.AppointmentRepository;
import pt.attendancetracking.repository.MemberRepository;
import pt.attendancetracking.util.TimeSlotUtil;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Member testMember;
    private Appointment testAppointment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testMember = Member.builder()
                .id(1L)
                .name("Test Member")
                .email("test@example.com")
                .build();
        testAppointment = Appointment.builder()
                .id(1L)
                .appointmentTime(LocalDateTime.of(2024, 1, 1, 10, 0))
                .status(AppointmentStatus.SCHEDULED)
                .member(testMember)
                .build();
    }

    @Nested
    @DisplayName("Get Appointment Tests")
    class GetAppointmentTests {

        @Test
        @DisplayName("Should return appointment by ID")
        void getAppointmentById_ShouldReturnAppointment() {
            // Arrange
            when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));

            // Act
            Appointment appointment = appointmentService.getAppointmentById(1L);

            // Assert
            assertThat(appointment).isEqualTo(testAppointment);
            verify(appointmentRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when appointment not found")
        void getAppointmentById_ShouldThrowExceptionWhenNotFound() {
            // Arrange
            when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> appointmentService.getAppointmentById(1L))
                    .isInstanceOf(RuntimeException.class);
            verify(appointmentRepository, times(1)).findById(1L);
        }
    }

    @Nested
    @DisplayName("Schedule Appointment Tests")
    class ScheduleAppointmentTests {

        @Test
        @DisplayName("Should schedule appointment successfully")
        void scheduleAppointment_ShouldScheduleSuccessfully() {
            // Arrange
            LocalDateTime appointmentTime = LocalDateTime.of(2024, 1, 1, 10, 0);
           try( MockedStatic<TimeSlotUtil> utilities= mockStatic(TimeSlotUtil.class))  {

            utilities.when(()->TimeSlotUtil.isValidBusinessHour(appointmentTime)).thenReturn(true);
            utilities.when(()->appointmentRepository.existsByAppointmentTime(appointmentTime)).thenReturn(false);
            utilities.when(()->memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

            // Act
            Optional<Appointment> scheduledAppointment = appointmentService.scheduleAppointment(1L, appointmentTime);

            // Assert
            assertThat(scheduledAppointment).isPresent();
            verify(appointmentRepository, times(1)).save(any(Appointment.class));
        }
}
        @Test
        @DisplayName("Should throw exception for invalid business hour")
        void scheduleAppointment_ShouldThrowExceptionForInvalidBusinessHour() {
            LocalDateTime appointmentTime = LocalDateTime.of(2024, 1, 1, 7, 0);
            try(MockedStatic<TimeSlotUtil> utilities=mockStatic(TimeSlotUtil.class)) {

            utilities.when(()->TimeSlotUtil.isValidBusinessHour(appointmentTime)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> appointmentService.scheduleAppointment(1L, appointmentTime))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Appointment can only be scheduled during business hours (8 AM - 5 PM)");
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }
        }
    }

    @Nested
    @DisplayName("Check-In Appointment Tests")
    class CheckInTests {

        @Test
        @DisplayName("Should successfully check-in")
        void checkIn_ShouldCheckInSuccessfully() {
            // Arrange
            LocalDateTime checkInTime = LocalDateTime.of(2024, 1, 1, 10, 0);

            try (MockedStatic<TimeSlotUtil> utilities = mockStatic(TimeSlotUtil.class)) {
                utilities.when(() -> TimeSlotUtil.isValidBusinessHour(checkInTime)).thenReturn(true);
                utilities.when(() -> TimeSlotUtil.roundToNearestHour(checkInTime)).thenReturn(checkInTime);

                when(appointmentRepository.existsAppointmentForTimeByOtherMember(checkInTime, 1L)).thenReturn(false);
                when(appointmentRepository.existsCheckedInAppointmentForTime(checkInTime)).thenReturn(false);
                when(appointmentRepository.findAppointmentByMemberAndTimeScheduledStatus(1L, checkInTime)).thenReturn(Optional.of(testAppointment));

                // Act
                Optional<Appointment> checkedInAppointment = appointmentService.checkIn(1L, checkInTime);

                // Assert
                assertThat(checkedInAppointment).isPresent();
                assertThat(checkedInAppointment.get().getStatus()).isEqualTo(AppointmentStatus.CHECKED_IN);
                verify(appointmentRepository, times(1)).save(testAppointment);
            } }


        @Test
        @DisplayName("Should throw exception for conflicting appointment")
        void checkIn_ShouldThrowExceptionForConflictingAppointment() {
            // Arrange
            LocalDateTime checkInTime = LocalDateTime.of(2024, 1, 1, 10, 0);

            try(MockedStatic<TimeSlotUtil> utilities=mockStatic(TimeSlotUtil.class)){
            utilities.when(() ->TimeSlotUtil.isValidBusinessHour(checkInTime)).thenReturn(true);
            utilities.when(()->TimeSlotUtil.roundToNearestHour(checkInTime)).thenReturn(checkInTime);

            when(appointmentRepository.existsAppointmentForTimeByOtherMember(checkInTime, 1L)).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> appointmentService.checkIn(1L, checkInTime))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("This time slot is already booked by another member");
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }
    }
} }
