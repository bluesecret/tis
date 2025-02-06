package io.wangk.peekaboo.webadmin.app.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.wangk.peekaboo.common.log.annotation.OperationLog;
import io.wangk.peekaboo.common.log.model.constant.SysOperationLogType;
import com.github.pagehelper.page.PageMethod;
import io.wangk.peekaboo.webadmin.app.vo.*;
import io.wangk.peekaboo.webadmin.app.dto.*;
import io.wangk.peekaboo.webadmin.app.model.*;
import io.wangk.peekaboo.webadmin.app.service.*;
import io.wangk.peekaboo.common.core.object.*;
import io.wangk.peekaboo.common.core.util.*;
import io.wangk.peekaboo.common.core.constant.*;
import io.wangk.peekaboo.common.core.annotation.MyRequestBody;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 检查结果操作控制器类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Tag(name = "检查结果管理接口")
@Slf4j
@RestController
@RequestMapping("/admin/app/tisPatResult")
public class TisPatResultController {

    @Autowired
    private TisPatResultService tisPatResultService;

    /**
     * 新增检查结果数据。
     *
     * @param tisPatResultDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {"tisPatResultDto.id"})
    @SaCheckPermission("tisPatResult.add")
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody TisPatResultDto tisPatResultDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(tisPatResultDto, false);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        TisPatResult tisPatResult = MyModelUtil.copyTo(tisPatResultDto, TisPatResult.class);
        tisPatResult = tisPatResultService.saveNew(tisPatResult);
        return ResponseResult.success(tisPatResult.getId());
    }

    /**
     * 更新检查结果数据。
     *
     * @param tisPatResultDto 更新对象。
     * @return 应答结果对象。
     */
    @SaCheckPermission("tisPatResult.update")
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody TisPatResultDto tisPatResultDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(tisPatResultDto, true);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        TisPatResult tisPatResult = MyModelUtil.copyTo(tisPatResultDto, TisPatResult.class);
        TisPatResult originalTisPatResult = tisPatResultService.getById(tisPatResult.getId());
        if (originalTisPatResult == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [数据] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!tisPatResultService.update(tisPatResult, originalTisPatResult)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除检查结果数据。
     *
     * @param id 删除对象主键Id。
     * @return 应答结果对象。
     */
    @SaCheckPermission("tisPatResult.delete")
    @OperationLog(type = SysOperationLogType.DELETE)
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long id) {
        if (MyCommonUtil.existBlankArgument(id)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        return this.doDelete(id);
    }

    /**
     * 批量删除检查结果数据。
     *
     * @param idList 待删除对象的主键Id列表。
     * @return 应答结果对象。
     */
    @SaCheckPermission("tisPatResult.delete")
    @OperationLog(type = SysOperationLogType.DELETE_BATCH)
    @PostMapping("/deleteBatch")
    public ResponseResult<Void> deleteBatch(@MyRequestBody List<Long> idList) {
        if (MyCommonUtil.existBlankArgument(idList)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        for (Long id : idList) {
            ResponseResult<Void> responseResult = this.doDelete(id);
            if (!responseResult.isSuccess()) {
                return responseResult;
            }
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的检查结果列表。
     *
     * @param tisPatResultDtoFilter 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @SaCheckPermission("tisPatResult.view")
    @PostMapping("/list")
    public ResponseResult<MyPageData<TisPatResultVo>> list(
            @MyRequestBody TisPatResultDto tisPatResultDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize(), pageParam.getCount());
        }
        TisPatResult tisPatResultFilter = MyModelUtil.copyTo(tisPatResultDtoFilter, TisPatResult.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, TisPatResult.class);
        List<TisPatResult> tisPatResultList =
                tisPatResultService.getTisPatResultListWithRelation(tisPatResultFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(tisPatResultList, TisPatResultVo.class));
    }

    /**
     * 查看指定检查结果对象详情。
     *
     * @param id 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @SaCheckPermission("tisPatResult.view")
    @GetMapping("/view")
    public ResponseResult<TisPatResultVo> view(@RequestParam Long id) {
        TisPatResult tisPatResult = tisPatResultService.getByIdWithRelation(id, MyRelationParam.full());
        if (tisPatResult == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        TisPatResultVo tisPatResultVo = MyModelUtil.copyTo(tisPatResult, TisPatResultVo.class);
        return ResponseResult.success(tisPatResultVo);
    }

    private ResponseResult<Void> doDelete(Long id) {
        String errorMessage;
        // 验证关联Id的数据合法性
        TisPatResult originalTisPatResult = tisPatResultService.getById(id);
        if (originalTisPatResult == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [对象] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!tisPatResultService.remove(id)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }
}
