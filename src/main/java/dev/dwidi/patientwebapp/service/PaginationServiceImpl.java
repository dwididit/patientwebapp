package dev.dwidi.patientwebapp.service;

import dev.dwidi.patientwebapp.dto.BaseResponse;
import dev.dwidi.patientwebapp.dto.patient.PaginationRequest;
import dev.dwidi.patientwebapp.dto.patient.PaginationResponse;
import dev.dwidi.patientwebapp.dto.patient.PatientResponse;
import dev.dwidi.patientwebapp.entity.Patient;
import dev.dwidi.patientwebapp.repository.PatientRepository;
import dev.dwidi.patientwebapp.utils.RequestIdUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaginationServiceImpl implements PaginationService {

    private final PatientRepository patientRepository;

    @Override
    public BaseResponse<PaginationResponse<PatientResponse>> getPatientsByPage(PaginationRequest request) {
        String requestId = RequestIdUtils.generateRequestId();
        try {
            Sort sort = createSort(request);
            Pageable pageable = createPageable(request, sort);
            Specification<Patient> spec = createSpecifications(request);

            Page<Patient> patientsPage = patientRepository.findAll(spec, pageable);
            PaginationResponse<PatientResponse> response = createPaginationResponse(patientsPage);

            return new BaseResponse<>(
                    HttpStatus.OK.value(),
                    patientsPage.isEmpty() ? "No patients found" : "Patients retrieved successfully",
                    response,
                    requestId
            );

        } catch (Exception e) {
            log.error("Error retrieving patients with pagination: {}", e.getMessage());
            return new BaseResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error retrieving patients",
                    null,
                    requestId
            );
        }
    }

    private Sort createSort(PaginationRequest request) {
        String sortField = request.getSortBy() != null ? request.getSortBy() : "createdAt";
        Sort.Direction direction = Sort.Direction.fromString(
                request.getSortDirection() != null ? request.getSortDirection() : "DESC"
        );
        return Sort.by(direction, sortField);
    }

    private Pageable createPageable(PaginationRequest request, Sort sort) {
        int pageNumber = Math.max(request.getPage(), 0);
        int pageSize = request.getSize() != null ?
                Math.min(Math.max(request.getSize(), 1), 100) : 10;
        return PageRequest.of(pageNumber, pageSize, sort);
    }

    private Specification<Patient> createSpecifications(PaginationRequest request) {
        return (root, query, cb) -> {
            Specification<Patient> spec = Specification.where(null);

            if (request.getName() != null && !request.getName().trim().isEmpty()) {
                String nameLike = "%" + request.getName().toLowerCase().trim() + "%";
                spec = spec.and((root1, query1, cb1) ->
                        cb1.or(
                                cb1.like(cb1.lower(root1.get("firstName")), nameLike),
                                cb1.like(cb1.lower(root1.get("lastName")), nameLike)
                        )
                );
            }

            if (request.getState() != null) {
                spec = spec.and((root1, query1, cb1) ->
                        cb1.equal(root1.get("address").get("state"), request.getState())
                );
            }

            if (request.getStartDate() != null && request.getEndDate() != null) {

                LocalDateTime startDateTime = request.getStartDate().atStartOfDay();
                LocalDateTime endDateTime = request.getEndDate().atTime(23, 59, 59);

                log.info("Date filter - Start: {}, End: {}", startDateTime, endDateTime);

                // Query a sample record to check dates
                List<Patient> samplePatients = patientRepository.findAll(PageRequest.of(0, 1)).getContent();
                if (!samplePatients.isEmpty()) {
                    log.info("Sample patient createdAt: {}", samplePatients.get(0).getCreatedAt());
                }

                spec = spec.and((root1, query1, cb1) ->
                        cb1.between(root1.get("createdAt"),
                                startDateTime,
                                endDateTime)
                );
            }

            return spec.toPredicate(root, query, cb);
        };
    }

    private PaginationResponse<PatientResponse> createPaginationResponse(Page<Patient> patientsPage) {
        List<PatientResponse> patientResponses = patientsPage.getContent()
                .stream()
                .map(this::mapToPatientResponse)
                .collect(Collectors.toList());

        return PaginationResponse.<PatientResponse>builder()
                .content(patientResponses)
                .page(patientsPage.getNumber())
                .size(patientsPage.getSize())
                .totalElements(patientsPage.getTotalElements())
                .totalPages(patientsPage.getTotalPages())
                .last(patientsPage.isLast())
                .build();
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
                .state(patient.getAddress().getState())
                .postcode(patient.getAddress().getPostcode())
                .createdAt(patient.getCreatedAt())
                .updateAt(patient.getUpdatedAt())
                .build();
    }

}