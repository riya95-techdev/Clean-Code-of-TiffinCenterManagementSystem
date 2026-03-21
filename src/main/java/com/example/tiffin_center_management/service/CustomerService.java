package com.example.tiffin_center_management.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import com.example.tiffin_center_management.payload.CustomerDTO;

public interface CustomerService {

	CustomerDTO create(CustomerDTO dto);
    List<CustomerDTO> createBulk(List<CustomerDTO> list);
    Page<CustomerDTO> getAll(int page, int size, String sortBy, String sortDir);
    CustomerDTO getById(Long id);
    CustomerDTO update(Long id, CustomerDTO dto);
    CustomerDTO partialUpdate(Long id, Map<String, Object> updates);
    void changeStatus(Long id, boolean active);
    void delete(Long id);
    void deleteBulk(List<Long> ids);
    List<CustomerDTO> search(String keyword);
	
}
