package com.mohamedheshsam.main.controllers;

import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mohamedheshsam.main.dtos.ImageDto;
import com.mohamedheshsam.main.models.Image;
import com.mohamedheshsam.main.responses.ApiResponse;
import com.mohamedheshsam.main.services.image.IImageService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/images")
public class ImageController {
  private final IImageService imageService;

  @PostMapping("/upload")
  public ResponseEntity<ApiResponse> saveImages(@RequestParam List<MultipartFile> files, @RequestParam Long productId) {
    try {
      List<ImageDto> savedImages = imageService.saveImages(files, productId);
      return ResponseEntity.ok(new ApiResponse("Images uploaded successfully", savedImages));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Failed to upload images", e.getMessage()));
    }
  }

  @GetMapping("/image/download/{id}")
  public ResponseEntity<ApiResponse> downloadImage(@PathVariable Long id) {
    try {
      Image image = imageService.getImageById(id);
      ByteArrayResource resource = new ByteArrayResource(image.getImage().getBytes(1, (int) image.getImage().length()));
      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(image.getFileType()))
          .header(HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=\"" + image.getFileName() + "\"")
          .body(new ApiResponse("Image downloaded successfully", resource));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Failed to download image", e.getMessage()));
    }
  }

  @PutMapping("/image/{id}/update")
  public ResponseEntity<ApiResponse> updateImage(@PathVariable Long id, @RequestParam MultipartFile file) {
    try {
      Image image = imageService.getImageById(id);
      if (image != null) {
        imageService.updateImage(file, id);
        return ResponseEntity.ok(new ApiResponse("Image updated successfully", null));
      } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiResponse("Image not found", null));
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Failed to update image", e.getMessage()));
    }
  }

  @DeleteMapping("/image/{id}/update")
  public ResponseEntity<ApiResponse> deleteImage(@PathVariable Long id) {
    try {
      Image image = imageService.getImageById(id);
      if (image != null) {
        imageService.deleteImageById(id);
        return ResponseEntity.ok(new ApiResponse("Image deleted successfully", null));
      } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiResponse("Image not found", null));
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse("Failed to delete image", e.getMessage()));
    }
  }
}
