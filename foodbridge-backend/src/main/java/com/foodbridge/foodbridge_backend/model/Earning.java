package com.foodbridge.foodbridge_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class Earning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "homemaker_id")
    private User homemaker;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private Double amount;          // total paid order amount
    private Double platformFee;     // 5% of amount (monthly aggregate)
    private Double netEarning;      // amount - platformFee

    @Column(name = "earning_month")  // 'month' is reserved in H2
    private Integer month;          // 1-12

    @Column(name = "earning_year")   // 'year' is reserved in H2
    private Integer year;

    private String feeStatus;       // PENDING, PAID, OVERDUE
    private LocalDateTime paidAt;
    private LocalDateTime earningTime;
}
