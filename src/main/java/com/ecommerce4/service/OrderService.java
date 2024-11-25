package com.ecommerce4.service;

import com.ecommerce4.entity.*;
import com.ecommerce4.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    public Order createOrderFromCart(Integer customerId, String shipAddress, Date shipDate, Integer voucherId) {
        // Lấy giỏ hàng của khách hàng
        Cart cart = cartRepository.findByCustomer_CustomerId(customerId);
        if (cart == null) {
            throw new RuntimeException("Cart not found for customer with id " + customerId);
        }

        // Kiểm tra nếu giỏ hàng trống
        List<CartItem> cartItems = cartItemRepository.findByCart_CartId(cart.getCartId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty, cannot create an order");
        }

        // Tạo đơn hàng
        Order order = new Order();
        order.setOrderDate(new Date());
        order.setShipAddress(shipAddress);
        order.setShipDate(shipDate);
        order.setCustomer(cart.getCustomer());

        // Nếu có voucher, gán vào đơn hàng
        if (voucherId != null) {
            Voucher voucher = new Voucher();
            voucher.setVoucherId(voucherId);
            order.setVoucher(voucher);
        }

        // Tính tổng tiền đơn hàng từ giỏ hàng
        Double total = cartItems.stream()
                .mapToDouble(cartItem -> cartItem.getQuantity() * cartItem.getProduct().getPrice())
                .sum();
        order.setTotal(total);

        // Lưu đơn hàng
        order = orderRepository.save(order);

        // Tạo chi tiết đơn hàng từ các mục trong giỏ hàng
        for (CartItem cartItem : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(cartItem.getPrice());

            // Lưu chi tiết đơn hàng
            orderDetailRepository.save(orderDetail);
        }

        // Xóa các mục trong giỏ hàng sau khi tạo đơn hàng
        cartItemRepository.deleteAll(cartItems);

        return order;
    }

    public List<Order> getOrdersByCustomerId(Integer customerId) {
        return orderRepository.findByCustomer_CustomerId(customerId);
    }
}

