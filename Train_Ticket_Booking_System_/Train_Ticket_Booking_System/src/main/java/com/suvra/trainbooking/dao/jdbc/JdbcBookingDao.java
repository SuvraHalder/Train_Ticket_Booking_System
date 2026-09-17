package com.suvra.trainbooking.dao.jdbc;

import com.suvra.trainbooking.dao.BookingDao;
import com.suvra.trainbooking.model.Booking;
import com.suvra.trainbooking.util.Db;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcBookingDao implements BookingDao {

    @Override
    public long createBooking(Connection connection,
                              String pnr,
                              long userId,
                              long trainId,
                              long sourceStationId,
                              long destinationStationId,
                              LocalDate journeyDate) throws SQLException {

        String sql = """
                INSERT INTO bookings(
                    pnr, user_id, train_id,
                    source_station_id, destination_station_id, journey_date
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, pnr);
            ps.setLong(2, userId);
            ps.setLong(3, trainId);
            ps.setLong(4, sourceStationId);
            ps.setLong(5, destinationStationId);
            ps.setDate(6, Date.valueOf(journeyDate));
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Could not obtain booking ID.");
                }
                return keys.getLong(1);
            }
        }
    }

    @Override
    public void attachSeat(Connection connection, long bookingId, long seatId)
            throws SQLException {

        String sql = """
                INSERT INTO booking_seats(booking_id, seat_id)
                VALUES (?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, bookingId);
            ps.setLong(2, seatId);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Booking> findByUser(long userId) throws SQLException {

        String sql = """
                SELECT
                    b.id,
                    b.pnr,
                    b.user_id,
                    b.train_id,
                    t.train_number,
                    t.train_name,
                    ss.name AS source_name,
                    ds.name AS destination_name,
                    b.journey_date,
                    b.status
                FROM bookings b
                JOIN trains t ON t.id = b.train_id
                JOIN stations ss ON ss.id = b.source_station_id
                JOIN stations ds ON ds.id = b.destination_station_id
                WHERE b.user_id = ?
                ORDER BY b.journey_date DESC, b.id DESC
                """;

        List<Booking> results = new ArrayList<>();

        try (Connection connection = Db.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(map(rs));
                }
            }
        }

        return results;
    }

    @Override
    public Optional<Booking> findConfirmedById(long bookingId, long userId)
            throws SQLException {

        String sql = """
                SELECT
                    b.id,
                    b.pnr,
                    b.user_id,
                    b.train_id,
                    t.train_number,
                    t.train_name,
                    ss.name AS source_name,
                    ds.name AS destination_name,
                    b.journey_date,
                    b.status
                FROM bookings b
                JOIN trains t ON t.id = b.train_id
                JOIN stations ss ON ss.id = b.source_station_id
                JOIN stations ds ON ds.id = b.destination_station_id
                WHERE b.id = ?
                  AND b.user_id = ?
                  AND b.status = 'CONFIRMED'
                """;

        try (Connection connection = Db.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, bookingId);
            ps.setLong(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(map(rs));
            }
        }
    }

    @Override
    public void cancelBooking(Connection connection, long bookingId)
            throws SQLException {

        String sql = """
                UPDATE bookings
                SET status = 'CANCELLED'
                WHERE id = ?
                  AND status = 'CONFIRMED'
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, bookingId);
            ps.executeUpdate();
        }
    }

    private Booking map(ResultSet rs) throws SQLException {
        return new Booking(
                rs.getLong("id"),
                rs.getString("pnr"),
                rs.getLong("user_id"),
                rs.getLong("train_id"),
                rs.getString("train_number"),
                rs.getString("train_name"),
                rs.getString("source_name"),
                rs.getString("destination_name"),
                rs.getDate("journey_date").toLocalDate(),
                rs.getString("status")
        );
    }
}
