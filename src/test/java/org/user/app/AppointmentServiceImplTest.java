package org.user.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.user.app.entity.Appointment;
import org.user.app.entity.Doctor;
import org.user.app.entity.Patient;
import org.user.app.exceptions.*;

import org.user.app.repository.AppointmentRepository;
import org.user.app.repository.DoctorRepository;
import org.user.app.repository.PatientRepository;
import org.user.app.service.AppointmentServiceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceImplTest {

    @InjectMocks
    private AppointmentServiceImpl appointmentServiceImpl;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Find available doctors - Success")
    void testFindAvailableDoctorsOnDate_Success() {
        LocalDate date = LocalDate.now();
        Doctor doctor = new Doctor();
        List<Doctor> doctorList = new ArrayList<>();
        doctorList.add(doctor);

        when(doctorRepository.findAll()).thenReturn(doctorList);
        when(appointmentRepository.findByDoctorIdAndAppointmentDate(anyLong(), eq(date))).thenReturn(List.of(new Appointment()));

        List<Doctor> result = appointmentServiceImpl.findAvailableDoctorsOnDate(date);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(doctorRepository).findAll();
    }

    @Test
    @DisplayName("Find available doctors - No available doctors")
    void testFindAvailableDoctorsOnDate_NoDoctors() {
        LocalDate date = LocalDate.now();
        List<Doctor> doctorList = new ArrayList<>();

        when(doctorRepository.findAll()).thenReturn(doctorList);

        NoAvailableDoctorsException exception = assertThrows(NoAvailableDoctorsException.class, () -> {
            appointmentServiceImpl.findAvailableDoctorsOnDate(date);
        });

        assertEquals("No doctors available on " + date, exception.getMessage());
    }

    @Test
    @DisplayName("Find available appointments - Success")
    void testFindAvailableAppointments_Success() {
        LocalDate date = LocalDate.now();
        List<Appointment> availableAppointments = List.of(new Appointment());

        when(appointmentRepository.findByDoctorIdAndAppointmentDateAndIsAvailable(anyLong(), eq(date), eq(true)))
                .thenReturn(availableAppointments);

        List<Appointment> result = appointmentServiceImpl.findAvailableAppointments(1L, date);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository).findByDoctorIdAndAppointmentDateAndIsAvailable(1L, date, true);
    }

    @Test
    @DisplayName("Find available appointments - No available appointments")
    void testFindAvailableAppointments_NoAppointments() {
        LocalDate date = LocalDate.now();

        when(appointmentRepository.findByDoctorIdAndAppointmentDateAndIsAvailable(anyLong(), eq(date), eq(true)))
                .thenReturn(new ArrayList<>());

        NoAvailableAppointmentsException exception = assertThrows(NoAvailableAppointmentsException.class, () -> {
            appointmentServiceImpl.findAvailableAppointments(1L, date);
        });

        assertEquals("No available appointments for doctor ID 1 on " + date, exception.getMessage());
    }

    @Test
    @DisplayName("Book appointment - Success")
    void testBookAppointment_Success() {
        Patient patient = new Patient();
        Appointment appointment = new Appointment();
        appointment.setSymptoms("Cough");

        when(patientRepository.findById(anyLong())).thenReturn(Optional.of(patient));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentServiceImpl.bookAppointment(1L, appointment);

        assertNotNull(result);
        assertEquals("Cough", result.getSymptoms());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    @DisplayName("Book appointment - Patient not found")
    void testBookAppointment_PatientNotFound() {
        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class, () -> {
            appointmentServiceImpl.bookAppointment(1L, new Appointment());
        });

        assertEquals("Patient not found with ID: 1", exception.getMessage());
    }

    @Test
    @DisplayName("Cancel appointment - Success")
    void testCancelAppointment_Success() {
        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(LocalDate.now().plusDays(1));
        appointment.setStartTime(LocalTime.of(10, 0));

        when(appointmentRepository.findByIdAndPatientId(anyLong(), anyLong())).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentServiceImpl.cancelAppointment(1L, 1L);

        assertEquals("AVAILABLE", result.getStatus());
        assertNull(result.getPatient());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    @DisplayName("Cancel appointment - Cannot cancel within 4 hours")
    void testCancelAppointment_CannotCancelWithinFourHours() {
        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(LocalDate.now());
        appointment.setStartTime(LocalTime.now().plusHours(3));

        when(appointmentRepository.findByIdAndPatientId(anyLong(), anyLong())).thenReturn(Optional.of(appointment));

        CannotCancelWithinFourHoursException exception = assertThrows(CannotCancelWithinFourHoursException.class, () -> {
            appointmentServiceImpl.cancelAppointment(1L, 1L);
        });

        assertEquals("Cannot cancel within 4 hours of the appointment.", exception.getMessage());
    }

    @Test
    @DisplayName("View appointments by doctor and date - Success")
    void testViewAppointments_Success() {
        LocalDate date = LocalDate.now();
        List<Appointment> appointments = List.of(new Appointment());

        when(appointmentRepository.findByDoctorIdAndAppointmentDate(anyLong(), eq(date))).thenReturn(appointments);

        List<Appointment> result = appointmentServiceImpl.viewAppointments(1L, date);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository).findByDoctorIdAndAppointmentDate(1L, date);
    }

    @Test
    @DisplayName("View appointments by doctor and date - No appointments found")
    void testViewAppointments_NoAppointments() {
        LocalDate date = LocalDate.now();

        when(appointmentRepository.findByDoctorIdAndAppointmentDate(anyLong(), eq(date))).thenReturn(new ArrayList<>());

        AppointmentNotFoundException exception = assertThrows(AppointmentNotFoundException.class, () -> {
            appointmentServiceImpl.viewAppointments(1L, date);
        });

        assertEquals("No appointments found for doctor ID: 1 on " + date, exception.getMessage());
    }

    @Test
    @DisplayName("Find appointment by ID - Success")
    void testFindAppointmentById_Success() {
        Appointment appointment = new Appointment();
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(appointment));

        Appointment result = appointmentServiceImpl.findAppointmentById(1L);

        assertNotNull(result);
        verify(appointmentRepository).findById(1L);
    }

    @Test
    @DisplayName("Find appointment by ID - Appointment not found")
    void testFindAppointmentById_NotFound() {
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        AppointmentNotFoundException exception = assertThrows(AppointmentNotFoundException.class, () -> {
            appointmentServiceImpl.findAppointmentById(1L);
        });

        assertEquals("Appointment not found with ID: 1", exception.getMessage());
    }

    @Test
    @DisplayName("Update appointment status - Success")
    void testUpdateAppointmentStatus_Success() {
        Appointment appointment = new Appointment();
        appointment.setStatus("PENDING");

        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentServiceImpl.updateAppointmentStatus(1L, "CONFIRMED");

        assertNotNull(result);
        assertEquals("CONFIRMED", result.getStatus());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    @DisplayName("Update appointment status - Appointment not found")
    void testUpdateAppointmentStatus_AppointmentNotFound() {
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        AppointmentNotFoundException exception = assertThrows(AppointmentNotFoundException.class, () -> {
            appointmentServiceImpl.updateAppointmentStatus(1L, "CONFIRMED");
        });

        assertEquals("Appointment not found with ID: 1", exception.getMessage());
    }
}


