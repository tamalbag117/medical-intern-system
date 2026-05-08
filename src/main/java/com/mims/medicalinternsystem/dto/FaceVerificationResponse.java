package com.mims.medicalinternsystem.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaceVerificationResponse {

    private Boolean matched;

    private Double similarity;

    private String message;
}