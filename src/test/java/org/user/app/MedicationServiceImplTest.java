package org.user.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.user.app.entity.Appointment;
import org.user.app.entity.Medication;
import org.user.app.exceptions.AppointmentNotFoundException;
import org.user.app.exceptions.MedicationNotFoundException;
import org.user.app.repository.AppointmentRepository;
import org.user.app.repository.MedicationRepository;
import org.user.app.service.MedicationServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MedicationServiceImplTest {

    @InjectMocks
    private MedicationServiceImpl medicationServiceImpl;

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    private Medication medication;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Initialize test data
        appointment = new Appointment();
        appointment.setId(1L);

        medication = new Medication();
        medication.setId(1L);
        medication.setAppointment(appointment);
    }

    @Test
    void addMedicationToAppointment_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(medicationRepository.save(medication)).thenReturn(medication);

        medicationServiceImpl.addMedicationToAppointment(1L, medication);

        verify(medicationRepository, times(1)).save(medication);
    }

    @Test
    void addMedicationToAppointment_AppointmentNotFound() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(AppointmentNotFoundException.class, () -> {
            medicationServiceImpl.addMedicationToAppointment(1L, medication);
        });

        assertEquals("Appointment not found", exception.getMessage());
        verify(medicationRepository, times(0)).save(medication);
    }

    @Test
    void updateMedication_Success() {
        when(medicationRepository.existsById(1L)).thenReturn(true);
        when(medicationRepository.save(medication)).thenReturn(medication);

        Medication updatedMedication = medicationServiceImpl.updateMedication(medication);

        assertNotNull(updatedMedication);
        verify(medicationRepository, times(1)).save(medication);
    }

    @Test
    void updateMedication_NotFound() {
        when(medicationRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(MedicationNotFoundException.class, () -> {
            medicationServiceImpl.updateMedication(medication);
        });

        assertEquals("Medication with ID 1 not found", exception.getMessage());
        verify(medicationRepository, times(0)).save(medication);
    }

    @Test
    void removeMedication_Success() {
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));

        medicationServiceImpl.removeMedication(1L);

        verify(medicationRepository, times(1)).delete(medication);
    }

    @Test
    void removeMedication_NotFound() {
        when(medicationRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(MedicationNotFoundException.class, () -> {
            medicationServiceImpl.removeMedication(1L);
        });

        assertEquals("Medication with ID 1 not found", exception.getMessage());
        verify(medicationRepository, times(0)).delete(medication);
    }

    @Test
    void getMedications_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(medicationRepository.findByAppointmentId(1L)).thenReturn(List.of(medication));

        var medications = medicationServiceImpl.getMedications(1L);

        assertFalse(medications.isEmpty());
        verify(medicationRepository, times(1)).findByAppointmentId(1L);
    }

    @Test
    void getMedications_AppointmentNotFound() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(AppointmentNotFoundException.class, () -> {
            medicationServiceImpl.getMedications(1L);
        });

        assertEquals("Appointment with ID 1 not found", exception.getMessage());
        verify(medicationRepository, times(0)).findByAppointmentId(1L);
    }

    @Test
    void getMedicationById_Success() {
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));

        var retrievedMedication = medicationServiceImpl.getMedicationById(1L);

        assertNotNull(retrievedMedication);
        assertEquals(medication.getId(), retrievedMedication.getId());
    }

    @Test
    void getMedicationById_NotFound() {
        when(medicationRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(MedicationNotFoundException.class, () -> {
            medicationServiceImpl.getMedicationById(1L);
        });

        assertEquals("Medication with ID 1 not found", exception.getMessage());
    }
}