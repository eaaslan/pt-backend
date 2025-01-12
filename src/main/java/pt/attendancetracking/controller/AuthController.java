package pt.attendancetracking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pt.attendancetracking.dto.LoginRequest;
import pt.attendancetracking.dto.LoginResponse;
import pt.attendancetracking.model.User;
import pt.attendancetracking.service.MemberService;
import pt.attendancetracking.service.UserService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://127.0.0.1:5500",
        "https://pt-frontend-gtju.vercel.app"
}, allowCredentials = "true")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.username());

        try {
            // Attempt authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user details using UserService
            User user = userService.getUserByUsername(loginRequest.username());

            // Create response using the DTO
            LoginResponse response = LoginResponse.fromUser(user);

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            log.error("Bad credentials for user: {}", loginRequest.username());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid username or password"
            ));
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", loginRequest.username(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Authentication failed: " + e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Unexpected error during login for user: {}", loginRequest.username(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "An unexpected error occurred: " + e.getMessage()
            ));
        }
    }

//    @PostMapping("/register")
//    public ResponseEntity<?> registerMember(@Valid @RequestBody CreateMemberRequest createMemberRequest) {
//        try {
//            log.debug("Attempting to register new member with username: {}", createMemberRequest.username());
//            MemberResponse member = memberService.createMember(createMemberRequest);
//            log.info("Successfully registered new member with id: {}", member.id());
//
//            return ResponseEntity.ok(Map.of(
//                    "message", "registration successful",
//                    "userId", member.id()
//            ));
//        } catch (RuntimeException e) {
//            log.error("Registration failed for username: {}", createMemberRequest.username(), e);
//            return ResponseEntity.badRequest().body(Map.of(
//                    "error", e.getMessage()
//            ));
//        }
//    }
}