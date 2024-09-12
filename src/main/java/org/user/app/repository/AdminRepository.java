package org.user.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.user.app.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByEmail(String email);
}