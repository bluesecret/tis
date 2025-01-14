package io.wangk.peekaboo.common.report.object;

import lombok.Data;

/**
 * 打印配置信息对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class ReportPrintInfo {

    public static final String A4 = "A4";
    public static final String A5 = "A5";

    /**
     * 是否为自定义尺寸。
     */
    private boolean customSize = false;
    /**
     * 自定义宽度。
     */
    private Integer customWidth;
    /**
     * 自定义高度。
     */
    private Integer customHeight;
    /**
     * 横向为true，否则false。
     */
    private boolean landscape;
    /**
     * 纸张类型(A4/A5)。
     */
    private String paperType;
    /**
     * 顶边距。
     */
    private Integer topMargin;
    /**
     * 底边距。
     */
    private Integer bottomMargin;
    /**
     * 左边距。
     */
    private Integer leftMargin;
    /**
     * 右边距。
     */
    private Integer rightMargin;
}
