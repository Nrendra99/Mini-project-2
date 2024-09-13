package org.user.app.entity;

import jakarta.persistence.*;

@Entity
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Med_seq")
    @SequenceGenerator(name = "Med_seq", sequenceName = "Med_sequence", allocationSize = 1)
    private Long Id;  // Unique identifier for each medication
    
    private String name;  // Name of the medication
    
    private String dosage;  // Dosage information for the medication
    
    private String frequency;  // Frequency of medication intake
    
    private String instructions;  // Special instructions for medication usage

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;  // The appointment associated with this medication

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDosage() {
		return dosage;
	}

	public void setDosage(String dosage) {
		this.dosage = dosage;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public Appointment getAppointment() {
		return appointment;
	}

	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}

	public Medication(Long id, String name, String dosage, String frequency, String instructions,
			Appointment appointment) {
		super();
		Id = id;
		this.name = name;
		this.dosage = dosage;
		this.frequency = frequency;
		this.instructions = instructions;
		this.appointment = appointment;
	}

	public Medication() {
		
	}
    
    
    
}