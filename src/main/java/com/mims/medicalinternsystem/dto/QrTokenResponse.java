package com.mims.medicalinternsystem.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QrTokenResponse {

    private String token;

    private String shiftName;

    private Long expiresInMinutes;
}
