package io.wangk.peekaboo.common.online.object;

import io.wangk.peekaboo.common.online.model.OnlineColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表字段数据对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColumnData {

    /**
     * 在线表字段对象。
     */
    private OnlineColumn column;

    /**
     * 字段值。
     */
    private Object columnValue;
}
