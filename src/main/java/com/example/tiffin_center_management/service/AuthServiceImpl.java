package com.example.tiffin_center_management.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.tiffin_center_management.exception.EmailAlreadyExistsException;
import com.example.tiffin_center_management.exception.InvalidCredentialsException;
import com.example.tiffin_center_management.model.Admin;
import com.example.tiffin_center_management.model.BaseUser;
import com.example.tiffin_center_management.model.Customer;
import com.example.tiffin_center_management.model.DeliveryBoy;
import com.example.tiffin_center_management.payload.LoginRequest;
import com.example.tiffin_center_management.payload.SignupRequest;
import com.example.tiffin_center_management.repository.AdminRepository;
import com.example.tiffin_center_management.repository.BaseUserRepository;
import com.example.tiffin_center_management.repository.CustomerRepository;
import com.example.tiffin_center_management.repository.DeliveryBoyRepository;
import com.example.tiffin_center_management.util.JwtUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

	
	private final BaseUserRepository userRepo;
	private final AdminRepository adminRepo;
    private final CustomerRepository customerRepo;
    private final DeliveryBoyRepository deliveryRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional // Transactional lagana zaroori hai
    public void signup(SignupRequest req) {

        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        String role = req.getRole().toUpperCase();

        if (!role.equals("ADMIN") && !role.equals("CUSTOMER") && !role.equals("DELIVERY")) {
            throw new IllegalArgumentException("Invalid Role! Only ADMIN, CUSTOMER, or DELIVERY allowed.");
        }
        
        if (role.equals("ADMIN")) {
            Admin admin = new Admin();
            setCommonFields(admin, req);
            adminRepo.save(admin); // Admin repo me save karo
        } 
        else if (role.equals("DELIVERY")) {
            DeliveryBoy db = new DeliveryBoy();
            setCommonFields(db, req);
            db.setAvailable(true); // Default available rakho
            db.setActive(true);
            // Agar SignupRequest me phone/address hai toh yahan set karein
            deliveryRepo.save(db); // Delivery repo me save karo
        } 
        else {
            Customer customer = new Customer();
            setCommonFields(customer, req);
            customer.setActive(true);
            customerRepo.save(customer); // Customer repo me save karo
        }
    }

    // Common fields set karne ke liye helper method
    private void setCommonFields(BaseUser user, SignupRequest req) {
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRole(req.getRole().toUpperCase());
    }

    @Override
    public String login(LoginRequest req) {

        BaseUser user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return jwtUtil.generateToken(user.getEmail());
    }
	
}
