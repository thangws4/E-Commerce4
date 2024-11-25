package com.ecommerce4.service;


import com.ecommerce4.entity.Customer;
import com.ecommerce4.entity.Role;
import com.ecommerce4.entity.Shop;
import com.ecommerce4.entity.User;
import com.ecommerce4.exception.ResourceNotFoundException;
import com.ecommerce4.repository.RoleRepository;
import com.ecommerce4.repository.ShopRepository;
import com.ecommerce4.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopService {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


    // Lấy tất cả shops
    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    // Lấy shop theo ID
    public Shop getShopById(Integer id) {
        return shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with id " + id));
    }

    public Shop createShop(Shop shop) {
        User user = shop.getUser();

        // Kiểm tra nếu User đã tồn tại hoặc chưa có thì tạo mới
        if (user.getUserId() == null) {
            Role role = roleRepository.findByRoleName("Role_Shop")
                    .orElseThrow(() -> new ResourceNotFoundException("Role 'Role_Shop' not found"));
            user.setRole(role);
            user.setPassword(encoder.encode(user.getPassword()));


            user = userRepository.save(user);  // Tạo mới User nếu chưa có ID
        } else {
            Integer userId = user.getUserId();
            user = userRepository.findById(user.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        }

        shop.setUser(user);  // Gán user đã tạo/cập nhật cho Shop
        return shopRepository.save(shop);
    }


    public Shop updateShop(Integer id, Shop shopDetails) {
        // Tìm shop hiện tại trong database
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with id " + id));

        // Cập nhật thông tin của Shop
        shop.setShopName(shopDetails.getShopName());
        shop.setAddress(shopDetails.getAddress());
        shop.setPhone(shopDetails.getPhone());

        // Kiểm tra thông tin của User và cập nhật nếu cần
        User userDetails = shopDetails.getUser();
        if (userDetails != null) {
            if (userDetails.getUserId() == null) {
                // Tạo mới User nếu chưa có ID
                User newUser = userRepository.save(userDetails);
                shop.setUser(newUser);
            } else {
                // Cập nhật User nếu đã tồn tại
                User existingUser = userRepository.findById(userDetails.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userDetails.getUserId()));

                existingUser.setUserName(userDetails.getUserName());
                existingUser.setEmail(userDetails.getEmail());
                existingUser.setPassword(encoder.encode(userDetails.getPassword()));  // Mã hóa mật khẩu nếu cần

                // Lưu User đã cập nhật
                userRepository.save(existingUser);
                shop.setUser(existingUser);
            }
        }

        // Lưu Shop đã cập nhật
        return shopRepository.save(shop);
    }


    public void deleteShop(Integer id) {
        // Tìm shop theo ID
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with id " + id));

        // Lấy thông tin User liên quan đến Shop
        User user = shop.getUser();

        // Xóa shop
        shopRepository.delete(shop);

        // Xóa luôn User nếu User tồn tại
            userRepository.delete(user);

    }

    public Integer getUserIdByShopId(Integer shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("Shop not found for ID: " + shopId));

        return shop.getUser().getUserId();
    }



}
