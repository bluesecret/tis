
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
INSERT INTO `sys_user` VALUES(1879052333397053440,'admin','$2a$10$HzYYngg/SOqZ4Xln/cF/Uu0Wakk59ONbeO73Vbk4JdqFHH8dx4tQy','管理员',1879052333397053443,NULL,0,0,NULL,NULL,NULL,1879052333397053440,CURDATE(),1879052333397053440,CURDATE(),1);
INSERT INTO `sys_dept_relation` VALUES(1879052333397053443,1879052333397053443);
COMMIT;
SET FOREIGN_KEY_CHECKS = 1;
