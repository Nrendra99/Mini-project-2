package org.user.app.service;

import org.user.app.entity.Appointment;
import org.user.app.entity.Doctor;
import org.user.app.entity.Medication;

import java.time.LocalDate;
import java.util.List;


public interface AppointmentService {

    /**
     * Find available appointments for a specific doctor on a specific date.
     * 
     * @param doctorId the ID of the doctor
     * @param date the date for which to find available appointments
     */
    List<Appointment> findAvailableAppointments(Long doctorId, LocalDate date);

    /**
     * Find all doctors who have any appointments on a given date.
     * 
     * @param date the date for which to find available doctors
     */
    List<Doctor> findAvailableDoctorsOnDate(LocalDate date);

    /**
     * Create half-hour appointment slots for a specific doctor for a range of dates.
     * 
     * @param doctor the doctor for whom to create appointments
     * @param startDate the start date for creating appointments
     * @param endDate the end date for creating appointments
     */
    List<Appointment> createHalfHourAppointmentsForDoctor(Doctor doctor, LocalDate startDate, LocalDate endDate);

    /**
     * Book an available appointment for a patient.
     * 
     * @param patientId the ID of the patient
     * @param availableAppointment the appointment to be booked
     * @return the booked appointment
     */
    Appointment bookAppointment(Long patientId, Appointment availableAppointment);

    /**
     * Find an appointment by its ID.
     * 
     * @param appointmentId the ID of the appointment
     */
    Appointment findAppointmentById(Long appointmentId);

    /**
     * Find appointments by patient ID and status.
     * 
     * @param patientId the ID of the patient
     * @param status the status of the appointment
     */
    List<Appointment> findByPatientIdAndStatus(Long patientId, String status);
   
    /**
     * Find appointments by appointmentId and PatientId.
     * 
     * @param appointment the ID of the Appointment
     * @param patientId the ID of the patient
     */
    Appointment cancelAppointment(Long appointmentId, Long patientId);

    /**
     * Update the status of an appointment.
     * 
     * @param appointmentId the ID of the appointment
     * @param status the new status to be updated
     */
    Appointment updateAppointmentStatus(Long appointmentId, String status);

    /**
     * View appointments for a doctor on a specific date.
     * 
     * @param doctorId the ID of the doctor
     * @param date the date for which to view appointments
     */
    List<Appointment> viewAppointments(Long doctorId, LocalDate date);
    
    Appointment save(Appointment appointment);
    
    Appointment addMeds(Long appointmentId, Medication medication);
}