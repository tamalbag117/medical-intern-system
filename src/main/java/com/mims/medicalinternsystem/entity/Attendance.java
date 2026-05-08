package com.mims.medicalinternsystem.entity;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* =========================================================
       ✅ INTERN
    ========================================================= */

    @Column(name = "intern_email")
    private String internEmail;

    /* =========================================================
       ✅ DATE
    ========================================================= */

    private LocalDate date;

    /* =========================================================
       ✅ CHECK IN / OUT
    ========================================================= */

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    /* =========================================================
       ✅ STATUS
    ========================================================= */

    private String status;

    /* =========================================================
       ✅ WORK TRACKING
    ========================================================= */

    @Column(name = "worked_minutes")
    private Long workedMinutes;

    @Column(name = "overtime_minutes")
    private Long overtimeMinutes;

    /* =========================================================
       ✅ SHIFT MANAGEMENT
    ========================================================= */

    @Column(name = "shift_name")
    private String shiftName;

    /* =========================================================
       ✅ LATE DETECTION
    ========================================================= */

    @Column(name = "late_marked")
    private Boolean lateMarked;

    @Column(name = "late_minutes")
    private Long lateMinutes;

    /* =========================================================
       ✅ AUTO ABSENCE
    ========================================================= */

    @Column(name = "auto_marked")
    private Boolean autoMarked;

    /* =========================================================
       ✅ GEOLOCATION
    ========================================================= */

    private Double latitude;

    private Double longitude;

    @Column(name = "geo_verified")
    private Boolean geoVerified;

    @Column(name = "distance_from_hospital")
    private Double distanceFromHospital;

    /* =========================================================
       ✅ QR     CODE
    ========================================================= */

    @Column(name = "qr_verified")
    private Boolean qrVerified;
}