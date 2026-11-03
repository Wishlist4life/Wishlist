--User table draft
--Bcrypt hash er altid 60 tegn
CREATE TABLE `world`.`users`
(
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `user_name` VARCHAR(255) NOT NULL,
  `user_email` VARCHAR(255) NOT NULL,
  `user_password` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `user_email_UNIQUE` (`user_email` ASC) VISIBLE
);

--wish table
CREATE TABLE `world`.`wish`
(
  `wish_id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT,
  `name` VARCHAR(255) NOT NULL,
  `description` VARCHAR(255),
  `price` DECIMAL(10,2),
  `link` VARCHAR(255),
  PRIMARY KEY (`wish_id`),
  FOREIGN KEY (`user_id`) REFERENCES `world`.`users` (`user_id`)
);

--Wishlist
CREATE TABLE `world`.`wishlist`
(
  `wishlist_id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT,
  `name` VARCHAR(255) NOT NULL,
  `description` VARCHAR(255),
  `sharetoken` VARCHAR(255) NOT NULL UNIQUE,
  PRIMARY KEY (`wishlist_id`),
  FOREIGN KEY (`user_id`) REFERENCES `world`.`users` (`user_id`)
);
