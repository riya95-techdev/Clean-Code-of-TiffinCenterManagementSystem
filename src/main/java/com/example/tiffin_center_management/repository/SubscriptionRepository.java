package com.example.tiffin_center_management.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.tiffin_center_management.model.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long>{

	@Query("SELECT s FROM Subscription s WHERE s.deliveryBoy.id = :dbId") 
	Page<Subscription> findByDeliveryBoyCustom(@Param("dbId") Long dbId, Pageable pageable);
	Page<Subscription> findByCustomerId(Long customerId, Pageable pageable);

	
}
