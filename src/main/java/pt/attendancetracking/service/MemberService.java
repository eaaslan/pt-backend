package pt.attendancetracking.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.attendancetracking.dto.CreateMemberRequest;
import pt.attendancetracking.model.Member;
import pt.attendancetracking.model.UserRole;
import pt.attendancetracking.repository.MemberRepository;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class MemberService {
    public final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Member> getAllMember(){
        return memberRepository.findAll();
    }
    public Member getMemberById(Long id){
        return memberRepository.findById(id).orElseThrow();
    }
    public Member getMemberByUserName(@NotBlank(message = "username cannot be blank") String username) {
            return memberRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("Can not find username :"+username));
    }

    public Member createMember(@Valid CreateMemberRequest createMemberRequest) {
        if (memberRepository.existsByUsername(createMemberRequest.username())) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        if (memberRepository.existsByEmail(createMemberRequest.email())) {
            throw new RuntimeException("Email already exists");
        }
        Member member= Member.builder()
                .email(createMemberRequest.email())
                .name(createMemberRequest.name())
                .password(passwordEncoder.encode(createMemberRequest.password()))
                .role(UserRole.ROLE_MEMBER)
                .build();

        return  memberRepository.save(member);
    }
}