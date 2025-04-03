package com.tr.ing.brokerage.service;

import com.tr.ing.brokerage.dto.OrderDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    OrderDTO createOrder(OrderDTO orderDTO);

    List<OrderDTO> getOrdersByCustomer(Long customerId);

    List<OrderDTO> getOrdersByCustomerAndDateRange(Long customerId, LocalDateTime startDate, LocalDateTime endDate);

    void cancelOrder(Long orderId);
}
