package org.user.app.service;


import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.user.app.entity.Doctor;
import org.user.app.entity.Patient;



public interface DoctorService {

    /**
     * Register a new doctor in the system.
     *
     * @param doctor the doctor entity to be registered
     */
    Doctor registerDoctor(Doctor doctor);
    
    /**
     * Retrieve a doctor entity by their email address.
     *
     * @param email the email address of the doctor to be retrieved.
     */
    Doctor getDoctorByEmail(String email);
    
    /**
     * Find all patients associated with a specific doctor.
     *
     * @param doctor the doctor whose patients are to be retrieved.
     */
    Set<Patient> findPatients(Doctor doctor);
    
    /**
     * Retrieve a doctor entity by its unique ID.
     *
     * @param Id the unique ID of the doctor to be retrieved.
     */
    Optional<Doctor>getDoctorById(Long Id);
    
    /**
     * Delete a doctor entity by its unique ID.
     *
     * @param id the unique ID of the doctor to be deleted. 
     */
    void deleteDoctor(Long id);
    
    /**
     * Retrieve all doctor entities.
     */
    List<Doctor> getAllDoctors();
    

    /**
     * Update the details of an existing doctor.
     *
     * @param id the unique ID of the doctor to be updated. 
     * @param updatedDoctor a doctor entity containing the updated details. 
     */
	Doctor updateDoctor(Long id, Doctor updatedDoctor);
}