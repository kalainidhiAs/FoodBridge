package com.foodbridge.foodbridge_backend.service;

import com.foodbridge.foodbridge_backend.model.*;
import com.foodbridge.foodbridge_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FoodListingService {

    @Autowired private FoodListingRepository foodListingRepository;

    public FoodListing createListing(FoodListing listing, User homemaker) {
        if (!"APPROVED".equalsIgnoreCase(homemaker.getStatus())) {
            throw new RuntimeException("Your account must be approved by admin before creating listings.");
        }
        listing.setHomemaker(homemaker);
        if (listing.getIsActive() == null) listing.setIsActive(true);
        if (listing.getIsDeleted() == null) listing.setIsDeleted(false);
        if (listing.getIsFree() == null) listing.setIsFree(false);
        if (listing.getIsVeg() == null) listing.setIsVeg(true);
        listing.setCreatedAt(LocalDateTime.now());
        return foodListingRepository.save(listing);
    }

    public FoodListing updateListing(Long id, FoodListing updates, User homemaker) {
        FoodListing listing = foodListingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        if (!listing.getHomemaker().getId().equals(homemaker.getId())) {
            throw new RuntimeException("Not authorized to edit this listing.");
        }
        if (updates.getFoodName() != null) listing.setFoodName(updates.getFoodName());
        if (updates.getDescription() != null) listing.setDescription(updates.getDescription());
        if (updates.getPrice() != null) listing.setPrice(updates.getPrice());
        if (updates.getQuantity() != null) listing.setQuantity(updates.getQuantity());
        if (updates.getStartTime() != null) listing.setStartTime(updates.getStartTime());
        if (updates.getEndTime() != null) listing.setEndTime(updates.getEndTime());
        if (updates.getCuisine() != null) listing.setCuisine(updates.getCuisine());
        if (updates.getIsVeg() != null) listing.setIsVeg(updates.getIsVeg());
        if (updates.getIsFree() != null) listing.setIsFree(updates.getIsFree());
        return foodListingRepository.save(listing);
    }

    public FoodListing toggleAvailability(Long id, User homemaker) {
        FoodListing listing = foodListingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        if (!listing.getHomemaker().getId().equals(homemaker.getId())) {
            throw new RuntimeException("Not authorized.");
        }
        listing.setIsActive(!listing.getIsActive());
        return foodListingRepository.save(listing);
    }

    public void softDelete(Long id, User homemaker) {
        FoodListing listing = foodListingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        if (!listing.getHomemaker().getId().equals(homemaker.getId())) {
            throw new RuntimeException("Not authorized.");
        }
        listing.setIsDeleted(true);
        listing.setDeletedAt(LocalDateTime.now());
        foodListingRepository.save(listing);
    }

    public List<FoodListing> getMyListings(User homemaker) {
        return foodListingRepository.findByHomemakerAndIsDeletedFalse(homemaker);
    }

    public List<FoodListing> getPaidFeed() {
        return foodListingRepository.findByIsActiveAndIsFreeFalseAndIsDeletedFalse(true);
    }

    public List<FoodListing> getFreeFeed() {
        return foodListingRepository.findByIsActiveAndIsFreeTrueAndIsDeletedFalse(true);
    }

    public List<FoodListing> search(String query) {
        return foodListingRepository.searchListings(query);
    }

    public List<FoodListing> getByLocation(String location, Boolean isFree) {
        return foodListingRepository.findByLocationAndType(location, isFree);
    }
}
