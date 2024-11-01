package pt.attendancetracking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.attendancetracking.model.Appointment;
import pt.attendancetracking.model.AppointmentStatus;
import pt.attendancetracking.model.Member;
import pt.attendancetracking.model.PackageStatus;
import pt.attendancetracking.repository.AppointmentRepository;
import pt.attendancetracking.repository.MemberRepository;
import pt.attendancetracking.util.TimeSlotUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final MemberRepository memberRepository;

    @Transactional
   public Optional<Appointment> scheduleAppointment(Long memberId, LocalDateTime appointmentTime){

      if( appointmentRepository.existsByAppointmentTime(appointmentTime)) {
          return Optional.empty();
      }
      Member member = memberRepository.findById(memberId).orElseThrow();
      Appointment appointment = Appointment.builder()
              .appointmentTime(appointmentTime)
              .status(AppointmentStatus.SCHEDULED)
              .member(member)
                .build();
       member.getAppointments().add(appointment);
      appointmentRepository.save(appointment);

      return Optional.of(appointment);
    }

    @Transactional
    public Optional<Appointment> TestScheduleAppointment(Long memberId, LocalDateTime appointmentTime){

        if( appointmentRepository.existsByAppointmentTime(appointmentTime)) {
            System.out.println("There is already appointment!!");
            return Optional.empty();
        }
        Member member = memberRepository.findById(memberId).orElseThrow();
        Appointment appointment = Appointment.builder()
                .appointmentTime(appointmentTime)
                .status(AppointmentStatus.CHECKED_IN)
                .member(member)
                .build();
        member.getAppointments().add(appointment);
        appointmentRepository.save(appointment);

        return Optional.of(appointment);
    }

    //todo we just specify our check in time but we use time.now in real test
    @Transactional
    public Optional<Appointment> checkIn(Long memberId, LocalDateTime checkInTime) {
        if (!TimeSlotUtil.isValidBusinessHour(checkInTime)) {
            throw new RuntimeException("Check-in is only allowed during business hours (9 AM - 5 PM)");
        }

        // Check-in zamanını en yakın saate yuvarla
        LocalDateTime roundedTime = TimeSlotUtil.roundToNearestHour(checkInTime);

        // 1. O zaman diliminde başka bir üyenin randevusu var mı kontrol et
        if (appointmentRepository.existsAppointmentForTimeByOtherMember(roundedTime, memberId)) {
            throw new RuntimeException("This time slot is already booked by another member");
        }

        // 2. O zaman diliminde zaten check-in yapılmış bir randevu var mı kontrol et
        if (appointmentRepository.existsCheckedInAppointmentForTime(roundedTime)) {
            throw new RuntimeException("There is already a checked-in appointment for this time slot");
        }

        // 3. Üyenin SCHEDULED durumunda randevusu var mı kontrol et
        Appointment appointment = appointmentRepository
                .findAppointmentByMemberAndTimeScheduledStatus(memberId, roundedTime)
                .orElseThrow(() -> new RuntimeException("No scheduled appointment found for rounded time: " + roundedTime));

        // 4. Randevuyu güncelle
        appointment.setCheckInTime(checkInTime);
        appointment.setStatus(AppointmentStatus.CHECKED_IN);
        appointmentRepository.save(appointment);

        return Optional.of(appointment);
    }

}