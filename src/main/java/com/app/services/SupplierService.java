package com.app.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dto.PageResponse;
import com.app.dto.SupplierCreateDto;
import com.app.dto.SupplierDto;
import com.app.entities.Supplier;
import com.app.exceptions.ResourceNotFoundException;
import com.app.mapper.PageMapper;
import com.app.mapper.SupplierMapper;
import com.app.repos.SupplierRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SupplierService {
    
    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private final PageMapper pageMapper;
    
    @Transactional(readOnly = true)
    public PageResponse<SupplierDto> getAllSuppliers(Pageable pageable) {
        Page<Supplier> page = supplierRepository.findAll(pageable);
        List<SupplierDto> content = supplierMapper.toDTOList(page.getContent());
        return pageMapper.toPageResponse(page, content);
    }
    
    @Transactional(readOnly = true)
    public List<SupplierDto> getAllSuppliersList() {
        return supplierRepository.findAll().stream()
            .map(supplierMapper::toDTO)
            .toList();
    }
    
    @Transactional(readOnly = true)
    public PageResponse<SupplierDto> searchSuppliers(String query, Pageable pageable) {
        Page<Supplier> page = supplierRepository.searchSuppliers(query, pageable);
        List<SupplierDto> content = supplierMapper.toDTOList(page.getContent());
        return pageMapper.toPageResponse(page, content);
    }
    
    @Transactional(readOnly = true)
    public SupplierDto getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        return supplierMapper.toDTO(supplier);
    }
    
    public SupplierDto createSupplier(SupplierCreateDto dto) {
        Supplier supplier = supplierMapper.toEntity(dto);
        Supplier savedSupplier = supplierRepository.save(supplier);
        
        log.info("Supplier created: {}", savedSupplier.getName());
        return supplierMapper.toDTO(savedSupplier);
    }
    
    public SupplierDto updateSupplier(Long id, SupplierCreateDto dto) {
        Supplier supplier = supplierRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        
        supplierMapper.updateEntityFromDTO(dto, supplier);
        
        Supplier updatedSupplier = supplierRepository.save(supplier);
        log.info("Supplier updated: {}", updatedSupplier.getName());
        
        return supplierMapper.toDTO(updatedSupplier);
    }
    
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        
        if (!supplier.getProducts().isEmpty()) {
            throw new IllegalStateException(
                "Cannot delete supplier with existing products. Reassign products first."
            );
        }
        
        if (!supplier.getPurchaseOrders().isEmpty()) {
            throw new IllegalStateException(
                "Cannot delete supplier with existing purchase orders."
            );
        }
        
        supplierRepository.delete(supplier);
        log.info("Supplier deleted with id: {}", id);
    }
}