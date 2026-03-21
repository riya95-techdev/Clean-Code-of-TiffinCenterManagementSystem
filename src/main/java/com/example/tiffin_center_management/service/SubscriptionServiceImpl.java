package com.example.tiffin_center_management.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tiffin_center_management.exception.ResourceNotFoundException;
import com.example.tiffin_center_management.model.Customer;
import com.example.tiffin_center_management.model.DeliveryBoy;
import com.example.tiffin_center_management.model.Subscription;
import com.example.tiffin_center_management.payload.SubscriptionDTO;
import com.example.tiffin_center_management.repository.CustomerRepository;
import com.example.tiffin_center_management.repository.DeliveryBoyRepository;
import com.example.tiffin_center_management.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepo;
    private final CustomerRepository customerRepo;
    private final DeliveryBoyRepository deliveryRepo;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public SubscriptionDTO create(SubscriptionDTO dto) {
        // 1. Check if Customer already has an ACTIVE or PENDING subscription
        // Ek customer ek baar mein ek hi tiffin khayega
        List<Subscription> existingSubs = subscriptionRepo.findByCustomerId(dto.getCustomerId(), Pageable.unpaged()).getContent();
        boolean hasActivePlan = existingSubs.stream()
                .anyMatch(s -> "ACTIVE".equals(s.getStatus()) || "PENDING".equals(s.getStatus()));

        if (hasActivePlan) {
            throw new RuntimeException("Customer already has an active or pending subscription!");
        }

        Customer customer = customerRepo.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", dto.getCustomerId()));

        Subscription subscription = mapper.map(dto, Subscription.class);
        subscription.setCustomer(customer);
        subscription.setDeliveryBoy(null); 
        subscription.setStatus("PENDING");

        Subscription savedSub = subscriptionRepo.save(subscription);
        return mapper.map(savedSub, SubscriptionDTO.class);
    }

    // ✅ GET ALL
    @Override
    public Page<SubscriptionDTO> getAll(int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(page, size);
        return subscriptionRepo.findAll(pageable)
                .map(sub -> mapper.map(sub, SubscriptionDTO.class));
    }

    // ✅ GET BY ID
    @Override
    public SubscriptionDTO getById(Long id) {
        Subscription sub = subscriptionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));
        return mapper.map(sub, SubscriptionDTO.class);
    }

    // ✅ UPDATE
    @Override
    @Transactional
    public SubscriptionDTO update(Long id, SubscriptionDTO dto) {
        Subscription sub = subscriptionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        Customer customer = customerRepo.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        sub.setCustomer(customer);
        sub.setPlan(dto.getPlan());
        sub.setPrice(dto.getPrice());
        sub.setStartDate(dto.getStartDate());
        sub.setEndDate(dto.getEndDate());
        sub.setStatus(dto.getStatus());

        if (dto.getDeliveryBoyId() != null) {
            DeliveryBoy db = deliveryRepo.findById(dto.getDeliveryBoyId())
                    .orElseThrow(() -> new ResourceNotFoundException("DeliveryBoy not found"));
            sub.setDeliveryBoy(db);
        } else {
            sub.setDeliveryBoy(null);
        }

        return mapper.map(subscriptionRepo.save(sub), SubscriptionDTO.class);
    }

    // ✅ DELETE
    @Override
    @Transactional
    public void delete(Long id) {
        Subscription sub = subscriptionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
        subscriptionRepo.delete(sub);
    }

    // ✅ GET BY CUSTOMER
    @Override
    public Page<SubscriptionDTO> getByCustomer(Long customerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return subscriptionRepo.findByCustomerId(customerId, pageable)
                .map(sub -> mapper.map(sub, SubscriptionDTO.class));
    }

    // ✅ GET BY DELIVERY BOY
    @Override
    public Page<SubscriptionDTO> getByDeliveryBoy(Long deliveryBoyId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return subscriptionRepo.findByDeliveryBoyCustom(deliveryBoyId, pageable)
                .map(sub -> mapper.map(sub, SubscriptionDTO.class));
    }

    // ✅ MARK DELIVERED
    @Override
    @Transactional
    public void markDelivered(Long id) {
        Subscription sub = subscriptionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
     // 2. Currently logged-in user ki email nikalo (SecurityContext se)
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        
        if (sub.getDeliveryBoy() == null) {
            throw new RuntimeException("No delivery boy assigned to this subscription yet!");
        }
     // 3. Check karo: Kya ye wahi Delivery Boy hai jise task assign hua tha?
        // subscription.getDb().getEmail() humein batayega assigned boy kaun hai
        if (!sub.getDeliveryBoy().getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("Security Alert: You are not authorized to deliver this tiffin!");
        }
        
        if (!"ACTIVE".equals(sub.getStatus())) {
            throw new RuntimeException("Tiffin cannot be delivered! Current status is: " + sub.getStatus());
        }
        sub.setStatus("COMPLETED"); 
        subscriptionRepo.save(sub);
    }
    
    // ✅ ASSIGN DELIVERY BOY (Status update added)
    @Override
    @Transactional
    public void assignDeliveryBoy(Long subId, Long deliveryBoyId) {
        Subscription sub = subscriptionRepo.findById(subId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        DeliveryBoy db = deliveryRepo.findById(deliveryBoyId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery Boy not found"));

        if (!db.isAvailable()) {
            throw new IllegalArgumentException("Delivery Boy is currently not available");
        }

        sub.setDeliveryBoy(db);
        sub.setStatus("ACTIVE"); // Jab assign ho jaye toh ACTIVE kar do
        subscriptionRepo.save(sub);
    }
}

