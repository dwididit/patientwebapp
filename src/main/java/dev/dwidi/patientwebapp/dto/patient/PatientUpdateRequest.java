package dev.dwidi.patientwebapp.dto.patient;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.dwidi.patientwebapp.enums.AustralianState;
import dev.dwidi.patientwebapp.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientUpdateRequest {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
    private String suburb;
    private AustralianState state;
    private String postcode;
    private String phoneNumber;
}
