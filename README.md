# Gym Management System - Personal Training Appointment Tracker

A comprehensive Spring Boot application for managing gym appointments, personal training sessions, and member packages.
This system facilitates the interaction between personal trainers and gym members, handling appointment scheduling,
package management, and attendance tracking.

## Features

### User Management

- Multi-role support (Admin, Personal Trainer, Member)
- Secure authentication and authorization
- User profile management
- Personal trainer assignment to members

### Appointment Management

- Schedule training sessions
- Check-in system with QR code support
- Appointment history tracking
- Real-time availability checking
- Business hours enforcement (8 AM - 10 PM)

### Package Management

- Training package allocation
- Session tracking
- Package status monitoring
- Automated package expiration handling
- Cancellation quota management

### Administrative Features

- Member management
- PT management
- Package oversight
- System-wide monitoring
- Data seeding for testing

## Technology Stack

- **Backend Framework:** Spring Boot 3.3.5
- **Security:** Spring Security with Basic Authentication
- **Database:** PostgreSQL
- **Build Tool:** Maven
- **API Documentation:** OpenAPI (Swagger)
- **Testing:** JUnit, Spring Boot Test
- **Other Libraries:**
    - Lombok for reduced boilerplate
    - QR Code generation support
    - BCrypt for password encryption

## Getting Started

### Prerequisites

- JDK 17 or later
- Maven 3.6+
- PostgreSQL 12+
- Git

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/eaaslan/pt-backend
   ```

2. Navigate to the project directory:
   ```bash
   cd gym-management-system
   ```

3. Configure the database:
    - Create a PostgreSQL database named `attendance_db`
    - Update `application-dev.properties` with your database credentials

4. Build the project:
   ```bash
   mvn clean install
   ```

5. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8090` by default.

## API Documentation

Once the application is running, you can access the API documentation at:

- Swagger UI: `http://localhost:8090/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8090/v3/api-docs`

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── pt/attendancetracking/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── dto/
│   │       ├── model/
│   │       ├── repository/
│   │       ├── security/
│   │       ├── service/
│   │       └── util/
│   └── resources/
│       ├── application.properties
│       ├── application-dev.properties
│       └── application-prod.properties
```

## Security

The application implements Spring Security with:

- Basic Authentication
- Role-based access control
- Password encryption
- CORS configuration
- Session management

## Testing

The project includes a comprehensive test suite:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AppointmentServiceTest
```

## Development Environment

The application supports multiple environments:

- Development (`application-dev.properties`)
- Production (`application-prod.properties`)

Set the active profile using:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Contact

Enes Alp Aslan - ensa.aslan@gmail.com

## Acknowledgments

- Spring Boot team for the excellent framework
- PostgreSQL team
- All contributors who have helped with testing and improvements