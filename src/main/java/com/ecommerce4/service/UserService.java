package com.ecommerce4.service;


import com.ecommerce4.entity.Role;
import com.ecommerce4.entity.User;
import com.ecommerce4.exception.ResourceNotFoundException;
import com.ecommerce4.repository.RoleRepository;
import com.ecommerce4.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User createUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public User updateUser(Integer id, User userDetails) {
        // Tìm user theo ID
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        // Cập nhật thông tin của User
        user.setUserName(userDetails.getUserName());
        user.setPassword(userDetails.getPassword());  // Bạn có thể mã hóa mật khẩu ở đây nếu cần
        user.setEmail(userDetails.getEmail());
        user.setAvatar(userDetails.getAvatar());

        // Cập nhật Role của User
        if (userDetails.getRole() != null) {
            Integer roleId = userDetails.getRole().getRoleId();
            // Kiểm tra xem Role có tồn tại không
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with id " + roleId));
            user.setRole(role);  // Gán Role cho User
        }

        // Lưu User đã cập nhật
        return userRepository.save(user);
    }


    public void deleteUser(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
    }
}

