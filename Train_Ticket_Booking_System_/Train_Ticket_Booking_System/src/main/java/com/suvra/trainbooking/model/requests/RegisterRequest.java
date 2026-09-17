package com.suvra.trainbooking.model.requests;

public record RegisterRequest(
        String name,
        String email,
        String password
) {
}
