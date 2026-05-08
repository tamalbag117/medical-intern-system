package com.mims.medicalinternsystem.service;

import com.mims.medicalinternsystem.enums.ShiftType;

import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class ShiftService {

    // ✅ SHIFT START
    public LocalTime shiftStart(ShiftType shift) {

        return switch (shift) {

            case MORNING ->
                    LocalTime.of(9, 0);

            case EVENING ->
                    LocalTime.of(16, 0);

            case NIGHT ->
                    LocalTime.of(22, 0);
        };
    }

    // ✅ SHIFT END
    public LocalTime shiftEnd(ShiftType shift) {

        return switch (shift) {

            case MORNING ->
                    LocalTime.of(17, 0);

            case EVENING ->
                    LocalTime.of(23, 0);

            case NIGHT ->
                    LocalTime.of(6, 0);
        };
    }
}