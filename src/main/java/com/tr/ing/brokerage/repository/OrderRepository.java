package com.tr.ing.brokerage.repository;

import com.tr.ing.brokerage.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"customer"})
    Optional<Order> findByCustomerId(Long id);

    @EntityGraph(attributePaths = {"customer"})
    List<Order> findByCustomerIdAndCreateDateBetween(Long customerId, LocalDateTime startDate, LocalDateTime endDate);

    @EntityGraph(attributePaths = {"customer"})
    Optional<Order> findByIdAndCustomerId(Long id, Long customerId);

}
