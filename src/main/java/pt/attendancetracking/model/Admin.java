package pt.attendancetracking.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;


@Entity
@Table(name = "admins")
public class Admin extends User {
    @Builder(builderMethodName = "adminBuilder")
    public Admin(String username, String password, String name, String email) {
        super(username, password, name, email, UserRole.ROLE_ADMIN);
    }

    protected Admin() {
        super();
    }
}