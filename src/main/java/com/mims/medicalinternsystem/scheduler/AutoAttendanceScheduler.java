package com.mims.medicalinternsystem.scheduler;

import com.mims.medicalinternsystem.entity.Attendance;
import com.mims.medicalinternsystem.entity.User;
import com.mims.medicalinternsystem.enums.Role;
import com.mims.medicalinternsystem.repository.AttendanceRepository;
import com.mims.medicalinternsystem.repository.LeaveRequestRepository;
import com.mims.medicalinternsystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class AutoAttendanceScheduler {

    private final UserRepository userRepository;

    private final AttendanceRepository attendanceRepository;

    private final LeaveRequestRepository leaveRepository;

    // ✅ RUN DAILY 11 PM
    @Scheduled(cron = "0 0 23 * * *")
    public void autoMarkAbsent() {

        LocalDate today = LocalDate.now();

        log.info("Running auto absence scheduler");

        var interns =
                userRepository.findByRole(Role.INTERN);

        for (User intern : interns) {

            String email = intern.getEmail();

            // ✅ ALREADY HAS ATTENDANCE
            boolean alreadyMarked =
                    attendanceRepository
                            .findByInternEmailAndDate(
                                    email,
                                    today
                            )
                            .isPresent();

            if (alreadyMarked) {
                continue;
            }

            // ✅ APPROVED LEAVE
            boolean onLeave =
                    leaveRepository
                            .existsByEmailAndStatusAndFromDateLessThanEqualAndToDateGreaterThanEqual(
                                    email,
                                    "APPROVED",
                                    today,
                                    today
                            );

            if (onLeave) {

                log.info(
                        "Skipping absent mark for {} due to approved leave",
                        email
                );

                continue;
            }

            // ✅ AUTO ABSENT
            Attendance attendance =
                    Attendance.builder()
                            .internEmail(email)
                            .date(today)
                            .status("ABSENT")
                            .workedMinutes(0L)
                            .autoMarked(true)
                            .build();

            attendanceRepository.save(attendance);

            log.info(
                    "Auto marked absent: {}",
                    email
            );
        }
    }
}
