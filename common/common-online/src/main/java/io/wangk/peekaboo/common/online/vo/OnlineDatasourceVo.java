package io.wangk.peekaboo.common.online.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 在线表单的数据源VO对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "在线表单的数据源VO对象")
@Data
public class OnlineDatasourceVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long datasourceId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @Schema(description = "应用编码。为空时，表示非第三方应用接入")
    private String appCode;

    /**
     * 数据源名称。
     */
    @Schema(description = "数据源名称")
    private String datasourceName;

    /**
     * 数据源变量名，会成为数据访问url的一部分。
     */
    @Schema(description = "数据源变量名")
    private String variableName;

    /**
     * 数据库链接Id。
     */
    @Schema(description = "数据库链接Id")
    private Long dblinkId;

    /**
     * 主表Id。
     */
    @Schema(description = "主表Id")
    private Long masterTableId;

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
     * datasourceId 的多对多关联表数据对象，数据对应类型为OnlinePageDatasource。
     */
    @Schema(description = "datasourceId 的多对多关联表数据对象，数据对应类型为OnlinePageDatasource")
    private Map<String, Object> onlinePageDatasource;

    /**
     * masterTableId 字典关联数据。
     */
    @Schema(description = "masterTableId 字典关联数据")
    private Map<String, Object> masterTableIdDictMap;

    /**
     * 当前数据源及其关联，引用的数据表对象列表。
     */
    @Schema(description = "当前数据源及其关联，引用的数据表对象列表")
    private List<OnlineTableVo> tableList;
}
