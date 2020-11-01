CREATE DATABASE IF NOT EXISTS `excitebot` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin */ /*!80016 DEFAULT ENCRYPTION='N' */;

CREATE TABLE IF NOT EXISTS `admins` (
  `discordID` bigint unsigned NOT NULL,
  PRIMARY KEY (`discordID`),
  UNIQUE KEY `discord_id_UNIQUE` (`discordID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `auditBan` (
  `auditID` bigint NOT NULL,
  `duration` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `expireTime` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `pardon` bigint DEFAULT NULL,
  PRIMARY KEY (`auditID`),
  UNIQUE KEY `auditID_UNIQUE` (`auditID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `auditCommand` (
  `auditID` bigint NOT NULL,
  `serverName` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `serverID` bigint unsigned NOT NULL,
  `channelName` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `channelID` bigint unsigned NOT NULL,
  `isGuildMessage` bit(1) NOT NULL,
  `isPrivateMessage` bit(1) NOT NULL,
  `isConsoleMessage` bit(1) NOT NULL,
  `isAdmin` bit(1) NOT NULL,
  `isOperator` bit(1) NOT NULL,
  PRIMARY KEY (`auditID`),
  UNIQUE KEY `auditID_UNIQUE` (`auditID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `auditNameChange` (
  `auditID` int NOT NULL,
  `oldName` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `newName` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `playerID` int NOT NULL,
  `friendCode` varchar(14) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`auditID`),
  UNIQUE KEY `auditID_UNIQUE` (`auditID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `auditPardon` (
  `auditID` bigint NOT NULL,
  `banID` bigint NOT NULL,
  PRIMARY KEY (`auditID`),
  UNIQUE KEY `auditID_UNIQUE` (`auditID`),
  UNIQUE KEY `banID_UNIQUE` (`banID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `auditProfileDiscovery` (
  `auditID` bigint NOT NULL,
  PRIMARY KEY (`auditID`),
  UNIQUE KEY `auditID_UNIQUE` (`auditID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `auditRankChange` (
  `auditID` bigint NOT NULL,
  `promotee` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `promoteeDiscordID` bigint unsigned NOT NULL,
  PRIMARY KEY (`auditID`),
  UNIQUE KEY `auditID_UNIQUE` (`auditID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `audits` (
  `auditID` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `issuer` bigint unsigned NOT NULL,
  `issuerName` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `issued` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`auditID`),
  UNIQUE KEY `auditID_UNIQUE` (`auditID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


CREATE TABLE IF NOT EXISTS `discord_servers` (
  `server_id` bigint unsigned NOT NULL,
  `name` varchar(45) COLLATE utf8mb4_bin NOT NULL DEFAULT 'UNKNOWN_DISCORD_SERVER',
  `prefix` char(7) COLLATE utf8mb4_bin NOT NULL DEFAULT '!',
  PRIMARY KEY (`server_id`),
  UNIQUE KEY `server_id_UNIQUE` (`server_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `discord_users` (
  `discordID` bigint unsigned NOT NULL,
  `discord_name` char(37) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `threshold` int NOT NULL DEFAULT '-1',
  `frequency` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT 'PT30M',
  `lastNotification` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `dippedBelowThreshold` bit(1) NOT NULL DEFAULT b'0',
  `notifyContinuously` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`discordID`),
  UNIQUE KEY `discord_id_UNIQUE` (`discordID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


CREATE TABLE IF NOT EXISTS `operators` (
  `discordID` bigint unsigned NOT NULL,
  PRIMARY KEY (`discordID`),
  UNIQUE KEY `discord_id_UNIQUE` (`discordID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `players` (
  `playerID` int NOT NULL,
  `friendCode` char(14) CHARACTER SET utf8 NOT NULL,
  `name` varchar(8) CHARACTER SET utf8 NOT NULL,
  `redacted` bit(1) NOT NULL DEFAULT b'0',
  `discordID` bigint unsigned DEFAULT NULL,
  `lastOnline` varchar(45) CHARACTER SET utf8 NOT NULL DEFAULT '2020-11-01T02:00:00+5000',
  `secondsPlayed` bigint NOT NULL DEFAULT '0',
  PRIMARY KEY (`playerID`),
  UNIQUE KEY `player_id_UNIQUE` (`playerID`),
  UNIQUE KEY `friendCode_UNIQUE` (`friendCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE ROLE IF NOT EXISTS Operator;
REVOKE ALL PRIVILEGES, GRANT OPTION FROM 'Operator';
GRANT CREATE, DELETE, INSERT, SELECT, UPDATE, SHOW VIEW ON excitebot.admins TO 'Operator';
GRANT CREATE, DELETE, INSERT, SELECT, UPDATE, SHOW VIEW ON excitebot.discord_users TO 'Operator';
GRANT CREATE, DELETE, INSERT, SELECT, UPDATE, SHOW VIEW ON excitebot.players TO 'Operator';
GRANT CREATE, DELETE, INSERT, SELECT, SHOW VIEW ON excitebot.operators TO 'Operator';
GRANT SELECT, SHOW VIEW ON excitebot.discord_servers TO 'Operator';

CREATE ROLE IF NOT EXISTS Admin;
REVOKE ALL PRIVILEGES, GRANT OPTION FROM 'Admin';
GRANT SELECT, SHOW VIEW ON excitebot.* TO 'Admin';

CREATE ROLE IF NOT EXISTS User;
REVOKE ALL PRIVILEGES, GRANT OPTION FROM 'User';
GRANT SELECT, SHOW VIEW ON players TO 'User';
