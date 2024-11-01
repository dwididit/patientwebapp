package dev.dwidi.patientwebapp.service;

import dev.dwidi.patientwebapp.dto.BaseResponse;
import dev.dwidi.patientwebapp.dto.patient.PaginationRequest;
import dev.dwidi.patientwebapp.dto.patient.PaginationResponse;
import dev.dwidi.patientwebapp.dto.patient.PatientResponse;

public interface PaginationService {
    BaseResponse<PaginationResponse<PatientResponse>> getPatientsByPage(PaginationRequest request);
}
