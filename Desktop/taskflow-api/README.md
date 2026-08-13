# TaskFlow API

A REST API for managing projects and tasks, built with **Spring Boot**, **Spring Data JPA (Hibernate)**, and **MySQL**. Built to demonstrate a properly layered backend service: controllers → services → repositories, DTOs separated from entities, centralized validation and error handling, pagination/filtering, and containerized deployment.

## Features

- Full CRUD for **Projects** and **Tasks** (one project has many tasks)
- Request validation with clear 400 responses (`@Valid` + `@NotBlank`/`@Size`)
- Centralized exception handling (`@RestControllerAdvice`) → consistent JSON error shape, correct HTTP status codes (404, 400, 500)
- Filtering tasks by `projectId` and/or `status`, with pagination and sorting
- DTOs for request/response — entities are never exposed directly over the API
- Dockerized: `Dockerfile` + `docker-compose.yml` (app + MySQL, one command)
- Runnable with **zero setup** via an in-memory H2 "demo" profile, or against real MySQL for production-style use

## Tech Stack

Java 17 · Spring Boot 3.2 · Spring Web · Spring Data JPA (Hibernate) · MySQL 8 · Bean Validation · Maven · Docker · JUnit 5

## Project Structure

```
src/main/java/com/shubham/taskflow/
├── model/         # JPA entities (Project, Task) + enums (TaskStatus, TaskPriority)
├── repository/     # Spring Data JPA repositories
├── service/        # Business logic, transactions
├── controller/      # REST endpoints
├── dto/            # Request/response objects
└── exception/       # Custom exceptions + global error handler
```

## Running it

### Option A — instantly, no MySQL required (H2 in-memory)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=demo
```

API is live at `http://localhost:8080`. Data resets on restart.

### Option B — with Docker Compose (app + real MySQL)

```bash
docker-compose up --build
```

### Option C — locally against your own MySQL

1. Create a MySQL user/password matching `src/main/resources/application.properties` (defaults: `root`/`root`).
2. `mvn spring-boot:run`

## API Reference

### Projects

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/projects` | Create a project |
| GET | `/api/projects` | List all projects |
| GET | `/api/projects/{id}` | Get a project by id |
| PUT | `/api/projects/{id}` | Update a project |
| DELETE | `/api/projects/{id}` | Delete a project (cascades to its tasks) |

### Tasks

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/tasks` | Create a task under a project |
| GET | `/api/tasks?projectId=&status=&page=&size=&sort=` | List tasks, filterable + paginated |
| GET | `/api/tasks/{id}` | Get a task by id |
| PUT | `/api/tasks/{id}` | Update a task |
| DELETE | `/api/tasks/{id}` | Delete a task |

## Sample requests

Create a project:

```bash
curl -X POST http://localhost:8080/api/projects \
  -H "Content-Type: application/json" \
  -d '{"name": "Portfolio Website", "description": "Personal site rebuild"}'
```

Create a task under project id 1:

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
        "title": "Set up CI pipeline",
        "description": "GitHub Actions build + test on push",
        "priority": "HIGH",
        "dueDate": "2026-09-01",
        "projectId": 1
      }'
```

Filter tasks — project 1, status IN_PROGRESS, page 0, 5 per page, sorted by due date:

```bash
curl "http://localhost:8080/api/tasks?projectId=1&status=IN_PROGRESS&page=0&size=5&sort=dueDate,asc"
```

Error response shape (e.g. requesting a task that doesn't exist):

```json
{
  "timestamp": "2026-08-13T10:15:30",
  "status": 404,
  "error": "Not Found",
  "message": "Task not found with id: 99",
  "details": []
}
```

## Notes on design decisions

- **DTOs instead of exposing entities directly** — keeps the API contract stable even if the database schema changes, and avoids leaking lazy-loaded associations.
- **`ddl-auto=update`** is used for local/demo convenience; a real production setup would use Flyway/Liquibase migrations instead.
- **Global exception handler** centralizes error formatting so every endpoint returns errors in the same shape, instead of each controller handling it ad hoc.
