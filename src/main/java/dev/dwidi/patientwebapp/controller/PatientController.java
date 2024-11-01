package dev.dwidi.patientwebapp.controller;

import dev.dwidi.patientwebapp.dto.BaseResponse;
import dev.dwidi.patientwebapp.dto.patient.*;
import dev.dwidi.patientwebapp.enums.AustralianState;
import dev.dwidi.patientwebapp.service.PaginationService;
import dev.dwidi.patientwebapp.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/patient")
@Slf4j
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final PaginationService paginationService;

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

    @GetMapping("/search")
    public BaseResponse<PaginationResponse<PatientResponse>> getPaginatedPatients(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortDirection,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) AustralianState state,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Receiving pagination request with parameters: page={}, size={}, name={}, state={}, sortBy={}, sortDirection={}",
                page, size, name, state, sortBy, sortDirection);

        PaginationRequest request = PaginationRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .name(name)
                .state(state)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        return paginationService.getPatientsByPage(request);
    }
}
