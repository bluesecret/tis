
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
