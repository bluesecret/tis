package io.wangk.peekaboo.common.report.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.wangk.peekaboo.common.core.annotation.MyRequestBody;
import io.wangk.peekaboo.common.core.constant.ErrorCodeEnum;
import io.wangk.peekaboo.common.core.object.MyPageData;
import io.wangk.peekaboo.common.core.object.MyPageParam;
import io.wangk.peekaboo.common.core.object.ResponseResult;
import io.wangk.peekaboo.common.report.object.ReportFilterParam;
import io.wangk.peekaboo.common.report.object.view.ViewDimensionData;
import io.wangk.peekaboo.common.report.object.view.ViewIndexData;
import io.wangk.peekaboo.common.report.object.view.ViewOrderData;
import io.wangk.peekaboo.common.report.service.*;
import io.wangk.peekaboo.common.report.util.ReportOperationHelper;
import io.wangk.peekaboo.common.satoken.annotation.SaTokenDenyAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 报表打印数据操作接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Tag(name = "报表打印数据操作接口")
@Slf4j
@RestController
@RequestMapping("${common-report.urlPrefix}/reportOperation")
@ConditionalOnProperty(name = "common-report.operationEnabled", havingValue = "true")
public class ReportOperationController {

    @Autowired
    private ReportOperationService reportOperationService;
    @Autowired
    private ReportPageService reportPageService;
    @Autowired
    private ReportOperationHelper reportOperationHelper;

    /**
     * 编辑器中组件预览数据时，调用的获取分组数据列表接口。
     *
     * @param pageCode            页面编码。
     * @param datasetId           数据集Id。
     * @param dimensionDataList   维度数据列表。
     * @param indexDataList       指标数据列表。
     * @param datasetFilterParams 结果集的过滤参数列表。
     * @param filterParams        过滤参数列表。
     * @param orderDataList       排序参数列表。
     * @param pageParam           分页对象。
     * @return 分组数据集合。
     */
    @SaCheckPermission("reportPage.all")
    @PostMapping("/previewData")
    public ResponseResult<MyPageData<Map<String, Object>>> previewData(
            @MyRequestBody(required = true) String pageCode,
            @MyRequestBody(required = true) Long datasetId,
            @MyRequestBody List<ViewDimensionData> dimensionDataList,
            @MyRequestBody List<ViewIndexData> indexDataList,
            @MyRequestBody List<ReportFilterParam> datasetFilterParams,
            @MyRequestBody List<ReportFilterParam> filterParams,
            @MyRequestBody List<ViewOrderData> orderDataList,
            @MyRequestBody MyPageParam pageParam) {
        if (null == reportPageService.getReportPageFromCache(pageCode)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, "数据验证失败，报表页面编码不存在！");
        }
        return reportOperationHelper.doProcessData(
                datasetId, dimensionDataList, indexDataList, datasetFilterParams, filterParams, orderDataList, pageParam);
    }

    /**
     * 获取分组数据列表。
     *
     * @param pageCode            页面编码。
     * @param datasetId           数据集Id。
     * @param dimensionDataList   维度数据列表。
     * @param indexDataList       指标数据列表。
     * @param datasetFilterParams 结果集的过滤参数列表。
     * @param filterParams        过滤参数列表。
     * @param orderDataList       排序参数列表。
     * @param pageParam           分页对象。
     * @return 分组数据集合。
     */
    @SaTokenDenyAuth
    @PostMapping("/listData/{pageCode}")
    public ResponseResult<MyPageData<Map<String, Object>>> listData(
            @PathVariable("pageCode") String pageCode,
            @MyRequestBody(required = true) Long datasetId,
            @MyRequestBody List<ViewDimensionData> dimensionDataList,
            @MyRequestBody List<ViewIndexData> indexDataList,
            @MyRequestBody List<ReportFilterParam> datasetFilterParams,
            @MyRequestBody List<ReportFilterParam> filterParams,
            @MyRequestBody List<ViewOrderData> orderDataList,
            @MyRequestBody MyPageParam pageParam) {
        if (reportPageService.getReportPageFromCache(pageCode) == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, "数据验证失败，报表页面编码不存在！");
        }
        return reportOperationHelper.doProcessData(
                datasetId, dimensionDataList, indexDataList, datasetFilterParams, filterParams, orderDataList, pageParam);
    }

    /**
     * 获取报表表单所关联的权限数据。
     * 注：该接口仅用于微服务间调用使用，无需对前端开放。
     *
     * @param pageIdSet 页面Id集合。
     * @return 报表表单所关联的权限数据。
     */
    @GetMapping("/calculatePermData")
    public ResponseResult<Map<Long, Set<String>>> calculatePermData(@RequestParam Set<Long> pageIdSet) {
        return ResponseResult.success(reportOperationService.calculatePermData(pageIdSet));
    }
}
