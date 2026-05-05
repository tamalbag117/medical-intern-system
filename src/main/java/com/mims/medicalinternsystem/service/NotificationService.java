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

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void send(String email, String message) {

        Notification n = new Notification();
        n.setEmail(email);
        n.setMessage(message);
        n.setCreatedAt(LocalDateTime.now());

        repo.save(n);

        // 🔥 push real-time
        messagingTemplate.convertAndSend("/topic/notify/" + email, message);
    }

    public List<Notification> getMy(String email) {
        return repo.findByEmailOrderByCreatedAtDesc(email);
    }

    public long countUnread(String email) {
        return repo.countByEmailAndReadFalse(email);
    }
}