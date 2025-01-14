package io.wangk.peekaboo.common.online.object;

import lombok.Data;

/**
 * 连接表信息对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class JoinTableInfo {

    /**
     * 是否左连接。
     */
    private Boolean leftJoin;

    /**
     * 连接表表名。
     */
    private String joinTableName;

    /**
     * 连接条件。
     */
    private String joinCondition;
}
