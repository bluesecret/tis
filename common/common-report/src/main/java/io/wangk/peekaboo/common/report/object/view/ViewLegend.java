package io.wangk.peekaboo.common.report.object.view;

import lombok.Data;

/**
 * ViewLegend对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class ViewLegend {

    /**
     * 是否显示。
     */
    private Boolean hidden;

    /**
     * 标题。
     */
    private String title;

    /**
     * 标题颜色。
     */
    private String color;

    /**
     * 标题字号。
     */
    private Integer fontSize;

    /**
     * 位置（左中右）。
     */
    private Integer poaition;

    /**
     * 斜体。
     */
    private Boolean italic;

    /**
     * 粗体。
     */
    private Boolean bold;
}
