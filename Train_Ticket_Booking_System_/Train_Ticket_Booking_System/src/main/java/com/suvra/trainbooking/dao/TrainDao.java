package com.suvra.trainbooking.dao;

import com.suvra.trainbooking.model.Seat;
import com.suvra.trainbooking.model.Train;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainDao {

    List<Train> search(String source, String destination, LocalDate date) throws SQLException;

    Optional<Train> findById(long trainId) throws SQLException;

    List<Seat> findSeats(long trainId) throws SQLException;

    Optional<Seat> findSeatForUpdate(Connection connection, long seatId, long trainId)
            throws SQLException;

    boolean bookSeat(Connection connection, long seatId) throws SQLException;

    boolean releaseSeat(Connection connection, long seatId) throws SQLException;
}
