package com.tr.ing.brokerage.service;

import com.tr.ing.brokerage.dto.CustomerDTO;

import java.util.List;

public interface CustomerService {

    CustomerDTO createCustomer(CustomerDTO customerDTO);

    CustomerDTO getCustomerById(Long id);

    CustomerDTO getCustomerByCustomerId(String customerId);

    List<CustomerDTO> searchCustomersByName(String name);

    CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO);

    void deleteCustomer(Long id);
}