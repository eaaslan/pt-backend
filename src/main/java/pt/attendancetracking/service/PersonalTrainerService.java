package pt.attendancetracking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.attendancetracking.dto.CreateMemberRequest;
import pt.attendancetracking.model.PersonalTrainer;
import pt.attendancetracking.repository.PersonalTrainerRepository;
import pt.attendancetracking.repository.UserRepository;

import java.util.List;

@Service
public class PersonalTrainerService extends UserService {
    private final PersonalTrainerRepository ptRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PersonalTrainerService(
            UserRepository userRepository,
            PersonalTrainerRepository ptRepository,
            PasswordEncoder passwordEncoder) {
        super(userRepository);
        this.ptRepository = ptRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public PersonalTrainer createPersonalTrainer(CreateMemberRequest request) {
        validateNewUser(request.username(), request.email());

        PersonalTrainer pt = PersonalTrainer.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .email(request.email())
                .build();

        return ptRepository.save(pt);
    }

    public List<PersonalTrainer> getAllPersonalTrainers() {
        return ptRepository.findAllWithDetails();
    }

    public PersonalTrainer getPersonalTrainerById(Long id) {
        return ptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personal trainer not found with id: " + id));
    }


    public PersonalTrainer getPersonalTrainerByUsername(String username) {
        return ptRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Personal trainer not found with username: " + username));

    }
}