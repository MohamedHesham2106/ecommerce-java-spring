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
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder {
  private final UserRepository userRepository;
  private final ProductRepository productRepository;
  private final ImageRepository imageRepository;
  private final CategoryRepository categoryRepository;
  private final PasswordEncoder passwordEncoder;
  private final Random random = new Random();
  private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

  @PostConstruct
  public void seed() {
    logger.info("Starting database seeding...");

    // Seed admin user if not exists
    seedAdminUser();

    // Create/update categories
    String[] brandNames = { "Uniqlo", "Zara", "H&M", "Nike", "Adidas", "Casio", "Timex", "Champion", "Under Armour" };
    String[] categoryNames = { "Jackets", "Shirts", "Dresses", "Blazers", "Sneakers", "Watches", "Hoodies" };
    Category[] categories = createCategories(categoryNames);

    // Create additional categories
    Category jeansCategory = getOrCreateCategory("Jeans");
    Category poloCategory = getOrCreateCategory("Polo Shirts");

    // Force seeding all products, regardless of existing count
    seedProducts(brandNames, categories, jeansCategory, poloCategory);

    logger.info("Database seeding completed successfully");
  }

  private void seedAdminUser() {
    String adminEmail = "admin@admin.com";
    if (!userRepository.existsByEmail(adminEmail)) {
      logger.info("Creating admin user...");
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
      logger.info("Admin user created successfully");
    } else {
      logger.info("Admin user already exists");
    }
  }

  private Category[] createCategories(String[] categoryNames) {
    Category[] categories = new Category[categoryNames.length];
    for (int i = 0; i < categoryNames.length; i++) {
      categories[i] = getOrCreateCategory(categoryNames[i]);
    }
    return categories;
  }

  private Category getOrCreateCategory(String name) {
    Category category = categoryRepository.findByNameIgnoreCase(name);
    if (category == null) {
      logger.info("Creating category: {}", name);
      category = new Category(name);
      category = categoryRepository.save(category);
    }
    return category;
  }

  private void seedProducts(String[] brandNames, Category[] categories, Category jeansCategory, Category poloCategory) {
    // Always seed all products to ensure they exist
    logger.info("Checking and seeding products...");

    // Product 1 - Varsity Jacket
    seedProduct(
        "Varsity Jacket",
        brandNames[0],
        new BigDecimal("40.00"),
        50,
        "Trendy varsity jacket for all seasons.",
        categories[0], // Jackets
        "nnk-684a088d62ed1bb99b051a68fc9ec929-2e6b7fe800f5c97ac06c41622b45cefa.jpg",
        "https://res.cloudinary.com/dlkykfpl7/image/upload/v1746381833/product_images/halgj9m4ukarleryf98a.jpg",
        "product_images/halgj9m4ukarleryf98a");

    // Product 2 - Classic Striped Shirt
    seedProduct(
        "Classic Striped Shirt",
        brandNames[1],
        new BigDecimal("55.50"),
        30,
        "Elegant striped shirt for formal and casual wear.",
        categories[1], // Shirts
        "kyle-austin-CO5uWKEo5mY-unsplash.jpg",
        "https://res.cloudinary.com/dlkykfpl7/image/upload/v1746382303/product_images/cvi9eqpm37i5wdmhyzat.jpg",
        "product_images/cvi9eqpm37i5wdmhyzat");

    // Product 3 - Elegance White Dress
    seedProduct(
        "Elegance White Dress",
        brandNames[2],
        new BigDecimal("70.00"),
        20,
        "Beautiful white dress for special occasions.",
        categories[2], // Dresses
        "ezgif-4d7f6a9f0084c4.jpg",
        "https://res.cloudinary.com/dlkykfpl7/image/upload/v1746382321/product_images/iwxhminayyk0kezel3ap.jpg",
        "product_images/iwxhminayyk0kezel3ap");

    // Product 4 - 4 Button Blazer
    seedProduct(
        "4 Button Blazer",
        brandNames[3],
        new BigDecimal("85.99"),
        15,
        "Premium 4 button blazer for business and events.",
        categories[3], // Blazers
        "ezgif-4d4c5c3e3156b2.jpg",
        "https://res.cloudinary.com/dlkykfpl7/image/upload/v1746382344/product_images/cnhoxgwacv0bcnlpcfgb.jpg",
        "product_images/cnhoxgwacv0bcnlpcfgb");

    // Product 5 - Nike Jordan 1 Retro High
    Product p5 = seedProduct(
        "Nike Jordan 1 Retro High",
        brandNames[3], // Nike
        new BigDecimal("129.99"),
        25,
        "Premium running shoes with responsive cushioning and breathable mesh upper for maximum comfort.",
        categories[4], // Sneakers
        "adidas-ultraboost-running-shoes.jpg",
        "https://images.unsplash.com/photo-1556906781-9a412961c28c?ixlib=rb-4.0.3&q=85&fm=jpg&crop=entropy&cs=srgb",
        "product_images/adidas-ultraboost");

    // Add second image for Product 5
    if (p5 != null) {
      addProductImage(
          p5,
          "nike-jordan-lifestyle.jpg",
          "https://images.unsplash.com/photo-1597045566677-8cf032ed6634?w=300&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTJ8fG5pa2UlMjBqb3JkYW58ZW58MHx8MHx8fDA%3D",
          "product_images/nike-jordan-lifestyle");
    }

    // Continue with the rest of the products...
    // Product 6 - Air Max Urban Sneakers
    seedProduct(
        "Air Max Urban Sneakers",
        brandNames[3], // Nike
        new BigDecimal("149.50"),
        18,
        "Stylish urban sneakers with iconic air cushioning and durable leather overlays for everyday wear.",
        categories[4], // Sneakers
        "nike-air-max-sneakers.jpg",
        "https://images.unsplash.com/photo-1597248881519-db089d3744a5?ixlib=rb-4.0.3&q=85&fm=jpg&crop=entropy&cs=srgb",
        "product_images/nike-air-max");

    // Continue with remaining products...
    // ... (remaining product seeding calls)

    // Product 7 - G-Shock Digital Sport Watch
    seedProduct(
        "G-Shock Digital Sport Watch",
        brandNames[5], // Casio
        new BigDecimal("89.95"),
        30,
        "Rugged digital watch with shock resistance, 200m water resistance, and multiple time functions.",
        categories[5], // Watches
        "casio-gshock-watch.jpg",
        "https://images.unsplash.com/photo-1523275335684-37898b6baf30?ixlib=rb-4.0.3&q=85&fm=jpg&crop=entropy&cs=srgb",
        "product_images/casio-gshock");

    // Product 8 - Weekender Chronograph Watch
    seedProduct(
        "Weekender Chronograph Watch",
        brandNames[6], // Timex
        new BigDecimal("79.99"),
        22,
        "Classic chronograph watch with fabric strap, INDIGLO light-up dial and date display for versatile wear.",
        categories[5], // Watches
        "timex-weekender-watch.jpg",
        "https://images.unsplash.com/photo-1524592094714-0f0654e20314?ixlib=rb-4.0.3&q=85&fm=jpg&crop=entropy&cs=srgb",
        "product_images/timex-weekender");

    // Product 9 - Reverse Weave Pullover Hoodie
    seedProduct(
        "Reverse Weave Pullover Hoodie",
        brandNames[7], // Champion
        new BigDecimal("65.00"),
        40,
        "Heavyweight cotton-blend hoodie with signature embroidered logo and kangaroo pocket for essential warmth.",
        categories[6], // Hoodies
        "champion-reverse-weave-hoodie.jpg",
        "https://images.unsplash.com/photo-1620799140408-edc6dcb6d633?ixlib=rb-4.0.3&q=85&fm=jpg&crop=entropy&cs=srgb",
        "product_images/champion-hoodie");

    // Product 10 - Premium Fleece Zip-Up Hoodie
    Product p10 = seedProduct(
        "Premium Fleece Zip-Up Hoodie",
        brandNames[8], // Under Armour
        new BigDecimal("79.99"),
        30,
        "Premium full-zip hoodie with brushed fleece interior, adjustable hood, and moisture management technology for all-day comfort.",
        categories[6], // Hoodies
        "premium-zip-hoodie.jpg",
        "https://images.unsplash.com/photo-1732475530155-90158f3b5f79?w=300&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTV8fGhvb2RpZXMlMjBtb2RlbHxlbnwwfHwwfHx8MA%3D%3D",
        "product_images/premium-zip-hoodie");

    if (p10 != null) {
      addProductImage(
          p10,
          "premium-zip-hoodie-2.jpg",
          "https://images.unsplash.com/photo-1732475530118-0db47f851736?q=80&w=988&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
          "product_images/premium-zip-hoodie-2");
    }

    // Product 11 - Adidas Ultraboost 22
    Product p11 = seedProduct(
        "Ultraboost 22 Performance Shoes",
        brandNames[4], // Adidas
        new BigDecimal("179.99"),
        35,
        "Revolutionary running shoes with Boost midsole technology, Primeknit upper, and Continental rubber outsole for ultimate energy return.",
        categories[4], // Sneakers
        "adidas-ultraboost-white.jpg",
        "https://images.unsplash.com/photo-1676821638611-56214ade4d73?q=80&w=987&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        "product_images/adidas-ultraboost-white");

    if (p11 != null) {
      addProductImage(
          p11,
          "adidas-ultraboost-lifestyle.jpg",
          "https://images.unsplash.com/photo-1676821666381-c0456feeeb07?q=80&w=987&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
          "product_images/adidas-ultraboost-lifestyle");
    }

    // Product 12 - Nike Hoodie
    Product p12 = seedProduct(
        "Dri-FIT Academy Hoodie",
        brandNames[3], // Nike
        new BigDecimal("89.99"),
        28,
        "Moisture-wicking pullover hoodie with soft fleece lining, ergonomic fit, and iconic swoosh logo for training and casual wear.",
        categories[6], // Hoodies
        "nike-hoodie-front.jpg",
        "https://images.unsplash.com/photo-1621027212913-da785ebe2bcb?q=80&w=987&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        "product_images/nike-hoodie-front");

    if (p12 != null) {
      addProductImage(
          p12,
          "nike-hoodie-lifestyle.jpg",
          "https://images.unsplash.com/photo-1621027212857-640155d6943c?w=300&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MjR8fEhvb2RpZXN8ZW58MHx8MHx8fDA%3D",
          "product_images/nike-hoodie-lifestyle");
    }

    // Product 13 - Jeans
    Product p13 = seedProduct(
        "Classic Slim Fit Denim Jeans",
        brandNames[1], // Zara
        new BigDecimal("49.95"),
        45,
        "Premium denim jeans with classic five-pocket styling, slim fit cut, and comfortable stretch fabric for everyday wear.",
        jeansCategory,
        "classic-jeans-detail.jpg",
        "https://images.unsplash.com/photo-1475178626620-a4d074967452?w=300&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTV8fEplYW5zfGVufDB8fDB8fHww",
        "product_images/classic-jeans-detail");

    if (p13 != null) {
      addProductImage(
          p13,
          "jeans-lifestyle.jpg",
          "https://images.unsplash.com/photo-1598554747436-c9293d6a588f?q=80&w=987&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
          "product_images/jeans-lifestyle");
    }

    // Product 14 - Jordan 6
    Product p14 = seedProduct(
        "Air Jordan 6 Retro Basketball Shoes",
        brandNames[3], // Nike
        new BigDecimal("199.99"),
        20,
        "Iconic basketball sneakers with visible Air-Sole unit, rubber outsole with herringbone pattern, and premium leather construction.",
        categories[4], // Sneakers
        "jordan-6-profile.jpg",
        "https://images.unsplash.com/photo-1656335362192-2bc9051b1824?q=80&w=995&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        "product_images/jordan-6-profile");

    if (p14 != null) {
      addProductImage(
          p14,
          "jordan-6-detail.jpg",
          "https://images.unsplash.com/photo-1656335219028-dcd6c0759162?w=300&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1yZWxhdGVkfDF8fHxlbnwwfHx8fHw%3D",
          "product_images/jordan-6-detail");
    }

    // Product 15 - Polo Shirt
    Product p15 = seedProduct(
        "Premium Cotton Polo Shirt",
        brandNames[0], // Uniqlo
        new BigDecimal("29.90"),
        55,
        "Classic polo shirt made from 100% premium cotton with three-button placket, ribbed collar, and comfortable regular fit.",
        poloCategory,
        "polo-shirt-navy.jpg",
        "https://images.unsplash.com/photo-1625910513399-c9fcba54338c?w=300&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8UG9sbyUyMHNoaXJ0fGVufDB8fDB8fHww",
        "product_images/polo-shirt-navy");

    if (p15 != null) {
      addProductImage(
          p15,
          "polo-shirt-detail.jpg",
          "https://images.unsplash.com/photo-1625910513520-bed0389ce32f?w=300&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1yZWxhdGVkfDF8fHxlbnwwfHx8fHw%3D",
          "product_images/polo-shirt-detail");
    }

    // Product 16 - Vans Sneakers
    String vansBrand = "Vans";
    Product p16 = seedProduct(
        "Old Skool Classic Skate Shoes",
        vansBrand,
        new BigDecimal("64.95"),
        40,
        "Iconic skate shoes with signature side stripe, durable canvas and suede upper, and waffle rubber outsole for superior grip.",
        categories[4], // Sneakers
        "vans-oldskool-profile.jpg",
        "https://images.unsplash.com/photo-1619646176605-b7417fb53b1e?q=80&w=987&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        "product_images/vans-oldskool-profile");

    if (p16 != null) {
      addProductImage(
          p16,
          "vans-oldskool-lifestyle.jpg",
          "https://images.unsplash.com/photo-1603994457622-63687030478a?w=300&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTh8fFZhbnN8ZW58MHx8MHx8fDA%3D",
          "product_images/vans-oldskool-lifestyle");
    }
  }

  private Product seedProduct(String name, String brand, BigDecimal price, int quantity, String description,
      Category category, String imageName, String imageUrl, String imagePath) {
    try {
      // Check if product already exists by name
      List<Product> existingProducts = productRepository.findByNameIgnoreCase(name);
      Product product;

      if (existingProducts.isEmpty()) {
        // Create new product
        logger.info("Creating product: {}", name);
        product = new Product(name, brand, price, quantity, description, category, random.nextBoolean());
        product = productRepository.save(product);

        // Add primary image
        Image image = new Image(null, imageName, "image/jpeg", imageUrl, imagePath, product, null);
        imageRepository.save(image);
      } else {
        // Update existing product
        logger.info("Product {} already exists, updating if needed", name);
        product = existingProducts.get(0);

        // Update product details if needed
        boolean needsUpdate = false;
        if (!product.getBrand().equals(brand)) {
          product.setBrand(brand);
          needsUpdate = true;
        }
        if (!product.getPrice().equals(price)) {
          product.setPrice(price);
          needsUpdate = true;
        }
        if (product.getInventory() != quantity) {
          product.setInventory(quantity);
          needsUpdate = true;
        }
        if (!product.getDescription().equals(description)) {
          product.setDescription(description);
          needsUpdate = true;
        }
        if (!product.getCategory().equals(category)) {
          product.setCategory(category);
          needsUpdate = true;
        }

        if (needsUpdate) {
          product = productRepository.save(product);
        }

        // Check if image exists
        boolean imageExists = false;
        for (Image img : product.getImages()) {
          if (img.getFileName().equals(imageName)) {
            imageExists = true;
            break;
          }
        }

        // Add image if it doesn't exist
        if (!imageExists) {
          Image image = new Image(null, imageName, "image/jpeg", imageUrl, imagePath, product, null);
          imageRepository.save(image);
        }
      }

      return product;

    } catch (Exception e) {
      logger.error("Failed to seed product {}: {}", name, e.getMessage(), e);
      return null;
    }
  }

  private void addProductImage(Product product, String imageName, String imageUrl, String imagePath) {
    try {
      // Check if image already exists
      boolean imageExists = false;
      for (Image img : product.getImages()) {
        if (img.getFileName().equals(imageName)) {
          imageExists = true;
          break;
        }
      }

      // Add image if it doesn't exist
      if (!imageExists) {
        logger.info("Adding additional image {} to product {}", imageName, product.getName());
        Image image = new Image(null, imageName, "image/jpeg", imageUrl, imagePath, product, null);
        imageRepository.save(image);
      }
    } catch (Exception e) {
      logger.error("Failed to add image {} to product {}: {}", imageName, product.getName(), e.getMessage(), e);
    }
  }
}
