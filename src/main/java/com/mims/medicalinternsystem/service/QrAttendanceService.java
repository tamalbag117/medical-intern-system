package com.mims.medicalinternsystem.service;

import com.mims.medicalinternsystem.dto.QrAttendanceRequest;
import com.mims.medicalinternsystem.dto.QrTokenResponse;

import com.mims.medicalinternsystem.entity.Attendance;
import com.mims.medicalinternsystem.entity.QrToken;

import com.mims.medicalinternsystem.repository.AttendanceRepository;
import com.mims.medicalinternsystem.repository.QrTokenRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QrAttendanceService {

    private final QrTokenRepository qrRepository;

    private final AttendanceRepository attendanceRepository;

    /* =========================================================
       ✅ GENERATE QR
    ========================================================= */

    public QrTokenResponse generateQr(
            String shiftName
    ) {

        String token =
                UUID.randomUUID().toString();

        QrToken qr =
                QrToken.builder()

                        .token(token)

                        .shiftName(shiftName)

                        .createdBy(currentUser())

                        .createdAt(LocalDateTime.now())

                        .expiresAt(
                                LocalDateTime.now()
                                        .plusMinutes(15)
                        )

                        .active(true)

                        .build();

        qrRepository.save(qr);

        return QrTokenResponse.builder()

                .token(token)

                .shiftName(shiftName)

                .expiresInMinutes(15L)

                .build();
    }

    /* =========================================================
       ✅ QR CHECK IN
    ========================================================= */

    public Attendance qrCheckIn(
            QrAttendanceRequest request
    ) {

        String email =
                currentUser();

        // ✅ ALREADY CHECKED IN
        if (
                attendanceRepository
                        .findByInternEmailAndDate(
                                email,
                                LocalDate.now()
                        )
                        .isPresent()
        ) {

            throw new IllegalStateException(
                    "Already checked in today"
            );
        }

        QrToken qr =
                qrRepository
                        .findByToken(
                                request.getToken()
                        )
                        .orElseThrow(() ->

                                new IllegalStateException(
                                        "Invalid QR token"
                                )
                        );

        // ✅ QR ACTIVE
        if (!qr.getActive()) {

            throw new IllegalStateException(
                    "QR token inactive"
            );
        }

        // ✅ QR EXPIRY
        if (
                LocalDateTime.now()
                        .isAfter(
                                qr.getExpiresAt()
                        )
        ) {

            throw new IllegalStateException(
                    "QR token expired"
            );
        }

        Attendance attendance =
                Attendance.builder()

                        .internEmail(email)

                        .date(LocalDate.now())

                        .checkInTime(LocalDateTime.now())

                        .status("PRESENT")

                        .workedMinutes(0L)

                        .qrVerified(true)

                        .shiftName(
                                qr.getShiftName()
                        )

                        .build();

        return attendanceRepository.save(
                attendance
        );
    }

    /* =========================================================
       ✅ USER
    ========================================================= */

    private String currentUser() {

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return auth.getName();
    }
}