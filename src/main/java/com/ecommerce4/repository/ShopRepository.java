package com.ecommerce4.repository;

import com.ecommerce4.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Integer> {
    List<Shop> findByUser_UserId(Integer userId);
    Optional<Shop> findById(Integer shopId);


}
