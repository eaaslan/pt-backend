package pt.attendancetracking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pt.attendancetracking.dto.*;
import pt.attendancetracking.model.Member;
import pt.attendancetracking.model.Package;
import pt.attendancetracking.service.MemberService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<List<MemberDTO>> getMembers() {
        List<Member> members = memberService.getAllMembers();
        List<MemberDTO> memberDTOs = members.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(memberDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable Long id) {
        try {
            Member member = memberService.getMemberById(id);
            return ResponseEntity.ok(convertToDTO(member));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private MemberDTO convertToDTO(Member member) {
        return MemberDTO.builder()
                .id(member.getId())
                .username(member.getUsername())
                .name(member.getName())
                .email(member.getEmail())
                .role(member.getRole())
                .packageInfo(member.getActivePackage() != null ?
                        convertToPackageDTO(member.getActivePackage()) : null)
                .assignedPtInfo(member.getAssignedPt() != null ?
                        new PtBasicInfo(
                                member.getAssignedPt().getId(),
                                member.getAssignedPt().getName(),
                                member.getAssignedPt().getEmail()
                        ) : null)
                .build();
    }

    private PackageDTO convertToPackageDTO(Package pkg) {
        return PackageDTO.builder()
                .id(pkg.getId())
                .totalSessions(pkg.getTotalSessions())
                .usedSessions(pkg.getUsedSessions())
                .remainingSessions(pkg.getRemainingSessions())
                .remainingCancellations(pkg.getRemainingCancellations())
                .status(pkg.getStatus())
                .build();
    }

    @GetMapping("/my-package")
    public ResponseEntity<?> getMyPackage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        try {
            Package memberPackage = memberService.getPackageByUsername(username);
            return ResponseEntity.ok(memberPackage);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<MemberResponse> createMember(@RequestBody CreateMemberRequest createMemberRequest) {
        MemberResponse memberResponse = memberService.createMember(createMemberRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberResponse);
    }


}