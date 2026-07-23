# Mini Doodle

A full-stack meeting scheduling application inspired by Doodle, built with **Java 21**, **Spring Boot**, **React**, **PostgreSQL**, and **Docker**.

The application enables users to create calendars, manage availability, schedule meetings, and view free/busy time slots through a modern web interface.

---

## Features

### Backend

- User Management
- Calendar Management
- Time Slot Management
- Meeting Scheduling
- Add/Remove Participants
- Cancel Meetings
- Reschedule Meetings
- Check Availability
- REST APIs
- Global Exception Handling
- Validation
- Swagger API Documentation
- Spring Boot Actuator
- Flyway Database Migration

### Frontend

- React + Vite
- Responsive UI
- Calendar View
- Time Slot Management
- Meeting Scheduling
- Availability Display
- REST API Integration

---

## Tech Stack

### Backend

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- PostgreSQL
- Flyway
- Maven

### Frontend

- React
- Vite
- JavaScript
- HTML
- CSS

### DevOps

- Docker
- Docker Compose

### Documentation

- Swagger / OpenAPI

---

## Project Structure

```
mini-doodle
│
├── backend
│   └── scheduling-service
│
├── frontend
│
├── docker-compose.yml
│
└── README.md
```

---

# Getting Started

## Prerequisites

Install the following software:

- Java 21
- Maven 3.9+
- Node.js 20+
- npm
- Docker Desktop
- Git

---

## Clone Repository

```bash
git clone https://github.com/sahanatoglarimatt92/mini-doodle.git

cd mini-doodle
```

---

# Start PostgreSQL

Run PostgreSQL using Docker Compose.

```bash
docker compose up -d
```

Stop containers

```bash
docker compose down
```

---

# Run Backend

Navigate to the backend.

```bash
cd backend/scheduling-service
```

Run the application.

```bash
mvn spring-boot:run
```

Backend runs on

```
http://localhost:8080
```

Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

Health Endpoint

```
http://localhost:8080/actuator/health
```

---

# Run Frontend

Open another terminal.

Navigate to frontend.

```bash
cd frontend
```

Install dependencies.

```bash
npm install
```

Run application.

```bash
npm run dev
```

Frontend runs on

```
http://localhost:5173
```

---

# API Documentation

Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

---

# Database

The project uses PostgreSQL.

Database schema is managed using **Flyway** migrations.

---

# Docker

To start required services:

```bash
docker compose up -d
```

To stop services:

```bash
docker compose down
```

---

# Screenshots

> Add screenshots of the application here.

Example:

```
screenshots/

user.png

time slots.PNG

meetings.png

Availability.PNG
```

---

# Future Enhancements

- Authentication & Authorization
- Email Notifications
- Recurring Meetings
- Time Zone Support
- Meeting Invitations
- Dockerized Backend & Frontend
- Kubernetes Deployment
- CI/CD using GitHub Actions
- Kafka Integration
- AWS Deployment

---

# Author

**Sahana Thogaleri Mutt**

GitHub

https://github.com/sahanatoglarimatt92

LinkedIn

https://www.linkedin.com/in/sahana-thogaleri-mutt/

---

