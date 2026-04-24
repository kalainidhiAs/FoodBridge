package com.foodbridge.foodbridge_backend.service;

import com.foodbridge.foodbridge_backend.model.*;
import com.foodbridge.foodbridge_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class EarningService {

    @Autowired private EarningRepository earningRepository;
    @Autowired private DonationRepository donationRepository;

    public Map<String, Object> getMonthlySummary(User homemaker, int month, int year) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusNanos(1);
        return getPeriodSummary(homemaker, start, end, "month_" + month + "_" + year);
    }

    public Map<String, Object> getPeriodSummary(User homemaker, LocalDateTime start, LocalDateTime end, String label) {
        Double totalAmount = earningRepository.sumAmountByHomemakerAndPeriod(homemaker, start, end);
        if (totalAmount == null) totalAmount = 0.0;
        double fee = Math.round(totalAmount * 0.05 * 100.0) / 100.0;
        double net = Math.round((totalAmount - fee) * 100.0) / 100.0;

        Long freeFoodCount = donationRepository.countByHomemakerAndStatusAndPeriod(homemaker, "COLLECTED", start, end);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("label", label);
        result.put("totalIncome", totalAmount);
        result.put("platformFee", fee);
        result.put("netEarning", net);
        result.put("freeFoodCount", freeFoodCount != null ? freeFoodCount : 0L);
        return result;
    }

    public Map<String, Object> getComprehensiveSummary(User homemaker) {
        LocalDateTime now = LocalDateTime.now();
        
        // Daily (Today)
        LocalDateTime dailyStart = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime dailyEnd = now.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        
        // Weekly (Last 7 days)
        LocalDateTime weeklyStart = now.minusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        // Monthly (Current month)
        LocalDateTime monthlyStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("daily", getPeriodSummary(homemaker, dailyStart, dailyEnd, "Today"));
        summary.put("weekly", getPeriodSummary(homemaker, weeklyStart, dailyEnd, "Last 7 Days"));
        summary.put("monthly", getPeriodSummary(homemaker, monthlyStart, dailyEnd, "This Month"));
        
        // For monthly fee payment tracking (existing logic)
        Optional<Earning> earningOpt = earningRepository.findByHomemakerAndMonthAndYear(homemaker, now.getMonthValue(), now.getYear());
        summary.put("feeStatus", earningOpt.map(Earning::getFeeStatus).orElse("PENDING"));
        
        return summary;
    }

    public List<Map<String, Object>> getLast6Months(User homemaker) {
        List<Map<String, Object>> summaries = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 6; i++) {
            LocalDateTime mDate = now.minusMonths(i);
            summaries.add(getMonthlySummary(homemaker, mDate.getMonthValue(), mDate.getYear()));
        }
        return summaries;
    }
}
