package io.wangk.peekaboo.webadmin.app.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.page.PageMethod;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.wangk.peekaboo.common.core.annotation.MyRequestBody;
import io.wangk.peekaboo.common.core.constant.ErrorCodeEnum;
import io.wangk.peekaboo.common.core.object.*;
import io.wangk.peekaboo.common.core.upload.*;
import io.wangk.peekaboo.common.core.util.MyCommonUtil;
import io.wangk.peekaboo.common.core.util.MyModelUtil;
import io.wangk.peekaboo.common.core.util.MyPageUtil;
import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import io.wangk.peekaboo.common.log.annotation.OperationLog;
import io.wangk.peekaboo.common.log.model.constant.SysOperationLogType;
import io.wangk.peekaboo.webadmin.app.dto.TisDeviceInfoDto;
import io.wangk.peekaboo.webadmin.app.model.TisDeviceInfo;
import io.wangk.peekaboo.webadmin.app.model.TisUserDevice;
import io.wangk.peekaboo.webadmin.app.service.TisDeviceInfoService;
import io.wangk.peekaboo.webadmin.app.util.ExcelUtil;
import io.wangk.peekaboo.webadmin.app.vo.TisDeviceInfoVo;
import io.wangk.peekaboo.webadmin.config.ApplicationConfig;
import io.wangk.peekaboo.webadmin.upms.dto.SysUserDto;
import io.wangk.peekaboo.webadmin.upms.model.SysRole;
import io.wangk.peekaboo.webadmin.upms.model.SysUser;
import io.wangk.peekaboo.webadmin.upms.service.SysUserService;
import io.wangk.peekaboo.webadmin.upms.vo.SysRoleVo;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.groups.Default;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 设备信息管理接口控制器类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Tag(name = "设备信息管理接口")
@Slf4j
@RestController
@RequestMapping("/admin/app/tisDeviceInfo")
public class TisDeviceInfoController {

    @Autowired
    private TisDeviceInfoService tisDeviceInfoService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private UpDownloaderFactory upDownloaderFactory;
    @Autowired
    private ApplicationConfig appConfig;

    /**
     * 新增设备操作。
     *
     * @param tisDeviceInfoDto       新增设备对象。
     * @return 应答结果对象，包含新增设备的主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {"tisDeviceInfoDto.id"})
    @SaCheckPermission("tisDeviceInfo.add")
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(
            @MyRequestBody TisDeviceInfoDto tisDeviceInfoDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(tisDeviceInfoDto);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }

        // 这里必须手动校验字典编码是否存在，因为我们缺省的实现是逻辑删除，所以字典编码字段没有设置为唯一索引。
        if (tisDeviceInfoService.existSerNo(tisDeviceInfoDto.getSerNo())) {
            errorMessage = "数据验证失败，设备编码已经存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        TisDeviceInfo tisDeviceInfo = MyModelUtil.copyTo(tisDeviceInfoDto, TisDeviceInfo.class);
        tisDeviceInfoService.saveNew(tisDeviceInfo);
        return ResponseResult.success(tisDeviceInfo.getId());
    }

    /**
     * 更新设备信息操作。
     *
     * @param tisDeviceInfoDto 更新设备对象。
     * @return 应答结果对象。
     */
    @SaCheckPermission("tisDeviceInfo.update")
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody TisDeviceInfoDto tisDeviceInfoDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(tisDeviceInfoDto, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        TisDeviceInfo originalTisDeviceInfo = tisDeviceInfoService.getById(tisDeviceInfoDto.getId());
        if (originalTisDeviceInfo == null) {
            errorMessage = "数据验证失败，当前设备信息并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        TisDeviceInfo tisDeviceInfo = MyModelUtil.copyTo(tisDeviceInfoDto, TisDeviceInfo.class);
        if (ObjectUtil.notEqual(tisDeviceInfoDto.getSerNo(), originalTisDeviceInfo.getSerNo())
                && tisDeviceInfoService.existSerNo(tisDeviceInfo.getSerNo())) {
            errorMessage = "数据验证失败，设备编码已经存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!tisDeviceInfoService.update(tisDeviceInfo, originalTisDeviceInfo)) {
            errorMessage = "更新失败，数据不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    @SaCheckPermission("tisDeviceInfo.delete")
    @OperationLog(type = SysOperationLogType.DELETE)
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody(required = true) Long id) {
        String errorMessage;
        TisDeviceInfo tisDeviceInfo = tisDeviceInfoService.getById(id);
        if (tisDeviceInfo == null) {
            errorMessage = "数据操作失败，设备不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!tisDeviceInfoService.remove(id)) {
            errorMessage = "数据操作失败，设备信息不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 文件上传操作。
     *
     * @param uploadFile 上传文件对象。
     */
    @SaCheckPermission("tisDeviceInfo.upload")
    @OperationLog(type = SysOperationLogType.UPLOAD, saveResponse = false)
    @PostMapping("/upload")
    public ResponseResult<Void> upload(
            @RequestParam String fieldName,
            @RequestParam Boolean asImage,
            @RequestParam("uploadFile") MultipartFile uploadFile) throws IOException {
        String errorMessage;
        BaseUpDownloader upDownloader = upDownloaderFactory.get(UploadStoreTypeEnum.LOCAL_SYSTEM);
        UploadResponseInfo responseInfo = upDownloader.doUpload(null,
                appConfig.getUploadFileBaseDir(), TisDeviceInfo.class.getSimpleName(), fieldName, asImage, uploadFile);
        if (BooleanUtil.isTrue(responseInfo.getUploadFailed())) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
                    ResponseResult.error(ErrorCodeEnum.UPLOAD_FAILED, responseInfo.getErrorMessage()));
            errorMessage = "文件上传失败，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.INVALID_UPLOAD_FILE_IOERROR, errorMessage);
        }
        List<List<String>> cellLists  = ExcelUtil.readExcel(responseInfo.getUploadPath()+"/"+responseInfo.getFilename());
        for (List<String> cellList : cellLists) {
            String str = StringUtils.strip(cellList.toString(), "[]").replaceAll(",", "").trim();
            if (StringUtils.isBlank(str)) {
                continue;
            }

            if (tisDeviceInfoService.existSerNo(cellList.get(0))) {
                continue;
            }

            TisDeviceInfo tisDeviceInfo = new TisDeviceInfo();
            tisDeviceInfo.setSerNo(cellList.get(0));
            tisDeviceInfo.setDeviceName(cellList.get(1));
            tisDeviceInfoService.saveNew(tisDeviceInfo);
        }
        return ResponseResult.success();
    }

    /**
     * 查看设备列表。
     *
     * @param tisDeviceInfoDto 设备过滤对象。
     * @param pageParam        分页参数。
     * @return 应答结果对象，包含角色列表。
     */
    @SaCheckPermission("tisDeviceInfoVo.view")
    @PostMapping("/list")
    public ResponseResult<MyPageData<TisDeviceInfoVo>> list(
            @MyRequestBody TisDeviceInfoDto tisDeviceInfoDto,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        TisDeviceInfo filter = MyModelUtil.copyTo(tisDeviceInfoDto, TisDeviceInfo.class);
        List<TisDeviceInfo> deviceInfoList =  tisDeviceInfoService.getListByFilter(filter);
        return ResponseResult.success(MyPageUtil.makeResponseData(deviceInfoList, TisDeviceInfoVo.class));
    }


    @SaCheckPermission("tisDeviceInfo.userBind")
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/userBind")
    public ResponseResult<Void> userBind(
            @MyRequestBody SysUserDto sysUserDto, @MyRequestBody String deviceIdListString) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysUserDto, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        SysUser originalSysUser = sysUserService.getById(sysUserDto.getUserId());
        if (originalSysUser == null) {
            errorMessage = "数据验证失败，当前用户并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }

        Set<String> devicelist = Arrays.stream(
                deviceIdListString.split(",")).map(String::valueOf).collect(Collectors.toSet());

        List<TisUserDevice> userDeviceList = new LinkedList<>();
        for (String serNo : devicelist) {
            TisUserDevice userDevice = new TisUserDevice();
            userDevice.setUserId(sysUserDto.getUserId());
            userDevice.setDeviceNo(serNo);
            userDeviceList.add(userDevice);
        }
        tisDeviceInfoService.addUserDeviceList(originalSysUser,userDeviceList);
        return ResponseResult.success();
    }
}
