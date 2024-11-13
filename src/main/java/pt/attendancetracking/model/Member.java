package pt.attendancetracking.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "members")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "is_pt")
    private boolean isPt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_pt_id")
    @JsonIgnoreProperties({"clients", "appointments", "assignedPt"})
    private Member assignedPt;

    @OneToMany(mappedBy = "assignedPt")
    @Builder.Default
    private List<Member> clients = new ArrayList<>();

    @Column(name = "name")
    private String name;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false, length = 120)
    private String password;

    @Column(name = "email")
    private String email;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("member")
    private Package activePackage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @ToString.Exclude
    @JsonIgnoreProperties({"member"})
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private List<Appointment> appointments = new ArrayList<>();

    public void setActivePackage(Package activePackage) {
        this.activePackage = activePackage;
        if (activePackage != null) {
            activePackage.setMember(this);
        }
    }

    public void addAppointments(Appointment appointment) {
        if (appointments == null) {
            appointments = new ArrayList<>();
        }
        appointments.add(appointment);
        appointment.setMember(this);
    }

    public void removeAppointment(Appointment appointment) {
        if (appointments != null) {
            appointments.remove(appointment);
            appointment.setMember(null);
        }
    }

    @Override
    public String toString() {
        return String.format("Member(id=%d, name='%s', email='%s')",
                id, name, email);
    }
}