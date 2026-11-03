package org.example.wishlist2.service;

import org.example.wishlist2.Model.User;
import org.example.wishlist2.exception.*;
import org.example.wishlist2.repository.WishRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class WishlistService {
 private final WishRepository wr;
 private final PasswordEncoder pe;

    public WishlistService(WishRepository wr, PasswordEncoder pe) {
        this.wr = wr;
        this.pe = pe;
    }

    public List<User> findAll() {
        try {
            return wr.findAll();
        } catch (DataAccessException ex) {
            throw new DatabaseExceptionHandler("Failed to retrieve Wishlist.", ex);
        }
    }

    public User create(User user) {
        //validate user
        validateUser(user);
        //hash password
        String hashedpassword = pe.encode(user.getPassword());
        user.setPassword(hashedpassword);

        try {
            return wr.insert(user);
        } catch (DataIntegrityViolationException ex) {
            throw new UserAlreadyExist("Email already exists.");
        } catch (DataAccessException ex) {
            throw new DatabaseExceptionHandler("Failed to create profile.", ex);
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserValidation("User is mandatory.");
        }

        String name = user.getName();
        if (name == null || name.isBlank()) {
            throw new UserValidation("Name is required.");
        }
        if (name.length() > 100) {
            throw new UserValidation("Name must be at most 100 characters.");
        }

        validateEmail(user.getEmail());
    }


    private void validateEmail(String email){
        if (email == null || email.isBlank()) {
            throw new EmailException("Email cannot be blank.");
        }
        if (email.length() > 100) {
            throw new EmailException("Email too long, max 100 characters.");
        }
        if (wr.existsByEmail(email)) {
            throw new UserAlreadyExist("This email is already registered.");
        }

        final Pattern EMAIL_PATTERN =
                Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new EmailException("Email format is invalid. Classic email format excepted ie: John@Doe.com");
        }
    }

    private void validatePassword(String password){
        if (password == null || password.isBlank()) {
            throw new PasswordException("Password cannot be blank.");
        }
        if (password.length() > 20 || password.length() < 8) {
            throw new PasswordException("Password most be at least 8 characters and not longer than 20.");
        }
        final Pattern PASSWORD_PATTERN =
                Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d)(?=.*[@#$%^&+=]).{8,20}$");

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new PasswordException("""
                    Have eight characters and less than 20
                    Include a capital letter
                    Use at least one lowercase letter
                    Consists of at least one digit
                    Need to have one special symbol (i.e., @, #, $, %, etc.)
                    Doesn’t contain space, tab, etc.
                    """);
        }
    }// Overkill for vores test ^^ måske bare disable?

}
