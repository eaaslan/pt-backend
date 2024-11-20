package pt.attendancetracking.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "personal_trainers")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"clients", "appointments"})
@ToString(exclude = {"clients", "appointments"})
public class PersonalTrainer extends User {
    @OneToMany(mappedBy = "assignedPt")
    private List<Member> clients = new ArrayList<>();

    @OneToMany(mappedBy = "personalTrainer")
    private List<Appointment> appointments = new ArrayList<>();

    public PersonalTrainer() {
        super();
    }

    @Builder
    public PersonalTrainer(String username, String password, String name, String email) {
        super(username, password, name, email, UserRole.ROLE_PT);
        this.clients = new ArrayList<>();
        this.appointments = new ArrayList<>();
    }

    public void addAppointment(Appointment appointment) {
        if (appointments == null) {
            appointments = new ArrayList<>();
        }
        appointments.add(appointment);
        appointment.setPersonalTrainer(this);
    }
}