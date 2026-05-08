package com.mims.medicalinternsystem.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.mims.medicalinternsystem.dto.FaceEnrollmentRequest;
import com.mims.medicalinternsystem.dto.FaceVerificationRequest;
import com.mims.medicalinternsystem.dto.FaceVerificationResponse;

import com.mims.medicalinternsystem.entity.FaceProfile;

import com.mims.medicalinternsystem.repository.FaceProfileRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FaceRecognitionService {

    private final FaceProfileRepository repository;

    private final ObjectMapper mapper =
            new ObjectMapper();

    /* =========================================================
       ✅ ENROLL FACE
    ========================================================= */

    public String enrollFace(
            FaceEnrollmentRequest request
    ) {

        String email =
                currentUser();

        FaceProfile profile =
                repository
                        .findByEmail(email)
                        .orElse(
                                FaceProfile.builder()
                                        .email(email)
                                        .createdAt(
                                                LocalDateTime.now()
                                        )
                                        .build()
                        );

        profile.setFaceDescriptor(
                request.getDescriptor()
        );

        profile.setUpdatedAt(
                LocalDateTime.now()
        );

        repository.save(profile);

        return "Face enrolled successfully";
    }

    /* =========================================================
       ✅ VERIFY FACE
    ========================================================= */

    public FaceVerificationResponse verifyFace(
            FaceVerificationRequest request
    ) {

        try {

            String email =
                    currentUser();

            FaceProfile profile =
                    repository
                            .findByEmail(email)
                            .orElseThrow(() ->

                                    new IllegalStateException(
                                            "Face profile not found"
                                    )
                            );

            List<Double> stored =
                    mapper.readValue(
                            profile.getFaceDescriptor(),
                            new TypeReference<List<Double>>() {}
                    );

            List<Double> incoming =
                    mapper.readValue(
                            request.getDescriptor(),
                            new TypeReference<List<Double>>() {}
                    );

            double similarity =
                    calculateSimilarity(
                            stored,
                            incoming
                    );

            boolean matched =
                    similarity >= 0.85;

            return FaceVerificationResponse
                    .builder()
                    .matched(matched)
                    .similarity(similarity)
                    .message(
                            matched
                                    ? "Face verified"
                                    : "Face mismatch"
                    )
                    .build();

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Face verification failed"
            );
        }
    }

    /* =========================================================
       ✅ SIMILARITY ENGINE
    ========================================================= */

    private double calculateSimilarity(

            List<Double> stored,

            List<Double> incoming

    ) {

        if (
                stored.size() != incoming.size()
        ) {

            return 0;
        }

        double sum = 0;

        for (
                int i = 0;
                i < stored.size();
                i++
        ) {

            double diff =
                    stored.get(i)
                            - incoming.get(i);

            sum += diff * diff;
        }

        double distance =
                Math.sqrt(sum);

        return 1.0 / (1.0 + distance);
    }

    /* =========================================================
       ✅ CURRENT USER
    ========================================================= */

    private String currentUser() {

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return auth.getName();
    }
}