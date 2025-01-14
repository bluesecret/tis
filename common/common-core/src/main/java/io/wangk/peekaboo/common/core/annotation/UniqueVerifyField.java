package io.wangk.peekaboo.common.core.annotation;

import java.lang.annotation.*;

/**
 * 唯一值验证字段注解。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UniqueVerifyField {

}
