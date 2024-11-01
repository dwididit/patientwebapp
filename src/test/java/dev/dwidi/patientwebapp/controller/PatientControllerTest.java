package dev.dwidi.patientwebapp.controller;

import dev.dwidi.patientwebapp.dto.BaseResponse;
import dev.dwidi.patientwebapp.dto.patient.*;
import dev.dwidi.patientwebapp.enums.AustralianState;
import dev.dwidi.patientwebapp.enums.Gender;
import dev.dwidi.patientwebapp.service.PaginationService;
import dev.dwidi.patientwebapp.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @Mock
    private PaginationService paginationService;

    @InjectMocks
    private PatientController patientController;

    private PatientRequest patientRequest;
    private PatientResponse patientResponse;
    private PatientUpdateRequest patientUpdateRequest;
    private BaseResponse<PatientResponse> successResponse;
    private String testPid;

    @BeforeEach
    void setUp() {
        testPid = "123456789012";

        // Setup PatientRequest
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

        // Setup PatientResponse
        patientResponse = PatientResponse.builder()
                .id(1L)
                .pid(testPid)
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE)
                .phoneNumber("0123456789")
                .address("123 Test St")
                .suburb("TestSuburb")
                .state(AustralianState.NSW)
                .postcode("2000")
                .createdAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        // Setup PatientUpdateRequest
        patientUpdateRequest = new PatientUpdateRequest();
        patientUpdateRequest.setFirstName("John");
        patientUpdateRequest.setLastName("Doe");
        patientUpdateRequest.setPhoneNumber("0123456789");

        // Setup BaseResponse
        successResponse = new BaseResponse<>(
                HttpStatus.OK.value(),
                "Success",
                patientResponse,
                "TEST-REQUEST-ID"
        );
    }

    @Test
    void createPatient_Success() {
        when(patientService.createPatient(any(PatientRequest.class)))
                .thenReturn(successResponse);

        BaseResponse<PatientResponse> response = patientController.createPatient(patientRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals(patientResponse, response.getData());
        verify(patientService, times(1)).createPatient(any(PatientRequest.class));
    }

    @Test
    void updatePatient_Success() {
        when(patientService.updatePatient(anyString(), any(PatientUpdateRequest.class)))
                .thenReturn(successResponse);

        BaseResponse<PatientResponse> response = patientController.updatePatient(testPid, patientUpdateRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals(patientResponse, response.getData());
        verify(patientService, times(1)).updatePatient(eq(testPid), any(PatientUpdateRequest.class));
    }

    @Test
    void deletePatient_Success() {
        when(patientService.deletePatient(anyString()))
                .thenReturn(successResponse);

        BaseResponse<PatientResponse> response = patientController.deletePatient(testPid);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        verify(patientService, times(1)).deletePatient(testPid);
    }

    @Test
    void getPatientByPID_Success() {
        when(patientService.getPatientByPID(anyString()))
                .thenReturn(successResponse);

        BaseResponse<PatientResponse> response = patientController.getPatientByPID(testPid);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals(patientResponse, response.getData());
        verify(patientService, times(1)).getPatientByPID(testPid);
    }

    @Test
    void getAllPatients_Success() {
        List<PatientResponse> patientList = Arrays.asList(patientResponse);
        Page<PatientResponse> patientPage = new PageImpl<>(patientList);
        BaseResponse<Page<PatientResponse>> pageResponse = new BaseResponse<>(
                HttpStatus.OK.value(),
                "Success",
                patientPage,
                "TEST-REQUEST-ID"
        );

        when(patientService.getAllPatients(any(), any()))
                .thenReturn(pageResponse);

        BaseResponse<Page<PatientResponse>> response = patientController.getAllPatients(0, 10);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().getContent().size());
        verify(patientService, times(1)).getAllPatients(0, 10);
    }

    @Test
    void getAllPatients_WithDefaultPagination() {
        // Test with null page and size parameters
        Pageable defaultPageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<PatientResponse> emptyPage = new PageImpl<>(
                new ArrayList<>(),
                defaultPageable,
                0L
        );

        BaseResponse<Page<PatientResponse>> expectedResponse = new BaseResponse<>(
                HttpStatus.OK.value(),
                "No patients found",
                emptyPage,
                "c169cfb7-3cc2-4577-90c8-a103d5102757"
        );

        when(patientService.getAllPatients(null, null))
                .thenReturn(expectedResponse);

        BaseResponse<Page<PatientResponse>> response = patientController.getAllPatients(null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("No patients found", response.getMessage());
        assertTrue(response.getData().getContent().isEmpty());
        verify(patientService, times(1)).getAllPatients(null, null);
    }
    @Test
    void getAllPatients_WithInvalidPageSize() {
        // Test with invalid page size (0)
        Pageable defaultPageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<PatientResponse> defaultPage = new PageImpl<>(
                Arrays.asList(patientResponse),
                defaultPageable,
                1L
        );

        BaseResponse<Page<PatientResponse>> expectedResponse = new BaseResponse<>(
                HttpStatus.OK.value(),
                "Patients retrieved successfully",
                defaultPage,
                "c169cfb7-3cc2-4577-90c8-a103d5102757"
        );

        when(patientService.getAllPatients(0, 0))
                .thenReturn(expectedResponse);

        BaseResponse<Page<PatientResponse>> response = patientController.getAllPatients(0, 0);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertFalse(response.getData().getContent().isEmpty());
        verify(patientService, times(1)).getAllPatients(0, 0);
    }

    @Test
    void getPaginatedPatients_Success() {
        // Create test data
        List<PatientResponse> patients = Arrays.asList(patientResponse);

        // Create PaginationResponse with the actual structure
        PaginationResponse<PatientResponse> paginationResponse = new PaginationResponse<>();
        paginationResponse.setContent(patients);
        paginationResponse.setPage(0);
        paginationResponse.setSize(10);
        paginationResponse.setTotalElements(1008L);
        paginationResponse.setTotalPages(101);
        paginationResponse.setLast(false);

        BaseResponse<PaginationResponse<PatientResponse>> expectedResponse = new BaseResponse<>(
                HttpStatus.OK.value(),
                "Patients retrieved successfully",
                paginationResponse,
                "c169cfb7-3cc2-4577-90c8-a103d5102757"
        );

        when(paginationService.getPatientsByPage(any(PaginationRequest.class)))
                .thenReturn(expectedResponse);

        // Test the endpoint
        BaseResponse<PaginationResponse<PatientResponse>> response = patientController.getPaginatedPatients(
                0, 10, "createdAt", "DESC", "Jennifer", AustralianState.QLD,
                LocalDate.of(1977, 2, 17), LocalDate.of(1977, 2, 17));

        // Verify the response
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Patients retrieved successfully", response.getMessage());

        PaginationResponse<PatientResponse> data = response.getData();
        assertNotNull(data);
        assertEquals(0, data.getPage());
        assertEquals(10, data.getSize());
        assertEquals(1008L, data.getTotalElements());
        assertEquals(101, data.getTotalPages());
        assertFalse(data.isLast());
        assertEquals(1, data.getContent().size());

        verify(paginationService, times(1)).getPatientsByPage(any(PaginationRequest.class));
    }

    @Test
    void createPatient_ValidationError() {
        // Create a request with validation error (missing required fields)
        PatientRequest invalidRequest = new PatientRequest();
        invalidRequest.setFirstName("");  // Invalid: empty name

        BaseResponse<PatientResponse> errorResponse = new BaseResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                "Validation error: First name is required",
                null,
                "TEST-REQUEST-ID"
        );

        when(patientService.createPatient(any(PatientRequest.class)))
                .thenReturn(errorResponse);

        BaseResponse<PatientResponse> response = patientController.createPatient(invalidRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("Validation error: First name is required", response.getMessage());
        assertNull(response.getData());
        verify(patientService, times(1)).createPatient(any(PatientRequest.class));
    }

    @Test
    void updatePatient_PatientNotFound() {
        String nonExistentPid = "nonexistent123";
        BaseResponse<PatientResponse> errorResponse = new BaseResponse<>(
                HttpStatus.NOT_FOUND.value(),
                "Patient not found with PID: " + nonExistentPid,
                null,
                "TEST-REQUEST-ID"
        );

        when(patientService.updatePatient(eq(nonExistentPid), any(PatientUpdateRequest.class)))
                .thenReturn(errorResponse);

        BaseResponse<PatientResponse> response = patientController.updatePatient(nonExistentPid, patientUpdateRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
        assertEquals("Patient not found with PID: " + nonExistentPid, response.getMessage());
        assertNull(response.getData());
        verify(patientService, times(1)).updatePatient(eq(nonExistentPid), any(PatientUpdateRequest.class));
    }

    @Test
    void getPatientByPID_PatientNotFound() {
        String nonExistentPid = "nonexistent123";
        BaseResponse<PatientResponse> errorResponse = new BaseResponse<>(
                HttpStatus.NOT_FOUND.value(),
                "Patient not found with PID: " + nonExistentPid,
                null,
                "TEST-REQUEST-ID"
        );

        when(patientService.getPatientByPID(nonExistentPid))
                .thenReturn(errorResponse);

        BaseResponse<PatientResponse> response = patientController.getPatientByPID(nonExistentPid);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
        assertEquals("Patient not found with PID: " + nonExistentPid, response.getMessage());
        assertNull(response.getData());
        verify(patientService, times(1)).getPatientByPID(nonExistentPid);
    }

    @Test
    void deletePatient_PatientNotFound() {
        String nonExistentPid = "nonexistent123";
        BaseResponse<PatientResponse> errorResponse = new BaseResponse<>(
                HttpStatus.NOT_FOUND.value(),
                "Patient not found with PID: " + nonExistentPid,
                null,
                "TEST-REQUEST-ID"
        );

        when(patientService.deletePatient(nonExistentPid))
                .thenReturn(errorResponse);

        BaseResponse<PatientResponse> response = patientController.deletePatient(nonExistentPid);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
        assertEquals("Patient not found with PID: " + nonExistentPid, response.getMessage());
        assertNull(response.getData());
        verify(patientService, times(1)).deletePatient(nonExistentPid);
    }

    @Test
    void getAllPatients_InternalServerError() {
        when(patientService.getAllPatients(any(), any()))
                .thenReturn(new BaseResponse<>(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Error retrieving patients",
                        null,
                        "TEST-REQUEST-ID"
                ));

        BaseResponse<Page<PatientResponse>> response = patientController.getAllPatients(0, 10);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode());
        assertEquals("Error retrieving patients", response.getMessage());
        assertNull(response.getData());
        verify(patientService, times(1)).getAllPatients(0, 10);
    }

    @Test
    void getPaginatedPatients_InvalidDateRange() {
        LocalDate endDate = LocalDate.of(1977, 2, 17);
        LocalDate startDate = endDate.plusDays(1); // Start date after end date

        PaginationRequest request = PaginationRequest.builder()
                .page(0)
                .size(10)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        BaseResponse<PaginationResponse<PatientResponse>> errorResponse = new BaseResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                "Start date cannot be after end date",
                null,
                "TEST-REQUEST-ID"
        );

        when(paginationService.getPatientsByPage(any(PaginationRequest.class)))
                .thenReturn(errorResponse);

        BaseResponse<PaginationResponse<PatientResponse>> response = patientController.getPaginatedPatients(
                0, 10, null, null, null, null, startDate, endDate);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("Start date cannot be after end date", response.getMessage());
        assertNull(response.getData());
        verify(paginationService, times(1)).getPatientsByPage(any(PaginationRequest.class));
    }

    @Test
    void getPaginatedPatients_InvalidPageNumber() {
        // Test with negative page number
        PaginationRequest request = PaginationRequest.builder()
                .page(-1)
                .size(10)
                .build();

        BaseResponse<PaginationResponse<PatientResponse>> errorResponse = new BaseResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                "Page number cannot be negative",
                null,
                "TEST-REQUEST-ID"
        );

        when(paginationService.getPatientsByPage(any(PaginationRequest.class)))
                .thenReturn(errorResponse);

        BaseResponse<PaginationResponse<PatientResponse>> response = patientController.getPaginatedPatients(
                -1, 10, null, null, null, null, null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("Page number cannot be negative", response.getMessage());
        assertNull(response.getData());
        verify(paginationService, times(1)).getPatientsByPage(any(PaginationRequest.class));
    }

    @Test
    void updatePatient_InvalidPostcode() {
        PatientUpdateRequest invalidRequest = new PatientUpdateRequest();
        invalidRequest.setPostcode("invalid"); // Invalid postcode
        invalidRequest.setState(AustralianState.NSW);

        BaseResponse<PatientResponse> errorResponse = new BaseResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid postcode for state NSW",
                null,
                "TEST-REQUEST-ID"
        );

        when(patientService.updatePatient(eq(testPid), any(PatientUpdateRequest.class)))
                .thenReturn(errorResponse);

        BaseResponse<PatientResponse> response = patientController.updatePatient(testPid, invalidRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("Invalid postcode for state NSW", response.getMessage());
        assertNull(response.getData());
        verify(patientService, times(1)).updatePatient(eq(testPid), any(PatientUpdateRequest.class));
    }
}
