package pt.attendancetracking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.attendancetracking.dto.CreateMemberRequest;
import pt.attendancetracking.dto.MemberResponse;
import pt.attendancetracking.model.Member;
import pt.attendancetracking.model.Package;
import pt.attendancetracking.model.PersonalTrainer;
import pt.attendancetracking.model.UserRole;
import pt.attendancetracking.repository.MemberRepository;
import pt.attendancetracking.repository.PersonalTrainerRepository;
import pt.attendancetracking.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final PersonalTrainerRepository ptRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberResponse createMember(CreateMemberRequest request) {

        validateNewUser(request.username(), request.email());

        Member member = Member.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .email(request.email())
                .build();

        Member savedMember = memberRepository.save(member);
        return mapToMemberResponse(savedMember);
    }

    @Transactional
    public MemberResponse createMemberWithPt(CreateMemberRequest request, PersonalTrainer pt) {
        validateNewUser(request.username(), request.email());

        Member member = Member.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .email(request.email())
                .build();

        // Set the role explicitly
        member.setRole(UserRole.ROLE_MEMBER);

        // Assign the PT
        member.setAssignedPt(pt);

        Member savedMember = memberRepository.save(member);
        return mapToMemberResponse(savedMember);
    }

    private void validateNewUser(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
    }

    private MemberResponse mapToMemberResponse(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getUsername(),
                member.getEmail(),
                member.getName()
        );
    }

    // Existing methods remain the same
    public List<Member> getAllMembers() {
        return memberRepository.findAllMembers();
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

    public Member getMemberByUserName(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Member not found with username: " + username));
    }
}