package pt.attendancetracking.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pt.attendancetracking.dto.CreateMemberRequest;
import pt.attendancetracking.dto.LoginRequest;
import pt.attendancetracking.dto.MemberResponse;
import pt.attendancetracking.model.Member;
import pt.attendancetracking.service.MemberService;

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
    private final MemberService memberService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        log.info("Login attempt for user: {}", loginRequest.username());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            Member member = memberService.getMemberByUserName(loginRequest.username());
            return ResponseEntity.ok(Map.of(

                    "message", "Login successful",
                    "user", Map.of(
                            "id", member.getId(),
                            "username", member.getUsername(),
                            "name", member.getName(),
                            "email", member.getEmail(),
                            "role", member.getRole()
                    )

            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(

                    "error", "invalid username or password"
            ));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerMember(@Valid @RequestBody CreateMemberRequest createMemberRequest) {
        try {
            MemberResponse member = memberService.createMember(createMemberRequest);

            return ResponseEntity.ok(Map.of(

                    "message", "registration successful",
                    "userId", member.id()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }


}
