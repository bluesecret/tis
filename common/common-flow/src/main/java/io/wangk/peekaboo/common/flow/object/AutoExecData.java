package io.wangk.peekaboo.common.flow.object;

import lombok.Data;

import java.util.List;

/**
 * 自动化任务的执行数据。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class AutoExecData {

    /**
     * 数据库链接ID。
     */
    private Long dblinkId;
    /**
     * 执行sql。
     */
    private String sql;
    /**
     * sql参数列表。
     */
    private List<Object> params;
}
