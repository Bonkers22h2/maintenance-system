package com.bonkers.maintenance_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonkers.maintenance_system.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
}