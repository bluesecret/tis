package io.wangk.peekaboo.common.report.object.view;

import lombok.Data;

/**
 * ViewBackground对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class ViewBackground {

    /**
     * 背景色。
     */
    private String color;

    /**
     * 透明度。
     */
    private Integer alpha;

    /**
     * 圆角大小。
     */
    private Integer radio;
}
