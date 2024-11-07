package pt.attendancetracking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotBlank(message = "username cannot be blank")
        String username,

        @NotBlank(message = "Password can not be blank")
        @Size(min = 6,max = 20,message = "Password must be between 6 and 20 characters")
        String password
){}
