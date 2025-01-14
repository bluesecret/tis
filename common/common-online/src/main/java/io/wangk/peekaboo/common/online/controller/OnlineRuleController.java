package io.wangk.peekaboo.common.online.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.BooleanUtil;
import io.wangk.peekaboo.common.core.annotation.MyRequestBody;
import io.wangk.peekaboo.common.core.constant.ErrorCodeEnum;
import io.wangk.peekaboo.common.core.object.*;
import io.wangk.peekaboo.common.core.util.MyCommonUtil;
import io.wangk.peekaboo.common.core.util.MyModelUtil;
import io.wangk.peekaboo.common.core.util.MyPageUtil;
import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import io.wangk.peekaboo.common.log.annotation.OperationLog;
import io.wangk.peekaboo.common.log.model.constant.SysOperationLogType;
import io.wangk.peekaboo.common.online.dto.OnlineRuleDto;
import io.wangk.peekaboo.common.online.model.OnlineRule;
import io.wangk.peekaboo.common.online.service.OnlineRuleService;
import io.wangk.peekaboo.common.online.vo.OnlineRuleVo;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.groups.Default;
import java.util.List;

/**
 * 在线表单字段验证规则接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Tag(name = "在线表单字段验证规则接口")
@Slf4j
@RestController
@RequestMapping("${common-online.urlPrefix}/onlineRule")
@ConditionalOnProperty(name = "common-online.operationEnabled", havingValue = "true")
public class OnlineRuleController {

    @Autowired
    private OnlineRuleService onlineRuleService;

    /**
     * 新增验证规则数据。
     *
     * @param onlineRuleDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {"onlineRuleDto.ruleId"})
    @SaCheckPermission("onlinePage.all")
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody OnlineRuleDto onlineRuleDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(onlineRuleDto);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        OnlineRule onlineRule = MyModelUtil.copyTo(onlineRuleDto, OnlineRule.class);
        onlineRule = onlineRuleService.saveNew(onlineRule);
        return ResponseResult.success(onlineRule.getRuleId());
    }

    /**
     * 更新验证规则数据。
     *
     * @param onlineRuleDto 更新对象。
     * @return 应答结果对象。
     */
    @OperationLog(type = SysOperationLogType.UPDATE)
    @SaCheckPermission("onlinePage.all")
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody OnlineRuleDto onlineRuleDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(onlineRuleDto, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        OnlineRule onlineRule = MyModelUtil.copyTo(onlineRuleDto, OnlineRule.class);
        ResponseResult<OnlineRule> verifyResult = this.doVerifyAndGet(onlineRule.getRuleId(), false);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        OnlineRule originalOnlineRule = verifyResult.getData();
        if (!onlineRuleService.update(onlineRule, originalOnlineRule)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除验证规则数据。
     *
     * @param ruleId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @SaCheckPermission("onlinePage.all")
    @OperationLog(type = SysOperationLogType.DELETE)
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long ruleId) {
        String errorMessage;
        ResponseResult<OnlineRule> verifyResult = this.doVerifyAndGet(ruleId, false);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        if (!onlineRuleService.remove(ruleId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的验证规则列表。
     *
     * @param onlineRuleDtoFilter 过滤对象。
     * @param orderParam          排序参数。
     * @param pageParam           分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @SaCheckPermission("onlinePage.all")
    @PostMapping("/list")
    public ResponseResult<MyPageData<OnlineRuleVo>> list(
            @MyRequestBody OnlineRuleDto onlineRuleDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        OnlineRule onlineRuleFilter = MyModelUtil.copyTo(onlineRuleDtoFilter, OnlineRule.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, OnlineRule.class);
        List<OnlineRule> onlineRuleList = onlineRuleService.getOnlineRuleListWithRelation(onlineRuleFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(onlineRuleList, OnlineRuleVo.class));
    }

    /**
     * 查看指定验证规则对象详情。
     *
     * @param ruleId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @SaCheckPermission("onlinePage.all")
    @GetMapping("/view")
    public ResponseResult<OnlineRuleVo> view(@RequestParam Long ruleId) {
        ResponseResult<OnlineRule> verifyResult = this.doVerifyAndGet(ruleId, true);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        OnlineRule onlineRule = verifyResult.getData();
        return ResponseResult.success(onlineRule, OnlineRuleVo.class);
    }

    private ResponseResult<OnlineRule> doVerifyAndGet(Long ruleId, boolean readOnly) {
        String errorMessage;
        if (MyCommonUtil.existBlankArgument(ruleId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 验证关联Id的数据合法性
        OnlineRule rule = onlineRuleService.getById(ruleId);
        if (rule == null) {
            errorMessage = "数据验证失败，当前在线字段规则并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!readOnly && BooleanUtil.isTrue(rule.getBuiltin())) {
            errorMessage = "数据验证失败，内置规则不能删除！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!StrUtil.equals(rule.getAppCode(), TokenData.takeFromRequest().getAppCode())) {
            errorMessage = "数据验证失败，当前应用并不包含该规则！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        return ResponseResult.success(rule);
    }
}
