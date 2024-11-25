package com.ecommerce4.service;


import com.ecommerce4.entity.Role;
import com.ecommerce4.exception.ResourceNotFoundException;
import com.ecommerce4.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    // Lấy tất cả vai trò
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // Lấy một vai trò theo ID
    public Role getRoleById(Integer id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id " + id));
    }

    // Tạo mới một vai trò
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    // Cập nhật vai trò theo ID
    public Role updateRole(Integer id, Role roleDetails) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id " + id));

        role.setRoleName(roleDetails.getRoleName());

        return roleRepository.save(role);
    }

    // Xóa vai trò theo ID
    public void deleteRole(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id " + id));

        roleRepository.delete(role);
    }
}

