package com.mohamedheshsam.main.dtos;

import lombok.Data;

@Data
public class ImageDto {
  private Long imageId;
  private String imageName;
  private String downloadUrl;
}
