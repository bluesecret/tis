package io.wangk.peekaboo.common.report.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.wangk.peekaboo.common.core.annotation.MyRequestBody;
import io.wangk.peekaboo.common.core.constant.ErrorCodeEnum;
import io.wangk.peekaboo.common.core.object.ResponseResult;
import io.wangk.peekaboo.common.core.object.TokenData;
import io.wangk.peekaboo.common.core.util.MyCommonUtil;
import io.wangk.peekaboo.common.core.util.MyModelUtil;
import io.wangk.peekaboo.common.log.annotation.OperationLog;
import io.wangk.peekaboo.common.log.model.constant.SysOperationLogType;
import io.wangk.peekaboo.common.report.dto.ReportPrintGroupDto;
import io.wangk.peekaboo.common.report.model.ReportPrint;
import io.wangk.peekaboo.common.report.model.ReportPrintGroup;
import io.wangk.peekaboo.common.report.service.ReportPrintGroupService;
import io.wangk.peekaboo.common.report.service.ReportPrintService;
import io.wangk.peekaboo.common.report.vo.ReportPrintGroupVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报表打印打印分组接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Tag(name = "报表打印打印分组接口")
@Slf4j
@RestController
@RequestMapping("${common-report.urlPrefix}/reportPrintGroup")
@ConditionalOnProperty(name = "common-report.operationEnabled", havingValue = "true")
public class ReportPrintGroupController {

    @Autowired
    private ReportPrintGroupService reportPrintGroupService;
    @Autowired
    private ReportPrintService reportPrintService;

    /**
     * 新增页面分组数据。
     *
     * @param reportPrintGroupDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {"reportPrintGroupDto.groupId"})
    @SaCheckPermission("reportPrint.all")
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody ReportPrintGroupDto reportPrintGroupDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(reportPrintGroupDto, false);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ReportPrintGroup reportPrintGroup = MyModelUtil.copyTo(reportPrintGroupDto, ReportPrintGroup.class);
        // 验证父Id的数据合法性
        ReportPrintGroup parentReportPrintGroup;
        if (MyCommonUtil.isNotBlankOrNull(reportPrintGroup.getParentId())) {
            parentReportPrintGroup = reportPrintGroupService.getById(reportPrintGroup.getParentId());
            if (parentReportPrintGroup == null) {
                errorMessage = "数据验证失败，关联的父节点并不存在，请刷新后重试！";
                return ResponseResult.error(ErrorCodeEnum.DATA_PARENT_ID_NOT_EXIST, errorMessage);
            }
        }
        reportPrintGroup = reportPrintGroupService.saveNew(reportPrintGroup);
        return ResponseResult.success(reportPrintGroup.getGroupId());
    }

    /**
     * 更新页面分组数据。
     *
     * @param reportPrintGroupDto 更新对象。
     * @return 应答结果对象。
     */
    @SaCheckPermission("reportPrint.all")
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody ReportPrintGroupDto reportPrintGroupDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(reportPrintGroupDto, true);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ReportPrintGroup reportPrintGroup = MyModelUtil.copyTo(reportPrintGroupDto, ReportPrintGroup.class);
        ResponseResult<ReportPrintGroup> verifyResult = this.doVerifyAndGet(reportPrintGroupDto.getGroupId());
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        ReportPrintGroup originalReportPrintGroup = verifyResult.getData();
        // 验证父Id的数据合法性
        if (MyCommonUtil.isNotBlankOrNull(reportPrintGroup.getParentId())
                && ObjectUtil.notEqual(reportPrintGroup.getParentId(), originalReportPrintGroup.getParentId())) {
            ReportPrintGroup parentReportPrintGroup = reportPrintGroupService.getById(reportPrintGroup.getParentId());
            if (parentReportPrintGroup == null) {
                // NOTE: 修改下面方括号中的话述
                errorMessage = "数据验证失败，关联的 [父节点] 并不存在，请刷新后重试！";
                return ResponseResult.error(ErrorCodeEnum.DATA_PARENT_ID_NOT_EXIST, errorMessage);
            }
        }
        if (!reportPrintGroupService.update(reportPrintGroup, originalReportPrintGroup)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除页面分组数据。
     *
     * @param groupId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @SaCheckPermission("reportPrint.all")
    @OperationLog(type = SysOperationLogType.DELETE)
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long groupId) {
        return this.doDelete(groupId);
    }

    /**
     * 列出符合过滤条件的页面分组列表。
     *
     * @return 应答结果对象，包含查询结果集。
     */
    @SaCheckPermission("reportPrint.all")
    @PostMapping("/list")
    public ResponseResult<List<ReportPrintGroupVo>> list() {
        List<ReportPrintGroup> reportPrintGroupList =
                reportPrintGroupService.getReportPrintGroupList(null, null);
        return ResponseResult.success(BeanUtil.copyToList(reportPrintGroupList, ReportPrintGroupVo.class));
    }

    /**
     * 查看指定页面分组对象详情。
     *
     * @param groupId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @SaCheckPermission("reportPrint.all")
    @GetMapping("/view")
    public ResponseResult<ReportPrintGroupVo> view(@RequestParam Long groupId) {
        ResponseResult<ReportPrintGroup> verifyResult = this.doVerifyAndGet(groupId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        ReportPrintGroupVo reportPrintGroupVo = BeanUtil.toBean(verifyResult.getData(), ReportPrintGroupVo.class);
        return ResponseResult.success(reportPrintGroupVo);
    }

    private ResponseResult<Void> doDelete(Long groupId) {
        String errorMessage;
        // 验证关联Id的数据合法性
        ResponseResult<ReportPrintGroup> verifyResult = this.doVerifyAndGet(groupId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        if (reportPrintGroupService.hasChildren(groupId)) {
            errorMessage = "数据验证失败，当前报表打印分组对象存在子分组，请先删除子分组！";
            return ResponseResult.error(ErrorCodeEnum.HAS_CHILDREN_DATA, errorMessage);
        }
        ReportPrint filter = new ReportPrint();
        filter.setGroupId(groupId);
        if (reportPrintService.getCountByFilter(filter) > 0) {
            errorMessage = "数据验证失败，当前报表打印分组对象包含打印模板，请先删除所属打印模板！";
            return ResponseResult.error(ErrorCodeEnum.HAS_CHILDREN_DATA, errorMessage);
        }
        if (!reportPrintGroupService.remove(groupId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    private ResponseResult<ReportPrintGroup> doVerifyAndGet(Long groupId) {
        if (MyCommonUtil.existBlankArgument(groupId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        ReportPrintGroup group = reportPrintGroupService.getById(groupId);
        if (group == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        TokenData tokenData = TokenData.takeFromRequest();
        if (ObjectUtil.notEqual(group.getTenantId(), tokenData.getTenantId())) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, "数据验证失败，当前租户并不存在该打印分组！");
        }
        if (!StrUtil.equals(group.getAppCode(), tokenData.getAppCode())) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, "数据验证失败，当前应用并不存在该打印分组！");
        }
        return ResponseResult.success(group);
    }
}
