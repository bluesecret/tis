package io.wangk.peekaboo.common.report.object.view;

import lombok.Data;

/**
 * ViewLine对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class ViewLine {

    /**
     * 线宽。
     */
    private Integer width;

    /**
     * 折点样式（圆形、方形、三角形、菱形）。
     */
    private Integer shape;

    /**
     * 折点大小。
     */
    private Integer pointSize;

    /**
     * 平滑折线。
     */
    private Boolean smooth;
}
