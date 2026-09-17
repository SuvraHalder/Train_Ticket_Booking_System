package com.suvra.trainbooking.service;

import com.suvra.trainbooking.dao.UserDao;
import com.suvra.trainbooking.exception.AppException;
import com.suvra.trainbooking.exception.ConflictException;
import com.suvra.trainbooking.exception.UnauthorizedException;
import com.suvra.trainbooking.model.User;
import com.suvra.trainbooking.util.PasswordUtil;

import java.sql.SQLException;

public class AuthService {

    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User register(String name, String email, String password) {

        if (name == null || name.isBlank()
                || email == null || email.isBlank()
                || password == null || password.length() < 6) {
            throw new AppException(
                    "Name, email and a password of at least 6 characters are required.",
                    400
            );
        }

        try {
            if (userDao.existsByEmail(email)) {
                throw new ConflictException("Email already registered.");
            }

            String hash = PasswordUtil.hash(password);
            long id = userDao.save(name.trim(), email.trim().toLowerCase(), hash);

            return new User(id, name.trim(), email.trim().toLowerCase(), hash);

        } catch (SQLException e) {
            throw new AppException("Database error while registering user.", 500);
        }
    }

    public User login(String email, String password) {

        try {
            User user = userDao.findByEmail(email.trim().toLowerCase())
                    .orElseThrow(() ->
                            new UnauthorizedException("Invalid email or password."));

            if (!PasswordUtil.matches(password, user.passwordHash())) {
                throw new UnauthorizedException("Invalid email or password.");
            }

            return user;

        } catch (SQLException e) {
            throw new AppException("Database error while logging in.", 500);
        }
    }
}
