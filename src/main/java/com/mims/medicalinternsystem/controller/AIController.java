package com.mims.medicalinternsystem.controller;

import com.mims.medicalinternsystem.dto.AIInsight;
import com.mims.medicalinternsystem.service.AIService;
import com.mims.medicalinternsystem.service.ActivityService;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private static final Logger log =
            LoggerFactory.getLogger(AIController.class);

    private final AIService aiService;

    private final ActivityService activityService;

    // ✅ AI INSIGHTS
    @GetMapping("/insights")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<List<AIInsight>> insights() {

        try {

            List<AIInsight> insights =
                    aiService.generateInsights(
                            activityService.allLogsForAI()
                    );

            // ✅ fallback protection
            if (insights == null || insights.isEmpty()) {

                insights = List.of(
                        new AIInsight(
                                "INFO",
                                "No insights available yet"
                        )
                );
            }

            return ResponseEntity.ok(insights);

        } catch (Exception e) {

            log.error("AI insight generation failed", e);

            return ResponseEntity.internalServerError()
                    .body(
                            List.of(
                                    new AIInsight(
                                            "ERROR",
                                            "AI temporarily unavailable"
                                    )
                            )
                    );
        }
    }
}