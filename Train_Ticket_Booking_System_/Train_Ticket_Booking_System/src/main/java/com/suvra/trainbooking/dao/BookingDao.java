package com.suvra.trainbooking.dao;

import com.suvra.trainbooking.model.Booking;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingDao {

    long createBooking(
            Connection connection,
            String pnr,
            long userId,
            long trainId,
            long sourceStationId,
            long destinationStationId,
            LocalDate journeyDate
    ) throws SQLException;

    void attachSeat(Connection connection, long bookingId, long seatId)
            throws SQLException;

    List<Booking> findByUser(long userId) throws SQLException;

    Optional<Booking> findConfirmedById(long bookingId, long userId) throws SQLException;

    void cancelBooking(Connection connection, long bookingId) throws SQLException;
}
