package com.mims.medicalinternsystem.service;

import com.mims.medicalinternsystem.entity.Notification;
import com.mims.medicalinternsystem.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository repo;

    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;

    // 🔔 SEND NOTIFICATION
    public void send(String email, String message) {

        if (email == null || email.isBlank()) return;
        if (message == null || message.isBlank()) return;

        Notification n = new Notification();
        n.setEmail(email);
        n.setMessage(message);
        n.setCreatedAt(LocalDateTime.now());
        n.setRead(false);

        repo.save(n);

        // 🔥 Safe real-time push
        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/notify/" + email, message);
        }
    }

    // 📥 GET USER NOTIFICATIONS
    public List<Notification> getMy(String email) {
        return repo.findByEmailOrderByCreatedAtDesc(email);
    }

    // 🔢 COUNT UNREAD
    public long countUnread(String email) {
        return repo.countByEmailAndReadFalse(email);
    }

    // ✅ MARK ALL AS READ (NEW)
    public void markAllRead(String email) {
        List<Notification> list = repo.findByEmailOrderByCreatedAtDesc(email);
        list.forEach(n -> n.setRead(true));
        repo.saveAll(list);
    }
}