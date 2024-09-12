package org.user.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.user.app.entity.Appointment;
import org.user.app.entity.Medication;
import org.user.app.exceptions.MedicationNotFoundException;
import org.user.app.service.AppointmentServiceImpl;
import org.user.app.service.MedicationServiceImpl;

import java.util.List;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/medications")
@Tag(name = "Medications", description = "Operations related to medication management")
public class MedicationController {

    @Autowired
    private MedicationServiceImpl medicationServiceImpl;
    
    @Autowired
    private AppointmentServiceImpl appointmentServiceImpl;

    /**
     * Show form to add medication to an appointment.
     *
     * @param model the model to add attributes for Thymeleaf rendering
     * @return the name of the Thymeleaf template to render
     */
    @GetMapping("/getForm")
    @Operation(summary = "Show form to add medication", 
               description = "Render the form to add a new medication to an appointment")
    public String showAddMedicationForm(@RequestParam Long appointmentId, Model model) {
    	Appointment appointment = appointmentServiceImpl.findAppointmentById(appointmentId);
    	model.addAttribute("appointment" , appointment);
        model.addAttribute("medication", new Medication());
        model.addAttribute("appointmentId", appointmentId);
        return "addMedication";
    }

    /**
     * Add medication to an appointment.
     *
     * @param appointmentId the ID of the appointment
     * @param medication the medication entity to be added
     * @param result the binding result to handle validation errors
     * @param model the model to add attributes for Thymeleaf rendering
     * @return the name of the Thymeleaf template to render
     */
    @PostMapping("/addMed")
    @Operation(summary = "Add medication", 
               description = "Handle form submission to add a new medication to an appointment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Medication added successfully"),
        @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public String addMedication(
            @RequestParam Long appointmentId, 
            @Valid @ModelAttribute Medication medication, 
            BindingResult result, 
            Model model) {
    	
        if (result.hasErrors()) {
            return "addMedication";
        }
        
        medicationServiceImpl.addMedicationToAppointment(appointmentId, medication);
        Appointment appointment = appointmentServiceImpl.findAppointmentById(appointmentId);
    	model.addAttribute("appointment" , appointment);
    	model.addAttribute("medication", new Medication());
        model.addAttribute("message", "Medication added successfully");
        return "addMedication";
    }

    /**
     * Show form to update medication details.
     *
     * @param medicationId the ID of the medication to be updated
     * @param model the model to add attributes for Thymeleaf rendering
     * @return the name of the Thymeleaf template to render
     */
    @GetMapping("/updateForm")
    @Operation(summary = "Show form to update medication", 
               description = "Render the form to update an existing medication")
    public String showUpdateMedicationForm(@RequestParam Long medicationId, Model model) {
     
      Medication medication = medicationServiceImpl.getMedicationById(medicationId);
        model.addAttribute("medication", medication);
        return "updateMedication";
    }

    /**
     * Update details of an existing medication.
     *
     * @param medicationId the ID of the medication
     * @param updatedMedication the updated medication entity
     * @param result the binding result to handle validation errors
     * @param model the model to add attributes for Thymeleaf rendering
     * @return the name of the Thymeleaf template to render
     */
    @PostMapping("/updateMed")
    public String updateMedication(
            @Valid @ModelAttribute Medication updatedMedication,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            return "updateMedication";
        }

        try {
            medicationServiceImpl.updateMedication(updatedMedication);
            
        	model.addAttribute("medication" , updatedMedication);
            model.addAttribute("message", "Medication updated successfully");
        } catch (MedicationNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "updateMedication";
    }
 
    /**
     * Delete a medication.
     *
     * @param medicationId the ID of the medication
     * @return redirect to list medications page 
     */
    @PostMapping("/removeMed")
    @Operation(summary = "Remove medication", 
               description = "Remove a medication from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Medication removed successfully"),
        @ApiResponse(responseCode = "404", description = "Medication not found")
    })
    public String removeMedication(@RequestParam Long medicationId) {
        
        Medication medication = medicationServiceImpl.getMedicationById(medicationId);
        
        medicationServiceImpl.removeMedication(medicationId);
        
        // Properly format the redirect URL with the query parameter for appointmentId
        return "redirect:/medications/listMed?appointmentId=" + medication.getAppointment().getId();
    }

    /**
     * Retrieve all medications associated with a specific appointment.
     *
     * @param appointmentId the ID of the appointment
     * @param model the model to add attributes for Thymeleaf rendering
     * @return the name of the Thymeleaf template to render
     */
    @GetMapping("/listMed")
    public String getMedications(@RequestParam Long appointmentId, Model model) {
        List<Medication> medications = medicationServiceImpl.getMedications(appointmentId);
        
        Appointment appointment = appointmentServiceImpl.findAppointmentById(appointmentId);
        model.addAttribute("medications", medications);
        model.addAttribute("appointmentId", appointmentId);
        model.addAttribute("appointment" , appointment);
        return "listMedications";
    }
}