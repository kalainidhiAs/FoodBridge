package com.foodbridge.foodbridge_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "food_listing_id")
    private FoodListing foodListing;

    private Integer quantity;
    private Double unitPrice; // price snapshot at time of order
    private Double subtotal;
}
