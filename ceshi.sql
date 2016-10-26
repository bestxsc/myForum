/*
Navicat MySQL Data Transfer

Source Server         : 127.0.0.1
Source Server Version : 50022
Source Host           : 127.0.0.1:3306
Source Database       : ceshi

Target Server Type    : MYSQL
Target Server Version : 50022
File Encoding         : 65001

Date: 2016-10-26 16:52:42
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for group
-- ----------------------------
DROP TABLE IF EXISTS `group`;
CREATE TABLE `group` (
  `gid` int(10) unsigned NOT NULL auto_increment,
  `gname` varchar(128) NOT NULL default '',
  PRIMARY KEY  (`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of group
-- ----------------------------
INSERT INTO `group` VALUES ('1', '研发部门');
INSERT INTO `group` VALUES ('2', '商务部门');
INSERT INTO `group` VALUES ('3', '人力部门');
INSERT INTO `group` VALUES ('4', '国际合作部门');
INSERT INTO `group` VALUES ('5', 'IT部门');
INSERT INTO `group` VALUES ('6', '安全保卫处');
INSERT INTO `group` VALUES ('7', '总务处');
INSERT INTO `group` VALUES ('8', '财务部门');
INSERT INTO `group` VALUES ('9', '公司高管');

-- ----------------------------
-- Table structure for notices
-- ----------------------------
DROP TABLE IF EXISTS `notices`;
CREATE TABLE `notices` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `uid` int(10) unsigned NOT NULL,
  `theme` text,
  `content` text,
  `group` int(10) unsigned NOT NULL default '0',
  `timestamp` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP COMMENT '时间戳',
  PRIMARY KEY  (`id`),
  KEY `uid` (`uid`),
  CONSTRAINT `notices_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of notices
-- ----------------------------
INSERT INTO `notices` VALUES ('15', '1', '开会通知', '<p><span style=\"font-size: 24px;\">﻿</span><span style=\"font-size: 24px;\">﻿本周三上午9：30，我们将会有一个临时会议，主要是商讨一下资金流的核算问题，请各位做好相关准备！</span><br></p>', '0', '2016-10-23 19:04:24');

-- ----------------------------
-- Table structure for reply
-- ----------------------------
DROP TABLE IF EXISTS `reply`;
CREATE TABLE `reply` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `uid` int(10) unsigned NOT NULL,
  `context` text,
  `notice` int(10) unsigned default '0',
  `timestamp` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`),
  KEY `uid` (`uid`),
  CONSTRAINT `reply_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of reply
-- ----------------------------
INSERT INTO `reply` VALUES ('51', '1', '我们本科生的可以去吗？具体时间是几点？', '8', '2016-10-23 19:05:19');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `uid` int(11) unsigned NOT NULL auto_increment,
  `uname` varchar(255) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `name` varchar(255) default '',
  `group` int(10) unsigned NOT NULL default '0',
  `publishing` bit(1) NOT NULL default '',
  `reply` bit(1) NOT NULL default '',
  PRIMARY KEY  (`uid`),
  UNIQUE KEY `uname` (`uname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'lpb', '5df40b485c943101a9577345fd1e43d18cd14e5f', '李强', '0', '', '');
INSERT INTO `user` VALUES ('5', 'hahaha', '111222', '大佬', '0', '\0', '');
INSERT INTO `user` VALUES ('6', 'lipengbiao', '112233', '李鹏彪', '0', '', '');
INSERT INTO `user` VALUES ('7', 'momo123', '112233', '小明', '0', '', '');
INSERT INTO `user` VALUES ('8', 'momo456', '112233', '哈哈', '0', '', '');
INSERT INTO `user` VALUES ('9', 'tian', '112233', '哈哈', '0', '', '');
INSERT INTO `user` VALUES ('10', 'momomo', 'a558983aff49c50dcfb596c551b9d23473b9f7f6', 'ooinini', '0', '', '');
