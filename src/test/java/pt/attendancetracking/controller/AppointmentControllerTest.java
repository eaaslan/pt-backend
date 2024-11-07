package pt.attendancetracking.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pt.attendancetracking.model.Appointment;
import pt.attendancetracking.model.AppointmentStatus;
import pt.attendancetracking.service.AppointmentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    @Nested
    @DisplayName("Get Appointments Tests")
    class GetAppointmentsTests {

        @Test
        @DisplayName("Should return appointments for a member")
        void getAppointments_ShouldReturnAppointmentsForMember() throws Exception {
            // Arrange
            Appointment appointment = Appointment.builder()
                    .id(1L)
                    .appointmentTime(LocalDateTime.of(2024, 1, 1, 10, 0))
                    .status(AppointmentStatus.SCHEDULED)
                    .build();
            Mockito.when(appointmentService.getMemberAllAppointment(anyLong())).thenReturn(List.of(appointment));

            // Act & Assert
            mockMvc.perform(get("/appointments")
                            .param("memberId", "1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].appointmentTime", is("2024-01-01T10:00:00")));
        }

        @Test
        @DisplayName("Should return no content when no appointments are found")
        void getAppointments_ShouldReturnNoContent() throws Exception {
            // Arrange
            Mockito.when(appointmentService.getMemberAllAppointment(anyLong())).thenReturn(List.of());

            // Act & Assert
            mockMvc.perform(get("/appointments")
                            .param("memberId", "1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("Get Appointment By ID Tests")
    class GetAppointmentByIdTests {

        @Test
        @DisplayName("Should return appointment by ID")
        void getAppointment_ShouldReturnAppointment() throws Exception {
            // Arrange
            Appointment appointment = Appointment.builder()
                    .id(1L)
                    .appointmentTime(LocalDateTime.of(2024, 1, 1, 10, 0))
                    .status(AppointmentStatus.SCHEDULED)
                    .build();
            Mockito.when(appointmentService.getAppointmentById(anyLong())).thenReturn(appointment);

            // Act & Assert
            mockMvc.perform(get("/appointments/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.appointmentTime", is("2024-01-01T10:00:00")));
        }

        @Test
        @DisplayName("Should return not found when appointment does not exist")
        void getAppointment_ShouldReturnNotFound() throws Exception {
            // Arrange
            Mockito.when(appointmentService.getAppointmentById(anyLong())).thenThrow(RuntimeException.class);

            // Act & Assert
            mockMvc.perform(get("/appointments/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Check-In Appointment Tests")
    class CheckInTests {

        @Test
        @DisplayName("Should successfully check-in")
        void checkIn_ShouldReturnSuccessMessage() throws Exception {
            // Arrange
            LocalDateTime checkInTime = LocalDateTime.of(2024, 1, 1, 10, 0);
            Appointment appointment = Appointment.builder()
                    .id(1L)
                    .appointmentTime(checkInTime)
                    .status(AppointmentStatus.SCHEDULED) // Initially Scheduled
                    .build();

            // Mocking service call
            Mockito.when(appointmentService.checkIn(anyLong(), any(LocalDateTime.class)))
                    .thenAnswer(invocation -> {
                        // Set the check-in time to simulate the behavior of checkIn()
                        appointment.setCheckInTime(checkInTime);
                        appointment.setStatus(AppointmentStatus.CHECKED_IN);
                        return Optional.of(appointment);
                    });

            // Act & Assert
            mockMvc.perform(post("/appointments/check-in/1")
                            .content("{\"checkInTime\": \"2024-01-01T10:00:00\"}")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", is("Check-in successful")))
                    .andExpect(jsonPath("$.appointment.id", is(1)))
                    .andExpect(jsonPath("$.appointment.checkInTime", is("2024-01-01T10:00:00")));
        }


        @Test
        @DisplayName("Should return error message for conflicting check-in")
        void checkIn_ShouldReturnErrorMessage() throws Exception {
            // Arrange
            LocalDateTime checkInTime = LocalDateTime.of(2024, 1, 1, 10, 0);
            Mockito.when(appointmentService.checkIn(anyLong(), any(LocalDateTime.class)))
                    .thenThrow(new RuntimeException("This time slot is already booked by another member"));

            // Act & Assert
            mockMvc.perform(post("/appointments/check-in/1")
                            .content("{\"checkInTime\": \"2024-01-01T10:00:00\"}")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error", is("This time slot is already booked by another member")));

        }
    }

    @Nested
    @DisplayName("Schedule Appointment Tests")
    class ScheduleAppointmentTests {

        @Test
        @DisplayName("Should successfully schedule an appointment")
        void scheduleAppointment_ShouldReturnSuccessMessage() throws Exception {
            // Arrange
            LocalDateTime appointmentTime = LocalDateTime.of(2024, 1, 1, 10, 0);
            Appointment appointment = Appointment.builder()
                    .id(1L)
                    .appointmentTime(appointmentTime)
                    .status(AppointmentStatus.SCHEDULED)
                    .build();

            Mockito.when(appointmentService.scheduleAppointment(anyLong(), any(LocalDateTime.class)))
                    .thenReturn(Optional.of(appointment));

            // Act & Assert
            mockMvc.perform(post("/appointments/1/book")
                            .param("appointmentTime", "2024-01-01T10:00:00")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", is("schedule is successful")))
                    .andExpect(jsonPath("$.appointment.id", is(1)))
                    .andExpect(jsonPath("$.appointmentTime", is("2024-01-01T10:00:00")));
        }

        @Test
        @DisplayName("Should return error message for invalid schedule")
        void scheduleAppointment_ShouldReturnErrorMessage() throws Exception {
            // Arrange
            LocalDateTime appointmentTime = LocalDateTime.of(2024, 1, 1, 7, 0);
            Mockito.when(appointmentService.scheduleAppointment(anyLong(), any(LocalDateTime.class)))
                    .thenThrow(new RuntimeException("Appointment can only be scheduled during business hours"));

            // Act & Assert
            mockMvc.perform(post("/appointments/1/book")
                            .param("appointmentTime", "2024-01-01T07:00:00")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error", is("Appointment can only be scheduled during business hours")));
        }
    }
}
