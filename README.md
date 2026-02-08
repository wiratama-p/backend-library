# Backend Library

A REST API for managing books, built with Spring Boot 4.0.2 and Java 25.

## Prerequisites

### Run Locally

- Java 25 (JDK) - [Eclipse Temurin](https://adoptium.net/) or [Oracle JDK](https://jdk.java.net/25/)
- Maven 3.9+ (or use the included Maven Wrapper `./mvnw`)

### Run with Docker

- Docker 20.10+

## Run Locally

1. Clone the repository:

```bash
git clone https://github.com/wiratama-p/backend-library.git
cd backend-library
```

2. Build the project:

```bash
./mvnw clean package
```

3. Run the application:

```bash
./mvnw spring-boot:run
```

The application starts on `http://localhost:8080`.

### Run Tests

```bash
./mvnw test
```

## Run with Docker

1. Build the Docker image:

```bash
docker build -t backend-library .
```

2. Run the container:

```bash
docker run -p 8080:8080 backend-library
```

The application starts on `http://localhost:8080`.

## API Endpoints

| Method | Endpoint       | Description                          |
|--------|----------------|--------------------------------------|
| POST   | /books         | Create a new book                    |
| GET    | /books         | List all books (optional `?search=`) |
| GET    | /books/{id}    | Get a book by ID                     |
| PUT    | /books/{id}    | Update a book by ID                  |
| DELETE | /books/{id}    | Delete a book by ID                  |

### Request Body Example (POST / PUT)

```json
{
  "title": "Mommyclopedia: 78 Resep MPASI",
  "author": "dr. Meta Hanindita, Sp.A",
  "isbn": "9786028519939",
  "publicationYear": "2016",
  "genre": "Parenting",
  "description": "Kumpulan resep MPASI untuk bayi"
}
```

### Validation Rules

- `title`, `author`, `isbn`, `publicationYear`, `genre` are required (cannot be blank)
- `isbn` must be exactly 13 digits, starting with `978` or `979`, no dashes
- `isbn` must be unique across all books
- `description` is optional

## Tech Stack

- Java 25
- Spring Boot 4.0.2
- Spring Data JPA
- H2 Database (in-memory)
- Lombok
- Virtual Threads enabled

## Future Improvements
- Add proper persisting DB like PostgreSQL / MySQL etc so the data wont be gone after restart
- Create proper and unified API response (dont just pass raw data to the client)
- Add auditing fields back (createdAt, updatedAt) using JPA @PrePersist / @PreUpdate or Spring Data's @EnableJpaAuditing 
- Add pagination and sorting to the list endpoint using Spring Data's Pageable instead of returning all results
- Add Spring Boot Actuator for health checks and metrics (for production ready apps)

## AI Usage
- Create minimal boilerplate project.
- Ask the business domain e.g what is ISBN, proper format ISBN etc.
- Ask for technical explanation and usage of regex
- Create a repetitive Integration Test (which I create once / twice before)
- Find recommendation for the lightweight base image to Dockerize the backend apps
- Create prerequisites and how to run locally documents