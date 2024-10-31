package dev.dwidi.patientwebapp.controller;

import dev.dwidi.patientwebapp.dto.BaseResponse;
import dev.dwidi.patientwebapp.dto.patient.PatientRequest;
import dev.dwidi.patientwebapp.dto.patient.PatientResponse;
import dev.dwidi.patientwebapp.dto.patient.PatientUpdateRequest;
import dev.dwidi.patientwebapp.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patient")
@Slf4j
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping("/create")
    public BaseResponse<PatientResponse> createPatient(@RequestBody PatientRequest patientRequest) {
        log.info("Receiving request to create new patient");
        return patientService.createPatient(patientRequest);
    }

    @PutMapping("/edit")
    public BaseResponse<PatientResponse> updatePatient(@RequestParam String pid, @RequestBody PatientUpdateRequest patientUpdateRequest) {
        log.info("Receiving request to edit patient");
        return patientService.updatePatient(pid,patientUpdateRequest);
    }

    @DeleteMapping("/delete")
    public BaseResponse<PatientResponse> deletePatient(@RequestParam String pid) {
        log.info("Receiving request to delete patient");
        return patientService.deletePatient(pid);
    }

    @GetMapping("/{pid}")
    public BaseResponse<PatientResponse> getPatientByPID(@PathVariable String pid) {
        log.info("Receiving request to get patient");
        return patientService.getPatientByPID(pid);
    }

    @GetMapping("/page")
    public BaseResponse<Page<PatientResponse>> getAllPatients(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        log.info("Receiving request to get patient using pagination");
        return patientService.getAllPatients(page, size);
    }
}
