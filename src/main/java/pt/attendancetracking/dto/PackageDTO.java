package pt.attendancetracking.dto;

import lombok.Builder;
import lombok.Data;
import pt.attendancetracking.model.PackageStatus;

@Data
@Builder
public class PackageDTO {
    private Long id;
    private Integer totalSessions;
    private Integer usedSessions;
    private Integer remainingSessions;
    private Integer remainingCancellations;
    private PackageStatus status;
}