package org.user.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
public class Appointment {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "App_seq")
	@SequenceGenerator(name = "App_seq", sequenceName = "App_sequence", allocationSize = 1)
	private Long Id;  // Unique ID for each appointment

	@ManyToOne
	@JoinColumn(name = "doctor_id", nullable = false)
	private Doctor doctor;  // Associated doctor

	@ManyToOne
	@JoinColumn(name = "patient_id", nullable = true)
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

	@OneToMany(mappedBy = "appointment")
	private Set<Medication> medications;  // Medications associated with this appointment
}