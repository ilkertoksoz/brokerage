package com.tr.ing.brokerage.service;

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
import com.tr.ing.brokerage.service.impl.OrderServiceImpl;
import com.tr.ing.brokerage.util.BaseModelMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AssetValidationHelper assetValidationHelper;

    @Mock
    private BaseModelMapper modelMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private OrderDTO orderDTO;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setCustomerId("CUST001");

        order = new Order();
        order.setId(1L);
        order.setCustomer(customer);
        order.setAssetName("BTC");
        order.setSide(Side.BUY);
        order.setSize(BigDecimal.valueOf(1.5));
        order.setPrice(BigDecimal.valueOf(50000));
        order.setStatus(Status.PENDING);
        order.setCreateDate(LocalDateTime.now());

        orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setCustomerId(1L);
        orderDTO.setAssetName("BTC");
        orderDTO.setSide(Side.BUY);
        orderDTO.setSize(BigDecimal.valueOf(1.5));
        orderDTO.setPrice(BigDecimal.valueOf(50000));
        orderDTO.setStatus(Status.PENDING);
    }

    @Test
    void createOrder_Success() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        when(modelMapper.convertToEntity(any(OrderDTO.class), eq(Order.class))).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(modelMapper.convertToDto(any(Order.class), eq(OrderDTO.class))).thenReturn(orderDTO);
        doNothing().when(assetValidationHelper).processOrder(any(OrderDTO.class));
        doNothing().when(assetValidationHelper).validateTryBalance(anyLong(), any(BigDecimal.class));

        OrderDTO result = orderService.createOrder(orderDTO);

        assertNotNull(result);
        assertEquals(orderDTO.getId(), result.getId());
        verify(customerRepository, times(1)).findById(anyLong());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(assetValidationHelper, times(1)).processOrder(any(OrderDTO.class));
    }

    @Test
    void createOrder_CustomerNotFound() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () ->
                orderService.createOrder(orderDTO));
        verify(customerRepository, times(1)).findById(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_SELL_Success() {
        orderDTO.setSide(Side.SELL);
        order.setSide(Side.SELL);

        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        when(modelMapper.convertToEntity(any(OrderDTO.class), eq(Order.class))).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(modelMapper.convertToDto(any(Order.class), eq(OrderDTO.class))).thenReturn(orderDTO);
        doNothing().when(assetValidationHelper).processOrder(any(OrderDTO.class));
        doNothing().when(assetValidationHelper).validateAssetBalance(anyLong(), anyString(), any(BigDecimal.class));

        OrderDTO result = orderService.createOrder(orderDTO);

        assertNotNull(result);
        assertEquals(Side.SELL, result.getSide());
        verify(assetValidationHelper, times(1)).validateAssetBalance(anyLong(), anyString(), any(BigDecimal.class));
    }

    @Test
    void getOrdersByCustomer_Success() {

        when(orderRepository.findByCustomerId(anyLong())).thenReturn(Optional.of(order));

        when(modelMapper.convertToDto(any(Order.class), eq(OrderDTO.class))).thenReturn(orderDTO);

        List<OrderDTO> result = orderService.getOrdersByCustomer(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findByCustomerId(anyLong());
    }

    @Test
    void getOrdersByCustomer_Empty() {
        when(orderRepository.findByCustomerId(anyLong()))
                .thenReturn(Optional.empty());

        List<OrderDTO> result = orderService.getOrdersByCustomer(1L);

        assertTrue(result.isEmpty());
        verify(orderRepository, times(1)).findByCustomerId(anyLong());
    }

    @Test
    void getOrdersByCustomerAndDateRange_Success() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        when(orderRepository.findByCustomerIdAndCreateDateBetween(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(order));
        when(modelMapper.convertToDto(any(Order.class), eq(OrderDTO.class))).thenReturn(orderDTO);

        List<OrderDTO> result = orderService.getOrdersByCustomerAndDateRange(1L, start, end);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(orderRepository, times(1))
                .findByCustomerIdAndCreateDateBetween(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void cancelOrder_Success() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(modelMapper.convertToDto(any(Order.class), eq(OrderDTO.class))).thenReturn(orderDTO);
        doNothing().when(assetValidationHelper).restoreOrderAssets(any(OrderDTO.class));

        assertDoesNotThrow(() -> orderService.cancelOrder(1L));
        assertEquals(Status.CANCELED, order.getStatus());
        verify(orderRepository, times(1)).findById(anyLong());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(assetValidationHelper, times(1)).restoreOrderAssets(any(OrderDTO.class));
    }

    @Test
    void cancelOrder_OrderNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () ->
                orderService.cancelOrder(1L));
        verify(orderRepository, times(1)).findById(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void cancelOrder_NotPending() {
        order.setStatus(Status.MATCHED);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        assertThrows(OrderNotPendingException.class, () ->
                orderService.cancelOrder(1L));
        verify(orderRepository, times(1)).findById(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }
}