//package pt.attendancetracking.config;
//
//
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//
//import javax.sql.DataSource;
//import java.net.URI;
//import java.net.URISyntaxException;
//
//@Configuration
//@Profile("prod")
//public class DatabaseConfig {
//
//    @Value("${DATABASE_URL}")
//    private String databaseUrl;
//
//    @Bean
//    public DataSource dataSource() throws URISyntaxException {
//        URI dbUri = new URI(databaseUrl);
//
//        String username = dbUri.getUserInfo().split(":")[0];
//        String password = dbUri.getUserInfo().split(":")[1];
//        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";
//
//        HikariConfig config = new HikariConfig();
//        config.setJdbcUrl(dbUrl);
//        config.setUsername(username);
//        config.setPassword(password);
//        config.setMaximumPoolSize(5);
//        config.addDataSourceProperty("socketTimeout", "30");
//
//        return new HikariDataSource(config);
//    }
//}