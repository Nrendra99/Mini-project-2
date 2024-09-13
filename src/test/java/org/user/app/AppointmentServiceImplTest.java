package org.user.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.user.app.entity.Appointment;
import org.user.app.entity.Doctor;
import org.user.app.entity.Medication;
import org.user.app.entity.Patient;
import org.user.app.exceptions.*;

import org.user.app.repository.AppointmentRepository;
import org.user.app.repository.DoctorRepository;
import org.user.app.repository.PatientRepository;
import org.user.app.service.AppointmentServiceImpl;
import org.user.app.service.PatientServiceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
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
    private PatientServiceImpl patientServiceImpl;

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
	public void testFindAvailableDoctorsOnDate_Success() {
	    // Given: Explicitly setting the start and end date
	    LocalDate date = LocalDate.now();
	

	    // Create a mock doctor and mock appointment
	    Doctor doctor = new Doctor();
	    doctor.setId(1L); 

	    // Mock the doctor list returned from the repository
	    List<Doctor> doctorList = new ArrayList<>();
	    doctorList.add(doctor);

	    // Create a mock appointment for the doctor on the specified date
	    Appointment appointment = new Appointment();
	    appointment.setDoctor(doctor);
	    appointment.setAppointmentDate(date);
	    appointment.setAvailable(true);
	    
	    // Mock repository behavior
	    when(doctorRepository.findAll()).thenReturn(doctorList);
	    when(appointmentRepository.findByDoctorIdAndAppointmentDate(doctor.getId(), date))
	        .thenReturn(List.of(appointment)); // Returning a list of appointments

	    // When: Call the service method to find available doctors
	    List<Doctor> result = appointmentServiceImpl.findAvailableDoctorsOnDate(date);

	    // Then: Assert that a doctor is found and returned
	    assertNotNull(result);
	    assertEquals(1, result.size());
	    verify(doctorRepository).findAll();
	    verify(appointmentRepository).findByDoctorIdAndAppointmentDate(doctor.getId(), date);
	}
	
    	
    @Test
    @DisplayName("Find available doctors - No available doctors")
    public void testFindAvailableDoctorsOnDate_NoDoctors() {
        // Given: Setting up an empty doctor list for the test
        LocalDate date = LocalDate.now();
        List<Doctor> doctorList = new ArrayList<>();

        // Mocking the doctor repository to return no doctors
        when(doctorRepository.findAll()).thenReturn(doctorList);

        // When/Then: Verifying that an exception is thrown when no doctors are available
        NoAvailableDoctorsException exception = assertThrows(NoAvailableDoctorsException.class, () -> {
            appointmentServiceImpl.findAvailableDoctorsOnDate(date);
        });

        // Then: Asserting that the exception message matches the expected output
        assertEquals("No doctors available on " + date, exception.getMessage());
    }

    @Test
    @DisplayName("Find available appointments - Success")
    public void testFindAvailableAppointments_Success() {
        // Given: Setting up a date and a list of available appointments
        LocalDate date = LocalDate.now();
        List<Appointment> availableAppointments = List.of(new Appointment());

        // Mocking the appointment repository to return available appointments for a given doctor and date
        when(appointmentRepository.findByDoctorIdAndAppointmentDateAndIsAvailable(anyLong(), eq(date), eq(true)))
            .thenReturn(availableAppointments);

        // When: Calling the service method to find available appointments
        List<Appointment> result = appointmentServiceImpl.findAvailableAppointments(1L, date);

        // Then: Verifying the result is not null and has the expected size
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository).findByDoctorIdAndAppointmentDateAndIsAvailable(1L, date, true);
    }

    @Test
    @DisplayName("Find available appointments - No available appointments")
    public void testFindAvailableAppointments_NoAppointments() {
        // Given: Setting up a date for the test
        LocalDate date = LocalDate.now();

        // Mocking the appointment repository to return no available appointments
        when(appointmentRepository.findByDoctorIdAndAppointmentDateAndIsAvailable(anyLong(), eq(date), eq(true)))
            .thenReturn(new ArrayList<>());

        // When/Then: Verifying that an exception is thrown when no appointments are available
        NoAvailableAppointmentsException exception = assertThrows(NoAvailableAppointmentsException.class, () -> {
            appointmentServiceImpl.findAvailableAppointments(1L, date);
        });

        // Then: Asserting the exception message matches the expected output
        assertEquals("No available appointments for doctor ID 1 on " + date, exception.getMessage());
    }

    @Test
    @DisplayName("Book appointment - Success")
    public void testBookAppointment_Success() {
        // Given: Setting up a patient and an appointment with symptoms
        Patient patient = new Patient();
        Appointment appointment = new Appointment();
        appointment.setSymptoms("Cough");

        // Mocking the patient repository to return the patient and the appointment repository to save the appointment
        when(patientRepository.findById(anyLong())).thenReturn(Optional.of(patient));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        // When: Booking the appointment through the service
        Appointment result = appointmentServiceImpl.bookAppointment(1L, appointment);

        // Then: Verifying that the result is not null and the symptoms match
        assertNotNull(result);
        assertEquals("Cough", result.getSymptoms());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    @DisplayName("Book appointment - Patient not found")
    public void testBookAppointment_PatientNotFound() {
        // Given: Mocking the patient repository to return empty (patient not found)
        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then: Verifying that a PatientNotFoundException is thrown
        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class, () -> {
            appointmentServiceImpl.bookAppointment(1L, new Appointment());
        });

        // Then: Asserting the exception message matches the expected output
        assertEquals("Patient not found with ID: 1", exception.getMessage());
    }

    @Test
    @DisplayName("Cancel appointment - Success")
    void testCancelAppointment_Success() {
        // Given: Setting up an appointment and medication
        Appointment appointment = new Appointment();
        appointment.setId(1L);  // Set the ID for the appointment
        appointment.setAppointmentDate(LocalDate.now().plusDays(1));
        appointment.setStartTime(LocalTime.of(10, 0));
        appointment.setStatus("BOOKED");
        appointment.setMedications(new HashSet<>());  // Initialize the medications set

        // Mock the repository to return the appointment when searched by ID
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(appointment));
        when(appointmentRepository.findByIdAndPatientId(anyLong(), anyLong())).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        

        // When: Canceling the appointment
        Appointment result = appointmentServiceImpl.cancelAppointment(1L, 1L);

        // Then: Verifying the status change and that the patient is null after cancellation
        assertEquals("AVAILABLE", result.getStatus());
        assertNull(result.getPatient());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    @DisplayName("Cancel appointment - Cannot cancel within 4 hours")
    public void testCancelAppointment_CannotCancelWithinFourHours() {
        // Given: Setting up an appointment within 4 hours
        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(LocalDate.now());
        appointment.setStartTime(LocalTime.now().plusHours(3));

        // Mocking the appointment repository to return the appointment
        when(appointmentRepository.findByIdAndPatientId(anyLong(), anyLong())).thenReturn(Optional.of(appointment));

        // When/Then: Verifying that a CannotCancelWithinFourHoursException is thrown
        CannotCancelWithinFourHoursException exception = assertThrows(CannotCancelWithinFourHoursException.class, () -> {
            appointmentServiceImpl.cancelAppointment(1L, 1L);
        });

        // Then: Asserting the exception message matches the expected output
        assertEquals("Cannot cancel within 4 hours of the appointment.", exception.getMessage());
    }

    @Test
    @DisplayName("View appointments by doctor and date - Success")
    public void testViewAppointments_Success() {
        // Given: Setting up a date and a list of appointments
        LocalDate date = LocalDate.now();
        List<Appointment> appointments = List.of(new Appointment());

        // Mocking the appointment repository to return appointments for the doctor and date
        when(appointmentRepository.findByDoctorIdAndAppointmentDate(anyLong(), eq(date))).thenReturn(appointments);

        // When: Viewing the appointments through the service
        List<Appointment> result = appointmentServiceImpl.viewAppointments(1L, date);

        // Then: Verifying the result is not null and has the expected size
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository).findByDoctorIdAndAppointmentDate(1L, date);
    }

    @Test
    @DisplayName("View appointments by doctor and date - No appointments found")
    public void testViewAppointments_NoAppointments() {
        // Given: Setting up a date for the test
        LocalDate date = LocalDate.now();

        // Mocking the appointment repository to return no appointments
        when(appointmentRepository.findByDoctorIdAndAppointmentDate(anyLong(), eq(date))).thenReturn(new ArrayList<>());

        // When/Then: Verifying that an AppointmentNotFoundException is thrown
        AppointmentNotFoundException exception = assertThrows(AppointmentNotFoundException.class, () -> {
            appointmentServiceImpl.viewAppointments(1L, date);
        });

        // Then: Asserting the exception message matches the expected output
        assertEquals("No appointments found for doctor ID: 1 on " + date, exception.getMessage());
    }

    @Test
    @DisplayName("Find appointment by ID - Success")
    public void testFindAppointmentById_Success() {
        // Given: Setting up an appointment for the test
        Appointment appointment = new Appointment();
        
        // Mocking the appointment repository to return the appointment by ID
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(appointment));

        // When: Finding the appointment by ID through the service
        Appointment result = appointmentServiceImpl.findAppointmentById(1L);

        // Then: Verifying the result is not null
        assertNotNull(result);
        verify(appointmentRepository).findById(1L);
    }

    @Test
    @DisplayName("Find appointment by ID - Appointment not found")
    public void testFindAppointmentById_NotFound() {
        // Given: Mocking the appointment repository to return empty (appointment not found)
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then: Verifying that an AppointmentNotFoundException is thrown
        AppointmentNotFoundException exception = assertThrows(AppointmentNotFoundException.class, () -> {
            appointmentServiceImpl.findAppointmentById(1L);
        });

        // Then: Asserting the exception message matches the expected output
        assertEquals("Appointment not found with ID: 1", exception.getMessage());
    }
    
    @Test
    @DisplayName("Update appointment status - Success")
    void testUpdateAppointmentStatus_Success() {
        // Given: Create an appointment with an initial status
        Appointment appointment = new Appointment();
        appointment.setStatus("PENDING");

        // Mock the repository to return the appointment when its ID is queried
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(appointment));
        // Mock the repository to return the same appointment when saving
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        // When: Call the service method to update the appointment status
        Appointment result = appointmentServiceImpl.updateAppointmentStatus(1L, "CONFIRMED");

        // Then: Verify that the result is not null and the status has been updated
        assertNotNull(result);
        assertEquals("CONFIRMED", result.getStatus());
        // Verify that the repository's save method was called once with the updated appointment
        verify(appointmentRepository).save(appointment);
    }

    @Test
    @DisplayName("Update appointment status - Appointment not found")
    void testUpdateAppointmentStatus_AppointmentNotFound() {
        // Mock the repository to return an empty result for a non-existent appointment
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then: Ensure that an AppointmentNotFoundException is thrown
        AppointmentNotFoundException exception = assertThrows(AppointmentNotFoundException.class, () -> {
            appointmentServiceImpl.updateAppointmentStatus(1L, "CONFIRMED");
        });

        // Assert that the exception message matches the expected message
        assertEquals("Appointment not found with ID: 1", exception.getMessage());
    }

    @Test
    @DisplayName("Save appointment successfully")
    public void testSaveAppointmentSuccess() {
        // Given: Create an appointment to be saved
        Appointment appointment = new Appointment();
        // Mock the repository to return the same appointment when saving
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        
        // When: Call the service method to save the appointment
        Appointment savedAppointment = appointmentServiceImpl.save(appointment);
        
        // Then: Verify that the saved appointment is not null
        assertNotNull(savedAppointment);
        // Verify that the repository's save method was called once with the appointment
        verify(appointmentRepository, times(1)).save(appointment);
    }

    @Test
    @DisplayName("Add medication to appointment successfully")
    public void testAddMedsSuccess() {
        // Given: Create an appointment and initialize the medications set
        Appointment appointment = new Appointment();
        appointment.setMedications(new HashSet<>()); // Initialize medications set
        Medication medication = new Medication();
        
        // Mock the repository to return the appointment when its ID is queried
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        // Mock the repository to return the same appointment when saving
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        
        // When: Call the service method to add medication to the appointment
        Appointment updatedAppointment = appointmentServiceImpl.addMeds(1L, medication);
        
        // Then: Verify that the result is not null and the medication has been added
        assertNotNull(updatedAppointment);
        assertTrue(updatedAppointment.getMedications().contains(medication)); // Check if medication was added
        // Verify that the repository's findById method was called once
        verify(appointmentRepository, times(1)).findById(1L);
        // Verify that the repository's save method was called once with the updated appointment
        verify(appointmentRepository, times(1)).save(appointment);
    }

    @Test
    @DisplayName("Add medication failure when appointment not found")
    public void testAddMedsFailure_AppointmentNotFound() {
        // Given: Create a medication to be added
        Medication medication = new Medication();
        
        // Mock the repository to return an empty result for a non-existent appointment
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When / Then: Ensure that an AppointmentNotFoundException is thrown
        assertThrows(AppointmentNotFoundException.class, () -> appointmentServiceImpl.addMeds(1L, medication));
        // Verify that the repository's findById method was called once
        verify(appointmentRepository, times(1)).findById(1L);
        // Verify that the repository's save method was not called
        verify(appointmentRepository, times(0)).save(any(Appointment.class));
    }
}


