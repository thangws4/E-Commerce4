package com.ecommerce4.repository;

import com.ecommerce4.entity.Product;
import com.ecommerce4.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByShop_ShopId(Integer shopId); // Updated method to reference shopId properly
    List<Product> findByCategory_CategoryId(Integer categoryId); // Correct reference to categoryId
    Optional<Product> findByProductId(Integer productId);
    Page<Product> findAll(Pageable pageable);
}
