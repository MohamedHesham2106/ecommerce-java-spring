package com.mohamedheshsam.main.dtos;

import lombok.Data;

@Data
public class ImageDto {
  private Long id;
  private String fileName;
  private String imageUrl;
  private String publicId;

  public ImageDto() {
  }

  public ImageDto(Long id, String fileName, String imageUrl) {
    this.id = id;
    this.fileName = fileName;
    this.imageUrl = imageUrl;
  }
}
