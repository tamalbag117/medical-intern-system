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
                .filter(a -> "COMPLETED".equals(a.getStatus()))
                .count();

        long pending = activities.stream()
                .filter(a -> "PENDING".equals(a.getStatus()))
                .count();

        int pendingPercent = (int) ((pending * 100.0) / total);

        // ⚠️ Pending overload
        if (pendingPercent > 60) {
            insights.add(new AIInsight("CRITICAL",
                    pendingPercent + "% tasks are pending — doctors overloaded"));
        } else if (pendingPercent > 40) {
            insights.add(new AIInsight("WARN",
                    "High pending tasks: " + pendingPercent + "%"));
        }

        // 📈 Today vs Yesterday
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        long todayCount = activities.stream()
                .filter(a -> today.equals(a.getVisitDate()))
                .count();

        long yesterdayCount = activities.stream()
                .filter(a -> yesterday.equals(a.getVisitDate()))
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

        // 🔥 Peak day
        Map<String, Integer> dayMap = new HashMap<>();

        for (ActivityLog a : activities) {
            if (a.getVisitDate() == null) continue;

            String day = a.getVisitDate().getDayOfWeek().toString();
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
                    "Approval rate is slower than submissions"));
        } else {
            insights.add(new AIInsight("INFO",
                    "System running efficiently"));
        }

        // 🚨 Sudden spike detection
        if (todayCount > (yesterdayCount * 2) && yesterdayCount > 0) {
            insights.add(new AIInsight("CRITICAL",
                    "Sudden spike in workload detected today"));
        }

        return insights;
    }
}