package org.user.app.service;


import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.user.app.entity.Doctor;
import org.user.app.entity.Patient;
import org.user.app.exceptions.PatientNotFoundException;
import org.user.app.repository.PatientRepository;


@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Register a new patient by saving the provided patient entity.
     * The patient's password is encrypted before saving.
     *
     * @param patient the patient entity to be registered
     * @return the registered patient entity
     */
    @Transactional
    @Override
    public Patient registerPatient(Patient patient) {
        String encryptedPassword = passwordEncoder.encode(patient.getPassword());
        patient.setPassword(encryptedPassword);
        return patientRepository.save(patient);
    }

    /**
     * Retrieve a patient entity by its unique ID.
     *
     * @param id the unique ID of the patient to be retrieved
     * @return the patient entity associated with the given id
     */
    @Override
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    /**
     * Delete a patient entity by its unique ID.
     *
     * @param id the unique ID of the patient to be deleted
     */
    @Override
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }

    /**
     * Retrieve all patient entities.
     *
     * @return a list of all patients in the database
     */
    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    /**
     * Retrieve a patient entity by their email address.
     *
     * @param email the email address of the patient to be retrieved
     * @return the patient entity associated with the given email address
     */
    @Override
    public Patient getPatientByEmail(String email) {
        return patientRepository.findByEmail(email);
    }

    /**
     * Update the details of an existing patient.
     * The patient's password is encrypted before updating.
     *
     * @param id the unique ID of the patient to be updated
     * @param updatedPatient a patient entity containing the updated details
     * @return the updated patient entity with the new details applied
     * @throws PatientNotFoundException if no patient with the given ID is found in the database
     */
    @Override
    public Patient updatePatient(Long id, Patient updatedPatient) {

        Patient existingPatient = patientRepository.findById(id)
            .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + id));
        
      
        existingPatient.setFirstName(updatedPatient.getFirstName());
        existingPatient.setLastName(updatedPatient.getLastName());
        existingPatient.setEmail(updatedPatient.getEmail());
        existingPatient.setPassword(updatedPatient.getPassword());
        existingPatient.setAge(updatedPatient.getAge());
        existingPatient.setGender(updatedPatient.getGender());
        existingPatient.setMedicalHistory(updatedPatient.getMedicalHistory());

        String encryptedPassword = passwordEncoder.encode(updatedPatient.getPassword());
        existingPatient.setPassword(encryptedPassword);
      
        return patientRepository.save(existingPatient);
    }
    
    /**
     * Adds a doctor to a patient's list of doctors and registers the updated patient.
     *
     * @param patient the patient to whom the doctor will be added.
     * @param doctor the doctor to be added to the patient's list of doctors.
     * @return the updated patient after the doctor has been added and the patient has been registered.
     */
    @Override
    public Patient addDoctor(Patient patient, Doctor doctor) {
        
    	patient.addDoctor(doctor);
    	return registerPatient(patient);     
    }
    
    /**
     * Find all doctors associated with a specific patient.
     *
     * @param patient the patient whose doctors are to be retrieved.
     * @return a list of patients associated with the specified doctor.
     */
    @Override
    public Set<Doctor> viewDoctors(Patient patient) {
    	
    	return patient.getDoctors();
    }
}
        
    

