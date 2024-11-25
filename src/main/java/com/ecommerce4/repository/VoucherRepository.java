package com.ecommerce4.repository;

import com.ecommerce4.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    List<Voucher> findByIsActive(Boolean isActive);
    List<Voucher> findByExpiryDateAfter(Date date);
}
