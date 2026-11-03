package com.example.wishlist.repository;

import com.example.wishlist.exception.DatabaseOperationException;
import com.example.wishlist.exception.UserAlreadyExist;
import com.example.wishlist.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class WishRepository {

    private final JdbcTemplate jdbcTemplate;

    // Password hentes bevidst ikke med ud ved opslag
    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setName(rs.getString("user_name"));
        user.setEmail(rs.getString("user_email"));
        return user;
    };

    public WishRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAll() {
        String sql = """
                SELECT user_id, user_name, user_email
                FROM users
                ORDER BY user_id
                """;
        return jdbcTemplate.query(sql, userRowMapper);
    }

    public User findById(int id) {
        String sql = """
                SELECT user_id, user_name, user_email
                FROM users
                WHERE user_id = ?
                LIMIT 1
                """;
        List<User> results = jdbcTemplate.query(sql, userRowMapper, id);
        if (results.isEmpty()) {
            return null;
        }
        return results.getFirst();
    }

    public User insert(User user) {
        String sql = """
                INSERT INTO users (user_name, user_email, user_password)
                VALUES (?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getName());
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getPassword());
                return ps;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            throw new UserAlreadyExist("User with this email already exists", e);
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Could not insert user", e);
        }

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to retrieve generated id.");
        }

        User saved = new User(user.getName(), user.getEmail(), null);
        saved.setId(key.intValue());
        return saved;
    }

}
