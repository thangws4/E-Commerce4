package com.ecommerce4.controller;

import com.ecommerce4.entity.Category;
import com.ecommerce4.entity.Customer;
import com.ecommerce4.entity.Shop;
import com.ecommerce4.entity.Voucher;
import com.ecommerce4.service.CategoryService;
import com.ecommerce4.service.CustomerService;
import com.ecommerce4.service.ShopService;
import com.ecommerce4.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/manager/{managerId}")
public class ManagerController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ShopService shopService;

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/category")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Integer categoryId) {
        Optional<Category> category = categoryService.getCategoryById(categoryId);
        return category.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/category")
    public Category createCategory(@RequestBody Category category) {
        return categoryService.createCategory(category);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/category/{categoryId}")
    public ResponseEntity<Category> updateCategory(@PathVariable Integer categoryId, @RequestBody Category categoryDetails) {
        Category updatedCategory = categoryService.updateCategory(categoryId, categoryDetails);
        return ResponseEntity.ok(updatedCategory);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/category/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/voucher")
    public List<Voucher> getAllVouchers() {
        return voucherService.getAllVouchers();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/voucher/{voucherId}")
    public ResponseEntity<Voucher> getVoucherById(@PathVariable Integer voucherId) {
        Optional<Voucher> voucher = voucherService.getVoucherById(voucherId);
        return voucher.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/voucher")
    public Voucher createVoucher(@RequestBody Voucher voucher) {
        return voucherService.createVoucher(voucher);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/voucher/{voucherId}")
    public ResponseEntity<Voucher> updateVoucher(@PathVariable Integer voucherId, @RequestBody Voucher voucherDetails) {
        Voucher updatedVoucher = voucherService.updateVoucher(voucherId, voucherDetails);
        return ResponseEntity.ok(updatedVoucher);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/voucher/{voucherId}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable Integer voucherId) {
        voucherService.deleteVoucher(voucherId);
        return ResponseEntity.noContent().build();
    }

    // Lấy tất cả các khách hàng
    @GetMapping("/customer")
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    // Lấy một khách hàng theo ID
    @GetMapping("/cutomer/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Integer customerId) {
        return ResponseEntity.ok(customerService.getCustomerById(customerId));
    }


    // Lấy tất cả shops
    @GetMapping(("/shop"))
    public List<Shop> getAllShops() {
        return shopService.getAllShops();
    }

    // Lấy shop theo ID
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<Shop> getShopById(@PathVariable Integer shopId) {
        return ResponseEntity.ok(shopService.getShopById(shopId));
    }


}
