package io.wangk.peekaboo.common.report.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 报表数据集分组实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "zz_report_dataset_group")
public class ReportDatasetGroup extends BaseReportGroup {

}
