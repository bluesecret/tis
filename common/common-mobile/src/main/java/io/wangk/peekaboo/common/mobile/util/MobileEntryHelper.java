package io.wangk.peekaboo.common.mobile.util;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import io.wangk.peekaboo.common.core.constant.ErrorCodeEnum;
import io.wangk.peekaboo.common.core.object.ResponseResult;
import io.wangk.peekaboo.common.core.upload.BaseUpDownloader;
import io.wangk.peekaboo.common.core.upload.UpDownloaderFactory;
import io.wangk.peekaboo.common.core.upload.UploadResponseInfo;
import io.wangk.peekaboo.common.core.upload.UploadStoreInfo;
import io.wangk.peekaboo.common.core.util.ContextUtil;
import io.wangk.peekaboo.common.core.util.MyModelUtil;
import io.wangk.peekaboo.common.mobile.config.MobileProperties;
import io.wangk.peekaboo.common.mobile.model.MobileEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 移动端入口模块帮助类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Component
public class MobileEntryHelper {

    @Autowired
    private UpDownloaderFactory upDownloaderFactory;
    @Autowired
    private MobileProperties mobileProperties;

    private static final String IMAGE_DATA_FIELD = "imageData";

    /**
     * 下载移动端入口模块的图片文件。
     *
     * @param filename 文件名。
     * @param response 应答对象。
     */
    public void downloadImage(String filename, HttpServletResponse response) {
        try {
            UploadStoreInfo storeInfo = MyModelUtil.getUploadStoreInfo(MobileEntry.class, IMAGE_DATA_FIELD);
            BaseUpDownloader upDownloader = upDownloaderFactory.get(storeInfo.getStoreType());
            upDownloader.doDownload(mobileProperties.getUploadFileBaseDir(), null, filename, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error(e.getMessage(), e);
        }
    }
    /**
     * 上传图片数据。
     *
     * @param uploadFile 上传图片文件。
     */
    public void uploadImage(MultipartFile uploadFile) throws IOException {
        UploadStoreInfo storeInfo = MyModelUtil.getUploadStoreInfo(MobileEntry.class, IMAGE_DATA_FIELD);
        BaseUpDownloader upDownloader = upDownloaderFactory.get(storeInfo.getStoreType());
        UploadResponseInfo responseInfo = upDownloader.doUpload(
                null, mobileProperties.getUploadFileBaseDir(), null, uploadFile);
        if (BooleanUtil.isTrue(responseInfo.getUploadFailed())) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
                    ResponseResult.error(ErrorCodeEnum.UPLOAD_FAILED, responseInfo.getErrorMessage()));
            return;
        }
        responseInfo.setDownloadUri(this.calculateDownloadUri());
        ResponseResult.output(ResponseResult.success(responseInfo));
    }

    private String calculateDownloadUri() {
        String uploadUri = ContextUtil.getHttpRequest().getRequestURI();
        uploadUri = StrUtil.removeSuffix(uploadUri, "/");
        uploadUri = StrUtil.removeSuffix(uploadUri, "/uploadImage");
        return uploadUri + "/downloadImage";
    }
}
