package dev.dwidi.patientwebapp.dto.patient;

import dev.dwidi.patientwebapp.enums.AustralianState;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PaginationRequest {
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy;
    private String sortDirection;
    private String name;
    private AustralianState state;
    private LocalDate startDate;
    private LocalDate endDate;
}
