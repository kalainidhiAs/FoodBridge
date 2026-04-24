package com.foodbridge.foodbridge_backend.repository;

import com.foodbridge.foodbridge_backend.model.Earning;
import com.foodbridge.foodbridge_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface EarningRepository extends JpaRepository<Earning, Long> {
    List<Earning> findByHomemaker(User homemaker);

    Optional<Earning> findByHomemakerAndMonthAndYear(User homemaker, Integer month, Integer year);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Earning e WHERE e.homemaker = :homemaker AND e.month = :month AND e.year = :year")
    Double sumAmountByHomemakerAndMonthAndYear(@Param("homemaker") User homemaker,
                                               @Param("month") Integer month,
                                               @Param("year") Integer year);

    List<Earning> findAllByOrderByYearDescMonthDesc();

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Earning e WHERE e.homemaker = :homemaker AND e.earningTime >= :start AND e.earningTime <= :end")
    Double sumAmountByHomemakerAndPeriod(@Param("homemaker") User homemaker,
                                         @Param("start") java.time.LocalDateTime start,
                                         @Param("end") java.time.LocalDateTime end);
}
