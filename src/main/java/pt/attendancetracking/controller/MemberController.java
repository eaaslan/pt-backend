//package pt.attendancetracking.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import pt.attendancetracking.model.Member;
//import pt.attendancetracking.model.Package;
//import pt.attendancetracking.service.MemberService;
//
//@RestController
//@RequestMapping("/api/members")
//@RequiredArgsConstructor
//public class MemberController {
//
//    private final MemberService memberService;
//
//    @PostMapping
//    public ResponseEntity<Member> createMember(@RequestBody Member memberDTO) {
//        return ResponseEntity.ok(memberService.createMember(memberDTO));
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Member> getMember(@PathVariable Long id) {
//        return ResponseEntity.ok(memberService.getMember(id));
//    }
//
//    @PostMapping("/{memberId}/packages")
//    public ResponseEntity<Package> assignPackage(
//            @PathVariable Long memberId,
//            @RequestBody Package packageDTO) {
//        return ResponseEntity.ok(memberService.assignPackage(memberId, packageDTO));
//    }
//}