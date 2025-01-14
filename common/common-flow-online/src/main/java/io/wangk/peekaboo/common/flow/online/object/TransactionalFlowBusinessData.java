package io.wangk.peekaboo.common.flow.online.object;

import io.wangk.peekaboo.common.core.util.ContextUtil;
import io.wangk.peekaboo.common.online.object.TransactionalBusinessData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 跨库存储工作流的业务数据的事物性事件数据。
 *
 * @author wangk
 * @date 2025-01-14
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TransactionalFlowBusinessData extends TransactionalBusinessData {

    /**
     * 流程实例Id。
     */
    private String processInstanceId;
    /**
     * 流程任务Id。
     */
    private String taskId;
    /**
     * 流程任务定义标识。
     */
    private String taskKey;
    /**
     * 流程任务定义名称。
     */
    private String taskName;
    /**
     * 审批注释。
     */
    private String taskComment;

    private static final ThreadLocal<TransactionalFlowBusinessData> FLOW_BUSINESS_DATA_THREAD_LOCAL = new ThreadLocal<>();

    public TransactionalFlowBusinessData() {
        super();
    }

    public TransactionalFlowBusinessData(HttpServletRequest request) {
        super(request);
    }

    /**
     * 获取HttpServletRequest属性中的事物性时间对象。如果没有情趣对象，则从线程本地存储中获取。
     *
     * @return 返回设置在当前请求属性中的事务性时间对象。如果不存在则返回NULL。
     */
    public static TransactionalFlowBusinessData getFromRequestAttribute() {
        HttpServletRequest request = ContextUtil.getHttpRequest();
        if (request != null) {
            return (TransactionalFlowBusinessData) request.getAttribute(BUSINESS_DATA_ATTR_KEY);
        }
        return FLOW_BUSINESS_DATA_THREAD_LOCAL.get();
    }

    /**
     * 获取HttpServletRequest属性中的事物性时间对象。如果存在直接返回，否则创建新对象，并设置到当前请求的指定属性中。
     * 如果没有情趣对象，则从线程本地存储中创建。
     *
     * @return 返回设置在当前请求属性中的事务性时间对象。
     */
    public static TransactionalFlowBusinessData getOrCreateFromRequestAttribute() {
        HttpServletRequest request = ContextUtil.getHttpRequest();
        TransactionalFlowBusinessData data;
        if (request != null) {
            data = (TransactionalFlowBusinessData) request.getAttribute(BUSINESS_DATA_ATTR_KEY);
            if (data != null) {
                return data;
            }
            data = new TransactionalFlowBusinessData(request);
            request.setAttribute(BUSINESS_DATA_ATTR_KEY, data);
        } else {
            data = FLOW_BUSINESS_DATA_THREAD_LOCAL.get();
            if (data != null) {
                return data;
            }
            data = new TransactionalFlowBusinessData();
            FLOW_BUSINESS_DATA_THREAD_LOCAL.set(data);
        }
        return data;
    }

    /**
     * 移除该对象。
     */
    public static void removeFromRequestAttribute() {
        if (ContextUtil.hasRequestContext()) {
            ContextUtil.getHttpRequest().removeAttribute(BUSINESS_DATA_ATTR_KEY);
        } else {
            FLOW_BUSINESS_DATA_THREAD_LOCAL.remove();
        }
    }
}
