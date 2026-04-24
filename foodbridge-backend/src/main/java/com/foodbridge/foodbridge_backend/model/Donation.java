package com.foodbridge.foodbridge_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ngo_id")
    private User ngo;

    @ManyToOne
    @JoinColumn(name = "food_listing_id")
    private FoodListing foodListing;

    private LocalDateTime collectionTime;
    private String status; // PENDING, COLLECTED, CANCELLED
}
