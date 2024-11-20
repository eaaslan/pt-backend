package pt.attendancetracking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.attendancetracking.model.PersonalTrainer;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalTrainerRepository extends JpaRepository<PersonalTrainer, Long> {
    @Query("SELECT pt FROM PersonalTrainer pt LEFT JOIN FETCH pt.clients LEFT JOIN FETCH pt.appointments")
    List<PersonalTrainer> findAllWithDetails();

    Optional<PersonalTrainer> findByUsername(String username);
}