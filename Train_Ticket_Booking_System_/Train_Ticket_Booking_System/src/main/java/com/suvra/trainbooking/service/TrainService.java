package com.suvra.trainbooking.service;

import com.suvra.trainbooking.dao.TrainDao;
import com.suvra.trainbooking.exception.AppException;
import com.suvra.trainbooking.exception.NotFoundException;
import com.suvra.trainbooking.model.Seat;
import com.suvra.trainbooking.model.Train;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TrainService {

    private final TrainDao trainDao;

    public TrainService(TrainDao trainDao) {
        this.trainDao = trainDao;
    }

    public List<Train> search(String source, String destination, LocalDate date) {
        if (source == null || source.isBlank()
                || destination == null || destination.isBlank()
                || date == null) {
            throw new AppException("Source, destination and date are required.", 400);
        }

        try {
            return trainDao.search(source, destination, date);
        } catch (SQLException e) {
            throw new AppException("Database error while searching trains.", 500);
        }
    }

    public Train getTrain(long trainId) {
        try {
            return trainDao.findById(trainId)
                    .orElseThrow(() -> new NotFoundException("Train not found."));
        } catch (SQLException e) {
            throw new AppException("Database error while loading train.", 500);
        }
    }

    public List<Seat> getSeats(long trainId) {
        getTrain(trainId);

        try {
            return trainDao.findSeats(trainId);
        } catch (SQLException e) {
            throw new AppException("Database error while loading seats.", 500);
        }
    }
}
