package org.user.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.user.app.entity.Doctor;
import org.user.app.entity.Patient;
import org.user.app.repository.PatientRepository;
import org.user.app.service.PatientServiceImpl;

import java.util.Optional;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PatientServiceImpl patientServiceImpl;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
 

    @Test
    @DisplayName("Register a new patient successfully")
    public void testRegisterPatientSuccess() {
        // Given
        Patient patient = new Patient();
        patient.setPassword("password");

        // Mocking the password encoder and repository methods
        when(passwordEncoder.encode("password")).thenReturn("encryptedPassword");
        when(patientRepository.save(patient)).thenReturn(patient);

        // When
        Patient result = patientServiceImpl.registerPatient(patient);

        // Then
        assertNotNull(result);
        assertEquals("encryptedPassword", result.getPassword());
        verify(patientRepository, times(1)).save(patient);
    }

    @Test
    @DisplayName("Retrieve a patient by ID successfully")
    public void testGetPatientByIdSuccess() {
        // Given
        Long id = 1L;
        Patient patient = new Patient();
        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));

        // When
        Optional<Patient> result = patientServiceImpl.getPatientById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(patient, result.get());
        verify(patientRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Delete a patient successfully")
    public void testDeletePatientSuccess() {
        // Given
        Long id = 1L;
        doNothing().when(patientRepository).deleteById(id);

        // When
        patientServiceImpl.deletePatient(id);

        // Then
        verify(patientRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Retrieve all patients successfully")
    public void testGetAllPatientsSuccess() {
        // Given
        List<Patient> patients = new ArrayList<>();
        when(patientRepository.findAll()).thenReturn(patients);

        // When
        List<Patient> result = patientServiceImpl.getAllPatients();

        // Then
        assertNotNull(result);
        assertEquals(patients, result);
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Retrieve patient by email successfully")
    public void testGetPatientByEmailSuccess() {
        // Given
        String email = "test@example.com";
        Patient patient = new Patient();
        when(patientRepository.findByEmail(email)).thenReturn(patient);

        // When
        Patient result = patientServiceImpl.getPatientByEmail(email);

        // Then
        assertNotNull(result);
        assertEquals(patient, result);
        verify(patientRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Update patient details successfully")
    public void testUpdatePatientSuccess() {
        // Given
        Long id = 1L;
        Patient existingPatient = new Patient();
        existingPatient.setPassword("oldPassword");

        Patient updatedPatient = new Patient();
        updatedPatient.setPassword("newPassword");

        when(patientRepository.findById(id)).thenReturn(Optional.of(existingPatient));
        when(patientRepository.save(any(Patient.class))).thenReturn(existingPatient);
        when(passwordEncoder.encode("newPassword")).thenReturn("encryptedNewPassword");

        // When
        Patient result = patientServiceImpl.updatePatient(id, updatedPatient);

        // Then
        assertNotNull(result);
        assertEquals("encryptedNewPassword", result.getPassword());
        verify(patientRepository, times(1)).findById(id);
        verify(patientRepository, times(1)).save(existingPatient);
    }

    @Test
    @DisplayName("Add doctor to patient successfully")
    public void testAddDoctorSuccess() {
        // Given
        Patient patient = new Patient();
        Doctor doctor = new Doctor();

        // Mocking the patient repository method
        when(patientRepository.save(patient)).thenReturn(patient);

        // When
        Patient result = patientServiceImpl.addDoctor(patient, doctor);

        // Then
        assertNotNull(result);
        verify(patientRepository, times(1)).save(patient);
    }

    @Test
    @DisplayName("View doctors associated with a patient successfully")
    public void testViewDoctorsSuccess() {
        // Given
        Patient patient = new Patient();
        Set<Doctor> doctors = new HashSet<>();
        doctors.add(new Doctor());
        patient.setDoctors(doctors);

        // When
        Set<Doctor> result = patientServiceImpl.viewDoctors(patient);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsAll(doctors));
    }
}