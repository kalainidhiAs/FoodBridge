package com.foodbridge.foodbridge_backend.repository;

import com.foodbridge.foodbridge_backend.model.Review;
import com.foodbridge.foodbridge_backend.model.User;
import com.foodbridge.foodbridge_backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByHomemaker(User homemaker);
    boolean existsByOrderAndCustomer(Order order, User customer);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.homemaker = :homemaker")
    Double findAverageRatingByHomemaker(@Param("homemaker") User homemaker);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.homemaker = :homemaker")
    Long countByHomemaker(@Param("homemaker") User homemaker);
}
