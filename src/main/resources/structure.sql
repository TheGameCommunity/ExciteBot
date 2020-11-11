CREATE DATABASE  IF NOT EXISTS `excitebot` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `excitebot`;
-- MySQL dump 10.13  Distrib 8.0.22, for Linux (x86_64)
--
-- Host: localhost    Database: excitebot
-- ------------------------------------------------------
-- Server version	8.0.22

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admins`
--

DROP TABLE IF EXISTS `admins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admins` (
  `discordID` bigint unsigned NOT NULL,
  PRIMARY KEY (`discordID`),
  UNIQUE KEY `discord_id_UNIQUE` (`discordID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `audit_bans`
--

DROP TABLE IF EXISTS `audit_bans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_bans` (
  `auditID` bigint unsigned NOT NULL,
  `duration` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `expireTime` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `bannedID` bigint unsigned NOT NULL,
  `bannedUsername` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `pardon` bigint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`auditID`),
  UNIQUE KEY `auditID_UNIQUE` (`auditID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `audit_commands`
--

DROP TABLE IF EXISTS `audit_commands`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_commands` (
  `auditID` bigint unsigned NOT NULL,
  `serverName` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `audit_name_changes`
--

DROP TABLE IF EXISTS `audit_name_changes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_name_changes` (
  `auditID` bigint unsigned NOT NULL,
  `oldName` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `newName` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `playerID` int NOT NULL,
  `friendCode` varchar(14) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`auditID`),
  UNIQUE KEY `auditID_UNIQUE` (`auditID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `audit_pardons`
--

DROP TABLE IF EXISTS `audit_pardons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_pardons` (
  `auditID` bigint unsigned NOT NULL,
  `banID` bigint unsigned NOT NULL,
  PRIMARY KEY (`auditID`),
  UNIQUE KEY `auditID_UNIQUE` (`auditID`),
  UNIQUE KEY `banID_UNIQUE` (`banID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `audit_profile_discoveries`
--

DROP TABLE IF EXISTS `audit_profile_discoveries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_profile_discoveries` (
  `auditID` bigint unsigned NOT NULL,
  PRIMARY KEY (`auditID`),
  UNIQUE KEY `auditID_UNIQUE` (`auditID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `audit_rank_changes`
--

DROP TABLE IF EXISTS `audit_rank_changes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_rank_changes` (
  `auditID` bigint unsigned NOT NULL,
  `promotee` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `promoteeDiscordID` bigint unsigned NOT NULL,
  PRIMARY KEY (`auditID`),
  UNIQUE KEY `auditID_UNIQUE` (`auditID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `audits`
--

DROP TABLE IF EXISTS `audits`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audits` (
  `auditID` bigint unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `issuer` bigint unsigned NOT NULL,
  `issuerName` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `issued` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`auditID`),
  UNIQUE KEY `auditID_UNIQUE` (`auditID`)
) ENGINE=InnoDB AUTO_INCREMENT=94 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `discord_servers`
--

DROP TABLE IF EXISTS `discord_servers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discord_servers` (
  `serverID` bigint unsigned NOT NULL,
  `serverName` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT 'UNKNOWN_DISCORD_SERVER',
  `prefix` char(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT '!',
  PRIMARY KEY (`serverID`),
  UNIQUE KEY `server_id_UNIQUE` (`serverID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `discord_users`
--

DROP TABLE IF EXISTS `discord_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discord_users` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `operators`
--

DROP TABLE IF EXISTS `operators`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operators` (
  `discordID` bigint unsigned NOT NULL,
  PRIMARY KEY (`discordID`),
  UNIQUE KEY `discord_id_UNIQUE` (`discordID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `players`
--

DROP TABLE IF EXISTS `players`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `players` (
  `playerID` int NOT NULL,
  `friendCode` char(14) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `name` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `redacted` bit(1) NOT NULL DEFAULT b'0',
  `discordID` bigint unsigned DEFAULT NULL,
  `lastOnline` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '2020-11-01T02:00:00+5000',
  `secondsPlayed` bigint NOT NULL DEFAULT '0',
  PRIMARY KEY (`playerID`),
  UNIQUE KEY `player_id_UNIQUE` (`playerID`),
  UNIQUE KEY `friendCode_UNIQUE` (`friendCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-11-10 21:19:52
