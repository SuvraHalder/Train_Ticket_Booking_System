package com.suvra.trainbooking.model;

import java.time.LocalDate;

public record Booking(
        long id,
        String pnr,
        long userId,
        long trainId,
        String trainNumber,
        String trainName,
        String source,
        String destination,
        LocalDate journeyDate,
        String status
) {
}
