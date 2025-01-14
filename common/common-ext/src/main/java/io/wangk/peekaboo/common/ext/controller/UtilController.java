package io.wangk.peekaboo.common.ext.controller;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.StrUtil;
import io.wangk.peekaboo.common.core.constant.ErrorCodeEnum;
import io.wangk.peekaboo.common.core.object.ResponseResult;
import io.wangk.peekaboo.common.core.object.TokenData;
import io.wangk.peekaboo.common.core.upload.BaseUpDownloader;
import io.wangk.peekaboo.common.core.upload.UpDownloaderFactory;
import io.wangk.peekaboo.common.core.upload.UploadResponseInfo;
import io.wangk.peekaboo.common.core.upload.UploadStoreTypeEnum;
import io.wangk.peekaboo.common.core.util.ContextUtil;
import io.wangk.peekaboo.common.ext.config.CommonExtProperties;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBinaryStream;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 扩展工具接口类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@RestController
@RequestMapping("${common-ext.urlPrefix}/util")
public class UtilController {

    @Autowired
    private UpDownloaderFactory upDownloaderFactory;
    @Autowired
    private CommonExtProperties properties;
    @Autowired
    private RedissonClient redissonClient;

    private static final String IMAGE_DATA_FIELD = "imageData";

    /**
     * 上传图片数据。
     *
     * @param uploadFile 上传图片文件。
     */
    @PostMapping("/uploadImage")
    public void uploadImage(@RequestParam("uploadFile") MultipartFile uploadFile) throws IOException {
        BaseUpDownloader upDownloader =
                upDownloaderFactory.get(EnumUtil.getEnumAt(UploadStoreTypeEnum.class, properties.getUploadStoreType()));
        UploadResponseInfo responseInfo = upDownloader.doUpload(null,
                properties.getUploadFileBaseDir(), "CommonExt", IMAGE_DATA_FIELD, true, uploadFile);
        if (BooleanUtil.isTrue(responseInfo.getUploadFailed())) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
                    ResponseResult.error(ErrorCodeEnum.UPLOAD_FAILED, responseInfo.getErrorMessage()));
            return;
        }
        String uploadUri = ContextUtil.getHttpRequest().getRequestURI();
        uploadUri = StrUtil.removeSuffix(uploadUri, "/");
        uploadUri = StrUtil.removeSuffix(uploadUri, "/uploadImage");
        responseInfo.setDownloadUri(uploadUri + "/downloadImage");
        ResponseResult.output(ResponseResult.success(responseInfo));
    }

    /**
     * 下载图片数据。
     *
     * @param filename 文件名。
     * @param response Http 应答对象。
     */
    @GetMapping("/downloadImage")
    public void downloadImage(@RequestParam String filename, HttpServletResponse response) {
        try {
            BaseUpDownloader upDownloader =
                    upDownloaderFactory.get(EnumUtil.getEnumAt(UploadStoreTypeEnum.class, properties.getUploadStoreType()));
            upDownloader.doDownload(properties.getUploadFileBaseDir(),
                    "CommonExt", IMAGE_DATA_FIELD, filename, true, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error(e.getMessage(), e);
        }
    }
    
    /**
     * 下载缓存的会话图片数据。
     *
     * @param filename 文件名。
     * @param response Http 应答对象。
     */
    @GetMapping("/downloadSessionImage")
    public void downloadSessionImage(@RequestParam String filename, HttpServletResponse response) throws IOException {
        TokenData tokenData = TokenData.takeFromRequest();
        String key = tokenData.getSessionId() + filename;
        RBinaryStream stream = redissonClient.getBinaryStream(key);
        if (!stream.isExists()) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
                    ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, "无效的会话缓存图片！"));
            return;
        }
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);
        try (OutputStream os = response.getOutputStream()) {
            os.write(stream.get());
        } catch (IOException e) {
            log.error("Failed to call LocalUpDownloader.doDownload", e);
        }
    }
}
