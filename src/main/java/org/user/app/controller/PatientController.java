package org.user.app.controller;
 


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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

    /**
     * Show the patient registration form.
     *
     * @param model the model to add attributes for Thymeleaf rendering
     * @return the name of the Thymeleaf template to render
     */
    @GetMapping("/getform")
    @Operation(summary = "Show patient registration form", 
               description = "Render the form to register a new patient")
    public String showRegistrationForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "registerPatient";
    }

    /**
     * Register a new patient.
     *
     * @param patient the patient entity to be registered
     * @param result the binding result to handle validation errors
     * @param model the model to add attributes for Thymeleaf rendering
     * @return the name of the Thymeleaf template to render
     */
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
    
    /**
     * Retrieve and display the information of the currently logged-in patient.
     *
     * @param model the model object to which the patient details will be added
     * @return the name of the view template to be rendered.
     */
    @GetMapping("/view")
    public String viewPatient(Model model) {
    
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInEmail = authentication.getName();


        Patient patient = patientServiceImpl.getPatientByEmail(loggedInEmail);
     
        model.addAttribute("patient", patient);

        return "viewPatient";
    }
}