package com.ecommerce4.service;

import com.ecommerce4.entity.Voucher;
import com.ecommerce4.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    public Optional<Voucher> getVoucherById(Integer id) {
        return voucherRepository.findById(id);
    }

    public Voucher createVoucher(Voucher voucher) {
        return voucherRepository.save(voucher);
    }

    public Voucher updateVoucher(Integer id, Voucher voucherDetails) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));
        voucher.setCode(voucherDetails.getCode());
        voucher.setDiscount(voucherDetails.getDiscount());
        voucher.setMinOrder(voucherDetails.getMinOrder());
        voucher.setMaxValue(voucherDetails.getMaxValue());
        voucher.setExpiryDate(voucherDetails.getExpiryDate());
        voucher.setActive(voucherDetails.getActive());
        return voucherRepository.save(voucher);
    }

    public void deleteVoucher(Integer id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));
        voucherRepository.delete(voucher);
    }
}