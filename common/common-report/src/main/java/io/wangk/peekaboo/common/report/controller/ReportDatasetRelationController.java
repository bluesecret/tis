package io.wangk.peekaboo.common.report.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.hutool.core.util.StrUtil;
import io.wangk.peekaboo.common.report.dto.ReportDatasetRelationDto;
import io.wangk.peekaboo.common.report.model.ReportDatasetRelation;
import io.wangk.peekaboo.common.report.service.ReportDatasetRelationService;
import io.wangk.peekaboo.common.report.vo.ReportDatasetRelationVo;
import com.github.pagehelper.page.PageMethod;
import io.wangk.peekaboo.common.core.annotation.MyRequestBody;
import io.wangk.peekaboo.common.core.constant.*;
import io.wangk.peekaboo.common.core.object.*;
import io.wangk.peekaboo.common.core.util.*;
import io.wangk.peekaboo.common.log.annotation.OperationLog;
import io.wangk.peekaboo.common.log.model.constant.SysOperationLogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报表打印数据集关联接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Tag(name = "报表打印数据集关联接口")
@Slf4j
@RestController
@RequestMapping("${common-report.urlPrefix}/reportDatasetRelation")
@ConditionalOnProperty(name = "common-report.operationEnabled", havingValue = "true")
public class ReportDatasetRelationController {

    @Autowired
    private ReportDatasetRelationService reportDatasetRelationService;

    /**
     * 新增数据集关联数据。
     *
     * @param reportDatasetRelationDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {"reportDatasetRelationDto.relationId"})
    @SaCheckPermission("reportDataset.all")
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody ReportDatasetRelationDto reportDatasetRelationDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(reportDatasetRelationDto, false);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ReportDatasetRelation reportDatasetRelation = MyModelUtil.copyTo(reportDatasetRelationDto, ReportDatasetRelation.class);
        // 验证关联Id的数据合法性
        CallResult callResult = reportDatasetRelationService.verifyRelatedData(reportDatasetRelation, null);
        if (!callResult.isSuccess()) {
            return ResponseResult.errorFrom(callResult);
        }
        try {
            reportDatasetRelation = reportDatasetRelationService.saveNew(reportDatasetRelation);
        } catch (DuplicateKeyException e) {
            errorMessage = "数据验证失败，同一数据集的关联标识不能重复！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        return ResponseResult.success(reportDatasetRelation.getRelationId());
    }

    /**
     * 更新数据集关联数据。
     *
     * @param reportDatasetRelationDto 更新对象。
     * @return 应答结果对象。
     */
    @SaCheckPermission("reportDataset.all")
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody ReportDatasetRelationDto reportDatasetRelationDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(reportDatasetRelationDto, true);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ReportDatasetRelation reportDatasetRelation = MyModelUtil.copyTo(reportDatasetRelationDto, ReportDatasetRelation.class);
        ResponseResult<ReportDatasetRelation> verifyResult = this.doVerifyAndGet(reportDatasetRelation.getRelationId());
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        ReportDatasetRelation originalReportDatasetRelation = verifyResult.getData();
        // 验证关联Id的数据合法性
        CallResult callResult = reportDatasetRelationService.verifyRelatedData(reportDatasetRelation, originalReportDatasetRelation);
        if (!callResult.isSuccess()) {
            return ResponseResult.errorFrom(callResult);
        }
        try {
            if (!reportDatasetRelationService.update(reportDatasetRelation, originalReportDatasetRelation)) {
                return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
            }
        } catch (DuplicateKeyException e) {
            errorMessage = "数据验证失败，同一数据集的关联标识不能重复！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 删除数据集关联数据。
     *
     * @param relationId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @SaCheckPermission("reportDataset.all")
    @OperationLog(type = SysOperationLogType.DELETE)
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long relationId) {
        return this.doDelete(relationId);
    }

    /**
     * 列出符合过滤条件的数据集关联列表。
     *
     * @param reportDatasetRelationDtoFilter 过滤对象。
     * @param orderParam                     排序参数。
     * @param pageParam                      分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @SaCheckPermission("reportDataset.all")
    @PostMapping("/list")
    public ResponseResult<MyPageData<ReportDatasetRelationVo>> list(
            @MyRequestBody ReportDatasetRelationDto reportDatasetRelationDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        ReportDatasetRelation filter =
                MyModelUtil.copyTo(reportDatasetRelationDtoFilter, ReportDatasetRelation.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, ReportDatasetRelation.class);
        List<ReportDatasetRelation> relationList =
                reportDatasetRelationService.getReportDatasetRelationListWithRelation(filter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(relationList, ReportDatasetRelationVo.class));
    }

    /**
     * 查看指定数据集关联对象详情。
     *
     * @param relationId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @SaCheckPermission("reportDataset.all")
    @GetMapping("/view")
    public ResponseResult<ReportDatasetRelationVo> view(@RequestParam Long relationId) {
        ResponseResult<ReportDatasetRelation> verifyResult = this.doVerifyAndGet(relationId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        ReportDatasetRelation reportDatasetRelation = verifyResult.getData();
        reportDatasetRelationService.buildRelationForData(reportDatasetRelation, MyRelationParam.full());
        return ResponseResult.success(reportDatasetRelation, ReportDatasetRelationVo.class);
    }

    private ResponseResult<Void> doDelete(Long relationId) {
        String errorMessage;
        // 验证关联Id的数据合法性
        ResponseResult<ReportDatasetRelation> verifyResult = this.doVerifyAndGet(relationId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        if (!reportDatasetRelationService.remove(relationId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    private ResponseResult<ReportDatasetRelation> doVerifyAndGet(Long relationId) {
        if (MyCommonUtil.existBlankArgument(relationId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        ReportDatasetRelation relation = reportDatasetRelationService.getById(relationId);
        if (relation == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        if (!StrUtil.equals(relation.getAppCode(), TokenData.takeFromRequest().getAppCode())) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, "数据验证失败，当前应用并不存在该数据集关联！");
        }
        return ResponseResult.success(relation);
    }
}
