package io.wangk.peekaboo.common.flow.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 工作流自动化任务的动作类型。
 *
 * @author wangk
 * @date 2025-01-14
 */
public class FlowAutoActionType {

    /**
     * 添加新数据。
     */
    public static final int ADD_NEW = 0;
    /**
     * 更新数据。
     */
    public static final int UPDATE = 1;
    /**
     * 删除数据。
     */
    public static final int DELETE = 2;
    /**
     * 查询单条数据。
     */
    public static final int SELECT_ONE = 3;
    /**
     * 聚合计算。
     */
    public static final int AGGREGATION_CALC = 5;
    /**
     * 数值计算。
     */
    public static final int NUMBER_CALC = 6;
    /**
     * HTTP请求调用
     */
    public static final int HTTP = 10;
    /**
     * 加载缓存。
     */
    public static final int LOAD_CACHE = 11;
    /**
     * 清空缓存。
     */
    public static final int DELETE_CACHE = 12;
    /**
     * 发送MQ消息。
     */
    public static final int SEND_MQ = 13;
    /**
     * 消费MQ消息。
     */
    public static final int CONSUME_MQ = 14;

    private static final Map<Integer, String> DICT_MAP = new HashMap<>(20);
    static {
        DICT_MAP.put(ADD_NEW, "添加新数据");
        DICT_MAP.put(UPDATE, "更新数据");
        DICT_MAP.put(DELETE, "删除数据");
        DICT_MAP.put(SELECT_ONE, "查询单条数据");
        DICT_MAP.put(AGGREGATION_CALC, "聚合计算");
        DICT_MAP.put(NUMBER_CALC, "数值计算");
        DICT_MAP.put(HTTP, "HTTP请求调用");
        DICT_MAP.put(LOAD_CACHE, "加载缓存");
        DICT_MAP.put(DELETE_CACHE, "清空缓存");
        DICT_MAP.put(SEND_MQ, "发送MQ消息");
        DICT_MAP.put(CONSUME_MQ, "消费MQ消息");
    }

    /**
     * 根据类型值返回显示值。
     *
     * @param flowActionType 类型值。
     * @return 对应的显示名。
     */
    public static String getShowNname(int flowActionType) {
        return DICT_MAP.get(flowActionType);
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private FlowAutoActionType() {
    }
}
