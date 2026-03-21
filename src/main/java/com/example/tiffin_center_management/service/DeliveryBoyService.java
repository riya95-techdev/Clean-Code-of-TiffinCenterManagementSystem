package com.example.tiffin_center_management.service;

import org.springframework.data.domain.Page;

import com.example.tiffin_center_management.payload.DeliveryBoyDTO;

public interface DeliveryBoyService {

	DeliveryBoyDTO create(DeliveryBoyDTO dto);
    Page<DeliveryBoyDTO> getAll(int page, int size, String sortBy, String sortDir);
    DeliveryBoyDTO getById(Long id);
    DeliveryBoyDTO update(Long id, DeliveryBoyDTO dto);
    void delete(Long id);
	
}
