package com.example.tiffin_center_management.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.tiffin_center_management.exception.ResourceNotFoundException;
import com.example.tiffin_center_management.model.DeliveryBoy;
import com.example.tiffin_center_management.payload.DeliveryBoyDTO;
import com.example.tiffin_center_management.repository.DeliveryBoyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryBoyServiceImpl implements DeliveryBoyService
{

	@Autowired
	private final DeliveryBoyRepository repository;
	@Autowired
    private final ModelMapper mapper;

    // ✅ CREATE
    @Override
    public DeliveryBoyDTO create(DeliveryBoyDTO dto) {
        DeliveryBoy entity = mapper.map(dto, DeliveryBoy.class);
        DeliveryBoy saved = repository.save(entity);
        return mapper.map(saved, DeliveryBoyDTO.class);
    }
    
    @Override
    public Page<DeliveryBoyDTO> getAll(int page, int size, String sortBy, String sortDir) {
        // 1. Pagination set karein
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // 2. Nayi query call karein (taaki Customer na aayein isme)
        return repository.findAllDeliveryBoys(pageable)
                .map(db -> mapper.map(db, DeliveryBoyDTO.class));
    }

    // ✅ GET BY ID
    @Override
    public DeliveryBoyDTO getById(Long id) {
        DeliveryBoy entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryBoy not found with id " + id));

        return mapper.map(entity, DeliveryBoyDTO.class);
    }

    // ✅ UPDATE
    @Override
    public DeliveryBoyDTO update(Long id, DeliveryBoyDTO dto) {

        DeliveryBoy entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryBoy not found with id " + id));

        // update fields manually (safe way)
        entity.setName(dto.getName());
        entity.setPhone(dto.getPhone());
        entity.setVehicleNumber(dto.getVehicleNumber());
        entity.setAvailable(dto.isAvailable());

        DeliveryBoy updated = repository.save(entity);

        return mapper.map(updated, DeliveryBoyDTO.class);
    }

    // ✅ DELETE
    @Override
    public void delete(Long id) {

        DeliveryBoy entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryBoy", "id", id));

        repository.delete(entity);
    }
	
}
