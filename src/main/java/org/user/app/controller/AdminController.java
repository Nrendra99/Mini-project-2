package org.user.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.user.app.entity.Doctor;
import org.user.app.entity.Patient;
import org.user.app.exceptions.DoctorNotFoundException;
import org.user.app.exceptions.PatientNotFoundException;
import org.user.app.service.DoctorServiceImpl;
import org.user.app.service.PatientServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
    @Autowired
	private PatientServiceImpl patientServiceImpl;
    
    @Autowired
   	private DoctorServiceImpl doctorServiceImpl;
    
    

    // Method to display admin home page
    @GetMapping("/home")
    @Operation(summary = "Admin homepage")
    public String adminHome() {
    	return "AdminHomepage";
    }
    
    // Method to delete a patient by ID
    @GetMapping("/patDelete/{id}")
    @Operation(summary = "Delete a patient by ID")
    public String deletePatient(@PathVariable Long id, Model model) {
        patientServiceImpl.deletePatient(id);
        model.addAttribute("message", "Patient deleted successfully.");
        return "redirect:/patients/list"; // Redirect to patient list
    }
    
    // Method to list all patients
    @GetMapping("/patList")
    @Operation(summary = "List all patients")
    public String listPatients(Model model) {
        List<Patient> patients = patientServiceImpl.getAllPatients();
        model.addAttribute("patients", patients);
        return "PatientsList"; // The view that lists all patients
    }

    // Method to display the update form for a patient
    @GetMapping("/patUpdate/{id}")
    @Operation(summary = "Show update form for a patient")
    public String showUpdatePatForm(@PathVariable Long id, Model model) {
        Patient patient = patientServiceImpl.getPatientById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + id));
        model.addAttribute("patient", patient);
        return "PatientUpdate"; // The view for updating a patient
    }

    // Method to handle patient update form submission
    @PostMapping("/patUpdate/{id}")
    @Operation(summary = "Update patient details")
    public String updatePatient(@PathVariable Long id, @Valid @ModelAttribute("patient") Patient updatedPatient, BindingResult result, Model model) {
        patientServiceImpl.updatePatient(id, updatedPatient);
        return "redirect:/admin/patList"; // Redirect to the list of patients
    }
    
    // Method to display the update form for a doctor
    @GetMapping("/docUpdate/{id}")
    @Operation(summary = "Show update form for a doctor")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Doctor doctor = doctorServiceImpl.getDoctorById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with ID: " + id));
        model.addAttribute("doctor", doctor);
        return "DoctorUpdate"; // The view for updating a doctor
    }

    // Method to handle doctor update form submission
    @PostMapping("/docUpdate/{id}")
    @Operation(summary = "Update doctor details")
    public String updateDoctor(@PathVariable Long id, @Valid @ModelAttribute("doctor") Doctor updatedDoctor, BindingResult result, Model model) {
        doctorServiceImpl.updateDoctor(id, updatedDoctor);
        return "redirect:/admin/listDoctors"; // Redirect to the list of doctors
    }

    // Method to delete a doctor by ID
    @PostMapping("/deleteDoc/{id}")
    @Operation(summary = "Delete a doctor by ID")
    public String deleteDoctor(@PathVariable Long id) {
        doctorServiceImpl.deleteDoctor(id);
        return "redirect:/doctors/viewDoctors"; // Redirect to doctor list
    }

    // Method to list all doctors
    @GetMapping("/docList")
    @Operation(summary = "List all doctors")
    public String listDoctors(Model model) {
        List<Doctor> doctors = doctorServiceImpl.getAllDoctors();
        model.addAttribute("doctors", doctors);
        return "DoctorsList"; // The view that lists all doctors
    }
}