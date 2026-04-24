package com.foodbridge.foodbridge_backend.controller;

import com.foodbridge.foodbridge_backend.model.*;
import com.foodbridge.foodbridge_backend.repository.UserRepository;
import com.foodbridge.foodbridge_backend.service.FoodListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/listings")
public class FoodListingController {

    @Autowired private FoodListingService foodListingService;
    @Autowired private UserRepository userRepository;

    private User currentUser(Authentication auth) {
        return userRepository.findByMobileNumber(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody FoodListing listing, Authentication auth) {
        try {
            return ResponseEntity.ok(foodListingService.createListing(listing, currentUser(auth)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<FoodListing>> myListings(Authentication auth) {
        return ResponseEntity.ok(foodListingService.getMyListings(currentUser(auth)));
    }

    @GetMapping("/feed")
    public ResponseEntity<List<FoodListing>> paidFeed() {
        return ResponseEntity.ok(foodListingService.getPaidFeed());
    }

    @GetMapping("/donations")
    public ResponseEntity<List<FoodListing>> freeFeed() {
        return ResponseEntity.ok(foodListingService.getFreeFeed());
    }

    @GetMapping("/search")
    public ResponseEntity<List<FoodListing>> search(@RequestParam String q) {
        return ResponseEntity.ok(foodListingService.search(q));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody FoodListing updates, Authentication auth) {
        try {
            return ResponseEntity.ok(foodListingService.updateListing(id, updates, currentUser(auth)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<?> toggle(@PathVariable Long id, Authentication auth) {
        try {
            return ResponseEntity.ok(foodListingService.toggleAvailability(id, currentUser(auth)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication auth) {
        try {
            foodListingService.softDelete(id, currentUser(auth));
            return ResponseEntity.ok(Map.of("message", "Listing deleted."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
