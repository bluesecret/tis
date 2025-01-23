-- ----------------------------
-- 该脚本用于删除自动生成的用户权限管理数据。在搭建初始环境的时候，不要执行该脚本。
-- 再次重新生成项目的时候，如果您在生成器中新增了用户权限相关的数据，而之前已经搭建好的数据库中，也存在了
-- 您自己手动插入的权限数据时，可以通过执行该脚本，将现有数据库表中，生成器生成的权限数据删除，删除之后，可以再执行新生成的数据库脚本数据。
-- 请仅在下面的数据库链接中执行该脚本。
-- 主数据源 [localhost:3306/tis]
-- ----------------------------

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 全部菜单数据
-- ----------------------------
BEGIN;
DELETE FROM `sys_menu` WHERE menu_id = 1879052333409636353;
DELETE FROM `sys_menu` WHERE menu_id = 1879052333418024960;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744225;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744226;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744227;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744228;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744229;
DELETE FROM `sys_menu` WHERE menu_id = 1879052333418024961;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744231;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744232;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744233;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744234;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744235;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744236;
DELETE FROM `sys_menu` WHERE menu_id = 1879052333418024962;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744238;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744239;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744240;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744241;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744242;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744243;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744244;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744245;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744246;
DELETE FROM `sys_menu` WHERE menu_id = 1879052333418024963;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744248;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744249;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744250;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744251;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744252;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744253;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744254;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744255;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744256;
DELETE FROM `sys_menu` WHERE menu_id = 1879052333418024964;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744258;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744259;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744260;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744261;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744262;
DELETE FROM `sys_menu` WHERE menu_id = 1879052333418024965;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744264;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744265;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744266;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744267;
DELETE FROM `sys_menu` WHERE menu_id = 1879052333418024968;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744271;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744272;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744273;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744274;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744275;
DELETE FROM `sys_menu` WHERE menu_id = 1879052333418024969;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744277;
DELETE FROM `sys_menu` WHERE menu_id = 1879052333418024970;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744279;
DELETE FROM `sys_menu` WHERE menu_id = 1879052428909744280;
DELETE FROM `sys_menu` WHERE menu_id = 1882320660345131008;
DELETE FROM `sys_menu` WHERE menu_id = 1882320825722343424;
DELETE FROM `sys_menu` WHERE menu_id = 1882320959277371475;
DELETE FROM `sys_menu` WHERE menu_id = 1882320959277371476;
DELETE FROM `sys_menu` WHERE menu_id = 1882320959277371477;
DELETE FROM `sys_menu` WHERE menu_id = 1882320959277371478;
DELETE FROM `sys_menu` WHERE menu_id = 1882320959277371479;
DELETE FROM `sys_menu` WHERE menu_id = 1882320959277371480;
DELETE FROM `sys_menu` WHERE menu_id = 1882320959277371481;

-- ----------------------------
-- 以下记录用于移动端，这里的注释，是为了便于老用户进行手动数据补偿。
-- ----------------------------
DELETE FROM `sys_menu` WHERE menu_id = 1687821642446671872;
DELETE FROM `sys_menu` WHERE menu_id = 1688105082400280576;
DELETE FROM `sys_menu` WHERE menu_id = 1687821728979357696;

-- ----------------------------
-- 以下记录用于在线表单，这里的注释，是为了便于老用户进行手动数据补偿。
-- ----------------------------
DELETE FROM `sys_menu` WHERE menu_id = 1634009076981567488;
DELETE FROM `sys_menu` WHERE menu_id = 1392786950682841088;
DELETE FROM `sys_menu` WHERE menu_id = 1392786549942259712;
DELETE FROM `sys_menu` WHERE menu_id = 1392786476428693504;

-- ----------------------------
-- 以下记录用于报表打印，这里的注释，是为了便于老用户进行手动数据补偿。
-- ----------------------------
DELETE FROM `sys_menu` WHERE menu_id = 1534897180643430400;
DELETE FROM `sys_menu` WHERE menu_id = 1517063268609298432;
DELETE FROM `sys_menu` WHERE menu_id = 1516291375656603648;
DELETE FROM `sys_menu` WHERE menu_id = 1515858207237476352;
DELETE FROM `sys_menu` WHERE menu_id = 1515858116057501696;
DELETE FROM `sys_menu` WHERE menu_id = 1515857992501694464;

-- ----------------------------
-- 以下记录用于工作流，这里的注释，是为了便于老用户进行手动数据补偿。
-- ----------------------------
DELETE FROM `sys_menu` WHERE menu_id = 1418057714138877952;
DELETE FROM `sys_menu` WHERE menu_id = 1418059005175009280;
DELETE FROM `sys_menu` WHERE menu_id = 1418057835631087616;
DELETE FROM `sys_menu` WHERE menu_id = 1418057835631087617;
DELETE FROM `sys_menu` WHERE menu_id = 1418058289182150656;
DELETE FROM `sys_menu` WHERE menu_id = 1418058744037642240;
DELETE FROM `sys_menu` WHERE menu_id = 1418059167532322816;
DELETE FROM `sys_menu` WHERE menu_id = 1418059283920064512;
DELETE FROM `sys_menu` WHERE menu_id = 1423161217970606080;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
