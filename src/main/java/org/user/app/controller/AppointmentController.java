package org.user.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.user.app.entity.Appointment;
import org.user.app.entity.Doctor;
import org.user.app.entity.Medication;
import org.user.app.entity.Patient;
import org.user.app.exceptions.*;
import org.user.app.service.*;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping("/appointments")
@Tag(name = "Appointments", description = "Operations related to appointment management")
public class AppointmentController {

    @Autowired
    private AppointmentServiceImpl appointmentServiceImpl;
    
    @Autowired
    private PatientServiceImpl patientServiceImpl;
    
    @Autowired
    private DoctorServiceImpl doctorServiceImpl;
    
    @Autowired
    private MedicationServiceImpl medicationServiceImpl;

    /**
     * Show form to select a date for available doctors.
     *
     * @param model Model for Thymeleaf
     * @return Thymeleaf template for available doctors
     */
    @GetMapping("/availableDoctors")
    @Operation(summary = "Show the form to select a date for available doctors")
    public String showAvailableDoctorsForm(Model model) {
        model.addAttribute("date", LocalDate.now()); 
        return "availableDoctors";
    }

    /**
     * View available doctors on a specific date.
     *
     * @param date Date to check for available doctors
     * @param model Model for Thymeleaf
     * @return Thymeleaf template for available doctors
     */
    @GetMapping("/availableDoctors/results")
    @Operation(summary = "Get available doctors on a specific date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Doctors found"),
        @ApiResponse(responseCode = "400", description = "Invalid date format")
    })
    public String viewAvailableDoctors(
            @RequestParam @Parameter(description = "Date to check for available doctors") LocalDate date, 
            Model model) {
        try {
            List<Doctor> doctors = appointmentServiceImpl.findAvailableDoctorsOnDate(date);
            model.addAttribute("doctors", doctors);
            model.addAttribute("date", date);
        } catch (NoAvailableDoctorsException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "availableDoctors";
    }

    /**
     * View available appointments for a doctor on a given date.
     *
     * @param doctorId ID of the doctor
     * @param date Date to check for available appointments
     * @param model Model for Thymeleaf
     * @return Thymeleaf template for available appointments
     */
    @GetMapping("/availableAppointments")
    @Operation(summary = "Get available appointments for a doctor on a specific date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appointments found"),
        @ApiResponse(responseCode = "400", description = "Invalid doctor ID or date format"),
        @ApiResponse(responseCode = "404", description = "No available appointments found")
    })
    public String viewAvailableAppointments(
            @RequestParam Long doctorId,
            @RequestParam LocalDate date, 
            Model model) {
        try {
            List<Appointment> appointments = appointmentServiceImpl.findAvailableAppointments(doctorId, date);
            Doctor doctor = doctorServiceImpl.getDoctorById(doctorId)
                                             .orElseThrow(() -> new DoctorNotFoundException("Doctor with ID " + doctorId + " not found"));
            model.addAttribute("appointments", appointments);
            model.addAttribute("doctor", doctor);
            model.addAttribute("date", date);

            return "availableAppointments";
            
        } catch (NoAvailableAppointmentsException ex) {
            model.addAttribute("error", ex.getMessage());
            return "availableAppointments"; 
        }
    }

    /**
     * Get the final booking form for an appointment.
     *
     * @param appointmentId ID of the appointment
     * @param model Model for Thymeleaf
     * @return Thymeleaf template for booking form
     */
    @GetMapping("/getform/{appointmentId}")
    @Operation(summary = "Get Final Booking Form")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appointment found"),
        @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public String getBookingForm(
            @PathVariable Long appointmentId, 
            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInEmail = authentication.getName();

        Patient patient = patientServiceImpl.getPatientByEmail(loggedInEmail);
        model.addAttribute("patient", patient);

        Appointment appointment = appointmentServiceImpl.findAppointmentById(appointmentId);
        model.addAttribute("appointment", appointment);

        return "bookingForm";
    }

    /**
     * Book an appointment for a patient.
     *
     * @param patientId ID of the patient
     * @param appointmentId ID of the appointment
     * @param symptoms Patient's symptoms
     * @return Redirect to patient's appointments page
     */
    @PostMapping("/book")
    @Operation(summary = "Book an appointment for a patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "302", description = "Appointment booked successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid patient ID or appointment ID")
    })
    public String bookAppointment(
            @RequestParam Long patientId, 
            @RequestParam Long appointmentId, 
            @RequestParam String symptoms) {
        Appointment appointment = appointmentServiceImpl.findAppointmentById(appointmentId);
        appointment.setSymptoms(symptoms); // Set symptoms
        appointmentServiceImpl.bookAppointment(patientId, appointment);

        return "redirect:/appointments/viewAll?patientId=" + patientId;
    }

    /**
     * Cancel an appointment for a patient.
     *
     * @param patientId ID of the patient
     * @param appointmentId ID of the appointment
     * @param model Model for Thymeleaf
     * @return Redirect to patient's appointments page or error view
     */
    @PostMapping("/cancel")
    @Operation(summary = "Cancel an appointment for a patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "302", description = "Appointment cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid patient ID or appointment ID")
    })
    public String cancelAppointment(
            @RequestParam Long patientId, 
            @RequestParam Long appointmentId, 
            Model model) {
        try {
            appointmentServiceImpl.cancelAppointment(appointmentId, patientId);
            return "redirect:/appointments/viewAll?patientId=" + patientId;
        } catch (CannotCancelWithinFourHoursException ex) {
            Patient patient = patientServiceImpl.getPatientById(patientId)
                                               .orElseThrow(() -> new PatientNotFoundException("Patient with ID " + patientId + " not found"));
            model.addAttribute("patient", patient);
            model.addAttribute("errorMessage", ex.getMessage());
            return "viewAppointments"; 
        }
    }

    /**
     * View all appointments for a patient by status.
     *
     * @param patientId ID of the patient
     * @param status Status of the appointments (default: "Booked")
     * @param model Model for Thymeleaf
     * @return Thymeleaf template for viewing appointments
     */
    @GetMapping("/viewAll")
    @Operation(summary = "View all appointments for a patient by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appointments found"),
        @ApiResponse(responseCode = "400", description = "Invalid patient ID or status")
    })
    public String getAppointmentsByStatusAndPatientId(
            @RequestParam Long patientId,
            @RequestParam(required = false, defaultValue = "Booked") String status, 
            Model model) {
        try {
            List<Appointment> appointments = appointmentServiceImpl.findByPatientIdAndStatus(patientId, status);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String loggedInEmail = authentication.getName();
            
            Patient patient = patientServiceImpl.getPatientByEmail(loggedInEmail);
            model.addAttribute("patient", patient);
            model.addAttribute("appointments", appointments);
            model.addAttribute("status", status); 
            return "viewAppointments";
        } catch (AppointmentNotFoundException ex) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String loggedInEmail = authentication.getName();

            Patient patient = patientServiceImpl.getPatientByEmail(loggedInEmail);
            model.addAttribute("patient", patient);
            model.addAttribute("errorMessage", ex.getMessage());
            return "viewAppointments";
        }
    }

    /**
     * Find an appointment by its ID.
     *
     * @param appointmentId ID of the appointment
     * @param model Model for Thymeleaf
     * @return Thymeleaf template for appointment details
     */
    @GetMapping("/view/{appointmentId}")
    @Operation(summary = "Find Appointment by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appointment found"),
        @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public String findAppointmentById(
            @PathVariable Long appointmentId, 
            Model model) {
        Appointment appointment = appointmentServiceImpl.findAppointmentById(appointmentId);
        model.addAttribute("appointment", appointment);
        return "appointmentDetails";
    }

    /**
     * Retrieve all medications associated with an appointment.
     *
     * @param appointmentId ID of the appointment
     * @param model Model for Thymeleaf
     * @return Thymeleaf template for listing medications
     */
    @GetMapping("/listMed")
    @Operation(summary = "List medications for an appointment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Medications found"),
        @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public String getMedicationByAppointmentId(
            @RequestParam Long appointmentId, 
            Model model) {
        List<Medication> medications = medicationServiceImpl.getMedications(appointmentId);
        model.addAttribute("medications", medications);
        Appointment appointment = appointmentServiceImpl.findAppointmentById(appointmentId);
        model.addAttribute("appointment", appointment);
        return "patlistMedications";
    }
    
}
   
