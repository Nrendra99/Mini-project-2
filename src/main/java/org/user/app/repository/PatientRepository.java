package org.user.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.user.app.entity.Patient;





public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Find a patient by their email.
     */
    Patient findByEmail(String email);

   
}