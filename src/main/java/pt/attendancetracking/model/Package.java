// Package.java
package pt.attendancetracking.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"member"})
public class Package {


    public Package(int totalSessions){
        this.totalSessions = totalSessions;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private Integer totalSessions;
    private Integer usedSessions;
    private Integer remainingSessions;
    private Integer remainingCancellations;

    @Enumerated(EnumType.STRING)
    private PackageStatus status;
}
