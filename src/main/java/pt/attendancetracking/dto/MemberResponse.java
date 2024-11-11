package pt.attendancetracking.dto;

public record MemberResponse(Long id, String username, String email, String name) {
    // The record automatically provides constructor and getters
}