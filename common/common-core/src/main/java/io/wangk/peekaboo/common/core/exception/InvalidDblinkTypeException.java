package io.wangk.peekaboo.common.core.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 无效的数据库链接类型自定义异常。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InvalidDblinkTypeException extends RuntimeException {

    /**
     * 构造函数。
     *
     * @param dblinkType 数据库链接类型。
     */
    public InvalidDblinkTypeException(int dblinkType) {
        super("Invalid Dblink Type [" + dblinkType + "].");
    }
}
