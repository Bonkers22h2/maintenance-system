package com.bonkers.maintenance_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonkers.maintenance_system.model.Notification;
import com.bonkers.maintenance_system.model.User;

public interface NotificationRepository extends JpaRepository<Notification, Long>{
    List<Notification> findByUser(User user);
}