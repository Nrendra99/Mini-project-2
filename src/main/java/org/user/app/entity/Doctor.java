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
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Doc_seq")
    @SequenceGenerator(name = "Doc_seq", sequenceName = "Doc_sequence", allocationSize = 1)
    private Long Id;  // Unique identifier for each doctor
    
    @NotBlank(message = "Name is mandatory")
    @Column(name = "first_name")
    private String firstName;  // Doctor's first name
    
    @Column(name = "last_name")
    private String lastName;  // Doctor's last name
    
    @Min(value = 1, message = "Age must be at least 1")
    @Max(value = 125, message = "Age must be less than or equal to 125")
    @Column(name = "age")
    private int age;  // Doctor's age
    
    @NotBlank(message = "Please specify Gender")
    @Column(name = "gender")
    private String gender;  // Doctors's gender
    
    @NotBlank(message = "Email is mandatory")
    @Column(name = "email")
    @Email(message = "Incorrect email format")
    private String email;  // Doctor's email address
    
    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    private String phoneNo;  // Doctor's phone number
    
    @Column(nullable = false)
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters long")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
    @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter")
    @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit")
    @Pattern(regexp = ".*[!@#$%^&*(),.?\":{}|<>].*", message = "Password must contain at least one special character")
    @Pattern(regexp = "\\S+", message = "Password cannot contain spaces")
    private String password;  // Doctor's password with validation rules
    
    @NotBlank(message = "Specialization is mandatory")
    @Column(name = "Specialization")
    private String specialization;  // Doctor's area of specialization

    final String role = "DOCTOR";  // Role fixed as "DOCTOR"
    
    @ManyToMany (mappedBy = "doctors")     
    private Set<Patient> patients = new HashSet<>(); // Patients linked to the doctor
    
    @OneToMany(mappedBy = "doctor" )
    private Set<Appointment> appointments;  // Appointments linked to the doctor

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

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public Set<Patient> getPatients() {
		return patients;
	}

	public void setPatients(Set<Patient> patients) {
		this.patients = patients;
	}

	public Set<Appointment> getAppointments() {
		return appointments;
	}

	public void setAppointments(Set<Appointment> appointments) {
		this.appointments = appointments;
	}

	public String getRole() {
		return role;
	}

	public Doctor(Long id, @NotBlank(message = "Name is mandatory") String firstName, String lastName,
			@Min(value = 1, message = "Age must be at least 1") @Max(value = 125, message = "Age must be less than or equal to 125") int age,
			@NotBlank(message = "Please specify Gender") String gender,
			@NotBlank(message = "Email is mandatory") @Email(message = "Incorrect email format") String email,
			@Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits") String phoneNo,
			@Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters long") @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter") @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter") @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit") @Pattern(regexp = ".*[!@#$%^&*(),.?\":{}|<>].*", message = "Password must contain at least one special character") @Pattern(regexp = "\\S+", message = "Password cannot contain spaces") String password,
			@NotBlank(message = "Specialization is mandatory") String specialization, Set<Patient> patients,
			Set<Appointment> appointments) {
		super();
		Id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.gender = gender;
		this.email = email;
		this.phoneNo = phoneNo;
		this.password = password;
		this.specialization = specialization;
		this.patients = patients;
		this.appointments = appointments;
	}

	public Doctor() {
		
	}
    
    
} 