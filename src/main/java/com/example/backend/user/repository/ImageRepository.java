package com.example.backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.user.entity.Image;

public interface ImageRepository extends JpaRepository<Image, String> {
}
