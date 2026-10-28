--User table draft
--Bcrypt hash er altid 60 tegn
CREATE TABLE `world`.`users` (
  `user_id` INT GENERATED ALWAYS AS () STORED,
  `user_name` VARCHAR(255) NOT NULL,
  `user_email` VARCHAR(255) NOT NULL,
  `user_password` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `user_email_UNIQUE` (`user_email` ASC) VISIBLE);

--wish table

CREATE TABLE `world`.`wish` (
  `wish_id` INT GENERATED ALWAYS AS () STORED,
  `wish` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`wish_id`));
