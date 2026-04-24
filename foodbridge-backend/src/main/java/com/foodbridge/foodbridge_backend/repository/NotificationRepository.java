package com.foodbridge.foodbridge_backend.repository;

import com.foodbridge.foodbridge_backend.model.Notification;
import com.foodbridge.foodbridge_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);
    List<Notification> findByRecipientAndIsReadFalse(User recipient);
    long countByRecipientAndIsReadFalse(User recipient);
}
