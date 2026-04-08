package com.example.tiffin_center_management.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.tiffin_center_management.payload.ApiResponse;
import com.example.tiffin_center_management.payload.CustomerDTO;
import com.example.tiffin_center_management.payload.DeliveryBoyDTO;
import com.example.tiffin_center_management.payload.SubscriptionDTO;
import com.example.tiffin_center_management.service.CustomerService;
import com.example.tiffin_center_management.service.DeliveryBoyService;
import com.example.tiffin_center_management.service.SubscriptionService;
import com.example.tiffin_center_management.service.TiffinScheduler;

import lombok.RequiredArgsConstructor;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final CustomerService customerService;
    private final DeliveryBoyService deliveryService;
    private final SubscriptionService subscriptionService;
    private final TiffinScheduler tiffinScheduler;
    
    // FETCH ALL DATA (GET)
    @GetMapping("/customers")
    public Page<CustomerDTO> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return customerService.getAll(page, size, sortBy, sortDir);
    }

    @GetMapping("/delivery-boys")
    public Page<DeliveryBoyDTO> getAllDelivery(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return deliveryService.getAll(page, size, sortBy, sortDir);
    }

    @GetMapping("/subscriptions")
    public Page<SubscriptionDTO> getAllSubs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return subscriptionService.getAll(page, size, sortBy, sortDir);
    }

    // DELETE METHODS (DELETE)
    @DeleteMapping("/customer/{id}")
    public ResponseEntity<ApiResponse> deleteCustomer(@PathVariable Long id) {
        customerService.delete(id); 
        return ResponseEntity.ok(new ApiResponse("Customer deleted successfully", null));
    }

    @PutMapping("/customer/{id}/activate")
    public ResponseEntity<ApiResponse> activateCustomer(@PathVariable Long id) {
        customerService.changeStatus(id, true);
        return ResponseEntity.ok(new ApiResponse("Customer Activated", null));
    }

    // 2. Assignment
    @PutMapping("/subscriptions/{subId}/assign-delivery/{dbId}")
    public ResponseEntity<ApiResponse> assign(@PathVariable Long subId, @PathVariable Long dbId) {
        subscriptionService.assignDeliveryBoy(subId, dbId);
        return ResponseEntity.ok(new ApiResponse("Assigned & Status is ACTIVE", null));
    }
    
 // Emergency ya Testing ke liye manually reset karne ka endpoint
    @PutMapping("/subscriptions/reset-daily-status")
    public ResponseEntity<ApiResponse> manualReset() {
        tiffinScheduler.resetDailyTiffinStatus(); // Scheduler ka method call karein
        return ResponseEntity.ok(new ApiResponse("All subscriptions reset to ACTIVE for today!", null));
    }
}

