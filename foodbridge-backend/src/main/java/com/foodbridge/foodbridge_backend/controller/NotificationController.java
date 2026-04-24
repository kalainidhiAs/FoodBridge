package com.foodbridge.foodbridge_backend.controller;

import com.foodbridge.foodbridge_backend.model.*;
import com.foodbridge.foodbridge_backend.repository.UserRepository;
import com.foodbridge.foodbridge_backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired private NotificationService notificationService;
    @Autowired private UserRepository userRepository;

    private User currentUser(Authentication auth) {
        return userRepository.findByMobileNumber(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAll(Authentication auth) {
        return ResponseEntity.ok(notificationService.getAll(currentUser(auth)));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount(Authentication auth) {
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(currentUser(auth))));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markRead(@PathVariable Long id) {
        notificationService.markRead(id);
        return ResponseEntity.ok(Map.of("message", "Marked as read."));
    }

    @PutMapping("/read-all")
    public ResponseEntity<?> markAllRead(Authentication auth) {
        notificationService.markAllRead(currentUser(auth));
        return ResponseEntity.ok(Map.of("message", "All marked as read."));
    }
}
