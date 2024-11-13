package pt.attendancetracking.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.attendancetracking.dto.CreateMemberRequest;
import pt.attendancetracking.dto.MemberResponse;
import pt.attendancetracking.model.Member;
import pt.attendancetracking.model.Package;
import pt.attendancetracking.model.UserRole;
import pt.attendancetracking.repository.MemberRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MemberService {
    public final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Member> getAllMember() {
        return memberRepository.findAllWithDetails();
    }

    public Member getMemberById(Long id) {
        return memberRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
    }

    public Package getPackageByUsername(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (member.getActivePackage() == null) {
            throw new RuntimeException("No active package found for this member");
        }

        return member.getActivePackage();
    }


    public Member getMemberByUserName(@NotBlank(message = "username cannot be blank") String username) {
        return memberRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Can not find username :" + username));
    }

    public MemberResponse createMember(@Valid CreateMemberRequest createMemberRequest) {
        if (memberRepository.existsByUsername(createMemberRequest.username())) {
            throw new RuntimeException("Username already exists");
        }
        // Check if email already exists
        if (memberRepository.existsByEmail(createMemberRequest.email())) {
            throw new RuntimeException("Email already exists");
        }
        // Check if email already exists
        if (memberRepository.existsByUsername(createMemberRequest.username())) {
            throw new RuntimeException("Username already exists");
        }
        Member member = Member.builder()
                .email(createMemberRequest.email())
                .username(createMemberRequest.username())
                .name(createMemberRequest.name())
                .password(passwordEncoder.encode(createMemberRequest.password()))
                .role(UserRole.ROLE_MEMBER)
                .build();

        Member savedMember = memberRepository.save(member);

        return mapToMemberResponse(savedMember);
    }

    private MemberResponse mapToMemberResponse(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getUsername(),
                member.getEmail(),
                member.getName()
        );
    }
}