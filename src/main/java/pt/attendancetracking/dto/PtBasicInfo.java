package pt.attendancetracking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PtBasicInfo {
    private Long id;
    private String name;
    private String email;
}