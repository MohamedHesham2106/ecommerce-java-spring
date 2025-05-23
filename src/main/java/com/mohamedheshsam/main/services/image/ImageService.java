package com.mohamedheshsam.main.services.image;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mohamedheshsam.main.dtos.ImageDto;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.models.Image;
import com.mohamedheshsam.main.models.Product;
import com.mohamedheshsam.main.models.User;
import com.mohamedheshsam.main.respository.ImageRepository;
import com.mohamedheshsam.main.respository.UserRepository;
import com.mohamedheshsam.main.services.products.IProductService;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {

  private final ImageRepository imageRepository;
  private final IProductService productService;
  private final UserRepository userRepository;
  private Cloudinary cloudinary;

  @Value("${CLOUDINARY_URL}")
  private String cloudinaryUrl;

  @PostConstruct
  public void init() {
    cloudinary = new Cloudinary(cloudinaryUrl);
  }

  @Override
  public List<ImageDto> saveImages(List<MultipartFile> files, Long productId) {
    Product product = productService.getProductById(productId);
    List<ImageDto> dtos = new ArrayList<>();
    for (MultipartFile file : files) {
      Image img = uploadAndBuildImage(file, product);
      Image saved = imageRepository.save(img);
      dtos.add(toDto(saved));
    }
    return dtos;
  }

  @Override
  public List<ImageDto> updateImages(List<MultipartFile> files, Long productId) {
    List<Image> existing = imageRepository.findByProductId(productId);
    if (!existing.isEmpty()) {
      // Optionally: delete from Cloudinary
      existing.forEach(img -> {
        try {
          // extract public_id from URL if stored
          String publicId = img.getPublicId();
          if (publicId != null) {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
          }
        } catch (IOException e) {
          throw new RuntimeException("Cloudinary delete failed: " + e.getMessage(), e);
        }
      });
      imageRepository.deleteAll(existing);
    }
    return saveImages(files, productId);
  }

  @Override
  public Image getImageById(Long id) {
    return imageRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No image found with id: " + id));
  }

  @Override
  public void deleteImageById(Long id) {
    Image img = getImageById(id);
    try {
      if (img.getPublicId() != null) {
        cloudinary.uploader().destroy(img.getPublicId(), ObjectUtils.emptyMap());
      }
    } catch (IOException e) {
      throw new RuntimeException("Cloudinary delete failed: " + e.getMessage(), e);
    }
    imageRepository.delete(img);
  }

  @Override
  @Transactional
  public Image saveUserImage(MultipartFile file, Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found for image upload: " + userId));

    try {
      // Upload the new image to Cloudinary first
      Map<?, ?> uploadResult = cloudinary.uploader().upload(
          file.getBytes(),
          ObjectUtils.asMap(
              "resource_type", "auto",
              "folder", "user_images"));
      String url = uploadResult.get("secure_url").toString();
      String publicId = uploadResult.get("public_id").toString();

      // Create new image entity or update existing one
      Image image;

      // Check if the user already has an image - handle null safely
      if (user.getImage() != null) {
        // Get the existing image
        image = user.getImage();

        // Delete old image from Cloudinary if it has a publicId
        if (image.getPublicId() != null) {
          try {
            cloudinary.uploader().destroy(image.getPublicId(), ObjectUtils.emptyMap());
          } catch (IOException e) {
            // Just log the error but continue - we don't want to fail the whole operation
            System.err.println("Failed to delete old image from Cloudinary: " + e.getMessage());
          }
        }

        // Update the existing image with new data
        image.setFileName(file.getOriginalFilename());
        image.setFileType(file.getContentType());
        image.setImageUrl(url);
        image.setPublicId(publicId);
      } else {
        // Create a new image if user doesn't have one
        image = new Image();
        image.setFileName(file.getOriginalFilename());
        image.setFileType(file.getContentType());
        image.setImageUrl(url);
        image.setPublicId(publicId);
        image.setUser(user);
      }

      // Save the image
      return imageRepository.save(image);
    } catch (IOException e) {
      throw new RuntimeException("Cloudinary upload failed: " + e.getMessage(), e);
    }
  }

  private Image uploadAndBuildImage(MultipartFile file, Product product) {
    try {
      Map<?, ?> uploadResult = cloudinary.uploader().upload(
          file.getBytes(),
          ObjectUtils.asMap(
              "resource_type", "auto",
              "folder", "product_images"));
      String url = uploadResult.get("secure_url").toString();
      String publicId = uploadResult.get("public_id").toString();
      Image image = new Image();
      image.setFileName(file.getOriginalFilename());
      image.setFileType(file.getContentType());
      image.setImageUrl(url);
      image.setPublicId(publicId);
      image.setProduct(product);
      return image;
    } catch (IOException e) {
      throw new RuntimeException("Cloudinary upload failed: " + e.getMessage(), e);
    }
  }

  private ImageDto toDto(Image img) {
    ImageDto dto = new ImageDto();
    dto.setId(img.getId());
    dto.setFileName(img.getFileName());
    dto.setImageUrl(img.getImageUrl());
    return dto;
  }
}
