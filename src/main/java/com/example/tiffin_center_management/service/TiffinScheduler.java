package com.example.tiffin_center_management.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.tiffin_center_management.model.Subscription;
import com.example.tiffin_center_management.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TiffinScheduler {

	private final SubscriptionRepository subscriptionRepo;

	// Ye Cron Expression "0 0 0 * * *" ka matlab hai: Roz Raat 12:00:00 AM
	@Scheduled(cron = "0 0 0 * * *") // Roz raat 12 baje
	public void resetDailyTiffinStatus() {
	    LocalDate today = LocalDate.now();
	    
	    // Sirf wahi dhoondo jo COMPLETED hain aur expiry date abhi baki hai
	    List<Subscription> subsToReset = subscriptionRepo.findAll().stream()
	            .filter(s -> "COMPLETED".equals(s.getStatus()) || "ACTIVE".equals(s.getStatus()))
	            .toList();

	    for (Subscription sub : subsToReset) {
	        if (sub.getEndDate().isBefore(today)) {
	            sub.setStatus("EXPIRED");
	        } else {
	            sub.setStatus("ACTIVE"); // Reset for next day
	        }
	    }
	    subscriptionRepo.saveAll(subsToReset);
	}	
}
