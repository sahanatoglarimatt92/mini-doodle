# Mini Doodle

A full-stack meeting scheduling application inspired by **Doodle**, built using **Java 21**, **Spring Boot**, **React**, **PostgreSQL**, and **Docker**.

The application enables users to create calendars, manage available time slots, schedule meetings, add participants, and check availability through a modern web interface.

---

# ✨ Highlights

- Full-stack Meeting Scheduling Application
- Java 21 & Spring Boot 3
- React + Vite Frontend
- PostgreSQL Database
- Flyway Database Migration
- Docker Compose for Local Development
- RESTful APIs
- Swagger/OpenAPI Documentation
- Layered Architecture
- Global Exception Handling & Validation

---

# Features

## Backend

- User Management
- Calendar Management
- Time Slot Management
- Meeting Scheduling
- Add / Remove Participants
- Cancel Meetings
- Reschedule Meetings
- Availability Management
- REST APIs
- Validation
- Global Exception Handling
- Spring Boot Actuator
- Swagger API Documentation
- Flyway Database Migration

## Frontend

- React + Vite
- Responsive UI
- Calendar View
- Time Slot Management
- Meeting Scheduling
- Availability View
- REST API Integration

---

# Tech Stack

| Category | Technology |
|----------|------------|
| Backend | Java 21, Spring Boot 3 |
| Frontend | React, Vite |
| Database | PostgreSQL |
| ORM | Spring Data JPA |
| Database Migration | Flyway |
| Build Tool | Maven |
| DevOps | Docker, Docker Compose |
| Documentation | Swagger / OpenAPI |

---

# Project Structure

```text
mini-doodle
│
├── backend
│   └── scheduling-service
│
├── frontend
│
├── screenshots
│   ├── users.png
│   ├── time-slots.png
│   ├── meetings.png
│   └── availability.png
│
├── docker-compose.yml
│
├── README.md
│
└── .gitignore
```

---

# 🚀 Getting Started

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

## Start PostgreSQL

```bash
docker compose up -d
```

Stop services

```bash
docker compose down
```

---

## Run Backend

```bash
cd backend/scheduling-service

mvn spring-boot:run
```

Backend

```
http://localhost:8080
```

Swagger

```
http://localhost:8080/swagger-ui/index.html
```

Health Endpoint

```
http://localhost:8080/actuator/health
```

---

## Run Frontend

Open another terminal.

```bash
cd frontend

npm install

npm run dev
```

Frontend

```
http://localhost:5173
```

---

# 🏗️ Architecture

The application follows a layered architecture where the React frontend communicates with the Spring Boot backend through REST APIs. Business logic is handled in the service layer, while Spring Data JPA manages persistence to PostgreSQL.

```text
                           +----------------------+
                           |    React + Vite UI   |
                           +----------+-----------+
                                      |
                                      | HTTP / REST
                                      ▼
                     +----------------------------------+
                     | Spring Boot Scheduling Service   |
                     +----------------------------------+
                     | Controllers                      |
                     |----------------------------------|
                     | UserController                   |
                     | CalendarController               |
                     | TimeSlotController               |
                     | MeetingController                |
                     | AvailabilityController           |
                     +----------------------------------+
                                      |
                                      ▼
                     +----------------------------------+
                     | Services                         |
                     |----------------------------------|
                     | UserService                      |
                     | CalendarService                  |
                     | TimeSlotService                  |
                     | MeetingService                   |
                     | AvailabilityService              |
                     +----------------------------------+
                                      |
                                      ▼
                     +----------------------------------+
                     | Repositories                     |
                     |----------------------------------|
                     | Spring Data JPA                  |
                     +----------------------------------+
                                      |
                                      ▼
                           +----------------------+
                           |     PostgreSQL       |
                           +----------------------+

                Flyway ─────► Database Schema Migration

        Docker Compose ───► PostgreSQL Container
```

---

# 📌 Request Flow

The following diagram illustrates how a meeting is scheduled.

```text
User
 │
 ▼
React Frontend
 │
 │ POST /api/meetings
 ▼
MeetingController
 │
 ▼
MeetingService
 │
 ├────────► Validate Participants
 │
 ├────────► Check Slot Availability
 │
 ├────────► Update Time Slot Status
 │
 ▼
MeetingRepository
 │
 ▼
PostgreSQL
 │
 ▼
Meeting Response
 │
 ▼
React UI
```

---

# 📸 Application Screenshots

| Users | Time Slots |
|-------|------------|
| ![](screenshots/users.png) | ![](screenshots/time-slots.png) |

| Meetings | Availability |
|----------|--------------|
| ![](screenshots/meetings.png) | ![](screenshots/availability.png) |

---

# 📡 API Documentation

Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

---

# Sample API

### Create Meeting

**POST** `/api/meetings`

#### Request

```json
{
  "title": "Sprint Planning",
  "calendarId": 1,
  "participantIds": [2, 3],
  "timeSlotId": 5
}
```

#### Response

```json
{
  "id": 12,
  "title": "Sprint Planning",
  "status": "SCHEDULED"
}
```

---

# Database

- PostgreSQL
- Spring Data JPA
- Flyway Database Migration

---

# Docker

Start services

```bash
docker compose up -d
```

Stop services

```bash
docker compose down
```

---

# 🚀 Future Enhancements

- JWT Authentication & Authorization
- Email Notifications
- Meeting Reminders
- Recurring Meetings
- Time Zone Support
- Kafka Integration
- Kubernetes Deployment
- AWS Cloud Deployment
- CI/CD using GitHub Actions

---

# 👩‍💻 Author

**Sahana Thogaleri Mutt**

**GitHub**

https://github.com/sahanatoglarimatt92

**LinkedIn**

https://www.linkedin.com/in/sahana-thogaleri-mutt/

