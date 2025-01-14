package io.wangk.peekaboo.common.report.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.wangk.peekaboo.common.report.dto.ReportDatasetColumnDto;
import io.wangk.peekaboo.common.report.model.ReportDatasetColumn;
import io.wangk.peekaboo.common.report.service.ReportDatasetColumnService;
import io.wangk.peekaboo.common.report.vo.ReportDatasetColumnVo;
import io.wangk.peekaboo.common.core.annotation.MyRequestBody;
import io.wangk.peekaboo.common.core.constant.*;
import io.wangk.peekaboo.common.core.object.*;
import io.wangk.peekaboo.common.core.util.*;
import io.wangk.peekaboo.common.log.annotation.OperationLog;
import io.wangk.peekaboo.common.log.model.constant.SysOperationLogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报表打印数据集字段接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Tag(name = "报表打印数据集字段接口")
@Slf4j
@RestController
@RequestMapping("${common-report.urlPrefix}/reportDatasetColumn")
@ConditionalOnProperty(name = "common-report.operationEnabled", havingValue = "true")
public class ReportDatasetColumnController {

    @Autowired
    private ReportDatasetColumnService reportDatasetColumnService;

    /**
     * 更新数据集字段数据。
     *
     * @param reportDatasetColumnDto 更新对象。
     * @return 应答结果对象。
     */
    @SaCheckPermission("reportDataset.all")
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody ReportDatasetColumnDto reportDatasetColumnDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(reportDatasetColumnDto, true);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ReportDatasetColumn reportDatasetColumn = MyModelUtil.copyTo(reportDatasetColumnDto, ReportDatasetColumn.class);
        ReportDatasetColumn originalReportDatasetColumn =
                reportDatasetColumnService.getById(reportDatasetColumn.getColumnId());
        if (originalReportDatasetColumn == null) {
            errorMessage = "数据验证失败，当前报表数据集字段并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        errorMessage = this.verifyColumnFlag(
                reportDatasetColumn, originalReportDatasetColumn, "logicDelete", "逻辑删除");
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (BooleanUtil.isTrue(reportDatasetColumn.getLogicDelete())
                && !StrUtil.equalsAny(reportDatasetColumn.getFieldType(), "Long", "Integer")) {
            errorMessage = "数据验证失败，逻辑删除字段必须为数值型！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        errorMessage = this.verifyColumnFlag(
                reportDatasetColumn, originalReportDatasetColumn, "deptFilter", "部门过滤");
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        errorMessage = this.verifyColumnFlag(
                reportDatasetColumn, originalReportDatasetColumn, "userFilter", "用户过滤");
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        errorMessage = this.verifyColumnFlag(
                reportDatasetColumn, originalReportDatasetColumn, "tenantFilter", "租户过滤");
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (BooleanUtil.isTrue(reportDatasetColumn.getTenantFilter())
                && !"Long".equals(reportDatasetColumn.getFieldType())) {
            errorMessage = "数据验证失败，自护过滤字段必须为长整型！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!reportDatasetColumnService.update(reportDatasetColumn, originalReportDatasetColumn)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 查看指定数据集字段对象详情。
     *
     * @param columnId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @SaCheckPermission("reportDataset.all")
    @GetMapping("/view")
    public ResponseResult<ReportDatasetColumnVo> view(@RequestParam Long columnId) {
        if (MyCommonUtil.existBlankArgument(columnId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        ReportDatasetColumn reportDatasetColumn =
                reportDatasetColumnService.getByIdWithRelation(columnId, MyRelationParam.full());
        if (reportDatasetColumn == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success(reportDatasetColumn, ReportDatasetColumnVo.class);
    }

    private String verifyColumnFlag(
            ReportDatasetColumn column, ReportDatasetColumn originalColumn, String fieldName, String fieldDesc) {
        Boolean value = (Boolean) ReflectUtil.getFieldValue(column, fieldName);
        Boolean originalValue = (Boolean) ReflectUtil.getFieldValue(originalColumn, fieldName);
        if (BooleanUtil.isTrue(value) && !value.equals(originalValue)) {
            if (BooleanUtil.isTrue(column.getPrimaryKey())) {
                return "数据验证失败，主键字段不能成为" + fieldDesc + "字段！";
            }
            List<ReportDatasetColumn> columnList =
                    reportDatasetColumnService.getReportDatasetColumnListByDatasetId(column.getDatasetId());
            if (columnList.stream().anyMatch(c -> BooleanUtil.isTrue((Boolean) ReflectUtil.getFieldValue(c, fieldName)))) {
                return "数据验证失败，不能同时存在多个" + fieldDesc + "，请刷新后重试！";
            }
        }
        return null;
    }
}
