package com.foodbridge.foodbridge_backend.repository;

import com.foodbridge.foodbridge_backend.model.Order;
import com.foodbridge.foodbridge_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerOrderByOrderTimeDesc(User customer);
    List<Order> findByHomemakerOrderByOrderTimeDesc(User homemaker);
    List<Order> findByCustomerAndStatus(User customer, String status);
    List<Order> findByHomemakerAndStatus(User homemaker, String status);
}
