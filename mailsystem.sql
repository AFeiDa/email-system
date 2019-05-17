/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80015
 Source Host           : localhost:3306
 Source Schema         : mailsystem

 Target Server Type    : MySQL
 Target Server Version : 80015
 File Encoding         : 65001

 Date: 17/05/2019 23:48:58
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for friend
-- ----------------------------
DROP TABLE IF EXISTS `friend`;
CREATE TABLE `friend`  (
  `me_ID` int(11) NOT NULL,
  `friend_ID` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`me_ID`) USING BTREE,
  INDEX `friend_ID`(`friend_ID`) USING BTREE,
  CONSTRAINT `friend_ibfk_1` FOREIGN KEY (`me_ID`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `friend_ibfk_2` FOREIGN KEY (`friend_ID`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = '这个表描述好友关系，USER_ID为ME_ID的USER好友列表里有USER_ID为FRIEND_ID的USER' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for log
-- ----------------------------
DROP TABLE IF EXISTS `log`;
CREATE TABLE `log`  (
  `log_ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '日志编号',
  `log_operatorID` int(11) NULL DEFAULT NULL COMMENT '操作者',
  `log_conent` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '日志内容，操作事项',
  `log_date` datetime(0) NULL DEFAULT NULL COMMENT '日志产生时间',
  `log_state` tinyint(1) NULL DEFAULT NULL COMMENT '操作结果（1是成功，0是失败）',
  PRIMARY KEY (`log_ID`) USING BTREE,
  INDEX `log_operatorID`(`log_operatorID`) USING BTREE,
  CONSTRAINT `log_ibfk_1` FOREIGN KEY (`log_operatorID`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = '记录操作日志的表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for mail
-- ----------------------------
DROP TABLE IF EXISTS `mail`;
CREATE TABLE `mail`  (
  `mail_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '邮件ID',
  `mail_from` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '发邮件的人',
  `mail_to` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '接收邮件的人',
  `mail_subject` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '邮件主题',
  `mail_content` varchar(1023) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '邮件内容',
  `mail_date` datetime(0) NULL DEFAULT NULL COMMENT '发送邮件时间',
  PRIMARY KEY (`mail_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = '保存邮件的表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_name` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '用户名',
  `user_password` varchar(25) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '用户密码',
  `user_mailstorage` int(11) NULL DEFAULT 2097152 COMMENT '用户邮箱的剩余存储空间，最大2M (2097152B)',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = '存储用户信息的表\r\nUSER_PASSWORD字段加密后存储' ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
