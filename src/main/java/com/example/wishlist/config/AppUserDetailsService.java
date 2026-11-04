package com.example.wishlist.config;

import com.example.wishlist.model.User;
import com.example.wishlist.service.WishlistService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Slaar brugeren op i databasen ved login (erstatter A's hardcodede InMemory-bruger "user"/"password"). */
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final WishlistService wishlistService;

    public AppUserDetailsService(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = wishlistService.findByEmailForLogin(email);
        if (user == null) {
            throw new UsernameNotFoundException("Ukendt email");
        }
        return new AppUserPrincipal(user.getId(), user.getName(), user.getEmail(), user.getPassword());
    }
}
