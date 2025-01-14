package io.wangk.peekaboo.common.flow.util;

import cn.hutool.core.util.StrUtil;
import io.wangk.peekaboo.common.core.object.TokenData;

/**
 * 工作流 Redis 键生成工具类。
 *
 * @author wangk
 * @date 2025-01-14
 */
public class FlowRedisKeyUtil {

    /**
     * 计算流程对象缓存在Redis中的键值。
     *
     * @param processDefinitionKey 流程标识。
     * @return 流程对象缓存在Redis中的键值。
     */
    public static String makeFlowEntryKey(String processDefinitionKey) {
        String prefix = "FLOW_ENTRY:";
        TokenData tokenData = TokenData.takeFromRequest();
        if (tokenData == null) {
            return prefix + processDefinitionKey;
        }
        String appCode = tokenData.getAppCode();
        if (StrUtil.isBlank(appCode)) {
            Long tenantId = tokenData.getTenantId();
            if (tenantId == null) {
                return prefix + processDefinitionKey;
            }
            return prefix + tenantId.toString() + ":" + processDefinitionKey;
        }
        return prefix + appCode + ":" + processDefinitionKey;
    }

    /**
     * 流程发布对象缓存在Redis中的键值。
     *
     * @param flowEntryPublishId 流程发布主键Id。
     * @return 流程发布对象缓存在Redis中的键值。
     */
    public static String makeFlowEntryPublishKey(Long flowEntryPublishId) {
        return "FLOW_ENTRY_PUBLISH:" + flowEntryPublishId;
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private FlowRedisKeyUtil() {
    }
}
