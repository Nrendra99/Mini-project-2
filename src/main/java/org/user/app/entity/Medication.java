package org.user.app.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @ManyToOne
    @JoinColumn(name = "appointment_id" , nullable=false)
    private Appointment appointment;  // The appointment associated with this medication
}