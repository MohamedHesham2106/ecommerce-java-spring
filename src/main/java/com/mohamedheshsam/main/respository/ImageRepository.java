package com.mohamedheshsam.main.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mohamedheshsam.main.models.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
