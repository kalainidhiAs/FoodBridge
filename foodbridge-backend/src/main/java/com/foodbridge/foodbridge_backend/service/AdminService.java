package com.foodbridge.foodbridge_backend.service;

import com.foodbridge.foodbridge_backend.model.*;
import com.foodbridge.foodbridge_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminService {

    @Autowired private UserRepository userRepository;
    @Autowired private EarningRepository earningRepository;
    @Autowired private NotificationService notificationService;

    public List<User> getPendingUsers() {
        return userRepository.findByStatus("PENDING");
    }

    public void approveUser(Long userId, String remarks) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus("APPROVED");
        userRepository.save(user);
        notificationService.send(user,
                "Your account has been approved! You can now access all features.",
                "VERIFICATION");
    }

    public void rejectUser(Long userId, String remarks) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus("REJECTED");
        userRepository.save(user);
        notificationService.send(user,
                "Your account verification was rejected. Reason: " + remarks,
                "VERIFICATION");
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Earning> getAllEarnings() {
        return earningRepository.findAllByOrderByYearDescMonthDesc();
    }

    public void updateFeeStatus(Long earningId, String status) {
        Earning e = earningRepository.findById(earningId)
                .orElseThrow(() -> new RuntimeException("Earning not found"));
        e.setFeeStatus(status);
        if ("PAID".equals(status)) {
            e.setPaidAt(java.time.LocalDateTime.now());
            notificationService.send(e.getHomemaker(),
                    "Your platform contribution for " + e.getMonth() + "/" + e.getYear() + " has been marked as Paid.",
                    "PAYMENT");
        }
        earningRepository.save(e);
    }
}
