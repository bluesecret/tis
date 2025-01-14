package io.wangk.peekaboo.common.online.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 在线表单数据表所在数据库链接VO对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "在线表单数据表所在数据库链接VO对象")
@Data
public class OnlineDblinkVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long dblinkId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @Schema(description = "应用编码。为空时，表示非第三方应用接入")
    private String appCode;

    /**
     * 链接中文名称。
     */
    @Schema(description = "链接中文名称")
    private String dblinkName;

    /**
     * 链接描述。
     */
    @Schema(description = "链接描述")
    private String dblinkDescription;

    /**
     * 配置信息。
     */
    @Schema(description = "配置信息")
    private String configuration;

    /**
     * 数据库链接类型。
     */
    @Schema(description = "数据库链接类型")
    private Integer dblinkType;

    /**
     * 更新者。
     */
    @Schema(description = "更新者")
    private Long updateUserId;

    /**
     * 更新时间。
     */
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * 创建者。
     */
    @Schema(description = "创建者")
    private Long createUserId;

    /**
     * 创建时间。
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 数据库链接类型常量字典关联数据。
     */
    @Schema(description = "数据库链接类型常量字典关联数据")
    private Map<String, Object> dblinkTypeDictMap;
}
