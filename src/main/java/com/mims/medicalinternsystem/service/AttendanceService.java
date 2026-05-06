package com.mims.medicalinternsystem.service;

import com.mims.medicalinternsystem.entity.Attendance;
import com.mims.medicalinternsystem.repository.AttendanceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository repo;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ✅ CHECK IN
    public Attendance checkIn() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        LocalDate today = LocalDate.now();

        if (repo.findByEmailAndDate(email, today).isPresent()) {
            throw new RuntimeException(
                    "Already checked in today"
            );
        }

        Attendance a = new Attendance();

        a.setEmail(email);
        a.setDate(today);
        a.setCheckIn(LocalDateTime.now());

        // 🔥 late after 10 AM
        if (LocalDateTime.now().getHour() >= 10) {
            a.setStatus("LATE");
        } else {
            a.setStatus("PRESENT");
        }

        Attendance saved = repo.save(a);

        messagingTemplate.convertAndSend(
                "/topic/attendance",
                "updated"
        );

        return saved;
    }

    // ✅ CHECK OUT
    public Attendance checkOut() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Attendance a = repo.findByEmailAndDate(
                email,
                LocalDate.now()
        ).orElseThrow(() ->
                new RuntimeException("No check-in found")
        );

        a.setCheckOut(LocalDateTime.now());

        double hours = Duration.between(
                a.getCheckIn(),
                a.getCheckOut()
        ).toMinutes() / 60.0;

        a.setTotalHours(hours);

        Attendance updated = repo.save(a);

        messagingTemplate.convertAndSend(
                "/topic/attendance",
                "updated"
        );

        return updated;
    }

    // ✅ MY HISTORY
    public List<Attendance> myAttendance() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return repo.findByEmailOrderByDateDesc(email);
    }

    // ✅ TODAY ALL
    public List<Attendance> todayAttendance() {
        return repo.findByDate(LocalDate.now());
    }
}
