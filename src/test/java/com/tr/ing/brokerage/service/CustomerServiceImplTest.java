package com.tr.ing.brokerage.service;

import com.tr.ing.brokerage.dto.CustomerDTO;
import com.tr.ing.brokerage.entity.Customer;
import com.tr.ing.brokerage.exception.CustomerAlreadyExistException;
import com.tr.ing.brokerage.exception.CustomerNotFoundException;
import com.tr.ing.brokerage.exception.CustomerValidationException;
import com.tr.ing.brokerage.repository.CustomerRepository;
import com.tr.ing.brokerage.service.impl.CustomerServiceImpl;
import com.tr.ing.brokerage.util.BaseModelMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BaseModelMapper modelMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setCustomerId("CUST001");
        customer.setName("John Doe");
        customer.setIsDeleted(false);

        customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setCustomerId("CUST001");
        customerDTO.setName("John Doe");
    }

    @Test
    void createCustomer_Success() {
        when(customerRepository.existsByCustomerId(anyString())).thenReturn(false);
        when(modelMapper.convertToEntity(any(CustomerDTO.class), eq(Customer.class))).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(modelMapper.convertToDto(any(Customer.class), eq(CustomerDTO.class))).thenReturn(customerDTO);

        CustomerDTO result = customerService.createCustomer(customerDTO);

        assertNotNull(result);
        assertEquals(customerDTO.getId(), result.getId());
        verify(customerRepository, times(1)).existsByCustomerId(anyString());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void createCustomer_ThrowsCustomerAlreadyExistException() {
        when(customerRepository.existsByCustomerId(anyString())).thenReturn(true);

        assertThrows(CustomerAlreadyExistException.class, () ->
                customerService.createCustomer(customerDTO));
        verify(customerRepository, times(1)).existsByCustomerId(anyString());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void getCustomerById_Success() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        when(modelMapper.convertToDto(any(Customer.class), eq(CustomerDTO.class))).thenReturn(customerDTO);

        CustomerDTO result = customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals(customerDTO.getId(), result.getId());
        verify(customerRepository, times(1)).findById(anyLong());
    }

    @Test
    void getCustomerById_ThrowsCustomerNotFoundException() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () ->
                customerService.getCustomerById(1L));
        verify(customerRepository, times(1)).findById(anyLong());
    }

    @Test
    void getCustomerByCustomerId_Success() {
        when(customerRepository.findByCustomerId(anyString())).thenReturn(Optional.of(customer));
        when(modelMapper.convertToDto(any(Customer.class), eq(CustomerDTO.class))).thenReturn(customerDTO);

        CustomerDTO result = customerService.getCustomerByCustomerId("CUST001");

        assertNotNull(result);
        assertEquals(customerDTO.getCustomerId(), result.getCustomerId());
        verify(customerRepository, times(1)).findByCustomerId(anyString());
    }

    @Test
    void getCustomerByCustomerId_ThrowsCustomerNotFoundException() {
        when(customerRepository.findByCustomerId(anyString())).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () ->
                customerService.getCustomerByCustomerId("CUST001"));
        verify(customerRepository, times(1)).findByCustomerId(anyString());
    }

    @Test
    void searchCustomersByName_Success() {
        when(customerRepository.findByNameContainingIgnoreCase(anyString()))
                .thenReturn(Collections.singletonList(customer));
        when(modelMapper.convertToDto(any(Customer.class), eq(CustomerDTO.class))).thenReturn(customerDTO);

        List<CustomerDTO> result = customerService.searchCustomersByName("John");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(customerRepository, times(1)).findByNameContainingIgnoreCase(anyString());
    }

    @Test
    void searchCustomersByName_EmptyResult() {
        when(customerRepository.findByNameContainingIgnoreCase(anyString()))
                .thenReturn(Collections.emptyList());

        List<CustomerDTO> result = customerService.searchCustomersByName("Unknown");

        assertTrue(result.isEmpty());
        verify(customerRepository, times(1)).findByNameContainingIgnoreCase(anyString());
    }

    @Test
    void updateCustomer_Success() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(modelMapper.convertToDto(any(Customer.class), eq(CustomerDTO.class))).thenReturn(customerDTO);

        CustomerDTO result = customerService.updateCustomer(1L, customerDTO);

        assertNotNull(result);
        verify(customerRepository, times(1)).findById(anyLong());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void updateCustomer_ThrowsCustomerNotFoundException() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () ->
                customerService.updateCustomer(1L, customerDTO));
        verify(customerRepository, times(1)).findById(anyLong());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void updateCustomer_ThrowsCustomerValidationException() {
        CustomerDTO differentCustomerDTO = new CustomerDTO();
        differentCustomerDTO.setCustomerId("DIFFERENT");

        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));

        assertThrows(CustomerValidationException.class, () ->
                customerService.updateCustomer(1L, differentCustomerDTO));
        verify(customerRepository, times(1)).findById(anyLong());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_Success() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        assertDoesNotThrow(() -> customerService.deleteCustomer(1L));
        assertTrue(customer.getIsDeleted());
        verify(customerRepository, times(1)).findById(anyLong());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_ThrowsCustomerNotFoundException() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () ->
                customerService.deleteCustomer(1L));
        verify(customerRepository, times(1)).findById(anyLong());
        verify(customerRepository, never()).save(any(Customer.class));
    }
}