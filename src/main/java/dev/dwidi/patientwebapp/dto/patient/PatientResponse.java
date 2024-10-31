package dev.dwidi.patientwebapp.dto.patient;

import dev.dwidi.patientwebapp.enums.AustralianState;
import dev.dwidi.patientwebapp.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientResponse {
    private Long id;
    private String pid;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
    private String suburb;
    private AustralianState state;
    private String postcode;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
}

