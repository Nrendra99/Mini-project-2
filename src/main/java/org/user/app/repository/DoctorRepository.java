package org.user.app.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.user.app.entity.Doctor;


public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    /**
     * Find a doctor by their email.
     */
	 Doctor findByEmail(String email);
}