package com.foodbridge.foodbridge_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
public class HomemakerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String cuisinesOffered;
    private String experience;
    private String specialization;
    private Double rating;
    private String idProofUrl;
    
    @ElementCollection
    private List<String> kitchenImagesUrls;
}
