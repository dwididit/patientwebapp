package dev.dwidi.patientwebapp.repository;

import dev.dwidi.patientwebapp.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByPid(String pid);

    @Query(value = "SELECT nextval('patient_id_seq')", nativeQuery = true)
    Long getNextSequenceValue();
}
