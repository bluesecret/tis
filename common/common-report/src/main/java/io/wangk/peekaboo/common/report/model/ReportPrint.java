package io.wangk.peekaboo.common.report.model;

import com.baomidou.mybatisplus.annotation.*;
import io.wangk.peekaboo.common.core.annotation.UploadFlagColumn;
import io.wangk.peekaboo.common.core.upload.UploadStoreTypeEnum;
import io.wangk.peekaboo.common.report.object.ReportPrintFragment;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 打印模板实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_report_print")
public class ReportPrint {

    /**
     * 主键Id。
     */
    @TableId(value = "print_id")
    private Long printId;

    /**
     * 租户Id。非多租户系统该值为NULL。
     * 为了保持组件的兼容性，该字段始终为字符型。
     */
    @TableField(value = "tenant_id")
    private Long tenantId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @TableField(value = "app_code")
    private String appCode;

    /**
     * 名称。
     */
    @TableField(value = "print_name")
    private String printName;

    /**
     * 变量名。
     */
    @TableField(value = "print_variable")
    private String printVariable;

    /**
     * 打印类型。
     */
    @TableField(value = "print_type")
    private Integer printType;

    /**
     * word模板。
     */
    @UploadFlagColumn(storeType = UploadStoreTypeEnum.LOCAL_SYSTEM)
    @TableField(value = "word_template")
    private String wordTemplate;

    /**
     * 页面分组Id。
     */
    @TableField(value = "group_id")
    private Long groupId;

    /**
     * 打印配置JSON。
     */
    @TableField(value = "print_json")
    private String printJson;

    /**
     * 参数数据JSON。
     */
    @TableField(value = "param_json")
    private String paramJson;

    /**
     * 打印片段参数JSON。
     */
    @TableField(value = "fragment_json")
    private String fragmentJson;

    /**
     * luckysheet电子表单原始配置JSON。
     */
    @TableField(value = "sheet_data_json")
    private String sheetDataJson;

    /**
     * 电子表格解析后的打印模板配置数据JSON。
     */
    @TableField(value = "template_data_json")
    private String templateDataJson;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 创建者。
     */
    @TableField(value = "create_user_id")
    private Long createUserId;

    /**
     * 更新时间。
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 更新者。
     */
    @TableField(value = "update_user_id")
    private Long updateUserId;

    /**
     * 当前打印模板关联的全部数据集。
     */
    @TableField(exist = false)
    private Map<Long, ReportDataset> datasetMap;

    /**
     * 当前打印模板关联的模板片段列表。
     */
    @TableField(exist = false)
    private List<ReportPrintFragment> fragmentList;
}
