package com.mims.medicalinternsystem.service;

import com.mims.medicalinternsystem.dto.AIInsight;
import com.mims.medicalinternsystem.entity.ActivityLog;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class AIService {

    public List<AIInsight> generateInsights(List<ActivityLog> activities) {

        List<AIInsight> insights = new ArrayList<>();

        // ✅ EMPTY DATA SAFETY
        if (activities == null || activities.isEmpty()) {

            insights.add(
                    new AIInsight(
                            "INFO",
                            "No activity data available yet"
                    )
            );

            return insights;
        }

        int total = activities.size();

        // ✅ STATUS COUNTS
        long completed = activities.stream()
                .filter(a ->
                        "COMPLETED".equalsIgnoreCase(a.getStatus()))
                .count();

        long pending = activities.stream()
                .filter(a ->
                        "PENDING".equalsIgnoreCase(a.getStatus()))
                .count();

        long rejected = activities.stream()
                .filter(a ->
                        "REJECTED".equalsIgnoreCase(a.getStatus()))
                .count();

        // ✅ PERCENTAGES
        int pendingPercent =
                total == 0
                        ? 0
                        : (int) ((pending * 100.0) / total);

        int rejectedPercent =
                total == 0
                        ? 0
                        : (int) ((rejected * 100.0) / total);

        // 🔥 SYSTEM HEALTH
        if (pendingPercent > 60) {

            insights.add(
                    new AIInsight(
                            "CRITICAL",
                            pendingPercent
                                    + "% tasks pending — doctors overloaded"
                    )
            );

        } else if (pendingPercent > 40) {

            insights.add(
                    new AIInsight(
                            "WARN",
                            "High pending tasks: "
                                    + pendingPercent
                                    + "%"
                    )
            );

        } else {

            insights.add(
                    new AIInsight(
                            "INFO",
                            "System running efficiently ("
                                    + completed
                                    + "/"
                                    + total
                                    + " completed)"
                    )
            );
        }

        // 🚨 HIGH REJECTION RATE
        if (rejectedPercent > 30) {

            insights.add(
                    new AIInsight(
                            "WARN",
                            "High rejection rate detected ("
                                    + rejectedPercent
                                    + "%)"
                    )
            );
        }

        // 📈 TODAY VS YESTERDAY
        LocalDate today = LocalDate.now();

        LocalDate yesterday = today.minusDays(1);

        long todayCount = activities.stream()
                .filter(a ->
                        today.equals(getDate(a)))
                .count();

        long yesterdayCount = activities.stream()
                .filter(a ->
                        yesterday.equals(getDate(a)))
                .count();

        if (yesterdayCount > 0) {

            int change =
                    (int) (((todayCount - yesterdayCount)
                            * 100.0)
                            / yesterdayCount);

            if (change > 20) {

                insights.add(
                        new AIInsight(
                                "INFO",
                                "Activity increased by "
                                        + change
                                        + "% today"
                        )
                );

            } else if (change < -20) {

                insights.add(
                        new AIInsight(
                                "WARN",
                                "Activity dropped by "
                                        + Math.abs(change)
                                        + "%"
                        )
                );
            }
        }

        // 🔥 PEAK WORKLOAD DAY
        Map<String, Integer> dayMap = new HashMap<>();

        for (ActivityLog a : activities) {

            LocalDate date = getDate(a);

            if (date == null) {
                continue;
            }

            String rawDay =
                    date.getDayOfWeek().name();

            String day =
                    rawDay.substring(0, 1)
                            +
                            rawDay.substring(1)
                                    .toLowerCase();

            dayMap.put(
                    day,
                    dayMap.getOrDefault(day, 0) + 1
            );
        }

        dayMap.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(e ->

                        insights.add(
                                new AIInsight(
                                        "INFO",
                                        "Peak workload on "
                                                + e.getKey()
                                )
                        )
                );

        // ⚡ PRODUCTIVITY CHECK
        if (completed < pending) {

            insights.add(
                    new AIInsight(
                            "WARN",
                            "Approval rate slower than submissions"
                    )
            );
        }

        // 🚨 SUDDEN SPIKE DETECTION
        if (yesterdayCount > 0
                &&
                todayCount > (yesterdayCount * 2)) {

            insights.add(
                    new AIInsight(
                            "CRITICAL",
                            "Sudden spike in workload detected today"
                    )
            );
        }

        // 🚨 STALE PENDING TASKS
        long stalePending = activities.stream()

                .filter(a ->
                        "PENDING".equalsIgnoreCase(a.getStatus()))

                .filter(a ->
                        getDate(a) != null
                                &&
                                getDate(a).isBefore(
                                        LocalDate.now().minusDays(2)
                                ))

                .count();

        if (stalePending > 0) {

            insights.add(
                    new AIInsight(
                            "CRITICAL",
                            stalePending
                                    + " pending activities older than 2 days"
                    )
            );
        }

        // ✅ SORT BY PRIORITY
        Map<String, Integer> priority = Map.of(
                "CRITICAL", 1,
                "WARN", 2,
                "INFO", 3
        );

        insights.sort(
                Comparator.comparingInt(
                        i ->
                                priority.getOrDefault(
                                        i.getType(),
                                        99
                                )
                )
        );

        return insights;
    }

    // 🔥 SAFE DATE HELPER
    private LocalDate getDate(ActivityLog a) {

        if (a == null) {
            return null;
        }

        if (a.getVisitDate() != null) {
            return a.getVisitDate();
        }

        if (a.getTimestamp() != null) {
            return a.getTimestamp().toLocalDate();
        }

        return null;
    }
}