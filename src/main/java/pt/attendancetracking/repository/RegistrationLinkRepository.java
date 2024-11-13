package pt.attendancetracking.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import pt.attendancetracking.model.RegistrationLink;

import java.util.Optional;

public interface RegistrationLinkRepository extends JpaRepository<RegistrationLink, Long> {
    Optional<RegistrationLink> findByTokenAndUsedFalse(String token);
}