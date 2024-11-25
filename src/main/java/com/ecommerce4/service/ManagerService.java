package com.ecommerce4.service;


import com.ecommerce4.entity.Manager;
import com.ecommerce4.entity.Role;
import com.ecommerce4.entity.User;
import com.ecommerce4.exception.ResourceNotFoundException;
import com.ecommerce4.repository.ManagerRepository;
import com.ecommerce4.repository.RoleRepository;
import com.ecommerce4.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManagerService {

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


    // Lấy tất cả managers
    public List<Manager> getAllManagers() {
        return managerRepository.findAll();
    }

    // Lấy manager theo ID
    public Manager getManagerById(Integer id) {
        return managerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id " + id));
    }

    public Manager createManager(Manager manager) {
        User user = manager.getUser();

        // Nếu user chưa có trong database, tạo mới user
        if (user.getUserId() == null) {
            Role role = roleRepository.findByRoleName("Role_Manager")
                    .orElseThrow(() -> new ResourceNotFoundException("Role 'Role_Manager' not found"));
            user.setRole(role);
            user.setPassword(encoder.encode(user.getPassword()));

            user = userRepository.save(user);  // Tạo mới user
        } else {
            // Nếu userId đã có, kiểm tra xem user có tồn tại không
            Integer userId = user.getUserId();
            user = userRepository.findById(user.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        }

        manager.setUser(user);  // Gán user đã tạo/cập nhật cho manager
        return managerRepository.save(manager);
    }


    public Manager updateManager(Integer id, Manager managerDetails) {
        // Tìm Manager theo id
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id " + id));

        // Cập nhật thông tin Manager
        manager.setManagerName(managerDetails.getManagerName());

        // Lấy thông tin User và cập nhật nếu cần
        User userDetails = managerDetails.getUser();

        if (userDetails != null) {
            if (userDetails.getUserId() == null) {
                // Tạo mới User nếu chưa có ID
                User newUser = userRepository.save(userDetails);
                manager.setUser(newUser);
            } else {
                // Nếu User đã tồn tại, cập nhật thông tin User
                User existingUser = userRepository.findById(userDetails.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userDetails.getUserId()));

                existingUser.setUserName(userDetails.getUserName());
                existingUser.setEmail(userDetails.getEmail());
                existingUser.setPassword(userDetails.getPassword());
                existingUser.setAvatar(userDetails.getAvatar());

                // Lưu User đã cập nhật
                userRepository.save(existingUser);
                manager.setUser(existingUser);
            }
        }

        // Lưu Manager đã cập nhật
        return managerRepository.save(manager);
    }


    // Xóa manager theo ID
    public void deleteManager(Integer id) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id " + id));

        // Lấy thông tin User liên quan đến Shop
        User user = manager.getUser();

        // Xóa shop
        managerRepository.delete(manager);

        // Xóa luôn User nếu User tồn tại
        userRepository.delete(user);

    }
}
