package com.suvra.trainbooking.dao.jdbc;

import com.suvra.trainbooking.dao.UserDao;
import com.suvra.trainbooking.model.User;
import com.suvra.trainbooking.util.Db;

import java.sql.*;
import java.util.Optional;

public class JdbcUserDao implements UserDao {

    @Override
    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ?";

        try (Connection connection = Db.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public long save(String name, String email, String passwordHash) throws SQLException {
        String sql = """
                INSERT INTO users(name, email, password_hash)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = Db.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, passwordHash);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Could not obtain generated user ID.");
                }
                return keys.getLong(1);
            }
        }
    }

    @Override
    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = """
                SELECT id, name, email, password_hash
                FROM users
                WHERE email = ?
                """;

        try (Connection connection = Db.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(new User(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password_hash")
                ));
            }
        }
    }

    @Override
    public Optional<User> findById(long id) throws SQLException {
        String sql = """
                SELECT id, name, email, password_hash
                FROM users
                WHERE id = ?
                """;

        try (Connection connection = Db.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(new User(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password_hash")
                ));
            }
        }
    }
}
