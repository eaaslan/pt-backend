package pt.attendancetracking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pt.attendancetracking.model.Member;

public interface MemberRepository extends JpaRepository<Member,Long> {

}
