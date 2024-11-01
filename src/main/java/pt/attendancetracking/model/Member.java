package pt.attendancetracking.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;


@Table(name = "member")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Package activePackage;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Appointment> appointments = new ArrayList<>();

    public void setActivePackage(Package activePackage) {
        this.activePackage = activePackage;
        if (activePackage != null) {
            activePackage.setMember(this);
        }
    }
}