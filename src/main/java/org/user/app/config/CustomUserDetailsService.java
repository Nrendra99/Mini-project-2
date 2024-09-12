package org.user.app.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.user.app.entity.Admin;
import org.user.app.entity.Doctor;
import org.user.app.entity.Patient;
import org.user.app.repository.AdminRepository;
import org.user.app.repository.DoctorRepository;
import org.user.app.repository.PatientRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Try to load user as Patient
        Patient patient = patientRepository.findByEmail(email);
        if (patient != null) {
            return org.springframework.security.core.userdetails.User
                    .withUsername(patient.getEmail())
                    .password(patient.getPassword())
                    .roles("PATIENT")
                    .build();
        }

        // Try to load user as Doctor
        Doctor doctor = doctorRepository.findByEmail(email);
        if (doctor != null) {
            return org.springframework.security.core.userdetails.User
                    .withUsername(doctor.getEmail())
                    .password(doctor.getPassword())
                    .roles("DOCTOR")
                    .build();
        }
        
        // Try to load user as Admin
        Admin admin = adminRepository.findByEmail(email);
        if (admin != null) {
            return org.springframework.security.core.userdetails.User
                    .withUsername(admin.getEmail())
                    .password(admin.getPassword())
                    .roles("ADMIN")
                    .build();
        }

        // Throw custom exception if neither patient nor doctor is found
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}

