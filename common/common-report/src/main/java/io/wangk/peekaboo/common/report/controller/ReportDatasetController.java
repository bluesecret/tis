package io.wangk.peekaboo.common.report.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.wangk.peekaboo.common.dbutil.object.DatasetParam;
import io.wangk.peekaboo.common.report.dto.ReportDatasetDto;
import io.wangk.peekaboo.common.report.model.ReportDataset;
import io.wangk.peekaboo.common.report.model.ReportDatasetColumn;
import io.wangk.peekaboo.common.report.model.constant.DatasetType;
import io.wangk.peekaboo.common.report.object.ReportDatasetInfo;
import io.wangk.peekaboo.common.report.object.ReportResultSet;
import io.wangk.peekaboo.common.report.service.ReportDatasetColumnService;
import io.wangk.peekaboo.common.report.service.ReportDatasetService;
import io.wangk.peekaboo.common.report.vo.ReportDatasetColumnVo;
import io.wangk.peekaboo.common.report.vo.ReportDatasetVo;
import com.github.pagehelper.page.PageMethod;
import io.wangk.peekaboo.common.core.annotation.MyRequestBody;
import io.wangk.peekaboo.common.core.constant.*;
import io.wangk.peekaboo.common.core.object.*;
import io.wangk.peekaboo.common.core.util.*;
import io.wangk.peekaboo.common.log.annotation.OperationLog;
import io.wangk.peekaboo.common.log.model.constant.SysOperationLogType;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 报表打印数据集接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Tag(name = "报表打印数据集接口")
@Slf4j
@RestController
@RequestMapping("${common-report.urlPrefix}/reportDataset")
@ConditionalOnProperty(name = "common-report.operationEnabled", havingValue = "true")
public class ReportDatasetController {

    @Autowired
    private ReportDatasetService reportDatasetService;
    @Autowired
    private ReportDatasetColumnService reportDatasetColumnService;

    /**
     * 新增报表数据集数据。
     *
     * @param reportDatasetDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @SaCheckPermission("reportDataset.all")
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody ReportDatasetDto reportDatasetDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(reportDatasetDto, false);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ReportDataset reportDataset = MyModelUtil.copyTo(reportDatasetDto, ReportDataset.class);
        // 验证关联Id的数据合法性
        CallResult callResult = reportDatasetService.verifyRelatedData(reportDataset, null);
        if (!callResult.isSuccess()) {
            return ResponseResult.errorFrom(callResult);
        }
        reportDatasetService.saveNew(reportDataset);
        return ResponseResult.success(reportDataset.getDatasetId());
    }

    /**
     * 更新报表数据集数据。
     *
     * @param reportDatasetDto 更新对象。
     * @return 应答结果对象。
     */
    @SaCheckPermission("reportDataset.all")
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody ReportDatasetDto reportDatasetDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(reportDatasetDto, true);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ReportDataset reportDataset = MyModelUtil.copyTo(reportDatasetDto, ReportDataset.class);
        ResponseResult<ReportDataset> verifyResult = this.doVerifyAndGet(reportDatasetDto.getDatasetId());
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        ReportDataset originalReportDataset = verifyResult.getData();
        if (!reportDataset.getDatasetType().equals(originalReportDataset.getDatasetType())) {
            errorMessage = "数据验证失败，当前数据集类型不能修改！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        // 验证关联Id的数据合法性
        CallResult callResult = reportDatasetService.verifyRelatedData(reportDataset, originalReportDataset);
        if (!callResult.isSuccess()) {
            return ResponseResult.errorFrom(callResult);
        }
        if (!reportDatasetService.update(reportDataset, originalReportDataset)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除报表数据集数据。
     *
     * @param datasetId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @SaCheckPermission("reportDataset.all")
    @OperationLog(type = SysOperationLogType.DELETE)
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody(required = true) Long datasetId) {
        return this.doDelete(datasetId);
    }

    /**
     * 列出符合过滤条件的报表数据集列表。
     *
     * @param reportDatasetDtoFilter 过滤对象。
     * @param orderParam             排序参数。
     * @param pageParam              分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @SaCheckPermission("reportDataset.all")
    @PostMapping("/list")
    public ResponseResult<MyPageData<ReportDatasetVo>> list(
            @MyRequestBody ReportDatasetDto reportDatasetDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        ReportDataset reportDatasetFilter = MyModelUtil.copyTo(reportDatasetDtoFilter, ReportDataset.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, ReportDataset.class);
        List<ReportDataset> reportDatasetList =
                reportDatasetService.getReportDatasetListWithRelation(reportDatasetFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(reportDatasetList, ReportDatasetVo.class));
    }

    /**
     * 查看指定报表数据集对象集合的详情。
     *
     * @param datasetIds 指定对象主键Id集合。
     * @return 应答结果对象。
     */
    @SaCheckPermission("reportDataset.all")
    @PostMapping("/listByIds")
    public ResponseResult<List<ReportDatasetVo>> listByIds(@MyRequestBody Set<Long> datasetIds) {
        List<ReportDataset> datasetList =
                reportDatasetService.getInListWithRelation(datasetIds, MyRelationParam.full());
        String appCode = TokenData.takeFromRequest().getAppCode();
        datasetList = datasetList.stream().filter(d -> StrUtil.equals(d.getAppCode(), appCode)).collect(Collectors.toList());
        return ResponseResult.success(MyModelUtil.copyCollectionTo(datasetList, ReportDatasetVo.class));
    }

    /**
     * 查看指定报表数据集对象详情。
     *
     * @param datasetId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<ReportDatasetVo> view(@RequestParam Long datasetId) {
        ResponseResult<ReportDataset> verifyResult = this.doVerifyAndGet(datasetId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        ReportDataset reportDataset = verifyResult.getData();
        reportDatasetService.buildRelationForData(reportDataset, MyRelationParam.full());
        if (CollUtil.isNotEmpty(reportDataset.getColumnList())) {
            List<ReportDatasetColumn> columnList = reportDataset.getColumnList().stream()
                    .sorted(Comparator.comparing(ReportDatasetColumn::getColumnShowOrder))
                    .collect(Collectors.toList());
            reportDataset.setColumnList(columnList);
        }
        ReportDatasetVo datasetVo = MyModelUtil.copyTo(reportDataset, ReportDatasetVo.class);
        if (reportDataset.getDatasetType().equals(DatasetType.API)
                && StrUtil.isNotBlank(reportDataset.getDatasetInfo())) {
            ReportDatasetInfo datasetInfo = JSON.parseObject(reportDataset.getDatasetInfo(), ReportDatasetInfo.class);
            datasetVo.setColumnList(datasetInfo.getColumnList());
        }
        return ResponseResult.success(datasetVo);
    }

    /**
     * 同步指定数据集的字段列表。
     *
     * @param datasetId 数据集Id。
     * @return 请求应答结果。
     */
    @SaCheckPermission("reportDataset.all")
    @PostMapping("/sync")
    public ResponseResult<List<ReportDatasetColumnVo>> sync(@MyRequestBody(required = true) Long datasetId) {
        ResponseResult<ReportDataset> verifyResult = this.doVerifyAndGet(datasetId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        CallResult syncResult = reportDatasetService.sync(verifyResult.getData());
        if (!syncResult.isSuccess()) {
            return ResponseResult.errorFrom(syncResult);
        }
        List<ReportDatasetColumn> columnList =
                reportDatasetColumnService.getReportDatasetColumnListByDatasetId(datasetId);
        return ResponseResult.success(MyModelUtil.copyCollectionTo(columnList, ReportDatasetColumnVo.class));
    }

    /**
     * 获取数据集的预览数据列表。
     *
     * @param datasetId 数据集Id。
     * @param pageParam 分页对象。
     * @return 数据集的数据列表。
     */
    @SaCheckPermission("reportDataset.all")
    @PostMapping("/previewDataset")
    public ResponseResult<ReportResultSet<Map<String, Object>>> previewDataset(
            @MyRequestBody(required = true) Long datasetId,
            @MyRequestBody MyPageParam pageParam) {
        ResponseResult<ReportDataset> verifyResult = this.doVerifyAndGet(datasetId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        ReportDataset reportDataset = verifyResult.getData();
        reportDatasetService.buildRelationForData(reportDataset, MyRelationParam.full());
        DatasetParam datasetParam = new DatasetParam();
        datasetParam.setPageParam(pageParam);
        ReportResultSet<Map<String, Object>> resultSet = reportDatasetService.getDataList(reportDataset, datasetParam);
        return ResponseResult.success(resultSet);
    }

    /**
     * 获取数据集指定字段的数据。
     *
     * @param datasetId 数据集Id。
     * @param columnId  指定返回的字段Id。
     * @return 数据集的数据列表。
     */
    @SaCheckPermission("reportDataset.all")
    @PostMapping("/listDataWithColumn")
    public ResponseResult<List<Map<String, Object>>> listDataWithColumn(
            @MyRequestBody(required = true) Long datasetId,
            @MyRequestBody(required = true) Long columnId) {
        ResponseResult<ReportDataset> verifyResult = this.doVerifyAndGet(datasetId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        ReportDataset reportDataset = verifyResult.getData();
        reportDatasetService.buildRelationForData(reportDataset, MyRelationParam.full());
        String columnName = null;
        for (ReportDatasetColumn column : reportDataset.getColumnList()) {
            if (column.getColumnId().equals(columnId)) {
                columnName = column.getColumnName();
                break;
            }
        }
        DatasetParam datasetParam = new DatasetParam();
        datasetParam.setDisableSqlDatasetFilter(true);
        datasetParam.setSelectColumnNameList(CollUtil.newArrayList(columnName));
        ReportResultSet<Map<String, Object>> resultSet =
                reportDatasetService.getDataList(reportDataset, datasetParam);
        if (CollUtil.isNotEmpty(resultSet.getDataList())) {
            String name = columnName;
            Set<Object> distinctDataSet = resultSet.getDataList().stream()
                    .filter(c -> c.get(name) != null)
                    .map(c -> c.get(name)).collect(Collectors.toCollection(TreeSet::new));
            List<Map<String, Object>> distinctDataList = new LinkedList<>();
            for (Object dataObject : distinctDataSet) {
                JSONObject o = new JSONObject();
                o.put(columnName, dataObject);
                distinctDataList.add(o);
            }
            resultSet.setDataList(distinctDataList);
            reportDatasetService.buildDataListWithDict(resultSet.getDataList(), resultSet.getColumnMetaList());
        }
        return ResponseResult.success(resultSet.getDataList());
    }

    /**
     * 以字典形式返回全部报表数据集数据集合。字典的键值为[datasetId, name]。
     * 白名单接口，登录用户均可访问。
     *
     * @param filter 过滤对象。
     * @return 应答结果对象，包含的数据为 List<Map<String, String>>，map中包含两条记录，key的值分别是id和name，value对应具体数据。
     */
    @GetMapping("/listDict")
    public ResponseResult<List<Map<String, Object>>> listDict(@ParameterObject ReportDatasetDto filter) {
        List<ReportDataset> resultList =
                reportDatasetService.getReportDatasetList(MyModelUtil.copyTo(filter, ReportDataset.class), null);
        return ResponseResult.success(MyCommonUtil.toDictDataList(
                resultList, ReportDataset::getDatasetId, ReportDataset::getDatasetName));
    }

    /**
     * 根据字典Id集合，获取查询后的字典数据。
     *
     * @param dictIds 字典Id集合。
     * @return 应答结果对象，包含字典形式的数据集合。
     */
    @GetMapping("/listDictByIds")
    public ResponseResult<List<Map<String, Object>>> listDictByIds(@RequestParam List<Long> dictIds) {
        List<ReportDataset> resultList = reportDatasetService.getInList(new HashSet<>(dictIds));
        String appCode = TokenData.takeFromRequest().getAppCode();
        resultList = resultList.stream().filter(d -> StrUtil.equals(d.getAppCode(), appCode)).collect(Collectors.toList());
        return ResponseResult.success(MyCommonUtil.toDictDataList(
                resultList, ReportDataset::getDatasetId, ReportDataset::getDatasetName));
    }

    private ResponseResult<Void> doDelete(Long datasetId) {
        String errorMessage;
        // 验证关联Id的数据合法性
        ResponseResult<ReportDataset> verifyResult = this.doVerifyAndGet(datasetId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        if (!reportDatasetService.remove(datasetId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    private ResponseResult<ReportDataset> doVerifyAndGet(Long datasetId) {
        if (MyCommonUtil.existBlankArgument(datasetId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        ReportDataset dataset = reportDatasetService.getById(datasetId);
        if (dataset == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        if (!StrUtil.equals(dataset.getAppCode(), TokenData.takeFromRequest().getAppCode())) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, "数据验证失败，当前应用并不存在该数据集！");
        }
        return ResponseResult.success(dataset);
    }
}
