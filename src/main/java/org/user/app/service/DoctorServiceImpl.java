package org.user.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.user.app.entity.Doctor;
import org.user.app.entity.Patient;
import org.user.app.exceptions.DoctorNotFoundException;
import org.user.app.repository.DoctorRepository;


import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.Set;



@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    
    @Autowired
    private AppointmentServiceImpl appointmentServiceImpl;

    /**
     * Register a new doctor and create half-hour appointment slots for the rest of the current month.
     *
     * @param doctor the doctor entity to be registered.
     * @return the registered doctor entity.
     */
    @Transactional
    public Doctor registerDoctor(Doctor doctor) {
        
        String encryptedPassword = passwordEncoder.encode(doctor.getPassword());
        doctor.setPassword(encryptedPassword);
        
        
        Doctor savedDoctor = doctorRepository.save(doctor);

        
        LocalDate today = LocalDate.now();
        LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

        
        appointmentServiceImpl.createHalfHourAppointmentsForDoctor(savedDoctor, today, endOfMonth);

        return savedDoctor;
    }

    /**
     * Retrieve a doctor entity by their email address.
     *
     * @param email the email address of the doctor to be retrieved.
     * @return the doctor entity associated with the given email address.
     */
    @Override
    public Doctor getDoctorByEmail(String email) {
        return doctorRepository.findByEmail(email);
    }

    /**
     * Find all patients associated with a specific doctor.
     *
     * @param doctor the doctor whose patients are to be retrieved.
     * @return a list of patients associated with the specified doctor.
     */
    @Override
    public Set<Patient> findPatients(Doctor doctor) {
        return doctor.getPatients();
    }

    /**
     * Retrieve a doctor entity by its unique ID.
     *
     * @param Id the unique ID of the doctor to be retrieved.
     * @return an Optional containing the doctor entity if found.
     */
    @Override
    public Optional<Doctor> getDoctorById(Long Id) {
        return doctorRepository.findById(Id);
    }

    /**
     * Delete a doctor entity by its unique ID.
     *
     * @param id the unique ID of the doctor to be deleted. 
     */
    @Override
    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    /**
     * Retrieve all doctor entities.
     *
     * @return a list of all doctors in the database.
     */
    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    /**
     * Update the details of an existing doctor.
     *
     * @param id the unique ID of the doctor to be updated. 
     * @param updatedDoctor a doctor entity containing the updated details. 
     * @return the updated doctor entity with the new details applied.
     * @throws DoctorNotFoundException if no doctor with the given ID is found in the database.
     */
    @Override
    public Doctor updateDoctor(Long id, Doctor updatedDoctor) {
        if (!doctorRepository.existsById(id)) {
            throw new DoctorNotFoundException("Doctor not found with ID: " + id);
        }
        
        String encryptedPassword = passwordEncoder.encode(updatedDoctor.getPassword());
        updatedDoctor.setPassword(encryptedPassword);
        
        return doctorRepository.save(updatedDoctor);
    }
    
}