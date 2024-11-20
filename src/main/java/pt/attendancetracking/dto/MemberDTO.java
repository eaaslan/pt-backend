package pt.attendancetracking.dto;


import lombok.Builder;
import lombok.Data;
import pt.attendancetracking.model.UserRole;

@Data
@Builder
public class MemberDTO {
    private Long id;
    private String username;
    private String name;
    private String email;
    private UserRole role;
    private PackageDTO packageInfo;
    private PtBasicInfo assignedPtInfo;
}