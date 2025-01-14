package io.wangk.peekaboo.common.core.annotation;

import java.lang.annotation.*;

/**
 * 标识Model和常量字典之间的关联关系。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RelationConstDict {

    /**
     * 是否为多选。多选为true时，返回的数据类型为List<Map<String, Object>>。
     *
     * @return true是，否则false。
     */
    boolean multiSelect() default false;

    /**
     * 当前对象的关联Id字段名称。
     *
     * @return 当前对象的关联Id字段名称。
     */
    String masterIdField();

    /**
     * 被关联的常量字典的Class对象。
     *
     * @return 关联的常量字典的Class对象。
     */
    Class<?> constantDictClass();
}
