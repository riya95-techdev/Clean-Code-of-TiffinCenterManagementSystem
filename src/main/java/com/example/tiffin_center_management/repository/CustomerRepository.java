package com.example.tiffin_center_management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.tiffin_center_management.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

	// Sirf wahi users lao jinka role 'CUSTOMER' hai
    @Query("SELECT c FROM Customer c WHERE c.role = 'CUSTOMER'")
    Page<Customer> findAllCustomers(Pageable pageable);
	
    //  Sahi Return Type: List<Customer>
    Optional<Customer> findByEmail(String email);

    //  Keyword se search karne ke liye List return karein
    List<Customer> findByNameContaining(String keyword);
}
