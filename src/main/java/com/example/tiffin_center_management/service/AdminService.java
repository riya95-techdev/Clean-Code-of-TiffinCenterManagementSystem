package com.example.tiffin_center_management.service;

import org.springframework.data.domain.Page;

import com.example.tiffin_center_management.payload.CustomerDTO;
import com.example.tiffin_center_management.payload.DeliveryBoyDTO;
import com.example.tiffin_center_management.payload.SubscriptionDTO;

public interface AdminService {

	Page<CustomerDTO> getAllCustomers(int page, int size,String sortBy, String sortDir);
    Page<DeliveryBoyDTO> getAllDeliveryBoys(int page, int size, String sortBy, String sortDir);
    Page<SubscriptionDTO> getAllSubscriptions(int page, int size, String sortBy, String sortDir);
    CustomerDTO getCustomerById(Long id);
    DeliveryBoyDTO getDeliveryBoyById(Long id);
    SubscriptionDTO getSubscriptionById(Long id);
    void deleteCustomer(Long id);
    void deleteDeliveryBoy(Long id);
    void deleteSubscription(Long id);
    void assignDeliveryBoy(Long subId, Long deliveryBoyId);
	
}
