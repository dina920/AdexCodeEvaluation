-- MySQL dump 10.13  Distrib 8.0.16, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: customers
-- ------------------------------------------------------
-- Server version	8.0.16

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `customer` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `active` tinyint(1) unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (1,'Big News Media Corp',1),(2,'Online Mega Store',1),(3,'Nachoroo Delivery',0),(4,'Euro Telecom Group',1);
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hourly_stats`
--

DROP TABLE IF EXISTS `hourly_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `hourly_stats` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `customer_id` int(11) unsigned NOT NULL,
  `time` timestamp NOT NULL,
  `request_count` bigint(20) unsigned NOT NULL DEFAULT '0',
  `invalid_count` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_customer_time` (`customer_id`,`time`),
  KEY `customer_idx` (`customer_id`),
  CONSTRAINT `hourly_stats_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hourly_stats`
--

LOCK TABLES `hourly_stats` WRITE;
/*!40000 ALTER TABLE `hourly_stats` DISABLE KEYS */;
INSERT INTO `hourly_stats` VALUES (4,3,'1970-01-18 10:12:05',0,3),(8,2,'1970-01-18 10:00:00',4,0),(12,2,'1970-01-19 09:00:00',8,6),(16,4,'1970-01-19 09:00:00',1,0),(18,1,'1970-01-19 09:00:00',0,3);
/*!40000 ALTER TABLE `hourly_stats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ip_blacklist`
--

DROP TABLE IF EXISTS `ip_blacklist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `ip_blacklist` (
  `ip` int(11) unsigned NOT NULL,
  PRIMARY KEY (`ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ip_blacklist`
--

LOCK TABLES `ip_blacklist` WRITE;
/*!40000 ALTER TABLE `ip_blacklist` DISABLE KEYS */;
INSERT INTO `ip_blacklist` VALUES (0),(2130706433),(4294967295);
/*!40000 ALTER TABLE `ip_blacklist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ua_blacklist`
--

DROP TABLE IF EXISTS `ua_blacklist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `ua_blacklist` (
  `ua` varchar(255) NOT NULL,
  PRIMARY KEY (`ua`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ua_blacklist`
--

LOCK TABLES `ua_blacklist` WRITE;
/*!40000 ALTER TABLE `ua_blacklist` DISABLE KEYS */;
INSERT INTO `ua_blacklist` VALUES ('A6-Indexer'),('Googlebot'),('Googlebot-News');
/*!40000 ALTER TABLE `ua_blacklist` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-11-19 12:32:17
