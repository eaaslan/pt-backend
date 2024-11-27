package pt.attendancetracking.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        // Get the authentication object from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Log the roles (authorities) of the authenticated user
        if (authentication != null) {
            System.out.println("User Roles:");
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                System.out.println(authority.getAuthority());
            }
        } else {
            System.out.println("No authentication information available.");
        }

        return "Hello, World!";
    }
}
