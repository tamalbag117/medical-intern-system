package com.mims.medicalinternsystem.service;



import com.mims.medicalinternsystem.entity.Notification;
import com.mims.medicalinternsystem.repository.NotificationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository repo;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 🔥 CREATE + PUSH
    public void send(String email, String message) {

        Notification n = new Notification();
        n.setEmail(email);
        n.setMessage(message);

        repo.save(n);

        messagingTemplate.convertAndSend("/topic/notify/" + email, message);
    }

    // 🔥 GET ALL
    public List<Notification> getMy() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return repo.findByEmailOrderByCreatedAtDesc(email);
    }

    // 🔥 UNREAD COUNT
    public long unreadCount() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return repo.countByEmailAndReadFalse(email);
    }

    // 🔥 MARK READ
    public void markRead(Long id) {
        Notification n = repo.findById(id).orElseThrow();
        n.setRead(true);
        repo.save(n);
    }
}
