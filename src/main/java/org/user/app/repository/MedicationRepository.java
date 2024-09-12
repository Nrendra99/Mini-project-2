package org.user.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.user.app.entity.Medication;

public interface MedicationRepository extends JpaRepository<Medication, Long> {

    /**
     * Find medications associated with a specific appointment.
     */
    List<Medication> findByAppointmentId(Long appointmentId);
    
    
}