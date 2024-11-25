package com.ecommerce4.service;

import com.ecommerce4.entity.Cart;
import com.ecommerce4.entity.CartItem;
import com.ecommerce4.entity.Customer;
import com.ecommerce4.entity.Product;
import com.ecommerce4.repository.CartItemRepository;
import com.ecommerce4.repository.CartRepository;
import com.ecommerce4.repository.CustomerRepository;
import com.ecommerce4.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public Cart addToCart(Integer customerId, Integer productId, Integer quantity) {
        // Tìm giỏ hàng (Cart) của khách hàng theo customerId, hoặc tạo mới nếu chưa có
        Cart cart = cartRepository.findByCustomer_CustomerId(customerId);
        if (cart == null) {
            // Nếu giỏ hàng chưa tồn tại, tạo mới và gán cho khách hàng
            cart = new Cart();
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found with id " + customerId));
            cart.setCustomer(customer);
            cartRepository.save(cart);
        }

        // Tìm sản phẩm theo productId
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + productId));

        // Kiểm tra sản phẩm đã có trong giỏ hàng hay chưa
        CartItem cartItem = cartItemRepository.findByCart_CartIdAndProduct_ProductId(cart.getCartId(), productId);

        if (cartItem != null) {
            // Nếu sản phẩm đã có trong giỏ, cập nhật số lượng và giá
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setPrice(cartItem.getQuantity() * product.getPrice());
        } else {
            // Nếu sản phẩm chưa có trong giỏ, thêm mới một CartItem
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setPrice(quantity * product.getPrice());
        }

        // Lưu lại CartItem vào database
        cartItemRepository.save(cartItem);

        // Trả về giỏ hàng đã được cập nhật
        return cart;
    }


    // Dummy method for customer retrieval (replace with actual logic)
    private Customer findCustomerById(Integer customerId) {
        // Add the logic to fetch the customer by ID
        return new Customer(); // Placeholder
    }

    public Cart getCartByCustomerId(Integer customerId) {
        // Tìm giỏ hàng theo customerId
        Cart cart = cartRepository.findByCustomer_CustomerId(customerId);
        if (cart == null) {
            throw new RuntimeException("Cart not found for customer with id " + customerId);
        }
        return cart;
    }


    public List<CartItem> getCartItemsByCartId(Integer cartId) {
        // Tìm tất cả các mục trong giỏ hàng theo cartId
        return cartItemRepository.findByCart_CartId(cartId);
    }

    public Cart updateCartItem(Integer customerId, Integer cartItemId, Integer newQuantity) {
        // Tìm giỏ hàng theo `customerId`
        Cart cart = cartRepository.findByCustomer_CustomerId(customerId);
        if (cart == null) {
            throw new RuntimeException("Cart not found for customer with id " + customerId);
        }

        // Tìm `CartItem` theo `cartItemId` và xác minh nó thuộc giỏ hàng của khách hàng
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found with id " + cartItemId));

        if (!cartItem.getCart().getCartId().equals(cart.getCartId())) {
            throw new RuntimeException("CartItem does not belong to the customer's cart");
        }

        // Cập nhật số lượng và giá
        cartItem.setQuantity(newQuantity);
        cartItem.setPrice(newQuantity * cartItem.getProduct().getPrice());

        // Lưu `CartItem` đã cập nhật
        cartItemRepository.save(cartItem);

        // Trả về giỏ hàng sau khi cập nhật
        return cart;
    }


    public Cart deleteCartItem(Integer customerId, Integer cartItemId) {
        // Tìm giỏ hàng theo `customerId`
        Cart cart = cartRepository.findByCustomer_CustomerId(customerId);
        if (cart == null) {
            throw new RuntimeException("Cart not found for customer with id " + customerId);
        }

        // Tìm `CartItem` theo `cartItemId` và xác minh nó thuộc giỏ hàng của khách hàng
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found with id " + cartItemId));

        if (!cartItem.getCart().getCartId().equals(cart.getCartId())) {
            throw new RuntimeException("CartItem does not belong to the customer's cart");
        }

        // Xóa `CartItem`
        cartItemRepository.delete(cartItem);

        // Trả về giỏ hàng sau khi xóa
        return cart;
    }

    public Integer getUserIdByCustomerId(Integer customerId) {
        // Tìm kiếm Customer bằng customerId
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found for ID: " + customerId));

        // Lấy userId từ Customer
        return customer.getUser().getUserId();
    }

}