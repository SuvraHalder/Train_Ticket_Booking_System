package com.suvra.trainbooking.model;

public record Seat(
        long id,
        long coachId,
        String coachNumber,
        int seatNumber,
        String status
) {
}
