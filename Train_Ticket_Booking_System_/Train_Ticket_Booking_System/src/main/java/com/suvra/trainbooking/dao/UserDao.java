package com.suvra.trainbooking.dao;

import com.suvra.trainbooking.model.User;

import java.sql.SQLException;
import java.util.Optional;

public interface UserDao {

    boolean existsByEmail(String email) throws SQLException;

    long save(String name, String email, String passwordHash) throws SQLException;

    Optional<User> findByEmail(String email) throws SQLException;

    Optional<User> findById(long id) throws SQLException;
}
