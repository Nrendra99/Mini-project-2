package org.user.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.user.app.entity.Patient;

import java.util.List;



public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Find a patient by their email.
     */
    Patient findByEmail(String email);

    /**
     * Find patients associated with a specific doctor by doctor ID.
     */
    @Query("SELECT p FROM Patient p JOIN p.doctors d WHERE d.id = :doctorId")
    List<Patient> findByDoctorId(Long doctorId);
   
}