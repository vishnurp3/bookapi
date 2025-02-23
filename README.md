# Book API

## Overview

This project is a secure Book API built with Spring Boot 3, Java 21, and MySQL. It provides a RESTful interface to
manage book records with role-based access control:

- **ADMIN**: Can add, update, and delete books.
- **USER**: Can fetch book details.

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
- `JWT_SECRET` - JWT Secret (at least 32 characters long)

### Build the Project

```bash
mvn clean install
```

### Run the Application

```bash
mvn spring-boot:run
```

### Accessing the API

- Swagger UI: http://localhost:8080/swagger-ui.html

- Use the /api/auth/login endpoint to authenticate and receive an access token.
- Use the access token to authorize API requests in Swagger UI.