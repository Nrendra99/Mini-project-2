package org.user.app.service;


import java.util.List;



import org.user.app.entity.Medication;

public interface MedicationService {

    /**
     * Add a new medication to a specific appointment.
     *
     * @param appointmentId the ID of the appointment to which the medication will be added
     * @param medication the medication entity to be added
     */
	void addMedicationToAppointment(Long appointmentId, Medication medication);

    /**
     * Update details of an existing medication.
     *
     * @param medicationId the ID of the medication to be updated
     * @param updatedMedication the updated medication entity
     */
    public Medication updateMedication(Medication medication);

    /**
     * Remove a medication from the system.
     *
     * @param medicationId the ID of the medication to be removed
     */
    void removeMedication(Long medicationId);

    /**
     * Retrieve all medications associated with a specific appointment.
     *
     * @param appointmentId the ID of the appointment for which to retrieve medications
     * @return a list of medications associated with the specified appointment
     */
    List<Medication> getMedications(Long appointmentId);
  
    /** 
     *  Retrieve medication associated with a specific ID.
     *
     * @param medicationID the ID of the appointment for which to retrieve medication
     * @return medication associated with the specified ID.
     */
    Medication getMedicationById(Long medicationId);
    
    
}