package com.foodbridge.foodbridge_backend.repository;

import com.foodbridge.foodbridge_backend.model.HomemakerProfile;
import com.foodbridge.foodbridge_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface HomemakerProfileRepository extends JpaRepository<HomemakerProfile, Long> {
    Optional<HomemakerProfile> findByUser(User user);
}
