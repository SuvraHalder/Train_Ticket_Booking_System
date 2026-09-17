# Build Order

This project is deliberately built in a fixed order.

## Phase 1 — Database

- Run schema.sql
- Run sample_data.sql
- Verify users/trains/stations/coaches/seats

## Phase 2 — JDBC

- Db
- DAO interfaces
- JDBC DAO implementations
- PreparedStatement

## Phase 3 — Authentication

- Register
- BCrypt
- Login
- HTTP session

## Phase 4 — Train

- Search
- Train details
- Seat list

## Phase 5 — Booking

- Validate route
- Lock seat with SELECT ... FOR UPDATE
- Mark seat BOOKED
- Insert booking
- Attach seat
- Commit

## Phase 6 — Cancellation

- Verify booking ownership
- Release seat
- Mark booking CANCELLED
- Commit

## Phase 7 — Interview polish

- Explain Servlet lifecycle
- Explain DAO
- Explain transactions
- Explain PreparedStatement
- Explain race condition prevention
- Explain session authentication
