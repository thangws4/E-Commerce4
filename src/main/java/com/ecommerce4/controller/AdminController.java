package com.ecommerce4.controller;


import com.ecommerce4.entity.Manager;
import com.ecommerce4.entity.Role;
import com.ecommerce4.service.ManagerService;
import com.ecommerce4.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ManagerService managerService;

    @Autowired
    private RoleService roleService;

    // Lấy tất cả managers
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/manager")
    public List<Manager> getAllManagers() {
        return managerService.getAllManagers();
    }

    // Lấy manager theo ID
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/manager/{id}")
    public ResponseEntity<Manager> getManagerById(@PathVariable Integer id) {
        return ResponseEntity.ok(managerService.getManagerById(id));
    }

    // Tạo mới manager
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/manager/add")
    public ResponseEntity<Manager> createManager(@RequestBody Manager manager) {
        return ResponseEntity.ok(managerService.createManager(manager));
    }

    // Cập nhật manager theo ID
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/manager/{id}")
    public ResponseEntity<Manager> updateManager(@PathVariable Integer id, @RequestBody Manager managerDetails) {
        return ResponseEntity.ok(managerService.updateManager(id, managerDetails));
    }

    // Xóa manager theo ID
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/manager/{id}")
    public ResponseEntity<Void> deleteManager(@PathVariable Integer id) {
        managerService.deleteManager(id);
        return ResponseEntity.noContent().build();
    }

    // Lấy tất cả các vai trò
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/role")
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    // Lấy một vai trò theo ID

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/role/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Integer id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    // Tạo mới một vai trò
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/role/add")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        return ResponseEntity.ok(roleService.createRole(role));
    }

    // Cập nhật một vai trò theo ID
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/role/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Integer id, @RequestBody Role roleDetails) {
        return ResponseEntity.ok(roleService.updateRole(id, roleDetails));
    }

    // Xóa một vai trò theo ID
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/role/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Integer id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }


}
