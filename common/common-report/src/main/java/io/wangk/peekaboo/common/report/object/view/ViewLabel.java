package io.wangk.peekaboo.common.report.object.view;

import lombok.Data;

/**
 * ViewLabel对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class ViewLabel {

    /**
     * 是否显示。
     */
    private Boolean hidden;

    /**
     * 标题。
     */
    private String title;

    /**
     * 标题字体。
     */
    private Integer fontSize;

    /**
     * 标题颜色。
     */
    private String color;

    /**
     * 位置（上中下）。
     */
    private Integer position;

    /**
     * 斜体。
     */
    private Boolean italics;

    /**
     * 粗体。
     */
    private Boolean bold;
}
