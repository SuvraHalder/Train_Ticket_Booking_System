# Train Ticket Booking System

A portfolio-oriented **Advanced Java** project built without Spring/Spring Boot.

## Stack

- Java 17
- Maven
- Jakarta Servlets
- Tomcat 10+
- JDBC
- MySQL 8+
- Jackson
- BCrypt
- HTTP session authentication

## Architecture

```text
Client / Postman
        |
        v
Servlet / Controller
        |
        v
Service Layer
        |
        v
DAO Layer
        |
        v
JDBC
        |
        v
MySQL
```

## Features

1. User registration with BCrypt password hashing
2. Login with HTTP session
3. Train search by source, destination and journey date
4. Train details
5. Available seat lookup
6. Seat booking
7. Transaction-safe booking
8. Booking history
9. Ticket cancellation
10. Seat release after cancellation

## Why this architecture?

Each layer has one job:

- **Servlet**: HTTP request/response
- **Service**: business rules
- **DAO**: SQL/JDBC
- **Model**: domain objects
- **Utility**: shared concerns such as DB connection and JSON

## Database

Run:

```sql
source sql/schema.sql;
source sql/sample_data.sql;
```

Create an environment file or set system environment variables:

```text
DB_URL=jdbc:mysql://localhost:3306/train_booking
DB_USER=root
DB_PASSWORD=your_password
```

The code uses these variables; no password is hard-coded.

## Run

1. Install JDK 17.
2. Install Maven.
3. Install MySQL 8+.
4. Execute `sql/schema.sql`.
5. Execute `sql/sample_data.sql`.
6. Set `DB_URL`, `DB_USER`, `DB_PASSWORD`.
7. Run:

```bash
mvn clean package
```

8. Deploy `target/train-booking.war` to Tomcat 10+.

## API

### Register

`POST /train-booking/api/auth/register`

```json
{
  "name": "Suvra",
  "email": "suvra@example.com",
  "password": "1234"
}
```

### Login

`POST /train-booking/api/auth/login`

```json
{
  "email": "suvra@example.com",
  "password": "1234"
}
```

Login stores `userId` in the HTTP session.

### Search trains

`GET /train-booking/api/trains/search?source=Delhi&destination=Kolkata&date=2026-08-20`

### Train details

`GET /train-booking/api/trains/1`

### Available seats

`GET /train-booking/api/trains/1/seats`

### Book a seat

`POST /train-booking/api/bookings`

```json
{
  "trainId": 1,
  "seatId": 3,
  "source": "Delhi",
  "destination": "Kolkata",
  "journeyDate": "2026-08-20"
}
```

### My bookings

`GET /train-booking/api/bookings`

### Cancel booking

`DELETE /train-booking/api/bookings/1`

## Important interview topics

- Why use DAO?
- Why PreparedStatement?
- Where is the transaction?
- Why BCrypt?
- Why use HTTP session?
- Why normalize train/station/seat tables?
- What happens when two users book the same seat?
- Why commit only after both booking and seat update succeed?
- Why rollback on exception?
- Difference between Statement and PreparedStatement
- Checked vs unchecked exceptions
- Servlet lifecycle
- Session vs request scope
- JDBC resource management
