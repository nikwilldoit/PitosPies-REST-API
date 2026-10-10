# PitosPies-REST-API

PitosPies-REST-API is the dedicated Spring Boot RESTful Web Service backend for the [PitosPies](https://github.com/nikwilldoit/PitosPies) ecosystem. It handles catalog management, order processing, delivery area administration, user authentication, and system caching through structured JSON and XML endpoints.

This repository serves as the standalone backend API service for the PitosPies system. The corresponding Spring Boot Web application (Thymeleaf MVC) is available at [PitosPies Repository](https://github.com/nikwilldoit/PitosPies).

---

## Architectural Highlights

* Controller Layer: Dedicated REST Controllers (`@RestController`) producing JSON/XML responses.
* Service Layer: Encapsulated business logic, relational mappings, and programmatic Bean Validation (`BeanPropertyBindingResult` + `Validator`).
* Data Access Layer: Spring Data JDBC persistence layer using derived queries, custom `@Query` annotations, and custom converters (`BooleanToIntegerConverter`, `IntegerToBooleanConverter`).
* Caching Engine: In-memory concurrent map caching (`@Cacheable`, `@CacheEvict`, `@Caching`) with a dedicated REST endpoint for real-time cache inspection.
* Centralized Exception Handling: `@ControllerAdvice` providing standardized JSON error structures.

---

## Technology Stack

| Category | Technology |
| --- | --- |
| Language & Runtime | Java 17+ / Java 25 |
| Framework | Spring Boot 3.x / 4.x (Spring REST) |
| Persistence | Spring Data JDBC |
| Database | MySQL |
| Security | Spring Security (BCrypt, HTTP Basic Auth) |
| Caching | Spring Cache (`ConcurrentMapCacheManager`) |
| Validation | Jakarta Bean Validation |
| Utilities | Lombok, Jackson (`ObjectMapper`) |

---

## Data Model & Domain Entities

* `User` (`user`): System credentials, personal details, verification status, and transient roles.
* `Role` (`role`): User roles (`USER`, `ADMIN`).
* `Pie` (`pie`): Product catalog item mapped with `@MappedCollection` for awards and `@Transient` ingredient strings.
* `Ingredient` (`ingredient`): Ingredients associated with pies through `pie_ingredient`.
* `Award` (`award`): Pie awards mapped via `@MappedCollection(idColumn = "pie_id")`.
* `Area` (`area`): Supported delivery regions.
* `Order` (`order`): Customer orders containing transient `area` and `orderItems`.
* `OrderItem` (`order_item`): Order line items associating pie names and quantities.

---

## Security & Authentication

Security is configured via Spring Security using HTTP Basic Authentication (`httpBasic`).

### Access Rules

* `ROLE_ADMIN`: Access to pie catalog management (`/api/pie/**`).
* `ROLE_USER`: Access to delivery area endpoints (`/api/area/**`).
* Public (`permitAll()`): Public product catalog browsing, registration, and general routes.

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

## API Endpoints

### 1. Delivery Areas (`/api/area`)

| Method | Path | Auth / Role | Description |
| --- | --- | --- | --- |
| GET | `/api/area/{id}` | Public | Get delivery area by ID |
| GET | `/api/areas` | `ROLE_USER` | List all delivery areas |
| POST | `/api/areas` | `ROLE_USER` | Create new delivery area |
| PUT | `/api/area/{id}` | `ROLE_USER` | Full update of delivery area |
| PATCH | `/api/area/{id}` | `ROLE_USER` | Partial update of delivery area |
| DELETE | `/api/area/{id}` | `ROLE_USER` | Delete delivery area |

---

### 2. Pie Catalog (`/api/pie`)

| Method | Path | Auth / Role | Description |
| --- | --- | --- | --- |
| GET | `/api/pie/{id}` | Public | Get pie by ID |
| GET | `/api/pies` | Public | List all pies |
| GET | `/api/pies?page=0&size=5` | Public | Paginated pie list |
| GET | `/api/pies?sortBy=price&sortOrder=asc` | Public | Sorted pie list |
| GET | `/api/pies?page=0&size=5&sortBy=name&sortOrder=desc` | Public | Paginated and sorted pie list |
| POST | `/api/pies` | `ROLE_ADMIN` | Create new pie |
| PUT | `/api/pie/{id}` | `ROLE_ADMIN` | Full update of pie |
| PATCH | `/api/pie/{id}` | `ROLE_ADMIN` | Partial update of pie |
| DELETE | `/api/pie/{id}` | `ROLE_ADMIN` | Delete pie |

---

### 3. Orders (`/api/order`)

| Method | Path | Auth / Role | Description |
| --- | --- | --- | --- |
| GET | `/api/order/{id}` | Public / Auth | Get order by ID |
| GET | `/api/orders` | Public / Auth | List all orders |
| GET | `/api/orders?page=0&size=10&sortBy=stamp&sortOrder=desc` | Public / Auth | Paginated and sorted orders |
| POST | `/api/orders` | Public / Auth | Submit new order |
| PUT | `/api/order/{id}` | Public / Auth | Full update of order |
| PATCH | `/api/order/{id}` | Public / Auth | Partial update of order |
| DELETE | `/api/order/{id}` | Public / Auth | Delete order |

---

### 4. System Cache (`/api/cache-data`)

| Method | Path | Auth / Role | Description |
| --- | --- | --- | --- |
| GET | `/api/cache-data/{name}` | Public / Auth | Inspect active cache entries for a region |

---

## Update Mechanics (PUT vs PATCH)

* PUT Requests: Full entity replacement. Expects a complete valid JSON object bound to `@Valid @RequestBody`. Missing fields trigger validation or database constraints.
* PATCH Requests: Partial entity updates. Payload is accepted as `Map<String, Object>`, enabling explicit distinction between omitted properties and null values. Updated fields are merged into the target entity and validated programmatically via `BeanPropertyBindingResult` before persistence.

---

## Global Error Handling

All uncaught REST exceptions are handled globally by `GlobalExceptionHandler`.

### JSON Error Response

```json
{
    "timestamp": "2026-10-10T18:00:00.000+00:00",
    "status": 400,
    "error": "Validation error message",
    "path": "uri=/api/orders"
}

```

### HTTP Response Status Codes

* `200 OK`: Successful retrieval, update, or deletion.
* `201 Created`: Successful resource creation.
* `204 No Content`: Successful execution without return payload.
* `400 Bad Request`: Validation failure or bad payload format.
* `401 Unauthorized`: Missing or invalid Basic Auth credentials.
* `404 Not Found`: Target resource does not exist.
* `409 Conflict`: Attempted creation of duplicate resource.
* `500 Internal Server Error`: Unhandled application exception.

---

## Setup & Running

### 1. Database Setup

Create MySQL database: find the sql schema [here](https://github.com/nikwilldoit/PitosPies/blob/main/src/main/resources/db/DB_schema.sql)

```sql
CREATE DATABASE pitos;

```

Configure `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/pitos?useSSL=false&serverTimezone=UTC
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD

```

### 2. Build & Execute

```bash
mvn clean install
mvn spring-boot:run

```

The REST API service runs locally at `http://localhost:8080/api`.

---

## Related Repositories

* [PitosPies Web Application](https://github.com/nikwilldoit/PitosPies): Spring Boot Thymeleaf Web Application.

---

## Credits

Developed by [Nikolaos Poulopoulos](https://github.com/nikwilldoit).
