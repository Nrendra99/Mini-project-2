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
    void updateMedication_Success() {
        // Given: Mock the repository to return true for the existence check and return the medication on save
        when(medicationRepository.existsById(1L)).thenReturn(true);
        when(medicationRepository.save(medication)).thenReturn(medication);

        // When: Call the service method to update the medication
        Medication updatedMedication = medicationServiceImpl.updateMedication(medication);

        // Then: Verify that the updated medication is not null and that the repository's save method was called once
        assertNotNull(updatedMedication);
        verify(medicationRepository, times(1)).save(medication);
    }

    @Test
    void updateMedication_NotFound() {
        // Given: Mock the repository to return false for the existence check (medication not found)
        when(medicationRepository.existsById(1L)).thenReturn(false);

        // When / Then: Ensure that a MedicationNotFoundException is thrown
        Exception exception = assertThrows(MedicationNotFoundException.class, () -> {
            medicationServiceImpl.updateMedication(medication);
        });

        // Assert that the exception message matches the expected message
        assertEquals("Medication with ID 1 not found", exception.getMessage());
        // Verify that the repository's save method was not called
        verify(medicationRepository, times(0)).save(medication);
    }

    @Test
    void removeMedication_Success() {
        // Given: Mock the repository to return the medication when queried by ID
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));

        // When: Call the service method to remove the medication
        medicationServiceImpl.removeMedication(1L);

        // Then: Verify that the repository's delete method was called once with the medication
        verify(medicationRepository, times(1)).delete(medication);
    }

    @Test
    void removeMedication_NotFound() {
        // Given: Mock the repository to return an empty result for a non-existent medication
        when(medicationRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then: Ensure that a MedicationNotFoundException is thrown
        Exception exception = assertThrows(MedicationNotFoundException.class, () -> {
            medicationServiceImpl.removeMedication(1L);
        });

        // Assert that the exception message matches the expected message
        assertEquals("Medication with ID 1 not found", exception.getMessage());
        // Verify that the repository's delete method was not called
        verify(medicationRepository, times(0)).delete(medication);
    }

    @Test
    void getMedications_Success() {
        // Given: Mock the repositories to return an appointment and a list of medications
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(medicationRepository.findByAppointmentId(1L)).thenReturn(List.of(medication));

        // When: Call the service method to retrieve medications for the appointment
        var medications = medicationServiceImpl.getMedications(1L);

        // Then: Verify that the list of medications is not empty and that the repository's findByAppointmentId method was called once
        assertFalse(medications.isEmpty());
        verify(medicationRepository, times(1)).findByAppointmentId(1L);
    }

    @Test
    void getMedications_AppointmentNotFound() {
        // Given: Mock the repository to return an empty result for a non-existent appointment
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then: Ensure that an AppointmentNotFoundException is thrown
        Exception exception = assertThrows(AppointmentNotFoundException.class, () -> {
            medicationServiceImpl.getMedications(1L);
        });

        // Assert that the exception message matches the expected message
        assertEquals("Appointment with ID 1 not found", exception.getMessage());
        // Verify that the repository's findByAppointmentId method was not called
        verify(medicationRepository, times(0)).findByAppointmentId(1L);
    }

    @Test
    void getMedicationById_Success() {
        // Given: Mock the repository to return the medication when queried by ID
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));

        // When: Call the service method to retrieve the medication by ID
        var retrievedMedication = medicationServiceImpl.getMedicationById(1L);

        // Then: Verify that the retrieved medication is not null and its ID matches the expected ID
        assertNotNull(retrievedMedication);
        assertEquals(medication.getId(), retrievedMedication.getId());
    }

    @Test
    void getMedicationById_NotFound() {
        // Given: Mock the repository to return an empty result for a non-existent medication
        when(medicationRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then: Ensure that a MedicationNotFoundException is thrown
        Exception exception = assertThrows(MedicationNotFoundException.class, () -> {
            medicationServiceImpl.getMedicationById(1L);
        });

        // Assert that the exception message matches the expected message
        assertEquals("Medication with ID 1 not found", exception.getMessage());
    }
}