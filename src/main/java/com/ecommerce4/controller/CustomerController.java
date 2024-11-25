package com.ecommerce4.controller;

import com.ecommerce4.entity.UserPrincipal;
import com.ecommerce4.dto.CartItemRequest;
import com.ecommerce4.dto.CartResponse;
import com.ecommerce4.dto.OrderRequest;
import com.ecommerce4.entity.Cart;
import com.ecommerce4.entity.CartItem;
import com.ecommerce4.entity.Customer;
import com.ecommerce4.entity.Order;
import com.ecommerce4.service.CartService;
import com.ecommerce4.service.CustomerService;
import com.ecommerce4.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer/{customerId}")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/info")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Integer customerId) {
        checkCustomerOwnership(customerId);
        return ResponseEntity.ok(customerService.getCustomerById(customerId));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("updateInfo")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Integer customerId, @RequestBody Customer customerDetails) {
        checkCustomerOwnership(customerId);
        return ResponseEntity.ok(customerService.updateCustomer(customerId, customerDetails));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/addToCart")
    public ResponseEntity<Cart> addToCart(
            @PathVariable Integer customerId, // Lấy customerId từ URL
            @RequestBody CartItemRequest request) { // Lấy productId và quantity từ body
        checkCustomerOwnership(customerId);

        // Gọi service để thêm sản phẩm vào giỏ hàng
        Cart updatedCart = cartService.addToCart(customerId, request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(updatedCart);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/showcart")
    public ResponseEntity<CartResponse> getCartByCustomerId(@PathVariable Integer customerId) {
        checkCustomerOwnership(customerId);

        // Lấy thông tin giỏ hàng
        Cart cart = cartService.getCartByCustomerId(customerId);

        // Lấy danh sách các mục trong giỏ hàng
        List<CartItem> cartItems = cartService.getCartItemsByCartId(cart.getCartId());

        // Tạo phản hồi (response)
        CartResponse cartResponse = new CartResponse();
        cartResponse.setCartId(cart.getCartId());
        cartResponse.setCustomerId(cart.getCustomer().getCustomerId());
        cartResponse.setCartItems(cartItems);

        return ResponseEntity.ok(cartResponse);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/cart/item/{cartItemId}")
    public ResponseEntity<Cart> updateCartItem(
            @PathVariable Integer customerId,
            @PathVariable Integer cartItemId,
            @RequestBody CartItemRequest request) { // Nhận `quantity` từ body
        checkCustomerOwnership(customerId);

        // Gọi service để cập nhật `CartItem`
        Cart updatedCart = cartService.updateCartItem(customerId, cartItemId, request.getQuantity());
        return ResponseEntity.ok(updatedCart);
    }


    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/cart/item/{cartItemId}")
    public ResponseEntity<Cart> deleteCartItem(
            @PathVariable Integer customerId,
            @PathVariable Integer cartItemId) {
        // Gọi service để xóa `CartItem`
        checkCustomerOwnership(customerId);

        Cart updatedCart = cartService.deleteCartItem(customerId, cartItemId);
        return ResponseEntity.ok(updatedCart);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/order")
    public ResponseEntity<Order> createOrder(
            @PathVariable Integer customerId,
            @RequestBody OrderRequest request) { // Dùng @RequestBody để nhận JSON
        checkCustomerOwnership(customerId);

        // Gọi service để tạo đơn hàng từ giỏ hàng
        Order order = orderService.createOrderFromCart(
                customerId,
                request.getShipAddress(),
                request.getShipDate(),
                request.getVoucherId()
        );
        return ResponseEntity.ok(order);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/showOrder")
    public ResponseEntity<List<Order>> getOrdersByCustomerId(@PathVariable Integer customerId) {
        checkCustomerOwnership(customerId);

        List<Order> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    // Helper để kiểm tra quyền sở hữu
    private void checkCustomerOwnership(Integer customerId) {
        // Lấy userId của người dùng đang đăng nhập
        Integer loggedInUserId = ((UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getUserId();

        // Lấy userId của customer dựa trên customerId
        Integer customerUserId = customerService.getUserIdByCustomerId(customerId);

        // So sánh userId
        if (!loggedInUserId.equals(customerUserId)) {
            throw new RuntimeException("Unauthorized: You do not own this resource.");
        }
    }
}
