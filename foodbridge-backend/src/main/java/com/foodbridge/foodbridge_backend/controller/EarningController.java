package com.foodbridge.foodbridge_backend.controller;

import com.foodbridge.foodbridge_backend.repository.UserRepository;
import com.foodbridge.foodbridge_backend.model.User;
import com.foodbridge.foodbridge_backend.service.EarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/earnings")
public class EarningController {

    @Autowired private EarningService earningService;
    @Autowired private UserRepository userRepository;

    private User currentUser(Authentication auth) {
        return userRepository.findByMobileNumber(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/summary")
    public ResponseEntity<List<Map<String, Object>>> getLast6Months(Authentication auth) {
        return ResponseEntity.ok(earningService.getLast6Months(currentUser(auth)));
    }

    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentMonth(Authentication auth) {
        LocalDateTime now = LocalDateTime.now();
        return ResponseEntity.ok(earningService.getMonthlySummary(currentUser(auth), now.getMonthValue(), now.getYear()));
    }

    @GetMapping("/comprehensive")
    public ResponseEntity<Map<String, Object>> getComprehensive(Authentication auth) {
        return ResponseEntity.ok(earningService.getComprehensiveSummary(currentUser(auth)));
    }
}
