package pt.attendancetracking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pt.attendancetracking.dto.CreateMemberRequest;
import pt.attendancetracking.dto.MemberResponse;
import pt.attendancetracking.model.Member;
import pt.attendancetracking.model.Package;
import pt.attendancetracking.service.MemberService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"})
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<List<Member>> getMembers() {
        return ResponseEntity.ok(memberService.getAllMember());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(memberService.getMemberById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
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