package com.csye6225.lms.dao;

import com.csye6225.lms.pojo.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {

}
