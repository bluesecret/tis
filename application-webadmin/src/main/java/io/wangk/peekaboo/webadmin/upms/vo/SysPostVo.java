package io.wangk.peekaboo.webadmin.upms.vo;

import io.wangk.peekaboo.common.core.base.vo.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 岗位VO对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "岗位VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysPostVo extends BaseVo {

    /**
     * 岗位Id。
     */
    @Schema(description = "岗位Id")
    private Long postId;

    /**
     * 岗位名称。
     */
    @Schema(description = "岗位名称")
    private String postName;

    /**
     * 岗位层级，数值越小级别越高。
     */
    @Schema(description = "岗位层级，数值越小级别越高")
    private Integer postLevel;

    /**
     * 是否领导岗位。
     */
    @Schema(description = "是否领导岗位")
    private Boolean leaderPost;

    /**
     * postId 的多对多关联表数据对象，数据对应类型为SysDeptPostVo。
     */
    @Schema(description = "postId 的多对多关联表数据对象")
    private Map<String, Object> sysDeptPost;
}
