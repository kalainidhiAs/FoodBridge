package com.foodbridge.foodbridge_backend.controller;

import com.foodbridge.foodbridge_backend.model.*;
import com.foodbridge.foodbridge_backend.repository.UserRepository;
import com.foodbridge.foodbridge_backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired private OrderService orderService;
    @Autowired private UserRepository userRepository;

    private User currentUser(Authentication auth) {
        return userRepository.findByMobileNumber(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
            String address = (String) body.get("deliveryAddress");
            return ResponseEntity.ok(orderService.placeOrder(currentUser(auth), items, address));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<Order>> myOrders(Authentication auth) {
        return ResponseEntity.ok(orderService.getCustomerOrders(currentUser(auth)));
    }

    @GetMapping("/homemaker")
    public ResponseEntity<List<Order>> homemakerOrders(Authentication auth) {
        return ResponseEntity.ok(orderService.getHomemakerOrders(currentUser(auth)));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<?> accept(@PathVariable Long id, Authentication auth) {
        try {
            return ResponseEntity.ok(orderService.acceptOrder(id, currentUser(auth)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication auth) {
        try {
            return ResponseEntity.ok(orderService.rejectOrder(id, currentUser(auth), body.getOrDefault("remark", "")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication auth) {
        try {
            return ResponseEntity.ok(orderService.updateStatus(id, currentUser(auth), body.get("status")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<?> payOrder(@PathVariable Long id, Authentication auth) {
        try {
            return ResponseEntity.ok(orderService.payOrder(id, currentUser(auth)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
