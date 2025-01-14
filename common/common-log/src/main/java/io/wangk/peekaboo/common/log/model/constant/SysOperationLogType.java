package io.wangk.peekaboo.common.log.model.constant;

/**
 * 操作日志类型常量字典对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
public final class SysOperationLogType {

    /**
     * 其他。
     */
    public static final int OTHER = -1;
    /**
     * 登录。
     */
    public static final int LOGIN = 0;
    /**
     * 登录移动端。
     */
    public static final int LOGIN_MOBILE = 1;
    /**
     * 登出。
     */
    public static final int LOGOUT = 5;
    /**
     * 登出移动端。
     */
    public static final int LOGOUT_MOBILE = 6;
    /**
     * 新增。
     */
    public static final int ADD = 10;
    /**
     * 修改。
     */
    public static final int UPDATE = 15;
    /**
     * 删除。
     */
    public static final int DELETE = 20;
    /**
     * 批量删除。
     */
    public static final int DELETE_BATCH = 21;
    /**
     * 新增多对多关联。
     */
    public static final int ADD_M2M = 25;
    /**
     * 移除多对多关联。
     */
    public static final int DELETE_M2M = 30;
    /**
     * 批量移除多对多关联。
     */
    public static final int DELETE_M2M_BATCH = 31;
    /**
     * 查询。
     */
    public static final int LIST = 35;
    /**
     * 分组查询。
     */
    public static final int LIST_WITH_GROUP = 40;
    /**
     * 导出。
     */
    public static final int EXPORT = 45;
    /**
     * 导入。
     */
    public static final int IMPORT = 46;
    /**
     * 上传。
     */
    public static final int UPLOAD = 50;
    /**
     * 下载。
     */
    public static final int DOWNLOAD = 55;
    /**
     * 重置缓存。
     */
    public static final int RELOAD_CACHE = 60;
    /**
     * 发布。
     */
    public static final int PUBLISH = 65;
    /**
     * 取消发布。
     */
    public static final int UNPUBLISH = 70;
    /**
     * 暂停。
     */
    public static final int SUSPEND = 75;
    /**
     * 恢复。
     */
    public static final int RESUME = 80;
    /**
     * 启动流程。
     */
    public static final int START_FLOW = 100;
    /**
     * 停止流程。
     */
    public static final int STOP_FLOW = 105;
    /**
     * 删除流程。
     */
    public static final int DELETE_FLOW = 110;
    /**
     * 取消流程。
     */
    public static final int CANCEL_FLOW = 115;
    /**
     * 提交任务。
     */
    public static final int SUBMIT_TASK = 120;
    /**
     * 催办任务。
     */
    public static final int REMIND_TASK = 125;
    /**
     * 干预任务。
     */
    public static final int INTERVENE_FLOW = 126;
    /**
     * 修复流程的业务数据。
     */
    public static final int FIX_FLOW_BUSINESS_DATA = 127;
    /**
     * 流程复活。
     */
    public static final int REVIVE_FLOW = 128;
    /**
     * 补偿自动化流程任务。
     */
    public static final int FIX_AUTO_FLOW_DATA = 129;

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private SysOperationLogType() {
    }
}
