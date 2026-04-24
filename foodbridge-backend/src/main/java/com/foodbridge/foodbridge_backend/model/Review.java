package com.foodbridge.foodbridge_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "homemaker_id")
    private User homemaker;

    private Integer rating;     // 1-5
    private String comment;     // optional, max 250 chars
    private LocalDateTime createdAt;
}
