package com.tr.ing.brokerage.api;

import com.tr.ing.brokerage.dto.OrderDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Order", description = "API for managing Stock Orders")
@SecurityRequirement(name = "access_token")
public interface OrderAPI {

    @Operation(method = "POST", summary = "Create a new order", operationId = "createOrder")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO);

    @Operation(method = "GET", summary = "Get orders by customer", operationId = "getOrdersByCustomer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    ResponseEntity<List<OrderDTO>> getOrdersByCustomer(@RequestParam Long customerId);

    @Operation(method = "GET", summary = "Get orders by customer and date range", operationId = "getOrdersByCustomerAndDateRange")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    ResponseEntity<List<OrderDTO>> getOrdersByCustomerAndDateRange(
            @RequestParam Long customerId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate
    );

    @Operation(method = "POST", summary = "Cancel an order", operationId = "cancelOrder")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order cancelled"),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    ResponseEntity<Void> cancelOrder(@RequestParam Long orderId);
}