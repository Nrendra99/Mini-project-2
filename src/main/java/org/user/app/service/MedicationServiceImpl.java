package org.user.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.user.app.entity.Appointment;
import org.user.app.entity.Medication;
import org.user.app.exceptions.AppointmentNotFoundException;
import org.user.app.exceptions.MedicationNotFoundException;
import org.user.app.repository.AppointmentRepository;
import org.user.app.repository.MedicationRepository;

import java.util.List;


@Service
public class MedicationServiceImpl implements MedicationService {

    @Autowired
    private MedicationRepository medicationRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;
    
    /**
     * Add a new medication to a specific appointment.
     *
     * @param appointmentId the ID of the appointment to which the medication will be added
     * @param medication the medication entity to be added
     */
    @Transactional
    public void addMedicationToAppointment(Long appointmentId, Medication medication) {
        // Find the appointment by ID
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found"));
       
       
        medication.setAppointment(appointment);
        medicationRepository.save(medication);
    }

    /**
     * Update details of an existing medication.
     *
     * @param medication the updated medication entity
     */
    @Transactional
    @Override
    public Medication updateMedication(Medication medication) {
        if (!medicationRepository.existsById(medication.getId())) {
            throw new MedicationNotFoundException("Medication with ID " + medication.getId() + " not found");
        }
        
        // Save the updated medication to the database
        return medicationRepository.save(medication);
    }
    /**
     * Remove a medication from the system.
     *
     * @param medicationId the ID of the medication to be removed
     */
    @Transactional
    @Override
    public void removeMedication(Long medicationId) {
        // Retrieve the medication by its ID
        Medication medication = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new MedicationNotFoundException("Medication with ID " + medicationId + " not found"));

        medicationRepository.delete(medication); // Delete the medication from the database
    }

    /**
     * Retrieve all medications associated with a specific appointment.
     *
     * @param appointmentId the ID of the appointment for which to retrieve medications
     * @return a Set of medications associated with the specified appointment
     */
    @Override
    public List<Medication> getMedications(Long appointmentId) {
        appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment with ID " + appointmentId + " not found"));

       return medicationRepository.findByAppointmentId(appointmentId);
    }

    /**
     * Retrieve a medication by its ID.
     *
     * @param medicationId the ID of the medication to retrieve
     * @return the optional medication entity
     */
    public Medication getMedicationById(Long medicationId) {
        return (medicationRepository.findById(medicationId)
                .orElseThrow(() -> new MedicationNotFoundException("Medication with ID " + medicationId + " not found"))); 
    }

  }