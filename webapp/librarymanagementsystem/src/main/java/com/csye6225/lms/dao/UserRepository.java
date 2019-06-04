package com.csye6225.lms.dao;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.csye6225.lms.pojo.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>{

    User findByEmail(String email);
}
