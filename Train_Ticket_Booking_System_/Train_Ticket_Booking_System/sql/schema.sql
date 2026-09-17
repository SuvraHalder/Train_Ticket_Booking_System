CREATE DATABASE IF NOT EXISTS train_booking;
USE train_booking;

DROP TABLE IF EXISTS booking_seats;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS seats;
DROP TABLE IF EXISTS coaches;
DROP TABLE IF EXISTS train_stations;
DROP TABLE IF EXISTS stations;
DROP TABLE IF EXISTS trains;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE trains (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    train_number VARCHAR(20) NOT NULL UNIQUE,
    train_name VARCHAR(100) NOT NULL
);

CREATE TABLE stations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE train_stations (
    train_id BIGINT NOT NULL,
    station_id BIGINT NOT NULL,
    station_order INT NOT NULL,
    arrival_time TIME NULL,
    departure_time TIME NULL,
    PRIMARY KEY (train_id, station_id),
    UNIQUE KEY uq_train_station_order (train_id, station_order),
    CONSTRAINT fk_ts_train FOREIGN KEY (train_id) REFERENCES trains(id),
    CONSTRAINT fk_ts_station FOREIGN KEY (station_id) REFERENCES stations(id)
);

CREATE TABLE coaches (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    train_id BIGINT NOT NULL,
    coach_number VARCHAR(20) NOT NULL,
    coach_type VARCHAR(30) NOT NULL,
    UNIQUE KEY uq_train_coach (train_id, coach_number),
    CONSTRAINT fk_coach_train FOREIGN KEY (train_id) REFERENCES trains(id)
);

CREATE TABLE seats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    coach_id BIGINT NOT NULL,
    seat_number INT NOT NULL,
    status ENUM('AVAILABLE', 'BOOKED') NOT NULL DEFAULT 'AVAILABLE',
    UNIQUE KEY uq_coach_seat (coach_id, seat_number),
    CONSTRAINT fk_seat_coach FOREIGN KEY (coach_id) REFERENCES coaches(id)
);

CREATE TABLE bookings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pnr VARCHAR(20) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    train_id BIGINT NOT NULL,
    source_station_id BIGINT NOT NULL,
    destination_station_id BIGINT NOT NULL,
    journey_date DATE NOT NULL,
    status ENUM('CONFIRMED', 'CANCELLED') NOT NULL DEFAULT 'CONFIRMED',
    booked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_booking_train FOREIGN KEY (train_id) REFERENCES trains(id),
    CONSTRAINT fk_booking_source FOREIGN KEY (source_station_id) REFERENCES stations(id),
    CONSTRAINT fk_booking_destination FOREIGN KEY (destination_station_id) REFERENCES stations(id)
);

CREATE TABLE booking_seats (
    booking_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    PRIMARY KEY (booking_id, seat_id),
    CONSTRAINT fk_bs_booking FOREIGN KEY (booking_id) REFERENCES bookings(id),
    CONSTRAINT fk_bs_seat FOREIGN KEY (seat_id) REFERENCES seats(id)
);

CREATE INDEX idx_train_stations_station ON train_stations(station_id);
CREATE INDEX idx_bookings_user ON bookings(user_id);
CREATE INDEX idx_bookings_date ON bookings(journey_date);
