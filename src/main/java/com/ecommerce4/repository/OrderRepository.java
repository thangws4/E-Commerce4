package com.ecommerce4.repository;

import com.ecommerce4.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByCustomer_CustomerId(Integer customerId);
    List<Order> deleteByCustomer_CustomerId(Integer customerId);
}
