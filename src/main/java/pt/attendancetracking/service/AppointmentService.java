package pt.attendancetracking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pt.attendancetracking.dto.AppointmentDTO;
import pt.attendancetracking.model.Package;
import pt.attendancetracking.model.*;
import pt.attendancetracking.repository.AppointmentRepository;
import pt.attendancetracking.repository.MemberRepository;
import pt.attendancetracking.repository.PersonalTrainerRepository;
import pt.attendancetracking.util.TimeUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pt.attendancetracking.util.TimeUtil.getCurrentTimeInUTCPlus3;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final PersonalTrainerRepository ptRepository;


    public List<AppointmentDTO> getCurrentMemberAppointments(String username) {
        List<Appointment> appointments = appointmentRepository
                .findAppointmentsByMemberUsername(username);
        return appointments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<Appointment> checkIn(Long memberId, LocalDateTime checkInTime) {
        LocalDateTime currentTime = checkInTime != null ? checkInTime : getCurrentTimeInUTCPlus3();
        Member member = memberService.getMemberById(memberId);
        if (!TimeUtil.isValidBusinessHour(currentTime)) {
            throw new RuntimeException("Check-in is only allowed during business hours (9 AM - 22 PM)");
        }

        LocalDateTime roundedTime = TimeUtil.roundToNearestHour(currentTime);

        if (appointmentRepository.existsCheckedInAppointmentForTime(roundedTime)) {
            throw new RuntimeException("There is already a checked-in appointment for this time slot");
        }

        Appointment appointment = appointmentRepository
                .findAppointmentByMemberAndTimeScheduledStatus(memberId, roundedTime)
                .orElseThrow(() -> new RuntimeException("No scheduled appointment found"));

        Package activePackage = member.getActivePackage();
        if (activePackage == null) {
            throw new RuntimeException("Member does not have an active package");
        }

        // Attempt to use a session from the package
        try {
            if (!activePackage.useSession()) {
                throw new RuntimeException("No remaining sessions in the package");
            }
        } catch (IllegalStateException e) {
            throw new RuntimeException("Cannot check in: " + e.getMessage());
        }
        appointment.setCheckInTime(currentTime);
        appointment.setStatus(AppointmentStatus.CHECKED_IN);


        return Optional.of(appointmentRepository.save(appointment));
    }

    public List<AppointmentDTO> getPtAppointments(Long ptId) {
        List<Appointment> appointments = appointmentRepository.findAppointmentsByPtId(ptId);
        return appointments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    public List<Appointment> getMemberAllAppointment(Long id) {
        return appointmentRepository.findAppointmentsByMemberId(id);
    }

    @Transactional
    public Optional<Appointment> scheduleAppointment(Long memberId, LocalDateTime appointmentTime) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (member.getAssignedPt() == null) {
            throw new RuntimeException("Member has no assigned PT");
        }

        return scheduleAppointment(memberId, member.getAssignedPt().getId(), appointmentTime);
    }

    @Transactional
    public Optional<Appointment> scheduleAppointment(Long memberId, Long ptId, LocalDateTime appointmentTime) {
        if (!TimeUtil.isValidBusinessHour(appointmentTime)) {
            throw new IllegalArgumentException("Appointment can only be scheduled during business hours");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        PersonalTrainer pt = ptRepository.findById(ptId)
                .orElseThrow(() -> new RuntimeException("PT not found"));

        Appointment appointment = Appointment.builder()
                .appointmentTime(appointmentTime)
                .status(AppointmentStatus.SCHEDULED)
                .member(member)
                .personalTrainer(pt)
                .build();

        member.addAppointment(appointment);
        pt.addAppointment(appointment);

        return Optional.of(appointmentRepository.save(appointment));
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
}