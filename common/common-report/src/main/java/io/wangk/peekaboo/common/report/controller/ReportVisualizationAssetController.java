package io.wangk.peekaboo.common.report.controller;

import com.github.pagehelper.page.PageMethod;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.wangk.peekaboo.common.core.annotation.MyRequestBody;
import io.wangk.peekaboo.common.core.annotation.NoAuthInterface;
import io.wangk.peekaboo.common.core.constant.ErrorCodeEnum;
import io.wangk.peekaboo.common.core.object.*;
import io.wangk.peekaboo.common.core.upload.*;
import io.wangk.peekaboo.common.core.util.ContextUtil;
import io.wangk.peekaboo.common.core.util.MyCommonUtil;
import io.wangk.peekaboo.common.core.util.MyModelUtil;
import io.wangk.peekaboo.common.core.util.MyPageUtil;
import io.wangk.peekaboo.common.log.annotation.IgnoreResponseLog;
import io.wangk.peekaboo.common.log.annotation.OperationLog;
import io.wangk.peekaboo.common.log.model.constant.SysOperationLogType;
import io.wangk.peekaboo.common.report.config.ReportProperties;
import io.wangk.peekaboo.common.report.dto.ReportVisualizationAssetDto;
import io.wangk.peekaboo.common.report.model.ReportVisualizationAsset;
import io.wangk.peekaboo.common.report.service.ReportVisualizationAssetService;
import io.wangk.peekaboo.common.report.vo.ReportVisualizationAssetVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 报表可视化素材操作控制器类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Tag(name = "报表可视化素材管理接口")
@Slf4j
@RestController
@RequestMapping("${common-report.urlPrefix}/reportVisualizationAsset")
@ConditionalOnProperty(name = "common-report.isVisualization", havingValue = "true")
public class ReportVisualizationAssetController {

    @Autowired
    private ReportVisualizationAssetService reportVisualizationAssetService;
    @Autowired
    private UpDownloaderFactory upDownloaderFactory;
    @Autowired
    private ReportProperties reportProperties;

    /**
     * 新增报表可视化素材数据。
     *
     * @param reportVisualizationAssetDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {"reportVisualizationAssetDto.assetId"})
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody ReportVisualizationAssetDto reportVisualizationAssetDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(reportVisualizationAssetDto, false);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ReportVisualizationAsset asset = MyModelUtil.copyTo(reportVisualizationAssetDto, ReportVisualizationAsset.class);
        asset = reportVisualizationAssetService.saveNew(asset);
        return ResponseResult.success(asset.getAssetId());
    }

    /**
     * 更新报表可视化素材数据。
     *
     * @param reportVisualizationAssetDto 更新对象。
     * @return 应答结果对象。
     */
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody ReportVisualizationAssetDto reportVisualizationAssetDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(reportVisualizationAssetDto, true);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ReportVisualizationAsset asset = MyModelUtil.copyTo(reportVisualizationAssetDto, ReportVisualizationAsset.class);
        ReportVisualizationAsset originalAsset = reportVisualizationAssetService.getById(asset.getAssetId());
        if (originalAsset == null) {
            errorMessage = "数据验证失败，当前可视化素材并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!reportVisualizationAssetService.update(asset, originalAsset)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除报表可视化素材数据。
     *
     * @param assetId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @OperationLog(type = SysOperationLogType.DELETE)
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long assetId) {
        if (MyCommonUtil.existBlankArgument(assetId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        String errorMessage;
        if (!reportVisualizationAssetService.remove(assetId)) {
            errorMessage = "数据操作失败，删除的可视化素材不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的报表可视化素材列表。
     *
     * @param reportVisualizationAssetDtoFilter 过滤对象。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @IgnoreResponseLog
    @PostMapping("/list")
    public ResponseResult<MyPageData<ReportVisualizationAssetVo>> list(
            @MyRequestBody ReportVisualizationAssetDto reportVisualizationAssetDtoFilter,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        ReportVisualizationAsset filter =
                MyModelUtil.copyTo(reportVisualizationAssetDtoFilter, ReportVisualizationAsset.class);
        List<ReportVisualizationAsset> assetList =
                reportVisualizationAssetService.getReportVisualizationAssetList(filter);
        return ResponseResult.success(MyPageUtil.makeResponseData(assetList, ReportVisualizationAssetVo.class));
    }

    /**
     * 查看指定报表可视化素材对象详情。
     *
     * @param assetId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @IgnoreResponseLog
    @GetMapping("/view")
    public ResponseResult<ReportVisualizationAssetVo> view(@RequestParam Long assetId) {
        ReportVisualizationAsset asset = reportVisualizationAssetService.getById(assetId);
        if (asset == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success(asset, ReportVisualizationAssetVo.class);
    }
    
    /**
     * 背景图上传接口。
     *
     * @param uploadFile 背景图文件。
     */
    @NoAuthInterface
    @PostMapping("/uploadBackgroundImage")
    public void uploadBackgroundImage(@RequestParam("uploadFile") MultipartFile uploadFile) throws IOException {
        BaseUpDownloader upDownloader = upDownloaderFactory.get(UploadStoreTypeEnum.LOCAL_SYSTEM);
        UploadResponseInfo responseInfo = upDownloader.doUpload(null,
                reportProperties.getVisualizationImagePath(), "background", null, true, uploadFile);
        if (Boolean.TRUE.equals(responseInfo.getUploadFailed())) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
                    ResponseResult.error(ErrorCodeEnum.UPLOAD_FAILED, responseInfo.getErrorMessage()));
            return;
        }
        String uri = ContextUtil.getHttpRequest().getRequestURI();
        uri = StringUtils.removeEnd(uri, "/");
        uri = StringUtils.removeEnd(uri, "/uploadBackgroundImage");
        responseInfo.setDownloadUri(uri + "/downloadBackgroundImage");
        ResponseResult.output(ResponseResult.success(responseInfo));
    }

    /**
     * 背景图下载接口。
     *
     * @param filename 文件名。
     * @param response 应答对象。
     */
    @NoAuthInterface
    @GetMapping("/downloadBackgroundImage")
    public void downloadBackgroundImage(@RequestParam String filename, HttpServletResponse response) {
        // 使用try来捕获异常，是为了保证一旦出现异常可以返回500的错误状态，便于调试。
        // 否则有可能给前端返回的是200的错误码。
        try {
            BaseUpDownloader upDownloader = upDownloaderFactory.get(UploadStoreTypeEnum.LOCAL_SYSTEM);
            upDownloader.doDownload(reportProperties.getVisualizationImagePath(),
                    "background", null, filename, true, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error(e.getMessage(), e);
        }
    }
}
