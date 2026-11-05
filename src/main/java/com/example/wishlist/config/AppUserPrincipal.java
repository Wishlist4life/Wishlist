package com.example.wishlist.config;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

/** Den loggede bruger inkl. bruger-id og navn. */
public class AppUserPrincipal extends User {

    private static final long serialVersionUID = 1L;

    private final int id;
    private final String displayName;

    public AppUserPrincipal(int id, String displayName, String email, String passwordHash) {
        super(email, passwordHash, AuthorityUtils.createAuthorityList("ROLE_USER"));
        this.id = id;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }
}
