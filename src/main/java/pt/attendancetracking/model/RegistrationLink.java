package pt.attendancetracking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_id", nullable = false)
    private Member pt;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    private boolean used;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
