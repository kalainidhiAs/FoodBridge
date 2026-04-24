package com.foodbridge.foodbridge_backend.repository;

import com.foodbridge.foodbridge_backend.model.NgoProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NgoProfileRepository extends JpaRepository<NgoProfile, Long> {
}
