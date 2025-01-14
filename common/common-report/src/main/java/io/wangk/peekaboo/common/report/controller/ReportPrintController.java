package io.wangk.peekaboo.common.report.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSON;
import io.wangk.peekaboo.common.core.upload.BaseUpDownloader;
import io.wangk.peekaboo.common.core.upload.UpDownloaderFactory;
import io.wangk.peekaboo.common.core.upload.UploadResponseInfo;
import io.wangk.peekaboo.common.core.upload.UploadStoreInfo;
import io.wangk.peekaboo.common.report.config.ReportProperties;
import io.wangk.peekaboo.common.report.model.constant.ReportPrintType;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.wangk.peekaboo.common.core.annotation.MyRequestBody;
import io.wangk.peekaboo.common.core.constant.ErrorCodeEnum;
import io.wangk.peekaboo.common.core.object.*;
import io.wangk.peekaboo.common.core.util.MyCommonUtil;
import io.wangk.peekaboo.common.core.util.MyModelUtil;
import io.wangk.peekaboo.common.core.util.MyPageUtil;
import io.wangk.peekaboo.common.log.annotation.OperationLog;
import io.wangk.peekaboo.common.log.model.constant.SysOperationLogType;
import io.wangk.peekaboo.common.redis.cache.SessionCacheHelper;
import io.wangk.peekaboo.common.report.constant.PrintRenderType;
import io.wangk.peekaboo.common.report.dto.ReportPrintDto;
import io.wangk.peekaboo.common.report.model.ReportPrint;
import io.wangk.peekaboo.common.report.model.ReportPrintGroup;
import io.wangk.peekaboo.common.report.object.ReportPrintParam;
import io.wangk.peekaboo.common.report.service.ReportDatasetRelationService;
import io.wangk.peekaboo.common.report.service.ReportDatasetService;
import io.wangk.peekaboo.common.report.service.ReportPrintGroupService;
import io.wangk.peekaboo.common.report.service.ReportPrintService;
import io.wangk.peekaboo.common.report.util.ReportOperationHelper;
import io.wangk.peekaboo.common.report.vo.ReportPrintVo;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

/**
 * 报表打印打印接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Tag(name = "报表打印打印接口")
@Slf4j
@RestController
@RequestMapping("${common-report.urlPrefix}/reportPrint")
@ConditionalOnProperty(name = "common-report.operationEnabled", havingValue = "true")
public class ReportPrintController {

    @Autowired
    private ReportPrintService reportPrintService;
    @Autowired
    private ReportDatasetService reportDatasetService;
    @Autowired
    private ReportDatasetRelationService reportDatasetRelationService;
    @Autowired
    private ReportPrintGroupService reportPrintGroupService;
    @Autowired
    private ReportOperationHelper reportOperationHelper;
    @Autowired
    private SessionCacheHelper sessionCacheHelper;
    @Autowired
    private UpDownloaderFactory upDownloaderFactory;
    @Autowired
    private ReportProperties reportProperties;

    /**
     * 新增报表页面数据。
     *
     * @param reportPrintDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {"reportPrintDto.printId"})
    @SaCheckPermission("reportPrint.all")
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody ReportPrintDto reportPrintDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(reportPrintDto, false);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ReportPrint reportPrint = MyModelUtil.copyTo(reportPrintDto, ReportPrint.class);
        if (reportPrintService.existByPrintVariable(reportPrint.getPrintVariable())) {
            errorMessage = "数据验证失败，指定打印模板变量已经存在！";
            return ResponseResult.error(ErrorCodeEnum.DUPLICATED_UNIQUE_KEY, errorMessage);
        }
        try {
            reportPrint = reportPrintService.saveNew(reportPrint);
        } catch (DuplicateKeyException e) {
            errorMessage = "数据验证失败，打印模板变量不能重复！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        return ResponseResult.success(reportPrint.getPrintId());
    }

    /**
     * 更新报表页面数据。
     *
     * @param reportPrintDto 更新对象。
     * @return 应答结果对象。
     */
    @SaCheckPermission("reportPrint.all")
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody ReportPrintDto reportPrintDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(reportPrintDto, true);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ReportPrint reportPrint = MyModelUtil.copyTo(reportPrintDto, ReportPrint.class);
        ResponseResult<ReportPrint> verifyResult = this.doVerifyAndGet(reportPrintDto.getPrintId());
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        ReportPrint originalReportPrint = verifyResult.getData();
        if (!StrUtil.equals(reportPrint.getPrintVariable(), originalReportPrint.getPrintVariable())
                && reportPrintService.existByPrintVariable(reportPrint.getPrintVariable())) {
            errorMessage = "数据验证失败，指定打印模板变量已经存在！";
            return ResponseResult.error(ErrorCodeEnum.DUPLICATED_UNIQUE_KEY, errorMessage);
        }
        try {
            if (!reportPrintService.update(reportPrint, originalReportPrint)) {
                return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
            }
        } catch (DuplicateKeyException e) {
            errorMessage = "数据验证失败，打印模板变量不能重复！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 删除报表打印数据。
     *
     * @param printId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @SaCheckPermission("reportPrint.all")
    @OperationLog(type = SysOperationLogType.DELETE)
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long printId) {
        return this.doDelete(printId);
    }

    /**
     * 列出符合过滤条件的报表页面列表。
     *
     * @param reportPrintDtoFilter 过滤对象。
     * @param orderParam           排序参数。
     * @param pageParam            分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @SaCheckPermission("reportPrint.all")
    @PostMapping("/list")
    public ResponseResult<MyPageData<ReportPrintVo>> list(
            @MyRequestBody ReportPrintDto reportPrintDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        ReportPrint reportPrintFilter = MyModelUtil.copyTo(reportPrintDtoFilter, ReportPrint.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, ReportPrint.class);
        List<ReportPrint> reportPrintList =
                reportPrintService.getReportPrintListWithRelation(reportPrintFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(reportPrintList, ReportPrintVo.class));
    }

    /**
     * 文件上传操作。
     *
     * @param fieldName  上传文件名。
     * @param uploadFile 上传文件对象。
     */
    @SaCheckPermission("reportPrint.all")
    @OperationLog(type = SysOperationLogType.UPLOAD, saveResponse = false)
    @PostMapping("/upload")
    public void upload(
            @RequestParam String fieldName,
            @RequestParam("uploadFile") MultipartFile uploadFile) throws IOException {
        UploadStoreInfo storeInfo = MyModelUtil.getUploadStoreInfo(ReportPrint.class, fieldName);
        // 这里就会判断参数中指定的字段，是否支持上传操作。
        if (!storeInfo.isSupportUpload()) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
                    ResponseResult.error(ErrorCodeEnum.INVALID_UPLOAD_FIELD));
            return;
        }
        // 根据字段注解中的存储类型，通过工厂方法获取匹配的上传下载实现类，从而解耦。
        BaseUpDownloader upDownloader = upDownloaderFactory.get(storeInfo.getStoreType());
        UploadResponseInfo responseInfo = upDownloader.doUpload(null,
                reportProperties.getUploadFileBaseDir(), ReportPrint.class.getSimpleName(), fieldName, false, uploadFile);
        if (Boolean.TRUE.equals(responseInfo.getUploadFailed())) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
                    ResponseResult.error(ErrorCodeEnum.UPLOAD_FAILED, responseInfo.getErrorMessage()));
            return;
        }
        sessionCacheHelper.putSessionUploadFile(responseInfo.getFilename());
        ResponseResult.output(ResponseResult.success(responseInfo));
    }

    /**
     * 模板下载。
     *
     * @param printId   附件所在记录的主键Id。
     * @param fieldName 附件所属的字段名。
     * @param filename  文件名。如果没有提供该参数，就从当前记录的指定字段中读取。
     * @param response  Http 应答对象。
     */
    @SaCheckPermission("reportPrint.all")
    @OperationLog(type = SysOperationLogType.DOWNLOAD, saveResponse = false)
    @GetMapping("/download")
    public void download(
            @RequestParam(required = false) Long printId,
            @RequestParam String fieldName,
            @RequestParam String filename,
            HttpServletResponse response) {
        if (MyCommonUtil.existBlankArgument(fieldName, filename)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        // 使用try来捕获异常，是为了保证一旦出现异常可以返回500的错误状态，便于调试。
        // 否则有可能给前端返回的是200的错误码。
        try {
            // 如果请求参数中没有包含主键Id，就判断该文件是否为当前session上传的。
            if (printId == null) {
                if (!sessionCacheHelper.existSessionUploadFile(filename)) {
                    ResponseResult.output(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            } else {
                ReportPrint reportPrint = reportPrintService.getById(printId);
                if (reportPrint == null) {
                    ResponseResult.output(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                String fieldJsonData = (String) ReflectUtil.getFieldValue(reportPrint, fieldName);
                if (fieldJsonData == null && !sessionCacheHelper.existSessionUploadFile(filename)) {
                    ResponseResult.output(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                if (!BaseUpDownloader.containFile(fieldJsonData, filename)
                        && !sessionCacheHelper.existSessionUploadFile(filename)) {
                    ResponseResult.output(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            }
            UploadStoreInfo storeInfo = MyModelUtil.getUploadStoreInfo(ReportPrint.class, fieldName);
            if (!storeInfo.isSupportUpload()) {
                ResponseResult.output(HttpServletResponse.SC_NOT_IMPLEMENTED,
                        ResponseResult.error(ErrorCodeEnum.INVALID_UPLOAD_FIELD));
                return;
            }
            BaseUpDownloader upDownloader = upDownloaderFactory.get(storeInfo.getStoreType());
            upDownloader.doDownload(reportProperties.getUploadFileBaseDir(),
                    ReportPrint.class.getSimpleName(), fieldName, filename, false, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 返回所有打印模板和打印模板分组数据列表。白名单接口。
     *
     * @return 应答结果对象。包含两个数据列表。
     */
    @PostMapping("/listAll")
    public ResponseResult<JSONObject> listAll() {
        List<ReportPrint> printList = reportPrintService.getReportPrintList(null, null);
        List<ReportPrintGroup> printGroupList = reportPrintGroupService.getReportPrintGroupList(null, null);
        JSONObject resultData = new JSONObject();
        resultData.put("allPrintList", printList);
        resultData.put("allPrintGroupList", printGroupList);
        return ResponseResult.success(resultData);
    }

    /**
     * 查看指定报表页面对象详情。
     *
     * @param printId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @SaCheckPermission("reportPrint.all")
    @GetMapping("/view")
    public ResponseResult<ReportPrintVo> view(@RequestParam Long printId) {
        ResponseResult<ReportPrint> verifyResult = this.doVerifyAndGet(printId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        ReportPrint reportPrint = verifyResult.getData();
        reportPrintService.buildRelationForData(reportPrint, MyRelationParam.full());
        return ResponseResult.success(reportPrint, ReportPrintVo.class);
    }

    /**
     * 打印预览。
     *
     * @param printId    打印模板Id。
     * @param printParam 打印参数。
     * @param renderType 参数值可参考PrintRenderType常量类。
     */
    @SaCheckPermission("reportPrint.all")
    @PostMapping("/preview")
    public void preview(
            @MyRequestBody(required = true) Long printId,
            @MyRequestBody(required = true) ReportPrintParam printParam,
            @MyRequestBody(required = true) Integer renderType) throws IOException {
        if (!PrintRenderType.isValid(renderType)) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
                    ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, "无效的渲染类型参数！"));
            return;
        }
        ResponseResult<ReportPrint> verifyResult = this.doVerifyAndGet(printId);
        if (!verifyResult.isSuccess()) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN, verifyResult);
            return;
        }
        reportOperationHelper.print(printId, printParam, renderType);
    }

    /**
     * 打印预览。参数以URL的传递。
     *
     * @param printId    打印模板Id。
     * @param printParam 打印参数。
     * @param renderType 参数值可参考PrintRenderType常量类。
     */
    @SaCheckPermission("reportPrint.all")
    @GetMapping("/previewWithUrl")
    public void previewWithUrl(
            @RequestParam Long printId,
            @RequestParam String printParam,
            @RequestParam Integer renderType) throws IOException {
        ReportPrintParam pp = new ReportPrintParam();
        printParam = URLDecoder.decode(printParam, StandardCharsets.UTF_8.name());
        pp.addAll(JSON.parseArray(printParam, ReportPrintParam.FilterInfo.class));
        this.preview(printId, pp, renderType);
    }

    /**
     * 执行打印。该方法会根据打印令牌参数printToken，从会话关联的缓存中获取实际的打印参数数据。
     * 白名单接口。
     *
     * @param printToken 打印令牌。
     */
    @GetMapping("/print")
    public void print(@RequestParam String printToken) throws IOException {
        // 根据打印令牌参数printToken，从会话关联的缓存中读取实际的打印数据。
        // 如果不存在则视为无效调用。
        MyPrintInfo printInfo = sessionCacheHelper.getSessionPrintInfoByToken(printToken);
        if (printInfo == null) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN, ResponseResult.error(ErrorCodeEnum.NO_ACCESS_PERMISSION));
            return;
        }
        // 将请求参数转换为打印服务所需的参数对象。
        List<ReportPrintParam> reportPrintParams = new LinkedList<>();
        for (JSONArray printParam : printInfo.getPrintParams()) {
            ReportPrintParam reportPrintParam = new ReportPrintParam();
            if (CollUtil.isNotEmpty(printParam)) {
                reportPrintParam = new ReportPrintParam();
                for (int i = 0; i < printParam.size(); ++i) {
                    ReportPrintParam.FilterInfo filterInfo =
                            printParam.getJSONObject(i).toJavaObject(ReportPrintParam.FilterInfo.class);
                    reportPrintParam.add(filterInfo);
                }
            }
            reportPrintParams.add(reportPrintParam);
        }
        // 执行实际的打印，并将打印结果作为应答数据流，直接返回给前端。
        if (CollUtil.isNotEmpty(reportPrintParams)) {
            ReportPrint print = reportPrintService.getById(printInfo.getPrintId());
            int renderType = print.getPrintType() == ReportPrintType.EXCEL ? PrintRenderType.EXCEL : PrintRenderType.WORD;
            reportOperationHelper.print(printInfo.getPrintId(), reportPrintParams, renderType);
        }
    }

    private ResponseResult<Void> doDelete(Long printId) {
        String errorMessage;
        // 验证关联Id的数据合法性
        ResponseResult<ReportPrint> verifyResult = this.doVerifyAndGet(printId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        if (!reportPrintService.remove(printId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    private ResponseResult<ReportPrint> doVerifyAndGet(Long printId) {
        if (MyCommonUtil.existBlankArgument(printId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        ReportPrint print = reportPrintService.getById(printId);
        if (print == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        TokenData tokenData = TokenData.takeFromRequest();
        if (ObjectUtil.notEqual(print.getTenantId(), tokenData.getTenantId())) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, "数据验证失败，当前租户并不存在该打印模板！");
        }
        if (!StrUtil.equals(print.getAppCode(), tokenData.getAppCode())) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, "数据验证失败，当前应用并不存在该打印模板！");
        }
        return ResponseResult.success(print);
    }
}
