package io.wangk.peekaboo.common.report.controller;

import io.wangk.peekaboo.common.log.annotation.IgnoreResponseLog;
import io.wangk.peekaboo.common.log.annotation.OperationLog;
import io.wangk.peekaboo.common.log.model.constant.SysOperationLogType;
import com.github.pagehelper.page.PageMethod;
import io.wangk.peekaboo.common.core.object.*;
import io.wangk.peekaboo.common.core.util.*;
import io.wangk.peekaboo.common.core.constant.*;
import io.wangk.peekaboo.common.core.annotation.MyRequestBody;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.wangk.peekaboo.common.report.dto.ReportVisualizationDto;
import io.wangk.peekaboo.common.report.model.ReportVisualization;
import io.wangk.peekaboo.common.report.object.ReportFilterParam;
import io.wangk.peekaboo.common.report.object.view.ViewDimensionData;
import io.wangk.peekaboo.common.report.object.view.ViewIndexData;
import io.wangk.peekaboo.common.report.object.view.ViewOrderData;
import io.wangk.peekaboo.common.report.service.ReportVisualizationService;
import io.wangk.peekaboo.common.report.util.ReportOperationHelper;
import io.wangk.peekaboo.common.report.vo.ReportVisualizationVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 报表可视化操作控制器类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Tag(name = "报表可视化管理接口")
@Slf4j
@RestController
@RequestMapping("${common-report.urlPrefix}/reportVisualization")
@ConditionalOnProperty(name = "common-report.isVisualization", havingValue = "true")
public class ReportVisualizationController {

    @Autowired
    private ReportVisualizationService reportVisualizationService;
    @Autowired
    private ReportOperationHelper reportOperationHelper;

    /**
     * 新增报表可视化数据。
     *
     * @param reportVisualizationDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {"reportVisualizationDto.visualId"})
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody ReportVisualizationDto reportVisualizationDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(reportVisualizationDto, false);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ReportVisualization reportVisualization = MyModelUtil.copyTo(reportVisualizationDto, ReportVisualization.class);
        reportVisualization = reportVisualizationService.saveNew(reportVisualization);
        return ResponseResult.success(reportVisualization.getVisualId());
    }

    /**
     * 更新报表可视化数据。
     *
     * @param reportVisualizationDto 更新对象。
     * @return 应答结果对象。
     */
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody ReportVisualizationDto reportVisualizationDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(reportVisualizationDto, true);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ReportVisualization reportVisualization = MyModelUtil.copyTo(reportVisualizationDto, ReportVisualization.class);
        ReportVisualization originalReportVisualization =
                reportVisualizationService.getById(reportVisualization.getVisualId());
        if (originalReportVisualization == null) {
            errorMessage = "数据验证失败，当前可视化数据并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!reportVisualizationService.update(reportVisualization, originalReportVisualization)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除报表可视化数据。
     *
     * @param visualId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @OperationLog(type = SysOperationLogType.DELETE)
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long visualId) {
        if (MyCommonUtil.existBlankArgument(visualId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        String errorMessage;
        if (!reportVisualizationService.remove(visualId)) {
            errorMessage = "数据操作失败，删除的可视化不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的报表可视化列表。
     *
     * @param reportVisualizationDtoFilter 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @IgnoreResponseLog
    @PostMapping("/list")
    public ResponseResult<MyPageData<ReportVisualizationVo>> list(
            @MyRequestBody ReportVisualizationDto reportVisualizationDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        ReportVisualization reportVisualizationFilter =
                MyModelUtil.copyTo(reportVisualizationDtoFilter, ReportVisualization.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, ReportVisualization.class);
        List<ReportVisualization> reportVisualizationList =
                reportVisualizationService.getReportVisualizationListWithRelation(reportVisualizationFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(reportVisualizationList, ReportVisualizationVo.class));
    }

    /**
     * 查看指定报表可视化对象详情。
     *
     * @param visualId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @IgnoreResponseLog
    @GetMapping("/view")
    public ResponseResult<ReportVisualizationVo> view(@RequestParam Long visualId) {
        ReportVisualization reportVisualization =
                reportVisualizationService.getByIdWithRelation(visualId, MyRelationParam.full());
        if (reportVisualization == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success(reportVisualization, ReportVisualizationVo.class);
    }

    /**
     * 大屏的数据渲染接口。
     *
     * @param datasetId           数据集Id。
     * @param dimensionDataList   维度数据列表。
     * @param indexDataList       指标数据列表。
     * @param datasetFilterParams 结果集的过滤参数列表。
     * @param filterParams        过滤参数列表。
     * @param orderDataList       排序参数列表。
     * @param pageParam           分页对象。
     * @return 分组数据集合。
     */
    @PostMapping("/render")
    public ResponseResult<MyPageData<Map<String, Object>>> render(
            @MyRequestBody(required = true) Long datasetId,
            @MyRequestBody List<ViewDimensionData> dimensionDataList,
            @MyRequestBody List<ViewIndexData> indexDataList,
            @MyRequestBody List<ReportFilterParam> datasetFilterParams,
            @MyRequestBody List<ReportFilterParam> filterParams,
            @MyRequestBody List<ViewOrderData> orderDataList,
            @MyRequestBody MyPageParam pageParam) {
        return reportOperationHelper.doProcessData(
                datasetId, dimensionDataList, indexDataList, datasetFilterParams, filterParams, orderDataList, pageParam);
    }
}
