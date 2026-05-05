package com.mims.medicalinternsystem.controller;

import com.mims.medicalinternsystem.dto.AIInsight;
import com.mims.medicalinternsystem.service.AIService;
import com.mims.medicalinternsystem.service.ActivityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    @Autowired
    private ActivityService activityService;

    @GetMapping("/insights")
    public List<AIInsight> insights() {
        return aiService.generateInsights(activityService.allLogs());
    }
}