-- USUÁRIOS: criar schema para credenciais de login de
-- administradores, gerentes, clientes, etc.

-- db.users definition

CREATE TABLE users (
	id BIGINT auto_increment NOT NULL,
	name varchar(100) NOT NULL,
	email varchar(100) UNIQUE NOT NULL,
	password varchar(100) NOT NULL,
	`role` varchar(100) NOT NULL,
	enabled boolean NOT NULL,
	CONSTRAINT users_pk PRIMARY KEY (id)
);

-- create table `db`.`users`(
--	username VARCHAR(50) not null primary key,
--	password VARCHAR(500) not null,
--	enabled boolean not null
-- );

create table `authorities` (
    id BIGINT auto_increment PRIMARY KEY,
	ref_user BIGINT not null,
	authority VARCHAR(50) not null,
	constraint fk_authorities_users foreign key(ref_user) references users(id)
);

-- usuário 'felipechoi', senha 'lipeadmin' (convertida com hash blowfish para evitar ataques de linha de BD)
INSERT INTO users
(name, email, password, `role`, enabled)
VALUES('felipe choi', 'felipe.choi@bluevelvet.not', '$2a$10$Ha3IM.uQ8tTTqTZ8KDFgn.XBMF5uWjjwG1Gh7Xdsj6kwORZrwMuCa', 'Administrator', 1);

INSERT INTO users
(name, email, password, `role`, enabled)
VALUES('fellipe rey', 'rey@nowhere.not', '$2a$12$hV1VunyHo57JJJ/kE16j8.TnIKSgphLj.CT7BVfsq/eNRZfcspYAS', 'Administrator', 1);

INSERT INTO users
(name, email, password, `role`, enabled)
VALUES('Charnal brandos', 'gordoseditor@gordosburger.br', '$2a$12$H4PBkG55IuIGR1i5x8xhDuIUNjo1TNF0kd6Z9ExdRiTo40jrNh6c6', 'Editor', 1);

INSERT INTO authorities
(ref_user, authority)
VALUES(1, 'ROLE_Administrator');
INSERT INTO authorities
(ref_user, authority)
VALUES(2, 'ROLE_Administrator');
INSERT INTO authorities
(ref_user, authority)
VALUES(3, 'ROLE_Editor');

-- PRODUTOS: criar schema para álbuns, músicas, autores, etc.
CREATE TABLE `product` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(30) NULL,
    `short_description` VARCHAR(50) NULL,
    `full_description` VARCHAR(100) NULL,
    `brand` VARCHAR(45) NULL,
    `category` VARCHAR(45) NULL,
    `list_price` DECIMAL(14,2) NULL,
    `discount` DECIMAL(14,2) NULL,
    `enabled` BIT NULL,
    `in_stock` BIT NULL,
    `creation_time` DATETIME NULL,
    `update_time` DATETIME NULL,
    `cost` DECIMAL(14,2) NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE);

CREATE TABLE `box_dimension` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `length` FLOAT NOT NULL,
  `width` FLOAT NOT NULL,
  `height` FLOAT NOT NULL,
  `weight` FLOAT NOT NULL,
  `product_id` INT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK_PRODUCT_idx` (`product_id` ASC) VISIBLE,
  CONSTRAINT `FK_PRODUCT`
      FOREIGN KEY (`product_id`)
          REFERENCES `product` (`id`)
          ON DELETE CASCADE
          ON UPDATE CASCADE);

CREATE TABLE `product_detail` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(45) NOT NULL,
    `value` VARCHAR(45) NOT NULL,
    `product_id` INT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_product_detail_idx` (`product_id` ASC) VISIBLE,
    CONSTRAINT `fk_product_detail`
        FOREIGN KEY (`product_id`)
            REFERENCES `product` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE);

INSERT INTO `product` (`id`, `name`, `short_description`, `full_description`, `brand`, `category`, `list_price`, `discount`, `enabled`, `in_stock`, `creation_time`, `cost`) VALUES (1, 'Guided by Voices', 'CD from an American Indie rock band', 'Guided by Voices is an American indie rock band formed in 1983 in Dayton, Ohio.', 'Matador Records', 'CD', '40.00', '0.00', b'1', b'1', '2024-11-17 19:54:28', '1');

INSERT INTO `box_dimension` (`id`, `length`, `width`, `height`, `weight`, `product_id`) VALUES ('1', '17.5', '10', '10', '15.5', '1');

INSERT INTO `product_detail` (`id`, `name`, `value`, `product_id`) VALUES ('1', 'Track 1', 'Spanish Coin', '1');
INSERT INTO `product_detail` (`id`, `name`, `value`, `product_id`) VALUES ('2', 'Track 2', 'High in the rain', '1');
INSERT INTO `product_detail` (`id`, `name`, `value`, `product_id`) VALUES ('3', 'Track 3', 'Dance of Gurus', '1');
INSERT INTO `product_detail` (`id`, `name`, `value`, `product_id`) VALUES ('4', 'Track 4', 'Flying without a license', '1');
INSERT INTO `product_detail` (`id`, `name`, `value`, `product_id`) VALUES ('5', 'Track 5', 'Psycho House', '1');
INSERT INTO `product_detail` (`id`, `name`, `value`, `product_id`) VALUES ('6', 'Track 6', 'Maintenance man of the haunted house', '1');
INSERT INTO `product_detail` (`id`, `name`, `value`, `product_id`) VALUES ('7', 'Track 7', 'I share a rhythm', '1');
INSERT INTO `product_detail` (`id`, `name`, `value`, `product_id`) VALUES ('8', 'Track 8', 'Razor Bug', '1');
INSERT INTO `product_detail` (`id`, `name`, `value`, `product_id`) VALUES ('9', 'Track 9', 'I wanna Monkey', '1');
INSERT INTO `product_detail` (`id`, `name`, `value`, `product_id`) VALUES ('10', 'Track 10', 'Cherub and the Great Child Actor', '1');
INSERT INTO `product_detail` (`id`, `name`, `value`, `product_id`) VALUES ('11', 'Track 11', 'Black and White eyes in a prism', '1');
INSERT INTO `product_detail` (`id`, `name`, `value`, `product_id`) VALUES ('12', 'Track 12', 'People need holes', '1');
INSERT INTO `product_detail` (`id`, `name`, `value`, `product_id`) VALUES ('13', 'Track 13', 'The bell gets out of the way', '1');
INSERT INTO `product_detail` (`id`, `name`, `value`, `product_id`) VALUES ('14', 'Track 14', 'Chain gang island', '1');
INSERT INTO `product_detail` (`id`, `name`, `value`, `product_id`) VALUES ('15', 'Track 15', 'My (limited) engagement', '1');
