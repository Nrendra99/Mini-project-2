package org.user.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.user.app.entity.Appointment;
import org.user.app.entity.Doctor;
import org.user.app.entity.Patient;
import org.user.app.exceptions.AppointmentNotFoundException;
import org.user.app.exceptions.CannotCancelWithinFourHoursException;
import org.user.app.exceptions.NoAvailableAppointmentsException;
import org.user.app.exceptions.NoAvailableDoctorsException;
import org.user.app.exceptions.PatientNotFoundException;
import org.user.app.repository.AppointmentRepository;
import org.user.app.repository.DoctorRepository;
import org.user.app.repository.PatientRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;
   
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    

    /**
     * Find all doctors who have any appointments on a given date.
     *
     * @param date the date for which to find available doctors
     * @return a list of doctors with appointments on the given date
     */
    @Override
    public List<Doctor> findAvailableDoctorsOnDate(LocalDate date) {
        List<Doctor> doctors = doctorRepository.findAll().stream()
                .filter(doctor -> !appointmentRepository.findByDoctorIdAndAppointmentDate(doctor.getId(), date).isEmpty())
                .collect(Collectors.toList());

        if (doctors.isEmpty()) {
            throw new NoAvailableDoctorsException("No doctors available on " + date);
        }
        return doctors;
    }


    /**
     * Find available appointments for a specific doctor on a specific date.
     *
     * @param doctorId the ID of the doctor
     * @param date the date for which to find available appointments
     * @return a list of available appointments for the doctor on the given date
     */
    @Override
    public List<Appointment> findAvailableAppointments(Long doctorId, LocalDate date) {
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentDateAndIsAvailable(doctorId, date, true);
        if (appointments.isEmpty()) {
            throw new NoAvailableAppointmentsException("No available appointments for doctor ID " + doctorId + " on " + date);
        }
        return appointments;
    }
    /**
     * Create half-hour appointment slots for a specific doctor within a date range.
     *
     * @param doctor the doctor for whom to create appointments
     * @param startDate the start date for creating appointments
     * @param endDate the end date for creating appointments
     * @return a list of created appointments
     */
    @Transactional
    @Override
    public List<Appointment> createHalfHourAppointmentsForDoctor(Doctor doctor, LocalDate startDate, LocalDate endDate) {
        List<Appointment> appointments = new ArrayList<>();
        LocalTime startTime = LocalTime.of(9, 0); // Start at 9:00 AM
        LocalTime endTime = LocalTime.of(19, 0); // End at 7:00 PM

        while (!startDate.isAfter(endDate)) {
            LocalTime currentStartTime = startTime;

            while (currentStartTime.isBefore(endTime)) {
                Appointment appointment = Appointment.builder()
                        .doctor(doctor)
                        .appointmentDate(startDate)
                        .startTime(currentStartTime)
                        .endTime(currentStartTime.plusMinutes(30))
                        .isAvailable(true)
                        .status("AVAILABLE")
                        .build();

                appointments.add(appointment);
                currentStartTime = currentStartTime.plusMinutes(30);
            }

            startDate = startDate.plusDays(1);
        }

        return appointmentRepository.saveAll(appointments);
    }

    /**
     * Book an available appointment for a patient.
     *
     * @param patientId the ID of the patient
     * @param availableAppointment the appointment to be booked
     * @return the booked appointment
     */
    @Transactional
    @Override
    public Appointment bookAppointment(Long patientId, Appointment availableAppointment) {
        // Fetch patient and ensure they exist
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + patientId));
        
        // Set appointment details
        availableAppointment.setPatient(patient);
        availableAppointment.setAvailable(false);
        availableAppointment.setStatus("BOOKED");
        availableAppointment.setSymptoms(availableAppointment.getSymptoms());
 
        return appointmentRepository.save(availableAppointment);
    }
    /**
     * Find an appointment by its ID.
     *
     * @param appointmentId the ID of the appointment
     * @return the appointment with the given ID
     */
    @Override
    public Appointment findAppointmentById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with ID: " + appointmentId));
    }
    
    /**
     * Find appointments by patient ID and status.
     *
     * @param patientId the ID of the patient
     * @param status the status of the appointment
     * @return a list of appointments with the given status for the patient
     */
    @Override
    public List<Appointment> findByPatientIdAndStatus(Long patientId, String status) {
        List<Appointment> appointments = appointmentRepository.findByPatientIdAndStatus(patientId, status);
        if (appointments.isEmpty()) {
            throw new AppointmentNotFoundException("No appointments found for patient ID: " + patientId + " with status: " + status);
        }
        return appointments;
    }   

    /**
     * Cancel an appointment.
     *
     * @param appointmentId the ID of the appointment
     * @param patientId     the ID of the patient requesting the cancellation
     * @return the updated appointment
     * @throws IllegalArgumentException if the appointment cannot be found or if cancellation is not allowed
     */
    @Transactional
    @Override
    public Appointment cancelAppointment(Long appointmentId, Long patientId) {
        Appointment appointment = appointmentRepository.findByIdAndPatientId(appointmentId, patientId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found or does not belong to the patient with ID: " + patientId));

        LocalDateTime appointmentDateTime = LocalDateTime.of(appointment.getAppointmentDate(), appointment.getStartTime());
        LocalDateTime now = LocalDateTime.now();

        if (appointmentDateTime.isBefore(now.plusHours(4))) {
            throw new CannotCancelWithinFourHoursException("Cannot cancel within 4 hours of the appointment.");
        }

        appointment.setStatus("AVAILABLE");
        appointment.setAvailable(true);
        appointment.setPatient(null);
        appointment.setSymptoms(null);
        appointment.setMedications(null);

        return appointmentRepository.save(appointment);
    }

  
    /**
     * Update the status of an appointment.
     *
     * @param appointmentId the ID of the appointment
     * @param status the new status to be updated
     * @return the updated appointment
     */
    @Transactional
    @Override
    public Appointment updateAppointmentStatus(Long appointmentId, String status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with ID: " + appointmentId));

        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }
    /**
     * View appointments for a doctor on a specific date.
     *
     * @param doctorId the ID of the doctor
     * @param date the date for which to view appointments
     * @return a list of appointments for the doctor on the specified date
     */
    @Override
    public List<Appointment> viewAppointments(Long doctorId, LocalDate date) {
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, date);
        if (appointments.isEmpty()) {
            throw new AppointmentNotFoundException("No appointments found for doctor ID: " + doctorId + " on " + date);
        }
        return appointments;
    }
    
   
}

        
    
   	