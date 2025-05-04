package com.mohamedheshsam.main.dtos;

import lombok.Data;

@Data
public class ImageDto {
  private Long id;
  private String fileName;
  private String imageUrl;
  private String publicId;
}
