# **PitosPiesWebService**

**PitosPiesWebService** is a high-performance **RESTful Web Service API** built with **Spring Boot** for managing pie ordering, product catalog operations, delivery area mappings, and customer transactions. 

It is designed as a standalone, backend-only microservice that powers front-end clients, mobile apps, or single-page applications (SPAs).

> 🔗 **Related Project:** For the Web Application interface (Thymeleaf MVC frontend), visit [PitosPies Web Application](https://github.com/nikwilldoit/PitosPies).

---

## 🏗️ Technical Highlights & Capabilities

* **RESTful Architecture:** Clear separation of concerns with `@RestController` components serving JSON/XML payloads.
* **Full CRUD Operations:** Support for resources (`Pie`, `Order`, `Area`).
* **PUT vs. PATCH Precision:**
  * **`PUT`:** Handles complete entity replacements mapped to `@Valid @RequestBody`.
  * **`PATCH`:** Handles partial entity updates using dynamic `Map<String, Object>` payloads and programmatic Bean Validation (`BeanPropertyBindingResult` + `Validator`) to avoid unintended null overwrites.
* **Advanced Pagination & Sorting:** Built-in `PagingAndSortingRepository` support for multi-field sorting and client-defined page sizes (`page`, `size`, `sortBy`, `sortOrder`).
* **In-Memory Caching (Spring Cache):** Performance optimization using `ConcurrentMapCacheManager` with annotations (`@Cacheable`, `@CacheEvict`, `@Caching`).
* **Cache Introspection:** Dedicated endpoint to inspect live memory cache state.
* **Spring Data JDBC Persistence:** Clean SQL-based data access layer featuring derived queries, custom `@Query` annotations with SQL JOINs, and custom type converters.
* **Global Exception Handling:** `@ControllerAdvice` providing standardized REST error responses (`400 Bad Request`, `401 Unauthorized`, `404 Not Found`, `409 Conflict`, `500 Internal Error`).
* **Role-Based Security:** Spring Security integration with `HTTP Basic Auth` and BCrypt password encryption.

---

## 🛠️ Technology Stack

| Category | Technology |
| :--- | :--- |
| **Language** | Java 17+ / Java 25 |
| **Framework** | Spring Boot 3.x / 4.x (Spring REST, Spring Web MVC) |
| **Persistence** | Spring Data JDBC |
| **Database** | MySQL |
| **Security** | Spring Security (HTTP Basic Auth, BCrypt) |
| **Caching** | Spring Cache Abstraction (`ConcurrentMapCacheManager`) |
| **Validation** | Jakarta Bean Validation + Custom Constraints |
| **Serialization** | Jackson (`ObjectMapper`) |
| **Boilerplate Reduction** | Lombok |

---

## 🛡️ Security & Role Access

Authentication is enforced via **HTTP Basic Authentication**. Endpoints are secured based on user roles:

* **`ROLE_ADMIN`:** Full management of catalog resources (`/api/pie/**`).
* **`ROLE_USER`:** Access to delivery areas (`/api/area/**`).
* **Public:** General queries, order placements, and authentication routes.

---

## 📡 REST API Endpoint Reference

### 1. Delivery Areas (`/api/area`)

| Method | Endpoint | Authority | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/area/{id}` | Public | Fetch delivery area by ID |
| `GET` | `/api/areas` | `ROLE_USER` | Fetch all available delivery areas |
| `POST` | `/api/areas` | `ROLE_USER` | Create a new delivery area |
| `PUT` | `/api/area/{id}` | `ROLE_USER` | Fully update a delivery area |
| `PATCH` | `/api/area/{id}` | `ROLE_USER` | Partially update a delivery area |
| `DELETE` | `/api/area/{id}` | `ROLE_USER` | Delete a delivery area |

---

### 2. Pies Catalog (`/api/pie`)

| Method | Endpoint | Authority | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/pie/{id}` | Public | Get pie details (ingredients, awards) |
| `GET` | `/api/pies` | Public | List all pies |
| `GET` | `/api/pies?page=0&size=5` | Public | Paginated pie results |
| `GET` | `/api/pies?sortBy=price&sortOrder=asc` | Public | Sorted pie results |
| `GET` | `/api/pies?page=0&size=5&sortBy=name&sortOrder=desc` | Public | Paginated and sorted pie results |
| `POST` | `/api/pies` | `ROLE_ADMIN` | Add a new pie to catalog |
| `PUT` | `/api/pie/{id}` | `ROLE_ADMIN` | Fully replace an existing pie |
| `PATCH` | `/api/pie/{id}` | `ROLE_ADMIN` | Partially update pie attributes |
| `DELETE` | `/api/pie/{id}` | `ROLE_ADMIN` | Remove a pie from catalog |

---

### 3. Orders (`/api/order`)

| Method | Endpoint | Authority | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/order/{id}` | Public / Auth | Retrieve order details by ID |
| `GET` | `/api/orders` | Public / Auth | Fetch all orders |
| `GET` | `/api/orders?page=0&size=10&sortBy=stamp&sortOrder=desc` | Public / Auth | Paginated & sorted orders |
| `POST` | `/api/orders` | Public / Auth | Submit a new order |
| `PUT` | `/api/order/{id}` | Public / Auth | Full update of an existing order |
| `PATCH` | `/api/order/{id}` | Public / Auth | Partial update of an order |
| `DELETE` | `/api/order/{id}` | Public / Auth | Cancel/Delete an order record |

---

### 4. Cache Management

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/cache-data/{name}` | Inspect active memory entries for a specific cache region (e.g., `cache`, `pie-cache`) |

---

## ⚡ Error Handling Format

Errors are returned in a unified JSON structure managed by `@ControllerAdvice`:

```json
{
    "timestamp": "2026-10-10T18:00:00.000+00:00",
    "status": 400,
    "error": "Bad Request / Validation Error Message",
    "path": "uri=/api/orders"
}
```

---

## ⚙️ How to Run

1. **Database Setup:**
   Ensure MySQL is running and execute your schema initialization script.
   
2. **Properties Configuration:**
   Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/pitos?useSSL=false&serverTimezone=UTC
   spring.datasource.username=YOUR_USER
   spring.datasource.password=YOUR_PASSWORD
   ```

3. **Build & Run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

---

## 👤 Credits

Developed by **[Nikolaos Poulopoulos](https://github.com/nikwilldoit)**.
