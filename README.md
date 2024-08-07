# Sticky Notes API

A Spring Boot backend application for managing sticky notes. This API supports all CRUD operations, user authentication, and note filtering. It is built using Spring Web, Lombok, Spring Data MongoDB, and Spring Security.

## Features

- **CRUD Operations**: Create, Read, Update, and Delete sticky notes.
- **User Authentication**: Register and log in with JWT-based authentication.
- **Secure Endpoints**: All endpoints are secure except for `/api/auth/**`.
- **Filtering**: Filter notes based on various criteria.

## Technologies Used

- **Spring Boot**
- **Spring Web**
- **Lombok**
- **Spring Data MongoDB**
- **Spring Security**

## API Endpoints

### Authentication

- **POST /api/auth/register**: Register a new user.
- **POST /api/auth/login**: Log in an existing user.

### Notes

- **POST /api/notes/add**: Create a new note.
- **PUT /api/notes/update/{id}**: Update an existing note.
- **DELETE /api/notes/delete/{id}**: Delete a note.
- **GET /api/notes/all**: Retrieve all notes for the logged-in user.
- **GET /api/notes/filter**: Filter notes based on criteria.

## Installation

### Prerequisites

- Java 21 or higher
- MongoDB (running instance)

### Setup

1. Clone the repository:
   ```bash
   git clone <repository-url>
