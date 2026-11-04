package com.example.wishlist.service;

import com.example.wishlist.exception.EmailException;
import com.example.wishlist.exception.PasswordException;
import com.example.wishlist.exception.ReservationException;
import com.example.wishlist.exception.UserAlreadyExist;
import com.example.wishlist.exception.UserValidation;
import com.example.wishlist.exception.WishValidation;
import com.example.wishlist.exception.WishlistNotFound;
import com.example.wishlist.model.User;
import com.example.wishlist.model.Wish;
import com.example.wishlist.model.Wishlist;
import com.example.wishlist.repository.WishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WishlistServiceTest {

    private WishRepository repository;
    private PasswordEncoder encoder;
    private WishlistService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(WishRepository.class);
        encoder = new BCryptPasswordEncoder();
        service = new WishlistService(repository, encoder);
    }

    // ------------------------------------------------------------- brugere

    @Test
    void createHashesPasswordAndNormalizesEmail() {
        when(repository.insert(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        service.create(new User("  Mia ", "  Mia@Test.DK ", "Test1234"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repository).insert(captor.capture());
        User saved = captor.getValue();
        assertEquals("Mia", saved.getName());
        assertEquals("mia@test.dk", saved.getEmail());
        assertNotEquals("Test1234", saved.getPassword());
        assertTrue(encoder.matches("Test1234", saved.getPassword()));
    }

    @Test
    void createRejectsBlankName() {
        assertThrows(UserValidation.class, () -> service.create(new User(" ", "a@b.dk", "Test1234")));
        verify(repository, never()).insert(any(User.class));
    }

    @Test
    void createRejectsInvalidEmail() {
        assertThrows(EmailException.class, () -> service.create(new User("Mia", "ikke-en-email", "Test1234")));
    }

    @Test
    void createRejectsWeakPassword() {
        assertThrows(PasswordException.class, () -> service.create(new User("Mia", "a@b.dk", "kort1")));
        assertThrows(PasswordException.class, () -> service.create(new User("Mia", "a@b.dk", "kunbogstaver")));
        assertThrows(PasswordException.class, () -> service.create(new User("Mia", "a@b.dk", "12345678")));
    }

    @Test
    void createRejectsExistingEmail() {
        when(repository.existsByEmail("a@b.dk")).thenReturn(true);
        assertThrows(UserAlreadyExist.class, () -> service.create(new User("Mia", "A@B.dk", "Test1234")));
        verify(repository, never()).insert(any(User.class));
    }

    // ---------------------------------------------------------- ønskesedler

    @Test
    void createWishlistGeneratesShareToken() {
        when(repository.insertWishlist(any(Wishlist.class))).thenAnswer(inv -> inv.getArgument(0));

        Wishlist created = service.createWishlist(1, " Fødselsdag ", "");

        assertEquals("Fødselsdag", created.getName());
        assertNull(created.getDescription());
        assertEquals(32, created.getShareToken().length());
    }

    @Test
    void createWishlistRejectsBlankName() {
        assertThrows(WishValidation.class, () -> service.createWishlist(1, "  ", null));
    }

    @Test
    void ownWishlistOfAnotherUserLooksLikeItDoesNotExist() {
        Wishlist other = new Wishlist(2, "Andens", null, "token");
        other.setId(10);
        when(repository.findWishlistById(10)).thenReturn(other);

        assertThrows(WishlistNotFound.class, () -> service.getOwnWishlist(10, 1));
    }

    // -------------------------------------------------------------- ønsker

    private void givenOwnWishlist(int wishlistId, int userId) {
        Wishlist wishlist = new Wishlist(userId, "Liste", null, "token");
        wishlist.setId(wishlistId);
        when(repository.findWishlistById(wishlistId)).thenReturn(wishlist);
        when(repository.insertWish(any(Wish.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void addWishParsesDanishPriceAndAddsHttps() {
        givenOwnWishlist(10, 1);

        Wish wish = service.addWish(10, 1, "Kande", null, "349,50", "butik.dk/kande");

        assertEquals(new BigDecimal("349.50"), wish.getPrice());
        assertEquals("https://butik.dk/kande", wish.getLink());
    }

    @Test
    void addWishAllowsEmptyPriceAndLink() {
        givenOwnWishlist(10, 1);

        Wish wish = service.addWish(10, 1, "Kande", null, "", "");

        assertNull(wish.getPrice());
        assertNull(wish.getLink());
    }

    @Test
    void addWishRejectsBadPriceAndDangerousLink() {
        givenOwnWishlist(10, 1);

        assertThrows(WishValidation.class, () -> service.addWish(10, 1, "Kande", null, "abc", null));
        assertThrows(WishValidation.class, () -> service.addWish(10, 1, "Kande", null, "-5", null));
        assertThrows(WishValidation.class, () -> service.addWish(10, 1, "Kande", null, null, "javascript:alert(1)"));
    }

    // -------------------------------------------------------- reservationer

    @Test
    void ownerCannotReserveOwnWish() {
        Wishlist wishlist = new Wishlist(1, "Liste", null, "token");
        wishlist.setId(10);
        when(repository.findWishlistByToken("token")).thenReturn(wishlist);

        assertThrows(ReservationException.class, () -> service.reserve("token", 5, 1));
        verify(repository, never()).insertReservation(anyInt(), anyInt());
    }

    @Test
    void reserveRejectsWishFromAnotherList() {
        Wishlist wishlist = new Wishlist(1, "Liste", null, "token");
        wishlist.setId(10);
        when(repository.findWishlistByToken("token")).thenReturn(wishlist);

        Wish foreign = new Wish(99, "Fremmed", null, null, null);
        foreign.setId(5);
        when(repository.findWishById(5)).thenReturn(foreign);

        assertThrows(WishlistNotFound.class, () -> service.reserve("token", 5, 2));
    }

    @Test
    void reserveStoresReservationForOtherUser() {
        Wishlist wishlist = new Wishlist(1, "Liste", null, "token");
        wishlist.setId(10);
        when(repository.findWishlistByToken("token")).thenReturn(wishlist);

        Wish wish = new Wish(10, "Kande", null, null, null);
        wish.setId(5);
        when(repository.findWishById(5)).thenReturn(wish);

        service.reserve("token", 5, 2);

        verify(repository).insertReservation(5, 2);
    }
}
