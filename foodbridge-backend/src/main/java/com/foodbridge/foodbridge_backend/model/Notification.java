package com.foodbridge.foodbridge_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    private String message;
    private String type;        // ORDER, DONATION, VERIFICATION, PAYMENT
    private Boolean isRead = false;
    private LocalDateTime createdAt;
}
