package com.foodbridge.foodbridge_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class FoodListing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "homemaker_id")
    private User homemaker;

    private String foodName;
    private String description;
    private Integer quantity;
    private Double price;
    private Boolean isVeg;
    private Boolean isFree; // true = donation for NGO, false = paid for customer
    private String cuisine;

    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private java.time.LocalDateTime startTime;
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private java.time.LocalDateTime endTime;

    @ElementCollection
    @CollectionTable(name = "food_listing_images", joinColumns = @JoinColumn(name = "food_listing_id"))
    @Column(name = "image_url")
    private List<String> imagesUrls;

    private Boolean isActive = true;
    private Boolean isDeleted = false;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt = LocalDateTime.now();
}
