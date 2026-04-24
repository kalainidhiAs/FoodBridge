package com.foodbridge.foodbridge_backend.repository;

import com.foodbridge.foodbridge_backend.model.OrderItem;
import com.foodbridge.foodbridge_backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
}
