package com.app.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dto.CustomerCreateDto;
import com.app.dto.CustomerDto;
import com.app.dto.PageResponse;
import com.app.entities.Customer;
import com.app.exceptions.ResourceNotFoundException;
import com.app.mapper.CustomerMapper;
import com.app.mapper.PageMapper;
import com.app.repos.CustomerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final PageMapper pageMapper;
    
    @Transactional(readOnly = true)
    public PageResponse<CustomerDto> getAllCustomers(Pageable pageable) {
        Page<Customer> page = customerRepository.findAll(pageable);
        List<CustomerDto> content = customerMapper.toDTOList(page.getContent());
        return pageMapper.toPageResponse(page, content);
    }
    
    @Transactional(readOnly = true)
    public List<CustomerDto> getAllCustomersList() {
        return customerRepository.findAll().stream()
            .map(customerMapper::toDTO)
            .toList();
    }
    
    @Transactional(readOnly = true)
    public PageResponse<CustomerDto> searchCustomers(String query, Pageable pageable) {
        Page<Customer> page = customerRepository.searchCustomers(query, pageable);
        List<CustomerDto> content = customerMapper.toDTOList(page.getContent());
        return pageMapper.toPageResponse(page, content);
    }
    
    @Transactional(readOnly = true)
    public CustomerDto getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return customerMapper.toDTO(customer);
    }
    
    @Transactional(readOnly = true)
    public CustomerDto getCustomerByCode(String code) {
        Customer customer = customerRepository.findByCode(code)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with code: " + code));
        return customerMapper.toDTO(customer);
    }
    
    public CustomerDto createCustomer(CustomerCreateDto dto) {
        if (customerRepository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException("Customer code already exists: " + dto.getCode());
        }
        
        Customer customer = customerMapper.toEntity(dto);
        Customer savedCustomer = customerRepository.save(customer);
        
        log.info("Customer created: {} ({})", savedCustomer.getName(), savedCustomer.getCode());
        return customerMapper.toDTO(savedCustomer);
    }
    
    public CustomerDto updateCustomer(Long id, CustomerCreateDto dto) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        
        if (!customer.getCode().equals(dto.getCode()) && 
            customerRepository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException("Customer code already exists: " + dto.getCode());
        }
        
        customerMapper.updateEntityFromDTO(dto, customer);
        
        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Customer updated: {}", updatedCustomer.getCode());
        
        return customerMapper.toDTO(updatedCustomer);
    }
    
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        
        if (!customer.getSalesOrders().isEmpty()) {
            throw new IllegalStateException(
                "Cannot delete customer with existing sales orders."
            );
        }
        
        customerRepository.delete(customer);
        log.info("Customer deleted: {}", customer.getCode());
    }
}