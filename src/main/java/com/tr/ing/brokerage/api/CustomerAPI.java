package com.tr.ing.brokerage.api;

import com.tr.ing.brokerage.dto.CustomerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Customer", description = "API for managing Customers")
@SecurityRequirement(name = "access_token")
public interface CustomerAPI {

    @Operation(method = "POST", summary = "Create a new customer")
    ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO);

    @Operation(method = "GET", summary = "Get customer by ID")
    ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id);

    @Operation(method = "GET", summary = "Get customer by customer ID")
    ResponseEntity<CustomerDTO> getCustomerByCustomerId(@PathVariable String customerId);

    @Operation(method = "GET", summary = "Search customers by name")
    ResponseEntity<List<CustomerDTO>> searchCustomersByName(@RequestParam String name);

    @Operation(method = "PUT", summary = "Update customer")
    ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerDTO customerDTO);

    @Operation(method = "DELETE", summary = "Delete customer")
    ResponseEntity<Void> deleteCustomer(@PathVariable Long id);
}
