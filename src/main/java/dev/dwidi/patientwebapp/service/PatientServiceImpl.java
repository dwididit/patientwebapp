package dev.dwidi.patientwebapp.service;

import dev.dwidi.patientwebapp.dto.BaseResponse;
import dev.dwidi.patientwebapp.dto.patient.PatientRequest;
import dev.dwidi.patientwebapp.dto.patient.PatientResponse;
import dev.dwidi.patientwebapp.dto.patient.PatientUpdateRequest;
import dev.dwidi.patientwebapp.entity.Patient;
import dev.dwidi.patientwebapp.entity.embedded.AustralianAddress;
import dev.dwidi.patientwebapp.enums.AustralianState;
import dev.dwidi.patientwebapp.exception.FailedGeneratePIDException;
import dev.dwidi.patientwebapp.exception.PatientNotFoundException;
import dev.dwidi.patientwebapp.repository.PatientRepository;
import dev.dwidi.patientwebapp.utils.RequestIdUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    public BaseResponse<PatientResponse> createPatient(PatientRequest patientRequest) {

        String requestId = RequestIdUtils.generateRequestId();

        try {

            Patient patient = new Patient();
            patient.setFirstName(patientRequest.getFirstName());
            patient.setLastName(patientRequest.getLastName());
            patient.setDateOfBirth(patientRequest.getDateOfBirth());
            patient.setGender(patientRequest.getGender());
            patient.setPid(generatePatientId());
            patient.setPhoneNumber(patientRequest.getPhoneNumber());

            AustralianAddress address = new AustralianAddress();
            address.setAddress(patientRequest.getAddress());
            address.setSuburb(patientRequest.getSuburb());
            address.setState(AustralianState.valueOf(String.valueOf(patientRequest.getState())));
            address.setPostcode(patientRequest.getPostcode());
            patient.setAddress(address);

            // Save patient
            Patient savedPatient = patientRepository.save(patient);

            // Map to response
            PatientResponse response = mapToPatientResponse(savedPatient);

            return new BaseResponse<>(
                    HttpStatus.CREATED.value(),
                    "Patient created successfully",
                    response, requestId
            );

        } catch (Exception e) {
            log.error("Error creating patient: ", e);
            return new BaseResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error creating patient",
                    null, requestId
            );
        }
    }

    private PatientResponse mapToPatientResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .pid(patient.getPid())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .phoneNumber(patient.getPhoneNumber())
                .address(patient.getAddress().getAddress())
                .suburb(patient.getAddress().getSuburb())
                .state(AustralianState.valueOf(String.valueOf(patient.getAddress().getState())))
                .postcode(patient.getAddress().getPostcode())
                .createdAt(patient.getCreatedAt())
                .updateAt(patient.getUpdatedAt())
                .build();
    }

    private String generatePatientId() {
        try {
            // Get next sequence value from database
            Long sequence = patientRepository.getNextSequenceValue();
            LocalDate now = LocalDate.now();

            // Ensure sequence stays within 3 digits
            int sequenceNumber = (int) (sequence % 1000);
            if (sequenceNumber == 0) sequenceNumber = 1;

            return String.format("%03d%02d%02d%02d",
                    sequenceNumber,
                    now.getDayOfMonth(),
                    now.getMonthValue(),
                    now.getYear() % 100);

        } catch (Exception e) {
            log.error("Error generating patient ID: {}", e.getMessage());
            throw new FailedGeneratePIDException("Error generating patient ID");
        }
    }

    @Override
    public BaseResponse<PatientResponse> updatePatient(String pid, PatientUpdateRequest patientUpdateRequest) {
        String requestId = RequestIdUtils.generateRequestId();

        try {
            Patient patient = patientRepository.findByPid(pid)
                    .orElseThrow(() -> new PatientNotFoundException("Patient not found with PID: " + pid));

            // Update patient details if provided (null-safe updates)
            updatePatientDetails(patient, patientUpdateRequest);

            // Save updated patient
            Patient updatedPatient = patientRepository.save(patient);

            return new BaseResponse<>(
                    HttpStatus.OK.value(),
                    "Patient updated successfully",
                    mapToPatientResponse(updatedPatient),
                    requestId
            );

        } catch (PatientNotFoundException e) {
            log.error("Patient not found: {}", pid);
            return new BaseResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    null,
                    requestId
            );
        } catch (Exception e) {
            log.error("Error updating patient: ", e);
            return new BaseResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error updating patient",
                    null,
                    requestId
            );
        }
    }

    private void updatePatientDetails(Patient patient, PatientUpdateRequest request) {
        // Update only if new values are provided
        Optional.ofNullable(request.getFirstName())
                .ifPresent(patient::setFirstName);
        Optional.ofNullable(request.getLastName())
                .ifPresent(patient::setLastName);
        Optional.ofNullable(request.getDateOfBirth())
                .ifPresent(patient::setDateOfBirth);
        Optional.ofNullable(request.getGender())
                .ifPresent(patient::setGender);
        Optional.ofNullable(request.getPhoneNumber())
                .ifPresent(patient::setPhoneNumber);

        // Handle address updates
        AustralianAddress address = patient.getAddress();
        Optional.ofNullable(request.getAddress())
                .ifPresent(address::setAddress);
        Optional.ofNullable(request.getSuburb())
                .ifPresent(address::setSuburb);
        Optional.ofNullable(request.getState())
                .ifPresent(address::setState);
        Optional.ofNullable(request.getPostcode())
                .ifPresent(address::setPostcode);
    }

    @Override
    public BaseResponse<PatientResponse> deletePatient(String pid) {
        String requestId = RequestIdUtils.generateRequestId();

        try {
            Patient patient = patientRepository.findByPid(pid)
                    .orElseThrow(() -> new PatientNotFoundException(("Patient not found with PID: " + pid)));

            patientRepository.delete(patient);

            return new BaseResponse<>(
                    HttpStatus.OK.value(),
                    "Patient deleted successfully",
                    null,
                    requestId
            );

        } catch (PatientNotFoundException e) {
            log.error("Patient not found for deletion: {}", pid);
            return new BaseResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    null,
                    requestId
            );
        } catch (Exception e) {
            log.error("Error deleting patient with pid {}: {}", pid, e.getMessage());
            return new BaseResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error deleting patient",
                    null,
                    requestId
            );
        }
    }

    @Override
    public BaseResponse<PatientResponse> getPatientByPID(String pid) {
        String requestId = RequestIdUtils.generateRequestId();

        if (pid == null) {
            throw new RuntimeException("PID can not be blank");
        }

        try {
            Patient patient = patientRepository.findByPid(pid)
                    .orElseThrow(() -> new PatientNotFoundException("Patient not found with PID: " + pid));

            return new BaseResponse<>(
                    HttpStatus.OK.value(),
                    "Patient retrieved successfully",
                    mapToPatientResponse(patient),
                    requestId
            );

        } catch (PatientNotFoundException e) {
            log.error("Patient not found with PID: {}", pid);
            return new BaseResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    null,
                    requestId
            );
        } catch (Exception e) {
            log.error("Error retrieving patient with PID {}: {}", pid, e.getMessage());
            return new BaseResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error retrieving patient",
                    null,
                    requestId
            );
        }
    }

    @Override
    public BaseResponse<Page<PatientResponse>> getAllPatients(Integer page, Integer size) {
        String requestId = RequestIdUtils.generateRequestId();

        try {
            // Default values if page or size is null
            int pageNumber = (page != null && page >= 0) ? page : 0;
            int pageSize = (size != null && size > 0) ? size : 10;

            // Create Pageable object with sorting by createdAt desc
            Pageable pageable = PageRequest.of(pageNumber, pageSize,
                    Sort.by("createdAt").descending());

            // Fetch patients with pagination
            Page<Patient> patientsPage = patientRepository.findAll(pageable);

            // Map to response
            Page<PatientResponse> patientResponses = patientsPage.map(this::mapToPatientResponse);

            if (patientsPage.isEmpty()) {
                return new BaseResponse<>(
                        HttpStatus.OK.value(),
                        "No patients found",
                        patientResponses,
                        requestId
                );
            }

            return new BaseResponse<>(
                    HttpStatus.OK.value(),
                    "Patients retrieved successfully",
                    patientResponses,
                    requestId
            );

        } catch (Exception e) {
            log.error("Error retrieving patients: {}", e.getMessage());
            return new BaseResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error retrieving patients",
                    null,
                    requestId
            );
        }
    }
}