package dev.dwidi.patientwebapp.service;

import dev.dwidi.patientwebapp.dto.BaseResponse;
import dev.dwidi.patientwebapp.dto.patient.PaginationRequest;
import dev.dwidi.patientwebapp.dto.patient.PaginationResponse;
import dev.dwidi.patientwebapp.dto.patient.PatientResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaginationServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PaginationServiceImpl paginationService;

    private Patient testPatient;
    private PaginationRequest request;
    private final LocalDateTime NOW = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        AustralianAddress address = new AustralianAddress();
        address.setAddress("71 Rosemary Avenue");
        address.setSuburb("Shepparton");
        address.setState(AustralianState.NSW);
        address.setPostcode("2482");

        testPatient = new Patient();
        testPatient.setId(671L);
        testPatient.setPid("4a104e760c63");
        testPatient.setFirstName("Amelia");
        testPatient.setLastName("Smith");
        testPatient.setDateOfBirth(LocalDate.of(1969, 3, 9));
        testPatient.setGender(Gender.FEMALE);
        testPatient.setPhoneNumber("0472989424");
        testPatient.setAddress(address);
        testPatient.setCreatedAt(NOW);
        testPatient.setUpdatedAt(NOW);

        request = PaginationRequest.builder()
                .page(0)
                .size(10)
                .name("Smith")
                .state(AustralianState.NSW)
                .sortBy("firstName")
                .sortDirection("ASC")
                .startDate(LocalDate.of(2024, 9, 1))
                .endDate(LocalDate.of(2024, 11, 1))
                .build();
    }

    @Test
    void getPatientsByPage_Success() {
        // Arrange
        Page<Patient> patientPage = new PageImpl<>(
                List.of(testPatient),
                PageRequest.of(0, 10),
                15
        );
        when(patientRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(patientPage);

        // Act
        BaseResponse<PaginationResponse<PatientResponse>> response =
                paginationService.getPatientsByPage(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Patients retrieved successfully", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().getContent().size());

        PatientResponse patientResponse = response.getData().getContent().get(0);
        assertEquals(testPatient.getId(), patientResponse.getId());
        assertEquals(testPatient.getFirstName(), patientResponse.getFirstName());
        assertEquals(testPatient.getLastName(), patientResponse.getLastName());
        assertEquals(AustralianState.NSW, patientResponse.getState());
    }

    @Test
    void getPatientsByPage_NoResults() {
        // Arrange
        Page<Patient> emptyPage = new PageImpl<>(List.of());
        when(patientRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        // Act
        BaseResponse<PaginationResponse<PatientResponse>> response =
                paginationService.getPatientsByPage(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("No patients found", response.getMessage());
        assertTrue(response.getData().getContent().isEmpty());
    }

    @Test
    void getPatientsByPage_InvalidDateRange() {
        // Arrange
        request.setStartDate(LocalDate.of(2024, 11, 1));
        request.setEndDate(LocalDate.of(2024, 9, 1));

        // Act & Assert
        BaseResponse<PaginationResponse<PatientResponse>> response =
                paginationService.getPatientsByPage(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode());
        assertEquals("Start date cannot be after end date", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void getPatientsByPage_DefaultSortingAndPaging() {
        // Arrange
        request.setSortBy(null);
        request.setSortDirection(null);

        Page<Patient> patientPage = new PageImpl<>(
                List.of(testPatient),
                PageRequest.of(0, 10),
                1
        );

        when(patientRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(patientPage);

        // Act
        BaseResponse<PaginationResponse<PatientResponse>> response =
                paginationService.getPatientsByPage(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertNotNull(response.getData());
        assertEquals(0, response.getData().getPage());
        assertEquals(10, response.getData().getSize());
        assertEquals(1, response.getData().getTotalPages());
    }
}