package io.wangk.peekaboo.common.online.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 在线表单所在页面VO对象。这里我们可以把页面理解为表单的容器。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "在线表单所在页面VO对象")
@Data
public class OnlinePageVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long pageId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @Schema(description = "应用编码。为空时，表示非第三方应用接入")
    private String appCode;

    /**
     * 页面编码。
     */
    @Schema(description = "页面编码")
    private String pageCode;

    /**
     * 页面名称。
     */
    @Schema(description = "页面名称")
    private String pageName;

    /**
     * 页面类型。
     */
    @Schema(description = "页面类型")
    private Integer pageType;

    /**
     * 扩展数据。
     */
    @Schema(description = "扩展数据")
    private String extraJson;

    /**
     * 页面编辑状态。
     */
    @Schema(description = "页面编辑状态")
    private Integer status;

    /**
     * 是否发布。
     */
    @Schema(description = "是否发布")
    private Boolean published;

    /**
     * 创建时间。
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 创建者。
     */
    @Schema(description = "创建者")
    private Long createUserId;

    /**
     * 更新时间。
     */
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * 更新者。
     */
    @Schema(description = "更新者")
    private Long updateUserId;

    /**
     * pageType 常量字典关联数据。
     */
    @Schema(description = "pageType 常量字典关联数据")
    private Map<String, Object> pageTypeDictMap;

    /**
     * status 常量字典关联数据。
     */
    @Schema(description = "status 常量字典关联数据")
    private Map<String, Object> statusDictMap;
}
