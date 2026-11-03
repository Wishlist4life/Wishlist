--User table draft

CREATE TABLE IF NOT EXISTS users (
    users_id LONG NOT NULL AUTO_INCREMENT,
    users_name VARCHAR(255) NOT NULL,
    users_email VARCHAR(255) NOT NULL,
    users_password VARCHAR(255) NOT NULL,
    PRIMARY KEY (users_id),
    CONSTRAINT uq_users_email UNIQUE (users_email)
    );

CREATE TABLE IF NOT EXISTS wishlists (
    wishlist_id LONG NOT NULL AUTO_INCREMENT,
    users_id LONG NOT NULL,
    wishlist_title VARCHAR(255) NOT NULL,
    share_url VARCHAR(255) NOT NULL,
    PRIMARY KEY (wishlist_id),
    CONSTRAINT uq_users_id FOREIGN KEY (users_id) REFERENCES users (users_id)
    );

CREATE TABLE IF NOT EXISTS wishes (
    wishes_id LONG NOT NULL AUTO_INCREMENT,
    wishlist_id LONG NOT NULL,
    wishes_title VARCHAR(255) NOT NULL,
    wishes_description VARCHAR(255) NOT NULL,
    wishes_img_path VARCHAR(255) NOT NULL,
    PRIMARY KEY (wishes_id),
    CONSTRAINT uq_wishlist_id FOREIGN KEY (wishlist_id) REFERENCES wishlists (wishlist_id)
    );

-- meget af dette er overkill, skulle vi simplificere?
--User kan kun reservere hvis de har en bruger, er det en dårlig ide?

    CREATE TABLE IF NOT EXISTS reservations (
      reservations_id LONG NOT NULL AUTO_INCREMENT,
      wishes_id LONG NOT NULL,
      user_id LONG NOT NULL,
      reservations_reserved TIMESTAMP NOT NULL,
      PRIMARY KEY (reservations_id),
      CONSTRAINT uq_wishes_id FOREIGN KEY (wishes_id) REFERENCES wishes (wishes_id)
      CONSTRAINT uq_user_id FOREIGN KEY (user_id) REFERENCES users (users_id)
      );
