package com.ecommerce4.service;

import com.ecommerce4.entity.Product;
import com.ecommerce4.entity.Shop;
import com.ecommerce4.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id);
    }

    public List<Product> getProductsByCategoryId(Integer categoryId) {
        return productRepository.findByCategory_CategoryId(categoryId);
    }

    public List<Product> getProductsByShopId(Integer shopId) {
        return productRepository.findByShop_ShopId(shopId);
    }

    public Page<Product> getAllProductsPaginated(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product createProductForShop(Integer shopId, Product product) {
        // Tạo một Shop mới và gán shopId
        Shop shop = new Shop();
        shop.setShopId(shopId);

        // Gán Shop cho sản phẩm
        product.setShop(shop);

        // Lưu sản phẩm vào database
        return productRepository.save(product);
    }


    public Product updateProductForShop(Integer shopId, Integer productId, Product productDetails) {
        // Tìm sản phẩm theo ID
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Kiểm tra xem sản phẩm có thuộc shop tương ứng không
        if (!product.getShop().getShopId().equals(shopId)) {
            throw new RuntimeException("Product does not belong to the specified shop");
        }

        // Cập nhật thông tin sản phẩm (không thay đổi thông tin shop)
        product.setProductName(productDetails.getProductName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStockQuantity(productDetails.getStockQuantity());
        product.setCategory(productDetails.getCategory());

        // Lưu lại thay đổi
        return productRepository.save(product);
    }


    public void deleteProductForShop(Integer shopId, Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (!product.getShop().getShopId().equals(shopId)) {
            throw new RuntimeException("Product does not belong to the specified shop");
        }

        productRepository.delete(product);
    }

    public Integer getShopIdByProductId(Integer productId) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Shop not found for ID: " + productId));

        return product.getShop().getShopId();
    }
}
