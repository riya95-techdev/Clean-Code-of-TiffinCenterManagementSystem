package com.example.tiffin_center_management.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.example.tiffin_center_management.exception.ResourceNotFoundException;
import com.example.tiffin_center_management.model.Customer;
import com.example.tiffin_center_management.payload.CustomerDTO;
import com.example.tiffin_center_management.payload.DeliveryBoyDTO;
import com.example.tiffin_center_management.payload.SubscriptionDTO;
import com.example.tiffin_center_management.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final CustomerService customerService;
    private final DeliveryBoyService deliveryService;
    private final SubscriptionService subscriptionService;
    private final CustomerRepository customerRepo;
    
    @Override
    public Page<CustomerDTO> getAllCustomers(int page, int size, String sortBy, String sortDir) {
        return customerService.getAll(page, size, sortBy, sortDir);
    }

    @Override
    public Page<DeliveryBoyDTO> getAllDeliveryBoys(int page, int size, String sortBy, String sortDir) {
        return deliveryService.getAll(page, size, sortBy, sortDir);
    }

    @Override
    public Page<SubscriptionDTO> getAllSubscriptions(int page, int size, String sortBy, String sortDir) {
        return subscriptionService.getAll(page, size, sortBy, sortDir);
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        return customerService.getById(id);
    }

    @Override
    public DeliveryBoyDTO getDeliveryBoyById(Long id) {
        return deliveryService.getById(id);
    }

    @Override
    public SubscriptionDTO getSubscriptionById(Long id) {
        return subscriptionService.getById(id);
    }

    @Override
    public void deleteCustomer(Long id) {
        // Pehle check karega, agar nahi mila toh exception throw karega
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        customerRepo.delete(customer);
    }

    @Override
    public void deleteDeliveryBoy(Long id) {
        deliveryService.delete(id);
    }

    @Override
    public void deleteSubscription(Long id) {
        subscriptionService.delete(id);
    }

    // Isse controller se connect karein
    @Override
    public void assignDeliveryBoy(Long subId, Long deliveryBoyId) {
        subscriptionService.assignDeliveryBoy(subId, deliveryBoyId);
    }
}

