package io.wangk.peekaboo.common.log.annotation;

import java.lang.annotation.*;

/**
 * 忽略接口应答数据记录日志的注解。该注解会被OperationLogAspect处理。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreResponseLog {

}
