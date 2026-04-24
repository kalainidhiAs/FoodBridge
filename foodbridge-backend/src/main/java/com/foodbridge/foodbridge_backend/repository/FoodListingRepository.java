package com.foodbridge.foodbridge_backend.repository;

import com.foodbridge.foodbridge_backend.model.FoodListing;
import com.foodbridge.foodbridge_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FoodListingRepository extends JpaRepository<FoodListing, Long> {
    List<FoodListing> findByHomemakerAndIsDeletedFalse(User homemaker);

    // Public paid feed for customers
    List<FoodListing> findByIsActiveAndIsFreeFalseAndIsDeletedFalse(Boolean isActive);

    // Free food for NGO
    List<FoodListing> findByIsActiveAndIsFreeTrueAndIsDeletedFalse(Boolean isActive);

    // Search: name, cuisine, homemaker name
    @Query("SELECT f FROM FoodListing f WHERE f.isDeleted = false AND f.isActive = true " +
           "AND (LOWER(f.foodName) LIKE LOWER(CONCAT('%',:query,'%')) " +
           "OR LOWER(f.cuisine) LIKE LOWER(CONCAT('%',:query,'%')) " +
           "OR LOWER(f.homemaker.name) LIKE LOWER(CONCAT('%',:query,'%')))")
    List<FoodListing> searchListings(@Param("query") String query);

    // Location-based (address string match)
    @Query("SELECT f FROM FoodListing f WHERE f.isDeleted = false AND f.isActive = true " +
           "AND f.isFree = :isFree " +
           "AND LOWER(f.homemaker.address) LIKE LOWER(CONCAT('%',:location,'%'))")
    List<FoodListing> findByLocationAndType(@Param("location") String location, @Param("isFree") Boolean isFree);
}
