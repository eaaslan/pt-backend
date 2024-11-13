package pt.attendancetracking.dto;

import lombok.Builder;
import lombok.Data;
import pt.attendancetracking.model.AppointmentStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class AppointmentDTO {
    private Long id;
    private LocalDateTime appointmentTime;
    private LocalDateTime checkInTime;
    private AppointmentStatus status;
    private Long memberId;
    private String memberName;
    private Long ptId;
    private String ptName;
}