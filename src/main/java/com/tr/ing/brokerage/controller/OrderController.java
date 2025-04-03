package com.tr.ing.brokerage.controller;

import com.tr.ing.brokerage.api.OrderAPI;
import com.tr.ing.brokerage.dto.OrderDTO;
import com.tr.ing.brokerage.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.tr.ing.brokerage.constant.AppConstants.API_BASE_PATH;

@Slf4j
@RestController
@RequestMapping(API_BASE_PATH + "/orders")
@RequiredArgsConstructor
public class OrderController implements OrderAPI {

    private final OrderService orderService;

    @Override
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        log.debug("Attempting to create new order for customer: {}", orderDTO.getCustomerId());
        OrderDTO createdOrder = orderService.createOrder(orderDTO);
        log.info("Order created successfully with ID: {}", createdOrder.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @Override
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("#customerId == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<List<OrderDTO>> getOrdersByCustomer(@PathVariable Long customerId) {
        log.debug("Fetching all orders for customer ID: {}", customerId);
        List<OrderDTO> orders = orderService.getOrdersByCustomer(customerId);
        log.debug("Found {} orders for customer ID: {}", orders.size(), customerId);
        return ResponseEntity.ok(orders);
    }

    @Override
    @GetMapping("/customer/{customerId}/date-range")
    @PreAuthorize("(hasRole('USER') and #customerId == authentication.principal.id) or hasRole('ADMIN')")
    public ResponseEntity<List<OrderDTO>> getOrdersByCustomerAndDateRange(
            @PathVariable Long customerId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        log.debug("Fetching orders for customer ID: {} between {} and {}", customerId, startDate, endDate);
        List<OrderDTO> orders = orderService.getOrdersByCustomerAndDateRange(customerId, startDate, endDate);
        log.debug("Found {} orders in date range for customer ID: {}", orders.size(), customerId);
        return ResponseEntity.ok(orders);
    }

    @Override
    @PostMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        log.debug("Attempting to cancel order with ID: {}", orderId);
        orderService.cancelOrder(orderId);
        log.info("Order {} cancelled successfully", orderId);
        return ResponseEntity.noContent().build();
    }
}