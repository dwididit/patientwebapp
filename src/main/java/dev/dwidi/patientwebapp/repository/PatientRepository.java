package dev.dwidi.patientwebapp.repository;

import dev.dwidi.patientwebapp.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {
    Optional<Patient> findByPid(String pid);

    @Query(value = """
        SELECT COALESCE(MAX(CAST(SUBSTRING(pid, 1, LENGTH(pid)-6) AS INTEGER)), 0)
        FROM patient 
        WHERE pid LIKE CONCAT('%', :datePattern)
        """, nativeQuery = true)
    Integer getMaxSequenceForToday(@Param("datePattern") String datePattern);

    boolean existsByPid(String pid);
}
