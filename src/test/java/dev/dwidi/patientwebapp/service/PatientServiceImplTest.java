package dev.dwidi.patientwebapp.service;

import dev.dwidi.patientwebapp.dto.BaseResponse;
import dev.dwidi.patientwebapp.dto.patient.PatientRequest;
import dev.dwidi.patientwebapp.dto.patient.PatientResponse;
import dev.dwidi.patientwebapp.dto.patient.PatientUpdateRequest;
import dev.dwidi.patientwebapp.entity.Patient;
import dev.dwidi.patientwebapp.entity.embedded.AustralianAddress;
import dev.dwidi.patientwebapp.enums.AustralianState;
import dev.dwidi.patientwebapp.enums.Gender;
import dev.dwidi.patientwebapp.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientServiceImpl patientService;

    private PatientRequest patientRequest;
    private Patient patient;
    private PatientUpdateRequest updateRequest;
    private final String TEST_PID = "123456789012";

    @BeforeEach
    void setUp() {
        // Setup Patient Request
        patientRequest = new PatientRequest();
        patientRequest.setFirstName("John");
        patientRequest.setLastName("Doe");
        patientRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        patientRequest.setGender(Gender.MALE);
        patientRequest.setPhoneNumber("0123456789");
        patientRequest.setAddress("123 Test St");
        patientRequest.setSuburb("TestSuburb");
        patientRequest.setState(AustralianState.NSW);
        patientRequest.setPostcode("2000");

        // Setup Patient Entity
        patient = new Patient();
        patient.setId(1L);
        patient.setPid(TEST_PID);
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        patient.setGender(Gender.MALE);
        patient.setPhoneNumber("0123456789");

        AustralianAddress address = new AustralianAddress();
        address.setAddress("123 Test St");
        address.setSuburb("TestSuburb");
        address.setState(AustralianState.NSW);
        address.setPostcode("2000");
        patient.setAddress(address);

        patient.setCreatedAt(LocalDateTime.now());
        patient.setUpdatedAt(LocalDateTime.now());

        // Setup Update Request
        updateRequest = new PatientUpdateRequest();
        updateRequest.setFirstName("John Updated");
        updateRequest.setPhoneNumber("0123456780");
    }

    @Test
    void createPatient_Success() {
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        BaseResponse<PatientResponse> response = patientService.createPatient(patientRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
        assertEquals("Patient created successfully", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(patientRequest.getFirstName(), response.getData().getFirstName());

        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void createPatient_ThrowsException() {
        when(patientRepository.save(any(Patient.class))).thenThrow(new RuntimeException("Database error"));

        BaseResponse<PatientResponse> response = patientService.createPatient(patientRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode());
        assertEquals("Error creating patient", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void updatePatient_Success() {
        // Setup
        String pid = "1003011124";
        LocalDateTime now = LocalDateTime.of(2024, 11, 1, 8, 13, 46, 569714000);

        PatientUpdateRequest updateRequest = new PatientUpdateRequest();
        updateRequest.setFirstName("John Edit");
        updateRequest.setLastName("Doe");
        updateRequest.setDateOfBirth(LocalDate.of(1990, 5, 15));
        updateRequest.setGender(Gender.MALE);
        updateRequest.setPhoneNumber("0412345678");
        updateRequest.setAddress("123 Sydney Road");
        updateRequest.setSuburb("Bondi");
        updateRequest.setState(AustralianState.NSW);
        updateRequest.setPostcode("2026");

        Patient existingPatient = new Patient();
        existingPatient.setId(1005L);
        existingPatient.setPid(pid);
        existingPatient.setFirstName("John");
        existingPatient.setLastName("Doe");
        existingPatient.setDateOfBirth(LocalDate.of(1990, 5, 15));
        existingPatient.setGender(Gender.MALE);
        existingPatient.setPhoneNumber("0412345678");

        AustralianAddress address = new AustralianAddress();
        address.setAddress("123 Sydney Road");
        address.setSuburb("Bondi");
        address.setState(AustralianState.NSW);
        address.setPostcode("2026");
        existingPatient.setAddress(address);

        existingPatient.setCreatedAt(LocalDateTime.of(2024, 11, 1, 8, 10, 59, 112592000));
        existingPatient.setUpdatedAt(now);

        Patient updatedPatient = new Patient();
        BeanUtils.copyProperties(existingPatient, updatedPatient);
        updatedPatient.setFirstName("John Edit");
        updatedPatient.setUpdatedAt(now);

        // Mock repository behavior
        when(patientRepository.findByPid(pid)).thenReturn(Optional.of(existingPatient));
        when(patientRepository.save(any(Patient.class))).thenReturn(updatedPatient);

        // Execute
        BaseResponse<PatientResponse> response = patientService.updatePatient(pid, updateRequest);

        // Verify
        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatusCode()),
                () -> assertEquals("Patient updated successfully", response.getMessage()),
                () -> assertNotNull(response.getData()),
                () -> assertNotNull(response.getRequestId()),
                () -> {
                    PatientResponse data = response.getData();
                    assertEquals(1005L, data.getId());
                    assertEquals(pid, data.getPid());
                    assertEquals("John Edit", data.getFirstName());
                    assertEquals("Doe", data.getLastName());
                    assertEquals(LocalDate.of(1990, 5, 15), data.getDateOfBirth());
                    assertEquals(Gender.MALE, data.getGender());
                    assertEquals("0412345678", data.getPhoneNumber());
                    assertEquals("123 Sydney Road", data.getAddress());
                    assertEquals("Bondi", data.getSuburb());
                    assertEquals(AustralianState.NSW, data.getState());
                    assertEquals("2026", data.getPostcode());
                    assertEquals(LocalDateTime.of(2024, 11, 1, 8, 10, 59, 112592000), data.getCreatedAt());
                    assertEquals(now, data.getUpdateAt());
                }
        );
    }

    @Test
    void updatePatient_PatientNotFound() {
        // Setup
        String nonExistentPid = "1003011124";
        PatientUpdateRequest updateRequest = new PatientUpdateRequest();
        updateRequest.setFirstName("John Edit");
        updateRequest.setLastName("Doe");
        updateRequest.setDateOfBirth(LocalDate.of(1990, 5, 15));
        updateRequest.setGender(Gender.MALE);
        updateRequest.setPhoneNumber("0412345678");
        updateRequest.setAddress("123 Sydney Road");
        updateRequest.setSuburb("Bondi");
        updateRequest.setState(AustralianState.NSW);
        updateRequest.setPostcode("2026");

        // Mock repository behavior
        when(patientRepository.findByPid(nonExistentPid)).thenReturn(Optional.empty());

        // Execute
        BaseResponse<PatientResponse> response = patientService.updatePatient(nonExistentPid, updateRequest);

        // Verify
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode()),
                () -> assertEquals("Patient not found with PID: " + nonExistentPid, response.getMessage()),
                () -> assertNull(response.getData()),
                () -> assertNotNull(response.getRequestId())
        );

        // Verify repository interactions
        verify(patientRepository, times(1)).findByPid(nonExistentPid);
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void deletePatient_Success() {
        when(patientRepository.findByPid(TEST_PID)).thenReturn(Optional.of(patient));
        doNothing().when(patientRepository).delete(any(Patient.class));

        BaseResponse<PatientResponse> response = patientService.deletePatient(TEST_PID);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Patient deleted successfully", response.getMessage());
        verify(patientRepository).delete(any(Patient.class));
    }

    @Test
    void deletePatient_PatientNotFound() {
        when(patientRepository.findByPid(TEST_PID)).thenReturn(Optional.empty());

        BaseResponse<PatientResponse> response = patientService.deletePatient(TEST_PID);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
        assertTrue(response.getMessage().contains("Patient not found"));
        verify(patientRepository, never()).delete(any(Patient.class));
    }

    @Test
    void getPatientByPID_Success() {
        when(patientRepository.findByPid(TEST_PID)).thenReturn(Optional.of(patient));

        BaseResponse<PatientResponse> response = patientService.getPatientByPID(TEST_PID);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Patient retrieved successfully", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(TEST_PID, response.getData().getPid());
    }

    @Test
    void getPatientByPID_PatientNotFound() {
        when(patientRepository.findByPid(TEST_PID)).thenReturn(Optional.empty());

        BaseResponse<PatientResponse> response = patientService.getPatientByPID(TEST_PID);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
        assertTrue(response.getMessage().contains("Patient not found"));
        assertNull(response.getData());
    }

    @Test
    void getPatientByPID_NullPID() {
        assertThrows(RuntimeException.class, () -> patientService.getPatientByPID(null));
    }

    @Test
    void getAllPatients_Success() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Patient> patientPage = new PageImpl<>(Arrays.asList(patient), pageable, 1);

        when(patientRepository.findAll(any(Pageable.class))).thenReturn(patientPage);

        BaseResponse<Page<PatientResponse>> response = patientService.getAllPatients(0, 10);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Patients retrieved successfully", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().getTotalElements());
    }

    @Test
    void getAllPatients_EmptyResult() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Patient> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);

        when(patientRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        BaseResponse<Page<PatientResponse>> response = patientService.getAllPatients(0, 10);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("No patients found", response.getMessage());
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void getAllPatients_WithNullPageAndSize() {
        Pageable defaultPageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Patient> patientPage = new PageImpl<>(Arrays.asList(patient), defaultPageable, 1);

        when(patientRepository.findAll(any(Pageable.class))).thenReturn(patientPage);

        BaseResponse<Page<PatientResponse>> response = patientService.getAllPatients(null, null);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().getTotalElements());
    }
}