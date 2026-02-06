# Prices API

A Spring Boot REST API for querying applicable prices based on date, product, and brand, implementing hexagonal architecture principles.

## Overview

This application provides a pricing query service that determines the applicable price for a product at a specific date and time. When multiple price ranges overlap, the system selects the price with the highest priority value.

### Business Logic

The API handles price selection based on:
- **Date Range**: Prices are valid between a start and end date
- **Priority**: When multiple prices apply, the highest priority wins (higher numeric value)
- **Brand & Product**: Filters prices by specific brand and product identifiers

## Features

- ✅ RESTful API for price queries
- ✅ Priority-based price selection
- ✅ Hexagonal (Ports & Adapters) architecture
- ✅ Java 21 with virtual threads enabled
- ✅ H2 in-memory database
- ✅ Comprehensive integration tests
- ✅ Clean domain-driven design

## Tech Stack

- **Java 21** - Latest LTS with virtual threads support
- **Spring Boot 4.0.2** - Latest Spring Boot version
- **Spring Data JPA** - Data persistence layer
- **H2 Database** - In-memory database for development/testing
- **Lombok** - Reduces boilerplate in JPA entities
- **JUnit 5 + MockMvc** - Testing framework
- **Maven** - Build and dependency management

## Architecture

The project follows **Hexagonal Architecture** with three distinct layers:

```
┌─────────────────────────────────────────────────────┐
│                 Infrastructure Layer                 │
│  (REST Controllers, JPA Repositories, Adapters)     │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│                Application Layer                     │
│        (Use Cases, DTOs, Business Logic)            │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│                  Domain Layer                        │
│   (Pure Business Logic, Entities, Ports/Interfaces) │
└─────────────────────────────────────────────────────┘
```

### Layer Responsibilities

- **Domain**: Core business entities and rules (framework-independent)
- **Application**: Use case orchestration and DTO mapping
- **Infrastructure**: Technical implementations (REST, JPA, database)

## Prerequisites

- **Java 21** or higher
- **Maven** (or use the included Maven wrapper)

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd prices
```

### 2. Build the Project

```bash
./mvnw clean install
```

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

The application will start on **http://localhost:8080**

### 4. Run Tests

```bash
./mvnw test
```

## API Documentation

### Endpoint

```
GET /api/prices
```

### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `applicationDate` | DateTime | Yes | Date and time for price query (ISO 8601 format) |
| `productId` | Long | Yes | Product identifier |
| `brandId` | Long | Yes | Brand identifier |

### Response

```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 2,
  "startDate": "2020-06-14T15:00:00",
  "endDate": "2020-06-14T18:30:00",
  "finalPrice": 25.45
}
```

### Example Requests

**Test 1**: Price at 10:00 on June 14
```bash
curl "http://localhost:8080/api/prices?applicationDate=2020-06-14T10:00:00&productId=35455&brandId=1"
```
**Expected**: PriceList 1, Price 35.50 EUR

**Test 2**: Price at 16:00 on June 14
```bash
curl "http://localhost:8080/api/prices?applicationDate=2020-06-14T16:00:00&productId=35455&brandId=1"
```
**Expected**: PriceList 2, Price 25.45 EUR (higher priority)

**Test 3**: Price at 21:00 on June 14
```bash
curl "http://localhost:8080/api/prices?applicationDate=2020-06-14T21:00:00&productId=35455&brandId=1"
```
**Expected**: PriceList 1, Price 35.50 EUR

**Test 4**: Price at 10:00 on June 15
```bash
curl "http://localhost:8080/api/prices?applicationDate=2020-06-15T10:00:00&productId=35455&brandId=1"
```
**Expected**: PriceList 3, Price 30.50 EUR (higher priority)

**Test 5**: Price at 21:00 on June 16
```bash
curl "http://localhost:8080/api/prices?applicationDate=2020-06-16T21:00:00&productId=35455&brandId=1"
```
**Expected**: PriceList 4, Price 38.95 EUR (higher priority)

### Error Responses

**404 Not Found** - No price found for the given parameters
```json
{
  "status": 404,
  "message": "No price found for product 35455, brand 1 at 2019-01-01T10:00:00",
  "timestamp": "2026-02-06T09:43:21.697"
}
```

**400 Bad Request** - Missing or invalid parameters
```json
{
  "status": 400,
  "message": "All query parameters are required",
  "timestamp": "2026-02-06T09:43:21.697"
}
```

## Test Data

The application is initialized with the following test data:

| Brand | Product | Price List | Start Date | End Date | Priority | Price | Currency |
|-------|---------|------------|------------|----------|----------|-------|----------|
| 1 | 35455 | 1 | 2020-06-14 00:00:00 | 2020-12-31 23:59:59 | 0 | 35.50 | EUR |
| 1 | 35455 | 2 | 2020-06-14 15:00:00 | 2020-06-14 18:30:00 | 1 | 25.45 | EUR |
| 1 | 35455 | 3 | 2020-06-15 00:00:00 | 2020-06-15 11:00:00 | 1 | 30.50 | EUR |
| 1 | 35455 | 4 | 2020-06-15 16:00:00 | 2020-12-31 23:59:59 | 1 | 38.95 | EUR |

## H2 Database Console

Access the H2 console for debugging and data exploration:

**URL**: http://localhost:8080/h2-console

**Connection Settings**:
- JDBC URL: `jdbc:h2:mem:pricesdb`
- Username: `sa`
- Password: *(leave empty)*

## Project Structure

```
src/
├── main/
│   ├── java/es/dfalconr/prices/
│   │   ├── PricesApplication.java
│   │   ├── domain/
│   │   │   ├── model/Price.java
│   │   │   ├── port/PriceRepository.java
│   │   │   └── exception/PriceNotFoundException.java
│   │   ├── application/
│   │   │   ├── dto/
│   │   │   │   ├── PriceQuery.java
│   │   │   │   └── PriceResponse.java
│   │   │   └── service/GetApplicablePriceService.java
│   │   └── infrastructure/
│   │       ├── persistence/
│   │       │   ├── entity/PriceJpaEntity.java
│   │       │   ├── repository/PriceJpaRepository.java
│   │       │   └── adapter/PriceRepositoryAdapter.java
│   │       └── rest/
│   │           ├── controller/PriceController.java
│   │           └── exception/GlobalExceptionHandler.java
│   └── resources/
│       ├── application.yaml
│       ├── schema.sql
│       └── data.sql
└── test/
    └── java/es/dfalconr/prices/
        └── infrastructure/rest/controller/
            └── PriceControllerIntegrationTest.java
```

## Configuration

Key configuration in `application.yaml`:

```yaml
spring:
  threads:
    virtual:
      enabled: true  # Java 21 virtual threads for improved performance

  datasource:
    url: jdbc:h2:mem:pricesdb

  jpa:
    hibernate:
      ddl-auto: validate  # Uses schema.sql for DDL

  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql
      data-locations: classpath:data.sql
```

## Development Guidelines

- **Java Streams API**: Prefer functional programming for collections
- **Java Records**: Use for immutable DTOs and domain models
- **Descriptive Naming**: All variables and classes use clear English names
- **KISS Principle**: Keep code simple and readable
- **Testing**: Each layer tested independently

## Building for Production

```bash
# Package the application
./mvnw clean package

# Run the JAR
java -jar target/prices-0.0.1-SNAPSHOT.jar
```

## License

This project is part of a code challenge implementation.
