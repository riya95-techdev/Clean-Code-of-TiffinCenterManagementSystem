package com.example.tiffin_center_management.service;

import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.tiffin_center_management.exception.ResourceNotFoundException;
import com.example.tiffin_center_management.model.Customer;
import com.example.tiffin_center_management.payload.CustomerDTO;
import com.example.tiffin_center_management.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService{

	private final CustomerRepository repository;
    private final ModelMapper mapper;

    @Override
    public CustomerDTO create(CustomerDTO dto) {
        Customer entity = mapper.map(dto, Customer.class);
        return mapper.map(repository.save(entity), CustomerDTO.class);
    }

    @Override
    public List<CustomerDTO> createBulk(List<CustomerDTO> list) {
        List<Customer> entities = list.stream()
                .map(d -> mapper.map(d, Customer.class))
                .toList();

        return repository.saveAll(entities).stream()
                .map(e -> mapper.map(e, CustomerDTO.class))
                .toList();
    }

    @Override
    public Page<CustomerDTO> getAll(int page, int size, String sortBy, String sortDir) {

        Sort sort = sortDir.equals("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort.by(Sort.Direction.fromString(sortDir), sortBy));

        return repository.findAllCustomers(pageable)
                .map(e -> mapper.map(e, CustomerDTO.class));
    }

    @Override
    public CustomerDTO getById(Long id) {
        Customer c = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return mapper.map(c, CustomerDTO.class);
    }

    @Override
    public CustomerDTO update(Long id, CustomerDTO dto) {
        Customer c = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        mapper.map(dto, c);
        return mapper.map(repository.save(c), CustomerDTO.class);
    }

    @Override
    public CustomerDTO partialUpdate(Long id, Map<String, Object> updates) {
        Customer c = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        updates.forEach((k, v) -> {
            switch (k) {
                case "name" -> c.setName((String) v);
                case "phone" -> c.setPhone((String) v);
            }
        });

        return mapper.map(repository.save(c), CustomerDTO.class);
    }

    @Override
    public void changeStatus(Long id, boolean active) {
        Customer c = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        c.setActive(active);
        repository.save(c);
    }

    @Override
    public void delete(Long id) {
    	// 1. Pehle check karo ki exist karta hai ya nahi
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        // 2. Agar mil gaya tabhi delete hoga
        repository.delete(customer);
    }

    @Override
    public void deleteBulk(List<Long> ids) {
        repository.deleteAllById(ids);
    }

    @Override
    public List<CustomerDTO> search(String keyword) {
        return repository.findByNameContaining(keyword).stream()
                .map(e -> mapper.map(e, CustomerDTO.class))
                .toList();
    }

}
