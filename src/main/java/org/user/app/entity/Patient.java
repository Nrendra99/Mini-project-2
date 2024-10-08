package org.user.app.entity;


import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@Entity
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pat_seq")
    @SequenceGenerator(name = "pat_seq", sequenceName = "pat_sequence", allocationSize = 1)
    private Long Id;  // Unique identifier for each patient

    @NotBlank(message = "Name is mandatory")
    @Column(name = "first_name")
    private String firstName;  // Patient's first name
    
    @Column(name = "last_name")
    private String lastName;  // Patient's last name
    
    @Min(value = 1, message = "Age must be at least 1")
    @Max(value = 125, message = "Age must be less than or equal to 125")
    @Column(name = "age")
    private int age;  // Patient's age
    
    @NotBlank(message = "Please specify gender")
    @Column(name = "gender")
    private String gender;  // Patient's gender
    
    @NotBlank(message = "Email is mandatory")
    @Column(name = "email")
    @Email(message = "Incorrect email format")
    private String email;  // Patient's email address
    
    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    private String phoneNo;  // Patient's phone number

    @Column(nullable = false)
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters long")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
    @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter")
    @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit")
    @Pattern(regexp = ".*[!@#$%^&*(),.?\":{}|<>].*", message = "Password must contain at least one special character")
    @Pattern(regexp = "\\S+", message = "Password cannot contain spaces")
    private String password;  // Patient's password with validation rules
    
    final String role = "PATIENT";  // Role fixed as "Patient"

    private String medicalHistory;  // Patient's medical history
    
    public void addDoctor (Doctor doctor) {
           doctors.add(doctor);
           doctor.getPatients().add(this);
    }
    
    public void removeDoctor (Doctor doctor) {
        doctors.remove(doctor);
        doctor.getPatients().remove(this);
    }
    
    
    @ManyToMany
    @JoinTable(	
        name = "patient_doctor",
        joinColumns = @JoinColumn(name = "patient_id"),
        inverseJoinColumns = @JoinColumn(name = "doctor_id")
    )
    private Set<Doctor> doctors = new HashSet<>();

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMedicalHistory() {
		return medicalHistory;
	}

	public void setMedicalHistory(String medicalHistory) {
		this.medicalHistory = medicalHistory;
	}

	public Set<Doctor> getDoctors() {
		return doctors;
	}

	public void setDoctors(Set<Doctor> doctors) {
		this.doctors = doctors;
	}

	public String getRole() {
		return role;
	}

	public Patient(Long id, @NotBlank(message = "Name is mandatory") String firstName, String lastName,
			@Min(value = 1, message = "Age must be at least 1") @Max(value = 125, message = "Age must be less than or equal to 125") int age,
			@NotBlank(message = "Please specify gender") String gender,
			@NotBlank(message = "Email is mandatory") @Email(message = "Incorrect email format") String email,
			@Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits") String phoneNo,
			@Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters long") @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter") @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter") @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit") @Pattern(regexp = ".*[!@#$%^&*(),.?\":{}|<>].*", message = "Password must contain at least one special character") @Pattern(regexp = "\\S+", message = "Password cannot contain spaces") String password,
			String medicalHistory, Set<Doctor> doctors) {
		super();
		Id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.gender = gender;
		this.email = email;
		this.phoneNo = phoneNo;
		this.password = password;
		this.medicalHistory = medicalHistory;
		this.doctors = doctors;
	}

	public Patient() {
		
	}
    
    
}