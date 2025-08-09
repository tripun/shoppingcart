A Spring Boot application that calculates the price of a shopping basket with special offers and discounts.

## Features

- Shopping cart price calculation with special offers
- Product catalog management
- Inventory tracking
- Redis caching
- Kafka event processing
- DynamoDB local storage
- JWT-based authentication
- Swagger API documentation

## Special Offers

- Apples: 35p each
- Bananas: 20p each
- Melons: 50p each (buy one get one free)
- Limes: 15p each (three for the price of two)

## Prerequisites
# Shopping Cart Application
- Java 17 or higher
- Docker and Docker Compose
- Maven
- Gradle

## Quick Start

1. Build the application:
   ```bash
   mvn clean package
   ```

2. Start the services using Docker Compose:
   ```bash
   docker-compose up -d
   ```

3. Access the application:
   - Application: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html

## Project Structure

- `src/main/java`: Main Java source code.
    - `com/example/shoppingcart/controller`: REST API endpoints.
    - `com/example/shoppingcart/service`: Business logic and service implementations.
    - `com/example/shoppingcart/repository`: Data access layer.
    - `com/example/shoppingcart/model`: Data models (entities, DTOs).
    - `com/example/shoppingcart/config`: Spring configurations.
    - `com/example/shoppingcart/aspect`: Aspect-oriented programming (AOP) for cross-cutting concerns.
    - `com/example/shoppingcart/exception`: Custom exceptions and global exception handling.
    - `com/example/shoppingcart/filter`: Servlet filters for request processing.
    - `com/example/shoppingcart/security`: Security configurations and JWT handling.
- `src/main/resources`: Application resources (e.g., `application.properties`, database migrations).
- `src/test/java`: Unit, integration, and system tests.
- `k8s`: Kubernetes deployment configurations.
- `postman`: Postman collection for API testing.
- `docs`: Additional documentation.

## API Endpoints

- POST `/api/cart/calculate` - Calculate total price of items in cart
- POST `/api/cart/checkout` - Process checkout and update inventory

### Direct Checkout API

This endpoint allows for a direct checkout process by providing a list of product names/IDs, without the need to first create and manage a shopping cart. It calculates the total price, including discounts, and returns the checkout summary.

-   **POST** `/api/v1/cart/checkout/direct`

    **Request Body Example:**
    ```json
    {
      "products": ["APPLE", "APPLE", "MELON", "LIME", "LIME", "LIME"],
      "customerId": "user123",
      "currency": "GBP",
      "region": "UK"
    }
    ```

    **Response Body Example (Success - HTTP 200 OK):**
    ```json
    {
      "success": true,
      "message": "Checkout completed successfully",
      "data": {
        "cartId": "DIRECT-ABCDEF12",
        "items": [
          {
            "productName": "Apple",
            "productId": "APPLE",
            "quantity": 2,
            "unitPrice": 0.35,
            "itemTotal": 0.70,
            "category": "FRUITS"
          },
          {
            "productName": "Melon",
            "productId": "MELON",
            "quantity": 1,
            "unitPrice": 0.50,
            "itemTotal": 0.50,
            "category": "FRUITS"
          },
          {
            "productName": "Lime",
            "productId": "LIME",
            "quantity": 3,
            "unitPrice": 0.15,
            "itemTotal": 0.30,
            "category": "FRUITS"
          }
        ],
        "subtotal": 1.55,
        "totalDiscount": 0.50,
        "finalTotal": 1.05,
        "currency": "GBP",
        "appliedDiscounts": [
          {
            "discountCode": "MELON_BOGO",
            "discountType": "BUY_ONE_GET_ONE_FREE",
            "percentage": 50.00,
            "discountAmount": 0.50,
            "description": "Buy one get one free on melons"
          }
        ],
        "checkoutTime": "2025-08-11T10:30:00"
      }
    }
    ```

## Dependencies to Install

-   **Java 21** or higher
-   **Gradle** (recommended to use `gradlew` wrapper included in the project)
-   **Docker** and **Docker Compose** (for running local services like Redis, Kafka, DynamoDB Local)

## How to Test

The project includes comprehensive unit, integration, and system tests.

1.  **Ensure Docker services are running**:
    ```bash
    docker-compose up -d
    ```
2.  **Run all tests**:
    ```bash
    ./gradlew test
    ```
    (On Windows, use `gradlew.bat test`)

3.  **Run specific tests**:
    To run tests from a specific class (e.g., `CartServiceTest`):
    ```bash
    ./gradlew test --tests "com.example.shoppingcart.CartServiceTest"
    ```
    (On Windows, use `gradlew.bat test --tests "com.example.shoppingcart.CartServiceTest"`)

4.  **Generate Test Reports**:
    Test reports (including JaCoCo code coverage) are generated in `build/reports/tests/test/html/index.html` and `build/reports/jacoco/test/html/index.html` after running tests.

## Security

The application uses JWT tokens for authentication. Default credentials:
- Username: admin
- Password: admin123

## Configuration

The application can be configured using environment variables or by modifying `application.properties`.

Key configurations:
- Redis host/port
- Kafka broker settings
- DynamoDB endpoint
- JWT secret and expiration
- Default region and currency