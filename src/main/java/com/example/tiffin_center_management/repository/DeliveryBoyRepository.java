package com.example.tiffin_center_management.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.tiffin_center_management.model.BaseUser;
import com.example.tiffin_center_management.model.DeliveryBoy;

@Repository
public interface DeliveryBoyRepository extends JpaRepository<DeliveryBoy, Long>{

	@Query("SELECT d FROM DeliveryBoy d WHERE d.role = 'DELIVERY'")
    Page<DeliveryBoy> findAllDeliveryBoys(Pageable pageable);
	Optional<? extends BaseUser> findByEmail(String email);

}
