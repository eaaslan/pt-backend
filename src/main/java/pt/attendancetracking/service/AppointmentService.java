package pt.attendancetracking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pt.attendancetracking.dto.AppointmentDTO;
import pt.attendancetracking.model.Appointment;
import pt.attendancetracking.model.AppointmentStatus;
import pt.attendancetracking.model.Member;
import pt.attendancetracking.model.UserRole;
import pt.attendancetracking.repository.AppointmentRepository;
import pt.attendancetracking.repository.MemberRepository;
import pt.attendancetracking.util.TimeUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pt.attendancetracking.util.TimeUtil.getCurrentTimeInUTCPlus3;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    public boolean isAuthorizedToViewAppointments(String username, Long requestedMemberId) {
        Member requestingMember = memberService.getMemberByUserName(username);

        // Admin can view all appointments
        if (requestingMember.getRole() == UserRole.ROLE_ADMIN) {
            return true;
        }

        // Personal trainers can view their clients' appointments (if you implement this feature)
//        if (requestingMember.getRole() == UserRole.ROLE_PT) {
//            // Add logic here if PTs should be able to view their clients' appointments
//            return false;
//        }

        // Members can only view their own appointments
        return requestingMember.getId().equals(requestedMemberId);
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElseThrow();
    }

    public List<Appointment> getMemberAllAppointment(Long id) {
        return appointmentRepository.findAppointmentsByMemberId(id);
    }


    @Transactional
    public Optional<Appointment> scheduleAppointment(Long memberId, Long ptId, LocalDateTime appointmentTime) {
        if (!TimeUtil.isValidBusinessHour(appointmentTime)) {
            throw new IllegalArgumentException("Appointment can only be scheduled during business hours (8 AM - 5 PM)");
        }

        // Check if PT is available at this time
        if (appointmentRepository.isPtBookedForTimeSlot(ptId, appointmentTime)) {
            throw new IllegalStateException("This PT is already booked for this time slot");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        Member pt = memberRepository.findById(ptId)
                .orElseThrow(() -> new IllegalArgumentException("PT not found with id: " + ptId));

        if (!pt.isPt()) {
            throw new IllegalArgumentException("Selected trainer is not a PT");
        }

        Appointment appointment = Appointment.builder()
                .appointmentTime(appointmentTime)
                .status(AppointmentStatus.SCHEDULED)
                .member(member)
                .personalTrainer(pt)
                .build();

        member.addAppointments(appointment);
        appointmentRepository.save(appointment);

        return Optional.of(appointment);
    }

    @Transactional
    public List<AppointmentDTO> getCurrentMemberAppointments(String username) {

        System.out.println("Fetching appointments for username" + username);

        List<Appointment> appointments = appointmentRepository
                .findAppointmentsByMemberUsername(username);

        return appointments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private AppointmentDTO convertToDTO(Appointment appointment) {
        return AppointmentDTO.builder()
                .id(appointment.getId())
                .appointmentTime(appointment.getAppointmentTime())
                .checkInTime(appointment.getCheckInTime())
                .status(appointment.getStatus())
                .memberId(appointment.getMember().getId())
                .memberName(appointment.getMember().getName())
                .ptId(appointment.getPersonalTrainer().getId())
                .ptName(appointment.getPersonalTrainer().getName())
                .build();
    }

    public ResponseEntity<List<AppointmentDTO>> getPtAppointmentsForMember(Long ptId) {
        // Fetch appointments by PT ID
        List<Appointment> appointments = appointmentRepository.findAppointmentsByPtId(ptId);

        // Map appointments to AppointmentDTO
        List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(appointment -> AppointmentDTO.builder()
                        .id(appointment.getId())
                        .appointmentTime(appointment.getAppointmentTime())
                        .checkInTime(appointment.getCheckInTime())
                        .status(appointment.getStatus())
                        .memberId(appointment.getMember().getId())
                        .memberName(appointment.getMember().getName())
                        .ptId(appointment.getPersonalTrainer().getId())
                        .ptName(appointment.getPersonalTrainer().getName())
                        .build())
                .toList();

        return ResponseEntity.ok(appointmentDTOs);
    }


    @Transactional
    public Optional<Appointment> scheduleAppointment(Long memberId, LocalDateTime appointmentTime) {
        if (!TimeUtil.isValidBusinessHour(appointmentTime)) {
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
        LocalDateTime currentTime = checkInTime != null ? checkInTime : getCurrentTimeInUTCPlus3();

        // Add logging to debug timezone issues
        System.out.println("Current time: " + currentTime);

        if (!TimeUtil.isValidBusinessHour(currentTime)) {
            throw new RuntimeException("Check-in is only allowed during business hours (9 AM - 22 PM)");
        }

        // Round the current time to nearest hour
        LocalDateTime roundedTime = TimeUtil.roundToNearestHour(currentTime);


        System.out.println("Rounded time: " + roundedTime);

        // Check if there's already a checked-in appointment
        if (appointmentRepository.existsCheckedInAppointmentForTime(roundedTime)) {
            throw new RuntimeException("There is already a checked-in appointment for this time slot");
        }

        // Find scheduled appointment
        Appointment appointment = appointmentRepository
                .findAppointmentByMemberAndTimeScheduledStatus(memberId, roundedTime)
                .orElseThrow(() -> new RuntimeException("No scheduled appointment found for : " + roundedTime));

        // Update appointment
        appointment.setCheckInTime(currentTime);
        appointment.setStatus(AppointmentStatus.CHECKED_IN);
        appointmentRepository.save(appointment);

        return Optional.of(appointment);
    }

}