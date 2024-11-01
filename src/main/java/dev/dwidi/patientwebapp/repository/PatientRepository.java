package dev.dwidi.patientwebapp.repository;

import dev.dwidi.patientwebapp.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {
    Optional<Patient> findByPid(String pid);

    @Query("SELECT CAST(SUBSTRING(MAX(p.pid), 1, LENGTH(MAX(p.pid)) - 6) AS int) " +
            "FROM Patient p WHERE p.pid LIKE concat('%', :datePattern)")
    Optional<Integer> findMaxSequenceByDatePattern(String datePattern);

    boolean existsByPid(String pid);
}
