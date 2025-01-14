package io.wangk.peekaboo.common.report.object.view;

import lombok.Data;

/**
 * ViewPie对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class ViewPie {

    /**
     * 内径占比。
     */
    private Integer innerWidth;

    /**
     * 外径大小。
     */
    private Integer width;
}
