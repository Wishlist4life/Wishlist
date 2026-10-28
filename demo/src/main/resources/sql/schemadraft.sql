--User table draft

CREATE TABLE `world`.`user` (
  `user_id` INT GENERATED ALWAYS AS () STORED,
  `user_name` VARCHAR(45) NOT NULL,
  `user_email` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `user_email_UNIQUE` (`user_email` ASC) VISIBLE);

--wish table

CREATE TABLE `world`.`wish` (
  `wish_id` INT GENERATED ALWAYS AS () STORED,
  `wish` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`wish_id`));
