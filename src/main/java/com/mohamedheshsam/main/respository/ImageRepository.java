package com.mohamedheshsam.main.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mohamedheshsam.main.models.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
  List<Image> findByProductId(Long id);
}
