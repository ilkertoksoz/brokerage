package com.tr.ing.brokerage.service.impl;

import com.tr.ing.brokerage.dto.CustomerDTO;
import com.tr.ing.brokerage.entity.Customer;
import com.tr.ing.brokerage.exception.CustomerAlreadyExistException;
import com.tr.ing.brokerage.exception.CustomerNotFoundException;
import com.tr.ing.brokerage.exception.CustomerValidationException;
import com.tr.ing.brokerage.repository.CustomerRepository;
import com.tr.ing.brokerage.service.CustomerService;
import com.tr.ing.brokerage.util.BaseModelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {


    private final CustomerRepository customerRepository;
    private final BaseModelMapper modelMapper;

    @Override
    @Transactional
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {

        if (customerRepository.existsByCustomerId(customerDTO.getCustomerId())) {

            throw new CustomerAlreadyExistException("Customer already exists: " + customerDTO.getCustomerId());
        }

        log.debug("Customer creation failed - ID {} already exists", customerDTO.getCustomerId());

        return modelMapper.convertToDto(customerRepository.save(modelMapper.convertToEntity(customerDTO, Customer.class)), CustomerDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(Long id) {

        log.debug("Customer not found by ID {}", id);

        return modelMapper.convertToDto(
                customerRepository.findById(id)
                        .orElseThrow(() -> new CustomerNotFoundException("Customer not found - ID: " + id)),
                CustomerDTO.class
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerByCustomerId(String customerId) {

        log.debug("Customer not found by customerId {}", customerId);

        return modelMapper.convertToDto(
                customerRepository.findByCustomerId(customerId)
                        .orElseThrow(() -> new CustomerNotFoundException("Customer not found - CustomerID: " + customerId)),
                CustomerDTO.class
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> searchCustomersByName(String name) {

        log.debug("Searching customers by name: {}", name);

        return customerRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(customer -> modelMapper.convertToDto(customer, CustomerDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        log.debug("Attempting to update customer with ID: {}", id);

        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", id);
                    return new CustomerNotFoundException("Customer not found - ID: " + id);
                });

        if (!existingCustomer.getCustomerId().equals(customerDTO.getCustomerId())) {

            log.warn("Customer ID change attempt detected for customer: {}", id);

            throw new CustomerValidationException("Customer ID cannot be changed");
        }

        existingCustomer.setName(customerDTO.getName());
        existingCustomer.setIsDeleted(customerDTO.getIsDeleted());

        log.debug("Customer with ID: {} updated successfully", id);

        return modelMapper.convertToDto(existingCustomer, CustomerDTO.class);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        log.debug("Attempting to delete customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Customer not found for deletion - ID: {}", id);
                    return new CustomerNotFoundException("Customer not found - ID: " + id);
                });

        customer.setIsDeleted(true);
        customerRepository.save(customer);
        log.info("Successfully soft-deleted customer with ID: {}", id);
    }
}