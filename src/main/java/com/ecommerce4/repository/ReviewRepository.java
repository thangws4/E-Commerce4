package com.ecommerce4.repository;

import com.ecommerce4.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByProduct_ProductId(Integer productId);
    List<Review> findByCustomer_CustomerId(Integer customerId);
}
