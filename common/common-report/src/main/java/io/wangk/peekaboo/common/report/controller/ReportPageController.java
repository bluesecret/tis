package io.wangk.peekaboo.common.report.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.wangk.peekaboo.common.report.dto.ReportPageDto;
import io.wangk.peekaboo.common.report.model.ReportPage;
import io.wangk.peekaboo.common.report.service.ReportPageService;
import io.wangk.peekaboo.common.report.vo.ReportPageVo;
import com.github.pagehelper.page.PageMethod;
import io.wangk.peekaboo.common.core.annotation.MyRequestBody;
import io.wangk.peekaboo.common.core.constant.*;
import io.wangk.peekaboo.common.core.object.*;
import io.wangk.peekaboo.common.core.util.*;
import io.wangk.peekaboo.common.log.annotation.OperationLog;
import io.wangk.peekaboo.common.log.model.constant.SysOperationLogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报表打印页面接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Tag(name = "报表打印页面接口")
@Slf4j
@RestController
@RequestMapping("${common-report.urlPrefix}/reportPage")
@ConditionalOnProperty(name = "common-report.operationEnabled", havingValue = "true")
public class ReportPageController {

    @Autowired
    private ReportPageService reportPageService;

    /**
     * 新增报表页面数据。
     *
     * @param reportPageDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {"reportPageDto.pageId"})
    @SaCheckPermission("reportPage.all")
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody ReportPageDto reportPageDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(reportPageDto, false);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ReportPage reportPage = MyModelUtil.copyTo(reportPageDto, ReportPage.class);
        if (reportPageService.existByPageCode(reportPage.getPageCode())) {
            errorMessage = "数据验证失败，页面编码已经存在！";
            return ResponseResult.error(ErrorCodeEnum.DUPLICATED_UNIQUE_KEY, errorMessage);
        }
        try {
            reportPage = reportPageService.saveNew(reportPage);
        } catch (DuplicateKeyException e) {
            errorMessage = "数据验证失败，页面编码不能重复！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        return ResponseResult.success(reportPage.getPageId());
    }

    /**
     * 更新报表页面数据。
     *
     * @param reportPageDto 更新对象。
     * @return 应答结果对象。
     */
    @SaCheckPermission("reportPage.all")
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody ReportPageDto reportPageDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(reportPageDto, true);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ReportPage reportPage = MyModelUtil.copyTo(reportPageDto, ReportPage.class);
        ResponseResult<ReportPage> verifyResult = this.doVerifyAndGet(reportPage.getPageId());
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        ReportPage originalReportPage = verifyResult.getData();
        if (!StrUtil.equals(reportPage.getPageCode(), originalReportPage.getPageCode())
                && reportPageService.existByPageCode(reportPage.getPageCode())) {
            errorMessage = "数据验证失败，页面编码已经存在！";
            return ResponseResult.error(ErrorCodeEnum.DUPLICATED_UNIQUE_KEY, errorMessage);
        }
        try {
            if (!reportPageService.update(reportPage, originalReportPage)) {
                return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
            }
        } catch (DuplicateKeyException e) {
            errorMessage = "数据验证失败，页面编码不能重复！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 删除报表页面数据。
     *
     * @param pageId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @SaCheckPermission("reportPage.all")
    @OperationLog(type = SysOperationLogType.DELETE)
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long pageId) {
        return this.doDelete(pageId);
    }

    /**
     * 列出符合过滤条件的报表页面列表。
     *
     * @param reportPageDtoFilter 过滤对象。
     * @param orderParam          排序参数。
     * @param pageParam           分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @SaCheckPermission("reportPage.all")
    @PostMapping("/list")
    public ResponseResult<MyPageData<ReportPageVo>> list(
            @MyRequestBody ReportPageDto reportPageDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        ReportPage reportPageFilter = MyModelUtil.copyTo(reportPageDtoFilter, ReportPage.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, ReportPage.class);
        List<ReportPage> reportPageList = reportPageService.getReportPageListWithRelation(reportPageFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(reportPageList, ReportPageVo.class));
    }

    /**
     * 查看指定报表页面对象详情。
     *
     * @param pageId 页面主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<ReportPageVo> view(@RequestParam Long pageId) {
        ResponseResult<ReportPage> verifyResult = this.doVerifyAndGet(pageId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        return ResponseResult.success(verifyResult.getData(), ReportPageVo.class);
    }

    private ResponseResult<Void> doDelete(Long pageId) {
        String errorMessage;
        // 验证关联Id的数据合法性
        ResponseResult<ReportPage> verifyResult = this.doVerifyAndGet(pageId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        if (!reportPageService.remove(verifyResult.getData())) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    private ResponseResult<ReportPage> doVerifyAndGet(Long pageId) {
        if (MyCommonUtil.existBlankArgument(pageId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        ReportPage page = reportPageService.getById(pageId);
        if (page == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        TokenData tokenData = TokenData.takeFromRequest();
        if (ObjectUtil.notEqual(page.getTenantId(), tokenData.getTenantId())) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, "数据验证失败，当前租户并不存在该报表页面！");
        }
        if (!StrUtil.equals(page.getAppCode(), tokenData.getAppCode())) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, "数据验证失败，当前应用并不存在该报表页面！");
        }
        return ResponseResult.success(page);
    }
}
