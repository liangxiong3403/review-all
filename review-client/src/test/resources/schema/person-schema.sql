CREATE TABLE `person` (
	`id` INT ( 11 ) NOT NULL,
	`title` VARCHAR ( 8 ) DEFAULT NULL,
	`firstName` VARCHAR ( 8 ) DEFAULT NULL,
	`lastName` VARCHAR ( 8 ) DEFAULT NULL,
PRIMARY KEY ( `id` )
) ENGINE = INNODB DEFAULT CHARSET = utf8;