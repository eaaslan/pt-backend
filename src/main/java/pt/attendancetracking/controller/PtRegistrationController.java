package pt.attendancetracking.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pt.attendancetracking.dto.CreateMemberRequest;
import pt.attendancetracking.dto.MemberResponse;
import pt.attendancetracking.model.Member;
import pt.attendancetracking.service.MemberService;
import pt.attendancetracking.service.RegistrationLinkService;

import java.util.Map;

@RestController
@RequestMapping("/api/registration")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://127.0.0.1:5500", "https://pt-frontend-gtju.vercel.app"})
public class PtRegistrationController {
    private final RegistrationLinkService registrationLinkService;
    private final MemberService memberService;


    @PreAuthorize("hasRole('ROLE_PT')")
    @PostMapping("/generate-link")
    public ResponseEntity<?> generateRegistrationLink(@AuthenticationPrincipal UserDetails userDetails) {
        try {

            String username = userDetails.getUsername();
            Member pt = memberService.getMemberByUserName(username);
            String token = registrationLinkService.generateRegistrationLink(pt);
            String registrationLink = "https://your-frontend-url/register?token=" + token;

            return ResponseEntity.ok(Map.of(
                    "message", "Registration link generated successfully",
                    "registrationLink", registrationLink
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/register/{token}")
    public ResponseEntity<?> registerWithToken(
            @PathVariable String token,
            @RequestBody CreateMemberRequest request) {
        try {
            MemberResponse member = registrationLinkService.validateAndRegisterMember(token, request);
            return ResponseEntity.ok(Map.of(
                    "message", "Registration successful",
                    "memberId", member.id()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }
}