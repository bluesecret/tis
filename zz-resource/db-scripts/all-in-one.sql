
-- ----------------------------
-- 请仅在下面的数据库链接中执行该脚本。
-- 主数据源 [localhost:3306/tis]
-- ----------------------------

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 部门管理表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
                            `dept_id` bigint NOT NULL COMMENT '部门Id',
                            `parent_id` bigint DEFAULT NULL COMMENT '父部门Id',
                            `dept_name` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '部门名称',
                            `show_order` int(11) NOT NULL COMMENT '兄弟部分之间的显示顺序，数字越小越靠前',
                            `create_user_id` bigint NOT NULL COMMENT '创建者Id',
                            `create_time` datetime NOT NULL COMMENT '创建时间',
                            `update_user_id` bigint NOT NULL COMMENT '更新者Id',
                            `update_time` datetime NOT NULL COMMENT '最后更新时间',
                            `deleted_flag` int(1) NOT NULL COMMENT '删除标记(1: 正常 -1: 已删除)',
                            PRIMARY KEY (`dept_id`) USING BTREE,
                            KEY `idx_parent_id` (`parent_id`) USING BTREE,
                            KEY `idx_show_order` (`show_order`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT COMMENT='部门管理表';

-- ----------------------------
-- 部门关联关系表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept_relation`;
CREATE TABLE `sys_dept_relation` (
                                     `parent_dept_id` bigint NOT NULL COMMENT '父部门Id',
                                     `dept_id` bigint NOT NULL COMMENT '部门Id',
                                     PRIMARY KEY (`parent_dept_id`,`dept_id`),
                                     KEY `idx_dept_id` (`dept_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT COMMENT='部门关联关系表';

-- ----------------------------
-- 系统部门岗位表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept_post`;
CREATE TABLE `sys_dept_post` (
                                 `dept_post_id` bigint NOT NULL COMMENT '主键Id',
                                 `dept_id` bigint NOT NULL COMMENT '部门Id',
                                 `post_id` bigint NOT NULL COMMENT '岗位Id',
                                 `post_show_name` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '部门岗位显示名称',
                                 PRIMARY KEY (`dept_post_id`) USING BTREE,
                                 KEY `idx_post_id` (`post_id`) USING BTREE,
                                 KEY `idx_dept_id` (`dept_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- 系统岗位表
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post` (
                            `post_id` bigint NOT NULL COMMENT '岗位Id',
                            `post_name` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '岗位名称',
                            `post_level` int(11) NOT NULL COMMENT '岗位层级，数值越小级别越高',
                            `leader_post` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否领导岗位',
                            `create_user_id` bigint NOT NULL COMMENT '创建者Id',
                            `create_time` datetime NOT NULL COMMENT '创建时间',
                            `update_user_id` bigint NOT NULL COMMENT '更新者Id',
                            `update_time` datetime NOT NULL COMMENT '最后更新时间',
                            PRIMARY KEY (`post_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- 系统用户岗位表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post` (
                                 `user_id` bigint NOT NULL COMMENT '用户Id',
                                 `dept_post_id` bigint NOT NULL COMMENT '部门岗位Id',
                                 `post_id` bigint NOT NULL COMMENT '岗位Id',
                                 PRIMARY KEY (`user_id`,`dept_post_id`) USING BTREE,
                                 KEY `idx_post_id` (`post_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- 系统用户表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
                            `user_id` bigint NOT NULL COMMENT '主键Id',
                            `login_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '用户登录名称',
                            `password` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '密码',
                            `show_name` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '用户显示名称',
                            `dept_id` bigint NOT NULL COMMENT '用户所在部门Id',
                            `head_image_url` varchar(512) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户头像的Url',
                            `user_type` int(11) NOT NULL COMMENT '用户类型(0: 管理员 1: 系统管理用户 2: 系统业务用户)',
                            `user_status` int(11) NOT NULL COMMENT '状态(0: 正常 1: 锁定)',
                            `email` varchar(512) COLLATE utf8mb4_bin COMMENT '用户邮箱',
                            `mobile` varchar(64) COLLATE utf8mb4_bin COMMENT '用户手机',
                            `user_auth_info` varchar(2000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '第三方授权用户信息',
                            `create_user_id` bigint NOT NULL COMMENT '创建者Id',
                            `create_time` datetime NOT NULL COMMENT '创建时间',
                            `update_user_id` bigint NOT NULL COMMENT '更新者Id',
                            `update_time` datetime NOT NULL COMMENT '最后更新时间',
                            `deleted_flag` int(11) NOT NULL COMMENT '删除标记(1: 正常 -1: 已删除)',
                            PRIMARY KEY (`user_id`) USING BTREE,
                            UNIQUE KEY `uk_login_name` (`login_name`) USING BTREE,
                            KEY `idx_dept_id` (`dept_id`) USING BTREE,
                            KEY `idx_status` (`user_status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT COMMENT='系统用户表';

-- ----------------------------
-- 用户第三方授权信息表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_auth`;
CREATE TABLE `sys_user_auth` (
                                 `id` bigint NOT NULL COMMENT '主键Id',
                                 `user_id` bigint NOT NULL COMMENT '用户Id',
                                 `source` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '授权来源',
                                 `open_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '授权方的OpenId',
                                 `union_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '授权方的UnionId',
                                 `auth_user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '授权方的UnionId',
                                 `extra_data` varchar(2000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '扩展数据',
                                 `create_time` datetime NOT NULL COMMENT '创建时间',
                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 UNIQUE KEY `uk_user_id_source` (`user_id`,`source`) USING BTREE,
                                 KEY `idx_source_open_id` (`source`,`open_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户第三方授权信息表';

-- ----------------------------
-- 系统角色表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
                            `role_id` bigint NOT NULL COMMENT '主键Id',
                            `role_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '角色名称',
                            `create_user_id` bigint NOT NULL COMMENT '创建者Id',
                            `create_time` datetime NOT NULL COMMENT '创建时间',
                            `update_user_id` bigint NOT NULL COMMENT '更新者Id',
                            `update_time` datetime NOT NULL COMMENT '最后更新时间',
                            PRIMARY KEY (`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT COMMENT='系统角色表';

-- ----------------------------
-- 用户与角色对应关系表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
                                 `user_id` bigint NOT NULL COMMENT '用户Id',
                                 `role_id` bigint NOT NULL COMMENT '角色Id',
                                 PRIMARY KEY (`user_id`,`role_id`) USING BTREE,
                                 KEY `idx_role_id` (`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT COMMENT='用户与角色对应关系表';

-- ----------------------------
-- 菜单和操作权限管理表
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
                            `menu_id` bigint NOT NULL COMMENT '主键Id',
                            `parent_id` bigint DEFAULT NULL COMMENT '父菜单Id，目录菜单的父菜单为null',
                            `menu_name` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '菜单显示名称',
                            `menu_type` int(11) NOT NULL COMMENT '(0: 目录 1: 菜单 2: 按钮 3: UI片段)',
                            `form_router_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '前端表单路由名称，仅用于menu_type为1的菜单类型',
                            `online_form_id` bigint(20) DEFAULT NULL COMMENT '在线表单主键Id',
                            `online_menu_perm_type` int(11) DEFAULT NULL COMMENT '在线表单菜单的权限控制类型',
                            `report_page_id` bigint(20) DEFAULT NULL COMMENT '统计页面主键Id',
                            `online_flow_entry_id` bigint(20) DEFAULT NULL COMMENT '仅用于在线表单的流程Id',
                            `show_order` int(11) NOT NULL COMMENT '菜单显示顺序 (值越小，排序越靠前)',
                            `icon` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '菜单图标',
                            `extra_data` text COLLATE utf8mb4_bin DEFAULT NULL COMMENT '附加信息',
                            `create_user_id` bigint NOT NULL COMMENT '创建者Id',
                            `create_time` datetime NOT NULL COMMENT '创建时间',
                            `update_user_id` bigint NOT NULL COMMENT '更新者Id',
                            `update_time` datetime NOT NULL COMMENT '最后更新时间',
                            PRIMARY KEY (`menu_id`) USING BTREE,
                            KEY `idx_show_order` (`show_order`) USING BTREE,
                            KEY `idx_parent_id` (`parent_id`) USING BTREE,
                            KEY `idx_menu_type` (`menu_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT COMMENT='菜单和操作权限管理表';

-- ----------------------------
-- 角色与菜单对应关系表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
                                 `role_id` bigint NOT NULL COMMENT '角色Id',
                                 `menu_id` bigint NOT NULL COMMENT '菜单Id',
                                 PRIMARY KEY (`role_id`,`menu_id`) USING BTREE,
                                 KEY `idx_menu_id` (`menu_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT COMMENT='角色与菜单对应关系表';

-- ----------------------------
-- 权限资源白名单表
-- ----------------------------
DROP TABLE IF EXISTS `sys_perm_whitelist`;
CREATE TABLE `sys_perm_whitelist` (
                                      `perm_url` varchar(512) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '权限资源的url',
                                      `module_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '权限资源所属模块名字(通常是Controller的名字)',
                                      `perm_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '权限的名称',
                                      PRIMARY KEY (`perm_url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='权限资源白名单表(认证用户均可访问的url资源)';

-- ----------------------------
-- 数据权限表
-- ----------------------------
DROP TABLE IF EXISTS `sys_data_perm`;
CREATE TABLE `sys_data_perm` (
                                 `data_perm_id` bigint NOT NULL COMMENT '主键',
                                 `data_perm_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '显示名称',
                                 `rule_type` tinyint(2) NOT NULL COMMENT '数据权限规则类型。',
                                 `extra_data` varchar(2000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '扩展数据',
                                 `create_user_id` bigint NOT NULL COMMENT '创建者Id',
                                 `create_time` datetime NOT NULL COMMENT '创建时间',
                                 `update_user_id` bigint NOT NULL COMMENT '更新者Id',
                                 `update_time` datetime NOT NULL COMMENT '最后更新时间',
                                 PRIMARY KEY (`data_perm_id`) USING BTREE,
                                 KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据权限表';

-- ----------------------------
-- 数据权限和用户关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_data_perm_user`;
CREATE TABLE `sys_data_perm_user` (
                                      `data_perm_id` bigint NOT NULL COMMENT '数据权限Id',
                                      `user_id` bigint NOT NULL COMMENT '用户Id',
                                      PRIMARY KEY (`data_perm_id`,`user_id`),
                                      KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据权限和用户关联表';

-- ----------------------------
-- 数据权限和部门关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_data_perm_dept`;
CREATE TABLE `sys_data_perm_dept` (
                                      `data_perm_id` bigint NOT NULL COMMENT '数据权限Id',
                                      `dept_id` bigint NOT NULL COMMENT '部门Id',
                                      PRIMARY KEY (`data_perm_id`,`dept_id`),
                                      KEY `idx_dept_id` (`dept_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据权限和部门关联表';

-- ----------------------------
-- 数据权限和菜单关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_data_perm_menu`;
CREATE TABLE `sys_data_perm_menu` (
                                      `data_perm_id` bigint NOT NULL COMMENT '数据权限Id',
                                      `menu_id` bigint NOT NULL COMMENT '菜单Id',
                                      PRIMARY KEY (`data_perm_id`,`menu_id`),
                                      KEY `idx_menu_id` (`menu_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据权限和菜单关联表';

-- ----------------------------
-- 系统操作日志表
-- ----------------------------
DROP TABLE IF EXISTS `zz_sys_operation_log`;
CREATE TABLE `zz_sys_operation_log` (
                                        `log_id` bigint(20) NOT NULL COMMENT '主键Id',
                                        `description` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '日志描述',
                                        `operation_type` int(11) DEFAULT NULL COMMENT '操作类型',
                                        `service_name` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '接口所在服务名称',
                                        `api_class` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '调用的controller全类名',
                                        `api_method` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '调用的controller中的方法',
                                        `session_id` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户会话sessionId',
                                        `trace_id` char(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '每次请求的Id',
                                        `elapse` int(11) DEFAULT NULL COMMENT '调用时长',
                                        `request_method` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'HTTP 请求方法，如GET',
                                        `request_url` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'HTTP 请求地址',
                                        `request_arguments` longtext COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'controller接口参数',
                                        `response_result` varchar(2000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'controller应答结果',
                                        `request_ip` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '请求IP',
                                        `success` bit(1) DEFAULT NULL COMMENT '应答状态',
                                        `error_msg` varchar(2000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '错误信息',
                                        `tenant_id` bigint DEFAULT NULL COMMENT '租户Id',
                                        `operator_id` bigint DEFAULT NULL COMMENT '操作员Id',
                                        `operator_name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '操作员名称',
                                        `operation_time` datetime DEFAULT NULL COMMENT '操作时间',
                                        PRIMARY KEY (`log_id`),
                                        KEY `idx_trace_id_idx` (`trace_id`),
                                        KEY `idx_operation_type_idx` (`operation_type`),
                                        KEY `idx_operation_time_idx` (`operation_time`) USING BTREE,
                                        KEY `idx_success` (`success`) USING BTREE,
                                        KEY `idx_elapse` (`elapse`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='系统操作日志表';

-- ----------------------------
-- 管理员账号数据
-- ----------------------------
BEGIN;
INSERT INTO `sys_dept` VALUES(1879052333397053443,NULL,'公司总部',1,1879052333397053440,CURDATE(),1879052333397053440,CURDATE(),1);
INSERT INTO `sys_user` VALUES(1879052333397053440,'admin','$2a$10$c0eS25j9/u4NHwNJgeY4yOoUfRYvHInNQRgi3qVVV7jbsMHk5pE4u','管理员',1879052333397053443,NULL,0,0,NULL,NULL,NULL,1879052333397053440,CURDATE(),1879052333397053440,CURDATE(),1);
INSERT INTO `sys_dept_relation` VALUES(1879052333397053443,1879052333397053443);
COMMIT;
SET FOREIGN_KEY_CHECKS = 1;


-- ----------------------------
-- 如果是多租户工程，则需要在租户管理数据库中执行该脚本，既TENANT_ADMIN数据源类型指向的数据库。
-- 这里包含流程引擎自带的数据表和自增序列，以及橙单工作流模块内置的zz_flow开头的数据表。
-- ----------------------------

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ACT_EVT_LOG
-- ----------------------------
DROP TABLE IF EXISTS `ACT_EVT_LOG`;
CREATE TABLE `ACT_EVT_LOG` (
                               `LOG_NR_` bigint NOT NULL AUTO_INCREMENT,
                               `TYPE_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                               `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                               `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                               `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                               `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                               `TIME_STAMP_` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
                               `USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               `DATA_` longblob,
                               `LOCK_OWNER_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               `LOCK_TIME_` timestamp(3) NULL DEFAULT NULL,
                               `IS_PROCESSED_` tinyint DEFAULT '0',
                               PRIMARY KEY (`LOG_NR_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_GE_BYTEARRAY
-- ----------------------------
DROP TABLE IF EXISTS `ACT_GE_BYTEARRAY`;
CREATE TABLE `ACT_GE_BYTEARRAY` (
                                    `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                    `REV_` int DEFAULT NULL,
                                    `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `DEPLOYMENT_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                    `BYTES_` longblob,
                                    `GENERATED_` tinyint DEFAULT NULL,
                                    PRIMARY KEY (`ID_`),
                                    KEY `ACT_FK_BYTEARR_DEPL` (`DEPLOYMENT_ID_`),
                                    CONSTRAINT `ACT_FK_BYTEARR_DEPL` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `ACT_RE_DEPLOYMENT` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_GE_PROPERTY
-- ----------------------------
DROP TABLE IF EXISTS `ACT_GE_PROPERTY`;
CREATE TABLE `ACT_GE_PROPERTY` (
                                   `NAME_` varchar(64) COLLATE utf8_bin NOT NULL,
                                   `VALUE_` varchar(300) COLLATE utf8_bin DEFAULT NULL,
                                   `REV_` int DEFAULT NULL,
                                   PRIMARY KEY (`NAME_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_GE_PROPERTY
-- ----------------------------
BEGIN;
INSERT INTO `ACT_GE_PROPERTY` VALUES ('batch.schema.version', '7.0.1.1', 1);
INSERT INTO `ACT_GE_PROPERTY` VALUES ('cfg.execution-related-entities-count', 'true', 1);
INSERT INTO `ACT_GE_PROPERTY` VALUES ('cfg.task-related-entities-count', 'true', 1);
INSERT INTO `ACT_GE_PROPERTY` VALUES ('common.schema.version', '7.0.1.1', 1);
INSERT INTO `ACT_GE_PROPERTY` VALUES ('entitylink.schema.version', '7.0.1.1', 1);
INSERT INTO `ACT_GE_PROPERTY` VALUES ('eventsubscription.schema.version', '7.0.1.1', 1);
INSERT INTO `ACT_GE_PROPERTY` VALUES ('identitylink.schema.version', '7.0.1.1', 1);
INSERT INTO `ACT_GE_PROPERTY` VALUES ('job.schema.version', '7.0.1.1', 1);
INSERT INTO `ACT_GE_PROPERTY` VALUES ('next.dbid', '1', 1);
INSERT INTO `ACT_GE_PROPERTY` VALUES ('schema.history', 'create(7.0.1.1)', 1);
INSERT INTO `ACT_GE_PROPERTY` VALUES ('schema.version', '7.0.1.1', 1);
INSERT INTO `ACT_GE_PROPERTY` VALUES ('task.schema.version', '7.0.1.1', 1);
INSERT INTO `ACT_GE_PROPERTY` VALUES ('variable.schema.version', '7.0.1.1', 1);
COMMIT;

-- ----------------------------
-- Table structure for ACT_HI_ACTINST
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_ACTINST`;
CREATE TABLE `ACT_HI_ACTINST` (
                                  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                  `REV_` int DEFAULT '1',
                                  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                  `ACT_ID_` varchar(255) COLLATE utf8_bin NOT NULL,
                                  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                  `CALL_PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                  `ACT_NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                  `ACT_TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
                                  `ASSIGNEE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                  `START_TIME_` datetime(3) NOT NULL,
                                  `END_TIME_` datetime(3) DEFAULT NULL,
                                  `TRANSACTION_ORDER_` int DEFAULT NULL,
                                  `DURATION_` bigint DEFAULT NULL,
                                  `DELETE_REASON_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
                                  PRIMARY KEY (`ID_`),
                                  KEY `ACT_IDX_HI_ACT_INST_START` (`START_TIME_`),
                                  KEY `ACT_IDX_HI_ACT_INST_END` (`END_TIME_`),
                                  KEY `ACT_IDX_HI_ACT_INST_PROCINST` (`PROC_INST_ID_`,`ACT_ID_`),
                                  KEY `ACT_IDX_HI_ACT_INST_EXEC` (`EXECUTION_ID_`,`ACT_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_ATTACHMENT
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_ATTACHMENT`;
CREATE TABLE `ACT_HI_ATTACHMENT` (
                                     `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                     `REV_` int DEFAULT NULL,
                                     `USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `DESCRIPTION_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                     `TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                     `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                     `URL_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                     `CONTENT_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                     `TIME_` datetime(3) DEFAULT NULL,
                                     PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_COMMENT
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_COMMENT`;
CREATE TABLE `ACT_HI_COMMENT` (
                                  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                  `TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                  `TIME_` datetime(3) NOT NULL,
                                  `USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                  `ACTION_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                  `MESSAGE_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                  `FULL_MSG_` longblob,
                                  PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_DETAIL
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_DETAIL`;
CREATE TABLE `ACT_HI_DETAIL` (
                                 `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                 `TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
                                 `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                 `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                 `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                 `ACT_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                 `NAME_` varchar(255) COLLATE utf8_bin NOT NULL,
                                 `VAR_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                 `REV_` int DEFAULT NULL,
                                 `TIME_` datetime(3) NOT NULL,
                                 `BYTEARRAY_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                 `DOUBLE_` double DEFAULT NULL,
                                 `LONG_` bigint DEFAULT NULL,
                                 `TEXT_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                 `TEXT2_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                 PRIMARY KEY (`ID_`),
                                 KEY `ACT_IDX_HI_DETAIL_PROC_INST` (`PROC_INST_ID_`),
                                 KEY `ACT_IDX_HI_DETAIL_ACT_INST` (`ACT_INST_ID_`),
                                 KEY `ACT_IDX_HI_DETAIL_TIME` (`TIME_`),
                                 KEY `ACT_IDX_HI_DETAIL_NAME` (`NAME_`),
                                 KEY `ACT_IDX_HI_DETAIL_TASK_ID` (`TASK_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_ENTITYLINK
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_ENTITYLINK`;
CREATE TABLE `ACT_HI_ENTITYLINK` (
                                     `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                     `LINK_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `CREATE_TIME_` datetime(3) DEFAULT NULL,
                                     `SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `SUB_SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `SCOPE_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `SCOPE_DEFINITION_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `PARENT_ELEMENT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `REF_SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `REF_SCOPE_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `REF_SCOPE_DEFINITION_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `ROOT_SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `ROOT_SCOPE_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `HIERARCHY_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     PRIMARY KEY (`ID_`),
                                     KEY `ACT_IDX_HI_ENT_LNK_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`,`LINK_TYPE_`),
                                     KEY `ACT_IDX_HI_ENT_LNK_REF_SCOPE` (`REF_SCOPE_ID_`,`REF_SCOPE_TYPE_`,`LINK_TYPE_`),
                                     KEY `ACT_IDX_HI_ENT_LNK_ROOT_SCOPE` (`ROOT_SCOPE_ID_`,`ROOT_SCOPE_TYPE_`,`LINK_TYPE_`),
                                     KEY `ACT_IDX_HI_ENT_LNK_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`,`LINK_TYPE_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_IDENTITYLINK
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_IDENTITYLINK`;
CREATE TABLE `ACT_HI_IDENTITYLINK` (
                                       `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                       `GROUP_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                       `CREATE_TIME_` datetime(3) DEFAULT NULL,
                                       `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                       `SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `SUB_SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `SCOPE_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `SCOPE_DEFINITION_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       PRIMARY KEY (`ID_`),
                                       KEY `ACT_IDX_HI_IDENT_LNK_USER` (`USER_ID_`),
                                       KEY `ACT_IDX_HI_IDENT_LNK_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`),
                                       KEY `ACT_IDX_HI_IDENT_LNK_SUB_SCOPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`),
                                       KEY `ACT_IDX_HI_IDENT_LNK_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`),
                                       KEY `ACT_IDX_HI_IDENT_LNK_TASK` (`TASK_ID_`),
                                       KEY `ACT_IDX_HI_IDENT_LNK_PROCINST` (`PROC_INST_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_PROCINST
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_PROCINST`;
CREATE TABLE `ACT_HI_PROCINST` (
                                   `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                   `REV_` int DEFAULT '1',
                                   `PROC_INST_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                   `BUSINESS_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                   `START_TIME_` datetime(3) NOT NULL,
                                   `END_TIME_` datetime(3) DEFAULT NULL,
                                   `DURATION_` bigint DEFAULT NULL,
                                   `START_USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `START_ACT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `END_ACT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `SUPER_PROCESS_INSTANCE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                   `DELETE_REASON_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                   `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
                                   `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `CALLBACK_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `CALLBACK_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `REFERENCE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `REFERENCE_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `PROPAGATED_STAGE_INST_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `BUSINESS_STATUS_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   PRIMARY KEY (`ID_`),
                                   UNIQUE KEY `PROC_INST_ID_` (`PROC_INST_ID_`),
                                   KEY `ACT_IDX_HI_PRO_INST_END` (`END_TIME_`),
                                   KEY `ACT_IDX_HI_PRO_I_BUSKEY` (`BUSINESS_KEY_`),
                                   KEY `ACT_IDX_HI_PRO_SUPER_PROCINST` (`SUPER_PROCESS_INSTANCE_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_TASKINST
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_TASKINST`;
CREATE TABLE `ACT_HI_TASKINST` (
                                   `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                   `REV_` int DEFAULT '1',
                                   `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                   `TASK_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                   `TASK_DEF_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                   `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                   `SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `SUB_SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `SCOPE_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `SCOPE_DEFINITION_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `PROPAGATED_STAGE_INST_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `STATE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `PARENT_TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                   `DESCRIPTION_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                   `OWNER_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `ASSIGNEE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `START_TIME_` datetime(3) NOT NULL,
                                   `IN_PROGRESS_TIME_` datetime(3) DEFAULT NULL,
                                   `IN_PROGRESS_STARTED_BY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `CLAIM_TIME_` datetime(3) DEFAULT NULL,
                                   `CLAIMED_BY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `SUSPENDED_TIME_` datetime(3) DEFAULT NULL,
                                   `SUSPENDED_BY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `END_TIME_` datetime(3) DEFAULT NULL,
                                   `COMPLETED_BY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `DURATION_` bigint DEFAULT NULL,
                                   `DELETE_REASON_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                   `PRIORITY_` int DEFAULT NULL,
                                   `IN_PROGRESS_DUE_DATE_` datetime(3) DEFAULT NULL,
                                   `DUE_DATE_` datetime(3) DEFAULT NULL,
                                   `FORM_KEY_` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
                                   `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
                                   `LAST_UPDATED_TIME_` datetime(3) DEFAULT NULL,
                                   PRIMARY KEY (`ID_`),
                                   KEY `ACT_IDX_HI_TASK_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`),
                                   KEY `ACT_IDX_HI_TASK_SUB_SCOPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`),
                                   KEY `ACT_IDX_HI_TASK_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`),
                                   KEY `ACT_IDX_HI_TASK_INST_PROCINST` (`PROC_INST_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_TSK_LOG
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_TSK_LOG`;
CREATE TABLE `ACT_HI_TSK_LOG` (
                                  `ID_` bigint NOT NULL AUTO_INCREMENT,
                                  `TYPE_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                  `TASK_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                  `TIME_STAMP_` timestamp(3) NOT NULL,
                                  `USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                  `DATA_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                  `SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                  `SCOPE_DEFINITION_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                  `SUB_SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                  `SCOPE_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
                                  PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_VARINST
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_VARINST`;
CREATE TABLE `ACT_HI_VARINST` (
                                  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                  `REV_` int DEFAULT '1',
                                  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                  `NAME_` varchar(255) COLLATE utf8_bin NOT NULL,
                                  `VAR_TYPE_` varchar(100) COLLATE utf8_bin DEFAULT NULL,
                                  `SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                  `SUB_SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                  `SCOPE_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                  `BYTEARRAY_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                  `DOUBLE_` double DEFAULT NULL,
                                  `LONG_` bigint DEFAULT NULL,
                                  `TEXT_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                  `TEXT2_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                  `META_INFO_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                  `CREATE_TIME_` datetime(3) DEFAULT NULL,
                                  `LAST_UPDATED_TIME_` datetime(3) DEFAULT NULL,
                                  PRIMARY KEY (`ID_`),
                                  KEY `ACT_IDX_HI_PROCVAR_NAME_TYPE` (`NAME_`,`VAR_TYPE_`),
                                  KEY `ACT_IDX_HI_VAR_SCOPE_ID_TYPE` (`SCOPE_ID_`,`SCOPE_TYPE_`),
                                  KEY `ACT_IDX_HI_VAR_SUB_ID_TYPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`),
                                  KEY `ACT_IDX_HI_PROCVAR_PROC_INST` (`PROC_INST_ID_`),
                                  KEY `ACT_IDX_HI_PROCVAR_TASK_ID` (`TASK_ID_`),
                                  KEY `ACT_IDX_HI_PROCVAR_EXE` (`EXECUTION_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_ID_BYTEARRAY
-- ----------------------------
DROP TABLE IF EXISTS `ACT_ID_BYTEARRAY`;
CREATE TABLE `ACT_ID_BYTEARRAY` (
                                    `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                    `REV_` int DEFAULT NULL,
                                    `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `BYTES_` longblob,
                                    PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_ID_GROUP
-- ----------------------------
DROP TABLE IF EXISTS `ACT_ID_GROUP`;
CREATE TABLE `ACT_ID_GROUP` (
                                `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                `REV_` int DEFAULT NULL,
                                `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                `TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_ID_INFO
-- ----------------------------
DROP TABLE IF EXISTS `ACT_ID_INFO`;
CREATE TABLE `ACT_ID_INFO` (
                               `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                               `REV_` int DEFAULT NULL,
                               `USER_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                               `TYPE_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                               `KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               `VALUE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               `PASSWORD_` longblob,
                               `PARENT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_ID_MEMBERSHIP
-- ----------------------------
DROP TABLE IF EXISTS `ACT_ID_MEMBERSHIP`;
CREATE TABLE `ACT_ID_MEMBERSHIP` (
                                     `USER_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                     `GROUP_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                     PRIMARY KEY (`USER_ID_`,`GROUP_ID_`),
                                     KEY `ACT_FK_MEMB_GROUP` (`GROUP_ID_`),
                                     CONSTRAINT `ACT_FK_MEMB_GROUP` FOREIGN KEY (`GROUP_ID_`) REFERENCES `ACT_ID_GROUP` (`ID_`),
                                     CONSTRAINT `ACT_FK_MEMB_USER` FOREIGN KEY (`USER_ID_`) REFERENCES `ACT_ID_USER` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_ID_PRIV
-- ----------------------------
DROP TABLE IF EXISTS `ACT_ID_PRIV`;
CREATE TABLE `ACT_ID_PRIV` (
                               `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                               `NAME_` varchar(255) COLLATE utf8_bin NOT NULL,
                               PRIMARY KEY (`ID_`),
                               UNIQUE KEY `ACT_UNIQ_PRIV_NAME` (`NAME_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_ID_PRIV_MAPPING
-- ----------------------------
DROP TABLE IF EXISTS `ACT_ID_PRIV_MAPPING`;
CREATE TABLE `ACT_ID_PRIV_MAPPING` (
                                       `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                       `PRIV_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                       `USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `GROUP_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       PRIMARY KEY (`ID_`),
                                       KEY `ACT_FK_PRIV_MAPPING` (`PRIV_ID_`),
                                       KEY `ACT_IDX_PRIV_USER` (`USER_ID_`),
                                       KEY `ACT_IDX_PRIV_GROUP` (`GROUP_ID_`),
                                       CONSTRAINT `ACT_FK_PRIV_MAPPING` FOREIGN KEY (`PRIV_ID_`) REFERENCES `ACT_ID_PRIV` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_ID_PROPERTY
-- ----------------------------
DROP TABLE IF EXISTS `ACT_ID_PROPERTY`;
CREATE TABLE `ACT_ID_PROPERTY` (
                                   `NAME_` varchar(64) COLLATE utf8_bin NOT NULL,
                                   `VALUE_` varchar(300) COLLATE utf8_bin DEFAULT NULL,
                                   `REV_` int DEFAULT NULL,
                                   PRIMARY KEY (`NAME_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_ID_PROPERTY
-- ----------------------------
BEGIN;
INSERT INTO `ACT_ID_PROPERTY` VALUES ('schema.version', '7.0.1.1', 1);
COMMIT;

-- ----------------------------
-- Table structure for ACT_ID_TOKEN
-- ----------------------------
DROP TABLE IF EXISTS `ACT_ID_TOKEN`;
CREATE TABLE `ACT_ID_TOKEN` (
                                `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                `REV_` int DEFAULT NULL,
                                `TOKEN_VALUE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                `TOKEN_DATE_` timestamp(3) NULL DEFAULT NULL,
                                `IP_ADDRESS_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                `USER_AGENT_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                `USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                `TOKEN_DATA_` varchar(2000) COLLATE utf8_bin DEFAULT NULL,
                                PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_ID_USER
-- ----------------------------
DROP TABLE IF EXISTS `ACT_ID_USER`;
CREATE TABLE `ACT_ID_USER` (
                               `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                               `REV_` int DEFAULT NULL,
                               `FIRST_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               `LAST_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               `DISPLAY_NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               `EMAIL_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               `PWD_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               `PICTURE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                               `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
                               PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_PROCDEF_INFO
-- ----------------------------
DROP TABLE IF EXISTS `ACT_PROCDEF_INFO`;
CREATE TABLE `ACT_PROCDEF_INFO` (
                                    `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                    `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                    `REV_` int DEFAULT NULL,
                                    `INFO_JSON_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                    PRIMARY KEY (`ID_`),
                                    UNIQUE KEY `ACT_UNIQ_INFO_PROCDEF` (`PROC_DEF_ID_`),
                                    KEY `ACT_IDX_INFO_PROCDEF` (`PROC_DEF_ID_`),
                                    KEY `ACT_FK_INFO_JSON_BA` (`INFO_JSON_ID_`),
                                    CONSTRAINT `ACT_FK_INFO_JSON_BA` FOREIGN KEY (`INFO_JSON_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
                                    CONSTRAINT `ACT_FK_INFO_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RE_DEPLOYMENT
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RE_DEPLOYMENT`;
CREATE TABLE `ACT_RE_DEPLOYMENT` (
                                     `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                     `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
                                     `DEPLOY_TIME_` timestamp(3) NULL DEFAULT NULL,
                                     `DERIVED_FROM_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                     `DERIVED_FROM_ROOT_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                     `PARENT_DEPLOYMENT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `ENGINE_VERSION_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RE_MODEL
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RE_MODEL`;
CREATE TABLE `ACT_RE_MODEL` (
                                `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                `REV_` int DEFAULT NULL,
                                `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                `KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
                                `LAST_UPDATE_TIME_` timestamp(3) NULL DEFAULT NULL,
                                `VERSION_` int DEFAULT NULL,
                                `META_INFO_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                `DEPLOYMENT_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                `EDITOR_SOURCE_VALUE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                `EDITOR_SOURCE_EXTRA_VALUE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
                                PRIMARY KEY (`ID_`),
                                KEY `ACT_FK_MODEL_SOURCE` (`EDITOR_SOURCE_VALUE_ID_`),
                                KEY `ACT_FK_MODEL_SOURCE_EXTRA` (`EDITOR_SOURCE_EXTRA_VALUE_ID_`),
                                KEY `ACT_FK_MODEL_DEPLOYMENT` (`DEPLOYMENT_ID_`),
                                CONSTRAINT `ACT_FK_MODEL_DEPLOYMENT` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `ACT_RE_DEPLOYMENT` (`ID_`),
                                CONSTRAINT `ACT_FK_MODEL_SOURCE` FOREIGN KEY (`EDITOR_SOURCE_VALUE_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
                                CONSTRAINT `ACT_FK_MODEL_SOURCE_EXTRA` FOREIGN KEY (`EDITOR_SOURCE_EXTRA_VALUE_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RE_PROCDEF
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RE_PROCDEF`;
CREATE TABLE `ACT_RE_PROCDEF` (
                                  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                  `REV_` int DEFAULT NULL,
                                  `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                  `KEY_` varchar(255) COLLATE utf8_bin NOT NULL,
                                  `VERSION_` int NOT NULL,
                                  `DEPLOYMENT_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                  `RESOURCE_NAME_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                  `DGRM_RESOURCE_NAME_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                  `DESCRIPTION_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                  `HAS_START_FORM_KEY_` tinyint DEFAULT NULL,
                                  `HAS_GRAPHICAL_NOTATION_` tinyint DEFAULT NULL,
                                  `SUSPENSION_STATE_` int DEFAULT NULL,
                                  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
                                  `ENGINE_VERSION_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                  `DERIVED_FROM_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                  `DERIVED_FROM_ROOT_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                  `DERIVED_VERSION_` int NOT NULL DEFAULT '0',
                                  PRIMARY KEY (`ID_`),
                                  UNIQUE KEY `ACT_UNIQ_PROCDEF` (`KEY_`,`VERSION_`,`DERIVED_VERSION_`,`TENANT_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_ACTINST
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_ACTINST`;
CREATE TABLE `ACT_RU_ACTINST` (
                                  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                  `REV_` int DEFAULT '1',
                                  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                  `ACT_ID_` varchar(255) COLLATE utf8_bin NOT NULL,
                                  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                  `CALL_PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                  `ACT_NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                  `ACT_TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
                                  `ASSIGNEE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                  `START_TIME_` datetime(3) NOT NULL,
                                  `END_TIME_` datetime(3) DEFAULT NULL,
                                  `DURATION_` bigint DEFAULT NULL,
                                  `TRANSACTION_ORDER_` int DEFAULT NULL,
                                  `DELETE_REASON_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
                                  PRIMARY KEY (`ID_`),
                                  KEY `ACT_IDX_RU_ACTI_START` (`START_TIME_`),
                                  KEY `ACT_IDX_RU_ACTI_END` (`END_TIME_`),
                                  KEY `ACT_IDX_RU_ACTI_PROC` (`PROC_INST_ID_`),
                                  KEY `ACT_IDX_RU_ACTI_PROC_ACT` (`PROC_INST_ID_`,`ACT_ID_`),
                                  KEY `ACT_IDX_RU_ACTI_EXEC` (`EXECUTION_ID_`),
                                  KEY `ACT_IDX_RU_ACTI_EXEC_ACT` (`EXECUTION_ID_`,`ACT_ID_`),
                                  KEY `ACT_IDX_RU_ACTI_TASK` (`TASK_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_DEADLETTER_JOB
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_DEADLETTER_JOB`;
CREATE TABLE `ACT_RU_DEADLETTER_JOB` (
                                         `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                         `REV_` int DEFAULT NULL,
                                         `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                         `TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
                                         `EXCLUSIVE_` tinyint(1) DEFAULT NULL,
                                         `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                         `PROCESS_INSTANCE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                         `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                         `ELEMENT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                         `ELEMENT_NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                         `SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                         `SUB_SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                         `SCOPE_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                         `SCOPE_DEFINITION_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                         `CORRELATION_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                         `EXCEPTION_STACK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                         `EXCEPTION_MSG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                         `DUEDATE_` timestamp(3) NULL DEFAULT NULL,
                                         `REPEAT_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                         `HANDLER_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                         `HANDLER_CFG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                         `CUSTOM_VALUES_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                         `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
                                         `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
                                         PRIMARY KEY (`ID_`),
                                         KEY `ACT_IDX_DEADLETTER_JOB_EXCEPTION_STACK_ID` (`EXCEPTION_STACK_ID_`),
                                         KEY `ACT_IDX_DEADLETTER_JOB_CUSTOM_VALUES_ID` (`CUSTOM_VALUES_ID_`),
                                         KEY `ACT_IDX_DEADLETTER_JOB_CORRELATION_ID` (`CORRELATION_ID_`),
                                         KEY `ACT_IDX_DJOB_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`),
                                         KEY `ACT_IDX_DJOB_SUB_SCOPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`),
                                         KEY `ACT_IDX_DJOB_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`),
                                         KEY `ACT_FK_DEADLETTER_JOB_EXECUTION` (`EXECUTION_ID_`),
                                         KEY `ACT_FK_DEADLETTER_JOB_PROCESS_INSTANCE` (`PROCESS_INSTANCE_ID_`),
                                         KEY `ACT_FK_DEADLETTER_JOB_PROC_DEF` (`PROC_DEF_ID_`),
                                         CONSTRAINT `ACT_FK_DEADLETTER_JOB_CUSTOM_VALUES` FOREIGN KEY (`CUSTOM_VALUES_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
                                         CONSTRAINT `ACT_FK_DEADLETTER_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
                                         CONSTRAINT `ACT_FK_DEADLETTER_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
                                         CONSTRAINT `ACT_FK_DEADLETTER_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`),
                                         CONSTRAINT `ACT_FK_DEADLETTER_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_ENTITYLINK
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_ENTITYLINK`;
CREATE TABLE `ACT_RU_ENTITYLINK` (
                                     `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                     `REV_` int DEFAULT NULL,
                                     `CREATE_TIME_` datetime(3) DEFAULT NULL,
                                     `LINK_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `SUB_SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `SCOPE_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `SCOPE_DEFINITION_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `PARENT_ELEMENT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `REF_SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `REF_SCOPE_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `REF_SCOPE_DEFINITION_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `ROOT_SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `ROOT_SCOPE_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `HIERARCHY_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     PRIMARY KEY (`ID_`),
                                     KEY `ACT_IDX_ENT_LNK_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`,`LINK_TYPE_`),
                                     KEY `ACT_IDX_ENT_LNK_REF_SCOPE` (`REF_SCOPE_ID_`,`REF_SCOPE_TYPE_`,`LINK_TYPE_`),
                                     KEY `ACT_IDX_ENT_LNK_ROOT_SCOPE` (`ROOT_SCOPE_ID_`,`ROOT_SCOPE_TYPE_`,`LINK_TYPE_`),
                                     KEY `ACT_IDX_ENT_LNK_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`,`LINK_TYPE_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_EVENT_SUBSCR
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_EVENT_SUBSCR`;
CREATE TABLE `ACT_RU_EVENT_SUBSCR` (
                                       `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                       `REV_` int DEFAULT NULL,
                                       `EVENT_TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
                                       `EVENT_NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                       `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                       `ACTIVITY_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                       `CONFIGURATION_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `CREATED_` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
                                       `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                       `SUB_SCOPE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                       `SCOPE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                       `SCOPE_DEFINITION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                       `SCOPE_DEFINITION_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `SCOPE_TYPE_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                       `LOCK_TIME_` timestamp(3) NULL DEFAULT NULL,
                                       `LOCK_OWNER_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
                                       PRIMARY KEY (`ID_`),
                                       KEY `ACT_IDX_EVENT_SUBSCR_CONFIG_` (`CONFIGURATION_`),
                                       KEY `ACT_IDX_EVENT_SUBSCR_SCOPEREF_` (`SCOPE_ID_`,`SCOPE_TYPE_`),
                                       KEY `ACT_FK_EVENT_EXEC` (`EXECUTION_ID_`),
                                       CONSTRAINT `ACT_FK_EVENT_EXEC` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_EXECUTION
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_EXECUTION`;
CREATE TABLE `ACT_RU_EXECUTION` (
                                    `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                    `REV_` int DEFAULT NULL,
                                    `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                    `BUSINESS_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `PARENT_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                    `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                    `SUPER_EXEC_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                    `ROOT_PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                    `ACT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `IS_ACTIVE_` tinyint DEFAULT NULL,
                                    `IS_CONCURRENT_` tinyint DEFAULT NULL,
                                    `IS_SCOPE_` tinyint DEFAULT NULL,
                                    `IS_EVENT_SCOPE_` tinyint DEFAULT NULL,
                                    `IS_MI_ROOT_` tinyint DEFAULT NULL,
                                    `SUSPENSION_STATE_` int DEFAULT NULL,
                                    `CACHED_ENT_STATE_` int DEFAULT NULL,
                                    `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
                                    `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `START_ACT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `START_TIME_` datetime(3) DEFAULT NULL,
                                    `START_USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `LOCK_TIME_` timestamp(3) NULL DEFAULT NULL,
                                    `LOCK_OWNER_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `IS_COUNT_ENABLED_` tinyint DEFAULT NULL,
                                    `EVT_SUBSCR_COUNT_` int DEFAULT NULL,
                                    `TASK_COUNT_` int DEFAULT NULL,
                                    `JOB_COUNT_` int DEFAULT NULL,
                                    `TIMER_JOB_COUNT_` int DEFAULT NULL,
                                    `SUSP_JOB_COUNT_` int DEFAULT NULL,
                                    `DEADLETTER_JOB_COUNT_` int DEFAULT NULL,
                                    `EXTERNAL_WORKER_JOB_COUNT_` int DEFAULT NULL,
                                    `VAR_COUNT_` int DEFAULT NULL,
                                    `ID_LINK_COUNT_` int DEFAULT NULL,
                                    `CALLBACK_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `CALLBACK_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `REFERENCE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `REFERENCE_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `PROPAGATED_STAGE_INST_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `BUSINESS_STATUS_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    PRIMARY KEY (`ID_`),
                                    KEY `ACT_IDX_EXEC_BUSKEY` (`BUSINESS_KEY_`),
                                    KEY `ACT_IDC_EXEC_ROOT` (`ROOT_PROC_INST_ID_`),
                                    KEY `ACT_IDX_EXEC_REF_ID_` (`REFERENCE_ID_`),
                                    KEY `ACT_FK_EXE_PROCINST` (`PROC_INST_ID_`),
                                    KEY `ACT_FK_EXE_PARENT` (`PARENT_ID_`),
                                    KEY `ACT_FK_EXE_SUPER` (`SUPER_EXEC_`),
                                    KEY `ACT_FK_EXE_PROCDEF` (`PROC_DEF_ID_`),
                                    CONSTRAINT `ACT_FK_EXE_PARENT` FOREIGN KEY (`PARENT_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`) ON DELETE CASCADE,
                                    CONSTRAINT `ACT_FK_EXE_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`),
                                    CONSTRAINT `ACT_FK_EXE_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`) ON DELETE CASCADE ON UPDATE CASCADE,
                                    CONSTRAINT `ACT_FK_EXE_SUPER` FOREIGN KEY (`SUPER_EXEC_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_EXTERNAL_JOB
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_EXTERNAL_JOB`;
CREATE TABLE `ACT_RU_EXTERNAL_JOB` (
                                       `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                       `REV_` int DEFAULT NULL,
                                       `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
                                       `LOCK_EXP_TIME_` timestamp(3) NULL DEFAULT NULL,
                                       `LOCK_OWNER_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `EXCLUSIVE_` tinyint(1) DEFAULT NULL,
                                       `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                       `PROCESS_INSTANCE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                       `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                       `ELEMENT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `ELEMENT_NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `SUB_SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `SCOPE_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `SCOPE_DEFINITION_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `CORRELATION_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `RETRIES_` int DEFAULT NULL,
                                       `EXCEPTION_STACK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                       `EXCEPTION_MSG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                       `DUEDATE_` timestamp(3) NULL DEFAULT NULL,
                                       `REPEAT_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `HANDLER_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `HANDLER_CFG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                       `CUSTOM_VALUES_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                       `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
                                       `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
                                       PRIMARY KEY (`ID_`),
                                       KEY `ACT_IDX_EXTERNAL_JOB_EXCEPTION_STACK_ID` (`EXCEPTION_STACK_ID_`),
                                       KEY `ACT_IDX_EXTERNAL_JOB_CUSTOM_VALUES_ID` (`CUSTOM_VALUES_ID_`),
                                       KEY `ACT_IDX_EXTERNAL_JOB_CORRELATION_ID` (`CORRELATION_ID_`),
                                       KEY `ACT_IDX_EJOB_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`),
                                       KEY `ACT_IDX_EJOB_SUB_SCOPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`),
                                       KEY `ACT_IDX_EJOB_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`),
                                       CONSTRAINT `ACT_FK_EXTERNAL_JOB_CUSTOM_VALUES` FOREIGN KEY (`CUSTOM_VALUES_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
                                       CONSTRAINT `ACT_FK_EXTERNAL_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_HISTORY_JOB
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_HISTORY_JOB`;
CREATE TABLE `ACT_RU_HISTORY_JOB` (
                                      `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                      `REV_` int DEFAULT NULL,
                                      `LOCK_EXP_TIME_` timestamp(3) NULL DEFAULT NULL,
                                      `LOCK_OWNER_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                      `RETRIES_` int DEFAULT NULL,
                                      `EXCEPTION_STACK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                      `EXCEPTION_MSG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                      `HANDLER_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                      `HANDLER_CFG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                      `CUSTOM_VALUES_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                      `ADV_HANDLER_CFG_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                      `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
                                      `SCOPE_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                      `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
                                      PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_IDENTITYLINK
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_IDENTITYLINK`;
CREATE TABLE `ACT_RU_IDENTITYLINK` (
                                       `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                       `REV_` int DEFAULT NULL,
                                       `GROUP_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                       `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                       `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                       `SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `SUB_SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `SCOPE_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       `SCOPE_DEFINITION_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                       PRIMARY KEY (`ID_`),
                                       KEY `ACT_IDX_IDENT_LNK_USER` (`USER_ID_`),
                                       KEY `ACT_IDX_IDENT_LNK_GROUP` (`GROUP_ID_`),
                                       KEY `ACT_IDX_IDENT_LNK_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`),
                                       KEY `ACT_IDX_IDENT_LNK_SUB_SCOPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`),
                                       KEY `ACT_IDX_IDENT_LNK_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`),
                                       KEY `ACT_IDX_ATHRZ_PROCEDEF` (`PROC_DEF_ID_`),
                                       KEY `ACT_FK_TSKASS_TASK` (`TASK_ID_`),
                                       KEY `ACT_FK_IDL_PROCINST` (`PROC_INST_ID_`),
                                       CONSTRAINT `ACT_FK_ATHRZ_PROCEDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`),
                                       CONSTRAINT `ACT_FK_IDL_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
                                       CONSTRAINT `ACT_FK_TSKASS_TASK` FOREIGN KEY (`TASK_ID_`) REFERENCES `ACT_RU_TASK` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_JOB
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_JOB`;
CREATE TABLE `ACT_RU_JOB` (
                              `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                              `REV_` int DEFAULT NULL,
                              `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                              `TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
                              `LOCK_EXP_TIME_` timestamp(3) NULL DEFAULT NULL,
                              `LOCK_OWNER_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                              `EXCLUSIVE_` tinyint(1) DEFAULT NULL,
                              `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                              `PROCESS_INSTANCE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                              `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                              `ELEMENT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                              `ELEMENT_NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                              `SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                              `SUB_SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                              `SCOPE_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                              `SCOPE_DEFINITION_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                              `CORRELATION_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                              `RETRIES_` int DEFAULT NULL,
                              `EXCEPTION_STACK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                              `EXCEPTION_MSG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                              `DUEDATE_` timestamp(3) NULL DEFAULT NULL,
                              `REPEAT_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                              `HANDLER_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                              `HANDLER_CFG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                              `CUSTOM_VALUES_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                              `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
                              `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
                              PRIMARY KEY (`ID_`),
                              KEY `ACT_IDX_JOB_EXCEPTION_STACK_ID` (`EXCEPTION_STACK_ID_`),
                              KEY `ACT_IDX_JOB_CUSTOM_VALUES_ID` (`CUSTOM_VALUES_ID_`),
                              KEY `ACT_IDX_JOB_CORRELATION_ID` (`CORRELATION_ID_`),
                              KEY `ACT_IDX_JOB_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`),
                              KEY `ACT_IDX_JOB_SUB_SCOPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`),
                              KEY `ACT_IDX_JOB_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`),
                              KEY `ACT_FK_JOB_EXECUTION` (`EXECUTION_ID_`),
                              KEY `ACT_FK_JOB_PROCESS_INSTANCE` (`PROCESS_INSTANCE_ID_`),
                              KEY `ACT_FK_JOB_PROC_DEF` (`PROC_DEF_ID_`),
                              CONSTRAINT `ACT_FK_JOB_CUSTOM_VALUES` FOREIGN KEY (`CUSTOM_VALUES_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
                              CONSTRAINT `ACT_FK_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
                              CONSTRAINT `ACT_FK_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
                              CONSTRAINT `ACT_FK_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`),
                              CONSTRAINT `ACT_FK_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_SUSPENDED_JOB
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_SUSPENDED_JOB`;
CREATE TABLE `ACT_RU_SUSPENDED_JOB` (
                                        `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                        `REV_` int DEFAULT NULL,
                                        `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                        `TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
                                        `EXCLUSIVE_` tinyint(1) DEFAULT NULL,
                                        `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                        `PROCESS_INSTANCE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                        `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                        `ELEMENT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                        `ELEMENT_NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                        `SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                        `SUB_SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                        `SCOPE_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                        `SCOPE_DEFINITION_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                        `CORRELATION_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                        `RETRIES_` int DEFAULT NULL,
                                        `EXCEPTION_STACK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                        `EXCEPTION_MSG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                        `DUEDATE_` timestamp(3) NULL DEFAULT NULL,
                                        `REPEAT_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                        `HANDLER_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                        `HANDLER_CFG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                        `CUSTOM_VALUES_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                        `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
                                        `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
                                        PRIMARY KEY (`ID_`),
                                        KEY `ACT_IDX_SUSPENDED_JOB_EXCEPTION_STACK_ID` (`EXCEPTION_STACK_ID_`),
                                        KEY `ACT_IDX_SUSPENDED_JOB_CUSTOM_VALUES_ID` (`CUSTOM_VALUES_ID_`),
                                        KEY `ACT_IDX_SUSPENDED_JOB_CORRELATION_ID` (`CORRELATION_ID_`),
                                        KEY `ACT_IDX_SJOB_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`),
                                        KEY `ACT_IDX_SJOB_SUB_SCOPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`),
                                        KEY `ACT_IDX_SJOB_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`),
                                        KEY `ACT_FK_SUSPENDED_JOB_EXECUTION` (`EXECUTION_ID_`),
                                        KEY `ACT_FK_SUSPENDED_JOB_PROCESS_INSTANCE` (`PROCESS_INSTANCE_ID_`),
                                        KEY `ACT_FK_SUSPENDED_JOB_PROC_DEF` (`PROC_DEF_ID_`),
                                        CONSTRAINT `ACT_FK_SUSPENDED_JOB_CUSTOM_VALUES` FOREIGN KEY (`CUSTOM_VALUES_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
                                        CONSTRAINT `ACT_FK_SUSPENDED_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
                                        CONSTRAINT `ACT_FK_SUSPENDED_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
                                        CONSTRAINT `ACT_FK_SUSPENDED_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`),
                                        CONSTRAINT `ACT_FK_SUSPENDED_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_TASK
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_TASK`;
CREATE TABLE `ACT_RU_TASK` (
                               `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                               `REV_` int DEFAULT NULL,
                               `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                               `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                               `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                               `TASK_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                               `SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               `SUB_SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               `SCOPE_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               `SCOPE_DEFINITION_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               `PROPAGATED_STAGE_INST_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               `STATE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               `PARENT_TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                               `DESCRIPTION_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                               `TASK_DEF_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               `OWNER_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               `ASSIGNEE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               `DELEGATION_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                               `PRIORITY_` int DEFAULT NULL,
                               `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
                               `IN_PROGRESS_TIME_` datetime(3) DEFAULT NULL,
                               `IN_PROGRESS_STARTED_BY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               `CLAIM_TIME_` datetime(3) DEFAULT NULL,
                               `CLAIMED_BY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               `SUSPENDED_TIME_` datetime(3) DEFAULT NULL,
                               `SUSPENDED_BY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               `IN_PROGRESS_DUE_DATE_` datetime(3) DEFAULT NULL,
                               `DUE_DATE_` datetime(3) DEFAULT NULL,
                               `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                               `SUSPENSION_STATE_` int DEFAULT NULL,
                               `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
                               `FORM_KEY_` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
                               `IS_COUNT_ENABLED_` tinyint DEFAULT NULL,
                               `VAR_COUNT_` int DEFAULT NULL,
                               `ID_LINK_COUNT_` int DEFAULT NULL,
                               `SUB_TASK_COUNT_` int DEFAULT NULL,
                               PRIMARY KEY (`ID_`),
                               KEY `ACT_IDX_TASK_CREATE` (`CREATE_TIME_`),
                               KEY `ACT_IDX_TASK_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`),
                               KEY `ACT_IDX_TASK_SUB_SCOPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`),
                               KEY `ACT_IDX_TASK_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`),
                               KEY `ACT_FK_TASK_EXE` (`EXECUTION_ID_`),
                               KEY `ACT_FK_TASK_PROCINST` (`PROC_INST_ID_`),
                               KEY `ACT_FK_TASK_PROCDEF` (`PROC_DEF_ID_`),
                               CONSTRAINT `ACT_FK_TASK_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
                               CONSTRAINT `ACT_FK_TASK_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`),
                               CONSTRAINT `ACT_FK_TASK_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_TIMER_JOB
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_TIMER_JOB`;
CREATE TABLE `ACT_RU_TIMER_JOB` (
                                    `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                    `REV_` int DEFAULT NULL,
                                    `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
                                    `LOCK_EXP_TIME_` timestamp(3) NULL DEFAULT NULL,
                                    `LOCK_OWNER_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `EXCLUSIVE_` tinyint(1) DEFAULT NULL,
                                    `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                    `PROCESS_INSTANCE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                    `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                    `ELEMENT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `ELEMENT_NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `SUB_SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `SCOPE_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `SCOPE_DEFINITION_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `CORRELATION_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `RETRIES_` int DEFAULT NULL,
                                    `EXCEPTION_STACK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                    `EXCEPTION_MSG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                    `DUEDATE_` timestamp(3) NULL DEFAULT NULL,
                                    `REPEAT_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `HANDLER_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                    `HANDLER_CFG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                    `CUSTOM_VALUES_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                    `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
                                    `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
                                    PRIMARY KEY (`ID_`),
                                    KEY `ACT_IDX_TIMER_JOB_EXCEPTION_STACK_ID` (`EXCEPTION_STACK_ID_`),
                                    KEY `ACT_IDX_TIMER_JOB_CUSTOM_VALUES_ID` (`CUSTOM_VALUES_ID_`),
                                    KEY `ACT_IDX_TIMER_JOB_CORRELATION_ID` (`CORRELATION_ID_`),
                                    KEY `ACT_IDX_TIMER_JOB_DUEDATE` (`DUEDATE_`),
                                    KEY `ACT_IDX_TJOB_SCOPE` (`SCOPE_ID_`,`SCOPE_TYPE_`),
                                    KEY `ACT_IDX_TJOB_SUB_SCOPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`),
                                    KEY `ACT_IDX_TJOB_SCOPE_DEF` (`SCOPE_DEFINITION_ID_`,`SCOPE_TYPE_`),
                                    KEY `ACT_FK_TIMER_JOB_EXECUTION` (`EXECUTION_ID_`),
                                    KEY `ACT_FK_TIMER_JOB_PROCESS_INSTANCE` (`PROCESS_INSTANCE_ID_`),
                                    KEY `ACT_FK_TIMER_JOB_PROC_DEF` (`PROC_DEF_ID_`),
                                    CONSTRAINT `ACT_FK_TIMER_JOB_CUSTOM_VALUES` FOREIGN KEY (`CUSTOM_VALUES_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
                                    CONSTRAINT `ACT_FK_TIMER_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
                                    CONSTRAINT `ACT_FK_TIMER_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
                                    CONSTRAINT `ACT_FK_TIMER_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`),
                                    CONSTRAINT `ACT_FK_TIMER_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_VARIABLE
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_VARIABLE`;
CREATE TABLE `ACT_RU_VARIABLE` (
                                   `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                   `REV_` int DEFAULT NULL,
                                   `TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
                                   `NAME_` varchar(255) COLLATE utf8_bin NOT NULL,
                                   `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                   `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                   `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                   `SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `SUB_SCOPE_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `SCOPE_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `BYTEARRAY_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                   `DOUBLE_` double DEFAULT NULL,
                                   `LONG_` bigint DEFAULT NULL,
                                   `TEXT_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                   `TEXT2_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                   `META_INFO_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
                                   PRIMARY KEY (`ID_`),
                                   KEY `ACT_IDX_RU_VAR_SCOPE_ID_TYPE` (`SCOPE_ID_`,`SCOPE_TYPE_`),
                                   KEY `ACT_IDX_RU_VAR_SUB_ID_TYPE` (`SUB_SCOPE_ID_`,`SCOPE_TYPE_`),
                                   KEY `ACT_FK_VAR_BYTEARRAY` (`BYTEARRAY_ID_`),
                                   KEY `ACT_IDX_VARIABLE_TASK_ID` (`TASK_ID_`),
                                   KEY `ACT_FK_VAR_EXE` (`EXECUTION_ID_`),
                                   KEY `ACT_FK_VAR_PROCINST` (`PROC_INST_ID_`),
                                   CONSTRAINT `ACT_FK_VAR_BYTEARRAY` FOREIGN KEY (`BYTEARRAY_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
                                   CONSTRAINT `ACT_FK_VAR_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
                                   CONSTRAINT `ACT_FK_VAR_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for FLW_CHANNEL_DEFINITION
-- ----------------------------
DROP TABLE IF EXISTS `FLW_CHANNEL_DEFINITION`;
CREATE TABLE `FLW_CHANNEL_DEFINITION` (
                                          `ID_` varchar(255) COLLATE utf8mb4_bin NOT NULL,
                                          `NAME_` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                          `VERSION_` int DEFAULT NULL,
                                          `KEY_` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                          `CATEGORY_` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                          `DEPLOYMENT_ID_` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                          `CREATE_TIME_` datetime(3) DEFAULT NULL,
                                          `TENANT_ID_` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                          `RESOURCE_NAME_` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                          `DESCRIPTION_` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                          `TYPE_` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                          `IMPLEMENTATION_` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                          PRIMARY KEY (`ID_`),
                                          UNIQUE KEY `ACT_IDX_CHANNEL_DEF_UNIQ` (`KEY_`,`VERSION_`,`TENANT_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for FLW_EV_DATABASECHANGELOG
-- ----------------------------
DROP TABLE IF EXISTS `FLW_EV_DATABASECHANGELOG`;
CREATE TABLE `FLW_EV_DATABASECHANGELOG` (
                                            `ID` varchar(255) COLLATE utf8mb4_bin NOT NULL,
                                            `AUTHOR` varchar(255) COLLATE utf8mb4_bin NOT NULL,
                                            `FILENAME` varchar(255) COLLATE utf8mb4_bin NOT NULL,
                                            `DATEEXECUTED` datetime NOT NULL,
                                            `ORDEREXECUTED` int NOT NULL,
                                            `EXECTYPE` varchar(10) COLLATE utf8mb4_bin NOT NULL,
                                            `MD5SUM` varchar(35) COLLATE utf8mb4_bin DEFAULT NULL,
                                            `DESCRIPTION` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                            `COMMENTS` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                            `TAG` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                            `LIQUIBASE` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL,
                                            `CONTEXTS` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                            `LABELS` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                            `DEPLOYMENT_ID` varchar(10) COLLATE utf8mb4_bin DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of FLW_EV_DATABASECHANGELOG
-- ----------------------------
BEGIN;
INSERT INTO `FLW_EV_DATABASECHANGELOG` VALUES ('1', 'flowable', 'org/flowable/eventregistry/db/liquibase/flowable-eventregistry-db-changelog.xml', '2024-01-23 14:13:11', 1, 'EXECUTED', '8:1b0c48c9cf7945be799d868a2626d687', 'createTable tableName=FLW_EVENT_DEPLOYMENT; createTable tableName=FLW_EVENT_RESOURCE; createTable tableName=FLW_EVENT_DEFINITION; createIndex indexName=ACT_IDX_EVENT_DEF_UNIQ, tableName=FLW_EVENT_DEFINITION; createTable tableName=FLW_CHANNEL_DEFIN...', '', NULL, '4.20.0', NULL, NULL, '5990391167');
INSERT INTO `FLW_EV_DATABASECHANGELOG` VALUES ('2', 'flowable', 'org/flowable/eventregistry/db/liquibase/flowable-eventregistry-db-changelog.xml', '2024-01-23 14:13:11', 2, 'EXECUTED', '8:0ea825feb8e470558f0b5754352b9cda', 'addColumn tableName=FLW_CHANNEL_DEFINITION; addColumn tableName=FLW_CHANNEL_DEFINITION', '', NULL, '4.20.0', NULL, NULL, '5990391167');
INSERT INTO `FLW_EV_DATABASECHANGELOG` VALUES ('3', 'flowable', 'org/flowable/eventregistry/db/liquibase/flowable-eventregistry-db-changelog.xml', '2024-01-23 14:13:11', 3, 'EXECUTED', '8:3c2bb293350b5cbe6504331980c9dcee', 'customChange', '', NULL, '4.20.0', NULL, NULL, '5990391167');
COMMIT;

-- ----------------------------
-- Table structure for FLW_EV_DATABASECHANGELOGLOCK
-- ----------------------------
DROP TABLE IF EXISTS `FLW_EV_DATABASECHANGELOGLOCK`;
CREATE TABLE `FLW_EV_DATABASECHANGELOGLOCK` (
                                                `ID` int NOT NULL,
                                                `LOCKED` bit(1) NOT NULL,
                                                `LOCKGRANTED` datetime DEFAULT NULL,
                                                `LOCKEDBY` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                                PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of FLW_EV_DATABASECHANGELOGLOCK
-- ----------------------------
BEGIN;
INSERT INTO `FLW_EV_DATABASECHANGELOGLOCK` VALUES (1, b'0', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for FLW_EVENT_DEFINITION
-- ----------------------------
DROP TABLE IF EXISTS `FLW_EVENT_DEFINITION`;
CREATE TABLE `FLW_EVENT_DEFINITION` (
                                        `ID_` varchar(255) COLLATE utf8mb4_bin NOT NULL,
                                        `NAME_` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                        `VERSION_` int DEFAULT NULL,
                                        `KEY_` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                        `CATEGORY_` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                        `DEPLOYMENT_ID_` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                        `TENANT_ID_` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                        `RESOURCE_NAME_` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                        `DESCRIPTION_` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                        PRIMARY KEY (`ID_`),
                                        UNIQUE KEY `ACT_IDX_EVENT_DEF_UNIQ` (`KEY_`,`VERSION_`,`TENANT_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for FLW_EVENT_DEPLOYMENT
-- ----------------------------
DROP TABLE IF EXISTS `FLW_EVENT_DEPLOYMENT`;
CREATE TABLE `FLW_EVENT_DEPLOYMENT` (
                                        `ID_` varchar(255) COLLATE utf8mb4_bin NOT NULL,
                                        `NAME_` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                        `CATEGORY_` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                        `DEPLOY_TIME_` datetime(3) DEFAULT NULL,
                                        `TENANT_ID_` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                        `PARENT_DEPLOYMENT_ID_` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                        PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for FLW_EVENT_RESOURCE
-- ----------------------------
DROP TABLE IF EXISTS `FLW_EVENT_RESOURCE`;
CREATE TABLE `FLW_EVENT_RESOURCE` (
                                      `ID_` varchar(255) COLLATE utf8mb4_bin NOT NULL,
                                      `NAME_` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                      `DEPLOYMENT_ID_` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                      `RESOURCE_BYTES_` longblob,
                                      PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for FLW_RU_BATCH
-- ----------------------------
DROP TABLE IF EXISTS `FLW_RU_BATCH`;
CREATE TABLE `FLW_RU_BATCH` (
                                `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                `REV_` int DEFAULT NULL,
                                `TYPE_` varchar(64) COLLATE utf8_bin NOT NULL,
                                `SEARCH_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                `SEARCH_KEY2_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                `CREATE_TIME_` datetime(3) NOT NULL,
                                `COMPLETE_TIME_` datetime(3) DEFAULT NULL,
                                `STATUS_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                `BATCH_DOC_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
                                PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for FLW_RU_BATCH_PART
-- ----------------------------
DROP TABLE IF EXISTS `FLW_RU_BATCH_PART`;
CREATE TABLE `FLW_RU_BATCH_PART` (
                                     `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
                                     `REV_` int DEFAULT NULL,
                                     `BATCH_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                     `TYPE_` varchar(64) COLLATE utf8_bin NOT NULL,
                                     `SCOPE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                     `SUB_SCOPE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                     `SCOPE_TYPE_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                     `SEARCH_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `SEARCH_KEY2_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `CREATE_TIME_` datetime(3) NOT NULL,
                                     `COMPLETE_TIME_` datetime(3) DEFAULT NULL,
                                     `STATUS_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                     `RESULT_DOC_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                                     `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
                                     PRIMARY KEY (`ID_`),
                                     KEY `FLW_IDX_BATCH_PART` (`BATCH_ID_`),
                                     CONSTRAINT `FLW_FK_BATCH_PART_PARENT` FOREIGN KEY (`BATCH_ID_`) REFERENCES `FLW_RU_BATCH` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- 流程分类表
-- ----------------------------
DROP TABLE IF EXISTS `zz_flow_category`;
CREATE TABLE `zz_flow_category` (
                                    `category_id` bigint(20) NOT NULL COMMENT '主键Id',
                                    `tenant_id` bigint(20) COMMENT '租户Id',
                                    `app_code` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用编码',
                                    `name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '显示名称',
                                    `code` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '分类编码',
                                    `show_order` int(11) NOT NULL COMMENT '实现顺序',
                                    `update_time` datetime NOT NULL COMMENT '更新时间',
                                    `update_user_id` bigint NOT NULL COMMENT '更新者Id',
                                    `create_time` datetime NOT NULL COMMENT '创建时间',
                                    `create_user_id` bigint NOT NULL COMMENT '创建者Id',
                                    PRIMARY KEY (`category_id`) USING BTREE,
                                    KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
                                    KEY `idx_code` (`code`) USING BTREE,
                                    KEY `idx_app_code` (`app_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='流程分类表';

-- ----------------------------
-- 流程管理表
-- ----------------------------
DROP TABLE IF EXISTS `zz_flow_entry`;
CREATE TABLE `zz_flow_entry` (
                                 `entry_id` bigint(20) NOT NULL COMMENT '主键',
                                 `tenant_id` bigint(20) COMMENT '租户Id',
                                 `app_code` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用编码',
                                 `process_definition_name` varchar(200) NOT NULL COMMENT '流程名称',
                                 `process_definition_key` varchar(150) NOT NULL COMMENT '流程标识Key',
                                 `category_id` bigint(20) NOT NULL COMMENT '流程分类',
                                 `main_entry_publish_id` bigint(20) DEFAULT NULL COMMENT '工作流部署的发布主版本Id',
                                 `latest_publish_time` datetime DEFAULT NULL COMMENT '最新发布时间',
                                 `status` int(11) NOT NULL COMMENT '流程状态',
                                 `bpmn_xml` longtext COMMENT '流程定义的xml',
                                 `diagram_type` int(11) NOT NULL COMMENT '流程图类型',
                                 `bind_form_type` int(11) NOT NULL COMMENT '绑定表单类型',
                                 `flow_type` int(11) NOT NULL COMMENT '流程类型',
                                 `auto_param_json` varchar(2000) DEFAULT NULL COMMENT '自动化流程的参数',
                                 `page_id` bigint(20) DEFAULT NULL COMMENT '在线表单的页面Id',
                                 `default_form_id` bigint(20) DEFAULT NULL COMMENT '在线表单Id',
                                 `default_router_name` varchar(255) DEFAULT NULL COMMENT '静态表单的缺省路由名称',
                                 `encoded_rule` varchar(255) DEFAULT NULL COMMENT '工单表编码字段的编码规则',
                                 `extension_data` varchar(3000) DEFAULT NULL COMMENT '流程的自定义扩展数据',
                                 `update_time` datetime NOT NULL COMMENT '更新时间',
                                 `update_user_id` bigint NOT NULL COMMENT '更新者Id',
                                 `create_time` datetime NOT NULL COMMENT '创建时间',
                                 `create_user_id` bigint NOT NULL COMMENT '创建者Id',
                                 PRIMARY KEY (`entry_id`) USING BTREE,
                                 KEY `idx_process_definition_key` (`process_definition_key`) USING BTREE,
                                 KEY `idx_app_code` (`app_code`) USING BTREE,
                                 KEY `idx_category_id` (`category_id`) USING BTREE,
                                 KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
                                 KEY `idx_status` (`status`) USING BTREE,
                                 KEY `idx_flow_type` (`flow_type`) USING BTREE,
                                 KEY `idx_process_definition_name` (`process_definition_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程管理表';

-- ----------------------------
-- 流程发布表
-- ----------------------------
DROP TABLE IF EXISTS `zz_flow_entry_publish`;
CREATE TABLE `zz_flow_entry_publish` (
                                         `entry_publish_id` bigint(20) NOT NULL COMMENT '主键Id',
                                         `entry_id` bigint(20) NOT NULL COMMENT '流程Id',
                                         `process_definition_id` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '流程引擎的定义Id',
                                         `deploy_id` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '流程引擎的部署Id',
                                         `publish_version` int(11) NOT NULL COMMENT '发布版本',
                                         `active_status` bit(1) NOT NULL COMMENT '激活状态',
                                         `main_version` bit(1) NOT NULL COMMENT '是否为主版本',
                                         `extension_data` varchar(3000) DEFAULT NULL COMMENT '流程的自定义扩展数据',
                                         `create_user_id` bigint NOT NULL COMMENT '创建者Id',
                                         `publish_time` datetime NOT NULL COMMENT '发布时间',
                                         `init_task_info` text CHARACTER SET utf8mb4 COMMENT '第一个非开始节点任务的附加信息',
                                         `analyzed_node_json` longtext CHARACTER SET utf8mb4 COMMENT '分析后的节点JSON信息',
                                         PRIMARY KEY (`entry_publish_id`) USING BTREE,
                                         UNIQUE KEY `uk_process_definition_id` (`process_definition_id`) USING BTREE,
                                         KEY `idx_entry_id` (`entry_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='流程发布表';

-- ----------------------------
-- 流程发布变量表
-- ----------------------------
DROP TABLE IF EXISTS `zz_flow_entry_publish_variable`;
CREATE TABLE `zz_flow_entry_publish_variable` (
                                                  `variable_id` bigint(20) NOT NULL COMMENT '主键Id',
                                                  `entry_publish_id` bigint(20) NOT NULL COMMENT '流程Id',
                                                  `variable_name` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '变量名',
                                                  `show_name` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '显示名',
                                                  `variable_type` int(11) NOT NULL COMMENT '变量类型',
                                                  `bind_datasource_id` bigint(20) DEFAULT NULL COMMENT '绑定数据源Id',
                                                  `bind_relation_id` bigint(20) DEFAULT NULL COMMENT '绑定数据源关联Id',
                                                  `bind_column_id` bigint(20) DEFAULT NULL COMMENT '绑定字段Id',
                                                  `builtin` bit(1) NOT NULL COMMENT '是否内置',
                                                  PRIMARY KEY (`variable_id`) USING BTREE,
                                                  KEY `idx_entry_publish_id` (`entry_publish_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='流程发布变量表';

-- ----------------------------
-- 流程变量表
-- ----------------------------
DROP TABLE IF EXISTS `zz_flow_entry_variable`;
CREATE TABLE `zz_flow_entry_variable` (
                                          `variable_id` bigint(20) NOT NULL COMMENT '主键Id',
                                          `entry_id` bigint(20) NOT NULL COMMENT '流程Id',
                                          `variable_name` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '变量名',
                                          `show_name` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '显示名',
                                          `variable_type` int(11) NOT NULL COMMENT '变量类型',
                                          `bind_datasource_id` bigint(20) DEFAULT NULL COMMENT '绑定数据源Id',
                                          `bind_relation_id` bigint(20) DEFAULT NULL COMMENT '绑定数据源关联Id',
                                          `bind_column_id` bigint(20) DEFAULT NULL COMMENT '绑定字段Id',
                                          `builtin` bit(1) NOT NULL COMMENT '是否内置',
                                          `create_time` datetime NOT NULL COMMENT '创建时间',
                                          PRIMARY KEY (`variable_id`) USING BTREE,
                                          UNIQUE KEY `uk_entry_id_variable_name` (`entry_id`,`variable_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='流程变量表';

-- ----------------------------
-- 流程任务审批表
-- ----------------------------
DROP TABLE IF EXISTS `zz_flow_task_comment`;
CREATE TABLE `zz_flow_task_comment` (
                                        `id` bigint(20) NOT NULL COMMENT '主键Id',
                                        `process_instance_id` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '流程实例Id',
                                        `task_id` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '任务Id',
                                        `task_key` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '任务标识',
                                        `task_name` varchar(512) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '任务名称',
                                        `target_task_key` varchar(255) COLLATE utf8mb4_bin COMMENT '目标任务标识',
                                        `execution_id` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '任务的执行Id',
                                        `multi_instance_exec_id` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '会签任务的执行Id',
                                        `approval_type` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '审批类型',
                                        `task_comment` varchar(1024) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '批注内容',
                                        `delegate_assignee` varchar(512) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '委托指定人，比如加签、转办等',
                                        `custom_business_data` longtext COLLATE utf8mb4_bin COMMENT '自定义数据。开发者可自行扩展，推荐使用JSON格式数据',
                                        `head_image_url` varchar(1024) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '审批用户头像',
                                        `create_user_id` bigint COMMENT '创建者Id',
                                        `create_login_name` varchar(255) COLLATE utf8mb4_bin COMMENT '创建者登录名',
                                        `create_username` varchar(255) COLLATE utf8mb4_bin COMMENT '创建者用户名',
                                        `create_time` datetime NOT NULL COMMENT '创建时间',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        KEY `idx_multi_instance_exec_id` (`multi_instance_exec_id`) USING BTREE,
                                        KEY `idx_process_instance_id` (`process_instance_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='流程任务审批表';

-- ----------------------------
-- 流程多实例任务审批流水表
-- ----------------------------
DROP TABLE IF EXISTS `zz_flow_multi_instance_trans`;
CREATE TABLE `zz_flow_multi_instance_trans` (
                                                `id` bigint NOT NULL COMMENT '主键Id',
                                                `process_instance_id` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '流程实例Id',
                                                `task_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '任务Id',
                                                `task_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '任务标识',
                                                `multi_instance_exec_id` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '会签任务的执行Id',
                                                `execution_id` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '任务的执行Id',
                                                `assignee_list` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '会签指派人列表',
                                                `create_user_id` bigint NOT NULL COMMENT '创建者Id',
                                                `create_login_name` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '创建者登录名',
                                                `create_username` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '创建者用户名',
                                                `create_time` datetime NOT NULL COMMENT '创建时间',
                                                PRIMARY KEY (`id`),
                                                UNIQUE KEY `uk_execution_id_task_id` (`execution_id`, `task_id`) USING BTREE,
                                                KEY `idx_multi_instance_exec_id` (`multi_instance_exec_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='流程多实例任务审批流水表';

-- ----------------------------
-- 流程流程图任务扩展表
-- ----------------------------
DROP TABLE IF EXISTS `zz_flow_task_ext`;
CREATE TABLE `zz_flow_task_ext` (
                                    `process_definition_id` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '流程引擎的定义Id',
                                    `task_id` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '流程引擎任务Id',
                                    `operation_list_json` longtext COLLATE utf8mb4_bin COMMENT '操作列表JSON',
                                    `variable_list_json` longtext COLLATE utf8mb4_bin COMMENT '变量列表JSON',
                                    `assignee_list_json` text COLLATE utf8mb4_bin COMMENT '存储多实例的assigneeList的JSON',
                                    `group_type` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '分组类型',
                                    `dept_post_list_json` varchar(1024) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '保存岗位相关的数据',
                                    `role_ids` varchar(1024) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '保存角色Id数据',
                                    `dept_ids` varchar(1024) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '保存部门Id数据',
                                    `candidate_usernames` varchar(4000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '保存候选组用户名数据',
                                    `copy_list_json` varchar(4000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '抄送相关的数据',
                                    `extra_data_json` text COLLATE utf8mb4_bin COMMENT '用户任务的扩展属性，存储为JSON的字符串格式',
                                    `auto_config_json` text COLLATE utf8mb4_bin COMMENT '自动化任务配置数据，存储为JSON的字符串格式',
                                    PRIMARY KEY (`process_definition_id`,`task_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='流程流程图任务扩展表';

-- ----------------------------
-- 流程工单表
-- ----------------------------
DROP TABLE IF EXISTS `zz_flow_work_order`;
CREATE TABLE `zz_flow_work_order` (
                                      `work_order_id` bigint(20) NOT NULL COMMENT '主键Id',
                                      `tenant_id` bigint(20) COMMENT '租户Id',
                                      `app_code` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用编码',
                                      `work_order_code` varchar(255) COLLATE utf8mb4_bin COMMENT '工单编码字段',
                                      `process_definition_key` varchar(128) COLLATE utf8mb4_bin NOT NULL COMMENT '流程定义标识',
                                      `process_definition_name` varchar(200) CHARACTER SET utf8mb4 NOT NULL COMMENT '流程名称',
                                      `process_definition_id` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '流程引擎的定义Id',
                                      `process_instance_id` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '流程实例Id',
                                      `online_table_id` bigint(20) DEFAULT NULL COMMENT '在线表单的主表Id',
                                      `table_name` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用于静态表单的表名',
                                      `business_key` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '业务主键值',
                                      `task_id` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '未完成的任务Id',
                                      `task_name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '未完成的任务名称',
                                      `task_definition_key` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '未完成的任务标识',
                                      `latest_approval_status` int(11) COMMENT '最近的审批状态',
                                      `flow_status` int(11) NOT NULL DEFAULT 0 COMMENT '流程状态',
                                      `submit_username` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '提交用户登录名称',
                                      `dept_id` bigint NOT NULL COMMENT '提交用户所在部门Id',
                                      `update_time` datetime NOT NULL COMMENT '更新时间',
                                      `update_user_id` bigint NOT NULL COMMENT '更新者Id',
                                      `create_time` datetime NOT NULL COMMENT '创建时间',
                                      `create_user_id` bigint NOT NULL COMMENT '创建者Id',
                                      `deleted_flag` int(11) NOT NULL COMMENT '删除标记(1: 正常 -1: 已删除)',
                                      PRIMARY KEY (`work_order_id`) USING BTREE,
                                      UNIQUE KEY `uk_process_instance_id` (`process_instance_id`) USING BTREE,
                                      UNIQUE KEY `uk_work_order_code` (`work_order_code`) USING BTREE,
                                      KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
                                      KEY `idx_app_code` (`app_code`) USING BTREE,
                                      KEY `idx_process_definition_key` (`process_definition_key`) USING BTREE,
                                      KEY `idx_create_user_id` (`create_user_id`) USING BTREE,
                                      KEY `idx_create_time` (`create_time`) USING BTREE,
                                      KEY `idx_dept_id` (`dept_id`) USING BTREE,
                                      KEY `idx_table_name` (`table_name`) USING BTREE,
                                      KEY `idx_business_key` (`business_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='流程工单表';

-- ----------------------------
-- 流程工单扩展表
-- ----------------------------
DROP TABLE IF EXISTS `zz_flow_work_order_ext`;
CREATE TABLE `zz_flow_work_order_ext` (
                                          `id` bigint NOT NULL COMMENT '主键Id',
                                          `work_order_id` bigint NOT NULL COMMENT '工单Id',
                                          `draft_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '草稿数据',
                                          `business_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '业务数据',
                                          `update_time` datetime NOT NULL COMMENT '更新时间',
                                          `update_user_id` bigint NOT NULL COMMENT '更新者Id',
                                          `create_time` datetime NOT NULL COMMENT '创建时间',
                                          `create_user_id` bigint NOT NULL COMMENT '创建者Id',
                                          `deleted_flag` int(11) NOT NULL COMMENT '删除标记(1: 正常 -1: 已删除)',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          KEY `idx_work_order_id` (`work_order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='流程工单扩展表';

-- ----------------------------
-- 流程消息通知表
-- ----------------------------
DROP TABLE IF EXISTS `zz_flow_message`;
CREATE TABLE `zz_flow_message` (
                                   `message_id` bigint(20) NOT NULL COMMENT '主键Id',
                                   `tenant_id` bigint(20) COMMENT '租户Id',
                                   `app_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用Id',
                                   `message_type` tinyint(4) NOT NULL COMMENT '消息类型',
                                   `message_content` varchar(1024) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '消息内容',
                                   `remind_count` int(11) DEFAULT 0 COMMENT '催办次数',
                                   `work_order_id` bigint(20) DEFAULT NULL COMMENT '工单Id',
                                   `process_definition_id` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '流程定义Id',
                                   `process_definition_key` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '流程定义标识',
                                   `process_definition_name` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '流程定义名称',
                                   `process_instance_id` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '流程实例Id',
                                   `process_instance_initiator` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '流程实例发起者',
                                   `task_id` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '流程任务Id',
                                   `task_definition_key` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '流程任务定义标识',
                                   `task_name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '流程任务名称',
                                   `task_start_time` datetime DEFAULT NULL COMMENT '任务开始时间',
                                   `task_finished` bit(1) NOT NULL DEFAULT b'0' COMMENT '任务是否已完成',
                                   `task_assignee` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '任务指派人登录名',
                                   `business_data_shot` longtext COLLATE utf8mb4_bin COMMENT '业务数据快照',
                                   `online_form_data` bit(1) COMMENT '是否为在线表单消息数据',
                                   `update_time` datetime NOT NULL COMMENT '更新时间',
                                   `update_user_id` bigint NOT NULL COMMENT '更新者Id',
                                   `create_time` datetime NOT NULL COMMENT '创建时间',
                                   `create_user_id` bigint NOT NULL COMMENT '创建者Id',
                                   `create_username` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '创建者显示名',
                                   PRIMARY KEY (`message_id`) USING BTREE,
                                   KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
                                   KEY `idx_app_code` (`app_code`) USING BTREE,
                                   KEY `idx_notified_username` (`task_assignee`) USING BTREE,
                                   KEY `idx_process_instance_id` (`process_instance_id`) USING BTREE,
                                   KEY `idx_message_type` (`message_type`) USING BTREE,
                                   KEY `idx_task_id` (`task_id`) USING BTREE,
                                   KEY `idx_task_finished` (`task_finished`) USING BTREE,
                                   KEY `idx_update_time` (`update_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='流程消息通知表';

-- ----------------------------
-- 流程消息通知候选人表
-- ----------------------------
DROP TABLE IF EXISTS `zz_flow_msg_candidate_identity`;
CREATE TABLE `zz_flow_msg_candidate_identity` (
                                                  `id` bigint(20) NOT NULL COMMENT '主键Id',
                                                  `message_id` bigint(20) NOT NULL COMMENT '流程任务Id',
                                                  `candidate_type` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '候选身份类型',
                                                  `candidate_id` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '候选身份Id',
                                                  PRIMARY KEY (`id`),
                                                  KEY `idx_candidate_id` (`candidate_id`) USING BTREE,
                                                  KEY `idx_message_id` (`message_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='流程消息通知候选人表';

-- ----------------------------
-- 流程消息候选人操作表
-- ----------------------------
DROP TABLE IF EXISTS `zz_flow_msg_identity_operation`;
CREATE TABLE `zz_flow_msg_identity_operation` (
                                                  `id` bigint NOT NULL COMMENT '主键Id',
                                                  `message_id` bigint NOT NULL COMMENT '流程任务Id',
                                                  `login_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户登录名',
                                                  `operation_type` int NOT NULL COMMENT '操作类型',
                                                  `operation_time` datetime NOT NULL COMMENT '操作时间',
                                                  PRIMARY KEY (`id`),
                                                  KEY `idx_message_id` (`message_id`) USING BTREE,
                                                  KEY `idx_login_name` (`login_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='流程消息候选人操作表';

-- ----------------------------
-- 流程变量日志表
-- ----------------------------
DROP TABLE IF EXISTS `zz_flow_variable_log`;
CREATE TABLE `zz_flow_variable_log` (
                                        `id` bigint NOT NULL COMMENT '主键Id',
                                        `process_definition_key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '流程定义标识',
                                        `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '流程实例Id',
                                        `task_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '未完成的任务标识',
                                        `variable_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '变量数据',
                                        `create_time` datetime NOT NULL COMMENT '创建时间',
                                        `expired_time` datetime DEFAULT NULL COMMENT '过期时间',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        KEY `idx_process_instance_id` (`process_instance_id`,`task_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='流程变量日志表';

-- ----------------------------
-- 路由表单工作流API请求日志表
-- ----------------------------
DROP TABLE IF EXISTS `zz_flow_api_request_log`;
CREATE TABLE `zz_flow_api_request_log` (
                                           `id` bigint NOT NULL COMMENT '主键Id',
                                           `request_id` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '单次请求Id',
                                           `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '流程实例Id',
                                           `task_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '任务Id',
                                           `task_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '任务标识',
                                           `business_key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '业务主键值',
                                           `create_time` datetime NOT NULL COMMENT '创建时间',
                                           `create_user_id` bigint NOT NULL COMMENT '创建者Id',
                                           PRIMARY KEY (`id`) USING BTREE,
                                           UNIQUE KEY `uk_request_id` (`request_id`) USING BTREE,
                                           KEY `idx_process_instance_id` (`process_instance_id`,`task_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='路由表单工作流API请求日志表';

-- ----------------------------
-- 流程任务超时作业表
-- ----------------------------
DROP TABLE IF EXISTS `zz_flow_task_timeout_job`;
CREATE TABLE `zz_flow_task_timeout_job` (
                                            `id` bigint NOT NULL COMMENT '主键Id',
                                            `process_definition_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '流程定义Id',
                                            `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '流程实例Id',
                                            `task_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '任务标识',
                                            `task_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '任务Id',
                                            `timeout_hours` int NOT NULL COMMENT '超时的小时数',
                                            `handle_way` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '超时处理方式',
                                            `default_assignee` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '超时处理缺省用户名',
                                            `error_message` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '错误信息',
                                            `status` int NOT NULL COMMENT '执行状态',
                                            `exec_time` datetime DEFAULT NULL COMMENT '执行时间',
                                            `create_time` datetime NOT NULL COMMENT '创建时间',
                                            `update_time` datetime NOT NULL COMMENT '更新时间',
                                            PRIMARY KEY (`id`) USING BTREE,
                                            KEY `idx_process_instance_id` (`process_instance_id`) USING BTREE,
                                            KEY `idx_status` (`status`),
                                            KEY `idx_task_id` (`task_id`),
                                            KEY `idx_exec_time` (`exec_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='流程任务超时作业表';

-- ----------------------------
-- 流程处理事务事件生产者流水表
-- ----------------------------
DROP TABLE IF EXISTS `zz_flow_trans_producer`;
CREATE TABLE `zz_flow_trans_producer` (
                                          `trans_id` bigint NOT NULL COMMENT '主键Id',
                                          `app_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用Id',
                                          `dblink_id` bigint COMMENT '数据库链接Id',
                                          `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '流程实例Id',
                                          `execution_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '流程执行实例Id',
                                          `task_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '任务Id',
                                          `task_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '任务标识',
                                          `task_name` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '任务名称',
                                          `task_comment` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '批注内容',
                                          `url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '当前请求的url',
                                          `init_method` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '创建该事务性事件对象的初始方法',
                                          `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '当前请求的traceId',
                                          `sql_data` text COLLATE utf8mb4_bin COMMENT '和SQL操作相关的数据',
                                          `auto_task_config` text COLLATE utf8mb4_bin COMMENT '自动化任务需要执行的数据',
                                          `try_times` int NOT NULL COMMENT '尝试次数',
                                          `error_reason` text COLLATE utf8mb4_bin COMMENT '提交业务数据时的错误信息',
                                          `create_login_name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建者登录名',
                                          `create_username` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建者中文用户名',
                                          `create_time` datetime NOT NULL COMMENT '创建时间',
                                          PRIMARY KEY (`trans_id`) USING BTREE,
                                          KEY `idx_app_code` (`app_code`) USING BTREE,
                                          KEY `idx_process_instance_id` (`process_instance_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- 流程处理事务事件消费者流水表
-- ----------------------------
DROP TABLE IF EXISTS `zz_flow_trans_consumer`;
CREATE TABLE `zz_flow_trans_consumer` (
                                          `trans_id` bigint NOT NULL COMMENT '主键Id',
                                          `create_time` datetime NOT NULL COMMENT '创建时间',
                                          PRIMARY KEY (`trans_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- 自动化流程数据库链接表
-- ----------------------------
DROP TABLE IF EXISTS `zz_flow_dblink`;
CREATE TABLE `zz_flow_dblink` (
                                  `dblink_id` bigint(20) NOT NULL COMMENT '主键Id',
                                  `app_code` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用编码',
                                  `dblink_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '链接中文名称',
                                  `dblink_description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '链接描述',
                                  `dblink_type` int NOT NULL COMMENT '数据源类型',
                                  `configuration` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '配置信息',
                                  `update_time` datetime NOT NULL COMMENT '更新时间',
                                  `update_user_id` bigint NOT NULL COMMENT '更新者',
                                  `create_time` datetime NOT NULL COMMENT '创建时间',
                                  `create_user_id` bigint NOT NULL COMMENT '创建者',
                                  PRIMARY KEY (`dblink_id`),
                                  KEY `idx_dblink_type` (`dblink_type`) USING BTREE,
                                  KEY `idx_app_code` (`app_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='在线表单数据库链接表';

-- ----------------------------
-- 自动化任务变量日志表
-- ----------------------------
DROP TABLE IF EXISTS `zz_flow_auto_variable_log`;
CREATE TABLE `zz_flow_auto_variable_log` (
                                             `id` bigint NOT NULL COMMENT '主键Id',
                                             `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '流程实例Id',
                                             `execution_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '执行实例Id',
                                             `task_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '任务Id',
                                             `task_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '任务标识',
                                             `trace_id` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '当前请求的traceId',
                                             `variable_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '变量数据',
                                             `create_time` datetime NOT NULL COMMENT '创建时间',
                                             PRIMARY KEY (`id`) USING BTREE,
                                             KEY `idx_process_instance_id` (`process_instance_id`,`task_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='自动化流程变量日志表';

SET FOREIGN_KEY_CHECKS = 1;



SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 全局字典表
-- ----------------------------
DROP TABLE IF EXISTS `zz_global_dict`;
CREATE TABLE `zz_global_dict` (
                                  `dict_id` bigint(20) NOT NULL COMMENT '主键Id',
                                  `dict_code` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '字典编码',
                                  `dict_name` varchar(2048) COLLATE utf8mb4_bin NOT NULL COMMENT '字典中文名称',
                                  `create_user_id` bigint NOT NULL COMMENT '创建用户Id',
                                  `create_time` datetime NOT NULL COMMENT '创建时间',
                                  `update_user_id` bigint NOT NULL COMMENT '更新用户名',
                                  `update_time` datetime NOT NULL COMMENT '更新时间',
                                  `deleted_flag` int NOT NULL COMMENT '逻辑删除字段',
                                  PRIMARY KEY (`dict_id`),
                                  KEY `idx_dict_code` (`dict_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='全局字典表';

-- ----------------------------
-- 全局字典项目表
-- ----------------------------
DROP TABLE IF EXISTS `zz_global_dict_item`;
CREATE TABLE `zz_global_dict_item` (
                                       `id` bigint(20) NOT NULL COMMENT '主键Id',
                                       `dict_code` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '字典编码',
                                       `item_id` varchar(64) NOT NULL COMMENT '字典数据项Id',
                                       `item_name` varchar(1024) COLLATE utf8mb4_bin NOT NULL COMMENT '字典数据项名称',
                                       `show_order` int NOT NULL COMMENT '显示顺序',
                                       `status` int NOT NULL COMMENT '字典状态',
                                       `create_user_id` bigint NOT NULL COMMENT '创建用户Id',
                                       `create_time` datetime NOT NULL COMMENT '创建时间',
                                       `update_user_id` bigint NOT NULL COMMENT '更新用户名',
                                       `update_time` datetime NOT NULL COMMENT '更新时间',
                                       `deleted_flag` int NOT NULL COMMENT '逻辑删除字段',
                                       PRIMARY KEY (`id`),
                                       KEY `idx_show_order` (`show_order`) USING BTREE,
                                       KEY `idx_dict_code_item_id` (`dict_code`,`item_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='全局字典项目表';

SET FOREIGN_KEY_CHECKS = 1;


-- ----------------------------
-- 一定要在与 UPMS 相同的数据库中执行该脚本。
-- 如果是多租户工程，需要在 TENANT_ADMIN，以及所有的租户业务数据库中执行该脚本。
-- ----------------------------

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 移动端入口表
-- ----------------------------
DROP TABLE IF EXISTS `zz_mobile_entry`;
CREATE TABLE `zz_mobile_entry` (
                                   `entry_id` bigint NOT NULL COMMENT '主键Id',
                                   `tenant_admin_entry_id` bigint DEFAULT NULL COMMENT '租户管理端的Id',
                                   `tenant_id` bigint DEFAULT NULL COMMENT '租户Id',
                                   `parent_id` bigint DEFAULT NULL COMMENT '父Id',
                                   `entry_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '显示名称',
                                   `entry_type` int NOT NULL COMMENT '移动端入口类型',
                                   `common_entry` int NOT NULL DEFAULT '0' COMMENT '通用入口对所有角色可见',
                                   `image_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '图片数据',
                                   `extra_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '附件信息',
                                   `show_order` int NOT NULL COMMENT '菜单显示顺序 (值越小，排序越靠前)',
                                   `tenant_available` bit(1) NOT NULL DEFAULT b'1' COMMENT '租户菜单对于当前租户是否可用标记',
                                   `tenant_custom` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否为租户自定义菜单',
                                   `create_user_id` bigint NOT NULL COMMENT '创建者Id',
                                   `create_time` datetime NOT NULL COMMENT '创建时间',
                                   `update_user_id` bigint NOT NULL COMMENT '更新者Id',
                                   `update_time` datetime NOT NULL COMMENT '最后更新时间',
                                   PRIMARY KEY (`entry_id`) USING BTREE,
                                   KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
                                   KEY `idx_tenant_admin_entry_id` (`tenant_admin_entry_id`) USING BTREE,
                                   KEY `idx_show_order` (`show_order`) USING BTREE,
                                   KEY `idx_common_entry` (`common_entry`) USING BTREE,
                                   KEY `idx_entry_type` (`entry_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='移动端入口表';

-- ----------------------------
-- 数据权限和移动端入口对应关系表
-- ----------------------------
DROP TABLE IF EXISTS `zz_mobile_entry_data_perm`;
CREATE TABLE `zz_mobile_entry_data_perm` (
                                             `data_perm_id` bigint NOT NULL COMMENT '数据权限Id',
                                             `entry_id` bigint NOT NULL COMMENT '移动端入口Id',
                                             PRIMARY KEY (`data_perm_id`,`entry_id`) USING BTREE,
                                             KEY `idx_entry_id` (`entry_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据权限和移动端入口对应关系表';

-- ----------------------------
-- 角色和移动端入口对应关系表
-- ----------------------------
DROP TABLE IF EXISTS `zz_mobile_entry_role`;
CREATE TABLE `zz_mobile_entry_role` (
                                        `role_id` bigint NOT NULL COMMENT '角色Id',
                                        `entry_id` bigint NOT NULL COMMENT '移动端入口Id',
                                        PRIMARY KEY (`role_id`,`entry_id`) USING BTREE,
                                        KEY `idx_entry_id` (`entry_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='角色和移动端入口对应关系表';

SET FOREIGN_KEY_CHECKS = 1;


SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 在线表单字段表
-- ----------------------------
DROP TABLE IF EXISTS `zz_online_column`;
CREATE TABLE `zz_online_column` (
                                    `column_id` bigint(20) NOT NULL COMMENT '主键Id',
                                    `column_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '字段名',
                                    `table_id` bigint(20) NOT NULL COMMENT '数据表Id',
                                    `column_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '数据表中的字段类型',
                                    `full_column_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '数据表中的完整字段类型(包括了精度和刻度)',
                                    `primary_key` bit(1) NOT NULL COMMENT '是否为主键',
                                    `auto_incr` bit(1) NOT NULL COMMENT '是否是自增主键(0: 不是 1: 是)',
                                    `nullable` bit(1) NOT NULL COMMENT '是否可以为空 (0: 不可以为空 1: 可以为空)',
                                    `column_default` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '缺省值',
                                    `column_show_order` int(11) NOT NULL COMMENT '字段在数据表中的显示位置',
                                    `column_comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '数据表中的字段注释',
                                    `object_field_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '对象映射字段名称',
                                    `object_field_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '对象映射字段类型',
                                    `numeric_precision` int(11) COMMENT '数值型字段的精度',
                                    `numeric_scale` int(11) COMMENT '数值型字段的刻度',
                                    `filter_type` int(11) NOT NULL DEFAULT 1 COMMENT '字段过滤类型',
                                    `parent_key` bit(1) NOT NULL COMMENT '是否是主键的父Id',
                                    `dept_filter` bit(1) NOT NULL COMMENT '是否部门过滤字段',
                                    `user_filter` bit(1) NOT NULL COMMENT '是否用户过滤字段',
                                    `field_kind` int(11) DEFAULT NULL COMMENT '字段类别',
                                    `max_file_count` int(11) DEFAULT NULL COMMENT '包含的文件文件数量，0表示无限制',
                                    `upload_file_system_type` int(11) DEFAULT 0 COMMENT '上传文件系统类型',
                                    `encoded_rule` varchar(255) DEFAULT NULL COMMENT '编码规则的JSON格式数据',
                                    `mask_field_type` varchar(64) DEFAULT NULL COMMENT '脱敏字段类型',
                                    `dict_id` bigint(20) DEFAULT NULL COMMENT '字典Id',
                                    `file_type` varchar(255) DEFAULT NULL COMMENT '文件类型，多个之间逗号分隔',
                                    `create_time` datetime NOT NULL COMMENT '创建时间',
                                    `create_user_id` bigint NOT NULL COMMENT '创建者',
                                    `update_time` datetime NOT NULL COMMENT '更新时间',
                                    `update_user_id` bigint NOT NULL COMMENT '更新者',
                                    PRIMARY KEY (`column_id`),
                                    KEY `idx_table_id` (`table_id`) USING BTREE,
                                    KEY `idx_dict_id` (`dict_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='在线表单字段表';

-- ----------------------------
-- 在线表单字段和字段规则关联中间表
-- ----------------------------
DROP TABLE IF EXISTS `zz_online_column_rule`;
CREATE TABLE `zz_online_column_rule` (
                                         `column_id` bigint(20) NOT NULL COMMENT '字段Id',
                                         `rule_id` bigint(20) NOT NULL COMMENT '规则Id',
                                         `prop_data_json` text COLLATE utf8mb4_bin COMMENT '规则属性数据',
                                         PRIMARY KEY (`column_id`,`rule_id`) USING BTREE,
                                         KEY `idx_rule_id` (`rule_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='在线表单字段和字段规则关联中间表';

-- ----------------------------
-- 在线表单数据源表
-- ----------------------------
DROP TABLE IF EXISTS `zz_online_datasource`;
CREATE TABLE `zz_online_datasource` (
                                        `datasource_id` bigint(20) NOT NULL COMMENT '主键Id',
                                        `app_code` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用编码',
                                        `datasource_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '数据源名称',
                                        `variable_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '数据源变量名',
                                        `dblink_id` bigint(20) NOT NULL COMMENT '数据库链接Id',
                                        `master_table_id` bigint(20) NOT NULL COMMENT '主表Id',
                                        `create_time` datetime NOT NULL COMMENT '创建时间',
                                        `create_user_id` bigint NOT NULL COMMENT '创建者',
                                        `update_time` datetime NOT NULL COMMENT '更新时间',
                                        `update_user_id` bigint NOT NULL COMMENT '更新者',
                                        PRIMARY KEY (`datasource_id`),
                                        UNIQUE KEY `uk_app_code_variable_name` (`app_code`,`variable_name`) USING BTREE,
                                        KEY `idx_master_table_id` (`master_table_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='在线表单数据源表';

-- ----------------------------
-- 在线表单数据源关联表
-- ----------------------------
DROP TABLE IF EXISTS `zz_online_datasource_relation`;
CREATE TABLE `zz_online_datasource_relation` (
                                                 `relation_id` bigint(20) NOT NULL COMMENT '主键Id',
                                                 `app_code` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用编码',
                                                 `relation_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '关联名称',
                                                 `variable_name` varchar(128) COLLATE utf8mb4_bin NOT NULL COMMENT '变量名',
                                                 `datasource_id` bigint(20) NOT NULL COMMENT '主数据源Id',
                                                 `relation_type` int(11) NOT NULL COMMENT '关联类型',
                                                 `master_column_id` bigint(20) NOT NULL COMMENT '主表关联字段Id',
                                                 `slave_table_id` bigint(20) NOT NULL COMMENT '从表Id',
                                                 `slave_column_id` bigint(20) NOT NULL COMMENT '从表关联字段Id',
                                                 `cascade_delete` bit(1) NOT NULL COMMENT '删除主表的时候是否级联删除一对一和一对多的从表数据，多对多只是删除关联，不受到这个标记的影响。',
                                                 `left_join` bit(1) NOT NULL COMMENT '是否左连接',
                                                 `relation_filter_sql` varchar(1000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '关联过滤SQL',
                                                 `create_time` datetime NOT NULL COMMENT '创建时间',
                                                 `create_user_id` bigint NOT NULL COMMENT '创建者',
                                                 `update_time` datetime NOT NULL COMMENT '更新时间',
                                                 `update_user_id` bigint NOT NULL COMMENT '更新者',
                                                 PRIMARY KEY (`relation_id`) USING BTREE,
                                                 KEY `idx_app_code` (`app_code`) USING BTREE,
                                                 UNIQUE KEY `uk_datasource_id_variable_name` (`datasource_id`,`variable_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='在线表单数据源关联表';

-- ----------------------------
-- 在线表单数据源和数据表关联的中间表
-- ----------------------------
DROP TABLE IF EXISTS `zz_online_datasource_table`;
CREATE TABLE `zz_online_datasource_table` (
                                              `id` bigint(20) NOT NULL COMMENT '主键Id',
                                              `datasource_id` bigint(20) NOT NULL COMMENT '数据源Id',
                                              `relation_id` bigint(20) DEFAULT NULL COMMENT '数据源关联Id',
                                              `table_id` bigint(20) NOT NULL COMMENT '数据表Id',
                                              PRIMARY KEY (`id`) USING BTREE,
                                              KEY `idx_relation_id` (`relation_id`) USING BTREE,
                                              KEY `idx_datasource_id` (`datasource_id`) USING BTREE,
                                              KEY `idx_table_id` (`table_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='在线表单数据源和数据表关联的中间表';

-- ----------------------------
-- 在线表单数据库链接表
-- ----------------------------
DROP TABLE IF EXISTS `zz_online_dblink`;
CREATE TABLE `zz_online_dblink` (
                                    `dblink_id` bigint(20) NOT NULL COMMENT '主键Id',
                                    `app_code` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用编码',
                                    `dblink_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '链接中文名称',
                                    `dblink_description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '链接描述',
                                    `dblink_type` int NOT NULL COMMENT '数据源类型',
                                    `configuration` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '配置信息',
                                    `create_time` datetime NOT NULL COMMENT '创建时间',
                                    `create_user_id` bigint NOT NULL COMMENT '创建者',
                                    `update_time` datetime NOT NULL COMMENT '更新时间',
                                    `update_user_id` bigint NOT NULL COMMENT '更新者',
                                    PRIMARY KEY (`dblink_id`),
                                    KEY `idx_dblink_type` (`dblink_type`) USING BTREE,
                                    KEY `idx_app_code` (`app_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='在线表单数据库链接表';

-- ----------------------------
-- 在线表单字典表
-- ----------------------------
DROP TABLE IF EXISTS `zz_online_dict`;
CREATE TABLE `zz_online_dict` (
                                  `dict_id` bigint(20) NOT NULL COMMENT '主键Id',
                                  `app_code` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用编码',
                                  `dict_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '字典名称',
                                  `dict_type` int(11) NOT NULL COMMENT '字典类型',
                                  `dblink_id` bigint(20) DEFAULT NULL COMMENT '数据库链接Id',
                                  `table_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '字典表名称',
                                  `dict_code` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '全局字典编码',
                                  `key_column_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '字典表键字段名称',
                                  `parent_key_column_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '字典表父键字段名称',
                                  `value_column_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '字典值字段名称',
                                  `deleted_column_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '逻辑删除字段',
                                  `user_filter_column_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户过滤滤字段名称',
                                  `dept_filter_column_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '部门过滤滤字段名称',
                                  `tenant_filter_column_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户过滤字段名称',
                                  `tree_flag` bit(1) NOT NULL COMMENT '是否树形标记',
                                  `dict_list_url` varchar(512) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '获取字典列表数据的url',
                                  `dict_ids_url` varchar(512) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '根据主键id批量获取字典数据的url',
                                  `dict_data_json` text COLLATE utf8mb4_bin COMMENT '字典的JSON数据',
                                  `create_time` datetime NOT NULL COMMENT '创建时间',
                                  `create_user_id` bigint NOT NULL COMMENT '创建者',
                                  `update_time` datetime NOT NULL COMMENT '更新时间',
                                  `update_user_id` bigint NOT NULL COMMENT '更新者',
                                  PRIMARY KEY (`dict_id`) USING BTREE,
                                  KEY `idx_app_code` (`app_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='在线表单字典表';

-- ----------------------------
-- 在线表单表单表
-- ----------------------------
DROP TABLE IF EXISTS `zz_online_form`;
CREATE TABLE `zz_online_form` (
                                  `form_id` bigint(20) NOT NULL COMMENT '主键Id',
                                  `tenant_id` bigint(20) COMMENT '租户id',
                                  `app_code` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用编码',
                                  `page_id` bigint(20) NOT NULL COMMENT '页面id',
                                  `form_code` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '表单编码',
                                  `form_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '表单名称',
                                  `form_kind` int(11) NOT NULL COMMENT '表单类别',
                                  `form_type` int(11) NOT NULL COMMENT '表单类型',
                                  `master_table_id` bigint(20) NOT NULL COMMENT '表单主表id',
                                  `widget_json` mediumtext COLLATE utf8mb4_bin COMMENT '表单组件JSON',
                                  `params_json` text COLLATE utf8mb4_bin COMMENT '表单参数JSON',
                                  `create_time` datetime NOT NULL COMMENT '创建时间',
                                  `create_user_id` bigint NOT NULL COMMENT '创建者',
                                  `update_time` datetime NOT NULL COMMENT '更新时间',
                                  `update_user_id` bigint NOT NULL COMMENT '更新者',
                                  PRIMARY KEY (`form_id`) USING BTREE,
                                  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
                                  UNIQUE KEY `uk_page_id_form_code` (`page_id`,`form_code`) USING BTREE,
                                  KEY `idx_app_code` (`app_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='在线表单表单表';

-- ----------------------------
-- 在线表单表单和数据源关联中间表
-- ----------------------------
DROP TABLE IF EXISTS `zz_online_form_datasource`;
CREATE TABLE `zz_online_form_datasource` (
                                             `id` bigint(20) NOT NULL COMMENT '主键Id',
                                             `form_id` bigint(20) NOT NULL COMMENT '表单Id',
                                             `datasource_id` bigint(20) NOT NULL COMMENT '数据源Id',
                                             PRIMARY KEY (`id`),
                                             KEY `idx_form_id` (`form_id`) USING BTREE,
                                             KEY `idx_datasource_id` (`datasource_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='在线表单表单和数据源关联中间表';

-- ----------------------------
-- 在线表单页面表
-- ----------------------------
DROP TABLE IF EXISTS `zz_online_page`;
CREATE TABLE `zz_online_page` (
                                  `page_id` bigint(20) NOT NULL COMMENT '主键Id',
                                  `tenant_id` bigint(20) COMMENT '租户id',
                                  `app_code` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用编码',
                                  `page_code` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '页面编码',
                                  `page_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '页面名称',
                                  `page_type` int(11) NOT NULL COMMENT '页面类型',
                                  `extra_json` varchar(2048) COLLATE utf8mb4_bin COMMENT '扩展数据',
                                  `status` int(11) NOT NULL COMMENT '页面编辑状态',
                                  `published` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否发布',
                                  `create_time` datetime NOT NULL COMMENT '创建时间',
                                  `create_user_id` bigint NOT NULL COMMENT '创建者',
                                  `update_time` datetime NOT NULL COMMENT '更新时间',
                                  `update_user_id` bigint NOT NULL COMMENT '更新者',
                                  PRIMARY KEY (`page_id`) USING BTREE,
                                  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
                                  KEY `idx_app_code` (`app_code`) USING BTREE,
                                  KEY `idx_page_code` (`page_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='在线表单页面表';

-- ----------------------------
-- 在线表单页面和数据源关联中间表
-- ----------------------------
DROP TABLE IF EXISTS `zz_online_page_datasource`;
CREATE TABLE `zz_online_page_datasource` (
                                             `id` bigint(20) NOT NULL COMMENT '主键Id',
                                             `page_id` bigint(20) NOT NULL COMMENT '页面主键Id',
                                             `datasource_id` bigint(20) NOT NULL COMMENT '数据源主键Id',
                                             PRIMARY KEY (`id`),
                                             KEY `idx_page_id` (`page_id`) USING BTREE,
                                             KEY `idx_datasource_id` (`datasource_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='在线表单页面和数据源关联中间表';

-- ----------------------------
-- 在线表单字段规则表
-- ----------------------------
DROP TABLE IF EXISTS `zz_online_rule`;
CREATE TABLE `zz_online_rule` (
                                  `rule_id` bigint(20) NOT NULL COMMENT '主键Id',
                                  `app_code` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用编码',
                                  `rule_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '规则名称',
                                  `rule_type` int(11) NOT NULL COMMENT '规则类型',
                                  `builtin` bit(1) NOT NULL COMMENT '内置规则标记',
                                  `pattern` varchar(512) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '自定义规则的正则表达式',
                                  `create_time` datetime NOT NULL COMMENT '创建时间',
                                  `create_user_id` bigint NOT NULL COMMENT '创建者',
                                  `update_time` datetime NOT NULL COMMENT '更新时间',
                                  `update_user_id` bigint NOT NULL COMMENT '更新者',
                                  `deleted_flag` int(11) NOT NULL COMMENT '逻辑删除标记',
                                  PRIMARY KEY (`rule_id`) USING BTREE,
                                  KEY `idx_app_code` (`app_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='在线表单字段规则表';

INSERT INTO `zz_online_rule` VALUES (1,NULL,'只允许整数',1,b'1',NULL,CURDATE(),1879052333397053440,CURDATE(),1879052333397053440,1);
INSERT INTO `zz_online_rule` VALUES (2,NULL,'只允许数字',2,b'1',NULL,CURDATE(),1879052333397053440,CURDATE(),1879052333397053440,1);
INSERT INTO `zz_online_rule` VALUES (3,NULL,'只允许英文字符',3,b'1',NULL,CURDATE(),1879052333397053440,CURDATE(),1879052333397053440,1);
INSERT INTO `zz_online_rule` VALUES (4,NULL,'范围验证',4,b'1',NULL,CURDATE(),1879052333397053440,CURDATE(),1879052333397053440,1);
INSERT INTO `zz_online_rule` VALUES (5,NULL,'邮箱格式验证',5,b'1',NULL,CURDATE(),1879052333397053440,CURDATE(),1879052333397053440,1);
INSERT INTO `zz_online_rule` VALUES (6,NULL,'手机格式验证',6,b'1',NULL,CURDATE(),1879052333397053440,CURDATE(),1879052333397053440,1);

-- ----------------------------
-- 在线表单数据表
-- ----------------------------
DROP TABLE IF EXISTS `zz_online_table`;
CREATE TABLE `zz_online_table` (
                                   `table_id` bigint(20) NOT NULL COMMENT '主键Id',
                                   `app_code` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用编码',
                                   `table_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '表名称',
                                   `model_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '实体名称',
                                   `dblink_id` bigint(20) NOT NULL COMMENT '数据库链接Id',
                                   `create_time` datetime NOT NULL COMMENT '创建时间',
                                   `create_user_id` bigint NOT NULL COMMENT '创建者',
                                   `update_time` datetime NOT NULL COMMENT '更新时间',
                                   `update_user_id` bigint NOT NULL COMMENT '更新者',
                                   PRIMARY KEY (`table_id`),
                                   KEY `idx_dblink_id` (`dblink_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='在线表单数据表';

-- ----------------------------
-- 在线表单虚拟字段表
-- ----------------------------
DROP TABLE IF EXISTS `zz_online_virtual_column`;
CREATE TABLE `zz_online_virtual_column` (
                                            `virtual_column_id` bigint(20) NOT NULL COMMENT '主键Id',
                                            `table_id` bigint(20) NOT NULL COMMENT '所在表Id',
                                            `object_field_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '字段名称',
                                            `object_field_type` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '属性类型',
                                            `column_prompt` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '字段提示名',
                                            `virtual_type` int(11) NOT NULL COMMENT '虚拟字段类型(0: 聚合)',
                                            `datasource_id` bigint(20) NOT NULL COMMENT '关联数据源Id',
                                            `relation_id` bigint(20) DEFAULT NULL COMMENT '关联Id',
                                            `aggregation_table_id` bigint(20) DEFAULT NULL COMMENT '聚合字段所在关联表Id',
                                            `aggregation_column_id` bigint(20) DEFAULT NULL COMMENT '关联表聚合字段Id',
                                            `aggregation_type` int(11) DEFAULT NULL COMMENT '聚合类型(0: sum 1: count 2: avg 3: min 4: max)',
                                            `where_clause_json` varchar(1024) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '存储过滤条件的json',
                                            PRIMARY KEY (`virtual_column_id`) USING BTREE,
                                            KEY `idx_database_id` (`datasource_id`) USING BTREE,
                                            KEY `idx_relation_id` (`relation_id`) USING BTREE,
                                            KEY `idx_table_id` (`table_id`) USING BTREE,
                                            KEY `idx_aggregation_column_id` (`aggregation_column_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='在线表单虚拟字段表';

-- ----------------------------
-- 在线表单用户扩展表
-- ----------------------------
DROP TABLE IF EXISTS `zz_online_form_user_ext`;
CREATE TABLE `zz_online_form_user_ext` (
                                           `id` bigint NOT NULL COMMENT '主键Id',
                                           `user_id` bigint NOT NULL COMMENT '用户Id',
                                           `online_form_id` bigint NOT NULL COMMENT '在线表单主键Id',
                                           `extra_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin COMMENT '扩展信息',
                                           PRIMARY KEY (`id`) USING BTREE,
                                           UNIQUE KEY `uk_user_id_form_id` (`user_id`,`online_form_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='在线表单用户扩展表';

SET FOREIGN_KEY_CHECKS = 1;



SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 租户数据集关联表(仅多租户时可用)
-- ----------------------------
DROP TABLE IF EXISTS `zz_report_tenant_dataset`;
CREATE TABLE `zz_report_tenant_dataset` (
                                            `tenant_id` bigint NOT NULL COMMENT '租户Id',
                                            `dataset_id` bigint NOT NULL COMMENT '数据集Id',
                                            PRIMARY KEY (`tenant_id`,`dataset_id`),
                                            KEY `idx_dataset_id` (`dataset_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='租户数据集关联表';

-- ----------------------------
-- 数据集表
-- ----------------------------
DROP TABLE IF EXISTS `zz_report_dataset`;
CREATE TABLE `zz_report_dataset` (
                                     `dataset_id` bigint NOT NULL COMMENT '主键Id',
                                     `app_code` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用编码',
                                     `dataset_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '数据集名称',
                                     `group_id` bigint NOT NULL COMMENT '分组Id',
                                     `dblink_id` bigint COMMENT '数据库链接Id',
                                     `dataset_type` int NOT NULL COMMENT '数据集类型',
                                     `table_name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '数据表名',
                                     `dataset_info` text COLLATE utf8mb4_bin COMMENT '表原始信息',
                                     `create_time` datetime NOT NULL COMMENT '创建时间',
                                     `create_user_id` bigint NOT NULL COMMENT '创建者',
                                     `update_time` datetime NOT NULL COMMENT '更新时间',
                                     `update_user_id` bigint NOT NULL COMMENT '更新者',
                                     PRIMARY KEY (`dataset_id`) USING BTREE,
                                     KEY `idx_group_id` (`group_id`) USING BTREE,
                                     KEY `idx_dblink_id` (`dblink_id`) USING BTREE,
                                     KEY `idx_app_code` (`app_code`) USING BTREE,
                                     KEY `idx_dataset_type` (`dataset_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据集表';

-- ----------------------------
-- 数据集字段表
-- ----------------------------
DROP TABLE IF EXISTS `zz_report_dataset_column`;
CREATE TABLE `zz_report_dataset_column` (
                                            `column_id` bigint NOT NULL COMMENT '主键Id',
                                            `column_name` varchar(128) COLLATE utf8mb4_bin NOT NULL COMMENT '字段名',
                                            `dataset_id` bigint NOT NULL COMMENT '数据集Id',
                                            `primary_key` int NOT NULL DEFAULT '0' COMMENT '是否为主键',
                                            `column_type` varchar(128) COLLATE utf8mb4_bin NOT NULL COMMENT '表字段类型',
                                            `column_show_order` int NOT NULL COMMENT '列位置',
                                            `column_comment` varchar(521) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '字段注释',
                                            `field_name` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT 'Java属性名',
                                            `field_type` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT 'Java属性类型',
                                            `numeric_precision` int DEFAULT '0' COMMENT '数值型字段的精度',
                                            `numeric_scale` int DEFAULT NULL COMMENT '数值型字段的刻度',
                                            `dict_id` bigint DEFAULT NULL COMMENT '字典Id',
                                            `dimension` int DEFAULT NULL COMMENT '是否为维度字段',
                                            `image` int NOT NULL DEFAULT '0' COMMENT '是否为图片字段',
                                            `logic_delete` int NOT NULL DEFAULT '0' COMMENT '是否为逻辑删除字段',
                                            `dept_filter` int NOT NULL DEFAULT '0' COMMENT '是否部门过滤字段',
                                            `user_filter` int NOT NULL DEFAULT '0' COMMENT '是否用户过滤字段',
                                            `tenant_filter` int NOT NULL DEFAULT '0' COMMENT '是否用户过滤字段',
                                            `field_kind` int NOT NULL DEFAULT '0' COMMENT '字段类别',
                                            `function_body` varchar(1024) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '函数体实现',
                                            PRIMARY KEY (`column_id`) USING BTREE,
                                            KEY `idx_dataset_id` (`dataset_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据集字段表';

-- ----------------------------
-- 数据集分组表
-- ----------------------------
DROP TABLE IF EXISTS `zz_report_dataset_group`;
CREATE TABLE `zz_report_dataset_group` (
                                           `group_id` bigint NOT NULL COMMENT '主键Id',
                                           `app_code` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用编码',
                                           `group_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '分组名称',
                                           `parent_id` bigint DEFAULT NULL COMMENT '父级Id',
                                           `create_time` datetime NOT NULL COMMENT '创建时间',
                                           `create_user_id` bigint NOT NULL COMMENT '创建者',
                                           `update_time` datetime NOT NULL COMMENT '更新时间',
                                           `update_user_id` bigint NOT NULL COMMENT '更新者',
                                           KEY `idx_app_code` (`app_code`) USING BTREE,
                                           PRIMARY KEY (`group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据集分组表';

-- ----------------------------
-- 数据集关联表
-- ----------------------------
DROP TABLE IF EXISTS `zz_report_dataset_relation`;
CREATE TABLE `zz_report_dataset_relation` (
                                              `relation_id` bigint NOT NULL COMMENT '主键Id',
                                              `app_code` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用编码',
                                              `variable_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '变量名',
                                              `master_dataset_id` bigint NOT NULL COMMENT '主表数据集Id',
                                              `master_column_id` bigint NOT NULL COMMENT '主表关联字段Id',
                                              `slave_dataset_id` bigint NOT NULL COMMENT '从表数据集Id',
                                              `slave_column_id` bigint NOT NULL COMMENT '从表关联字段Id',
                                              `relation_type` int NOT NULL COMMENT '关联类型 (0:一对一 1:一对多)',
                                              `create_time` datetime NOT NULL COMMENT '创建时间',
                                              `create_user_id` bigint NOT NULL COMMENT '创建者',
                                              `update_time` datetime NOT NULL COMMENT '更新时间',
                                              `update_user_id` bigint NOT NULL COMMENT '更新者',
                                              PRIMARY KEY (`relation_id`) USING BTREE,
                                              KEY `idx_app_code` (`app_code`) USING BTREE,
                                              UNIQUE KEY `uk_master_dataset_id` (`master_dataset_id`, `variable_name`) USING BTREE,
                                              KEY `idx_slave_dataset_id` (`slave_dataset_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据集关联表';

-- ----------------------------
-- 数据库链接表
-- ----------------------------
DROP TABLE IF EXISTS `zz_report_dblink`;
CREATE TABLE `zz_report_dblink` (
                                    `dblink_id` bigint NOT NULL COMMENT '主键Id',
                                    `app_code` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用编码',
                                    `dblink_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '数据源名称',
                                    `dblink_description` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '数据源描述',
                                    `dblink_type` int NOT NULL COMMENT '数据源类型',
                                    `configuration` varchar(2000) COLLATE utf8mb4_bin NOT NULL COMMENT '配置信息',
                                    `create_time` datetime NOT NULL COMMENT '创建时间',
                                    `create_user_id` bigint NOT NULL COMMENT '创建者',
                                    `update_time` datetime NOT NULL COMMENT '更新时间',
                                    `update_user_id` bigint NOT NULL COMMENT '更新者',
                                    PRIMARY KEY (`dblink_id`) USING BTREE,
                                    KEY `idx_app_code` (`app_code`) USING BTREE,
                                    KEY `idx_dblink_type` (`dblink_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='数据库链接表';

-- ----------------------------
-- 字典表
-- ----------------------------
DROP TABLE IF EXISTS `zz_report_dict`;
CREATE TABLE `zz_report_dict` (
                                  `dict_id` bigint NOT NULL COMMENT '主键Id',
                                  `app_code` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用编码',
                                  `dict_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '字典名称',
                                  `dict_type` int NOT NULL COMMENT '字典类型',
                                  `dblink_id` bigint DEFAULT NULL COMMENT '数据库链接Id',
                                  `table_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '字典表名称',
                                  `dict_code` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '全局字典编码',
                                  `key_column_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '字典表键字段名称',
                                  `parent_key_column_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '字典表父键字段名称',
                                  `value_column_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '字典值字段名称',
                                  `deleted_column_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '逻辑删除字段',
                                  `tenant_filter_column_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户过滤字段名称',
                                  `tree_flag` int NOT NULL COMMENT '是否树形标记',
                                  `dict_list_url` varchar(512) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '获取字典列表数据的url',
                                  `dict_ids_url` varchar(512) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '根据主键id批量获取字典数据的url',
                                  `dict_data_json` text COLLATE utf8mb4_bin COMMENT '字典的JSON数据',
                                  `create_time` datetime NOT NULL COMMENT '创建时间',
                                  `create_user_id` bigint NOT NULL COMMENT '创建者',
                                  `update_time` datetime NOT NULL COMMENT '更新时间',
                                  `update_user_id` bigint NOT NULL COMMENT '更新者',
                                  PRIMARY KEY (`dict_id`) USING BTREE,
                                  KEY `idx_app_code` (`app_code`) USING BTREE,
                                  KEY `idx_dict_type` (`dict_type`) USING BTREE,
                                  KEY `idx_dblink_id` (`dblink_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='字典表';

-- ----------------------------
-- 在线统计表单页面表
-- ----------------------------
DROP TABLE IF EXISTS `zz_report_page`;
CREATE TABLE `zz_report_page` (
                                  `page_id` bigint NOT NULL COMMENT '主键Id',
                                  `tenant_id` bigint COMMENT '租户Id',
                                  `app_code` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用编码',
                                  `page_code` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '页面编码',
                                  `page_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '名称',
                                  `group_id` bigint NOT NULL COMMENT '页面分组Id',
                                  `page_json` longtext COLLATE utf8mb4_bin COMMENT '页面配置的JSON',
                                  `widget_json` text COLLATE utf8mb4_bin COMMENT '表单组件JSON',
                                  `param_json` text COLLATE utf8mb4_bin COMMENT '表单参数JSON',
                                  `create_time` datetime NOT NULL COMMENT '创建时间',
                                  `create_user_id` bigint NOT NULL COMMENT '创建者',
                                  `update_time` datetime NOT NULL COMMENT '更新时间',
                                  `update_user_id` bigint NOT NULL COMMENT '更新者',
                                  PRIMARY KEY (`page_id`) USING BTREE,
                                  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
                                  KEY `idx_app_code` (`app_code`) USING BTREE,
                                  KEY `idx_page_code` (`page_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='在线统计表单页面表';

-- ----------------------------
-- 统计页面分组表
-- ----------------------------
DROP TABLE IF EXISTS `zz_report_page_group`;
CREATE TABLE `zz_report_page_group` (
                                        `group_id` bigint NOT NULL COMMENT '主键Id',
                                        `tenant_id` bigint COMMENT '租户Id',
                                        `app_code` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用编码',
                                        `group_name` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '名称',
                                        `parent_id` bigint DEFAULT NULL COMMENT '父级id',
                                        `create_time` datetime NOT NULL COMMENT '创建时间',
                                        `create_user_id` bigint NOT NULL COMMENT '创建者',
                                        `update_time` datetime NOT NULL COMMENT '更新时间',
                                        `update_user_id` bigint NOT NULL COMMENT '更新者',
                                        KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
                                        KEY `idx_app_code` (`app_code`) USING BTREE,
                                        PRIMARY KEY (`group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='统计页面分组表';

-- ----------------------------
-- 打印模板表
-- ----------------------------
DROP TABLE IF EXISTS `zz_report_print`;
CREATE TABLE `zz_report_print` (
                                   `print_id` bigint NOT NULL COMMENT '主键Id',
                                   `tenant_id` bigint COMMENT '租户Id',
                                   `app_code` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用编码',
                                   `print_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '名称',
                                   `print_variable` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '变量名',
                                   `print_type` int DEFAULT '1' COMMENT '打印模板类型',
                                   `word_template` varchar(1024) COLLATE utf8mb4_bin COMMENT 'word打印模板',
                                   `group_id` bigint NOT NULL COMMENT '页面分组Id',
                                   `print_json` text COLLATE utf8mb4_bin COMMENT '打印配置JSON',
                                   `param_json` text COLLATE utf8mb4_bin COMMENT '参数数据JSON',
                                   `fragment_json` text COLLATE utf8mb4_bin COMMENT '打印片段参数JSON',
                                   `sheet_data_json` longtext COLLATE utf8mb4_bin COMMENT 'luckysheet电子表单原始配置JSON',
                                   `template_data_json` longtext COLLATE utf8mb4_bin COMMENT '电子表格解析后的打印模板配置数据JSON',
                                   `create_time` datetime NOT NULL COMMENT '创建时间',
                                   `create_user_id` bigint NOT NULL COMMENT '创建者',
                                   `update_time` datetime NOT NULL COMMENT '更新时间',
                                   `update_user_id` bigint NOT NULL COMMENT '更新者',
                                   PRIMARY KEY (`print_id`) USING BTREE,
                                   KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
                                   KEY `idx_app_code` (`app_code`) USING BTREE,
                                   KEY `idx_print_variable` (`print_variable`) USING BTREE,
                                   KEY `idx_group_id` (`group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='打印模板表';

-- ----------------------------
-- 打印模板分组表
-- ----------------------------
DROP TABLE IF EXISTS `zz_report_print_group`;
CREATE TABLE `zz_report_print_group` (
                                         `group_id` bigint NOT NULL COMMENT '主键Id',
                                         `tenant_id` bigint COMMENT '租户Id',
                                         `app_code` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用编码',
                                         `group_name` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '名称',
                                         `parent_id` bigint DEFAULT NULL COMMENT '父级id',
                                         `create_time` datetime NOT NULL COMMENT '创建时间',
                                         `create_user_id` bigint NOT NULL COMMENT '创建者',
                                         `update_time` datetime NOT NULL COMMENT '更新时间',
                                         `update_user_id` bigint NOT NULL COMMENT '更新者',
                                         KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
                                         KEY `idx_app_code` (`app_code`) USING BTREE,
                                         PRIMARY KEY (`group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='打印模板分组表';

-- ----------------------------
-- 大屏可视化数据表
-- ----------------------------
DROP TABLE IF EXISTS `zz_report_visualization`;
CREATE TABLE `zz_report_visualization` (
                                           `visual_id` bigint NOT NULL COMMENT '主键Id',
                                           `visual_name` varchar(512) COLLATE utf8mb4_bin NOT NULL COMMENT '名称',
                                           `config_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '可视化配置',
                                           `cover_img` longtext COLLATE utf8mb4_bin COMMENT '封面图的BASE64',
                                           `code_page` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '代码页',
                                           `publish_status` int NOT NULL COMMENT '发布状态',
                                           `create_time` datetime NOT NULL COMMENT '创建时间',
                                           `create_user_id` bigint NOT NULL COMMENT '创建者',
                                           `update_time` datetime NOT NULL COMMENT '更新时间',
                                           `update_user_id` bigint NOT NULL COMMENT '更新者',
                                           `deleted_flag` int NOT NULL COMMENT '删除标记(1: 正常 -1: 已删除)',
                                           PRIMARY KEY (`visual_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='大屏可视化数据表';

-- ----------------------------
-- 大屏可视化素材数据表
-- ----------------------------
DROP TABLE IF EXISTS `zz_report_visualization_asset`;
CREATE TABLE `zz_report_visualization_asset` (
                                                 `asset_id` bigint NOT NULL COMMENT '主键Id',
                                                 `visual_id` bigint COMMENT '可视化项目Id',
                                                 `asset_name` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '素材名称',
                                                 `thumbnail_img` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '缩略图BASE64',
                                                 `asset_img` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '图的BASE64',
                                                 `create_time` datetime NOT NULL COMMENT '创建时间',
                                                 `create_user_id` bigint NOT NULL COMMENT '创建者',
                                                 `update_time` datetime NOT NULL COMMENT '更新时间',
                                                 `update_user_id` bigint NOT NULL COMMENT '更新者',
                                                 PRIMARY KEY (`asset_id`) USING BTREE,
                                                 KEY `idx_visual_id` (`visual_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='大屏可视化素材数据表';

SET FOREIGN_KEY_CHECKS = 1;



-- ----------------------------
-- 请仅在下面的数据库链接中执行该脚本。
-- 主数据源 [localhost:3306/tis]
-- ----------------------------

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 全部菜单数据
-- ----------------------------
BEGIN;
INSERT INTO `sys_menu` VALUES(1879052333409636353,NULL,'系统管理',0,NULL,NULL,NULL,NULL,NULL,1,NULL,'',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052333418024960,1879052333409636353,'用户管理',1,'formSysUser',NULL,NULL,NULL,NULL,100,NULL,'',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744225,1879052333418024960,'显示',3,NULL,NULL,NULL,NULL,NULL,1,NULL,'{"menuCode":"formSysUser:fragmentSysUser","permCodeList":["sysUser.view"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744226,1879052333418024960,'新增',3,NULL,NULL,NULL,NULL,NULL,2,NULL,'{"menuCode":"formSysUser:fragmentSysUser:add","permCodeList":["sysUser.add"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744227,1879052333418024960,'编辑',3,NULL,NULL,NULL,NULL,NULL,3,NULL,'{"menuCode":"formSysUser:fragmentSysUser:update","permCodeList":["sysUser.update"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744228,1879052333418024960,'删除',3,NULL,NULL,NULL,NULL,NULL,4,NULL,'{"menuCode":"formSysUser:fragmentSysUser:delete","permCodeList":["sysUser.delete"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744229,1879052333418024960,'重置密码',3,NULL,NULL,NULL,NULL,NULL,5,NULL,'{"menuCode":"formSysUser:fragmentSysUser:resetPassword","permCodeList":["sysUser.resetPassword"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052333418024961,1879052333409636353,'部门管理',1,'formSysDept',NULL,NULL,NULL,NULL,105,NULL,'',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744231,1879052333418024961,'显示',3,NULL,NULL,NULL,NULL,NULL,1,NULL,'{"menuCode":"formSysDept:fragmentSysDept","permCodeList":["sysDept.view"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744232,1879052333418024961,'新增',3,NULL,NULL,NULL,NULL,NULL,2,NULL,'{"menuCode":"formSysDept:fragmentSysDept:add","permCodeList":["sysDept.add"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744233,1879052333418024961,'编辑',3,NULL,NULL,NULL,NULL,NULL,3,NULL,'{"menuCode":"formSysDept:fragmentSysDept:update","permCodeList":["sysDept.update"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744234,1879052333418024961,'删除',3,NULL,NULL,NULL,NULL,NULL,4,NULL,'{"menuCode":"formSysDept:fragmentSysDept:delete","permCodeList":["sysDept.delete"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744235,1879052333418024961,'设置岗位',3,NULL,NULL,NULL,NULL,NULL,5,NULL,'{"menuCode":"formSysDept:fragmentSysDept:editPost","permCodeList":["sysDept.update"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744236,1879052333418024961,'查看岗位',3,NULL,NULL,NULL,NULL,NULL,6,NULL,'{"menuCode":"formSysDept:fragmentSysDept:viewPost","permCodeList":["sysDept.update"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052333418024962,1879052333409636353,'角色管理',1,'formSysRole',NULL,NULL,NULL,NULL,110,NULL,'',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744238,1879052333418024962,'角色管理',2,NULL,NULL,NULL,NULL,NULL,1,NULL,'',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744239,1879052333418024962,'用户授权',2,NULL,NULL,NULL,NULL,NULL,2,NULL,'',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744240,1879052428909744238,'显示',3,NULL,NULL,NULL,NULL,NULL,1,NULL,'{"menuCode":"formSysRole:fragmentSysRole","permCodeList":["sysRole.view"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744241,1879052428909744238,'新增',3,NULL,NULL,NULL,NULL,NULL,2,NULL,'{"menuCode":"formSysRole:fragmentSysRole:add","permCodeList":["sysRole.add"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744242,1879052428909744238,'编辑',3,NULL,NULL,NULL,NULL,NULL,3,NULL,'{"menuCode":"formSysRole:fragmentSysRole:update","permCodeList":["sysRole.update"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744243,1879052428909744238,'删除',3,NULL,NULL,NULL,NULL,NULL,4,NULL,'{"menuCode":"formSysRole:fragmentSysRole:delete","permCodeList":["sysRole.delete"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744244,1879052428909744239,'显示',3,NULL,NULL,NULL,NULL,NULL,1,NULL,'{"menuCode":"formSysRole:fragmentSysRoleUser","permCodeList":["sysRole.view"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744245,1879052428909744239,'授权用户',3,NULL,NULL,NULL,NULL,NULL,2,NULL,'{"menuCode":"formSysRole:fragmentSysRoleUser:addUserRole","permCodeList":["sysRole.update"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744246,1879052428909744239,'移除用户',3,NULL,NULL,NULL,NULL,NULL,3,NULL,'{"menuCode":"formSysRole:fragmentSysRoleUser:deleteUserRole","permCodeList":["sysRole.update"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052333418024963,1879052333409636353,'数据权限管理',1,'formSysDataPerm',NULL,NULL,NULL,NULL,115,NULL,'',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744248,1879052333418024963,'数据权限管理',2,NULL,NULL,NULL,NULL,NULL,1,NULL,'',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744249,1879052333418024963,'用户授权',2,NULL,NULL,NULL,NULL,NULL,2,NULL,'',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744250,1879052428909744248,'显示',3,NULL,NULL,NULL,NULL,NULL,1,NULL,'{"menuCode":"formSysDataPerm:fragmentSysDataPerm","permCodeList":["sysDataPerm.view"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744251,1879052428909744248,'新增',3,NULL,NULL,NULL,NULL,NULL,2,NULL,'{"menuCode":"formSysDataPerm:fragmentSysDataPerm:add","permCodeList":["sysDataPerm.add"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744252,1879052428909744248,'编辑',3,NULL,NULL,NULL,NULL,NULL,3,NULL,'{"menuCode":"formSysDataPerm:fragmentSysDataPerm:update","permCodeList":["sysDataPerm.update"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744253,1879052428909744248,'删除',3,NULL,NULL,NULL,NULL,NULL,4,NULL,'{"menuCode":"formSysDataPerm:fragmentSysDataPerm:delete","permCodeList":["sysDataPerm.delete"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744254,1879052428909744249,'显示',3,NULL,NULL,NULL,NULL,NULL,1,NULL,'{"menuCode":"formSysDataPerm:fragmentSysDataPermUser","permCodeList":["sysDataPerm.view"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744255,1879052428909744249,'授权用户',3,NULL,NULL,NULL,NULL,NULL,2,NULL,'{"menuCode":"formSysDataPerm:fragmentSysDataPermUser:addDataPermUser","permCodeList":["sysDataPerm.update"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744256,1879052428909744249,'移除用户',3,NULL,NULL,NULL,NULL,NULL,3,NULL,'{"menuCode":"formSysDataPerm:fragmentSysDataPermUser:deleteDataPermUser","permCodeList":["sysDataPerm.update"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052333418024964,1879052333409636353,'岗位管理',1,'formSysPost',NULL,NULL,NULL,NULL,106,NULL,'',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744258,1879052333418024964,'岗位管理',2,NULL,NULL,NULL,NULL,NULL,1,NULL,'',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744259,1879052428909744258,'显示',3,NULL,NULL,NULL,NULL,NULL,1,NULL,'{"menuCode":"formSysPost:fragmentSysPost","permCodeList":["sysPost.view"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744260,1879052428909744258,'新增',3,NULL,NULL,NULL,NULL,NULL,2,NULL,'{"menuCode":"formSysPost:fragmentSysPost:add","permCodeList":["sysPost.add"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744261,1879052428909744258,'编辑',3,NULL,NULL,NULL,NULL,NULL,3,NULL,'{"menuCode":"formSysPost:fragmentSysPost:update","permCodeList":["sysPost.update"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744262,1879052428909744258,'删除',3,NULL,NULL,NULL,NULL,NULL,4,NULL,'{"menuCode":"formSysPost:fragmentSysPost:delete","permCodeList":["sysPost.delete"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052333418024965,1879052333409636353,'菜单管理',1,'formSysMenu',NULL,NULL,NULL,NULL,120,NULL,'',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744264,1879052333418024965,'显示',3,NULL,NULL,NULL,NULL,NULL,1,NULL,'{"menuCode":"formSysMenu:fragmentSysMenu","permCodeList":["sysMenu.view"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744265,1879052333418024965,'新增',3,NULL,NULL,NULL,NULL,NULL,2,NULL,'{"menuCode":"formSysMenu:fragmentSysMenu:add","permCodeList":["sysMenu.add"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744266,1879052333418024965,'编辑',3,NULL,NULL,NULL,NULL,NULL,3,NULL,'{"menuCode":"formSysMenu:fragmentSysMenu:update","permCodeList":["sysMenu.update"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744267,1879052333418024965,'删除',3,NULL,NULL,NULL,NULL,NULL,4,NULL,'{"menuCode":"formSysMenu:fragmentSysMenu:delete","permCodeList":["sysMenu.delete"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052333418024968,1879052333409636353,'字典管理',1,'formSysDict',NULL,NULL,NULL,NULL,135,NULL,'',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744271,1879052333418024968,'显示',3,NULL,NULL,NULL,NULL,NULL,1,NULL,'{"menuCode":"formSysDict:fragmentSysDict","permCodeList":["globalDict.view"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744272,1879052333418024968,'新增',3,NULL,NULL,NULL,NULL,NULL,2,NULL,'{"menuCode":"formSysDict:fragmentSysDict:add","permCodeList":["globalDict.update"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744273,1879052333418024968,'编辑',3,NULL,NULL,NULL,NULL,NULL,3,NULL,'{"menuCode":"formSysDict:fragmentSysDict:update","permCodeList":["globalDict.update"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744274,1879052333418024968,'删除',3,NULL,NULL,NULL,NULL,NULL,4,NULL,'{"menuCode":"formSysDict:fragmentSysDict:delete","permCodeList":["globalDict.update"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744275,1879052333418024968,'同步缓存',3,NULL,NULL,NULL,NULL,NULL,5,NULL,'{"menuCode":"formSysDict:fragmentSysDict:reloadCache","permCodeList":["globalDict.view"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052333418024969,1879052333409636353,'操作日志',1,'formSysOperationLog',NULL,NULL,NULL,NULL,140,NULL,'',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744277,1879052333418024969,'显示',3,NULL,NULL,NULL,NULL,NULL,1,NULL,'{"menuCode":"formSysOperationLog:fragmentSysOperationLog","permCodeList":["sysOperationLog.view"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052333418024970,1879052333409636353,'在线用户',1,'formSysLoginUser',NULL,NULL,NULL,NULL,145,NULL,'',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744279,1879052333418024970,'显示',3,NULL,NULL,NULL,NULL,NULL,1,NULL,'{"menuCode":"formSysLoginUser:fragmentLoginUser","permCodeList":["loginUser.view"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1879052428909744280,1879052333418024970,'强制下线',3,NULL,NULL,NULL,NULL,NULL,2,NULL,'{"menuCode":"formSysLoginUser:fragmentLoginUser:delete","permCodeList":["loginUser.delete"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1882320660345131008,NULL,'患者管理',0,NULL,NULL,NULL,NULL,NULL,100,NULL,'',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1882320825722343424,1882320660345131008,'患者信息',1,'formTisPatInfo',NULL,NULL,NULL,NULL,200,NULL,'{}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1882452385217908813,1882320825722343424,'显示',3,NULL,NULL,NULL,NULL,NULL,1,NULL,'{"menuCode":"formTisPatInfo:formTisPatInfo","permCodeList":["tisPatInfo.view"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES(1882452385217908814,1882320825722343424,'检查结果',3,NULL,NULL,NULL,NULL,NULL,2,NULL,'{"menuCode":"formTisPatInfo:formTisPatInfo:editTisPatInfo","permCodeList":["tisPatInfo.view"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());

-- ----------------------------
-- 以下记录用于移动端，这里的注释，是为了便于老用户进行手动数据补偿。
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1687821642446671872,NULL,'移动端管理',0,NULL,NULL,NULL,NULL,NULL,10,NULL,NULL,1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES (1688105082400280576,1687821642446671872,'轮播图配置',1,'formBanner',NULL,NULL,NULL,NULL,1,NULL,'{"permCodeList":["mobileEntry.all"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES (1687821728979357696,1687821642446671872,'九宫格配置',1,'formSudoku',NULL,NULL,NULL,NULL,2,NULL,'{"permCodeList":["mobileEntry.all"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());

-- ----------------------------
-- 以下记录用于在线表单，这里的注释，是为了便于老用户进行手动数据补偿。
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1634009076981567488,1392786476428693504,'数据库链接',1,'formOnlineDblink',NULL,NULL,NULL,NULL,1,NULL,'{"permCodeList":["onlineDblink.all"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES (1392786549942259712,1392786476428693504,'字典管理',1,'formOnlineDict',NULL,NULL,NULL,NULL,2,NULL,'{"permCodeList":["onlineDict.all"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES (1392786950682841088,1392786476428693504,'表单管理',1,'formOnlinePage',NULL,NULL,NULL,NULL,3,NULL,'{"permCodeList":["onlinePage.all"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES (1392786476428693504,NULL,'在线表单',0,NULL,NULL,NULL,NULL,NULL,2,NULL,NULL,1879052333397053440,CURDATE(),1879052333397053440,CURDATE());

-- ----------------------------
-- 以下记录用于报表打印，这里的注释，是为了便于老用户进行手动数据补偿。
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1534897180643430400,1515857992501694464,'打印管理',1,'formPrintManage',NULL,NULL,NULL,NULL,5,NULL,'{"permCodeList":["reportPrint.all"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES (1517063268609298432,1515857992501694464,'页面管理',1,'formReportPage',NULL,NULL,NULL,NULL,4,NULL,'{"permCodeList":["reportPage.all"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES (1516291375656603648,1515857992501694464,'报表字典',1,'formReportDict',NULL,NULL,NULL,NULL,2,NULL,'{"permCodeList":["reportDict.all"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES (1515858207237476352,1515857992501694464,'数据集',1,'formReportDataset',NULL,NULL,NULL,NULL,3,NULL,'{"permCodeList":["reportDataset.all"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES (1515858116057501696,1515857992501694464,'数据库链接',1,'formReportDblink',NULL,NULL,NULL,NULL,1,NULL,'{"permCodeList":["reportDblink.all"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES (1515857992501694464,NULL,'报表管理',0,NULL,NULL,NULL,NULL,NULL,25,NULL,NULL,1879052333397053440,CURDATE(),1879052333397053440,CURDATE());

-- ----------------------------
-- 以下记录用于工作流，这里的注释，是为了便于老用户进行手动数据补偿。
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1418057714138877952,NULL,'流程管理',0,NULL,NULL,NULL,NULL,NULL,3,NULL,NULL,1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES (1418059005175009280,NULL,'任务管理',0,NULL,NULL,NULL,NULL,NULL,4,NULL,NULL,1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES (1418057835631087616,1418057714138877952,'流程分类',1,'formFlowCategory',NULL,NULL,NULL,NULL,1,NULL,'{"permCodeList":["flowCategory.all"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES (1418058289182150656,1418057714138877952,'流程设计',1,'formFlowEntry',NULL,NULL,NULL,NULL,2,NULL,'{"permCodeList":["flowEntry.all"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES (1418057835631087617,1418057714138877952,'数据库链接',1,'formFlowDblink',NULL,NULL,NULL,NULL,0,NULL,'{"permCodeList":["flowDblink.all"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES (1418058744037642240,1418057714138877952,'流程实例',1,'formAllInstance',NULL,NULL,NULL,NULL,3,NULL,'{"permCodeList":["flowOperation.all"]}',1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES (1418059167532322816,1418059005175009280,'待办任务',1,'formMyTask',NULL,NULL,NULL,NULL,1,NULL,NULL,1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES (1423161217970606080,1418059005175009280,'已办任务',1,'formMyApprovedTask',NULL,NULL,NULL,NULL,2,NULL,NULL,1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
INSERT INTO `sys_menu` VALUES (1418059283920064512,1418059005175009280,'历史任务',1,'formMyHistoryTask',NULL,NULL,NULL,NULL,3,NULL,NULL,1879052333397053440,CURDATE(),1879052333397053440,CURDATE());
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;


DROP TABLE IF EXISTS `tis_pat_info`;
CREATE TABLE `tis_pat_info`  (
                                 `id` bigint(20) NOT NULL COMMENT '主键Id',
                                 `pat_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
                                 `batch_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '批次号',
                                 `age` varchar(3) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '年龄',
                                 `project_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '检测项目',
                                 `sex` varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别',
                                 `sample_no` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '样本编号',
                                 `pat_no` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '患者编号',
                                 `sample_type` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '样本类型',
                                 `operator` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作人员',
                                 `cutoff_val` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'cotful值',
                                 `range_val` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '范围',
                                 `pic_path` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '患者卡条图片路径',
                                 `test_time` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '检测时间',
                                 `test_unit` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '检测单位',
                                 `test_stat` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '检测状态',
                                 `file_path` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'txt文件路径',
                                 `remark1` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备用字段1',
                                 `remark2` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备用字段2',
                                 `remark3` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备用字段3',
                                 `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                 `created_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建用户',
                                 `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
                                 `update_user_id` bigint(20) NULL DEFAULT NULL COMMENT '修改用户',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '患者信息表' ROW_FORMAT = DYNAMIC;


DROP TABLE IF EXISTS `tis_pat_result`;
CREATE TABLE `tis_pat_result`  (
                                   `id` bigint(20) NOT NULL COMMENT '主键Id',
                                   `pat_id` bigint(20) NOT NULL COMMENT '患者ID',
                                   `project_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '检测项目',
                                   `result` varchar(3) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '检测结果',
                                   `remark1` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备用字段1',
                                   `remark2` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备用字段2',
                                   `remark3` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备用字段3',
                                   `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                   `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建用户',
                                   `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
                                   `update_user_id` bigint(20) NULL DEFAULT NULL COMMENT '修改用户',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '患者检测结果表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;

