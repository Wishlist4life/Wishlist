package com.example.wishlist.service;

import com.example.wishlist.exception.EmailException;
import com.example.wishlist.exception.PasswordException;
import com.example.wishlist.exception.ReservationException;
import com.example.wishlist.exception.UserAlreadyExist;
import com.example.wishlist.exception.UserNotFound;
import com.example.wishlist.exception.UserValidation;
import com.example.wishlist.exception.WishValidation;
import com.example.wishlist.exception.WishlistNotFound;
import com.example.wishlist.model.User;
import com.example.wishlist.model.Wish;
import com.example.wishlist.model.Wishlist;
import com.example.wishlist.repository.WishRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class WishlistService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final int MAX_TEXT = 255;
    private static final BigDecimal MAX_PRICE = new BigDecimal("99999999.99");

    private final WishRepository wr;
    private final PasswordEncoder pe;

    public WishlistService(WishRepository wr, PasswordEncoder pe) {
        this.wr = wr;
        this.pe = pe;
    }

    // ------------------------------------------------------------------ users

    public List<User> findAll() {
        return wr.findAll();
    }

    public User findById(int id) {
        User user = wr.findById(id);
        if (user == null) {
            throw new UserNotFound(id);
        }
        return user;
    }

    /** Til login. Returnerer null, hvis emailen ikke findes. */
    public User findByEmailForLogin(String email) {
        if (email == null) {
            return null;
        }
        return wr.findByEmailWithPassword(normalizeEmail(email));
    }

    public User create(User user) {
        validateUser(user);
        validatePassword(user.getPassword());

        String email = normalizeEmail(user.getEmail());
        if (wr.existsByEmail(email)) {
            throw new UserAlreadyExist("Der findes allerede en bruger med denne email.");
        }

        User toSave = new User(user.getName().trim(), email, pe.encode(user.getPassword()));
        return wr.insert(toSave);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserValidation("Bruger mangler.");
        }

        String name = user.getName();
        if (name == null || name.isBlank()) {
            throw new UserValidation("Navn skal udfyldes.");
        }
        if (name.trim().length() > 100) {
            throw new UserValidation("Navn må højst være 100 tegn.");
        }

        validateEmail(user.getEmail());
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new EmailException("Email skal udfyldes.");
        }
        String normalized = normalizeEmail(email);
        if (normalized.length() > 100) {
            throw new EmailException("Email må højst være 100 tegn.");
        }
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new EmailException("Email er ugyldig. Brug fx navn@eksempel.dk");
        }
    }

    // 8-72 tegn med mindst ét bogstav og ét tal
    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new PasswordException("Kodeord skal udfyldes.");
        }
        if (password.length() < 8 || password.length() > 72) {
            throw new PasswordException("Kodeord skal være mellem 8 og 72 tegn.");
        }
        boolean hasLetter = password.chars().anyMatch(Character::isLetter);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        if (!hasLetter || !hasDigit) {
            throw new PasswordException("Kodeord skal indeholde mindst ét bogstav og ét tal.");
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    // -------------------------------------------------------------- wishlists

    public List<Wishlist> findWishlists(int userId) {
        return wr.findWishlistsByUser(userId);
    }

    public Wishlist createWishlist(int userId, String name, String description) {
        String cleanName = requireText(name, "Navn på ønskesedlen");
        String cleanDescription = optionalText(description, "Beskrivelse");

        Wishlist wishlist = new Wishlist(userId, cleanName, cleanDescription,
                UUID.randomUUID().toString().replace("-", ""));
        return wr.insertWishlist(wishlist);
    }

    /** Henter egen seddel. Andres giver "findes ikke". */
    public Wishlist getOwnWishlist(int wishlistId, int userId) {
        Wishlist wishlist = wr.findWishlistById(wishlistId);
        if (wishlist == null || wishlist.getUserId() != userId) {
            throw new WishlistNotFound(wishlistId);
        }
        return wishlist;
    }

    public Wishlist getSharedWishlist(String shareToken) {
        Wishlist wishlist = (shareToken == null) ? null : wr.findWishlistByToken(shareToken);
        if (wishlist == null) {
            throw new WishlistNotFound("Ønskesedlen findes ikke.");
        }
        return wishlist;
    }

    public String getOwnerName(Wishlist wishlist) {
        return findById(wishlist.getUserId()).getName();
    }

    @Transactional
    public void deleteWishlist(int wishlistId, int userId) {
        getOwnWishlist(wishlistId, userId);
        wr.deleteWishlist(wishlistId, userId);
    }

    // ----------------------------------------------------------------- wishes

    public List<Wish> findWishes(int wishlistId) {
        return wr.findWishesByWishlist(wishlistId);
    }

    public Wish addWish(int wishlistId, int userId, String name, String description, String price, String link) {
        getOwnWishlist(wishlistId, userId);

        Wish wish = new Wish(
                wishlistId,
                requireText(name, "Navn på ønsket"),
                optionalText(description, "Beskrivelse"),
                parsePrice(price),
                normalizeLink(link));
        return wr.insertWish(wish);
    }

    @Transactional
    public void deleteWish(int wishlistId, int wishId, int userId) {
        getOwnWishlist(wishlistId, userId);
        if (wr.deleteWish(wishId, wishlistId) == 0) {
            throw new WishlistNotFound("Ønsket findes ikke.");
        }
    }

    // ----------------------------------------------------------- reservations

    @Transactional
    public void reserve(String shareToken, int wishId, int userId) {
        Wishlist wishlist = getSharedWishlist(shareToken);
        if (wishlist.getUserId() == userId) {
            throw new ReservationException("Du kan ikke reservere dine egne ønsker.");
        }

        Wish wish = wr.findWishById(wishId);
        if (wish == null || !wish.getWishlistId().equals(wishlist.getId())) {
            throw new WishlistNotFound("Ønsket findes ikke.");
        }

        try {
            wr.insertReservation(wishId, userId);
        } catch (DuplicateKeyException e) {
            throw new ReservationException("Ønsket er allerede reserveret.");
        }
    }

    @Transactional
    public void unreserve(String shareToken, int wishId, int userId) {
        Wishlist wishlist = getSharedWishlist(shareToken);
        Wish wish = wr.findWishById(wishId);
        if (wish == null || !wish.getWishlistId().equals(wishlist.getId())) {
            throw new WishlistNotFound("Ønsket findes ikke.");
        }
        wr.deleteReservation(wishId, userId);
    }

    // ---------------------------------------------------------------- helpers

    private String requireText(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new WishValidation(label + " skal udfyldes.");
        }
        String trimmed = value.trim();
        if (trimmed.length() > MAX_TEXT) {
            throw new WishValidation(label + " må højst være " + MAX_TEXT + " tegn.");
        }
        return trimmed;
    }

    private String optionalText(String value, String label) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() > MAX_TEXT) {
            throw new WishValidation(label + " må højst være " + MAX_TEXT + " tegn.");
        }
        return trimmed;
    }

    /** Tager fx 349, 349.50 og 349,50. Tom pris er ok. */
    private BigDecimal parsePrice(String price) {
        if (price == null || price.isBlank()) {
            return null;
        }
        try {
            BigDecimal value = new BigDecimal(price.trim().replace(',', '.'));
            if (value.signum() < 0 || value.compareTo(MAX_PRICE) > 0) {
                throw new WishValidation("Prisen skal være mellem 0 og 99.999.999,99.");
            }
            return value.setScale(2, java.math.RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            throw new WishValidation("Prisen er ugyldig. Brug fx 349 eller 349,50.");
        }
    }

    /** Kun http/https-links. Mangler https://, tilføjes det. */
    private String normalizeLink(String link) {
        if (link == null || link.isBlank()) {
            return null;
        }
        String candidate = link.trim();
        if (!candidate.contains("://")) {
            candidate = "https://" + candidate;
        }
        if (candidate.length() > MAX_TEXT) {
            throw new WishValidation("Linket må højst være " + MAX_TEXT + " tegn.");
        }
        try {
            URI uri = URI.create(candidate);
            String scheme = uri.getScheme();
            boolean httpScheme = "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
            if (!httpScheme || uri.getHost() == null) {
                throw new WishValidation("Linket er ugyldigt. Brug fx https://butik.dk/produkt");
            }
            return candidate;
        } catch (IllegalArgumentException e) {
            throw new WishValidation("Linket er ugyldigt. Brug fx https://butik.dk/produkt");
        }
    }
}
