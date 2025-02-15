package pt.attendancetracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AttendanceTrackingApplication {


    public static void main(String[] args) {
        SpringApplication.run(AttendanceTrackingApplication.class, args);

        // Set prod profile for Heroku
//        if (System.getenv("DYNO") != null) {
//            app.setAdditionalProfiles("prod");
//        }

    }

//    @Bean
//    CommandLineRunner passwordMigration(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        return args -> {
//            // Get all users
//            var users = userRepository.findAll();
//
//            // Update passwords for each user
//            for (User user : users) {
//                // Check if password is not already encoded (doesn't start with $2a$)
//                if (!user.getPassword().startsWith("$2a$")) {
//                    // Encode the plain text password
//                    String encodedPassword = passwordEncoder.encode(user.getPassword());
//                    user.setPassword(encodedPassword);
//                    userRepository.save(user);
//                    System.out.println("Updated password for user: " + user.getUsername());
//                }
//            }
//
//            System.out.println("Password migration completed!");
//        };
//    }

//    @Bean
//    CommandLineRunner initUsers(MemberRepository memberRepository, PasswordEncoder encoder) {
//        return args -> {
//            // Create admin if not exists
//            if (!memberRepository.existsByUsername("admin")) {
//                User admin = User.builder()
//                        .username("admin")
//                        .password(encoder.encode("admin123"))
//                        .name("Admin User")
//                        .email("admin@gym.com")
//                        .role(UserRole.ROLE_ADMIN)
//                        .build();
//                memberRepository.save(admin);
//                System.out.println("Admin user created: admin/admin123");
//            }
//
//            // Create PT if not exists
//            if (!memberRepository.existsByUsername("pt")) {
//                User pt = User.builder()
//                        .username("pt")
//                        .password(encoder.encode("pt123"))
//                        .name("PT User")
//                        .email("pt@gym.com")
//                        .isPt(true)
//                        .role(UserRole.ROLE_PT)
//                        .build();
//                memberRepository.save(pt);
//                System.out.println("PT user created: pt/pt123");
//            }
//
//            // Create member if not exists
//            if (!memberRepository.existsByUsername("member")) {
//                User user = User.builder()
//                        .username("member")
//                        .password(encoder.encode("member123"))
//                        .name("Test Member")
//                        .email("member@gym.com")
//                        .role(UserRole.ROLE_MEMBER)
//                        .build();
//                memberRepository.save(user);
//                System.out.println("Member user created: member/member123");
//            }
//        };
//    }
}