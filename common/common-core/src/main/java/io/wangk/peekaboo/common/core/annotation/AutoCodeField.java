package io.wangk.peekaboo.common.core.annotation;

import java.lang.annotation.*;

/**
 * 自动编码字段的编码规则注解。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoCodeField {

    /**
     * 前缀。
     */
    String prefix() default "";

    /**
     * 精确到YEAR/MONTH/DAYS/HOURS/MINUTES/SECONDS
     */
    String precisionTo();

    /**
     * 中缀。
     */
    String middle() default "";

    /**
     * 流水序号的字符宽度，不足的前面补0。
     */
    int idWidth();
}
