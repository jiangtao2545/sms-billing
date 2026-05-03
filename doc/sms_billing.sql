CREATE DATABASE IF NOT EXISTS sms_billing DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sms_billing;

CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(100) NOT NULL,
  `nickname` VARCHAR(50) DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `sms_application` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `app_id` VARCHAR(32) NOT NULL UNIQUE,
  `app_name` VARCHAR(100) NOT NULL,
  `app_key` VARCHAR(64) NOT NULL,
  `price` DECIMAL(10,4) NOT NULL DEFAULT 0.0500,
  `status` TINYINT NOT NULL DEFAULT 1,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `sms_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `app_id` VARCHAR(32) NOT NULL,
  `phone` VARCHAR(20) NOT NULL,
  `content` TEXT,
  `char_count` INT DEFAULT 0,
  `billing_count` INT DEFAULT 0,
  `price` DECIMAL(10,4) DEFAULT 0.0000,
  `total_fee` DECIMAL(10,4) DEFAULT 0.0000,
  `status` TINYINT NOT NULL DEFAULT 0,
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_app_id` (`app_id`),
  KEY `idx_phone` (`phone`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `sys_login_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT DEFAULT NULL,
  `username` VARCHAR(50) DEFAULT NULL,
  `ip` VARCHAR(50) DEFAULT NULL,
  `user_agent` VARCHAR(500) DEFAULT NULL,
  `login_time` DATETIME DEFAULT NULL,
  `status` TINYINT DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `sys_user` (`username`, `password`, `nickname`, `create_time`)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyU4T/Gzq', '管理员', NOW());
