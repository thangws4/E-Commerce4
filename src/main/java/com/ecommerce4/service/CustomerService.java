package com.ecommerce4.service;


import com.ecommerce4.entity.Customer;
import com.ecommerce4.entity.Role;
import com.ecommerce4.entity.User;
import com.ecommerce4.exception.ResourceNotFoundException;
import com.ecommerce4.repository.CustomerRepository;
import com.ecommerce4.repository.RoleRepository;
import com.ecommerce4.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;



    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


    // Lấy tất cả khách hàng
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // Lấy một khách hàng theo ID
    public Customer getCustomerById(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + id));
    }

    // Tạo mới một khách hàng (và User nếu chưa tồn tại)
    public Customer createCustomer(Customer customer) {
        User user = customer.getUser();

        // Nếu User chưa có trong database, thì tạo mới User
        if (user.getUserId() == null) {
            // Gán Role mặc định là "Role_Customer"
            Role role = roleRepository.findByRoleName("Role_Customer")
                    .orElseThrow(() -> new ResourceNotFoundException("Role 'Role_Customer' not found"));
            user.setRole(role);
            user.setPassword(encoder.encode(user.getPassword()));


            // Lưu User mới
            user = userRepository.save(user);
        } else {
            // Nếu User đã tồn tại, kiểm tra xem nó có trong database không
            Integer userId = user.getUserId();

            user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        }

        // Gán User cho Customer và lưu vào database
        customer.setUser(user);
        return customerRepository.save(customer);
    }


    // Cập nhật khách hàng và user theo ID
    public Customer updateCustomer(Integer id, Customer customerDetails) {
        // Tìm customer hiện tại trong database
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + id));

        // Cập nhật thông tin của Customer
        customer.setCustomerName(customerDetails.getCustomerName());
        customer.setAddress(customerDetails.getAddress());
        customer.setPhoneNumber(customerDetails.getPhoneNumber());

        // Cập nhật thông tin của User nếu có
        User user = customer.getUser();
        if (customerDetails.getUser() != null) {
            User userDetails = customerDetails.getUser();

            // Nếu User tồn tại thì cập nhật các thông tin cần thiết
            if (user != null) {
                user.setUserName(userDetails.getUserName());
                String encodedPassword = encoder.encode(userDetails.getPassword());

                user.setEmail(userDetails.getEmail());
                user.setPassword(encodedPassword);

                user.setAvatar(userDetails.getAvatar());

                // Lưu User sau khi cập nhật
                userRepository.save(user);
            }
        }

        // Lưu lại thông tin customer sau khi cập nhật
        return customerRepository.save(customer);
    }


    // Xóa khách hàng theo ID
    public void deleteCustomer(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + id));

        // Lấy thông tin User liên quan đến Shop
        User user = customer.getUser();

        // Xóa shop
        customerRepository.delete(customer);

        // Xóa luôn User nếu User tồn tại
        userRepository.delete(user);

    }



        public Integer getUserIdByCustomerId(Integer customerId) {
            // Truy xuất Customer từ database
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found for ID: " + customerId));

            // Trả về userId liên kết với Customer
            return customer.getUser().getUserId();
        }


}
