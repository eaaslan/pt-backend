package pt.attendancetracking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.attendancetracking.model.Appointment;
import pt.attendancetracking.model.AppointmentStatus;
import pt.attendancetracking.model.Member;
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

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElseThrow();
    }

    public List<Appointment> getMemberAllAppointment(Long id) {
        return appointmentRepository.findAppointmentsByMemberId(id);
    }

    @Transactional
    public Optional<Appointment> scheduleAppointment(Long memberId, LocalDateTime appointmentTime) {
        if (!TimeSlotUtil.isValidBusinessHour(appointmentTime)) {
            throw new IllegalArgumentException("Appointment can only be scheduled during business hours (8 AM - 5 PM)");
        }

        if (appointmentRepository.existsByAppointmentTime(appointmentTime)) {
            throw new IllegalStateException("This time slot is already booked");
        }

        Member member = memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("Member not found with id: " + memberId));

        Appointment appointment = Appointment.builder()
                .appointmentTime(appointmentTime)
                .status(AppointmentStatus.SCHEDULED)
                .build();

        member.addAppointments(appointment);

        appointmentRepository.save(appointment);

        return Optional.of(appointment);
    }

    //todo we just specify our check in time but we use time.now in real test
//    @Transactional
//    public Optional<Appointment> checkIn(Long memberId, LocalDateTime checkInTime) {
//        if (!TimeSlotUtil.isValidBusinessHour(checkInTime)) {
//            throw new RuntimeException("Check-in is only allowed during business hours (9 AM - 22 PM)");
//        }
//
//        System.out.println(checkInTime);
//
//        // Check-in zamanını en yakın saate yuvarla
//        LocalDateTime roundedTime = TimeSlotUtil.roundToNearestHour(checkInTime);
//
//        System.out.println(roundedTime);
//
//        // 2. O zaman diliminde zaten check-in yapılmış bir randevu var mı kontrol et
//        if (appointmentRepository.existsCheckedInAppointmentForTime(roundedTime)) {
//            throw new RuntimeException("There is already a checked-in appointment for this time slot");
//        }
//        // 3. Üyenin SCHEDULED durumunda randevusu var mı kontrol et
//        Appointment appointment = appointmentRepository
//                .findAppointmentByMemberAndTimeScheduledStatus(memberId, roundedTime)
//                .orElseThrow(() -> new RuntimeException("No scheduled appointment found for rounded time: " + roundedTime));
//
//        // 4. Randevuyu güncelle
//        appointment.setCheckInTime(checkInTime);
//        appointment.setStatus(AppointmentStatus.CHECKED_IN);
//        appointmentRepository.save(appointment);
//
//        return Optional.of(appointment);
//    }

    @Transactional
    public Optional<Appointment> checkIn(Long memberId, LocalDateTime checkInTime) {
        // If checkInTime is null, use current time in system default timezone
        LocalDateTime currentTime = checkInTime != null ? checkInTime : LocalDateTime.now();

        // Add logging to debug timezone issues
        System.out.println("Current system time: " + currentTime);
        System.out.println("System default timezone: " + java.time.ZoneId.systemDefault());

        if (!TimeSlotUtil.isValidBusinessHour(currentTime)) {
            throw new RuntimeException("Check-in is only allowed during business hours (9 AM - 22 PM)");
        }

        // Round the current time to nearest hour
        LocalDateTime roundedTime = TimeSlotUtil.roundToNearestHour(currentTime);

        System.out.println("Rounded time: " + roundedTime);

        // Check if there's already a checked-in appointment
        if (appointmentRepository.existsCheckedInAppointmentForTime(roundedTime)) {
            throw new RuntimeException("There is already a checked-in appointment for this time slot");
        }

        // Find scheduled appointment
        Appointment appointment = appointmentRepository
                .findAppointmentByMemberAndTimeScheduledStatus(memberId, roundedTime)
                .orElseThrow(() -> new RuntimeException("No scheduled appointment found for rounded time: " + roundedTime));

        // Update appointment
        appointment.setCheckInTime(currentTime);
        appointment.setStatus(AppointmentStatus.CHECKED_IN);
        appointmentRepository.save(appointment);

        return Optional.of(appointment);
    }

}