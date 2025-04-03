package com.tr.ing.brokerage.controller;

import com.tr.ing.brokerage.api.CustomerAPI;
import com.tr.ing.brokerage.dto.CustomerDTO;
import com.tr.ing.brokerage.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.tr.ing.brokerage.constant.AppConstants.API_BASE_PATH;

@Slf4j
@RestController
@RequestMapping(API_BASE_PATH + "/customers")
@RequiredArgsConstructor
public class CustomerController implements CustomerAPI {

    private final CustomerService customerService;

    @Override
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO) {
        log.debug("Creating customer: {}", customerDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.createCustomer(customerDTO));
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable final Long id) {
        log.debug("Fetching customer by ID: {}", id);
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @Override
    @GetMapping("/customer-id/{customerId}")
    @PreAuthorize("(hasRole('USER') and #customerId == authentication.principal.username) or hasRole('ADMIN')")
    public ResponseEntity<CustomerDTO> getCustomerByCustomerId(@PathVariable final String customerId) {
        log.debug("Fetching customer by customer ID: {}", customerId);
        return ResponseEntity.ok(customerService.getCustomerByCustomerId(customerId));
    }

    @Override
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerDTO>> searchCustomersByName(@RequestParam String name) {
        log.debug("Searching customers by name: {}", name);
        return ResponseEntity.ok(customerService.searchCustomersByName(name));
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable final Long id,
            @RequestBody CustomerDTO customerDTO) {
        log.debug("Updating customer with ID: {}", id);
        return ResponseEntity.ok(customerService.updateCustomer(id, customerDTO));
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        log.debug("Processing delete request for customer ID: {}", id);
        customerService.deleteCustomer(id);
        log.debug("Customer deletion processed for ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}