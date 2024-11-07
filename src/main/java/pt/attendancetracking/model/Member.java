package pt.attendancetracking.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;


@Table(name = "members")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"activePackage.member", "appointments.member"})
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Package activePackage;

    @ToString.Exclude
    @JsonIgnoreProperties
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();

    public void setActivePackage(Package activePackage) {
        this.activePackage = activePackage;
        if (activePackage != null) {
            activePackage.setMember(this);
        }
    }

    public void addAppointments(Appointment appointment){
        if(appointments==null){
            appointments=new ArrayList<>();
        }
        appointments.add(appointment);
        appointment.setMember(this);
    }

    public void removeAppointment(Appointment appointment){
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