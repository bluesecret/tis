package io.wangk.peekaboo.common.report.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import io.wangk.peekaboo.common.dict.util.GlobalDictOperationHelper;
import io.wangk.peekaboo.common.dict.dto.GlobalDictDto;
import io.wangk.peekaboo.common.dict.vo.GlobalDictVo;
import io.wangk.peekaboo.common.report.dto.ReportDictDto;
import io.wangk.peekaboo.common.report.dto.ReportDictFilterDto;
import io.wangk.peekaboo.common.report.model.ReportDict;
import io.wangk.peekaboo.common.report.service.ReportDictService;
import io.wangk.peekaboo.common.report.vo.ReportDictVo;
import io.wangk.peekaboo.common.core.annotation.MyRequestBody;
import io.wangk.peekaboo.common.core.constant.*;
import io.wangk.peekaboo.common.core.object.*;
import io.wangk.peekaboo.common.core.util.*;
import io.wangk.peekaboo.common.log.annotation.OperationLog;
import io.wangk.peekaboo.common.log.model.constant.SysOperationLogType;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 报表打印字典接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Tag(name = "报表打印字典接口")
@Slf4j
@RestController
@RequestMapping("${common-report.urlPrefix}/reportDict")
@ConditionalOnProperty(name = "common-report.operationEnabled", havingValue = "true")
public class ReportDictController {

    @Autowired
    private ReportDictService reportDictService;
    @Autowired
    private GlobalDictOperationHelper globalDictOperationHelper;

    /**
     * 新增报表字典数据。
     *
     * @param reportDictDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {"reportDictDto.dictId"})
    @SaCheckPermission("reportDict.all")
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody ReportDictDto reportDictDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(reportDictDto, false);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ReportDict reportDict = MyModelUtil.copyTo(reportDictDto, ReportDict.class);
        // 验证关联Id的数据合法性
        CallResult callResult = reportDictService.verifyRelatedData(reportDict, null);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        reportDict = reportDictService.saveNew(reportDict);
        return ResponseResult.success(reportDict.getDictId());
    }

    /**
     * 更新报表字典数据。
     *
     * @param reportDictDto 更新对象。
     * @return 应答结果对象。
     */
    @SaCheckPermission("reportDict.all")
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody ReportDictDto reportDictDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(reportDictDto, true);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ReportDict reportDict = MyModelUtil.copyTo(reportDictDto, ReportDict.class);
        ResponseResult<ReportDict> verifyResult = this.doVerifyAndGet(reportDictDto.getDictId());
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        ReportDict originalReportDict = verifyResult.getData();
        // 验证关联Id的数据合法性
        CallResult callResult = reportDictService.verifyRelatedData(reportDict, originalReportDict);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!reportDictService.update(reportDict, originalReportDict)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除报表字典数据。
     *
     * @param dictId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @SaCheckPermission("reportDict.all")
    @OperationLog(type = SysOperationLogType.DELETE)
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long dictId) {
        return this.doDelete(dictId);
    }

    /**
     * 列出符合过滤条件的报表字典列表。
     *
     * @param reportDictDtoFilter 过滤对象。
     * @param orderParam          排序参数。
     * @param pageParam           分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @SaCheckPermission("reportDict.all")
    @PostMapping("/list")
    public ResponseResult<MyPageData<ReportDictVo>> list(
            @MyRequestBody ReportDictDto reportDictDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        ReportDict reportDictFilter = MyModelUtil.copyTo(reportDictDtoFilter, ReportDict.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, ReportDict.class);
        List<ReportDict> reportDictList = reportDictService.getReportDictListWithRelation(reportDictFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(reportDictList, ReportDictVo.class));
    }

    /**
     * 查看指定报表字典对象详情。
     *
     * @param dictId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @SaCheckPermission("reportDict.all")
    @GetMapping("/view")
    public ResponseResult<ReportDictVo> view(@RequestParam Long dictId) {
        ResponseResult<ReportDict> verifyResult = this.doVerifyAndGet(dictId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        ReportDict reportDict = verifyResult.getData();
        reportDictService.buildRelationForData(reportDict, MyRelationParam.full());
        return ResponseResult.success(reportDict, ReportDictVo.class);
    }

    /**
     * 获取全部编码字典列表。
     * NOTE: 白名单接口。
     *
     * @param globalDictDtoFilter 过滤对象。
     * @param pageParam           分页参数。
     * @return 字典的数据列表。
     */
    @PostMapping("/listAllGlobalDict")
    public ResponseResult<MyPageData<GlobalDictVo>> listAllGlobalDict(
            @MyRequestBody GlobalDictDto globalDictDtoFilter,
            @MyRequestBody MyPageParam pageParam) {
        return globalDictOperationHelper.listAllGlobalDict(globalDictDtoFilter, pageParam);
    }

    /**
     * 获取字典的全部数据。
     * NOTE: 白名单接口。
     *
     * @param dictId        字典Id。
     * @param filterDtoList 过滤参数列表，目前仅支持数据表字典过滤。
     * @return 字典的数据列表。
     */
    @PostMapping("/listDictData")
    public ResponseResult<List<Map<String, Object>>> listDictData(
            @MyRequestBody(required = true) Long dictId, @MyRequestBody List<ReportDictFilterDto> filterDtoList) {
        ResponseResult<ReportDict> verifyResult = this.doVerifyAndGet(dictId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        List<Map<String, Object>> resultList = reportDictService.getDataList(verifyResult.getData(), filterDtoList);
        return ResponseResult.success(resultList);
    }

    /**
     * 以字典形式返回全部报表字典数据集合。字典的键值为[dictId, dictName]。
     * 白名单接口，登录用户均可访问。
     *
     * @param filter 过滤对象。
     * @return 应答结果对象，包含的数据为 List<Map<String, String>>，map中包含两条记录，key的值分别是id和name，value对应具体数据。
     */
    @GetMapping("/listDict")
    public ResponseResult<List<Map<String, Object>>> listDict(@ParameterObject ReportDictDto filter) {
        List<ReportDict> dataList =
                reportDictService.getReportDictList(MyModelUtil.copyTo(filter, ReportDict.class), null);
        if (CollUtil.isEmpty(dataList)) {
            return ResponseResult.success(new LinkedList<>());
        }
        List<Map<String, Object>> resultList = dataList.stream().map(item -> {
            Map<String, Object> dataMap = new HashMap<>(2);
            dataMap.put(ApplicationConstant.DICT_ID, item.getDictId());
            dataMap.put(ApplicationConstant.DICT_NAME, item.getDictName());
            dataMap.put("treeFlag", item.getTreeFlag());
            dataMap.put("dictDataJson", item.getDictDataJson());
            return dataMap;
        }).collect(Collectors.toList());
        return ResponseResult.success(resultList);
    }

    private ResponseResult<Void> doDelete(Long dictId) {
        String errorMessage;
        // 验证关联Id的数据合法性
        ResponseResult<ReportDict> verifyResult = this.doVerifyAndGet(dictId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        if (!reportDictService.remove(verifyResult.getData())) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    private ResponseResult<ReportDict> doVerifyAndGet(Long dictId) {
        if (MyCommonUtil.existBlankArgument(dictId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        String errorMessage;
        ReportDict dict = reportDictService.getReportDictFromCache(dictId);
        if (dict == null) {
            errorMessage = "数据验证失败，字典Id并不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!StrUtil.equals(dict.getAppCode(), TokenData.takeFromRequest().getAppCode())) {
            errorMessage = "数据验证失败，当前应用并不存在该报表字典！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        return ResponseResult.success(dict);
    }
}
