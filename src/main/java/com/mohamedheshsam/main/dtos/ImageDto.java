package com.mohamedheshsam.main.dtos;

import lombok.Data;

@Data
public class ImageDto {
  private Long id;
  private String fileName;
  private String downloadUrl;
  private String base64Image;
}
