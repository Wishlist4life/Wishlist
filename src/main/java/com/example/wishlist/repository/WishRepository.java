package com.example.wishlist.repository;

import com.example.wishlist.exception.DatabaseOperationException;
import com.example.wishlist.exception.UserAlreadyExist;
import com.example.wishlist.model.User;
import com.example.wishlist.model.Wish;
import com.example.wishlist.model.Wishlist;
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

    // Password hentes bevidst ikke med ud ved almindelige opslag
    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setName(rs.getString("user_name"));
        user.setEmail(rs.getString("user_email"));
        return user;
    };

    // Kun til login: her skal password-hashen med
    private final RowMapper<User> userWithPasswordRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setName(rs.getString("user_name"));
        user.setEmail(rs.getString("user_email"));
        user.setPassword(rs.getString("user_password"));
        return user;
    };

    private final RowMapper<Wishlist> wishlistRowMapper = (rs, rowNum) -> {
        Wishlist wishlist = new Wishlist();
        wishlist.setId(rs.getInt("wishlist_id"));
        wishlist.setUserId(rs.getInt("user_id"));
        wishlist.setName(rs.getString("name"));
        wishlist.setDescription(rs.getString("description"));
        wishlist.setShareToken(rs.getString("sharetoken"));
        return wishlist;
    };

    private final RowMapper<Wish> wishRowMapper = (rs, rowNum) -> {
        Wish wish = new Wish();
        wish.setId(rs.getInt("wish_id"));
        wish.setWishlistId(rs.getInt("wishlist_id"));
        wish.setName(rs.getString("name"));
        wish.setDescription(rs.getString("description"));
        wish.setPrice(rs.getBigDecimal("price"));
        wish.setLink(rs.getString("link"));
        int reservedBy = rs.getInt("reserved_by");
        wish.setReservedBy(rs.wasNull() ? null : reservedBy);
        return wish;
    };

    public WishRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ------------------------------------------------------------------ users

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

    /** Bruges af login. Returnerer brugeren inkl. password-hash, eller null. */
    public User findByEmailWithPassword(String email) {
        String sql = """
                SELECT user_id, user_name, user_email, user_password
                FROM users
                WHERE user_email = ?
                LIMIT 1
                """;
        List<User> results = jdbcTemplate.query(sql, userWithPasswordRowMapper, email);
        if (results.isEmpty()) {
            return null;
        }
        return results.getFirst();
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
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

    // -------------------------------------------------------------- wishlists

    public List<Wishlist> findWishlistsByUser(int userId) {
        String sql = """
                SELECT wishlist_id, user_id, name, description, sharetoken
                FROM wishlist
                WHERE user_id = ?
                ORDER BY wishlist_id DESC
                """;
        return jdbcTemplate.query(sql, wishlistRowMapper, userId);
    }

    public Wishlist findWishlistById(int wishlistId) {
        String sql = """
                SELECT wishlist_id, user_id, name, description, sharetoken
                FROM wishlist
                WHERE wishlist_id = ?
                LIMIT 1
                """;
        List<Wishlist> results = jdbcTemplate.query(sql, wishlistRowMapper, wishlistId);
        return results.isEmpty() ? null : results.getFirst();
    }

    public Wishlist findWishlistByToken(String shareToken) {
        String sql = """
                SELECT wishlist_id, user_id, name, description, sharetoken
                FROM wishlist
                WHERE sharetoken = ?
                LIMIT 1
                """;
        List<Wishlist> results = jdbcTemplate.query(sql, wishlistRowMapper, shareToken);
        return results.isEmpty() ? null : results.getFirst();
    }

    public Wishlist insertWishlist(Wishlist wishlist) {
        String sql = """
                INSERT INTO wishlist (user_id, name, description, sharetoken)
                VALUES (?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, wishlist.getUserId());
            ps.setString(2, wishlist.getName());
            ps.setString(3, wishlist.getDescription());
            ps.setString(4, wishlist.getShareToken());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to retrieve generated id.");
        }
        wishlist.setId(key.intValue());
        return wishlist;
    }

    /** Sletter kun hvis oenskesedlen tilhoerer brugeren. Returnerer antal slettede raekker (0 eller 1). */
    public int deleteWishlist(int wishlistId, int userId) {
        // Ejerskabet tjekkes i alle tre sletninger, saa en andens liste aldrig roeres
        jdbcTemplate.update("""
                DELETE FROM reservation
                WHERE wish_id IN (
                    SELECT w.wish_id FROM wish w
                    JOIN wishlist l ON l.wishlist_id = w.wishlist_id
                    WHERE l.wishlist_id = ? AND l.user_id = ?)
                """, wishlistId, userId);
        jdbcTemplate.update("""
                DELETE FROM wish
                WHERE wishlist_id IN (SELECT wishlist_id FROM wishlist WHERE wishlist_id = ? AND user_id = ?)
                """, wishlistId, userId);
        return jdbcTemplate.update("DELETE FROM wishlist WHERE wishlist_id = ? AND user_id = ?", wishlistId, userId);
    }

    // ----------------------------------------------------------------- wishes

    public List<Wish> findWishesByWishlist(int wishlistId) {
        String sql = """
                SELECT w.wish_id, w.wishlist_id, w.name, w.description, w.price, w.link,
                       r.user_id AS reserved_by
                FROM wish w
                LEFT JOIN reservation r ON r.wish_id = w.wish_id
                WHERE w.wishlist_id = ?
                ORDER BY w.wish_id
                """;
        return jdbcTemplate.query(sql, wishRowMapper, wishlistId);
    }

    public Wish findWishById(int wishId) {
        String sql = """
                SELECT w.wish_id, w.wishlist_id, w.name, w.description, w.price, w.link,
                       r.user_id AS reserved_by
                FROM wish w
                LEFT JOIN reservation r ON r.wish_id = w.wish_id
                WHERE w.wish_id = ?
                LIMIT 1
                """;
        List<Wish> results = jdbcTemplate.query(sql, wishRowMapper, wishId);
        return results.isEmpty() ? null : results.getFirst();
    }

    public Wish insertWish(Wish wish) {
        String sql = """
                INSERT INTO wish (wishlist_id, name, description, price, link)
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, wish.getWishlistId());
            ps.setString(2, wish.getName());
            ps.setString(3, wish.getDescription());
            ps.setBigDecimal(4, wish.getPrice());
            ps.setString(5, wish.getLink());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to retrieve generated id.");
        }
        wish.setId(key.intValue());
        return wish;
    }

    /** Sletter oensket (og dets reservation) hvis det ligger paa den angivne oenskeseddel. */
    public int deleteWish(int wishId, int wishlistId) {
        jdbcTemplate.update("""
                DELETE FROM reservation
                WHERE wish_id IN (SELECT wish_id FROM wish WHERE wish_id = ? AND wishlist_id = ?)
                """, wishId, wishlistId);
        return jdbcTemplate.update("DELETE FROM wish WHERE wish_id = ? AND wishlist_id = ?", wishId, wishlistId);
    }

    // ----------------------------------------------------------- reservations

    /** Kaster DuplicateKeyException hvis oensket allerede er reserveret (wish_id er UNIQUE). */
    public void insertReservation(int wishId, int userId) {
        jdbcTemplate.update("INSERT INTO reservation (wish_id, user_id) VALUES (?, ?)", wishId, userId);
    }

    /** Fjerner kun brugerens egen reservation. Returnerer antal slettede raekker (0 eller 1). */
    public int deleteReservation(int wishId, int userId) {
        return jdbcTemplate.update("DELETE FROM reservation WHERE wish_id = ? AND user_id = ?", wishId, userId);
    }
}
