package org.user.app.controller;
 


import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.user.app.entity.Doctor;
import org.user.app.entity.Patient;
import org.user.app.service.PatientServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping("/patients")
@Tag(name = "Patients", description = "Operations related to patient management")
public class PatientController {

    @Autowired
    private PatientServiceImpl patientServiceImpl;


    // Show the patient registration form
    @GetMapping("/getform")
    @Operation(summary = "Show patient registration form", 
               description = "Render the form to register a new patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registration form successfully rendered")
    })
    public String showRegistrationForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "registerPatient";
    }

    // Register a new patient
    @PostMapping("/register")
    @Operation(summary = "Register a new patient", 
               description = "Handle patient registration form submission")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patient registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid patient data")
    })
    public String registerPatient(@jakarta.validation.Valid @ModelAttribute Patient patient, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // If there are validation errors, return to the registration form with error messages
            return "registerPatient";
        }

        this.patientServiceImpl.registerPatient(patient);
        model.addAttribute("message", "Patient is registered successfully!");
        model.addAttribute("patient", new Patient());
        return "registerPatient";
    }

    // Retrieve and display the information of the currently logged-in patient
    @GetMapping("/view")
    @Operation(summary = "Retrieve patient information", 
               description = "Display the information of the currently logged-in patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patient information retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public String viewPatient(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInEmail = authentication.getName();

        Patient patient = patientServiceImpl.getPatientByEmail(loggedInEmail);

        model.addAttribute("patient", patient);

        return "viewPatient";
    }

    // View all patients associated with a specific doctor
    @GetMapping("/doctors")
    @Operation(summary = "View all patients for the logged-in doctor", 
               description = "Retrieve and display all patients associated with the currently logged-in doctor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patients found and displayed successfully"),
        @ApiResponse(responseCode = "400", description = "Error fetching patients"),
        @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    public String viewDoctors(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInEmail = authentication.getName(); 

        Patient patient = patientServiceImpl.getPatientByEmail(loggedInEmail);

        Set<Doctor> doctors = patientServiceImpl.viewDoctors(patient);
        model.addAttribute("doctors", doctors);

        return "listDoctors";
    }
   
}