package io.wangk.peekaboo.common.report.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.wangk.peekaboo.common.dbutil.object.SqlTableColumn;
import io.wangk.peekaboo.common.report.util.ReportDataSourceUtil;
import io.wangk.peekaboo.common.report.dto.ReportDblinkDto;
import io.wangk.peekaboo.common.report.model.ReportDblink;
import io.wangk.peekaboo.common.report.service.ReportDblinkService;
import io.wangk.peekaboo.common.report.vo.ReportDblinkVo;
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

import java.util.List;
import java.util.Map;

/**
 * 报表打印数据库链接接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Tag(name = "报表打印数据库链接接口")
@Slf4j
@RestController
@RequestMapping("${common-report.urlPrefix}/reportDblink")
@ConditionalOnProperty(name = "common-report.operationEnabled", havingValue = "true")
public class ReportDblinkController {

    @Autowired
    private ReportDblinkService reportDblinkService;
    @Autowired
    private ReportDataSourceUtil dataSourceUtil;

    /**
     * 新增数据库链接数据。
     *
     * @param reportDblinkDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {"reportDblinkDto.dblinkId"})
    @SaCheckPermission("reportDblink.all")
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody ReportDblinkDto reportDblinkDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(reportDblinkDto, false);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ReportDblink reportDblink = MyModelUtil.copyTo(reportDblinkDto, ReportDblink.class);
        reportDblink = reportDblinkService.saveNew(reportDblink);
        return ResponseResult.success(reportDblink.getDblinkId());
    }

    /**
     * 更新数据库链接数据。
     *
     * @param reportDblinkDto 更新对象。
     * @return 应答结果对象。
     */
    @SaCheckPermission("reportDblink.all")
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody ReportDblinkDto reportDblinkDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(reportDblinkDto, true);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ReportDblink reportDblink = MyModelUtil.copyTo(reportDblinkDto, ReportDblink.class);
        ResponseResult<ReportDblink> verifyResult = this.doVerifyAndGet(reportDblinkDto.getDblinkId());
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        ReportDblink originalReportDblink = verifyResult.getData();
        if (ObjectUtil.notEqual(reportDblink.getDblinkType(), originalReportDblink.getDblinkType())) {
            errorMessage = "数据验证失败，不能修改数据库类型！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        String passwdKey = "password";
        JSONObject configJson = JSON.parseObject(reportDblink.getConfiguration());
        String password = configJson.getString(passwdKey);
        if (StrUtil.isNotBlank(password) && StrUtil.isAllCharMatch(password, c -> '*' == c)) {
            password = JSON.parseObject(originalReportDblink.getConfiguration()).getString(passwdKey);
            configJson.put(passwdKey, password);
            reportDblink.setConfiguration(configJson.toJSONString());
        }
        if (!reportDblinkService.update(reportDblink, originalReportDblink)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除数据库链接数据。
     *
     * @param dblinkId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @SaCheckPermission("reportDblink.all")
    @OperationLog(type = SysOperationLogType.DELETE)
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long dblinkId) {
        String errorMessage;
        // 验证关联Id的数据合法性
        ResponseResult<ReportDblink> verifyResult = this.doVerifyAndGet(dblinkId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        if (!reportDblinkService.remove(dblinkId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的数据库链接列表。
     *
     * @param reportDblinkDtoFilter 过滤对象。
     * @param orderParam            排序参数。
     * @param pageParam             分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @SaCheckPermission("reportDblink.all")
    @PostMapping("/list")
    public ResponseResult<MyPageData<ReportDblinkVo>> list(
            @MyRequestBody ReportDblinkDto reportDblinkDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        ReportDblink reportDblinkFilter = MyModelUtil.copyTo(reportDblinkDtoFilter, ReportDblink.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, ReportDblink.class);
        List<ReportDblink> reportDblinkList =
                reportDblinkService.getReportDblinkListWithRelation(reportDblinkFilter, orderBy);
        for (ReportDblink dblink : reportDblinkList) {
            this.maskOffPassword(dblink);
        }
        return ResponseResult.success(MyPageUtil.makeResponseData(reportDblinkList, ReportDblinkVo.class));
    }

    /**
     * 查看指定数据库链接对象详情。
     *
     * @param dblinkId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @SaCheckPermission("reportDblink.all")
    @GetMapping("/view")
    public ResponseResult<ReportDblinkVo> view(@RequestParam Long dblinkId) {
        ResponseResult<ReportDblink> verifyResult = this.doVerifyAndGet(dblinkId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        ReportDblink reportDblink = verifyResult.getData();
        reportDblinkService.buildRelationForData(reportDblink, MyRelationParam.full());
        if (!StrUtil.equals(reportDblink.getAppCode(), TokenData.takeFromRequest().getAppCode())) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, "数据验证失败，当前应用并不存在该数据库链接！");
        }
        this.maskOffPassword(reportDblink);
        return ResponseResult.success(reportDblink, ReportDblinkVo.class);
    }

    /**
     * 查看当前数据库链接下的所有表名称。
     *
     * @param dblinkId 数据库链接Id。
     * @return 数据表名列表。
     */
    @SaCheckPermission("reportDblink.all")
    @GetMapping("/listAllTables")
    public ResponseResult<List<String>> listAllTables(@RequestParam Long dblinkId) {
        ResponseResult<ReportDblink> verifyAndGet = this.doVerifyAndGet(dblinkId);
        if (!verifyAndGet.isSuccess()) {
            return ResponseResult.errorFrom(verifyAndGet);
        }
        List<String> allTables = reportDblinkService.getAllTables(verifyAndGet.getData());
        return ResponseResult.success(allTables);
    }

    /**
     * 查看指定数据库链接内的数据表的字段列表。
     *
     * @param dblinkId  数据库链接Id。
     * @param tableName 表名。
     * @return 字段列表。
     */
    @SaCheckPermission("reportDblink.all")
    @GetMapping("/listTableColumn")
    public ResponseResult<List<SqlTableColumn>> listAllTables(
            @RequestParam Long dblinkId, @RequestParam String tableName) {
        ResponseResult<ReportDblink> verifyAndGet = this.doVerifyAndGet(dblinkId);
        if (!verifyAndGet.isSuccess()) {
            return ResponseResult.errorFrom(verifyAndGet);
        }
        List<SqlTableColumn> columnList = reportDblinkService.getTableColumnList(verifyAndGet.getData(), tableName);
        return ResponseResult.success(columnList);
    }

    /**
     * 以字典形式返回全部数据库链接数据集合。字典的键值为[dblinkId, name]。
     * 白名单接口，登录用户均可访问。
     *
     * @param filter 过滤对象。
     * @return 应答结果对象，包含的数据为 List<Map<String, String>>，map中包含两条记录，key的值分别是id和name，value对应具体数据。
     */
    @GetMapping("/listDict")
    public ResponseResult<List<Map<String, Object>>> listDict(@ParameterObject ReportDblinkDto filter) {
        List<ReportDblink> resultList =
                reportDblinkService.getReportDblinkList(MyModelUtil.copyTo(filter, ReportDblink.class), null);
        return ResponseResult.success(
                MyCommonUtil.toDictDataList(resultList, ReportDblink::getDblinkId, ReportDblink::getDblinkName));
    }

    /**
     * 测试数据库链接的接口。
     *
     * @return 应答结果。
     */
    @GetMapping("/testConnection")
    public ResponseResult<Void> testConnection(@RequestParam Long dblinkId) {
        ResponseResult<ReportDblink> verifyAndGet = this.doVerifyAndGet(dblinkId);
        if (!verifyAndGet.isSuccess()) {
            return ResponseResult.errorFrom(verifyAndGet);
        }
        try {
            dataSourceUtil.testConnection(dblinkId);
            return ResponseResult.success();
        } catch (Exception e) {
            log.error("Failed to test connection with REPORT_DBLINK_ID [" + dblinkId + "]!", e);
            return ResponseResult.error(ErrorCodeEnum.DATA_ACCESS_FAILED, "数据库连接失败！");
        }
    }

    private void maskOffPassword(ReportDblink dblink) {
        String passwdKey = "password";
        JSONObject configJson = JSON.parseObject(dblink.getConfiguration());
        if (configJson.containsKey(passwdKey)) {
            String password = configJson.getString(passwdKey);
            if (StrUtil.isNotBlank(password)) {
                configJson.put(passwdKey, StrUtil.repeat('*', password.length()));
                dblink.setConfiguration(configJson.toJSONString());
            }
        }
    }

    private ResponseResult<ReportDblink> doVerifyAndGet(Long dblinkId) {
        if (MyCommonUtil.existBlankArgument(dblinkId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        ReportDblink reportDblink = reportDblinkService.getById(dblinkId);
        if (reportDblink == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        if (!StrUtil.equals(reportDblink.getAppCode(), TokenData.takeFromRequest().getAppCode())) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, "数据验证失败，当前应用并不存在该数据库链接！");
        }
        return ResponseResult.success(reportDblink);
    }
}
