package com.example.tiffin_center_management.controller;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tiffin_center_management.payload.CustomerDTO;
import com.example.tiffin_center_management.service.CustomerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class CustomerContoller {

	private final CustomerService service;

    // READ ALL (pagination)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Page<CustomerDTO> getAll(@RequestParam(defaultValue ="0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(defaultValue = "id") String sortBy,
                           	    @RequestParam(defaultValue = "asc") String sortDir) {
        return service.getAll(page, size,sortBy,sortDir);
    }

    // CUSTOMER APNI PROFILE KHID BS DEKHE OR ADMIN TO FIR DEKH HI SKTA H 
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @GetMapping("/{id}")
    public CustomerDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    // UPDATE:USER/CUSTOMER APNI PROFILE KHUD UPDATE KR SKE 
 
    @PreAuthorize("hasRole('CUSTOMER','ADMIN')")
    @PutMapping("/{id}")
    public CustomerDTO update(@PathVariable Long id,
                              @RequestBody CustomerDTO dto) {
        return service.update(id, dto);
    }

    // DELETE
 // Baaki DELETE sirf ADMIN ke paas hona chahiye
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "Customer deleted";
    }
	
}
