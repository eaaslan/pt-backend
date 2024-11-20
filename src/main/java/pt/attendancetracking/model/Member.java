package pt.attendancetracking.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

//todo change member builder
@Entity
@Table(name = "members")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"activePackage", "appointments"})
@JsonIgnoreProperties({"member"})
@ToString(exclude = {"activePackage", "appointments"})
public class Member extends User {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_pt_id")
    private PersonalTrainer assignedPt;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Package activePackage;  // This now correctly maps to Package.member

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Appointment> appointments = new ArrayList<>();

    public Member() {
        super();
    }

    public static MemberBuilder builder() {
        return new MemberBuilder();
    }

    public void addAppointment(Appointment appointment) {
        if (appointments == null) {
            appointments = new ArrayList<>();
        }
        appointments.add(appointment);
        appointment.setMember(this);
    }

    public static class MemberBuilder {
        private String username;
        private String password;
        private String name;
        private String email;
        private PersonalTrainer assignedPt;

        public MemberBuilder username(String username) {
            this.username = username;
            return this;
        }

        public MemberBuilder password(String password) {
            this.password = password;
            return this;
        }

        public MemberBuilder name(String name) {
            this.name = name;
            return this;
        }

        public MemberBuilder email(String email) {
            this.email = email;
            return this;
        }

        public MemberBuilder assignedPt(PersonalTrainer assignedPt) {
            this.assignedPt = assignedPt;
            return this;
        }

        public Member build() {
            Member member = new Member();
            member.setUsername(username);
            member.setPassword(password);
            member.setName(name);
            member.setEmail(email);
            member.setRole(UserRole.ROLE_MEMBER);
            member.setAssignedPt(assignedPt);
            return member;
        }
    }
}