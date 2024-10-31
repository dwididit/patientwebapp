package dev.dwidi.patientwebapp.service;

import dev.dwidi.patientwebapp.dto.BaseResponse;
import dev.dwidi.patientwebapp.dto.patient.PatientRequest;
import dev.dwidi.patientwebapp.dto.patient.PatientResponse;
import dev.dwidi.patientwebapp.dto.patient.PatientUpdateRequest;
import org.springframework.data.domain.Page;

public interface PatientService {
    BaseResponse<PatientResponse> createPatient(PatientRequest patientRequest);
    BaseResponse<PatientResponse> updatePatient(String pid, PatientUpdateRequest patientUpdateRequest);
    BaseResponse<PatientResponse> deletePatient(String pid);
    BaseResponse<PatientResponse> getPatientByPID(String pid);
    BaseResponse<Page<PatientResponse>> getAllPatients(Integer page, Integer size);
}
