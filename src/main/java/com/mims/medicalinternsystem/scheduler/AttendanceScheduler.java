package com.mims.medicalinternsystem.scheduler;

import com.mims.medicalinternsystem.entity.Attendance;
import com.mims.medicalinternsystem.entity.User;
import com.mims.medicalinternsystem.enums.Role;
import com.mims.medicalinternsystem.repository.AttendanceRepository;
import com.mims.medicalinternsystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AttendanceScheduler {

    private final UserRepository userRepository;

    private final AttendanceRepository attendanceRepository;

    // ✅ RUN DAILY 11 PM
    @Scheduled(cron = "0 0 23 * * *")
    public void markAbsentUsers() {

        LocalDate today = LocalDate.now();

        List<User> interns =
                userRepository.findAll()
                        .stream()
                        .filter(user ->
                                user.getRole() == Role.INTERN
                        )
                        .toList();

        for (User user : interns) {

            boolean exists =
                    attendanceRepository
                            .findByInternEmailAndDate(
                                    user.getEmail(),
                                    today
                            )
                            .isPresent();

            if (!exists) {

                Attendance attendance =
                        Attendance.builder()
                                .internEmail(user.getEmail())
                                .date(today)
                                .status("ABSENT")
                                .workedMinutes(0L)
                                .build();

                attendanceRepository.save(attendance);
            }
        }
    }
}