# **PitosPies Web & REST API Service**

**[PitosPies](https://github.com/nikwilldoit/PitosPies)** is an production-ready **Spring Boot application** for online pie ordering and order management. It features a dual-architecture design combining a **Thymeleaf MVC Web Application** for end-users with a **RESTful Web Service API** for mobile, single-page application (SPA), or third-party integrations.

---

## Architecture & Key Features

### 1. Architectural Layers
* **Controller Layer:** Dedicated MVC Controllers for server-side HTML rendering and `@RestController` components for JSON/XML REST API endpoints.
* **Service Layer:** Business logic handling, programmatic validation, relational mappings, and Spring Cache abstraction (`@Cacheable`, `@CacheEvict`, `@Caching`).
* **Data Access Layer:** Lightweight **Spring Data JDBC** persistence using derived queries, custom `@Query` SQL annotations, and custom JDBC Type Converters.
* **Security & Auth:** Spring Security implementation featuring BCrypt password hashing, Custom `UserDetailsService`, Role-Based Access Control (RBAC), and HTTP Basic Authentication.

### 2. Core Capabilities
* **Full CRUD REST API** for Pies, Orders, and Delivery Areas.
* **REST Updates Mechanics:**
  * **PUT (Full Update):** Requires a full valid JSON object mapped to `@Valid @RequestBody`.
  * **PATCH (Partial Update):** Accepts dynamic `Map<String, Object>` payloads to selectively update target properties without overwriting missing fields, followed by programmatic Bean Validation using `BeanPropertyBindingResult` and `Validator`.
* **Advanced Querying:** Native Pagination & Sorting via `PagingAndSortingRepository`, `PageRequest`, and `Sort.Direction`.
* **In-Memory Caching:** Concurrent Map Caching enabled for entities (`Pie`, `Order`, `Area`, `Ingredient`) to optimize database query overhead.
* **Cache Introspection:** Dedicated REST Endpoint to inspect live cache state programmatically.
* **Global Exception Handling:** `@ControllerAdvice` providing standardized JSON error structures for validation errors, type mismatches, missing resources, and unexpected runtime exceptions.

---

## Technology Stack

| Category | Technology / Library |
| :--- | :--- |
| **Language & Runtime** | Java 17+ / Java 25 |
| **Framework** | Spring Boot 3.x / 4.x |
| **Web Layer** | Spring Web MVC, Spring REST |
| **Persistence** | Spring Data JDBC |
| **Database** | MySQL |
| **Security** | Spring Security (BCrypt) |
| **Caching** | Spring Cache (`ConcurrentMapCacheManager`) |
| **Validation** | Jakarta Bean Validation (`@Valid`, `@Validated`, Custom Constraints) |
| **Templating** | Thymeleaf (HTML5 / SCSS) |
| **Notifications** | JavaMailSender (Email), Twilio SDK (SMS) |
| **Utilities** | Lombok, Jackson (`ObjectMapper`) |

---

## Data Model & Relational Mappings

### Domain Entities
* **`User` (`user`):** System users containing credentials, profile info, verification status, and transient `roles` list.
* **`Role` (`role`):** System roles (`USER`, `ADMIN`).
* **`Pie` (`pie`):** Pie catalog item (`name`, `price`, `filename`) with `@MappedCollection` for `Award` entities and `@Transient` ingredient strings.
* **`Ingredient` (`ingredient`):** Pie ingredients linked via join table (`pie_ingredient`).
* **`Award` (`award`):** Awards associated with individual pies mapped using `@MappedCollection(idColumn = "pie_id")`.
* **`Area` (`area`):** Delivery regions available for orders.
* **`Order` (`order`):** Customer orders with transient `area` and `orderItems`. Custom Spring Data JDBC converters handle MySQL boolean mapping (`BooleanToIntegerConverter` / `IntegerToBooleanConverter`).
* **`OrderItem` (`order_item`):** Order line items linking pie names and purchased quantities.

---

## Security & Authorization

Authentication is managed through **Spring Security** using `HTTP Basic Auth` for the REST API and Form-Based Authentication for the Web interface.

### Endpoints & Role Access Rules
* **`ROLE_ADMIN`:** Full access to manage pie catalog endpoints (`/api/pie/**`).
* **`ROLE_USER`:** Access to area endpoints (`/api/area/**`).
* **Public (`permitAll()`):** Authentication, registration, public pie browsing, shopping cart, and general routes.

```java
@Bean
@Primary
protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/api/pie/**").hasAuthority("ROLE_ADMIN")
            .requestMatchers("/api/area/**").hasAuthority("ROLE_USER")
            .anyRequest().permitAll()
        )
        .httpBasic(Customizer.withDefaults());
    return http.build();
}
```

---

## REST API Documentation

### 1. Delivery Areas (`/api/area`)

| Method | Endpoint | Access / Auth | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/area/{id}` | Public | Fetch a specific delivery area by ID |
| `GET` | `/api/areas` | `ROLE_USER` | Fetch all available delivery areas |
| `POST` | `/api/areas` | `ROLE_USER` | Create a new delivery area |
| `PUT` | `/api/area/{id}` | `ROLE_USER` | Fully update a delivery area |
| `PATCH` | `/api/area/{id}` | `ROLE_USER` | Partially update delivery area fields |
| `DELETE`| `/api/area/{id}` | `ROLE_USER` | Delete a delivery area |

---

### 2. Pies Catalog (`/api/pie`)

| Method | Endpoint | Access / Auth | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/pie/{id}` | Public | Get pie details including ingredients & awards |
| `GET` | `/api/pies` | Public | Fetch complete list of pies |
| `GET` | `/api/pies?page=0&size=5` | Public | Paginated list of pies |
| `GET` | `/api/pies?sortBy=price&sortOrder=asc` | Public | Sorted list of pies |
| `GET` | `/api/pies?page=0&size=5&sortBy=name&sortOrder=desc` | Public | Combined Pagination and Sorting |
| `POST` | `/api/pies` | `ROLE_ADMIN` | Create a new pie record |
| `PUT` | `/api/pie/{id}` | `ROLE_ADMIN` | Fully replace an existing pie |
| `PATCH` | `/api/pie/{id}` | `ROLE_ADMIN` | Partially update pie attributes |
| `DELETE`| `/api/pie/{id}` | `ROLE_ADMIN` | Remove a pie from catalog |

---

### 3. Orders (`/api/order`)

| Method | Endpoint | Access / Auth | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/order/{id}` | Public / Auth | Retrieve order details by ID |
| `GET` | `/api/orders` | Public / Auth | List all customer orders |
| `GET` | `/api/orders?page=0&size=10&sortBy=stamp&sortOrder=desc` | Public / Auth | Paginated & sorted orders list |
| `POST` | `/api/orders` | Public / Auth | Submit a new order |
| `PUT` | `/api/order/{id}` | Public / Auth | Full replacement of an existing order |
| `PATCH` | `/api/order/{id}` | Public / Auth | Partial update of an order |
| `DELETE`| `/api/order/{id}` | Public / Auth | Cancel/Delete an order record |

---

### 4. Cache Operations & Introspection

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/cache-data/{name}` | Inspect the active entries stored inside a specific cache region (e.g. `cache`, `pie-cache`) |

---

## Global Exception Handling & API Response Specifications

The application uses a centralized `@ControllerAdvice` (`GlobalExceptionHandler`) to ensure consistent JSON error responses across all REST endpoints.

### Standard Error Response Structure
```json
{
    "timestamp": "2026-10-10T18:00:00.000+00:00",
    "status": 400,
    "error": "Bad Request / Validation Failed Message",
    "path": "uri=/api/orders"
}
```

### Supported HTTP Status Codes
* **`200 OK`**: Successful `GET`, `PUT`, `PATCH`, or `DELETE` execution returning a body.
* **`201 Created`**: Successful creation of a new resource (`POST`).
* **`204 No Content`**: Successful execution where no response body is returned.
* **`400 Bad Request`**: Returned on constraint violations, method argument validation errors, or URL parameter mismatches.
* **`401 Unauthorized`**: Invalid credentials or missing Authorization header.
* **`404 Not Found`**: Resource or route does not exist.
* **`409 Conflict`**: Attempting to create a duplicate entity (`ResourceAlreadyExistsException`).
* **`500 Internal Server Error`**: Unexpected server-side runtime failure.

---

## Setup & Execution

### Prerequisites
* Java JDK 17 or higher
* MySQL Server 8.0+
* Maven 3.8+

### Database Configuration
1. Create the MySQL database: [find the sql code](https://github.com/nikwilldoit/PitosPies/blob/main/src/main/resources/db/DB_schema.sql): 
   ```sql
   CREATE DATABASE pitos;
   ```
2. Configure database credentials in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/pitos?useSSL=false&serverTimezone=UTC
   spring.datasource.username=YOUR_DB_USER
   spring.datasource.password=YOUR_DB_PASSWORD
   ```

### Running the Application
Build and run using Maven:
```bash
mvn clean install
mvn spring-boot:run
```
The server will start at `http://localhost:8080`.

---

## Credits

Developed by [Nikolaos Poulopoulos](https://github.com/nikwilldoit).
