package com.tr.ing.brokerage.service.impl;

import com.tr.ing.brokerage.dto.OrderDTO;
import com.tr.ing.brokerage.entity.Customer;
import com.tr.ing.brokerage.entity.Order;
import com.tr.ing.brokerage.enums.Side;
import com.tr.ing.brokerage.enums.Status;
import com.tr.ing.brokerage.exception.CustomerNotFoundException;
import com.tr.ing.brokerage.exception.OrderNotFoundException;
import com.tr.ing.brokerage.exception.OrderNotPendingException;
import com.tr.ing.brokerage.helper.AssetValidationHelper;
import com.tr.ing.brokerage.repository.CustomerRepository;
import com.tr.ing.brokerage.repository.OrderRepository;
import com.tr.ing.brokerage.service.OrderService;
import com.tr.ing.brokerage.util.BaseModelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final AssetValidationHelper assetValidationHelper;
    private final BaseModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {

        Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + orderDTO.getCustomerId()));

        validateOrderBalances(orderDTO, customer.getId());

        Order order = modelMapper.convertToEntity(orderDTO, Order.class);
        order.setCustomer(customer);
        Order savedOrder = orderRepository.save(order);

        assetValidationHelper.processOrder(modelMapper.convertToDto(savedOrder, OrderDTO.class));

        log.info("Created order {} for customer {}", savedOrder.getId(), customer.getId());
        return modelMapper.convertToDto(savedOrder, OrderDTO.class);
    }

    private void validateOrderBalances(OrderDTO orderDTO, Long customerId) {
        if (orderDTO.getSide() == Side.BUY) {
            BigDecimal totalAmount = orderDTO.getPrice().multiply(orderDTO.getSize());
            assetValidationHelper.validateTryBalance(customerId, totalAmount);
        } else {
            assetValidationHelper.validateAssetBalance(
                    customerId,
                    orderDTO.getAssetName(),
                    orderDTO.getSize()
            );
        }
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(order -> modelMapper.convertToDto(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByCustomerAndDateRange(Long customerId,
                                                          LocalDateTime startDate,
                                                          LocalDateTime endDate) {
        return orderRepository.findByCustomerIdAndCreateDateBetween(customerId, startDate, endDate).stream()
                .map(order -> modelMapper.convertToDto(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() != Status.PENDING) {
            throw new OrderNotPendingException("Order with id " + orderId +
                    " is not pending. Current status: " + order.getStatus());
        }

        order.setStatus(Status.CANCELED);
        Order canceledOrder = orderRepository.save(order);

        assetValidationHelper.restoreOrderAssets(modelMapper.convertToDto(canceledOrder, OrderDTO.class));
        log.info("Cancelled order {}", orderId);
    }
}