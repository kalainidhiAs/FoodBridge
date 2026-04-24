package com.foodbridge.foodbridge_backend.controller;

import com.foodbridge.foodbridge_backend.model.*;
import com.foodbridge.foodbridge_backend.repository.UserRepository;
import com.foodbridge.foodbridge_backend.service.DonationService;
import com.foodbridge.foodbridge_backend.service.FoodListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/donations")
public class DonationController {

    @Autowired private DonationService donationService;
    @Autowired private FoodListingService foodListingService;
    @Autowired private UserRepository userRepository;

    private User currentUser(Authentication auth) {
        return userRepository.findByMobileNumber(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public ResponseEntity<List<FoodListing>> getFreeFeed() {
        return ResponseEntity.ok(foodListingService.getFreeFeed());
    }

    @PostMapping("/collect/{listingId}")
    public ResponseEntity<?> requestCollection(@PathVariable Long listingId, Authentication auth) {
        try {
            return ResponseEntity.ok(donationService.requestCollection(currentUser(auth), listingId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<?> confirmCollection(@PathVariable Long id, Authentication auth) {
        try {
            return ResponseEntity.ok(donationService.confirmCollection(currentUser(auth), id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String status, Authentication auth) {
        try {
            return ResponseEntity.ok(donationService.updateDonationStatus(currentUser(auth), id, status));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/homemaker")
    public ResponseEntity<List<Donation>> homemakerDonations(Authentication auth) {
        return ResponseEntity.ok(donationService.getHomemakerDonations(currentUser(auth)));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Donation>> myDonations(Authentication auth) {
        return ResponseEntity.ok(donationService.getNgoDonations(currentUser(auth)));
    }
}
