package org.user.app.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.user.app.entity.Doctor;
import org.user.app.entity.Patient;

public interface PatientService {

    /**
     * Register a new patient in the system.
     *
     * @param patient the patient entity to be registered
     */
    Patient registerPatient(Patient patient);
    
    /**
     * Retrieve a patient entity by its unique ID.
     *
     * @param id the unique ID of the patient to be retrieved
     */
    Optional<Patient> getPatientById(Long id);
    
    /**
     * Delete a patient entity by its unique ID.
     *
     * @param id the unique ID of the patient to be deleted
     */
    void deletePatient(Long id);
    
    /**
     * Retrieve all patient entities.
     *
     * @return a list of all patients in the database
     */
    List<Patient> getAllPatients();
    
    /**
     * Retrieve a patient entity by their email address.
     *
     * @param email the email address of the patient to be retrieved
     * @return the patient entity associated with the given email address
     */
    Patient getPatientByEmail(String email);
    
    /**
     * Update the details of an existing patient.
     * The patient's password is encrypted before updating.
     *
     * @param id the unique ID of the patient to be updated
     * @param updatedPatient a patient entity containing the updated details
     */
	Patient updatePatient(Long id, Patient updatedPatient);
	
	/**
     * Adds a doctor to a patient's list of doctors and registers the updated patient.
     *
     * @param patient the patient to whom the doctor will be added.
     * @param doctor the doctor to be added to the patient's list of doctors.
     */
	Patient addDoctor(Patient patient, Doctor doctor);
	
	/**
     * Find all doctors associated with a specific patient.
     *
     * @param patient the patient whose doctors are to be retrieved.
     */
	Set<Doctor> viewDoctors(Patient patient);
}