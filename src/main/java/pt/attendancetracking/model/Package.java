// Package.java
package pt.attendancetracking.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"member"})
@EqualsAndHashCode(exclude = {"member"})  // Exclude member from hashCode calculation
@ToString(exclude = {"member"})
public class Package {
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

    public Package(int totalSessions) {
        this.totalSessions = totalSessions;
        this.usedSessions = 0;
        this.remainingSessions = totalSessions;
        this.remainingCancellations = calculateInitialCancellations(totalSessions);
        this.status = PackageStatus.ACTIVE;
    }


    /**
     * Records a used session and updates package status
     *
     * @return true if session was successfully recorded, false if no sessions remaining
     * @throws IllegalStateException if package is expired
     */
    public boolean useSession() {
        if (status == PackageStatus.EXPIRED) {
            throw new IllegalStateException("Cannot use sessions from an expired package");
        }

        if (remainingSessions <= 0) {
            return false;
        }

        usedSessions++;
        remainingSessions--;

        if (remainingSessions == 0) {
            status = PackageStatus.EXPIRED;
        }

        return true;
    }

    /**
     * Records a cancelled session
     *
     * @return true if cancellation was recorded, false if no cancellations remaining
     * @throws IllegalStateException if package is expired
     */
    public boolean recordCancellation() {
        if (status == PackageStatus.EXPIRED) {
            throw new IllegalStateException("Cannot cancel sessions from an expired package");
        }

        if (remainingCancellations <= 0) {
            return false;
        }

        remainingCancellations--;
        return true;
    }

    /**
     * Refunds a used session back to the package
     *
     * @throws IllegalStateException if no sessions have been used
     */
    public void refundSession() {
        if (usedSessions <= 0) {
            throw new IllegalStateException("No sessions to refund");
        }

        usedSessions--;
        remainingSessions++;

        if (status == PackageStatus.EXPIRED && remainingSessions > 0) {
            status = PackageStatus.ACTIVE;
        }
    }

    /**
     * Adds bonus sessions to the package
     *
     * @param bonusSessions number of bonus sessions to add
     * @throws IllegalArgumentException if bonusSessions is negative
     */
    public void addBonusSessions(int bonusSessions) {
        if (bonusSessions < 0) {
            throw new IllegalArgumentException("Cannot add negative number of sessions");
        }

        totalSessions += bonusSessions;
        remainingSessions += bonusSessions;

        if (status == PackageStatus.EXPIRED && remainingSessions > 0) {
            status = PackageStatus.ACTIVE;
        }
    }

    /**
     * Calculates initial cancellations based on package size
     * Business rule: 1 cancellation per 4 sessions, rounded down
     */
    private int calculateInitialCancellations(int totalSessions) {
        return totalSessions / 4;
    }

    /**
     * Checks if the package has available sessions
     *
     * @return true if package has remaining sessions and is not expired
     */
    public boolean hasAvailableSessions() {
        return status == PackageStatus.ACTIVE && remainingSessions > 0;
    }

    /**
     * Gets session usage percentage
     *
     * @return percentage of sessions used
     */
    public double getUsagePercentage() {
        return totalSessions == 0 ? 0 :
                ((double) usedSessions / totalSessions) * 100;
    }

    /**
     * Force expires the package regardless of remaining sessions
     */
    public void forceExpire() {
        this.status = PackageStatus.EXPIRED;
    }

    /**
     * Reactivates an expired package if it has remaining sessions
     *
     * @return true if package was reactivated, false if no sessions remaining
     */
    public boolean reactivate() {
        if (remainingSessions > 0) {
            this.status = PackageStatus.ACTIVE;
            return true;
        }
        return false;
    }
}