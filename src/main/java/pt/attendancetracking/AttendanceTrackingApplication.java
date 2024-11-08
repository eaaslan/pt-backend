package pt.attendancetracking;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import pt.attendancetracking.model.Member;
import pt.attendancetracking.model.UserRole;
import pt.attendancetracking.repository.MemberRepository;

@SpringBootApplication
public class AttendanceTrackingApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(AttendanceTrackingApplication.class);

        // Set prod profile for Heroku
//        if (System.getenv("DYNO") != null) {
//            app.setAdditionalProfiles("prod");
//        }

        app.run(args);
    }

    @Bean
    CommandLineRunner initUsers(MemberRepository memberRepository, PasswordEncoder encoder) {
        return args -> {
            // Create admin if not exists
            if (!memberRepository.existsByUsername("admin")) {
                Member admin = Member.builder()
                        .username("admin")
                        .password(encoder.encode("admin123"))
                        .name("Admin User")
                        .email("admin@gym.com")
                        .role(UserRole.ROLE_ADMIN)
                        .build();
                memberRepository.save(admin);
                System.out.println("Admin user created: admin/admin123");
            }

            // Create PT if not exists
            if (!memberRepository.existsByUsername("pt")) {
                Member pt = Member.builder()
                        .username("pt")
                        .password(encoder.encode("pt123"))
                        .name("PT User")
                        .email("pt@gym.com")
                        .role(UserRole.ROLE_PT)
                        .build();
                memberRepository.save(pt);
                System.out.println("PT user created: pt/pt123");
            }

            // Create member if not exists
            if (!memberRepository.existsByUsername("member")) {
                Member member = Member.builder()
                        .username("member")
                        .password(encoder.encode("member123"))
                        .name("Test Member")
                        .email("member@gym.com")
                        .role(UserRole.ROLE_MEMBER)
                        .build();
                memberRepository.save(member);
                System.out.println("Member user created: member/member123");
            }
        };
    }
}