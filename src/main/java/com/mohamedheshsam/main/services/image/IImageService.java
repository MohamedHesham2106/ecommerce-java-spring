package com.mohamedheshsam.main.services.image;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mohamedheshsam.main.dtos.ImageDto;
import com.mohamedheshsam.main.models.Image;

public interface IImageService {
  Image getImageById(Long id);

  void deleteImageById(Long id);

  List<ImageDto> saveImages(List<MultipartFile> file, Long productId);

  List<ImageDto> updateImages(List<MultipartFile> files, Long productId);

  Image saveUserImage(MultipartFile file, Long userId);
}
