package com.example.tiffin_center_management.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.tiffin_center_management.model.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long>{

	// Admin specific email search (agar kabhi zaroorat pade)
    Optional<Admin> findByEmail(String email);

}
