package io.wangk.peekaboo.common.flow.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.page.PageMethod;
import io.wangk.peekaboo.common.core.annotation.MyRequestBody;
import io.wangk.peekaboo.common.core.constant.ErrorCodeEnum;
import io.wangk.peekaboo.common.core.object.*;
import io.wangk.peekaboo.common.core.util.MyCommonUtil;
import io.wangk.peekaboo.common.core.util.MyModelUtil;
import io.wangk.peekaboo.common.core.util.MyPageUtil;
import io.wangk.peekaboo.common.dbutil.object.SqlTable;
import io.wangk.peekaboo.common.dbutil.object.SqlTableColumn;
import io.wangk.peekaboo.common.flow.dto.FlowDblinkDto;
import io.wangk.peekaboo.common.flow.model.FlowDblink;
import io.wangk.peekaboo.common.flow.service.FlowDblinkService;
import io.wangk.peekaboo.common.flow.util.FlowDataSourceUtil;
import io.wangk.peekaboo.common.flow.vo.FlowDblinkVo;
import io.wangk.peekaboo.common.log.annotation.OperationLog;
import io.wangk.peekaboo.common.log.model.constant.SysOperationLogType;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 工作流数据库链接接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Tag(name = "工作流数据库链接接口")
@Slf4j
@RestController
@RequestMapping("${common-flow.urlPrefix}/flowDblink")
@ConditionalOnProperty(name = "common-flow.operationEnabled", havingValue = "true")
public class FlowDblinkController {

    @Autowired
    private FlowDblinkService flowDblinkService;
    @Autowired
    private FlowDataSourceUtil dataSourceUtil;

    /**
     * 新增数据库链接数据。
     *
     * @param flowDblinkDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @SaCheckPermission("flowDblink.all")
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody FlowDblinkDto flowDblinkDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(flowDblinkDto, false);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        FlowDblink flowDblink = MyModelUtil.copyTo(flowDblinkDto, FlowDblink.class);
        flowDblink = flowDblinkService.saveNew(flowDblink);
        return ResponseResult.success(flowDblink.getDblinkId());
    }

    /**
     * 更新数据库链接数据。
     *
     * @param flowDblinkDto 更新对象。
     * @return 应答结果对象。
     */
    @SaCheckPermission("flowDblink.all")
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody FlowDblinkDto flowDblinkDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(flowDblinkDto, true);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        FlowDblink flowDblink = MyModelUtil.copyTo(flowDblinkDto, FlowDblink.class);
        ResponseResult<FlowDblink> verifyResult = this.doVerifyAndGet(flowDblinkDto.getDblinkId());
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        FlowDblink originalFlowDblink = verifyResult.getData();
        if (ObjectUtil.notEqual(flowDblink.getDblinkType(), originalFlowDblink.getDblinkType())) {
            errorMessage = "数据验证失败，不能修改数据库类型！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        String passwdKey = "password";
        JSONObject configJson = JSON.parseObject(flowDblink.getConfiguration());
        String password = configJson.getString(passwdKey);
        if (StrUtil.isNotBlank(password) && StrUtil.isAllCharMatch(password, c -> '*' == c)) {
            password = JSON.parseObject(originalFlowDblink.getConfiguration()).getString(passwdKey);
            configJson.put(passwdKey, password);
            flowDblink.setConfiguration(configJson.toJSONString());
        }
        if (!flowDblinkService.update(flowDblink, originalFlowDblink)) {
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
    @SaCheckPermission("flowDblink.all")
    @OperationLog(type = SysOperationLogType.DELETE)
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long dblinkId) {
        String errorMessage;
        // 验证关联Id的数据合法性
        ResponseResult<FlowDblink> verifyResult = this.doVerifyAndGet(dblinkId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        if (!flowDblinkService.remove(dblinkId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的数据库链接列表。
     *
     * @param flowDblinkDtoFilter 过滤对象。
     * @param orderParam            排序参数。
     * @param pageParam             分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @SaCheckPermission("flowDblink.all")
    @PostMapping("/list")
    public ResponseResult<MyPageData<FlowDblinkVo>> list(
            @MyRequestBody FlowDblinkDto flowDblinkDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        FlowDblink flowDblinkFilter = MyModelUtil.copyTo(flowDblinkDtoFilter, FlowDblink.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, FlowDblink.class);
        List<FlowDblink> flowDblinkList =
                flowDblinkService.getFlowDblinkListWithRelation(flowDblinkFilter, orderBy);
        for (FlowDblink dblink : flowDblinkList) {
            this.maskOffPassword(dblink);
        }
        return ResponseResult.success(MyPageUtil.makeResponseData(flowDblinkList, FlowDblinkVo.class));
    }

    /**
     * 查看指定数据库链接对象详情。
     *
     * @param dblinkId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @SaCheckPermission("flowDblink.all")
    @GetMapping("/view")
    public ResponseResult<FlowDblinkVo> view(@RequestParam Long dblinkId) {
        ResponseResult<FlowDblink> verifyResult = this.doVerifyAndGet(dblinkId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        FlowDblink flowDblink = verifyResult.getData();
        flowDblinkService.buildRelationForData(flowDblink, MyRelationParam.full());
        if (!StrUtil.equals(flowDblink.getAppCode(), TokenData.takeFromRequest().getAppCode())) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, "数据验证失败，当前应用并不存在该数据库链接！");
        }
        this.maskOffPassword(flowDblink);
        return ResponseResult.success(flowDblink, FlowDblinkVo.class);
    }

    /**
     * 获取指定数据库链接下的所有动态表单依赖的数据表列表。
     *
     * @param dblinkId 数据库链接Id。
     * @return 所有动态表单依赖的数据表列表
     */
    @SaCheckPermission("flowDblink.all")
    @GetMapping("/listDblinkTables")
    public ResponseResult<List<SqlTable>> listDblinkTables(@RequestParam Long dblinkId) {
        FlowDblink dblink = flowDblinkService.getById(dblinkId);
        if (dblink == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success(flowDblinkService.getDblinkTableList(dblink));
    }

    /**
     * 获取指定数据库链接下，指定数据表的所有字段信息。
     *
     * @param dblinkId  数据库链接Id。
     * @param tableName 表名。
     * @return 该表的所有字段列表。
     */
    @SaCheckPermission("flowDblink.all")
    @GetMapping("/listDblinkTableColumns")
    public ResponseResult<List<SqlTableColumn>> listDblinkTableColumns(
            @RequestParam Long dblinkId, @RequestParam String tableName) {
        FlowDblink dblink = flowDblinkService.getById(dblinkId);
        if (dblink == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success(flowDblinkService.getDblinkTableColumnList(dblink, tableName));
    }

    /**
     * 测试数据库链接的接口。
     *
     * @return 应答结果。
     */
    @GetMapping("/testConnection")
    public ResponseResult<Void> testConnection(@RequestParam Long dblinkId) {
        ResponseResult<FlowDblink> verifyAndGet = this.doVerifyAndGet(dblinkId);
        if (!verifyAndGet.isSuccess()) {
            return ResponseResult.errorFrom(verifyAndGet);
        }
        try {
            dataSourceUtil.testConnection(dblinkId);
            return ResponseResult.success();
        } catch (Exception e) {
            log.error("Failed to test connection with FLOW_DBLINK_ID [" + dblinkId + "]!", e);
            return ResponseResult.error(ErrorCodeEnum.DATA_ACCESS_FAILED, "数据库连接失败！");
        }
    }

    /**
     * 以字典形式返回全部数据库链接数据集合。字典的键值为[dblinkId, dblinkName]。
     * 白名单接口，登录用户均可访问。
     *
     * @param filter 过滤对象。
     * @return 应答结果对象，包含的数据为 List<Map<String, String>>，map中包含两条记录，key的值分别是id和name，value对应具体数据。
     */
    @GetMapping("/listDict")
    public ResponseResult<List<Map<String, Object>>> listDict(@ParameterObject FlowDblinkDto filter) {
        List<FlowDblink> resultList =
                flowDblinkService.getFlowDblinkList(MyModelUtil.copyTo(filter, FlowDblink.class), null);
        return ResponseResult.success(
                MyCommonUtil.toDictDataList(resultList, FlowDblink::getDblinkId, FlowDblink::getDblinkName));
    }

    private ResponseResult<FlowDblink> doVerifyAndGet(Long dblinkId) {
        if (MyCommonUtil.existBlankArgument(dblinkId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        FlowDblink flowDblink = flowDblinkService.getById(dblinkId);
        if (flowDblink == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        if (!StrUtil.equals(flowDblink.getAppCode(), TokenData.takeFromRequest().getAppCode())) {
            return ResponseResult.error(
                    ErrorCodeEnum.DATA_VALIDATED_FAILED, "数据验证失败，当前应用并不存在该数据库链接！");
        }
        return ResponseResult.success(flowDblink);
    }

    private void maskOffPassword(FlowDblink dblink) {
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
}
