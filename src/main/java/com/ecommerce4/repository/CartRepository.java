package com.ecommerce4.repository;

import com.ecommerce4.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CartRepository extends JpaRepository<Cart, Integer> {
    Cart findByCustomer_CustomerId(Integer customerId);
}
