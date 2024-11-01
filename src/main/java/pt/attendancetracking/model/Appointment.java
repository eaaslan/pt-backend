package pt.attendancetracking.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Table(name = "appointment")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH
    })
    private Member member;

    @Column(name = "appointment_time")
    private LocalDateTime appointmentTime;

    @Column(name = "checkin_time")
    private LocalDateTime checkInTime;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;
}