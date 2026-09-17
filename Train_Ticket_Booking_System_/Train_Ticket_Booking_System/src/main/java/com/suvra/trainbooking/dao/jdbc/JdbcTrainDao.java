package com.suvra.trainbooking.dao.jdbc;

import com.suvra.trainbooking.dao.TrainDao;
import com.suvra.trainbooking.model.Seat;
import com.suvra.trainbooking.model.Train;
import com.suvra.trainbooking.util.Db;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcTrainDao implements TrainDao {

    @Override
    public List<Train> search(String source, String destination, LocalDate date)
            throws SQLException {

        String sql = """
                SELECT t.id, t.train_number, t.train_name
                FROM trains t
                JOIN train_stations ts_source ON ts_source.train_id = t.id
                JOIN stations source_station ON source_station.id = ts_source.station_id
                JOIN train_stations ts_destination ON ts_destination.train_id = t.id
                JOIN stations destination_station ON destination_station.id = ts_destination.station_id
                WHERE LOWER(source_station.name) = LOWER(?)
                  AND LOWER(destination_station.name) = LOWER(?)
                  AND ts_source.station_order < ts_destination.station_order
                ORDER BY t.train_number
                """;

        List<Train> results = new ArrayList<>();

        try (Connection connection = Db.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, source.trim());
            ps.setString(2, destination.trim());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new Train(
                            rs.getLong("id"),
                            rs.getString("train_number"),
                            rs.getString("train_name")
                    ));
                }
            }
        }

        return results;
    }

    @Override
    public Optional<Train> findById(long trainId) throws SQLException {
        String sql = """
                SELECT id, train_number, train_name
                FROM trains
                WHERE id = ?
                """;

        try (Connection connection = Db.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, trainId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(new Train(
                        rs.getLong("id"),
                        rs.getString("train_number"),
                        rs.getString("train_name")
                ));
            }
        }
    }

    @Override
    public List<Seat> findSeats(long trainId) throws SQLException {
        String sql = """
                SELECT s.id, s.coach_id, c.coach_number, s.seat_number, s.status
                FROM seats s
                JOIN coaches c ON c.id = s.coach_id
                WHERE c.train_id = ?
                ORDER BY c.coach_number, s.seat_number
                """;

        List<Seat> results = new ArrayList<>();

        try (Connection connection = Db.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, trainId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new Seat(
                            rs.getLong("id"),
                            rs.getLong("coach_id"),
                            rs.getString("coach_number"),
                            rs.getInt("seat_number"),
                            rs.getString("status")
                    ));
                }
            }
        }

        return results;
    }

    @Override
    public Optional<Seat> findSeatForUpdate(Connection connection,
                                             long seatId,
                                             long trainId) throws SQLException {

        String sql = """
                SELECT s.id, s.coach_id, c.coach_number, s.seat_number, s.status
                FROM seats s
                JOIN coaches c ON c.id = s.coach_id
                WHERE s.id = ?
                  AND c.train_id = ?
                FOR UPDATE
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, seatId);
            ps.setLong(2, trainId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(new Seat(
                        rs.getLong("id"),
                        rs.getLong("coach_id"),
                        rs.getString("coach_number"),
                        rs.getInt("seat_number"),
                        rs.getString("status")
                ));
            }
        }
    }

    @Override
    public boolean bookSeat(Connection connection, long seatId) throws SQLException {
        String sql = """
                UPDATE seats
                SET status = 'BOOKED'
                WHERE id = ?
                  AND status = 'AVAILABLE'
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, seatId);
            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public boolean releaseSeat(Connection connection, long seatId) throws SQLException {
        String sql = """
                UPDATE seats
                SET status = 'AVAILABLE'
                WHERE id = ?
                  AND status = 'BOOKED'
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, seatId);
            return ps.executeUpdate() == 1;
        }
    }
}
