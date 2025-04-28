# E-Commerce API Documentation

This API provides endpoints for managing users, products, categories, carts, orders, and authentication for an e-commerce platform.

---

## Authentication

### Login

- **URL**: `/api/v1/auth/login`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "password123"
  }
  ```
- **Response**:
  - **200 OK**:
    ```json
    {
      "message": "Login Success!",
      "data": {
        "id": 1,
        "token": "JWT_TOKEN"
      }
    }
    ```
  - **401 Unauthorized**:
    ```json
    {
      "message": "Invalid credentials",
      "data": null
    }
    ```

---

## Users

### Get User by ID

- **URL**: `/api/v1/users/{id}`
- **Method**: `GET`
- **Protected**: No
- **Response**:
  - **200 OK**:
    ```json
    {
      "message": "Success",
      "data": {
        "id": 1,
        "firstName": "John",
        "lastName": "Doe",
        "email": "john.doe@example.com",
        "orders": [],
        "cart": null
      }
    }
    ```
  - **404 Not Found**:
    ```json
    {
      "message": "User not found",
      "data": null
    }
    ```

### Create User

- **URL**: `/api/v1/users`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "password": "password123"
  }
  ```
- **Response**:
  - **200 OK**:
    ```json
    {
      "message": "Create User Success!",
      "data": {
        "id": 1,
        "firstName": "John",
        "lastName": "Doe",
        "email": "john.doe@example.com"
      }
    }
    ```
  - **409 Conflict**:
    ```json
    {
      "message": "Email already exists",
      "data": null
    }
    ```

### Update User

- **URL**: `/api/v1/users/{id}`
- **Method**: `PUT`
- **Request Body**:
  ```json
  {
    "firstName": "Jane",
    "lastName": "Doe"
  }
  ```
- **Response**:
  - **200 OK**:
    ```json
    {
      "message": "Update User Success!",
      "data": {
        "id": 1,
        "firstName": "Jane",
        "lastName": "Doe",
        "email": "john.doe@example.com"
      }
    }
    ```
  - **404 Not Found**:
    ```json
    {
      "message": "User not found",
      "data": null
    }
    ```

### Delete User

- **URL**: `/api/v1/users/{id}`
- **Method**: `DELETE`
- **Response**:
  - **200 OK**:
    ```json
    {
      "message": "Delete User Success!",
      "data": null
    }
    ```
  - **404 Not Found**:
    ```json
    {
      "message": "User not found",
      "data": null
    }
    ```

---

## Products

### Get All Products

- **URL**: `/api/v1/products`
- **Method**: `GET`
- **Protected**: No
- **Response**:
  - **200 OK**:
    ```json
    {
      "message": "success",
      "data": [
        {
          "id": 1,
          "name": "Product 1",
          "brand": "Brand A",
          "price": 100.0,
          "inventory": 50,
          "description": "Description of Product 1",
          "category": {
            "id": 1,
            "name": "Category A"
          },
          "images": []
        }
      ]
    }
    ```

### Get Product by ID

- **URL**: `/api/v1/products/{id}`
- **Method**: `GET`
- **Protected**: No
- **Response**:
  - **200 OK**:
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
          "id": 1,
          "name": "Category A"
        },
        "images": []
      }
    }
    ```
  - **404 Not Found**:
    ```json
    {
      "message": "Product not found",
      "data": null
    }
    ```

### Add Product

- **URL**: `/api/v1/products`
- **Method**: `POST`
- **Protected**: Yes (Admin only)
- **Request Body**:
  ```json
  {
    "name": "Product 1",
    "brand": "Brand A",
    "price": 100.0,
    "inventory": 50,
    "description": "Description of Product 1",
    "category": {
      "id": 1,
      "name": "Category A"
    }
  }
  ```
- **Response**:
  - **201 Created**:
    ```json
    {
      "message": "Product added successfully",
      "data": {
        "id": 1,
        "name": "Product 1",
        "brand": "Brand A",
        "price": 100.0,
        "inventory": 50,
        "description": "Description of Product 1",
        "category": {
          "id": 1,
          "name": "Category A"
        }
      }
    }
    ```
  - **500 Internal Server Error**:
    ```json
    {
      "message": "Failed to add product",
      "data": null
    }
    ```

### Update Product

- **URL**: `/api/v1/products/{id}`
- **Method**: `PUT`
- **Protected**: Yes (Admin only)
- **Request Body**:
  ```json
  {
    "name": "Updated Product",
    "brand": "Updated Brand",
    "price": 120.0,
    "inventory": 40,
    "description": "Updated description",
    "category": {
      "id": 1,
      "name": "Category A"
    }
  }
  ```
- **Response**:
  - **200 OK**:
    ```json
    {
      "message": "Product updated successfully",
      "data": {
        "id": 1,
        "name": "Updated Product",
        "brand": "Updated Brand",
        "price": 120.0,
        "inventory": 40,
        "description": "Updated description",
        "category": {
          "id": 1,
          "name": "Category A"
        }
      }
    }
    ```
  - **404 Not Found**:
    ```json
    {
      "message": "Product not found",
      "data": null
    }
    ```

### Delete Product

- **URL**: `/api/v1/products/{id}`
- **Method**: `DELETE`
- **Protected**: Yes (Admin only)
- **Response**:
  - **200 OK**:
    ```json
    {
      "message": "Product deleted successfully",
      "data": null
    }
    ```
  - **404 Not Found**:
    ```json
    {
      "message": "Product not found",
      "data": null
    }
    ```

---

## Categories

### Get All Categories

- **URL**: `/api/v1/categories`
- **Method**: `GET`
- **Protected**: No
- **Response**:
  - **200 OK**:
    ```json
    {
      "message": "Retrieved Categories successfully",
      "data": [
        {
          "id": 1,
          "name": "Category A"
        }
      ]
    }
    ```

### Add Category

- **URL**: `/api/v1/categories`
- **Method**: `POST`
- **Protected**: Yes (Admin only)
- **Request Body**:
  ```json
  {
    "name": "Category A"
  }
  ```
- **Response**:
  - **201 Created**:
    ```json
    {
      "message": "Category added successfully",
      "data": {
        "id": 1,
        "name": "Category A"
      }
    }
    ```
  - **409 Conflict**:
    ```json
    {
      "message": "Category already exists",
      "data": null
    }
    ```

---

## Carts

### Get User Cart

- **URL**: `/api/v1/carts/users/{userId}`
- **Method**: `GET`
- **Protected**: Yes
- **Response**:
  - **200 OK**:
    ```json
    {
      "message": "Success",
      "data": {
        "id": 1,
        "items": [],
        "totalAmount": 0.0
      }
    }
    ```
  - **404 Not Found**:
    ```json
    {
      "message": "Cart not found",
      "data": null
    }
    ```

---

## Orders

### Place Order

- **URL**: `/api/v1/orders/users/{userId}`
- **Method**: `POST`
- **Protected**: Yes
- **Response**:
  - **200 OK**:
    ```json
    {
      "message": "Items Order Success!",
      "data": {
        "id": 1,
        "orderDate": "2023-10-01",
        "totalAmount": 200.0,
        "status": "PENDING",
        "items": []
      }
    }
    ```
  - **500 Internal Server Error**:
    ```json
    {
      "message": "Error occurred!",
      "data": "Error details"
    }
    ```

---

For more details, refer to the source code.
