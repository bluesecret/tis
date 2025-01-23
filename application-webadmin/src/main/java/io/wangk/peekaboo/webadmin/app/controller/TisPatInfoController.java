package io.wangk.peekaboo.webadmin.app.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.alibaba.fastjson.JSONObject;
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
 * 患者信息操作控制器类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Tag(name = "患者信息管理接口")
@Slf4j
@RestController
@RequestMapping("/admin/app/tisPatInfo")
public class TisPatInfoController {

    @Autowired
    private TisPatInfoService tisPatInfoService;

    /**
     * 新增患者信息数据，及其关联的从表数据。
     *
     * @param tisPatInfoDto 新增主表对象。
     * @param tisPatResultDtoList 一对多患者检测结果从表列表。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {"tisPatInfoDto.id"})
    @SaCheckPermission("tisPatInfo.add")
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(
            @MyRequestBody TisPatInfoDto tisPatInfoDto,
            @MyRequestBody List<TisPatResultDto> tisPatResultDtoList) {
        ResponseResult<Tuple2<TisPatInfo, JSONObject>> verifyResult =
                this.doBusinessDataVerifyAndConvert(tisPatInfoDto, false, tisPatResultDtoList);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        Tuple2<TisPatInfo, JSONObject> bizData = verifyResult.getData();
        TisPatInfo tisPatInfo = bizData.getFirst();
        tisPatInfo = tisPatInfoService.saveNewWithRelation(tisPatInfo, bizData.getSecond());
        return ResponseResult.success(tisPatInfo.getId());
    }

    /**
     * 修改患者信息数据，及其关联的从表数据。
     *
     * @param tisPatInfoDto 修改后的对象。
     * @param tisPatResultDtoList 一对多患者检测结果从表列表。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {"tisPatInfoDto.id"})
    @SaCheckPermission("tisPatInfo.update")
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Long> update(
            @MyRequestBody TisPatInfoDto tisPatInfoDto,
            @MyRequestBody List<TisPatResultDto> tisPatResultDtoList) {
        String errorMessage;
        ResponseResult<Tuple2<TisPatInfo, JSONObject>> verifyResult =
                this.doBusinessDataVerifyAndConvert(tisPatInfoDto, true, tisPatResultDtoList);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        Tuple2<TisPatInfo, JSONObject> bizData = verifyResult.getData();
        TisPatInfo originalTisPatInfo = bizData.getSecond().getObject("originalData", TisPatInfo.class);
        TisPatInfo tisPatInfo = bizData.getFirst();
        if (!tisPatInfoService.updateWithRelation(tisPatInfo, originalTisPatInfo, bizData.getSecond())) {
            errorMessage = "数据验证失败，[TisPatInfo] 数据不存在!";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success(tisPatInfo.getId());
    }

    /**
     * 删除患者信息数据。
     *
     * @param id 删除对象主键Id。
     * @return 应答结果对象。
     */
    @SaCheckPermission("tisPatInfo.delete")
    @OperationLog(type = SysOperationLogType.DELETE)
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long id) {
        if (MyCommonUtil.existBlankArgument(id)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        return this.doDelete(id);
    }

    /**
     * 批量删除患者信息数据。
     *
     * @param idList 待删除对象的主键Id列表。
     * @return 应答结果对象。
     */
    @SaCheckPermission("tisPatInfo.delete")
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
     * 列出符合过滤条件的患者信息列表。
     *
     * @param tisPatInfoDtoFilter 过滤对象。
     * @param tisPatResultDtoFilter 一对多从表过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @SaCheckPermission("tisPatInfo.view")
    @PostMapping("/list")
    public ResponseResult<MyPageData<TisPatInfoVo>> list(
            @MyRequestBody TisPatInfoDto tisPatInfoDtoFilter,
            @MyRequestBody TisPatResultDto tisPatResultDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize(), pageParam.getCount());
        }
        TisPatInfo tisPatInfoFilter = MyModelUtil.copyTo(tisPatInfoDtoFilter, TisPatInfo.class);
        TisPatResult tisPatResultFilter = MyModelUtil.copyTo(tisPatResultDtoFilter, TisPatResult.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, TisPatInfo.class);
        List<TisPatInfo> tisPatInfoList =
                tisPatInfoService.getTisPatInfoListWithRelation(tisPatInfoFilter, tisPatResultFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(tisPatInfoList, TisPatInfoVo.class));
    }

    /**
     * 查看指定患者信息对象详情。
     *
     * @param id 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @SaCheckPermission("tisPatInfo.view")
    @GetMapping("/view")
    public ResponseResult<TisPatInfoVo> view(@RequestParam Long id) {
        TisPatInfo tisPatInfo = tisPatInfoService.getByIdWithRelation(id, MyRelationParam.full());
        if (tisPatInfo == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        TisPatInfoVo tisPatInfoVo = MyModelUtil.copyTo(tisPatInfo, TisPatInfoVo.class);
        return ResponseResult.success(tisPatInfoVo);
    }

    private ResponseResult<Tuple2<TisPatInfo, JSONObject>> doBusinessDataVerifyAndConvert(
            TisPatInfoDto tisPatInfoDto,
            boolean forUpdate,
            List<TisPatResultDto> tisPatResultDtoList) {
        ErrorCodeEnum errorCode = ErrorCodeEnum.DATA_VALIDATED_FAILED;
        String errorMessage = MyCommonUtil.getModelValidationError(tisPatInfoDto, false);
        if (errorMessage != null) {
            return ResponseResult.error(errorCode, errorMessage);
        }
        errorMessage = MyCommonUtil.getModelValidationError(tisPatResultDtoList);
        if (errorMessage != null) {
            return ResponseResult.error(errorCode, "参数 [tisPatResultDtoList] " + errorMessage);
        }
        // 全部关联从表数据的验证和转换
        JSONObject relationData = new JSONObject();
        CallResult verifyResult;
        // 下面是输入参数中，主表关联数据的验证。
        TisPatInfo tisPatInfo = MyModelUtil.copyTo(tisPatInfoDto, TisPatInfo.class);
        TisPatInfo originalData;
        if (forUpdate && tisPatInfo != null) {
            originalData = tisPatInfoService.getById(tisPatInfo.getId());
            if (originalData == null) {
                return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
            }
            relationData.put("originalData", originalData);
        }
        // 处理主表的一对多关联 [TisPatResult]
        List<TisPatResult> tisPatResultList = MyModelUtil.copyCollectionTo(tisPatResultDtoList, TisPatResult.class);
        relationData.put("tisPatResultList", tisPatResultList);
        return ResponseResult.success(new Tuple2<>(tisPatInfo, relationData));
    }

    private ResponseResult<Void> doDelete(Long id) {
        String errorMessage;
        // 验证关联Id的数据合法性
        TisPatInfo originalTisPatInfo = tisPatInfoService.getById(id);
        if (originalTisPatInfo == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [对象] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!tisPatInfoService.remove(id)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }
}
