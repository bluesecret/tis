package io.wangk.peekaboo.common.mobile.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.page.PageMethod;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.wangk.peekaboo.common.core.annotation.MyRequestBody;
import io.wangk.peekaboo.common.core.constant.ErrorCodeEnum;
import io.wangk.peekaboo.common.core.exception.MyRuntimeException;
import io.wangk.peekaboo.common.core.object.*;
import io.wangk.peekaboo.common.core.util.MyCommonUtil;
import io.wangk.peekaboo.common.core.util.MyModelUtil;
import io.wangk.peekaboo.common.core.util.MyPageUtil;
import io.wangk.peekaboo.common.log.annotation.OperationLog;
import io.wangk.peekaboo.common.log.model.constant.SysOperationLogType;
import io.wangk.peekaboo.common.mobile.dto.MobileEntryDto;
import io.wangk.peekaboo.common.mobile.model.MobileEntry;
import io.wangk.peekaboo.common.mobile.model.constant.MobileEntryType;
import io.wangk.peekaboo.common.mobile.service.MobileEntryService;
import io.wangk.peekaboo.common.mobile.util.MobileEntryHelper;
import io.wangk.peekaboo.common.mobile.vo.MobileEntryVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 移动端首页显示管理操作控制器类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Tag(name = "移动端首页显示管理管理接口")
@Slf4j
@RestController
@RequestMapping("${common-mobile.urlPrefix}/mobileEntry")
public class MobileEntryController {

    @Autowired
    private MobileEntryService mobileEntryService;
    @Autowired
    private MobileEntryHelper mobileEntryHelper;

    /**
     * 新增移动端入口显示管理数据。
     *
     * @param mobileEntryDto   新增对象。
     * @param roleIdListString 逗号分隔的角色Id列表。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {"mobileEntryDto.entryId"})
    @SaCheckPermission("mobileEntry.all")
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(
            @MyRequestBody MobileEntryDto mobileEntryDto,
            @MyRequestBody String roleIdListString) {
        String errorMessage = MyCommonUtil.getModelValidationError(mobileEntryDto, false);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        MobileEntry mobileEntry = MyModelUtil.copyTo(mobileEntryDto, MobileEntry.class);
        if (mobileEntry.getParentId() != null && !mobileEntry.getEntryType().equals(MobileEntryType.SQUARE)) {
            errorMessage = "数据验证失败，只有九宫格支持父Id！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        mobileEntry = mobileEntryService.saveNew(mobileEntry, roleIdListString);
        return ResponseResult.success(mobileEntry.getEntryId());
    }

    /**
     * 更新移动端入口显示管理数据。
     *
     * @param mobileEntryDto   更新对象。
     * @param roleIdListString 逗号分隔的角色Id列表。
     * @return 应答结果对象。
     */
    @SaCheckPermission("mobileEntry.all")
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> update(
            @MyRequestBody MobileEntryDto mobileEntryDto,
            @MyRequestBody String roleIdListString) {
        String errorMessage = MyCommonUtil.getModelValidationError(mobileEntryDto, true);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        MobileEntry mobileEntry = MyModelUtil.copyTo(mobileEntryDto, MobileEntry.class);
        ResponseResult<MobileEntry> verifyResult = this.doVerifyAndGet(mobileEntry.getEntryId());
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        MobileEntry originalMobileEntry = verifyResult.getData();
        if (originalMobileEntry == null) {
            errorMessage = "数据验证失败，当前移动端入口对象并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (BooleanUtil.isFalse(originalMobileEntry.getTenantCustom())) {
            if (ObjectUtil.notEqual(mobileEntry.getParentId(), originalMobileEntry.getParentId())) {
                errorMessage = "数据验证失败，非自定义移动端入口不能修改 [所属分组]！";
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
            if (ObjectUtil.notEqual(mobileEntry.getCommonEntry(), originalMobileEntry.getCommonEntry())) {
                errorMessage = "数据验证失败，非自定义移动端入口不能修改 [可见性]！";
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
        }
        if (mobileEntry.getParentId() != null && !mobileEntry.getEntryType().equals(MobileEntryType.SQUARE)) {
            errorMessage = "数据验证失败，只有九宫格支持父Id！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!mobileEntryService.update(mobileEntry, originalMobileEntry, roleIdListString)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除移动端首页显示管理数据。
     *
     * @param entryId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @SaCheckPermission("mobileEntry.all")
    @OperationLog(type = SysOperationLogType.DELETE)
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long entryId) {
        if (MyCommonUtil.existBlankArgument(entryId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        return this.doDelete(entryId);
    }

    /**
     * 列出符合过滤条件的移动端首页显示管理列表。
     *
     * @param mobileEntryDtoFilter 过滤对象。
     * @param orderParam           排序参数。
     * @param pageParam            分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @SaCheckPermission("mobileEntry.all")
    @PostMapping("/list")
    public ResponseResult<MyPageData<MobileEntryVo>> list(
            @MyRequestBody MobileEntryDto mobileEntryDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        MobileEntry mobileEntryFilter = MyModelUtil.copyTo(mobileEntryDtoFilter, MobileEntry.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, MobileEntry.class);
        List<MobileEntry> mobileEntryList =
                mobileEntryService.getMobileEntryListWithRelation(mobileEntryFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(mobileEntryList, MobileEntryVo.class));
    }

    /**
     * 查看指定移动端首页显示管理对象详情。
     *
     * @param entryId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @SaCheckPermission("mobileEntry.all")
    @GetMapping("/view")
    public ResponseResult<MobileEntryVo> view(@RequestParam Long entryId) {
        ResponseResult<MobileEntry> verifyResult = this.doVerifyAndGet(entryId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        MobileEntry mobileEntry = verifyResult.getData();
        mobileEntryService.buildRelationForData(mobileEntry, MyRelationParam.full());
        MobileEntryVo mobileEntryVo = MyModelUtil.copyTo(mobileEntry, MobileEntryVo.class);
        return ResponseResult.success(mobileEntryVo);
    }

    /**
     * 下载图片数据。
     *
     * @param filename 文件名。
     * @param response Http 应答对象。
     */
    @SaCheckPermission("mobileEntry.all")
    @GetMapping("/downloadImage")
    public void downloadImage(@RequestParam String filename, HttpServletResponse response) {
        mobileEntryHelper.downloadImage(filename, response);
    }

    /**
     * 上传图片数据。
     *
     * @param uploadFile 上传图片文件。
     */
    @SaCheckPermission("mobileEntry.all")
    @PostMapping("/uploadImage")
    public void uploadImage(@RequestParam("uploadFile") MultipartFile uploadFile) throws IOException {
        mobileEntryHelper.uploadImage(uploadFile);
    }

    private ResponseResult<Void> doDelete(Long entryId) {
        String errorMessage;
        // 验证关联Id的数据合法性
        ResponseResult<MobileEntry> verifyResult = this.doVerifyAndGet(entryId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        if (BooleanUtil.isFalse(verifyResult.getData().getTenantCustom())) {
            errorMessage = "数据操作失败，非自定义的租户移动端入口不能删除！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        try {
            if (!mobileEntryService.remove(entryId)) {
                errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
                return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
            }
        } catch (MyRuntimeException e) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, e.getMessage());
        }
        return ResponseResult.success();
    }

    private ResponseResult<MobileEntry> doVerifyAndGet(Long entryId) {
        if (MyCommonUtil.existBlankArgument(entryId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 验证关联Id的数据合法性
        MobileEntry mobileEntry = mobileEntryService.getById(entryId);
        return mobileEntry == null
                ? ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST) : ResponseResult.success(mobileEntry);
    }
}
