package org.user.app.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.user.app.entity.Appointment;
import org.user.app.entity.Doctor;
import org.user.app.entity.Patient;
import org.user.app.exceptions.AppointmentNotFoundException;
import org.user.app.repository.DoctorRepository;
import org.user.app.service.AppointmentServiceImpl;
import org.user.app.service.DoctorServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/doctors")
@Tag(name = "Doctors", description = "Operations related to doctor management")
public class DoctorController {

    @Autowired
    private DoctorServiceImpl doctorServiceImpl;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private AppointmentServiceImpl appointmentServiceImpl;


    /**
     * Show the doctor registration form.
     *
     * @param model the model to add attributes for Thymeleaf rendering
     * @return the name of the Thymeleaf template to render
     */
    @GetMapping("/getform")
    @Operation(summary = "Show doctor registration form", 
               description = "Render the form to register a new doctor")
    public String showRegistrationForm(Model model) {
        model.addAttribute("doctor", new Doctor());
        return "registerDoctor";
    }

    /**
     * Register a new doctor.
     *
     * @param doctor the doctor entity to be registered
     * @param result the binding result to handle validation errors
     * @param model the model to add attributes for Thymeleaf rendering
     * @return the name of the Thymeleaf template to render
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new doctor", 
               description = "Handle doctor registration form submission")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Doctor registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid doctor data")
    })
    public String registerDoctor(@Valid @ModelAttribute Doctor doctor, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // If there are validation errors, return to the registration form with error messages
            return "registerDoctor";
        }

        doctorServiceImpl.registerDoctor(doctor);
        model.addAttribute("message", "Doctor is registered successfully!");
        model.addAttribute("doctor", new Doctor());
        return "registerDoctor";
    }

    /**
     * View all patients associated with a specific doctor.
     *
     * @param doctor the doctor whose patients are to be retrieved.
     * @param model the model to add attributes for Thymeleaf rendering
     * @return the name of the Thymeleaf template to render
     */
    @GetMapping("/patients")
    @Operation(summary = "View all patients for the logged-in doctor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patients found"),
        @ApiResponse(responseCode = "400", description = "Error fetching patients")
    })
    public String viewPatients(Model model) {
  
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String doctorEmail = authentication.getName(); 

        Doctor doctor = doctorServiceImpl.getDoctorByEmail(doctorEmail);
              
        Set<Patient> patients = doctorServiceImpl.findPatients(doctor);
        model.addAttribute("patients", patients);
     
        return "listPatients";
    }
  
    
   
    /**
     * Show a form to select a date for viewing appointments.
     *
     * @param model the model object used to pass attributes to the view.
     * @return the name of the view to display the date selection form.
     */
    @GetMapping("/appointments/selectDate")
    @Operation(summary = "Show a form to select a date for viewing appointments")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Date selection form displayed")
    })
    public String showDateSelectionForm(Model model) {
 
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Doctor doctor = doctorRepository.findByEmail(authentication.getName()); 
        
        Long doctorId = doctor.getId();
        
        model.addAttribute("doctorId", doctorId);
       
        return "doctorAppointments";
    }
    
    /**
     * View all appointments for the logged-in doctor on a specific date.
     *
     * @param date the date for which to view the appointments. Must be provided as a request parameter.
     * @param model the model object used to pass attributes to the view.
     * @return the name of the view to display the appointments.
     * @throws NoAvailableAppointmentsException if no appointments are found for the given doctor and date.
     */
    @GetMapping("/appointments")
    @Operation(summary = "View all appointments for the logged-in doctor on a specific date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appointments found"),
        @ApiResponse(responseCode = "400", description = "Error fetching appointments")
    })
    public String viewDoctorAppointments(
            @RequestParam LocalDate date,
            Model model) {

      
        try {Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Doctor doctor = doctorRepository.findByEmail(authentication.getName()); 
        
        Long doctorId = doctor.getId();

        List<Appointment> appointments = appointmentServiceImpl.viewAppointments(doctorId, date);
        
        model.addAttribute("appointments", appointments);
        model.addAttribute("doctorId", doctorId);
        model.addAttribute("date", date);

             return "doctorAppointments";
             
        } catch (AppointmentNotFoundException ex){
        	
        model.addAttribute("errorMessage", ex.getMessage());	
        	 return "doctorAppointments";
        }
    } 

    /**
     * Show the form to update the status of an appointment.
     *
     * @param appointmentId the ID of the appointment to update
     * @param model the model to add attributes for Thymeleaf rendering
     * @return the name of the Thymeleaf template to render
     */
    @GetMapping("/updateStatusForm")
    @Operation(summary = "Show update form for appointment status", 
               description = "Render the form to update the status of a specific appointment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Form rendered successfully"),
        @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public String showUpdateStatusForm(
            @RequestParam @Parameter(description = "ID of the appointment") Long appointmentId,
            Model model) {

        Appointment appointment = appointmentServiceImpl.findAppointmentById(appointmentId);
        model.addAttribute("appointment", appointment);
        return "updateAppointmentStatusForm";  // Name of Thymeleaf template to render the form
    }
    
    /**
     * Update the status of an appointment.
     *
     * @param appointmentId the ID of the appointment
     * @param status the new status to be updated
     * @param model the model to add attributes for Thymeleaf rendering
     * @return the name of the Thymeleaf template to render
     */
    @PostMapping("/updateStatus")
    @Operation(summary = "Update Appointment Status", 
               description = "Update the status of a specific appointment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appointment status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public String updateAppointmentStatus(
            @RequestParam @Parameter(description = "ID of the appointment") Long appointmentId,
            @RequestParam @Parameter(description = "New status of the appointment") String status,
            Model model) {

        // Update the appointment status
        Appointment updatedAppointment = appointmentServiceImpl.updateAppointmentStatus(appointmentId, status);
        
        // Add the updated appointment to the model
        model.addAttribute("appointment", updatedAppointment);
        
      
        return "redirect:/doctors/appointments?date=" + updatedAppointment.getAppointmentDate() 
                + "&doctorId=" + updatedAppointment.getDoctor().getId();
    }
    
    /**
     * View the details of the logged-in doctor.
     *
     * @param model the model object used to pass attributes to the view.
     * @return the name of the view to display the doctor's details.
     */
    @GetMapping("/view")
    public String viewDoctor(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInEmail = authentication.getName();

        // Fetch the doctor by their email
        Doctor doctor = doctorServiceImpl.getDoctorByEmail(loggedInEmail);
        model.addAttribute("doctor", doctor);
        
        return "viewDoctor";
    }
    

}