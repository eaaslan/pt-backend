//package pt.attendancetracking.config;
//
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//
//import java.util.List;
//
//@Component
//public class CustomCorsConfiguration implements CorsConfigurationSource {
//
//
//    @Override
//    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOrigins(List.of("http://localhost:8080", "http://127.0.0.1:8080",
//                "http://127.0.0.1:5500", "http://192.168.1.13:5500", "https://pt-frontend-gtju.vercel.app"));
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
//        config.setAllowedHeaders(List.of("*"));
//        config.setAllowCredentials(true);
//        return config;
//    }
//}
