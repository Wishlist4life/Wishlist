package com.example.demo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class WishRepository(){

    private final JdbcTemplate jdbcTemplate;
    //Rowmapper her eller klasse for sig?
    private final RowMapper<Profile> RowMapper = (rs, rowNum) ->
            new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email")
            );

    public WishRepository {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAll() {
        String sql = """
                SELECT id, name, email
                FROM users
                ORDER BY id
                """;
        return jdbcTemplate.query(sql, RowMapper);
    }

    public User findById(int id) {
        String sql = """
                SELECT id, name, email
                FROM profiles
                WHERE id = ?
                LIMIT 1
                """;
        List<User> results = jdbcTemplate.query(sql, profileRowMapper, id);
        if (results.isEmpty()) {
            return null;
        }
        return results.getFirst();
        }

    public User insert(User user) {
        String sql = """
                INSERT INTO users (name, email)
                VALUES (?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to retrieve generated id.");
        }

        return new User(key.intValue(), user.getName(), user.getEmail());
    }

}