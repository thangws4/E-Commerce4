package com.ecommerce4.service;

import com.ecommerce4.entity.User;
import com.ecommerce4.entity.UserPrincipal;
import com.ecommerce4.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempting to load user by username: {}", username);

        // Kiểm tra nếu username null hoặc rỗng
        if (username == null || username.trim().isEmpty()) {
            logger.error("Username is null or empty");
            throw new UsernameNotFoundException("Username cannot be null or empty");
        }

        // Tìm kiếm trong repository
        Optional<User> userOptional = userRepository.findByUserName(username);

        // Kiểm tra nếu người dùng không tồn tại
        if (userOptional.isEmpty()) {
            logger.warn("User not found with username: {}", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Lấy thông tin người dùng từ Optional
        User user = userOptional.get();
        logger.info("User found: {}", user.getUserName());

        return new UserPrincipal(user);
    }
}
