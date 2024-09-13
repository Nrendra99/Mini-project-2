package org.user.app.entity;

import jakarta.persistence.*;

import lombok.Builder;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Builder
@Entity
public class Appointment {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "App_seq")
	@SequenceGenerator(name = "App_seq", sequenceName = "App_sequence", allocationSize = 1)
	private Long Id;  // Unique ID for each appointment

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "doctor_id")
	private Doctor doctor;  // Associated doctor

	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_id")
	private Patient patient;  // Associated patient (optional)

	@Column(nullable = false)
	private LocalDate appointmentDate;  // Date of the appointment

	@Column(nullable = false)
	private LocalTime startTime;  // Appointment start time

	@Column(nullable = false)
	private LocalTime endTime;  // Appointment end time

	@Column(nullable = false)
	private boolean isAvailable;  // Appointment availability status

	@Column(nullable = false)
	private String status;  // Appointment status (e.g., CONFIRMED, CANCELED)

	private String symptoms;  // Patient's symptoms (optional)

	@OneToMany(mappedBy = "appointment" , cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private Set<Medication> medications = new HashSet <>();  // Medications associated with this appointment
	
	public void addMedication (Medication medication){
		medications.add(medication);
		medication.setAppointment(this);
	}
	
	public void removeMedication (Medication medication){
		medications.remove(medication);
		medication.setAppointment(null);
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public LocalDate getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(LocalDate appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(String symptoms) {
		this.symptoms = symptoms;
	}

	public Set<Medication> getMedications() {
		return medications;
	}

	public void setMedications(Set<Medication> medications) {
		this.medications = medications;
	}

	public Appointment(Long id, Doctor doctor, Patient patient, LocalDate appointmentDate, LocalTime startTime,
			LocalTime endTime, boolean isAvailable, String status, String symptoms, Set<Medication> medications) {
		super();
		Id = id;
		this.doctor = doctor;
		this.patient = patient;
		this.appointmentDate = appointmentDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.isAvailable = isAvailable;
		this.status = status;
		this.symptoms = symptoms;
		this.medications = medications;
	}

	public Appointment() {
		
	}
	
}