package com.foodbridge.foodbridge_backend.controller;

import com.foodbridge.foodbridge_backend.model.*;
import com.foodbridge.foodbridge_backend.repository.UserRepository;
import com.foodbridge.foodbridge_backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired private ReviewService reviewService;
    @Autowired private UserRepository userRepository;

    private User currentUser(Authentication auth) {
        return userRepository.findByMobileNumber(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public ResponseEntity<?> submitReview(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            Long orderId = Long.valueOf(body.get("orderId").toString());
            int rating = Integer.parseInt(body.get("rating").toString());
            String comment = (String) body.get("comment");
            return ResponseEntity.ok(reviewService.submitReview(currentUser(auth), orderId, rating, comment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/homemaker/{id}")
    public ResponseEntity<List<Review>> getHomemakerReviews(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewsForHomemaker(id));
    }
}
