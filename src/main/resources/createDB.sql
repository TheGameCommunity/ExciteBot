CREATE DATABASE IF NOT EXISTS `excitebot` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin */ /*!80016 DEFAULT ENCRYPTION='N' */;

CREATE TABLE IF NOT EXISTS `admins` (
  `discord_id` bigint unsigned NOT NULL,
  PRIMARY KEY (`discord_id`),
  UNIQUE KEY `discord_id_UNIQUE` (`discord_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `discord_servers` (
  `server_id` bigint unsigned NOT NULL,
  `name` varchar(45) COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT 'UNKNOWN_DISCORD_SERVER',
  `prefix` char(7) COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT '!',
  PRIMARY KEY (`server_id`),
  UNIQUE KEY `server_id_UNIQUE` (`server_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `discord_users` (
  `discord_id` bigint unsigned NOT NULL,
  `discord_name` char(37) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `threshold` int NOT NULL DEFAULT '-1',
  `frequency` varchar(45) COLLATE utf8mb4_bin NOT NULL DEFAULT 'PT30M',
  `lastNotification` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL,
  `dippedBelowThreshold` bit(1) NOT NULL DEFAULT b'0',
  `notifyContinuously` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`discord_id`),
  UNIQUE KEY `discord_id_UNIQUE` (`discord_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `operators` (
  `discord_id` bigint unsigned NOT NULL,
  PRIMARY KEY (`discord_id`),
  UNIQUE KEY `discord_id_UNIQUE` (`discord_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `players` (
  `playerID` int NOT NULL,
  `friendCode` char(14) NOT NULL,
  `name` varchar(8) NOT NULL,
  `discordID` bigint unsigned DEFAULT NULL,
  `redacted` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`playerID`),
  UNIQUE KEY `player_id_UNIQUE` (`playerID`),
  UNIQUE KEY `friendCode_UNIQUE` (`friendCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
