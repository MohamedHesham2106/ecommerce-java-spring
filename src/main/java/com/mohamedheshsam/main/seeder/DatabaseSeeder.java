package com.mohamedheshsam.main.seeder;

import com.mohamedheshsam.main.enums.RoleType;
import com.mohamedheshsam.main.models.Category;
import com.mohamedheshsam.main.models.Image;
import com.mohamedheshsam.main.models.Product;
import com.mohamedheshsam.main.models.User;
import com.mohamedheshsam.main.respository.CategoryRepository;
import com.mohamedheshsam.main.respository.ImageRepository;
import com.mohamedheshsam.main.respository.ProductRepository;
import com.mohamedheshsam.main.respository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder {
  private final UserRepository userRepository;
  private final ProductRepository productRepository;
  private final ImageRepository imageRepository;
  private final CategoryRepository categoryRepository;
  private final PasswordEncoder passwordEncoder;

  @PostConstruct
  public void seed() {
    // Seed admin user if not exists
    String adminEmail = "admin@admin.com";
    if (!userRepository.existsByEmail(adminEmail)) {
      User admin = new User();
      admin.setFirstName("Admin");
      admin.setLastName("User");
      admin.setEmail(adminEmail);
      admin.setPassword(passwordEncoder.encode("admin123"));
      admin.setRole(RoleType.ADMIN);
      // Save admin first
      admin = userRepository.save(admin);
      // Assign placeholder image to admin
      Image placeholderImage = new Image(null,
          "jtwtwjjsg7yzw1enh9o1",
          "image/jpeg",
          "https://res.cloudinary.com/dlkykfpl7/image/upload/v1746381833/user_images/jtwtwjjsg7yzw1enh9o1.jpg",
          "user_images/jtwtwjjsg7yzw1enh9o1",
          null,
          admin);
      imageRepository.save(placeholderImage);
      admin.setImage(placeholderImage);
      userRepository.save(admin);
    }

    String[] brandNames = { "Uniqlo", "Zara", "H&M", "Nike" };
    String[] categoryNames = { "Jackets", "Shirts", "Dresses", "Blazers" };
    Category[] categories = new Category[categoryNames.length];
    for (int i = 0; i < categoryNames.length; i++) {
      Category cat = categoryRepository.findByNameIgnoreCase(categoryNames[i]);
      if (cat == null) {
        cat = new Category(categoryNames[i]);
        cat = categoryRepository.save(cat);
      }
      categories[i] = cat;
    }

    // Seed products if not exists
    if (productRepository.count() < 4) {
      // Product 1
      Product p1 = new Product(
          "Varsity Jacket",
          brandNames[0],
          new BigDecimal("40.00"),
          50,
          "Trendy varsity jacket for all seasons.",
          categories[0],
          true);
      p1 = productRepository.save(p1);
      Image i1 = new Image(null, "nnk-684a088d62ed1bb99b051a68fc9ec929-2e6b7fe800f5c97ac06c41622b45cefa.jpg",
          "image/jpeg",
          "https://res.cloudinary.com/dlkykfpl7/image/upload/v1746381833/product_images/halgj9m4ukarleryf98a.jpg",
          "product_images/halgj9m4ukarleryf98a", p1, null);
      imageRepository.save(i1);

      // Product 2
      Product p2 = new Product(
          "Classic Striped Shirt",
          brandNames[1],
          new BigDecimal("55.50"),
          30,
          "Elegant striped shirt for formal and casual wear.",
          categories[1],
          true);
      p2 = productRepository.save(p2);
      Image i2 = new Image(null, "kyle-austin-CO5uWKEo5mY-unsplash.jpg", "image/jpeg",
          "https://res.cloudinary.com/dlkykfpl7/image/upload/v1746382303/product_images/cvi9eqpm37i5wdmhyzat.jpg",
          "product_images/cvi9eqpm37i5wdmhyzat", p2, null);
      imageRepository.save(i2);

      // Product 3
      Product p3 = new Product(
          "Elegance White Dress",
          brandNames[2],
          new BigDecimal("70.00"),
          20,
          "Beautiful white dress for special occasions.",
          categories[2],
          true);
      p3 = productRepository.save(p3);
      Image i3 = new Image(null, "ezgif-4d7f6a9f0084c4.jpg", "image/jpeg",
          "https://res.cloudinary.com/dlkykfpl7/image/upload/v1746382321/product_images/iwxhminayyk0kezel3ap.jpg",
          "product_images/iwxhminayyk0kezel3ap", p3, null);
      imageRepository.save(i3);

      // Product 4
      Product p4 = new Product(
          "4 Button Blazer",
          brandNames[3],
          new BigDecimal("85.99"),
          15,
          "Premium 4 button blazer for business and events.",
          categories[3],
          true);
      p4 = productRepository.save(p4);
      Image i4 = new Image(null, "ezgif-4d4c5c3e3156b2.jpg", "image/jpeg",
          "https://res.cloudinary.com/dlkykfpl7/image/upload/v1746382344/product_images/cnhoxgwacv0bcnlpcfgb.jpg",
          "product_images/cnhoxgwacv0bcnlpcfgb", p4, null);
      imageRepository.save(i4);
    }
  }
}
