package com.foodbridge.foodbridge_backend.controller;

import com.foodbridge.foodbridge_backend.model.*;
import com.foodbridge.foodbridge_backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private AdminService adminService;

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getPendingUsers() {
        return ResponseEntity.ok(adminService.getPendingUsers());
    }

    @PostMapping("/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveUser(@PathVariable Long id,
                                          @RequestBody(required = false) Map<String, String> body) {
        String remarks = body != null ? body.getOrDefault("remarks", "") : "";
        adminService.approveUser(id, remarks);
        return ResponseEntity.ok(Map.of("message", "User approved successfully."));
    }

    @PostMapping("/reject/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectUser(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String remarks = body.getOrDefault("remarks", "");
        if (remarks.length() < 10) {
            return ResponseEntity.badRequest().body(Map.of("error", "Rejection remarks must be at least 10 characters."));
        }
        adminService.rejectUser(id, remarks);
        return ResponseEntity.ok(Map.of("message", "User rejected."));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/earnings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Earning>> getAllEarnings() {
        return ResponseEntity.ok(adminService.getAllEarnings());
    }

    @PutMapping("/earnings/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateFeeStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        adminService.updateFeeStatus(id, body.get("status"));
        return ResponseEntity.ok(Map.of("message", "Fee status updated."));
    }
}
