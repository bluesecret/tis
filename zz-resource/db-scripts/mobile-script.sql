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
