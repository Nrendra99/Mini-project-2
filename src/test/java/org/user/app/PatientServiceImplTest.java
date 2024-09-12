package org.user.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.user.app.entity.Patient;
import org.user.app.exceptions.PatientNotFoundException;
import org.user.app.repository.PatientRepository;
import org.user.app.service.PatientServiceImpl;

import java.util.Optional;
import java.util.List;

public class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PatientServiceImpl patientServiceImpl;

    private Patient patient;
    private Patient updatedPatient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        patient = new Patient();
        patient.setId(1L);
        patient.setPassword("plainPassword");

        updatedPatient = new Patient();
        updatedPatient.setId(1L);
        updatedPatient.setPassword("newPlainPassword");
    }

    @Test
    public void testRegisterPatient() {
        String encryptedPassword = "encryptedPassword";
        when(passwordEncoder.encode(patient.getPassword())).thenReturn(encryptedPassword);
        when(patientRepository.save(patient)).thenReturn(patient);

        Patient registeredPatient = patientServiceImpl.registerPatient(patient);

        assertNotNull(registeredPatient);
        assertEquals(encryptedPassword, registeredPatient.getPassword());
        verify(patientRepository).save(patient);
    }

    @Test
    public void testRegisterPatientWithNullPassword() {
        patient.setPassword(null);
        when(patientRepository.save(patient)).thenReturn(patient);

        Patient registeredPatient = patientServiceImpl.registerPatient(patient);

        assertNotNull(registeredPatient);
        assertNull(registeredPatient.getPassword());
        verify(patientRepository).save(patient);
    }

    @Test
    public void testGetPatientById() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        Optional<Patient> foundPatient = patientServiceImpl.getPatientById(1L);

        assertTrue(foundPatient.isPresent());
        assertEquals(patient, foundPatient.get());
        verify(patientRepository).findById(1L);
    }

    @Test
    public void testGetPatientByIdNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Patient> foundPatient = patientServiceImpl.getPatientById(1L);

        assertFalse(foundPatient.isPresent());
        verify(patientRepository).findById(1L);
    }

    @Test
    public void testDeletePatient() {
        doNothing().when(patientRepository).deleteById(1L);

        patientServiceImpl.deletePatient(1L);

        verify(patientRepository).deleteById(1L);
    }

    @Test
    public void testGetAllPatients() {
        List<Patient> patients = List.of(patient);
        when(patientRepository.findAll()).thenReturn(patients);

        List<Patient> allPatients = patientServiceImpl.getAllPatients();

        assertNotNull(allPatients);
        assertEquals(1, allPatients.size());
        assertEquals(patient, allPatients.get(0));
        verify(patientRepository).findAll();
    }

    @Test
    public void testGetPatientByEmail() {
        when(patientRepository.findByEmail("test@example.com")).thenReturn(patient);

        Patient foundPatient = patientServiceImpl.getPatientByEmail("test@example.com");

        assertNotNull(foundPatient);
        assertEquals(patient, foundPatient);
        verify(patientRepository).findByEmail("test@example.com");
    }

    @Test
    public void testUpdatePatient() {
        String encryptedPassword = "newEncryptedPassword";
        when(patientRepository.existsById(1L)).thenReturn(true);
        when(passwordEncoder.encode(updatedPatient.getPassword())).thenReturn(encryptedPassword);
        when(patientRepository.save(updatedPatient)).thenReturn(updatedPatient);

        Patient result = patientServiceImpl.updatePatient(1L, updatedPatient);

        assertNotNull(result);
        assertEquals(encryptedPassword, result.getPassword());
        verify(patientRepository).save(updatedPatient);
    }

    @Test
    public void testUpdatePatientNotFound() {
        when(patientRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(PatientNotFoundException.class, () -> {
            patientServiceImpl.updatePatient(1L, updatedPatient);
        });

        assertEquals("Patient not found with ID: 1", exception.getMessage());
        verify(patientRepository, never()).save(updatedPatient);
    }
}

