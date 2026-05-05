package com.mims.medicalinternsystem.controller;

import com.mims.medicalinternsystem.dto.AIInsight;
import com.mims.medicalinternsystem.service.AIService;
import com.mims.medicalinternsystem.service.ActivityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin // optional safety (CORS fallback)
public class AIController {

    @Autowired
    private AIService aiService;

    @Autowired
    private ActivityService activityService;

    @GetMapping("/insights")
    public List<AIInsight> insights() {
        try {
            List<AIInsight> result = aiService.generateInsights(
                    activityService.allLogsForAI()
            );

            // fallback safety
            if (result == null || result.isEmpty()) {
                return List.of(new AIInsight("INFO", "No insights available"));
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();

            return List.of(
                    new AIInsight("ERROR", "AI temporarily unavailable")
            );
        }
    }
}