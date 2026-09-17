package com.suvra.trainbooking.web;

import com.suvra.trainbooking.model.requests.BookingRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/bookings/*")
public class BookingServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        try {
            long userId = requireUser(request);

            json(response, 200,
                    ApiServlet.bookingService().getMyBookings(userId));

        } catch (Exception ex) {
            error(response, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {

        try {
            long userId = requireUser(request);

            BookingRequest body =
                    mapper.readValue(request.getReader(), BookingRequest.class);

            var booking = ApiServlet.bookingService().book(
                    userId,
                    body.trainId(),
                    body.seatId(),
                    body.source(),
                    body.destination(),
                    body.journeyDate()
            );

            json(response, 201, booking);

        } catch (Exception ex) {
            error(response, ex);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request,
                            HttpServletResponse response) throws IOException {

        try {
            long userId = requireUser(request);

            String path = request.getPathInfo();

            if (path == null || !path.matches("/\\d+")) {
                response.sendError(400, "Booking ID is required.");
                return;
            }

            long bookingId = Long.parseLong(path.substring(1));

            ApiServlet.bookingService().cancel(userId, bookingId);

            json(response, 200, Map.of(
                    "message", "Booking cancelled successfully."
            ));

        } catch (Exception ex) {
            error(response, ex);
        }
    }

    private long requireUser(HttpServletRequest request) {
        Object userId = request.getSession(false) == null
                ? null
                : request.getSession(false).getAttribute("userId");

        if (userId == null) {
            throw new com.suvra.trainbooking.exception.UnauthorizedException(
                    "Please login first."
            );
        }

        return (Long) userId;
    }
}
