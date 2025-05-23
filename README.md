# E-Commerce API Documentation

This API provides endpoints for managing users, products, categories, carts, orders, and authentication for an e-commerce platform.

## Table of Contents

- [Authentication](#authentication)
- [Users](#users)
- [Products](#products)
- [Categories](#categories)
- [Cart](#cart)
- [Cart Items](#cart-items)
- [Orders](#orders)
- [Image Management](#image-management)
- [Data Transfer Objects (DTOs)](#data-transfer-objects-dtos)

---

## Authentication

### Login

- **URL**: `/api/v1/auth/login`
- **Method**: `POST`
- **Description**: Authenticates a user and returns a JWT token
- **Request Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "password123"
  }
  ```
- **Responses**:
  - **200 OK**: Login successful
    ```json
    {
      "message": "Login Success!",
      "data": {
        "token": "JWT_TOKEN"
      }
    }
    ```
  - **401 Unauthorized**: Invalid credentials
  - **400 Bad Request**: Validation errors in request

### Register

- **URL**: `/api/v1/auth/register`
- **Method**: `POST`
- **Description**: Registers a new user
- **Request Body**: Multipart form data
  - `user` part: JSON with user details
    ```json
    {
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      "password": "password123"
    }
    ```
  - `image` part (optional): Profile image file
- **Responses**:
  - **200 OK**: User registered successfully
    ```json
    {
      "message": "Create User Success!",
      "data": {
        "id": 1,
        "firstName": "John",
        "lastName": "Doe",
        "email": "john.doe@example.com",
        "role": "USER"
      }
    }
    ```
  - **409 Conflict**: Email already exists
  - **400 Bad Request**: Validation errors in request

---

## Users

### Get All Users (Admin only)

- **URL**: `/api/v1/users`
- **Method**: `GET`
- **Description**: Retrieves all users in the system
- **Authorization**: Requires ADMIN role
- **Responses**:
  - **200 OK**: List of users
    ```json
    {
      "message": "Success",
      "data": {
        "users": [
          {
            "id": 1,
            "firstName": "John",
            "lastName": "Doe",
            "email": "john.doe@example.com",
            "role": "USER",
            "gender": "MALE",
            "image": {
              "id": 1,
              "fileName": "profile.jpg",
              "imageUrl": "path/to/image.jpg"
            },
            "orders": [],
            "cart": null
          }
        ]
      }
    }
    ```
  - **500 Internal Server Error**: Error fetching users
  - **403 Forbidden**: Unauthorized access

### Get User by ID

- **URL**: `/api/v1/users/{id}`
- **Method**: `GET`
- **Description**: Retrieves user details by ID
- **Responses**:
  - **200 OK**: User details
    ```json
    {
      "message": "Success",
      "data": {
        "id": 1,
        "firstName": "John",
        "lastName": "Doe",
        "email": "john.doe@example.com",
        "role": "USER",
        "gender": "MALE",
        "image": {
          "id": 1,
          "fileName": "profile.jpg",
          "imageUrl": "path/to/image.jpg"
        },
        "orders": [],
        "cart": null
      }
    }
    ```
  - **404 Not Found**: User not found

### Update User Profile

- **URL**: `/api/v1/users`
- **Method**: `PUT`
- **Description**: Updates the authenticated user's profile
- **Request Body**: Multipart form data
  - `user` part (optional): JSON with user details to update
    ```json
    {
      "firstName": "Jane",
      "lastName": "Doe",
      "gender": "FEMALE"
    }
    ```
  - `image` part (optional): New profile image
- **Responses**:
  - **200 OK**: Profile updated successfully
    ```json
    {
      "message": "Update User Success!",
      "data": {
        "id": 1,
        "firstName": "Jane",
        "lastName": "Doe",
        "email": "jane.doe@example.com",
        "image": {
          "id": 1,
          "fileName": "new-profile.jpg",
          "imageUrl": "path/to/updated-image.jpg"
        }
      }
    }
    ```
  - **400 Bad Request**: No update data provided
  - **404 Not Found**: User not found
  - **500 Internal Server Error**: Error updating user profile

### Update User (Admin only)

- **URL**: `/api/v1/users/{id}`
- **Method**: `PUT`
- **Description**: Updates any user by ID (admin only)
- **Authorization**: Requires ADMIN role
- **Request Body**: Multipart form data
  - `user` part: JSON with user details to update
  - `image` part (optional): New profile image
- **Responses**:
  - **200 OK**: User updated successfully
  - **404 Not Found**: User not found
  - **403 Forbidden**: Unauthorized access
  - **500 Internal Server Error**: Error updating user

### Delete User

- **URL**: `/api/v1/users/{id}`
- **Method**: `DELETE`
- **Description**: Deletes a user by ID
- **Responses**:
  - **200 OK**: User deleted successfully
    ```json
    {
      "message": "Delete User Success!",
      "data": null
    }
    ```
  - **404 Not Found**: User not found
  - **403 Forbidden**: Unauthorized access

---

## Products

### Get All Products

- **URL**: `/api/v1/products`
- **Method**: `GET`
- **Description**: Retrieves products with optional filtering and pagination
- **Query Parameters**:
  - `category` (optional): Filter by category name
  - `brand` (optional): Filter by brand name
  - `name` (optional): Filter by product name
  - `isFeatured` (optional): Filter featured products
  - `page` (default: 1): Page number
  - `limit` (default: 10): Items per page
- **Responses**:
  - **200 OK**: Products retrieved
    ```json
    {
      "message": "success",
      "data": {
        "products": [
          {
            "id": 1,
            "name": "Product 1",
            "brand": "Brand A",
            "price": 100.0,
            "inventory": 50,
            "description": "Description of Product 1",
            "category": {
              "name": "Category A"
            },
            "isFeatured": true,
            "images": [
              {
                "id": 1,
                "fileName": "product1.jpg",
                "imageUrl": "path/to/image.jpg"
              }
            ]
          }
        ],
        "meta": {
          "page": 1,
          "pages": 5,
          "limit": 10,
          "offset": 0,
          "total": 50
        }
      }
    }
    ```
  - **404 Not Found**: No products found for this page
  - **500 Internal Server Error**: Failed to retrieve products

### Get Product by ID

- **URL**: `/api/v1/products/{id}`
- **Method**: `GET`
- **Description**: Retrieves a product by ID
- **Responses**:
  - **200 OK**: Product details
    ```json
    {
      "message": "success",
      "data": {
        "id": 1,
        "name": "Product 1",
        "brand": "Brand A",
        "price": 100.0,
        "inventory": 50,
        "description": "Description of Product 1",
        "category": {
          "name": "Category A"
        },
        "isFeatured": true,
        "images": [
          {
            "id": 1,
            "fileName": "product1.jpg",
            "imageUrl": "path/to/image.jpg"
          }
        ]
      }
    }
    ```
  - **404 Not Found**: Product not found

### Add Product (Admin only)

- **URL**: `/api/v1/products`
- **Method**: `POST`
- **Description**: Adds a new product
- **Authorization**: Requires ADMIN role
- **Request Body**: Multipart form data
  - `product` part: JSON with product details
    ```json
    {
      "name": "Product 1",
      "brand": "Brand A",
      "price": 100.0,
      "inventory": 50,
      "description": "Description of Product 1",
      "category": {
        "name": "Category A"
      },
      "isFeatured": true
    }
    ```
  - `images` part (optional): Array of product images (files)
- **Responses**:
  - **201 Created**: Product added successfully
    ```json
    {
      "message": "Product and images added successfully",
      "data": {
        "id": 1,
        "name": "Product 1",
        "brand": "Brand A",
        "price": 100.0,
        "inventory": 50,
        "description": "Description of Product 1",
        "category": {
          "name": "Category A"
        },
        "isFeatured": true
      }
    }
    ```
  - **403 Forbidden**: Unauthorized access
  - **500 Internal Server Error**: Failed to add product

### Update Product (Admin only)

- **URL**: `/api/v1/products/{id}`
- **Method**: `PUT`
- **Description**: Updates a product by ID
- **Authorization**: Requires ADMIN role
- **Request Body**: Multipart form data
  - `product` part: JSON with product details to update
    ```json
    {
      "name": "Updated Product Name",
      "brand": "Brand B",
      "price": 120.0,
      "inventory": 75,
      "description": "Updated description",
      "category": {
        "id": 2,
        "name": "Category B"
      },
      "isFeatured": false
    }
    ```
  - `images` part (optional): New product images (files)
- **Responses**:
  - **200 OK**: Product updated successfully
    ```json
    {
      "message": "Product and images updated successfully",
      "data": {
        "id": 1,
        "name": "Updated Product Name",
        "brand": "Brand B",
        "price": 120.0,
        "inventory": 75,
        "description": "Updated description",
        "category": {
          "id": 2,
          "name": "Category B"
        },
        "isFeatured": false,
        "images": [
          {
            "id": 2,
            "fileName": "new-image.jpg",
            "imageUrl": "path/to/new-image.jpg"
          }
        ]
      }
    }
    ```
  - **404 Not Found**: Product not found
  - **403 Forbidden**: Unauthorized access
  - **500 Internal Server Error**: Failed to update product

### Delete Product (Admin only)

- **URL**: `/api/v1/products/{id}`
- **Method**: `DELETE`
- **Description**: Deletes a product by ID
- **Authorization**: Requires ADMIN role
- **Responses**:
  - **200 OK**: Product deleted successfully
    ```json
    {
      "message": "Product deleted successfully",
      "data": null
    }
    ```
  - **404 Not Found**: Product not found
  - **403 Forbidden**: Unauthorized access
  - **500 Internal Server Error**: Failed to delete product

### Get All Brands

- **URL**: `/api/v1/products/brands`
- **Method**: `GET`
- **Description**: Retrieves all brands with product counts
- **Responses**:
  - **200 OK**: List of brands with counts
    ```json
    [
      {
        "brand": "Brand A",
        "count": 10
      },
      {
        "brand": "Brand B",
        "count": 5
      }
    ]
    ```

### Get Products Count

- **URL**: `/api/v1/products/count`
- **Method**: `GET`
- **Description**: Gets the total number of products
- **Responses**:
  - **200 OK**: Count of products
    ```json
    {
      "message": "success",
      "data": {
        "count": 50
      }
    }
    ```

---

## Categories

### Get All Categories

- **URL**: `/api/v1/categories`
- **Method**: `GET`
- **Description**: Retrieves all categories
- **Responses**:
  - **200 OK**: List of categories
    ```json
    {
      "message": "Retrieved Categories successfully",
      "data": [
        {
          "id": 1,
          "name": "Category A"
        },
        {
          "id": 2,
          "name": "Category B"
        }
      ]
    }
    ```
  - **500 Internal Server Error**: Failed to retrieve categories

### Add Category

- **URL**: `/api/v1/categories`
- **Method**: `POST`
- **Description**: Adds a new category
- **Request Body**:
  ```json
  {
    "name": "Category A"
  }
  ```
- **Responses**:
  - **201 Created**: Category added successfully
    ```json
    {
      "message": "Category added successfully",
      "data": {
        "id": 1,
        "name": "Category A"
      }
    }
    ```
  - **409 Conflict**: Category already exists
  - **500 Internal Server Error**: Failed to add category

### Get Category by ID

- **URL**: `/api/v1/categories/{id}`
- **Method**: `GET`
- **Description**: Retrieves a category by ID
- **Responses**:
  - **200 OK**: Category details
    ```json
    {
      "message": "Category retrieved successfully",
      "data": {
        "id": 1,
        "name": "Category A"
      }
    }
    ```
  - **404 Not Found**: Category not found
  - **500 Internal Server Error**: Failed to retrieve category

### Get Category by Name

- **URL**: `/api/v1/categories?name={name}`
- **Method**: `GET`
- **Description**: Retrieves a category by name
- **Responses**:
  - **200 OK**: Category details
    ```json
    {
      "message": "Category retrieved successfully",
      "data": {
        "id": 1,
        "name": "Category A"
      }
    }
    ```
  - **404 Not Found**: Category not found
  - **500 Internal Server Error**: Failed to retrieve category

### Update Category

- **URL**: `/api/v1/categories/{id}`
- **Method**: `PUT`
- **Description**: Updates a category by ID
- **Request Body**:
  ```json
  {
    "name": "Updated Category"
  }
  ```
- **Responses**:
  - **200 OK**: Category updated successfully
    ```json
    {
      "message": "Category updated successfully",
      "data": {
        "id": 1,
        "name": "Updated Category"
      }
    }
    ```
  - **404 Not Found**: Category not found
  - **500 Internal Server Error**: Failed to update category

### Delete Category

- **URL**: `/api/v1/categories/{id}`
- **Method**: `DELETE`
- **Description**: Deletes a category by ID
- **Responses**:
  - **200 OK**: Category deleted successfully
    ```json
    {
      "message": "Category deleted successfully",
      "data": null
    }
    ```
  - **404 Not Found**: Category not found
  - **500 Internal Server Error**: Failed to delete category

---

## Cart

### Get User Cart

- **URL**: `/api/v1/carts`
- **Method**: `GET`
- **Description**: Retrieves the authenticated user's cart
- **Authorization**: Requires authenticated user
- **Responses**:
  - **200 OK**: Cart details
    ```json
    {
      "message": "Cart retrieved successfully.",
      "data": {
        "id": 1,
        "items": [
          {
            "id": 1,
            "quantity": 2,
            "unitPrice": 100.0,
            "totalPrice": 200.0,
            "product": {
              "id": 1,
              "name": "Product 1",
              "brand": "Brand A",
              "price": 100.0,
              "inventory": 50,
              "images": [
                {
                  "id": 1,
                  "fileName": "product1.jpg",
                  "imageUrl": "path/to/image.jpg"
                }
              ]
            }
          }
        ],
        "totalAmount": 200.0
      }
    }
    ```
  - **404 Not Found**: Cart not found
  - **401 Unauthorized**: User not authenticated

### Clear Cart

- **URL**: `/api/v1/carts`
- **Method**: `DELETE`
- **Description**: Clears all items from the authenticated user's cart
- **Authorization**: Requires authenticated user
- **Responses**:
  - **200 OK**: Cart cleared successfully
    ```json
    {
      "message": "Cart cleared successfully.",
      "data": null
    }
    ```
  - **404 Not Found**: Cart not found
  - **401 Unauthorized**: User not authenticated

### Get Cart Items Count

- **URL**: `/api/v1/carts/count`
- **Method**: `GET`
- **Description**: Gets the count of items in the authenticated user's cart
- **Authorization**: Requires authenticated user
- **Responses**:
  - **200 OK**: Count of items
    ```json
    {
      "message": "Items count retrieved successfully.",
      "data": {
        "count": 5
      }
    }
    ```
  - **404 Not Found**: Cart not found
  - **401 Unauthorized**: User not authenticated

---

## Cart Items

### Add Item to Cart

- **URL**: `/api/v1/carts/items`
- **Method**: `POST`
- **Description**: Adds an item to the authenticated user's cart
- **Authorization**: Requires authenticated user
- **Request Body**:
  ```json
  {
    "productId": 1,
    "quantity": 2
  }
  ```
- **Responses**:
  - **201 Created**: Item added successfully
    ```json
    {
      "message": "Item added successfully.",
      "data": 1
    }
    ```
  - **404 Not Found**: Product not found
  - **401 Unauthorized**: User not authenticated
  - **400 Bad Request**: Invalid quantity or product ID

### Update Item Quantity

- **URL**: `/api/v1/carts/items/{productId}`
- **Method**: `PUT`
- **Description**: Updates the quantity of a product in the cart
- **Authorization**: Requires authenticated user
- **Request Body**:
  ```json
  {
    "quantity": 3
  }
  ```
- **Responses**:
  - **200 OK**: Item updated successfully
    ```json
    {
      "message": "Item updated successfully.",
      "data": null
    }
    ```
  - **404 Not Found**: Item not found
  - **401 Unauthorized**: User not authenticated
  - **400 Bad Request**: Invalid quantity

### Remove Item from Cart

- **URL**: `/api/v1/carts/items/{productId}`
- **Method**: `DELETE`
- **Description**: Removes a product from the cart
- **Authorization**: Requires authenticated user
- **Responses**:
  - **200 OK**: Item removed successfully
    ```json
    {
      "message": "Item removed successfully.",
      "data": null
    }
    ```
  - **404 Not Found**: Item not found
  - **401 Unauthorized**: User not authenticated

---

## Orders

### Create Order

- **URL**: `/api/v1/orders`
- **Method**: `POST`
- **Description**: Creates a new order from the authenticated user's cart
- **Authorization**: Requires authenticated user
- **Process**:
  1. Gets the authenticated user's cart
  2. Creates an order with PENDING status
  3. Converts cart items to order items
  4. Calculates the total amount
  5. Reduces product inventory quantities
  6. Saves the order
  7. Clears the cart
- **Responses**:
  - **200 OK**: Order created successfully
    ```json
    {
      "message": "Items Order Success!",
      "data": {
        "id": 1,
        "userId": 1,
        "totalAmount": 200.0,
        "status": "PENDING",
        "items": [
          {
            "id": 1,
            "quantity": 2,
            "price": 100.0,
            "name": "Product 1",
            "images": [
              {
                "id": 1,
                "fileName": "product1.jpg",
                "imageUrl": "path/to/image.jpg"
              }
            ]
          }
        ]
      }
    }
    ```
  - **500 Internal Server Error**: Error creating order
  - **404 Not Found**: Cart not found
  - **400 Bad Request**: Cart is empty

### Get User Orders

- **URL**: `/api/v1/orders`
- **Method**: `GET`
- **Description**: Retrieves all orders for the authenticated user
- **Authorization**: Requires authenticated user
- **Responses**:
  - **200 OK**: List of orders
    ```json
    {
      "message": "User orders retrieved successfully",
      "data": [
        {
          "id": 1,
          "userId": 1,
          "totalAmount": 200.0,
          "status": "PENDING",
          "items": [
            {
              "id": 1,
              "quantity": 2,
              "price": 100.0,
              "name": "Product 1",
              "images": [
                {
                  "id": 1,
                  "fileName": "product1.jpg",
                  "imageUrl": "path/to/image.jpg"
                }
              ]
            }
          ]
        }
      ]
    }
    ```
  - **404 Not Found**: No orders found
  - **401 Unauthorized**: User not authenticated

### Get All Orders (Admin only)

- **URL**: `/api/v1/orders/all`
- **Method**: `GET`
- **Description**: Retrieves all orders in the system (admin only)
- **Authorization**: Requires ADMIN role
- **Responses**:
  - **200 OK**: List of all orders
    ```json
    {
      "message": "All orders retrieved successfully",
      "data": [
        {
          "id": 1,
          "userId": 1,
          "totalAmount": 200.0,
          "status": "PENDING",
          "items": [...]
        },
        {
          "id": 2,
          "userId": 2,
          "totalAmount": 150.0,
          "status": "SHIPPED",
          "items": [...]
        }
      ]
    }
    ```
  - **404 Not Found**: No orders found
  - **403 Forbidden**: Unauthorized access

### Cancel Order

- **URL**: `/api/v1/orders/{id}/cancel`
- **Method**: `DELETE`
- **Description**: Cancels an order and restores product inventory
- **Process**:
  1. Finds the order by ID
  2. For each order item, restores the product inventory quantity
  3. Sets the order status to CANCELLED
  4. Saves the updated order
- **Responses**:
  - **200 OK**: Order cancelled successfully
    ```json
    {
      "message": "Order cancelled successfully",
      "data": null
    }
    ```
  - **404 Not Found**: Order not found
  - **400 Bad Request**: Order already delivered/cannot be cancelled
  - **403 Forbidden**: Unauthorized access

### Update Order Status (Admin only)

- **URL**: `/api/v1/orders/{id}`
- **Method**: `PUT`
- **Description**: Updates the status of an order
- **Authorization**: Requires ADMIN role
- **Request Body**:
  ```json
  {
    "status": "SHIPPED"
  }
  ```
- **Possible Status Values**:
  - PENDING: Order initially created
  - PROCESSING: Order is being prepared
  - SHIPPED: Order has been shipped
  - DELIVERED: Order has been delivered
  - CANCELLED: Order has been cancelled
- **Responses**:
  - **200 OK**: Order status updated successfully
    ```json
    {
      "message": "Order status updated successfully",
      "data": null
    }
    ```
  - **404 Not Found**: Order not found
  - **403 Forbidden**: Unauthorized access
  - **400 Bad Request**: Invalid status

---

## Image Management

### Image Handling

The API uses Cloudinary to store and manage images. The following operations are supported:

#### User Profile Images

- **Upload**: During registration or profile update
- **Update**: When updating user profile
- **Storage**: Stored in Cloudinary's 'user_images' folder
- **Format**:
  ```json
  {
    "id": 1,
    "fileName": "profile.jpg",
    "fileType": "image/jpeg",
    "imageUrl": "https://cloudinary.com/path/to/user_images/image.jpg",
    "publicId": "user_images/image_id"
  }
  ```

#### Product Images

- **Upload**: When creating a product
- **Update**: When updating a product
- **Multiple Images**: Products can have multiple images
- **Storage**: Stored in Cloudinary's 'product_images' folder
- **Format**:
  ```json
  {
    "id": 1,
    "fileName": "product.jpg",
    "fileType": "image/jpeg",
    "imageUrl": "https://cloudinary.com/path/to/product_images/image.jpg",
    "publicId": "product_images/image_id"
  }
  ```

#### Image Operations

- **Save**: Upload a new image to Cloudinary and save reference in database
- **Update**: Delete old image from Cloudinary and upload new one
- **Delete**: Remove image from both Cloudinary and database
- **Fetch**: Retrieve image details including URL for display

---

## Data Transfer Objects (DTOs)

### UserDto

```java
{
  "id": Long,
  "firstName": String,
  "lastName": String,
  "email": String,
  "orders": List<OrderDto>,
  "cart": CartDto,
  "role": RoleType,  // "USER" or "ADMIN"
  "image": ImageDto,
  "gender": Gender  // "MALE" or "FEMALE"
}
```

### ProductDto

```java
{
  "id": Long,
  "name": String,
  "brand": String,
  "price": BigDecimal,
  "inventory": Integer,
  "description": String,
  "category": Category,
  "isFeatured": Boolean,
  "images": List<ImageDto>
}
```

### CategoryDto

```java
{
  "id": Long,
  "name": String
}
```

### CartDto

```java
{
  "id": Long,
  "items": List<CartItemDto>,
  "totalAmount": BigDecimal
}
```

### CartItemDto

```java
{
  "id": Long,
  "quantity": Integer,
  "unitPrice": BigDecimal,
  "totalPrice": BigDecimal,
  "product": ProductDto
}
```

### OrderDto

```java
{
  "id": Long,
  "userId": Long,
  "totalAmount": BigDecimal,
  "status": String,  // "PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"
  "items": List<OrderItemDto>
}
```

### OrderItemDto

```java
{
  "id": Long,
  "quantity": Integer,
  "price": BigDecimal,
  "name": String,
  "images": List<ImageDto>
}
```

### ImageDto

```java
{
  "id": Long,
  "fileName": String,
  "imageUrl": String
}
```

### BrandCountDto

```java
{
  "brand": String,
  "count": Long
}
```

---

## Data Models

### User

- **Fields**:
  - `id`: Long - Primary key
  - `firstName`: String - User's first name
  - `lastName`: String - User's last name
  - `email`: String - User's email (unique)
  - `password`: String - Hashed password
  - `gender`: Gender - User's gender (MALE/FEMALE)
  - `role`: RoleType - User's role (USER/ADMIN)
  - `image`: Image - User's profile image (OneToOne)
  - `cart`: Cart - User's shopping cart (OneToOne)
  - `orders`: List<Order> - User's orders (OneToMany)

### Product

- **Fields**:
  - `id`: Long - Primary key
  - `name`: String - Product name
  - `brand`: String - Product brand
  - `price`: BigDecimal - Product price
  - `inventory`: int - Available quantity
  - `description`: String - Product description
  - `isFeatured`: Boolean - Featured status
  - `category`: Category - Product category (ManyToOne)
  - `images`: List<Image> - Product images (OneToMany)

### Category

- **Fields**:
  - `id`: Long - Primary key
  - `name`: String - Category name (unique)
  - `products`: List<Product> - Products in this category (OneToMany)

### Cart

- **Fields**:
  - `id`: Long - Primary key
  - `user`: User - Cart owner (OneToOne)
  - `items`: Set<CartItem> - Items in cart (OneToMany)
  - `totalAmount`: BigDecimal - Total cart value

### CartItem

- **Fields**:
  - `id`: Long - Primary key
  - `cart`: Cart - Parent cart (ManyToOne)
  - `product`: Product - Referenced product (ManyToOne)
  - `quantity`: int - Item quantity
  - `unitPrice`: BigDecimal - Price per unit
  - `totalPrice`: BigDecimal - Total price for this item

### Order

- **Fields**:
  - `id`: Long - Primary key
  - `user`: User - Order owner (ManyToOne)
  - `orderItems`: Set<OrderItem> - Items in order (OneToMany)
  - `totalAmount`: BigDecimal - Total order value
  - `status`: OrderStatus - Current order status (PENDING/PROCESSING/SHIPPED/DELIVERED/CANCELLED)
  - `orderDate`: LocalDate - Date order was placed

### OrderItem

- **Fields**:
  - `id`: Long - Primary key
  - `order`: Order - Parent order (ManyToOne)
  - `product`: Product - Referenced product (ManyToOne)
  - `quantity`: int - Item quantity
  - `price`: BigDecimal - Price at time of order

### Image

- **Fields**:
  - `id`: Long - Primary key
  - `fileName`: String - Original filename
  - `fileType`: String - MIME type
  - `imageUrl`: String - Cloudinary URL
  - `publicId`: String - Cloudinary public ID
  - `product`: Product - Associated product (ManyToOne)
  - `user`: User - Associated user (OneToOne)
