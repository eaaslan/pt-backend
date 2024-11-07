package pt.attendancetracking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.attendancetracking.model.Member;
import pt.attendancetracking.repository.MemberRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MemberService {
    public final MemberRepository memberRepository;

    public List<Member> getAllMember(){
        return memberRepository.findAll();
    }

    public Member getMemberById(Long id){
        return memberRepository.findById(id).orElseThrow();
    }


}