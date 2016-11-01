/*
SQLyog Community v11.1 (64 bit)
MySQL - 5.5.5-10.1.10-MariaDB : Database - demo_cms
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`demo_cms` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `demo_cms`;

/*Table structure for table `tbl_wechat_tokens` */

DROP TABLE IF EXISTS `tbl_wechat_tokens`;

CREATE TABLE `tbl_wechat_tokens` (
  `appId` VARCHAR(100) NOT NULL,
  `token` VARCHAR(300) DEFAULT NULL,
  `nextTime` BIGINT(20) DEFAULT '0' COMMENT '�´�ȥ��ȡtoken��ʱ��',
  `lastModTime` BIGINT(20) DEFAULT NULL COMMENT '����¼����޸�ʱ��',
  `validTime` BIGINT(20) DEFAULT NULL COMMENT '��ǰtoken��Ч����ʱ��',
  `secret` VARCHAR(100) DEFAULT NULL COMMENT '��Կ',
  `jsapiTicket` VARCHAR(200) DEFAULT NULL,
  `jsapiTicketValidTime` BIGINT(20) DEFAULT NULL,
  PRIMARY KEY (`appId`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
