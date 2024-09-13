package org.user.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.user.app.entity.Appointment;
import org.user.app.entity.Doctor;
import org.user.app.entity.Patient;
import org.user.app.repository.DoctorRepository;
import org.user.app.service.AppointmentServiceImpl;
import org.user.app.service.DoctorServiceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DoctorServiceImplTest {

    @InjectMocks
    private DoctorServiceImpl doctorServiceImpl;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AppointmentServiceImpl appointmentServiceImpl;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Register a new doctor")
    public void testRegisterDoctorSuccess() {
        // Given
        Doctor doctor = new Doctor();
        doctor.setPassword("password");
        Doctor savedDoctor = new Doctor();
        savedDoctor.setId(1L);

        // Mock data for appointments
        List<Appointment> mockAppointments = new ArrayList<>();
        Appointment mockAppointment = Appointment.builder()
                .doctor(savedDoctor)
                .appointmentDate(LocalDate.now())
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(9, 30))
                .isAvailable(true)
                .status("AVAILABLE")
                .build();
        mockAppointments.add(mockAppointment);

        // Stubbing for createHalfHourAppointmentsForDoctor method
        when(appointmentServiceImpl.createHalfHourAppointmentsForDoctor(any(Doctor.class), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(mockAppointments);

        when(doctorRepository.save(any(Doctor.class))).thenReturn(savedDoctor);
        when(passwordEncoder.encode("password")).thenReturn("encryptedPassword");

        // When
        Doctor result = doctorServiceImpl.registerDoctor(doctor);

        // Then
        assertNotNull(result);
        assertEquals(savedDoctor.getId(), result.getId());
        verify(doctorRepository, times(1)).save(doctor);
        verify(appointmentServiceImpl, times(1)).createHalfHourAppointmentsForDoctor(savedDoctor, LocalDate.now(), LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()));
    }
    
    @Test
    @DisplayName("Get doctor by email")
    public void testGetDoctorByEmailSuccess() {
        // Given
        String email = "test@example.com";
        Doctor doctor = new Doctor();
        when(doctorRepository.findByEmail(email)).thenReturn(doctor);

        // When
        Doctor result = doctorServiceImpl.getDoctorByEmail(email);

        // Then
        assertNotNull(result);
        verify(doctorRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Find patients associated with a doctor")
    public void testFindPatients() {
        // Given
        Set<Patient> patients = new HashSet<>();
        patients.add(new Patient(1L, "John Doe", null, 0, null, null, null, null, null, null));
        patients.add(new Patient(2L, "Jane Smith", null, 0, null, null, null, null, null, null));
        
        // Create a mock Doctor object
        Doctor doctor = mock(Doctor.class);
        when(doctor.getPatients()).thenReturn(patients);

        // When
        Set<Patient> result = doctorServiceImpl.findPatients(doctor);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getFirstName().equals("John Doe")));
        assertTrue(result.stream().anyMatch(p -> p.getFirstName().equals("Jane Smith")));
        verify(doctor, times(1)).getPatients();
    }

    @Test
    @DisplayName("Get doctor by ID")
    public void testGetDoctorByIdSuccess() {
        // Given
        Long doctorId = 1L;
        Doctor doctor = new Doctor();
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        // When
        Optional<Doctor> result = doctorServiceImpl.getDoctorById(doctorId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(doctor, result.get());
        verify(doctorRepository, times(1)).findById(doctorId);
    }

    @Test
    @DisplayName("Delete doctor by ID")
    public void testDeleteDoctorSuccess() {
        // Given
        Long doctorId = 1L;
        doNothing().when(doctorRepository).deleteById(doctorId);

        // When
        doctorServiceImpl.deleteDoctor(doctorId);

        // Then
        verify(doctorRepository, times(1)).deleteById(doctorId);
    }

    @Test
    @DisplayName("Get all doctors")
    public void testGetAllDoctorsSuccess() {
        // Given
        List<Doctor> doctors = new ArrayList<>();
        when(doctorRepository.findAll()).thenReturn(doctors);

        // When
        List<Doctor> result = doctorServiceImpl.getAllDoctors();

        // Then
        assertNotNull(result);
        assertEquals(doctors, result);
        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Update doctor by ID")
    public void testUpdateDoctorSuccess() {
        // Given
        Long doctorId = 1L;
        Doctor updatedDoctor = new Doctor();
        updatedDoctor.setPassword("newPassword");

        when(doctorRepository.existsById(doctorId)).thenReturn(true);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(updatedDoctor);
        when(passwordEncoder.encode("newPassword")).thenReturn("encryptedNewPassword");

        // When
        Doctor result = doctorServiceImpl.updateDoctor(doctorId, updatedDoctor);

        // Then
        assertNotNull(result);
        assertEquals("encryptedNewPassword", result.getPassword());
        verify(doctorRepository, times(1)).save(updatedDoctor);
    }
}