package io.wangk.peekaboo.common.report.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 报表页面分组实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "zz_report_page_group")
public class ReportPageGroup extends BaseReportGroup {

    /**
     * 租户Id。非多租户系统该值为NULL。
     * 为了保持组件的兼容性，该字段始终为字符型。
     */
    @TableField(value = "tenant_id")
    private Long tenantId;
}
