package com.ecommerce4.controller;

import com.ecommerce4.entity.Product;
import com.ecommerce4.entity.ProductImage;
import com.ecommerce4.entity.UserPrincipal;
import com.ecommerce4.service.ProductImageService;
import com.ecommerce4.service.ProductService;
import com.ecommerce4.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/shop/{shopId}")
public class ShopController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private ShopService shopService;

    @PreAuthorize("hasRole('SHOP')")
    @GetMapping("/product")
    public List<Product> getProductsByShopId(@PathVariable Integer shopId) {
        checkShopOwnership(shopId);
        return productService.getProductsByShopId(shopId);
    }

    @PreAuthorize("hasRole('SHOP')")
    @PostMapping("/product/addproduct")
    public ResponseEntity<Product> addProductForShop(
            @PathVariable Integer shopId, // Lấy shopId từ đường dẫn
            @RequestBody Product product) {
        checkShopOwnership(shopId);

        // Gọi service để tạo sản phẩm
        Product createdProduct = productService.createProductForShop(shopId, product);
        return ResponseEntity.ok(createdProduct);
    }

    @PreAuthorize("hasRole('SHOP')")
    @PutMapping("/product/{productId}")
    public ResponseEntity<Product> updateProductForShop(
            @PathVariable Integer shopId,
            @PathVariable Integer productId,
            @RequestBody Product productDetails) {
        checkShopOwnership(shopId);
        // Gọi service để cập nhật sản phẩm
        Product updatedProduct = productService.updateProductForShop(shopId, productId, productDetails);
        return ResponseEntity.ok(updatedProduct);
    }

    @PreAuthorize("hasRole('SHOP')")
    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Void> deleteProductForShop(
            @PathVariable Integer shopId,
            @PathVariable Integer productId) {
        checkShopOwnership(shopId);// Ensure the authenticated user is deleting a product from their own shop
        productService.deleteProductForShop(shopId, productId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('SHOP')")
    @GetMapping("/product/{productId}/productImg")
    public List<ProductImage> getImagesByProductId(@PathVariable Integer shopId, @PathVariable Integer productId) {
        checkShopOwnership(shopId);
        checkProductShop(productId, shopId);
        return productImageService.getImagesByProductId(productId);
    }

    @PreAuthorize("hasRole('SHOP')")
    @GetMapping("/product/{productId}/productImg/{imgId}")
    public Optional<ProductImage> getProductImageById(@PathVariable Integer shopId,
                                                      @PathVariable Integer productId,
                                                      @PathVariable Integer imgId) {
        checkShopOwnership(shopId);
        checkProductShop(productId, shopId);

        // Kiểm tra imgId có thuộc về productId
        ProductImage productImage = productImageService.getProductImageById(imgId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found for the given ID"));

        if (!productImage.getProduct().getProductId().equals(productId)) {
            throw new IllegalArgumentException("Image does not belong to the specified product");
        }

        return Optional.of(productImage);
    }


    @PreAuthorize("hasRole('SHOP')")
    @PostMapping("/product/{productId}/productImg")
    public ProductImage createProductImage(@PathVariable Integer shopId,@PathVariable Integer productId,@RequestBody ProductImage productImage) {
        checkShopOwnership(shopId);
        checkProductShop(productId, shopId);
        return productImageService.createProductImage(productId, productImage);
    }

    @PreAuthorize("hasRole('SHOP')")
    @PutMapping("/product/{productId}/productImg/{imgId}")
    public ResponseEntity<ProductImage> updateProductImage(@PathVariable Integer shopId,@PathVariable Integer productId,@PathVariable Integer imgId, @RequestBody ProductImage imageDetails) {
        checkShopOwnership(shopId);
        checkProductShop(productId, shopId);
        // Kiểm tra imgId có thuộc về productId
        ProductImage productImage = productImageService.getProductImageById(imgId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found for the given ID"));

        if (!productImage.getProduct().getProductId().equals(productId)) {
            throw new IllegalArgumentException("Image does not belong to the specified product");
        }
        ProductImage updatedImage = productImageService.updateProductImage(imgId, imageDetails);
        return ResponseEntity.ok(updatedImage);
    }

    @PreAuthorize("hasRole('SHOP')")
    @DeleteMapping("/product/{productId}/productImg/{imgId}")
    public ResponseEntity<Void> deleteProductImage(@PathVariable Integer shopId,@PathVariable Integer productId,@PathVariable Integer imgId) {
        checkShopOwnership(shopId);
        checkProductShop(productId, shopId);
        // Kiểm tra imgId có thuộc về productId
        ProductImage productImage = productImageService.getProductImageById(imgId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found for the given ID"));

        if (!productImage.getProduct().getProductId().equals(productId)) {
            throw new IllegalArgumentException("Image does not belong to the specified product");
        }
        productImageService.deleteProductImage(imgId);
        return ResponseEntity.noContent().build();
    }

    // Helper để kiểm tra quyền sở hữu
    private void checkShopOwnership(Integer shopId) {
        // Lấy userId của người dùng đang đăng nhập
        Integer loggedInUserId = ((UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getUserId();

        Integer shopUserId = shopService.getUserIdByShopId(shopId);

        if (!loggedInUserId.equals(shopUserId)) {
            throw new RuntimeException("Unauthorized: You do not own this resource.");
        }
    }

    private void checkProductShop(Integer productId,Integer shopId) {


        Integer shopProductId = productService.getShopIdByProductId(productId);

        if (!shopProductId.equals(shopId)) {
            throw new RuntimeException("Unauthorized: You do not own this resource.");
        }
    }




}
