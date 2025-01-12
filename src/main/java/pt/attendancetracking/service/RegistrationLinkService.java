package pt.attendancetracking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.attendancetracking.dto.CreateMemberRequest;
import pt.attendancetracking.dto.MemberResponse;
import pt.attendancetracking.model.PersonalTrainer;
import pt.attendancetracking.model.RegistrationLink;
import pt.attendancetracking.model.UserRole;
import pt.attendancetracking.repository.RegistrationLinkRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationLinkService {
    private final RegistrationLinkRepository registrationLinkRepository;
    private final MemberService memberService;

    public String generateRegistrationLink(PersonalTrainer pt) {
        if (!pt.getRole().equals(UserRole.ROLE_PT)) {
            throw new RuntimeException("Only PTs can generate registration links");
        }
        RegistrationLink link = RegistrationLink.builder()
                .token(UUID.randomUUID().toString())
                .pt(pt)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();

        registrationLinkRepository.save(link);
        return link.getToken();
    }

    public MemberResponse validateAndRegisterMember(String token, CreateMemberRequest request) {
        RegistrationLink link = registrationLinkRepository.findByTokenAndUsedFalse(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired registration link"));

        // Check if the link has expired
        if (link.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Registration link has expired");
        }

        // Get the PT from the link
        PersonalTrainer pt = link.getPt();

        // Create the member with the assigned PT
        MemberResponse member = memberService.createMemberWithPt(request, pt);

        // Mark the link as used
        link.setUsed(true);
        registrationLinkRepository.save(link);


        return member;
    }
}