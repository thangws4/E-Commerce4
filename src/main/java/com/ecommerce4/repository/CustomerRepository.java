package com.ecommerce4.repository;

import com.ecommerce4.entity.Customer;
import com.ecommerce4.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findById(Integer customerId);

    Optional<Customer> findByUser_UserId(Integer userId);
}
