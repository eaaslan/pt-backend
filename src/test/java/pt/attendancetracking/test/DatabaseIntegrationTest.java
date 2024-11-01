package pt.attendancetracking.test;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pt.attendancetracking.model.Appointment;
import pt.attendancetracking.model.Member;
import pt.attendancetracking.repository.AppointmentRepository;
import pt.attendancetracking.repository.MemberRepository;
import pt.attendancetracking.repository.PackageRepository;
import pt.attendancetracking.service.AppointmentService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test") // This will use application-test.properties
public class DatabaseIntegrationTest {

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private PackageRepository packageRepository;

    private static final Logger log = LoggerFactory.getLogger(DatabaseIntegrationTest.class);


    @Test
    public void testCheckIn(){
        LocalDateTime checkInTime = LocalDateTime.of(2025, 11, 1, 9, 13);
        Member member = memberRepository.findById(1L).orElse(null);
        Optional<Appointment> appointment = appointmentService.checkIn(member.getId(), checkInTime);
    }
    @Test
    public void testGetAppointment() {
        // Assuming an Appointment with ID 1L exists in the database
        Appointment appointment = appointmentRepository.findById(1L).orElse(null);
        assertNotNull(appointment, "Appointment with ID 1 should exist");
        System.out.println("Found appointment: " + appointment.getAppointmentTime());
    }
    @Test
    public void checkScheduledAppointmentWithTimeAndMember(){
        LocalDateTime appointmentTime = LocalDateTime.of(2025, 11, 1, 15, 0);
        Member member = memberRepository.findById(2L).orElse(null);
        assert member != null;
        Optional<Appointment> appointment = appointmentRepository.findAppointmentByMemberAndTimeScheduledStatus(member.getId(), appointmentTime);
        System.out.println(appointment.get().getAppointmentTime());
        System.out.println(member.getName());
        assertTrue(appointment.isPresent());
    }
    @Test
    public void checkAppointmentWithTimeAndMember(){
        LocalDateTime appointmentTime = LocalDateTime.of(2025, 11, 1, 15, 0);
        Member member = memberRepository.findById(2L).orElse(null);
        Optional<Appointment> appointment = appointmentRepository.findAppointmentByMemberAndTime(member.getId(), appointmentTime);
        System.out.println(appointment.get().getAppointmentTime());
        System.out.println(member.getName());
        assertTrue(appointment.isPresent());
    }
    @Test
    public void testBookAppointmentAvailableTime() {

        LocalDateTime appointmentTime = LocalDateTime.of(2025, 11, 1, 15, 0);
        Member member = memberRepository.findById(1L).orElse(null);
       Optional<Appointment> appointment = appointmentService.scheduleAppointment(member.getId(), appointmentTime);
       assertTrue(appointment.isPresent());
    }
    @Test
    public void testCheckInAlreadyCheckedInAppointment() {

        LocalDateTime appointmentTime = LocalDateTime.of(2025, 11, 1, 11, 0);
        Member member = memberRepository.findById(3L).orElse(null);
        Optional<Appointment> appointment = appointmentRepository.findAppointmentByMemberAndTimeScheduledStatus(member.getId(), appointmentTime);
        System.out.println(appointment.get().getAppointmentTime());
        System.out.println(appointment.get().getStatus());
        assertTrue(appointment.isPresent());
    }
    @Test
    public void testGetAllAppointments() {
        LocalDateTime appointmentTime = LocalDateTime.of(2024, 11, 1, 15, 0);
        List<LocalDateTime> appointmentDates = appointmentRepository.appointmentsByDateTime(appointmentTime);
        assertNotNull(appointmentDates, "Appointment with ID 1 should exist");
        System.out.println(appointmentDates);

    }
    @Test
    public void testAppointmentShouldExist(){

        LocalDateTime appointmentTime = LocalDateTime.of(2024, 12, 1, 15, 0);
        log.info("Testing appointment time: {}", appointmentTime);

        boolean exists = appointmentRepository.existsByAppointmentTime(appointmentTime);
        log.info("Appointment exists check result: {}", exists);

        assertTrue(exists);
    }
    @Test
    public void testAppointmentNotExist(){

        LocalDateTime appointmentTime = LocalDateTime.of(2024, 12, 1, 15, 0);
        log.info("Testing appointment time: {}", appointmentTime);

        boolean exists = appointmentRepository.existsByAppointmentTime(appointmentTime);
        log.info("Appointment exists check result: {}", exists);

        assertFalse(exists);
    }

    @Test
    void shouldCheckInSuccessfully() {
        // 12:00'de randevusu var
        // 11:50'de check-in yapıyor -> Başarılı
    }

    @Test
    void shouldCheckInSuccessfullyWithExactTime() {
        // 12:00'de randevusu var
        // 12:00'de check-in yapıyor -> Başarılı
    }

    @Test
    void shouldCheckInSuccessfullyWithinTimeSlot() {
        // 12:00'de randevusu var
        // 12:20'de check-in yapıyor -> Başarılı
    }

    @Test
    void shouldFailCheckInOutsideBusinessHours() {
        // 08:00'de check-in deniyor -> Hata (mesai saati dışı)
    }

    @Test
    void shouldFailCheckInTooEarly() {
        // 12:00'de randevusu var
        // 11:20'de check-in deniyor -> Hata (çok erken)
    }

    @Test
    void shouldFailCheckInTooLate() {
        // 12:00'de randevusu var
        // 13:10'da check-in deniyor -> Hata (çok geç)
    }

    @Test
    void shouldFailCheckInWhenNoAppointment() {
        // Hiç randevusu yok
        // Check-in deniyor -> Hata
    }

    @Test
    void shouldFailCheckInWhenAlreadyCheckedIn() {
        // 12:00'de randevusu var
        // İlk check-in başarılı
        // İkinci kez check-in deniyor -> Hata
    }

    @Test
    void shouldFailCheckInWhenAppointmentCancelled() {
        // 12:00'de randevusu var ama iptal edilmiş
        // Check-in deniyor -> Hata
    }

    @Test
    void shouldFailCheckInWhenSlotTakenByOtherMember() {
        // Member1'in 12:00'de randevusu var
        // Member2'nin 12:00'de randevusu var
        // Member2 önce check-in yapıyor
        // Member1 check-in deniyor -> Hata
    }

    @Test
    void shouldFailCheckInWhenOtherMemberAlreadyCheckedIn() {
        // Member1'in 12:00'de randevusu var ve check-in yapmış
        // Member2'nin 12:00'de randevusu var
        // Member2 check-in deniyor -> Hata
    }
}