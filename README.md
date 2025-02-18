# Book API

## Overview

This project is a secure Book API built with Spring Boot 3, Java 21, and MySQL, following enterprise-level best
practices. It provides a RESTful interface to manage book records with role-based access control:

- **ADMIN**: Can add, update, and delete books.
- **USER**: Can fetch book details.

The API features comprehensive input validation, custom exception handling, detailed Swagger documentation, and uses
environment variables for sensitive configuration (such as passwords).

## Technologies

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA (Hibernate)
- MySQL (for production) and H2 (for testing)
- Springdoc OpenAPI (Swagger)
- Lombok
- JUnit 5 and Mockito for testing

## Prerequisites

- Java 21
- Maven 3.8+
- MySQL Database

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/vishnurp3/bookapi.git
cd <repository-directory>
```

### Set Environment Variables

Set the following environment variables before running the application:

- `MYSQL_PASSWORD` – MySQL database password
- `ADMIN_PASSWORD` – Password for the default admin user
- `USER_PASSWORD` – Password for the default normal user

### Build the Project

```bash
mvn clean install
```

### Run the Application

```bash
mvn spring-boot:run
```

### Accessing the API

- Base URL: http://localhost:8080/api/books
- Swagger UI: http://localhost:8080/swagger-ui.html

In the Swagger UI, you can click the "Authorize" button and enter your credentials to test secured endpoints.