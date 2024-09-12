package org.user.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.user.app.entity.Doctor;
import org.user.app.entity.Patient;
import org.user.app.exceptions.DoctorNotFoundException;
import org.user.app.repository.DoctorRepository;
import org.user.app.repository.PatientRepository;
import org.user.app.service.AppointmentServiceImpl;
import org.user.app.service.DoctorServiceImpl;


import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DoctorServiceImplTest {

    @InjectMocks
    private DoctorServiceImpl doctorServiceImpl;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AppointmentServiceImpl appointmentServiceImpl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Register a new doctor - Positive Scenario")
    public void testRegisterDoctorSuccess() {
        Doctor doctor = new Doctor();
        doctor.setPassword("password");
        Doctor savedDoctor = new Doctor();
        savedDoctor.setId(1L);

        when(doctorRepository.save(any(Doctor.class))).thenReturn(savedDoctor);
        when(passwordEncoder.encode("password")).thenReturn("encryptedPassword");
        doNothing().when(appointmentServiceImpl).createHalfHourAppointmentsForDoctor(any(Doctor.class), any(LocalDate.class), any(LocalDate.class));

        Doctor result = doctorServiceImpl.registerDoctor(doctor);

        assertNotNull(result);
        assertEquals(savedDoctor.getId(), result.getId());
        verify(doctorRepository, times(1)).save(doctor);
        verify(appointmentServiceImpl, times(1)).createHalfHourAppointmentsForDoctor(savedDoctor, LocalDate.now(), LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()));
    }

    @Test
    @DisplayName("Register a new doctor - Negative Scenario")
    public void testRegisterDoctorFailure() {
        Doctor doctor = new Doctor();
        doctor.setPassword("password");
        when(doctorRepository.save(any(Doctor.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            doctorServiceImpl.registerDoctor(doctor);
        });
        verify(doctorRepository, times(1)).save(doctor);
    }

    @Test
    @DisplayName("Get doctor by email - Positive Scenario")
    public void testGetDoctorByEmailSuccess() {
        String email = "test@example.com";
        Doctor doctor = new Doctor();
        when(doctorRepository.findByEmail(email)).thenReturn(doctor);

        Doctor result = doctorServiceImpl.getDoctorByEmail(email);

        assertNotNull(result);
        verify(doctorRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Get doctor by email - Negative Scenario")
    public void testGetDoctorByEmailFailure() {
        String email = "nonexistent@example.com";
        when(doctorRepository.findByEmail(email)).thenReturn(null);

        Doctor result = doctorServiceImpl.getDoctorByEmail(email);

        assertNull(result);
        verify(doctorRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Find patients by doctor ID - Positive Scenario")
    public void testFindPatientsByDoctorIdSuccess() {
        Long doctorId = 1L;
        List<Patient> patients = new ArrayList<>();
        when(patientRepository.findByDoctorId(doctorId)).thenReturn(patients);

        List<Patient> result = doctorServiceImpl.findPatientsByDoctorId(doctorId);

        assertNotNull(result);
        assertEquals(patients, result);
        verify(patientRepository, times(1)).findByDoctorId(doctorId);
    }

    @Test
    @DisplayName("Find patients by doctor ID - Negative Scenario")
    public void testFindPatientsByDoctorIdFailure() {
        Long doctorId = 1L;
        when(patientRepository.findByDoctorId(doctorId)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            doctorServiceImpl.findPatientsByDoctorId(doctorId);
        });
        verify(patientRepository, times(1)).findByDoctorId(doctorId);
    }

    @Test
    @DisplayName("Get doctor by ID - Positive Scenario")
    public void testGetDoctorByIdSuccess() {
        Long doctorId = 1L;
        Doctor doctor = new Doctor();
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        Optional<Doctor> result = doctorServiceImpl.getDoctorById(doctorId);

        assertTrue(result.isPresent());
        assertEquals(doctor, result.get());
        verify(doctorRepository, times(1)).findById(doctorId);
    }

    @Test
    @DisplayName("Get doctor by ID - Negative Scenario")
    public void testGetDoctorByIdFailure() {
        Long doctorId = 1L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        Optional<Doctor> result = doctorServiceImpl.getDoctorById(doctorId);

        assertFalse(result.isPresent());
        verify(doctorRepository, times(1)).findById(doctorId);
    }

    @Test
    @DisplayName("Delete doctor - Positive Scenario")
    public void testDeleteDoctorSuccess() {
        Long doctorId = 1L;
        doNothing().when(doctorRepository).deleteById(doctorId);

        doctorServiceImpl.deleteDoctor(doctorId);

        verify(doctorRepository, times(1)).deleteById(doctorId);
    }

    @Test
    @DisplayName("Delete doctor - Negative Scenario")
    public void testDeleteDoctorFailure() {
        Long doctorId = 1L;
        doThrow(new RuntimeException("Database error")).when(doctorRepository).deleteById(doctorId);

        assertThrows(RuntimeException.class, () -> {
            doctorServiceImpl.deleteDoctor(doctorId);
        });
        verify(doctorRepository, times(1)).deleteById(doctorId);
    }

    @Test
    @DisplayName("Get all doctors - Positive Scenario")
    public void testGetAllDoctorsSuccess() {
        List<Doctor> doctors = new ArrayList<>();
        when(doctorRepository.findAll()).thenReturn(doctors);

        List<Doctor> result = doctorServiceImpl.getAllDoctors();

        assertNotNull(result);
        assertEquals(doctors, result);
        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Get all doctors - Negative Scenario")
    public void testGetAllDoctorsFailure() {
        when(doctorRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            doctorServiceImpl.getAllDoctors();
        });
        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Update doctor - Positive Scenario")
    public void testUpdateDoctorSuccess() {
        Long doctorId = 1L;
        Doctor updatedDoctor = new Doctor();
        updatedDoctor.setPassword("newPassword");

        when(doctorRepository.existsById(doctorId)).thenReturn(true);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(updatedDoctor);
        when(passwordEncoder.encode("newPassword")).thenReturn("encryptedNewPassword");

        Doctor result = doctorServiceImpl.updateDoctor(doctorId, updatedDoctor);

        assertNotNull(result);
        assertEquals(updatedDoctor, result);
        verify(doctorRepository, times(1)).existsById(doctorId);
        verify(doctorRepository, times(1)).save(updatedDoctor);
    }

    @Test
    @DisplayName("Update doctor - Negative Scenario")
    public void testUpdateDoctorFailure() {
        Long doctorId = 1L;
        Doctor updatedDoctor = new Doctor();
        updatedDoctor.setPassword("newPassword");

        when(doctorRepository.existsById(doctorId)).thenReturn(false);

        assertThrows(DoctorNotFoundException.class, () -> {
            doctorServiceImpl.updateDoctor(doctorId, updatedDoctor);
        });
        verify(doctorRepository, times(1)).existsById(doctorId);
        verify(doctorRepository, never()).save(any(Doctor.class));
    }
}
