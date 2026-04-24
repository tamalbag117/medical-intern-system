package com.mims.medicalinternsystem.repository;



import com.mims.medicalinternsystem.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findByInternEmail(String email);
}
