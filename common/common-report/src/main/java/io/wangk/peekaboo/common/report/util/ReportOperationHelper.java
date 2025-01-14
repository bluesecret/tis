package io.wangk.peekaboo.common.report.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.wangk.peekaboo.common.core.constant.ErrorCodeEnum;
import io.wangk.peekaboo.common.core.constant.FieldFilterType;
import io.wangk.peekaboo.common.core.exception.MyRuntimeException;
import io.wangk.peekaboo.common.core.object.*;
import io.wangk.peekaboo.common.dict.service.GlobalDictService;
import io.wangk.peekaboo.common.report.constant.PrintRenderType;
import io.wangk.peekaboo.common.report.model.ReportDataset;
import io.wangk.peekaboo.common.report.model.ReportDatasetColumn;
import io.wangk.peekaboo.common.report.model.ReportPrint;
import io.wangk.peekaboo.common.report.model.constant.CalculateType;
import io.wangk.peekaboo.common.report.model.constant.DatasetType;
import io.wangk.peekaboo.common.report.model.constant.OrderType;
import io.wangk.peekaboo.common.report.object.ReportDatasetInfo;
import io.wangk.peekaboo.common.report.object.ReportFilterParam;
import io.wangk.peekaboo.common.report.object.ReportPrintParam;
import io.wangk.peekaboo.common.report.object.view.ViewDimensionData;
import io.wangk.peekaboo.common.report.object.view.ViewIndexData;
import io.wangk.peekaboo.common.report.object.view.ViewOrderData;
import io.wangk.peekaboo.common.report.service.ReportDatasetService;
import io.wangk.peekaboo.common.report.service.ReportDictService;
import io.wangk.peekaboo.common.report.service.ReportOperationService;
import io.wangk.peekaboo.common.report.service.ReportPrintService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 报表打印模块的运行时帮助类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Component
public class ReportOperationHelper {

    @Autowired
    private ReportPrintService reportPrintService;
    @Autowired
    private ReportDatasetService reportDatasetService;
    @Autowired
    private ReportDictService reportDictService;
    @Autowired
    private GlobalDictService globalDictService;
    @Autowired
    private ReportOperationService reportOperationService;

    /**
     * 根据打印模板，数据参数等信息，执行打印渲染，该操作会将渲染结果通过HTTP应答对象返回给前端。
     *
     * @param printId    打印模板Id。
     * @param printParam 打印参数。
     * @param renderType 渲染类型，具体值参考PrintRenderType常量类。
     */
    public void print(Long printId, ReportPrintParam printParam, int renderType) throws IOException {
        this.print(printId, CollUtil.newLinkedList(printParam), renderType);
    }

    /**
     * 根据打印模板，数据参数等信息，执行批量打印渲染，该操作会将渲染结果通过HTTP应答对象返回给前端。
     *
     * @param printId     打印模板Id。
     * @param printParams 打印参数列表。
     * @param renderType  渲染类型，具体值参考PrintRenderType常量类。
     */
    public void print(Long printId, List<ReportPrintParam> printParams, int renderType) throws IOException {
        String errorMessage;
        ReportPrint reportPrint = reportPrintService.getById(printId);
        if (reportPrint == null) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
                    ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST));
            return;
        }
        if (renderType != PrintRenderType.WORD && StrUtil.isBlank(reportPrint.getTemplateDataJson())) {
            errorMessage = "数据验证失败，当前打印模板没有任何模板数据！";
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
                    ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage));
            return;
        }
        if (StrUtil.isBlank(reportPrint.getFragmentJson())) {
            errorMessage = "数据验证失败，没有配置任何打印模板片段！";
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
                    ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage));
            return;
        }
        CallResult renderResult = reportPrintService.render(reportPrint, printParams, renderType);
        if (!renderResult.isSuccess()) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN, ResponseResult.errorFrom(renderResult));
        }
    }

    public ResponseResult<MyPageData<Map<String, Object>>> doProcessData(
            Long datasetId,
            List<ViewDimensionData> dimensionDataList,
            List<ViewIndexData> indexDataList,
            List<ReportFilterParam> datasetFilterParams,
            List<ReportFilterParam> filterParams,
            List<ViewOrderData> orderDataList,
            MyPageParam pageParam) {
        String errorMessage;
        ReportDataset dataset = reportDatasetService.getReportDatasetFromCache(datasetId);
        if (dataset == null) {
            errorMessage = "数据验证失败，数据集Id不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        Set<Long> columnIdSet = dataset.getColumnList()
                .stream().map(ReportDatasetColumn::getColumnId).collect(Collectors.toSet());
        ResponseResult<Void> dimensionDataListVerifyResult =
                this.verifyDimensionDataList(dimensionDataList, columnIdSet);
        if (!dimensionDataListVerifyResult.isSuccess()) {
            return ResponseResult.errorFrom(dimensionDataListVerifyResult);
        }
        ResponseResult<Void> indexDataListVerifyResult = this.verifyIndexDataList(indexDataList, columnIdSet);
        if (!indexDataListVerifyResult.isSuccess()) {
            return ResponseResult.errorFrom(indexDataListVerifyResult);
        }
        ResponseResult<Void> datasetFilterParamsVerifyResult =
                this.verifyDatasetFilterParams(datasetFilterParams, dataset);
        if (!datasetFilterParamsVerifyResult.isSuccess()) {
            return ResponseResult.errorFrom(datasetFilterParamsVerifyResult);
        }
        ResponseResult<Void> filterParamsVerifyResult = this.verifyFilterParams(filterParams, dataset);
        if (!filterParamsVerifyResult.isSuccess()) {
            return ResponseResult.errorFrom(filterParamsVerifyResult);
        }
        ResponseResult<Void> orderDataListVerifyResult = this.verifyOrderDataList(orderDataList, columnIdSet);
        if (!orderDataListVerifyResult.isSuccess()) {
            return ResponseResult.errorFrom(orderDataListVerifyResult);
        }
        MyPageData<Map<String, Object>> resultList;
        if (dataset.getDatasetType().equals(DatasetType.API)) {
            resultList = this.getApiDataList(dataset, datasetFilterParams, orderDataList, pageParam);
        } else {
            if (CollUtil.isNotEmpty(dimensionDataList) && CollUtil.isNotEmpty(indexDataList)) {
                List<Map<String, Object>> dataList = reportOperationService.getDataListWithGroup(dataset,
                        dimensionDataList, indexDataList, datasetFilterParams, filterParams, orderDataList);
                resultList = new MyPageData<>(dataList, (long) dataList.size());
            } else {
                resultList = reportOperationService.getDataListWithPage(
                        dataset, datasetFilterParams, filterParams, orderDataList, pageParam);
            }
            reportDatasetService.buildDataListWithDict(resultList.getDataList(), dataset.getColumnList());
        }
        return ResponseResult.success(resultList);
    }

    /**
     * 获取API数据集的数据列表。
     * @param dataset             数据集对象。
     * @param datasetFilterParams 过滤参数集合。
     * @param orderDataList       排序数据集合。
     * @param pageParam           分页参数。
     * @return API数据集的数据列表。
     */
    public MyPageData<Map<String, Object>> getApiDataList(
            ReportDataset dataset,
            List<ReportFilterParam> datasetFilterParams,
            List<ViewOrderData> orderDataList,
            MyPageParam pageParam) {
        ReportDatasetInfo datasetInfo = JSON.parseObject(dataset.getDatasetInfo(), ReportDatasetInfo.class);
        // 为了保证更好的兼容性，我们在请求头和请求参数中，均添加了用于默认权限验证的数据。
        // headerMap中的Authorization是请求头中的验证数据，param中的Authorization则是请求参数中的验证数据。
        // Token值我们直接使用了当前用户登录时返回的Token数据。该方式对于橙单系统的API接口，是无需有任何修改的。
        Map<String, String> headerMap = new HashMap<>(1);
        headerMap.put("Authorization", TokenData.takeFromRequest().getToken());
        Map<String, Object> param = new HashMap<>(4);
        param.put("Authorization", TokenData.takeFromRequest().getToken());
        if (CollUtil.isNotEmpty(datasetInfo.getParamList())) {
            datasetInfo.getParamList().stream().filter(p -> p.getDefaultValue() != null)
                    .forEach(p -> param.put(p.getParamName(), StrUtil.replace(p.getDefaultValue(), " ", "%20")));
        }
        if (CollUtil.isNotEmpty(datasetFilterParams)) {
            datasetFilterParams.stream().filter(p -> p.getParamValue() != null)
                    .forEach(p -> param.put(p.getParamName(), StrUtil.replace(p.getParamValue(), " ", "%20")));
        }
        // 这里是排序数据列表，API接口的实现，需要自行解析该默认规则中的排序信息。
        if (CollUtil.isNotEmpty(orderDataList)) {
            String orderBy = JSON.toJSONString(orderDataList);
            orderBy = StrUtil.replace(orderBy, " ", "%20");
            param.put("orderParam", orderBy);
        }
        HttpResponse response;
        if (StrUtil.equalsIgnoreCase(datasetInfo.getMethod(), HttpMethod.GET.name())) {
            // 这里是GET请求的分页参数，API接口的实现，需要自行提取该默认分页信息。
            if (pageParam != null) {
                param.put("pageNum", pageParam.getPageNum());
                param.put("pageSize", pageParam.getPageSize());
            }
            response = HttpUtil.createGet(datasetInfo.getUrl()).form(param).addHeaders(headerMap).execute();
        } else {
            // 这里是POST请求的分页参数，API接口的实现，需要自行提取该默认分页信息。
            if (pageParam != null) {
                param.put("pageParam", pageParam);
            }
            headerMap.put("Content-Type", "application/json; charset=utf-8");
            String url = datasetInfo.getUrl() + "?Authorization=" + TokenData.takeFromRequest().getToken();
            response = HttpUtil.createPost(url).body(JSON.toJSONString(param)).addHeaders(headerMap).execute();
        }
        if (!response.isOk()) {
            throw new MyRuntimeException(response.body());
        }
        // 下面一行代码是对请求结果的JSON解析，返回的数据对象，必须和橙单的ResponseResult<MyPageData<Map<String, Object>>>
        // 类对象结构保持一致。即便API接口无需分页，也需要返回分页对象，MyPageData中的totalCount默认为数据列表的数据size。
        // 对于分页接口，totalCount则为总数据量。
        ResponseResult<MyPageData<Map<String, Object>>> responseResult =
                JSON.parseObject(response.body(), new TypeReference<ResponseResult<MyPageData<Map<String, Object>>>>() {
                });
        if (!responseResult.isSuccess()) {
            throw new MyRuntimeException(responseResult.getErrorMessage());
        }
        return responseResult.getData();
    }

    private ResponseResult<Void> verifyDimensionDataList(
            List<ViewDimensionData> dimensionDataList, Set<Long> allColumnIdSet) {
        if (CollUtil.isEmpty(dimensionDataList)) {
            return ResponseResult.success();
        }
        for (ViewDimensionData dimensionData : dimensionDataList) {
            if (!allColumnIdSet.contains(dimensionData.getColumnId())) {
                String errorMessage = StrFormatter.format(
                        "数据验证失败, 维度字段Id [{}] 在当前数据集中并不存在！", dimensionData.getColumnId());
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
        }
        return ResponseResult.success();
    }

    private ResponseResult<Void> verifyIndexDataList(List<ViewIndexData> indexDataList, Set<Long> allColumnIdSet) {
        if (CollUtil.isEmpty(indexDataList)) {
            return ResponseResult.success();
        }
        String errorMessage;
        for (ViewIndexData indexData : indexDataList) {
            if (!allColumnIdSet.contains(indexData.getColumnId())) {
                errorMessage = StrFormatter.format(
                        "数据验证失败, 指标字段Id [{}] 在当前数据集中并不存在！", indexData.getColumnId());
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
            if (!CalculateType.isValid(indexData.getCalculateType())) {
                errorMessage = StrFormatter.format(
                        "数据验证失败, 指标聚合类型值 [{}] 目前并不支持！", indexData.getCalculateType());
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
            if (CollUtil.isEmpty(indexData.getFilterParams())) {
                continue;
            }
            for (ReportFilterParam filterParam : indexData.getFilterParams()) {
                if (filterParam.getFilterType() < FieldFilterType.EQUAL
                        || filterParam.getFilterType() > FieldFilterType.LT) {
                    errorMessage = StrFormatter.format(
                            "数据验证失败, 指标字段Id [{}] 的过滤参数为 [{}] 目前并不支持！",
                            indexData.getColumnId(), filterParam.getFilterType());
                    return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
                }
            }
        }
        return ResponseResult.success();
    }

    private ResponseResult<Void> verifyDatasetFilterParams(
            List<ReportFilterParam> datasetFilterParams, ReportDataset dataset) {
        if (CollUtil.isEmpty(datasetFilterParams)
                || ObjectUtil.equal(dataset.getDatasetType(), DatasetType.TABLE)) {
            return ResponseResult.success();
        }
        String errorMessage;
        ReportDatasetInfo datasetInfo = JSON.parseObject(dataset.getDatasetInfo(), ReportDatasetInfo.class);
        if (datasetInfo.getAllParamNameSet() == null) {
            if (CollUtil.isEmpty(datasetInfo.getParamList())) {
                datasetInfo.setAllParamNameSet(new HashSet<>());
            } else {
                datasetInfo.setAllParamNameSet(datasetInfo.getParamList()
                        .stream().map(ReportDatasetInfo.SqlDatasetParam::getParamName).collect(Collectors.toSet()));
            }
        }
        for (ReportFilterParam filter : datasetFilterParams) {
            if (!CollUtil.contains(datasetInfo.getAllParamNameSet(), filter.getParamName())) {
                errorMessage = StrFormatter.format(
                        "数据验证失败, 过滤参数 [{}] 在当前SQL数据集中并不存在！", filter.getParamName());
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
        }
        return ResponseResult.success();
    }

    private ResponseResult<Void> verifyFilterParams(List<ReportFilterParam> filterParams, ReportDataset dataset) {
        if (CollUtil.isEmpty(filterParams)) {
            return ResponseResult.success();
        }
        String errorMessage;
        Set<String> columnNameSet = dataset.getColumnList()
                .stream().map(ReportDatasetColumn::getColumnName).collect(Collectors.toSet());
        for (ReportFilterParam filter : filterParams) {
            if (!columnNameSet.contains(filter.getParamName())) {
                errorMessage = StrFormatter.format(
                        "数据验证失败, 过滤字段 [{}] 在当前数据集中并不存在！", filter.getParamName());
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
            if (BooleanUtil.isTrue(filter.getRequired()) && StrUtil.isBlank(filter.getParamValue())) {
                errorMessage = StrFormatter.format(
                        "数据验证失败, 过滤字段 [{}] 是必填参数，过滤值不能为空！", filter.getParamName());
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
            if (!FieldFilterType.isValid(filter.getFilterType())) {
                errorMessage = StrFormatter.format(
                        "数据验证失败, 过滤字段 [{}] 包含无效过滤类型值 [{}]！",
                        filter.getParamName(), filter.getFilterType());
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
        }
        return ResponseResult.success();
    }

    private ResponseResult<Void> verifyOrderDataList(List<ViewOrderData> orderDataList, Set<Long> allColumnIdSet) {
        if (CollUtil.isEmpty(orderDataList)) {
            return ResponseResult.success();
        }
        String errorMessage;
        for (ViewOrderData orderData : orderDataList) {
            if (!allColumnIdSet.contains(orderData.getColumnId())) {
                errorMessage = StrFormatter.format(
                        "数据验证失败, 排序字段Id [{}] 在当前数据集中并不存在！", orderData.getColumnId());
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
            if (orderData.getCalculateType() != null
                    && !CalculateType.isValid(orderData.getCalculateType())) {
                errorMessage = StrFormatter.format(
                        "数据验证失败, 排序字段的指标聚合类型值 [{}] 目前并不支持！", orderData.getCalculateType());
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
            if (orderData.getOrderType() == null) {
                errorMessage = "数据验证失败, 排序字段的排序类型不能为空！";
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
            if (orderData.getOrderType() != OrderType.ASC && orderData.getOrderType() != OrderType.DESC) {
                errorMessage = "数据验证失败, 排序字段的排序类型错误！";
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
        }
        return ResponseResult.success();
    }
}
