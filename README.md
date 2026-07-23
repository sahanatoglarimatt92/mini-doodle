# Mini Doodle – Meeting Scheduling Platform

A full-stack meeting scheduling application inspired by Doodle.

The application allows users to manage calendars and time slots, schedule meetings, add participants, and find common availability between multiple users.

## Features

### User Management

- Create users
- View all users
- View a user by ID
- Update user information
- Delete users

### Calendar Management

- Create a personal calendar for a user
- View calendars
- Update calendar information
- Delete calendars

### Time Slot Management

- Create available time slots
- Update existing time slots
- Delete free time slots
- Prevent overlapping time slots
- Prevent modification or deletion of booked time slots
- Track slot status as `FREE` or `BOOKED`

### Meeting Scheduling

- Convert a free time slot into a meeting
- Add meeting title and description
- Assign an organizer
- Add or remove participants
- Reschedule meetings
- Cancel meetings
- Prevent double booking
- Validate participant availability

### Availability Search

- Search availability for one or more users
- View busy intervals for each user
- Calculate common free time slots
- Merge overlapping or adjacent busy intervals
- Clip meeting intervals to the requested search range

## Technology Stack

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- PostgreSQL
- Flyway
- Maven
- JUnit 5
- Mockito
- Spring Boot Actuator
- Swagger / OpenAPI

### Frontend

- React
- Vite
- Axios
- JavaScript
- CSS

### Infrastructure

- Docker
- Docker Compose
- PostgreSQL container

## Project Architecture

The backend follows a feature-oriented layered architecture.

```text
src/main/java/com/sahana/doodle/scheduling
├── controller
├── dto
├── exception
├── mapper
├── model
├── repository
└── service