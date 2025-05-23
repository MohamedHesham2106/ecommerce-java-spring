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

@Component
@RequiredArgsConstructor
public class DatabaseSeeder {
  private final UserRepository userRepository;
  private final ProductRepository productRepository;
  private final ImageRepository imageRepository;
  private final CategoryRepository categoryRepository;
  private final PasswordEncoder passwordEncoder;
  private final Random random = new Random();

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

    String[] brandNames = { "Uniqlo", "Zara", "H&M", "Nike", "Adidas", "Casio", "Timex", "Champion", "Under Armour" };
    String[] categoryNames = { "Jackets", "Shirts", "Dresses", "Blazers", "Sneakers", "Watches", "Hoodies" };
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
          random.nextBoolean());
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
          random.nextBoolean());
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
          random.nextBoolean());
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
          random.nextBoolean());
      p4 = productRepository.save(p4);
      Image i4 = new Image(null, "ezgif-4d4c5c3e3156b2.jpg", "image/jpeg",
          "https://res.cloudinary.com/dlkykfpl7/image/upload/v1746382344/product_images/cnhoxgwacv0bcnlpcfgb.jpg",
          "product_images/cnhoxgwacv0bcnlpcfgb", p4, null);
      imageRepository.save(i4);

      // Product 5 - Sneakers 1
      Product p5 = new Product(
          "Nike Jordan 1 Retro High",
          brandNames[3], // Nike (fixed comment)
          new BigDecimal("129.99"),
          25,
          "Premium running shoes with responsive cushioning and breathable mesh upper for maximum comfort.",
          categories[4], // Sneakers
          random.nextBoolean());
      p5 = productRepository.save(p5);
      Image i5 = new Image(null, "adidas-ultraboost-running-shoes.jpg", "image/jpeg",
          "https://images.unsplash.com/photo-1556906781-9a412961c28c?ixlib=rb-4.0.3&q=85&fm=jpg&crop=entropy&cs=srgb",
          "product_images/adidas-ultraboost", p5, null);
      Image i6 = new Image(null, "nike-jordan-lifestyle.jpg", "image/jpeg",
          "https://images.unsplash.com/photo-1597045566677-8cf032ed6634?w=300&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTJ8fG5pa2UlMjBqb3JkYW58ZW58MHx8MHx8fDA%3D",
          "product_images/nike-jordan-lifestyle", p5, null);
      imageRepository.save(i5);
      imageRepository.save(i6);

      // Product 6 - Sneakers 2
      Product p6 = new Product(
          "Air Max Urban Sneakers",
          brandNames[3], // Nike
          new BigDecimal("149.50"),
          18,
          "Stylish urban sneakers with iconic air cushioning and durable leather overlays for everyday wear.",
          categories[4], // Sneakers
          random.nextBoolean());
      p6 = productRepository.save(p6);
      Image i7 = new Image(null, "nike-air-max-sneakers.jpg", "image/jpeg",
          "https://images.unsplash.com/photo-1597248881519-db089d3744a5?ixlib=rb-4.0.3&q=85&fm=jpg&crop=entropy&cs=srgb",
          "product_images/nike-air-max", p6, null);
      imageRepository.save(i7);

      // Product 7 - Watch 1
      Product p7 = new Product(
          "G-Shock Digital Sport Watch",
          brandNames[5], // Casio
          new BigDecimal("89.95"),
          30,
          "Rugged digital watch with shock resistance, 200m water resistance, and multiple time functions.",
          categories[5], // Watches
          random.nextBoolean());
      p7 = productRepository.save(p7);
      Image i8 = new Image(null, "casio-gshock-watch.jpg", "image/jpeg",
          "https://images.unsplash.com/photo-1523275335684-37898b6baf30?ixlib=rb-4.0.3&q=85&fm=jpg&crop=entropy&cs=srgb",
          "product_images/casio-gshock", p7, null);
      imageRepository.save(i8);

      // Product 8 - Watch 2
      Product p8 = new Product(
          "Weekender Chronograph Watch",
          brandNames[6], // Timex
          new BigDecimal("79.99"),
          22,
          "Classic chronograph watch with fabric strap, INDIGLO light-up dial and date display for versatile wear.",
          categories[5], // Watches
          random.nextBoolean());
      p8 = productRepository.save(p8);
      Image i9 = new Image(null, "timex-weekender-watch.jpg", "image/jpeg",
          "https://images.unsplash.com/photo-1524592094714-0f0654e20314?ixlib=rb-4.0.3&q=85&fm=jpg&crop=entropy&cs=srgb",
          "product_images/timex-weekender", p8, null);
      imageRepository.save(i9);

      // Product 9 - Hoodie 1
      Product p9 = new Product(
          "Reverse Weave Pullover Hoodie",
          brandNames[7], // Champion
          new BigDecimal("65.00"),
          40,
          "Heavyweight cotton-blend hoodie with signature embroidered logo and kangaroo pocket for essential warmth.",
          categories[6], // Hoodies
          random.nextBoolean());
      p9 = productRepository.save(p9);
      Image i10 = new Image(null, "champion-reverse-weave-hoodie.jpg", "image/jpeg",
          "https://images.unsplash.com/photo-1620799140408-edc6dcb6d633?ixlib=rb-4.0.3&q=85&fm=jpg&crop=entropy&cs=srgb",
          "product_images/champion-hoodie", p9, null);
      imageRepository.save(i10);

      // Product 10 - Hoodie 2
      Product p10 = new Product(
          "Premium Fleece Zip-Up Hoodie",
          brandNames[8],
          new BigDecimal("79.99"),
          30,
          "Premium full-zip hoodie with brushed fleece interior, adjustable hood, and moisture management technology for all-day comfort.",
          categories[6], // Hoodies
          random.nextBoolean());
      p10 = productRepository.save(p10);
      Image i11 = new Image(null, "premium-zip-hoodie.jpg", "image/jpeg",
          "https://images.unsplash.com/photo-1732475530155-90158f3b5f79?w=300&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTV8fGhvb2RpZXMlMjBtb2RlbHxlbnwwfHwwfHx8MA%3D%3D",
          "product_images/premium-zip-hoodie", p10, null);
      Image i12 = new Image(null, "premium-zip-hoodie-2.jpg", "image/jpeg",
          "https://images.unsplash.com/photo-1732475530118-0db47f851736?q=80&w=988&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
          "product_images/premium-zip-hoodie-2", p10, null);
      imageRepository.save(i11);
      imageRepository.save(i12);

      // Product 11 - Adidas Sneakers
      Product p11 = new Product(
          "Ultraboost 22 Performance Shoes",
          brandNames[4], // Adidas
          new BigDecimal("179.99"),
          35,
          "Revolutionary running shoes with Boost midsole technology, Primeknit upper, and Continental rubber outsole for ultimate energy return.",
          categories[4], // Sneakers
          random.nextBoolean());
      p11 = productRepository.save(p11);
      Image i13 = new Image(null, "adidas-ultraboost-white.jpg", "image/jpeg",
          "https://images.unsplash.com/photo-1676821638611-56214ade4d73?q=80&w=987&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
          "product_images/adidas-ultraboost-white", p11, null);
      Image i14 = new Image(null, "adidas-ultraboost-lifestyle.jpg", "image/jpeg",
          "https://images.unsplash.com/photo-1676821666381-c0456feeeb07?q=80&w=987&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
          "product_images/adidas-ultraboost-lifestyle", p11, null);
      imageRepository.save(i13);
      imageRepository.save(i14);

      // Product 12 - Nike Hoodie
      Product p12 = new Product(
          "Dri-FIT Academy Hoodie",
          brandNames[3], // Nike
          new BigDecimal("89.99"),
          28,
          "Moisture-wicking pullover hoodie with soft fleece lining, ergonomic fit, and iconic swoosh logo for training and casual wear.",
          categories[6], // Hoodies
          random.nextBoolean());
      p12 = productRepository.save(p12);
      Image i15 = new Image(null, "nike-hoodie-front.jpg", "image/jpeg",
          "https://images.unsplash.com/photo-1621027212913-da785ebe2bcb?q=80&w=987&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
          "product_images/nike-hoodie-front", p12, null);
      Image i16 = new Image(null, "nike-hoodie-lifestyle.jpg", "image/jpeg",
          "https://images.unsplash.com/photo-1621027212857-640155d6943c?w=300&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MjR8fEhvb2RpZXN8ZW58MHx8MHx8fDA%3D",
          "product_images/nike-hoodie-lifestyle", p12, null);
      imageRepository.save(i15);
      imageRepository.save(i16);

      // Create Jeans category if not exists
      Category jeansCategory = categoryRepository.findByNameIgnoreCase("Jeans");
      if (jeansCategory == null) {
        jeansCategory = new Category("Jeans");
        jeansCategory = categoryRepository.save(jeansCategory);
      }

      // Product 13 - Jeans
      Product p13 = new Product(
          "Classic Slim Fit Denim Jeans",
          brandNames[1], // Zara
          new BigDecimal("49.95"),
          45,
          "Premium denim jeans with classic five-pocket styling, slim fit cut, and comfortable stretch fabric for everyday wear.",
          jeansCategory,
          random.nextBoolean());
      p13 = productRepository.save(p13);
      Image i17 = new Image(null, "classic-jeans-detail.jpg", "image/jpeg",
          "https://images.unsplash.com/photo-1475178626620-a4d074967452?w=300&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTV8fEplYW5zfGVufDB8fDB8fHww",
          "product_images/classic-jeans-detail", p13, null);
      Image i18 = new Image(null, "jeans-lifestyle.jpg", "image/jpeg",
          "https://images.unsplash.com/photo-1598554747436-c9293d6a588f?q=80&w=987&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
          "product_images/jeans-lifestyle", p13, null);
      imageRepository.save(i17);
      imageRepository.save(i18);

      // Product 14 - Jordan 6
      Product p14 = new Product(
          "Air Jordan 6 Retro Basketball Shoes",
          brandNames[3], // Nike
          new BigDecimal("199.99"),
          20,
          "Iconic basketball sneakers with visible Air-Sole unit, rubber outsole with herringbone pattern, and premium leather construction.",
          categories[4], // Sneakers
          random.nextBoolean());
      p14 = productRepository.save(p14);
      Image i19 = new Image(null, "jordan-6-profile.jpg", "image/jpeg",
          "https://images.unsplash.com/photo-1656335362192-2bc9051b1824?q=80&w=995&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
          "product_images/jordan-6-profile", p14, null);
      Image i20 = new Image(null, "jordan-6-detail.jpg", "image/jpeg",
          "https://images.unsplash.com/photo-1656335219028-dcd6c0759162?w=300&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1yZWxhdGVkfDF8fHxlbnwwfHx8fHw%3D",
          "product_images/jordan-6-detail", p14, null);
      imageRepository.save(i19);
      imageRepository.save(i20);

      // Create Polo Shirts category if not exists
      Category poloCategory = categoryRepository.findByNameIgnoreCase("Polo Shirts");
      if (poloCategory == null) {
        poloCategory = new Category("Polo Shirts");
        poloCategory = categoryRepository.save(poloCategory);
      }

      // Product 15 - Polo Shirt
      Product p15 = new Product(
          "Premium Cotton Polo Shirt",
          brandNames[0], // Uniqlo
          new BigDecimal("29.90"),
          55,
          "Classic polo shirt made from 100% premium cotton with three-button placket, ribbed collar, and comfortable regular fit.",
          poloCategory,
          random.nextBoolean());
      p15 = productRepository.save(p15);
      Image i21 = new Image(null, "polo-shirt-navy.jpg", "image/jpeg",
          "https://images.unsplash.com/photo-1625910513399-c9fcba54338c?w=300&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8UG9sbyUyMHNoaXJ0fGVufDB8fDB8fHww",
          "product_images/polo-shirt-navy", p15, null);
      Image i22 = new Image(null, "polo-shirt-detail.jpg", "image/jpeg",
          "https://images.unsplash.com/photo-1625910513520-bed0389ce32f?w=300&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1yZWxhdGVkfDF8fHxlbnwwfHx8fHw%3D",
          "product_images/polo-shirt-detail", p15, null);
      imageRepository.save(i21);
      imageRepository.save(i22);

      // Add Vans brand to brandNames array if needed (or create new brand variable)
      String vansBrand = "Vans";

      // Product 16 - Vans Sneakers
      Product p16 = new Product(
          "Old Skool Classic Skate Shoes",
          vansBrand,
          new BigDecimal("64.95"),
          40,
          "Iconic skate shoes with signature side stripe, durable canvas and suede upper, and waffle rubber outsole for superior grip.",
          categories[4], // Sneakers
          random.nextBoolean());
      p16 = productRepository.save(p16);
      Image i23 = new Image(null, "vans-oldskool-profile.jpg", "image/jpeg",
          "https://images.unsplash.com/photo-1619646176605-b7417fb53b1e?q=80&w=987&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
          "product_images/vans-oldskool-profile", p16, null);
      Image i24 = new Image(null, "vans-oldskool-lifestyle.jpg", "image/jpeg",
          "https://images.unsplash.com/photo-1603994457622-63687030478a?w=300&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTh8fFZhbnN8ZW58MHx8MHx8fDA%3D",
          "product_images/vans-oldskool-lifestyle", p16, null);
      imageRepository.save(i23);
      imageRepository.save(i24);
    }
  }
}
