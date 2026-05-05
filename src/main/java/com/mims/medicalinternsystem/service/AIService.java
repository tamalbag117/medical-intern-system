package com.mims.medicalinternsystem.service;

import com.mims.medicalinternsystem.entity.ActivityLog;
import com.mims.medicalinternsystem.dto.AIInsight;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class AIService {

    public List<AIInsight> generateInsights(List<ActivityLog> activities) {

        List<AIInsight> insights = new ArrayList<>();

        if (activities == null || activities.isEmpty()) {
            insights.add(new AIInsight("INFO", "No activity data available yet"));
            return insights;
        }

        int total = activities.size();

        long completed = activities.stream()
                .filter(a -> "COMPLETED".equalsIgnoreCase(a.getStatus()))
                .count();

        long pending = activities.stream()
                .filter(a -> "PENDING".equalsIgnoreCase(a.getStatus()))
                .count();

        int pendingPercent = total == 0 ? 0 : (int) ((pending * 100.0) / total);

        // 🔥 Pending overload (FIXED LOGIC)
        if (pendingPercent > 60) {
            insights.add(new AIInsight("CRITICAL",
                    pendingPercent + "% tasks pending — doctors overloaded"));
        } else if (pendingPercent > 40) {
            insights.add(new AIInsight("WARN",
                    "High pending tasks: " + pendingPercent + "%"));
        } else {
            insights.add(new AIInsight("INFO",
                    "System running efficiently (" + completed + "/" + total + " completed)"));
        }

        // 📈 Today vs Yesterday
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        long todayCount = activities.stream()
                .filter(a -> today.equals(getDate(a)))
                .count();

        long yesterdayCount = activities.stream()
                .filter(a -> yesterday.equals(getDate(a)))
                .count();

        if (yesterdayCount > 0) {
            int change = (int) (((todayCount - yesterdayCount) * 100.0) / yesterdayCount);

            if (change > 20) {
                insights.add(new AIInsight("INFO",
                        "Activity increased by " + change + "% today"));
            } else if (change < -20) {
                insights.add(new AIInsight("WARN",
                        "Activity dropped by " + Math.abs(change) + "%"));
            }
        }

        // 🔥 Peak workload day
        Map<String, Integer> dayMap = new HashMap<>();

        for (ActivityLog a : activities) {
            LocalDate date = getDate(a);
            if (date == null) continue;

            String day = date.getDayOfWeek().toString();
            dayMap.put(day, dayMap.getOrDefault(day, 0) + 1);
        }

        dayMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(e ->
                        insights.add(new AIInsight("INFO",
                                "Peak workload on " + e.getKey()))
                );

        // ⚡ Productivity check
        if (completed < pending) {
            insights.add(new AIInsight("WARN",
                    "Approval rate slower than submissions"));
        }

        // 🚨 Sudden spike detection
        if (yesterdayCount > 0 && todayCount > (yesterdayCount * 2)) {
            insights.add(new AIInsight("CRITICAL",
                    "Sudden spike in workload detected today"));
        }

        return insights;
    }

    // 🔥 SAFE DATE HELPER
    private LocalDate getDate(ActivityLog a) {
        if (a.getVisitDate() != null) return a.getVisitDate();
        if (a.getTimestamp() != null) return a.getTimestamp().toLocalDate();
        return null;
    }
}