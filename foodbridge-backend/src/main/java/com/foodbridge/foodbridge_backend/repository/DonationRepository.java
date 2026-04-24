package com.foodbridge.foodbridge_backend.repository;

import com.foodbridge.foodbridge_backend.model.Donation;
import com.foodbridge.foodbridge_backend.model.User;
import com.foodbridge.foodbridge_backend.model.FoodListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByNgo(User ngo);
    List<Donation> findByFoodListing(FoodListing foodListing);
    Optional<Donation> findByNgoAndFoodListing(User ngo, FoodListing foodListing);
    Long countByFoodListingHomemakerAndStatus(User homemaker, String status);
    List<Donation> findByFoodListingHomemaker(User homemaker);

    @Query("SELECT COUNT(d) FROM Donation d WHERE d.foodListing.homemaker = :homemaker AND d.status = :status AND d.collectionTime >= :start AND d.collectionTime <= :end")
    Long countByHomemakerAndStatusAndPeriod(@Param("homemaker") User homemaker,
                                            @Param("status") String status,
                                            @Param("start") java.time.LocalDateTime start,
                                            @Param("end") java.time.LocalDateTime end);
}
