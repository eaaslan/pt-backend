//package pt.attendancetracking.service;
//
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import pt.attendancetracking.model.Member;
//import pt.attendancetracking.model.Package;
//import pt.attendancetracking.model.PackageStatus;
//import pt.attendancetracking.repository.MemberRepository;
//import pt.attendancetracking.repository.PackageRepository;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class MemberService {
//
//    private final MemberRepository memberRepository;
//    private final PackageRepository packageRepository;
//
//    public Member createMember(Member member) {
//
//        return memberRepository.save(member);
//    }
//
//    public Member getMember(Long id) {
//        return memberRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Member not found"));
//    }
//
//    public Package assignPackage(Long memberId, Package packageDTO) {
//        Member member = getMember(memberId);
//
//        // Check if member already has an active package
//        if (member.getActivePackage() != null &&
//                member.getActivePackage().getStatus() == PackageStatus.ACTIVE) {
//            throw new RuntimeException("Member already has an active package");
//        }
//        Package newPackage = new Package();
//        newPackage.setMember(member);
//        newPackage.setTotalSessions(packageDTO.getTotalSessions());
//        newPackage.setUsedSessions(0);
//        newPackage.setRemainingSessions(packageDTO.getTotalSessions());
//        newPackage.setRemainingCancellations(3); // Default value
//        newPackage.setStatus(PackageStatus.ACTIVE);
//
//        return packageRepository.save(newPackage);
//    }
//}