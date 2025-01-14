package io.wangk.peekaboo.common.core.annotation;

import java.lang.annotation.*;

/**
 * 业务表中记录流程实例结束标记的字段。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FlowStatusColumn {

}
