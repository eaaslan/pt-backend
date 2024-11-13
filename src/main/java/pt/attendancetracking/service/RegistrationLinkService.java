package pt.attendancetracking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.attendancetracking.dto.CreateMemberRequest;
import pt.attendancetracking.dto.MemberResponse;
import pt.attendancetracking.model.Member;
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

    public String generateRegistrationLink(Member pt) {
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

        if (link.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Registration link has expired");
        }

        MemberResponse member = memberService.createMemberWithPt(request, link.getPt());

        link.setUsed(true);
        registrationLinkRepository.save(link);

        return member;
    }
}