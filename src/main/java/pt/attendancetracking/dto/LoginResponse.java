package pt.attendancetracking.dto;

import lombok.Builder;
import lombok.Data;
import pt.attendancetracking.model.Member;
import pt.attendancetracking.model.PersonalTrainer;
import pt.attendancetracking.model.User;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class LoginResponse {
    private String message;
    private Map<String, Object> user;

    public static LoginResponse fromUser(User user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("username", user.getUsername());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("role", user.getRole());

        // Add member-specific data
        if (user instanceof Member member) {
            userData.put("assignedPtId",
                    member.getAssignedPt() != null ? member.getAssignedPt().getId() : null);
        }

        // Add PT-specific data if needed
        if (user instanceof PersonalTrainer) {
            // Add any PT specific fields here
        }

        return LoginResponse.builder()
                .message("Login successful")
                .user(userData)
                .build();
    }
}