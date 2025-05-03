package com.mohamedheshsam.main.services.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.mohamedheshsam.main.dtos.ImageDto;
import com.mohamedheshsam.main.exceptions.ResourceNotFoundException;
import com.mohamedheshsam.main.models.Image;
import com.mohamedheshsam.main.models.Product;
import com.mohamedheshsam.main.respository.ImageRepository;
import com.mohamedheshsam.main.services.products.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {
  private final ImageRepository imageRepository;
  private final IProductService productService;

  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final String SERVICE_ACCOUNT_KEY_PATH = getPathToGoogleCredentials();

  @Override
  public List<ImageDto> saveImages(List<MultipartFile> files, Long productId) {
    Product product = productService.getProductById(productId);
    List<ImageDto> savedImageDtos = new ArrayList<>();

    for (MultipartFile file : files) {
      Image image = uploadAndBuildImage(file, product);
      Image saved = imageRepository.save(image);
      savedImageDtos.add(toDto(saved));
    }
    return savedImageDtos;
  }

  @Override
  public List<ImageDto> updateImages(List<MultipartFile> files, Long productId) {
    List<Image> existing = imageRepository.findByProductId(productId);
    if (!existing.isEmpty()) {
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
    imageRepository.delete(img);
  }

  private Image uploadAndBuildImage(MultipartFile file, Product product) {
    try {
      File temp = convertMultipartFileToFile(file);
      String url = uploadImageToGoogleDrive(temp);
      Image image = new Image();
      image.setFileName(file.getOriginalFilename());
      image.setFileType(file.getContentType());
      image.setImageUrl(url);
      image.setProduct(product);
      return image;
    } catch (IOException | GeneralSecurityException e) {
      throw new RuntimeException("Image upload failed: " + e.getMessage(), e);
    }
  }

  private ImageDto toDto(Image img) {
    ImageDto dto = new ImageDto();
    dto.setId(img.getId());
    dto.setFileName(img.getFileName());
    dto.setImageUrl(img.getImageUrl());
    return dto;
  }

  private static String getPathToGoogleCredentials() {
    Path path = Paths.get(System.getProperty("user.dir"), "credentials.json");
    return path.toString();
  }

  private String uploadImageToGoogleDrive(File file) throws GeneralSecurityException, IOException {
    Drive drive = createDriveService();
    String folderId = "1UT60sgXfsB284zuCNFaKCIufbf-Va2a2";
    com.google.api.services.drive.model.File metadata = new com.google.api.services.drive.model.File();

    // use the original filename in Drive metadata
    metadata.setName(file.getName());
    metadata.setParents(Collections.singletonList(folderId));

    // CORRECT: derive the real MIME type (e.g. "image/png")
    String mimeType = Files.probeContentType(file.toPath());
    if (mimeType == null) {
      // fallback if probe fails
      mimeType = URLConnection.guessContentTypeFromName(file.getName());
      if (mimeType == null) {
        mimeType = "application/octet-stream";
      }
    }

    FileContent media = new FileContent(mimeType, file);
    com.google.api.services.drive.model.File uploaded = drive.files()
        .create(metadata, media)
        .setFields("id")
        .execute();

    // clean up temp file
    file.delete();
    return "https://drive.google.com/uc?export=view&id=" + uploaded.getId();
  }

  private Drive createDriveService() throws GeneralSecurityException, IOException {
    GoogleCredential cred = GoogleCredential.fromStream(new FileInputStream(SERVICE_ACCOUNT_KEY_PATH))
        .createScoped(Collections.singleton(DriveScopes.DRIVE));
    return new Drive.Builder(
        GoogleNetHttpTransport.newTrustedTransport(),
        JSON_FACTORY,
        cred)
        .build();
  }

  private File convertMultipartFileToFile(MultipartFile file) throws IOException {
    String prefix = "upload-" + System.currentTimeMillis() + "-";
    String suffix = file.getOriginalFilename() != null ? file.getOriginalFilename().replaceAll("\\s+", "_") : "temp";
    File conv = File.createTempFile(prefix, suffix);
    try (FileOutputStream os = new FileOutputStream(conv)) {
      os.write(file.getBytes());
    }
    return conv;
  }
}
