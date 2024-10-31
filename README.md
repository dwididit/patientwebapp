# Patient Web Application

A Spring Boot web application for managing patient records with comprehensive search capabilities and Australian address support.

## Features

- **CRUD Operations**: Complete patient record management
- **Advanced Search**: Comprehensive search functionality with pagination
- **Address Validation**: Built-in Australian address validation
- **Auto-generation**: Automated Patient ID (PID) creation
- **Request Tracking**: Unique Request ID for monitoring
- **Error Handling**: Global exception handling system
- **Database**: Robust PostgreSQL integration

## Tech Stack

### Core Technologies
- Java 21
- Spring Boot 3.x
- PostgreSQL

### Dependencies
- Spring Data JPA
- Hibernate Types
- Lombok
- Jakarta Validation

## Prerequisites

Before you begin, ensure you have the following installed:

| Requirement | Version |
|------------|---------|
| JDK        | ≥ 17    |
| Maven      | ≥ 3.6   |
| PostgreSQL | ≥ 12    |

## Getting Started

1. **Clone the repository**
   ```bash
   gh repo clone dwididit/patientwebapp
   cd patientwebapp/
   ```

2. **Configure PostgreSQL**
   ```properties
   # Update application.properties with your database credentials
   spring.datasource.url=jdbc:postgresql://localhost:5432/patient_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Build the application**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

## Configuration

### Application Properties

```properties
# Server Configuration
server.port=9090

# Database Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Logging Configuration
logging.level.org.springframework=INFO
logging.level.com.hospital=DEBUG
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Support

For support and queries, please create an issue in the GitHub repository or contact the development team.
