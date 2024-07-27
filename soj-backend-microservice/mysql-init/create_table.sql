/*
 Navicat Premium Data Transfer

 Source Server         : shiro
 Source Server Type    : MySQL
 Source Server Version : 80034
 Source Host           : localhost:3306
 Source Schema         : soj

 Target Server Type    : MySQL
 Target Server Version : 80034
 File Encoding         : 65001

 Date: 04/10/2023 13:49:26
*/

create database if not exists soj;

use soj;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for question
-- ----------------------------
CREATE TABLE if not exists question
(
    `id`          bigint                                                         NOT NULL AUTO_INCREMENT COMMENT 'id',
    `title`       varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '标题',
    `content`     text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci          NULL COMMENT '内容',
    `tags`        varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '标签列表(JSON字符串)',
    `answer`      text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci          NULL COMMENT '题目答案',
    `submitNum`   int                                                            NOT NULL DEFAULT 0 COMMENT '题目提交数',
    `acceptedNum` int                                                            NOT NULL DEFAULT 0 COMMENT '题目通过数',
    `judgeConfig` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci          NULL COMMENT '判题配置(JSON对象) ---便于扩展,只需要改变内部的字段,而不是修改数据库\r\n\r\n{\r\n  timeLimit:string\r\n  stackLimit:string\r\n}',
    `judgeCase`   text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci          NULL COMMENT '判题用例(JSON对象)\r\n{\r\n   input: string\r\n   output:string\r\n}',
    `userId`      bigint                                                         NOT NULL COMMENT '创建题目的用户id',
    `createTime`  datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime`  datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`    tinyint                                                        NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_userId` (`userId` ASC) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '题目'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question_submit
-- ----------------------------
CREATE TABLE if not exists `question_submit`
(
    `id`         bigint                                                        NOT NULL AUTO_INCREMENT COMMENT 'id',
    `questionId` bigint                                                        NOT NULL COMMENT '题目id',
    `language`   varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编程语言',
    `code`       text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci         NOT NULL COMMENT '用户代码',
    `judgeInfo`  text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci         NULL COMMENT '判题信息(JSON对象)',
    `status`     int                                                           NOT NULL DEFAULT 0 COMMENT '判题状态(0-待判题,1-判题中,2-成功,3-失败)',
    `userId`     bigint                                                        NOT NULL COMMENT '提交用户 id',
    `createTime` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`   tinyint                                                       NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_questionId` (`questionId` ASC) USING BTREE,
    INDEX `idx_userId` (`userId` ASC) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '题目提交'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
CREATE TABLE if not exists `user`
(
    `id`           bigint                                                         NOT NULL AUTO_INCREMENT COMMENT 'id',
    `userAccount`  varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '账号',
    `userPassword` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '密码',
    `userName`     varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '用户昵称',
    `userAvatar`   varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '用户头像',
    `userProfile`  text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci          NULL COMMENT '用户简介',
    `userRole`     varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL DEFAULT 'user' COMMENT '用户角色：user/admin/ban',
    `createTime`   datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime`   datetime                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`     tinyint                                                        NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '用户'
  ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
