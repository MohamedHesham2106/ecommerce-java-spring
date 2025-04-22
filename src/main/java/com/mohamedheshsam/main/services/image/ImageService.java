package com.mohamedheshsam.main.services.image;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mohamedheshsam.main.dtos.ImageDto;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.models.Image;
import com.mohamedheshsam.main.models.Product;
import com.mohamedheshsam.main.respository.ImageRepository;
import com.mohamedheshsam.main.respository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {
  private final ImageRepository imageRepository;
  private final ProductRepository productRepository;

  @Override
  public Image getImageById(Long id) {
    return imageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Image not found"));
  }

  @Override
  public void deleteImageById(Long id) {
    imageRepository.findById(id).ifPresentOrElse(
        imageRepository::delete, () -> {
          throw new ResourceNotFoundException("Image not found");
        });
  }

  @Override
  public List<ImageDto> saveImages(List<MultipartFile> files, Long productId) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    List<ImageDto> savedImageDto = new ArrayList<>();
    for (MultipartFile file : files) {
      try {
        Image image = new Image();
        image.setFileName(file.getOriginalFilename());
        image.setFileType(file.getContentType());
        image.setImage(new SerialBlob(file.getBytes()));
        image.setProduct(product);

        String buildDownloadUrl = "/api/v1/images/image/download/";
        String downloadUrl = buildDownloadUrl + image.getId();

        image.setDownloadUrl(downloadUrl);
        Image savedImage = imageRepository.save(image);
        savedImage.setDownloadUrl(buildDownloadUrl + savedImage.getId());
        imageRepository.save(savedImage);

        ImageDto imageDto = new ImageDto();
        imageDto.setImageId(savedImage.getId());
        imageDto.setImageName(savedImage.getFileName());
        imageDto.setDownloadUrl(savedImage.getDownloadUrl());
        savedImageDto.add(imageDto);

      } catch (IOException | SQLException e) {
        throw new RuntimeException("Error processing file: " + e.getMessage());
      }
    }
    return savedImageDto;
  }

  @Override
  public void updateImage(MultipartFile file, Long id) {
    Image image = getImageById(id);
    try {
      image.setFileName(file.getOriginalFilename());
      image.setFileType(file.getContentType());
      image.setImage(new SerialBlob(file.getBytes()));
      imageRepository.save(image);
    } catch (IOException | SQLException e) {
      throw new RuntimeException("Error processing image: " + e.getMessage());
    }

  }

}
