package io.wangk.peekaboo.common.dict.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 租户全局系统字典Vo。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "租户全局系统字典Vo")
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantGlobalDictVo extends GlobalDictVo {

    /**
     * 是否为所有租户的通用字典。
     */
    @Schema(description = "是否为所有租户的通用字典")
    private Boolean tenantCommon;

    /**
     * 租户的非公用字典的初始化字典数据。
     */
    @Schema(description = "租户的非公用字典的初始化字典数据")
    private String initialData;
}
