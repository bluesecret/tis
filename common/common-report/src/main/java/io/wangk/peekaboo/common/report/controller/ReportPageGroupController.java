package io.wangk.peekaboo.common.report.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import io.wangk.peekaboo.common.report.dto.ReportPageGroupDto;
import io.wangk.peekaboo.common.report.model.ReportPage;
import io.wangk.peekaboo.common.report.model.ReportPageGroup;
import io.wangk.peekaboo.common.report.service.ReportPageGroupService;
import io.wangk.peekaboo.common.report.service.ReportPageService;
import io.wangk.peekaboo.common.report.vo.ReportPageGroupVo;
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
 * 报表打印页面分组接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Tag(name = "报表打印页面分组接口")
@Slf4j
@RestController
@RequestMapping("${common-report.urlPrefix}/reportPageGroup")
@ConditionalOnProperty(name = "common-report.operationEnabled", havingValue = "true")
public class ReportPageGroupController {

    @Autowired
    private ReportPageGroupService reportPageGroupService;
    @Autowired
    private ReportPageService reportPageService;

    /**
     * 新增页面分组数据。
     *
     * @param reportPageGroupDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {"reportPageGroupDto.groupId"})
    @SaCheckPermission("reportPage.all")
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody ReportPageGroupDto reportPageGroupDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(reportPageGroupDto, false);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ReportPageGroup reportPageGroup = MyModelUtil.copyTo(reportPageGroupDto, ReportPageGroup.class);
        // 验证父Id的数据合法性
        ReportPageGroup parentReportPageGroup;
        if (MyCommonUtil.isNotBlankOrNull(reportPageGroup.getParentId())) {
            parentReportPageGroup = reportPageGroupService.getById(reportPageGroup.getParentId());
            if (parentReportPageGroup == null) {
                errorMessage = "数据验证失败，关联的父节点并不存在，请刷新后重试！";
                return ResponseResult.error(ErrorCodeEnum.DATA_PARENT_ID_NOT_EXIST, errorMessage);
            }
        }
        reportPageGroup = reportPageGroupService.saveNew(reportPageGroup);
        return ResponseResult.success(reportPageGroup.getGroupId());
    }

    /**
     * 更新页面分组数据。
     *
     * @param reportPageGroupDto 更新对象。
     * @return 应答结果对象。
     */
    @SaCheckPermission("reportPage.all")
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody ReportPageGroupDto reportPageGroupDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(reportPageGroupDto, true);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ReportPageGroup reportPageGroup = MyModelUtil.copyTo(reportPageGroupDto, ReportPageGroup.class);
        ResponseResult<ReportPageGroup> verifyResult = this.doVerifyAndGet(reportPageGroupDto.getGroupId());
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        ReportPageGroup originalReportPageGroup = verifyResult.getData();
        // 验证父Id的数据合法性
        if (MyCommonUtil.isNotBlankOrNull(reportPageGroup.getParentId())
                && ObjectUtil.notEqual(reportPageGroup.getParentId(), originalReportPageGroup.getParentId())) {
            ReportPageGroup parentReportPageGroup = reportPageGroupService.getById(reportPageGroup.getParentId());
            if (parentReportPageGroup == null) {
                // NOTE: 修改下面方括号中的话述
                errorMessage = "数据验证失败，关联的 [父节点] 并不存在，请刷新后重试！";
                return ResponseResult.error(ErrorCodeEnum.DATA_PARENT_ID_NOT_EXIST, errorMessage);
            }
        }
        if (!reportPageGroupService.update(reportPageGroup, originalReportPageGroup)) {
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
    @SaCheckPermission("reportPage.all")
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
    @SaCheckPermission("reportPage.all")
    @PostMapping("/list")
    public ResponseResult<List<ReportPageGroupVo>> list() {
        List<ReportPageGroup> reportPageGroupList = reportPageGroupService.getReportPageGroupList(null, null);
        return ResponseResult.success(BeanUtil.copyToList(reportPageGroupList, ReportPageGroupVo.class));
    }

    /**
     * 查看指定页面分组对象详情。
     *
     * @param groupId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @SaCheckPermission("reportPage.all")
    @GetMapping("/view")
    public ResponseResult<ReportPageGroupVo> view(@RequestParam Long groupId) {
        ResponseResult<ReportPageGroup> verifyResult = this.doVerifyAndGet(groupId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        ReportPageGroupVo reportPageGroupVo = BeanUtil.toBean(verifyResult.getData(), ReportPageGroupVo.class);
        return ResponseResult.success(reportPageGroupVo);
    }

    /**
     * 获取所有页面分组数据和全部页面数据。
     * 白名单接口。
     *
     * @return 应答数据结果。
     */
    @PostMapping("/listAll")
    public ResponseResult<JSONObject> listAll() {
        List<ReportPageGroup> reportPageGroupList = reportPageGroupService.getReportPageGroupList(null, null);
        List<ReportPage> reportPageList = reportPageService.getReportPageList(null, null);
        JSONObject resultData = new JSONObject();
        resultData.put("reportPageGroupList", reportPageGroupList);
        resultData.put("reportPageList", reportPageList);
        return ResponseResult.success(resultData);
    }

    private ResponseResult<Void> doDelete(Long groupId) {
        String errorMessage;
        // 验证关联Id的数据合法性
        ResponseResult<ReportPageGroup> verifyResult = this.doVerifyAndGet(groupId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        if (reportPageGroupService.hasChildren(groupId)) {
            errorMessage = "数据验证失败，当前报表页面分组对象存在子分组，请先删除下级分组！";
            return ResponseResult.error(ErrorCodeEnum.HAS_CHILDREN_DATA, errorMessage);
        }
        ReportPage filter = new ReportPage();
        filter.setGroupId(groupId);
        if (reportPageService.getCountByFilter(filter) > 0) {
            errorMessage = "数据验证失败，当前报表页面分组包含页面数据，请先删除所属页面！";
            return ResponseResult.error(ErrorCodeEnum.HAS_CHILDREN_DATA, errorMessage);
        }
        if (!reportPageGroupService.remove(groupId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    private ResponseResult<ReportPageGroup> doVerifyAndGet(Long groupId) {
        if (MyCommonUtil.existBlankArgument(groupId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        ReportPageGroup group = reportPageGroupService.getById(groupId);
        if (group == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        TokenData tokenData = TokenData.takeFromRequest();
        if (ObjectUtil.notEqual(group.getTenantId(), tokenData.getTenantId())) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, "数据验证失败，当前租户并不存在该页面分组！");
        }
        if (!StrUtil.equals(group.getAppCode(), tokenData.getAppCode())) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, "数据验证失败，当前应用并不存在该页面分组！");
        }
        return ResponseResult.success(group);
    }
}
