package com.foodbridge.foodbridge_backend.service;

import com.foodbridge.foodbridge_backend.model.*;
import com.foodbridge.foodbridge_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    @Autowired private ReviewRepository reviewRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private HomemakerProfileRepository homemakerProfileRepository;
    @Autowired private NotificationService notificationService;

    public Review submitReview(User customer, Long orderId, int rating, String comment) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!"COMPLETED".equals(order.getStatus())) {
            throw new RuntimeException("You can only review completed orders.");
        }
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("This is not your order.");
        }
        if (reviewRepository.existsByOrderAndCustomer(order, customer)) {
            throw new RuntimeException("You have already reviewed this order.");
        }
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5.");
        }

        Review review = new Review();
        review.setOrder(order);
        review.setCustomer(customer);
        review.setHomemaker(order.getHomemaker());
        review.setRating(rating);
        review.setComment(comment != null && comment.length() > 250 ? comment.substring(0, 250) : comment);
        review.setCreatedAt(LocalDateTime.now());
        Review saved = reviewRepository.save(review);

        // Update homemaker average rating
        updateHomemakerRating(order.getHomemaker());

        notificationService.send(order.getHomemaker(),
                customer.getName() + " left you a " + rating + "★ review!", "ORDER");

        return saved;
    }

    public List<Review> getReviewsForHomemaker(Long homemakerId) {
        User homemaker = new User();
        homemaker.setId(homemakerId);
        return reviewRepository.findByHomemaker(homemaker);
    }

    private void updateHomemakerRating(User homemaker) {
        Double avg = reviewRepository.findAverageRatingByHomemaker(homemaker);
        homemakerProfileRepository.findByUser(homemaker).ifPresent(profile -> {
            profile.setRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
            homemakerProfileRepository.save(profile);
        });
    }
}
