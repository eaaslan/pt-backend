package pt.attendancetracking.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MemberAppointmentsResponse {
    private String message;
    private List<AppointmentDTO> appointments;
    private int totalAppointments;
}