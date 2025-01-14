package io.wangk.peekaboo.common.report.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import io.wangk.peekaboo.common.report.dto.ReportDatasetGroupDto;
import io.wangk.peekaboo.common.report.model.ReportDataset;
import io.wangk.peekaboo.common.report.model.ReportDatasetGroup;
import io.wangk.peekaboo.common.report.model.ReportDatasetRelation;
import io.wangk.peekaboo.common.report.service.ReportDatasetGroupService;
import io.wangk.peekaboo.common.report.service.ReportDatasetRelationService;
import io.wangk.peekaboo.common.report.service.ReportDatasetService;
import io.wangk.peekaboo.common.report.vo.ReportDatasetGroupVo;
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
 * 报表打印数据集分组接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Tag(name = "报表打印数据集分组接口")
@Slf4j
@RestController
@RequestMapping("${common-report.urlPrefix}/reportDatasetGroup")
@ConditionalOnProperty(name = "common-report.operationEnabled", havingValue = "true")
public class ReportDatasetGroupController {

    @Autowired
    private ReportDatasetGroupService reportDatasetGroupService;
    @Autowired
    private ReportDatasetService reportDatasetService;
    @Autowired
    private ReportDatasetRelationService reportDatasetRelationService;

    /**
     * 新增数据集分组数据。
     *
     * @param reportDatasetGroupDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {"reportDatasetGroupDto.groupId"})
    @SaCheckPermission("reportDataset.all")
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody ReportDatasetGroupDto reportDatasetGroupDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(reportDatasetGroupDto, false);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ReportDatasetGroup reportDatasetGroup = MyModelUtil.copyTo(reportDatasetGroupDto, ReportDatasetGroup.class);
        // 验证父Id的数据合法性
        ReportDatasetGroup parentReportDatasetGroup;
        if (ObjectUtil.isNotEmpty(reportDatasetGroup.getParentId())) {
            parentReportDatasetGroup = reportDatasetGroupService.getById(reportDatasetGroup.getParentId());
            if (parentReportDatasetGroup == null) {
                errorMessage = "数据验证失败，关联的父节点并不存在，请刷新后重试！";
                return ResponseResult.error(ErrorCodeEnum.DATA_PARENT_ID_NOT_EXIST, errorMessage);
            }
        }
        reportDatasetGroup = reportDatasetGroupService.saveNew(reportDatasetGroup);
        return ResponseResult.success(reportDatasetGroup.getGroupId());
    }

    /**
     * 更新数据集分组数据。
     *
     * @param reportDatasetGroupDto 更新对象。
     * @return 应答结果对象。
     */
    @SaCheckPermission("reportDataset.all")
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody ReportDatasetGroupDto reportDatasetGroupDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(reportDatasetGroupDto, true);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ReportDatasetGroup reportDatasetGroup =
                MyModelUtil.copyTo(reportDatasetGroupDto, ReportDatasetGroup.class);
        ResponseResult<ReportDatasetGroup> verifyResult = this.doVerifyAndGet(reportDatasetGroupDto.getGroupId());
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        ReportDatasetGroup originalReportDatasetGroup = verifyResult.getData();
        // 验证父Id的数据合法性
        if (ObjectUtil.isNotEmpty(reportDatasetGroup.getParentId())
                && ObjectUtil.notEqual(reportDatasetGroup.getParentId(), originalReportDatasetGroup.getParentId())) {
            ReportDatasetGroup parentReportDatasetGroup =
                    reportDatasetGroupService.getById(reportDatasetGroup.getParentId());
            if (parentReportDatasetGroup == null) {
                // NOTE: 修改下面方括号中的话述
                errorMessage = "数据验证失败，关联的 [父节点] 并不存在，请刷新后重试！";
                return ResponseResult.error(ErrorCodeEnum.DATA_PARENT_ID_NOT_EXIST, errorMessage);
            }
        }
        if (!reportDatasetGroupService.update(reportDatasetGroup, originalReportDatasetGroup)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除数据集分组数据。
     *
     * @param groupId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @SaCheckPermission("reportDataset.all")
    @OperationLog(type = SysOperationLogType.DELETE)
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long groupId) {
        return this.doDelete(groupId);
    }

    /**
     * 列出符合过滤条件的数据集分组列表。
     *
     * @return 应答结果对象，包含查询结果集。
     */
    @SaCheckPermission("reportDataset.all")
    @PostMapping("/list")
    public ResponseResult<List<ReportDatasetGroupVo>> list() {
        List<ReportDatasetGroup> reportDatasetGroupList =
                reportDatasetGroupService.getReportDatasetGroupList(null, null);
        return ResponseResult.success(BeanUtil.copyToList(reportDatasetGroupList, ReportDatasetGroupVo.class));
    }

    /**
     * 查看指定数据集分组对象详情。
     *
     * @param groupId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @SaCheckPermission("reportDataset.all")
    @GetMapping("/view")
    public ResponseResult<ReportDatasetGroupVo> view(@RequestParam Long groupId) {
        ResponseResult<ReportDatasetGroup> verifyResult = this.doVerifyAndGet(groupId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        ReportDatasetGroupVo reportDatasetGroupVo = BeanUtil.toBean(verifyResult.getData(), ReportDatasetGroupVo.class);
        return ResponseResult.success(reportDatasetGroupVo);
    }

    /**
     * 获取所有数据集分组数据和全部数据集数据。
     * 白名单接口。
     *
     * @return 应答数据结果。
     */
    @PostMapping("/listAll")
    public ResponseResult<JSONObject> listAll() {
        List<ReportDatasetGroup> reportDatasetGroupList =
                reportDatasetGroupService.getReportDatasetGroupList(null, null);
        List<ReportDataset> reportDatasetList = reportDatasetService.getReportDatasetList(null, null);
        List<ReportDatasetRelation> reportDatasetRelationList =
                reportDatasetRelationService.getReportDatasetRelationList(null, null);
        JSONObject resultData = new JSONObject();
        resultData.put("reportDatasetGroupList", reportDatasetGroupList);
        resultData.put("reportDatasetList", reportDatasetList);
        resultData.put("reportDatasetRelationList", reportDatasetRelationList);
        return ResponseResult.success(resultData);
    }

    private ResponseResult<Void> doDelete(Long groupId) {
        String errorMessage;
        // 验证关联Id的数据合法性
        ResponseResult<ReportDatasetGroup> verifyResult = this.doVerifyAndGet(groupId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        if (reportDatasetGroupService.hasChildren(groupId)) {
            errorMessage = "数据验证失败，当前数据集分组存在子分组，请先删除下级分组！";
            return ResponseResult.error(ErrorCodeEnum.HAS_CHILDREN_DATA, errorMessage);
        }
        ReportDataset filter = new ReportDataset();
        filter.setGroupId(groupId);
        if (reportDatasetService.getCountByFilter(filter) > 0) {
            errorMessage = "数据验证失败，当前数据集分组存在数据集，请先删除所属的数据集！";
            return ResponseResult.error(ErrorCodeEnum.HAS_CHILDREN_DATA, errorMessage);
        }
        if (!reportDatasetGroupService.remove(groupId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    private ResponseResult<ReportDatasetGroup> doVerifyAndGet(Long groupId) {
        if (MyCommonUtil.existBlankArgument(groupId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        ReportDatasetGroup group = reportDatasetGroupService.getById(groupId);
        if (group == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        if (!StrUtil.equals(group.getAppCode(), TokenData.takeFromRequest().getAppCode())) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, "数据验证失败，当前应用并不存在该数据集分组！");
        }
        return ResponseResult.success(group);
    }
}
