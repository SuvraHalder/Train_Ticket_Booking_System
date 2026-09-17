package com.suvra.trainbooking.web;

import com.suvra.trainbooking.dao.jdbc.JdbcBookingDao;
import com.suvra.trainbooking.dao.jdbc.JdbcTrainDao;
import com.suvra.trainbooking.dao.jdbc.JdbcUserDao;
import com.suvra.trainbooking.service.AuthService;
import com.suvra.trainbooking.service.BookingService;
import com.suvra.trainbooking.service.TrainService;

/**
 * Central service registry for this small application.
 * A DI framework is intentionally not used.
 */
public final class ApiServlet {

    private static final JdbcUserDao USER_DAO = new JdbcUserDao();
    private static final JdbcTrainDao TRAIN_DAO = new JdbcTrainDao();
    private static final JdbcBookingDao BOOKING_DAO = new JdbcBookingDao();

    private static final AuthService AUTH_SERVICE = new AuthService(USER_DAO);
    private static final TrainService TRAIN_SERVICE = new TrainService(TRAIN_DAO);
    private static final BookingService BOOKING_SERVICE =
            new BookingService(TRAIN_DAO, BOOKING_DAO);

    private ApiServlet() {
    }

    public static AuthService authService() {
        return AUTH_SERVICE;
    }

    public static TrainService trainService() {
        return TRAIN_SERVICE;
    }

    public static BookingService bookingService() {
        return BOOKING_SERVICE;
    }
}
