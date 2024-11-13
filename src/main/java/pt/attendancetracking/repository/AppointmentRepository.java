package pt.attendancetracking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pt.attendancetracking.model.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a WHERE a.member.id = :memberId ORDER BY a.appointmentTime DESC")
    List<Appointment> findAppointmentsByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT DISTINCT a FROM Appointment a " +
            "LEFT JOIN FETCH a.member m " +
            "LEFT JOIN FETCH a.personalTrainer pt " +
            "WHERE m.username = :username " +
            "ORDER BY a.appointmentTime DESC")
    List<Appointment> findAppointmentsByMemberUsername(@Param("username") String username);

    @Query("SELECT a.appointmentTime FROM Appointment a")
    List<LocalDateTime> appointmentsByDateTime(LocalDateTime dateTime);

    @Query("SELECT appointment FROM Appointment appointment WHERE appointment.member.id = :memberId AND appointment.appointmentTime = :appointmentTime")
    Optional<Appointment> findAppointmentByMemberAndTime(@Param("memberId") Long memberId, @Param("appointmentTime") LocalDateTime appointmentTime);

    @Query("select appointment from Appointment " +
            " appointment where appointment.member.id=:memberId " +
            " and appointment.appointmentTime=  :appointmentTime " +
            " and appointment.status= 'SCHEDULED' ")
    Optional<Appointment> findAppointmentByMemberAndTimeScheduledStatus(@Param("memberId") Long memberId, @Param("appointmentTime") LocalDateTime appointmentTime);

    boolean existsByAppointmentTime(LocalDateTime appointmentTime);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
            "WHERE a.appointmentTime = :appointmentTime " +
            "AND a.status = pt.attendancetracking.model.AppointmentStatus.CHECKED_IN")
    boolean existsCheckedInAppointmentForTime(@Param("appointmentTime") LocalDateTime appointmentTime);

    @Query("SELECT a FROM Appointment a WHERE a.personalTrainer.id = :ptId")
    List<Appointment> findAppointmentsByPtId(@Param("ptId") Long ptId);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
            "WHERE a.personalTrainer.id = :ptId " +
            "AND a.appointmentTime = :appointmentTime " +
            "AND a.status != 'CANCELLED'")
    boolean isPtBookedForTimeSlot(@Param("ptId") Long ptId,
                                  @Param("appointmentTime") LocalDateTime appointmentTime);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
            "WHERE a.appointmentTime = :appointmentTime " +
            "AND a.member.id <> :memberId")
    boolean existsAppointmentForTimeByOtherMember(
            @Param("appointmentTime") LocalDateTime appointmentTime,
            @Param("memberId") Long memberId
    );

//    @Query("SELECT a FROM Appointment a WHERE a.member.id = :memberId")
//    List<Appointment> findAppointmentsByMemberId(@Param("memberId") Long memberId);

}