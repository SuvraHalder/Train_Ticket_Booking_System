package com.suvra.trainbooking.model.requests;

public record LoginRequest(
        String email,
        String password
) {
}
