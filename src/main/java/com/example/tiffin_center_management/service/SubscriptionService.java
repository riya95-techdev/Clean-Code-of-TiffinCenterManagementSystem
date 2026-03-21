package com.example.tiffin_center_management.service;

import org.springframework.data.domain.Page;

import com.example.tiffin_center_management.payload.SubscriptionDTO;

public interface SubscriptionService {

	SubscriptionDTO create(SubscriptionDTO dto);
    Page<SubscriptionDTO> getAll(int page, int size, String sortBy, String sortDir);
    SubscriptionDTO getById(Long id);
    SubscriptionDTO update(Long id, SubscriptionDTO dto);
    void delete(Long id);
    Page<SubscriptionDTO> getByCustomer(Long customerId, int page, int size);
    Page<SubscriptionDTO> getByDeliveryBoy(Long deliveryBoyId, int page, int size);
    void markDelivered(Long id);    
    void assignDeliveryBoy(Long subId, Long deliveryBoyId);
}
