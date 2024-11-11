package pt.attendancetracking.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pt.attendancetracking.model.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT DISTINCT m FROM Member m " +
            "LEFT JOIN FETCH m.appointments " +
            "LEFT JOIN FETCH m.activePackage " +
            "WHERE m.id = :id")
    Optional<Member> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT DISTINCT m FROM Member m " +
            "LEFT JOIN FETCH m.appointments " +
            "LEFT JOIN FETCH m.activePackage")
    List<Member> findAllWithDetails();

    boolean existsByEmail(@NotBlank(message = "Email cannot be blank") @Email(message = "Invalid email format") String email);
}
