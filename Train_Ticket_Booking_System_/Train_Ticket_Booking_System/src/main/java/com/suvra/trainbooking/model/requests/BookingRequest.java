package com.suvra.trainbooking.model.requests;

import java.time.LocalDate;

public record BookingRequest(
        long trainId,
        long seatId,
        String source,
        String destination,
        LocalDate journeyDate
) {
}
