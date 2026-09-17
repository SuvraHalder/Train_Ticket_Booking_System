package com.suvra.trainbooking.service;

import com.suvra.trainbooking.dao.BookingDao;
import com.suvra.trainbooking.dao.TrainDao;
import com.suvra.trainbooking.exception.AppException;
import com.suvra.trainbooking.exception.ConflictException;
import com.suvra.trainbooking.exception.NotFoundException;
import com.suvra.trainbooking.model.Booking;
import com.suvra.trainbooking.model.Seat;
import com.suvra.trainbooking.model.Train;
import com.suvra.trainbooking.util.Db;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * The most important business operation is booking.
 *
 * Booking and seat reservation happen in ONE DB transaction.
 * This prevents "booking created but seat not updated" partial states.
 */
public class BookingService {

    private final TrainDao trainDao;
    private final BookingDao bookingDao;

    public BookingService(TrainDao trainDao, BookingDao bookingDao) {
        this.trainDao = trainDao;
        this.bookingDao = bookingDao;
    }

    public Booking book(long userId,
                        long trainId,
                        long seatId,
                        String source,
                        String destination,
                        LocalDate journeyDate) {

        if (source == null || source.isBlank()
                || destination == null || destination.isBlank()
                || journeyDate == null) {
            throw new AppException("All booking fields are required.", 400);
        }

        try (Connection connection = Db.getConnection()) {

            connection.setAutoCommit(false);

            try {
                Train train = trainDao.findById(trainId)
                        .orElseThrow(() -> new NotFoundException("Train not found."));

                Seat seat = trainDao.findSeatForUpdate(connection, seatId, trainId)
                        .orElseThrow(() ->
                                new NotFoundException("Seat not found for this train."));

                if (!"AVAILABLE".equals(seat.status())) {
                    throw new ConflictException("Seat is already booked.");
                }

                // Ensure requested route is valid for this train.
                validateRoute(connection, trainId, source, destination);

                boolean seatBooked = trainDao.bookSeat(connection, seatId);

                if (!seatBooked) {
                    throw new ConflictException("Seat could not be booked.");
                }

                String pnr = generatePnr();

                long bookingId = bookingDao.createBooking(
                        connection,
                        pnr,
                        userId,
                        trainId,
                        findStationId(connection, source),
                        findStationId(connection, destination),
                        journeyDate
                );

                bookingDao.attachSeat(connection, bookingId, seatId);

                connection.commit();

                return new Booking(
                        bookingId,
                        pnr,
                        userId,
                        train.id(),
                        train.trainNumber(),
                        train.trainName(),
                        source,
                        destination,
                        journeyDate,
                        "CONFIRMED"
                );

            } catch (RuntimeException | SQLException e) {
                rollbackQuietly(connection);
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new AppException("Booking transaction failed.", 500);
        }
    }

    public List<Booking> getMyBookings(long userId) {
        try {
            return bookingDao.findByUser(userId);
        } catch (SQLException e) {
            throw new AppException("Database error while loading bookings.", 500);
        }
    }

    public void cancel(long userId, long bookingId) {

        try (Connection connection = Db.getConnection()) {

            connection.setAutoCommit(false);

            try {
                Booking booking = bookingDao.findConfirmedById(bookingId, userId)
                        .orElseThrow(() ->
                                new NotFoundException("Confirmed booking not found."));

                List<Long> seatIds = findSeatIds(connection, bookingId);

                for (Long seatId : seatIds) {
                    if (!trainDao.releaseSeat(connection, seatId)) {
                        throw new AppException(
                                "Could not release seat " + seatId,
                                500
                        );
                    }
                }

                bookingDao.cancelBooking(connection, bookingId);

                connection.commit();

            } catch (RuntimeException | SQLException e) {
                rollbackQuietly(connection);
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new AppException("Cancellation transaction failed.", 500);
        }
    }

    private String generatePnr() {
        return "PNR" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10)
                .toUpperCase();
    }

    private void validateRoute(Connection connection,
                               long trainId,
                               String source,
                               String destination) throws SQLException {

        String sql = """
                SELECT
                    (SELECT ts1.station_order
                     FROM train_stations ts1
                     JOIN stations s1 ON s1.id = ts1.station_id
                     WHERE ts1.train_id = ?
                       AND LOWER(s1.name) = LOWER(?)) AS source_order,
                    (SELECT ts2.station_order
                     FROM train_stations ts2
                     JOIN stations s2 ON s2.id = ts2.station_id
                     WHERE ts2.train_id = ?
                       AND LOWER(s2.name) = LOWER(?)) AS destination_order
                """;

        try (var ps = connection.prepareStatement(sql)) {
            ps.setLong(1, trainId);
            ps.setString(2, source.trim());
            ps.setLong(3, trainId);
            ps.setString(4, destination.trim());

            try (var rs = ps.executeQuery()) {
                if (!rs.next()
                        || rs.getObject("source_order") == null
                        || rs.getObject("destination_order") == null
                        || rs.getInt("source_order") >= rs.getInt("destination_order")) {
                    throw new AppException("Invalid source/destination for this train.", 400);
                }
            }
        }
    }

    private long findStationId(Connection connection, String name) throws SQLException {
        String sql = """
                SELECT id
                FROM stations
                WHERE LOWER(name) = LOWER(?)
                """;

        try (var ps = connection.prepareStatement(sql)) {
            ps.setString(1, name.trim());

            try (var rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFoundException("Station not found: " + name);
                }
                return rs.getLong("id");
            }
        }
    }

    private List<Long> findSeatIds(Connection connection, long bookingId) throws SQLException {
        String sql = """
                SELECT seat_id
                FROM booking_seats
                WHERE booking_id = ?
                """;

        List<Long> ids = new java.util.ArrayList<>();

        try (var ps = connection.prepareStatement(sql)) {
            ps.setLong(1, bookingId);

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getLong("seat_id"));
                }
            }
        }

        return ids;
    }
    private void rollbackQuietly(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException rollbackException) {
            rollbackException.printStackTrace();
        }
    }

}
