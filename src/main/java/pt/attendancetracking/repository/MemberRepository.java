package pt.attendancetracking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pt.attendancetracking.model.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.appointments LEFT JOIN FETCH m.activePackage WHERE m.id = :id")
    Optional<Member> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.appointments LEFT JOIN FETCH m.activePackage")
    List<Member> findAllMembers();

    @Query("SELECT m FROM Member m WHERE m.username = :username")
    Optional<Member> findByUsername(@Param("username") String username);


    boolean existsByUsername(String admin);
}
