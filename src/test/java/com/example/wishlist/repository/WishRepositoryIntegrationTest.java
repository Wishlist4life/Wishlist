package com.example.wishlist.repository;

import com.example.wishlist.model.User;
import com.example.wishlist.model.Wish;
import com.example.wishlist.model.Wishlist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Integrationstest mod H2 (MySQL-mode). schema.sql og data.sql koeres automatisk; hver test rulles tilbage. */
@SpringBootTest
@Transactional
class WishRepositoryIntegrationTest {

    @Autowired
    private WishRepository repository;

    @Test
    void findsSeededUserByEmailIncludingPasswordHash() {
        User user = repository.findByEmailWithPassword("mia@test.dk");

        assertNotNull(user);
        assertEquals("Mia", user.getName());
        assertNotNull(user.getPassword());
    }

    @Test
    void normalLookupDoesNotExposePassword() {
        User user = repository.findAll().getFirst();
        assertNull(user.getPassword());
    }

    @Test
    void insertUserThenFindIt() {
        User saved = repository.insert(new User("Ny", "ny@test.dk", "hash"));

        assertNotNull(saved.getId());
        assertTrue(repository.existsByEmail("ny@test.dk"));
        assertEquals("Ny", repository.findById(saved.getId()).getName());
        assertFalse(repository.existsByEmail("findes-ikke@test.dk"));
    }

    @Test
    void wishlistWishAndReservationRoundTrip() {
        User owner = repository.insert(new User("Ejer", "ejer@test.dk", "hash"));
        User guest = repository.insert(new User("Gæst", "gaest2@test.dk", "hash"));

        Wishlist wishlist = repository.insertWishlist(new Wishlist(owner.getId(), "Liste", "Beskrivelse", "tokentokentoken"));
        assertEquals(wishlist.getId(), repository.findWishlistByToken("tokentokentoken").getId());
        assertEquals(1, repository.findWishlistsByUser(owner.getId()).size());

        Wish wish = repository.insertWish(new Wish(wishlist.getId(), "Kande", null, new BigDecimal("349.00"), null));
        List<Wish> wishes = repository.findWishesByWishlist(wishlist.getId());
        assertEquals(1, wishes.size());
        assertFalse(wishes.getFirst().isReserved());

        repository.insertReservation(wish.getId(), guest.getId());
        assertEquals(guest.getId(), repository.findWishById(wish.getId()).getReservedBy());

        // Et ønske kan kun reserveres én gang
        assertThrows(DuplicateKeyException.class, () -> repository.insertReservation(wish.getId(), owner.getId()));

        assertEquals(1, repository.deleteReservation(wish.getId(), guest.getId()));
        assertFalse(repository.findWishById(wish.getId()).isReserved());
    }

    @Test
    void deleteWishlistRemovesWishesAndOnlyForOwner() {
        User owner = repository.insert(new User("Ejer", "ejer2@test.dk", "hash"));
        Wishlist wishlist = repository.insertWishlist(new Wishlist(owner.getId(), "Liste", null, "tokendelete"));
        repository.insertWish(new Wish(wishlist.getId(), "Kande", null, null, null));

        // En anden bruger kan ikke slette listen, og ønskerne er stadig der
        assertEquals(0, repository.deleteWishlist(wishlist.getId(), owner.getId() + 1000));
        assertEquals(1, repository.findWishesByWishlist(wishlist.getId()).size());

        assertEquals(1, repository.deleteWishlist(wishlist.getId(), owner.getId()));
        assertNull(repository.findWishlistById(wishlist.getId()));
        assertTrue(repository.findWishesByWishlist(wishlist.getId()).isEmpty());
    }
}
