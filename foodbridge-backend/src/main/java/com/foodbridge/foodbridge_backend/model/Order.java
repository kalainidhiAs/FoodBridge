package com.foodbridge.foodbridge_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "homemaker_id")
    private User homemaker;

    private String status; // PENDING, CONFIRMED, OUT_FOR_DELIVERY, COMPLETED, REJECTED, CANCELLED
    private Double totalAmount;
    private String paymentMethod; // COD
    private String paymentStatus; // PENDING_PAYMENT, PAID
    private String deliveryAddress;
    private String rejectionRemark;
    private LocalDateTime orderTime;
    private LocalDateTime updatedAt;
}
