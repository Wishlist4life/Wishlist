-- Opretter tabellerne

CREATE TABLE IF NOT EXISTS users
(
    user_id       INT          NOT NULL AUTO_INCREMENT,
    user_name     VARCHAR(255) NOT NULL,
    user_email    VARCHAR(255) NOT NULL,
    user_password VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id),
    CONSTRAINT uq_users_email UNIQUE (user_email)
);

CREATE TABLE IF NOT EXISTS wishlist
(
    wishlist_id INT          NOT NULL AUTO_INCREMENT,
    user_id     INT          NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    sharetoken  VARCHAR(64)  NOT NULL,
    PRIMARY KEY (wishlist_id),
    CONSTRAINT uq_wishlist_sharetoken UNIQUE (sharetoken),
    CONSTRAINT fk_wishlist_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS wish
(
    wish_id     INT          NOT NULL AUTO_INCREMENT,
    wishlist_id INT          NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    price       DECIMAL(10, 2),
    link        VARCHAR(255),
    PRIMARY KEY (wish_id),
    CONSTRAINT fk_wish_wishlist FOREIGN KEY (wishlist_id) REFERENCES wishlist (wishlist_id) ON DELETE CASCADE
);

-- Et ønske kan kun reserveres én gang
CREATE TABLE IF NOT EXISTS reservation
(
    reservation_id INT       NOT NULL AUTO_INCREMENT,
    wish_id        INT       NOT NULL,
    user_id        INT       NOT NULL,
    reserved_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (reservation_id),
    CONSTRAINT uq_reservation_wish UNIQUE (wish_id),
    CONSTRAINT fk_reservation_wish FOREIGN KEY (wish_id) REFERENCES wish (wish_id) ON DELETE CASCADE,
    CONSTRAINT fk_reservation_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);
