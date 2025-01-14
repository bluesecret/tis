package io.wangk.peekaboo.common.dict.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 租户全局系统字典项目Dto。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "租户全局系统字典项目Dto")
@EqualsAndHashCode(callSuper = true)
@Data
public class TenantGlobalDictItemDto extends GlobalDictItemDto {

}
