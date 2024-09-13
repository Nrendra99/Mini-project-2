package org.user.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        Medication medication = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new MedicationNotFoundException("Medication with ID " + medicationId + " not found"));

        medicationRepository.delete(medication); 
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