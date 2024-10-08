package org.user.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.user.app.entity.Appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {



    /**
     * Find available appointments for a doctor by date.
     */
    List<Appointment> findByDoctorIdAndAppointmentDateAndIsAvailable(
            Long doctorId, LocalDate date, boolean isAvailable);

    /**
     * Find all appointments for a doctor on a specific date.
     */
    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate date);

    /**
     * Find appointment by ID.
     */
    Appointment findAppointmentById(Long appointmentId);

    /**
     * Find appointments by patient ID and status.
     */
    List<Appointment> findByPatientIdAndStatus(Long patientId, String status);
    
    /**
     * Find appointments by appointment ID and patientID.
     */
    Optional<Appointment> findByIdAndPatientId(Long apointmentId, Long patientId);
    
  
}
  