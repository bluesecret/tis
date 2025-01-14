package io.wangk.peekaboo.common.core.upload;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import io.wangk.peekaboo.common.core.constant.ErrorCodeEnum;
import io.wangk.peekaboo.common.core.exception.MyRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 存储本地文件的上传下载实现类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Component
public class LocalUpDownloader extends BaseUpDownloader {

    @Autowired
    private UpDownloaderFactory factory;

    @PostConstruct
    public void doRegister() {
        factory.registerUpDownloader(UploadStoreTypeEnum.LOCAL_SYSTEM, this);
    }

    @Override
    public InputStream getInputSream(
            String rootBaseDir, String modelName, String fieldName, String fileName, boolean asImage) {
        String uploadPath = makeFullPath(rootBaseDir, modelName, fieldName, asImage);
        String fullFileanme = uploadPath + "/" + fileName;
        try {
            return new FileInputStream(fullFileanme);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new MyRuntimeException(e);
        }
    }

    @Override
    public void doDownload(
            String rootBaseDir,
            String modelName,
            String fieldName,
            String fileName,
            Boolean asImage,
            HttpServletResponse response) {
        String uploadPath = makeFullPath(rootBaseDir, modelName, fieldName, asImage);
        String fullFileanme = uploadPath + "/" + fileName;
        this.downloadInternal(fullFileanme, fileName, response);
    }

    @Override
    public void doDownload(
            String rootBaseDir,
            String uriPath,
            String fileName,
            HttpServletResponse response) throws IOException {
        StringBuilder pathBuilder = new StringBuilder(128);
        if (StrUtil.isNotBlank(rootBaseDir)) {
            pathBuilder.append(rootBaseDir);
        }
        if (StrUtil.isNotBlank(uriPath)) {
            pathBuilder.append(uriPath);
        }
        pathBuilder.append("/");
        String fullFileanme = pathBuilder.append(fileName).toString();
        this.downloadInternal(fullFileanme, fileName, response);
    }

    @Override
    public UploadResponseInfo doUpload(
            String serviceContextPath,
            String rootBaseDir,
            String modelName,
            String fieldName,
            Boolean asImage,
            MultipartFile uploadFile) throws IOException {
        String uploadPath = makeFullPath(rootBaseDir, modelName, fieldName, asImage);
        return this.doUploadInternally(serviceContextPath, uploadPath, asImage, uploadFile);
    }

    @Override
    public UploadResponseInfo doUpload(
            String serviceContextPath,
            String rootBaseDir,
            String uriPath,
            MultipartFile uploadFile) throws IOException {
        String uploadPath = makeFullPath(rootBaseDir, uriPath);
        return this.doUploadInternally(serviceContextPath, uploadPath, false, uploadFile);
    }

    /**
     * 判断filename参数指定的文件名，是否被包含在fileInfoJson参数中。
     *
     * @param fileInfoJson 内部类UploadFileInfo的JSONArray数组。
     * @param filename     被包含的文件名。
     * @return 存在返回true，否则false。
     */
    public static boolean containFile(String fileInfoJson, String filename) {
        return BaseUpDownloader.containFile(fileInfoJson, filename);
    }

    private UploadResponseInfo doUploadInternally(
            String serviceContextPath,
            String uploadPath,
            Boolean asImage,
            MultipartFile uploadFile) throws IOException {
        UploadResponseInfo responseInfo = super.verifyUploadArgument(uploadFile);
        if (BooleanUtil.isTrue(responseInfo.getUploadFailed())) {
            return responseInfo;
        }
        responseInfo.setUploadPath(uploadPath);
        fillUploadResponseInfo(responseInfo, serviceContextPath, uploadFile.getOriginalFilename());
        try {
            byte[] bytes = uploadFile.getBytes();
            StringBuilder sb = new StringBuilder(256);
            sb.append(uploadPath).append("/").append(responseInfo.getFilename());
            Path path = Paths.get(sb.toString());
            // 如果没有files文件夹，则创建
            if (!Files.isWritable(path)) {
                Files.createDirectories(Paths.get(uploadPath));
            }
            // 文件写入指定路径
            Files.write(path, bytes);
        } catch (IOException e) {
            log.error("Failed to write uploaded file [" + uploadFile.getOriginalFilename() + " ].", e);
            responseInfo.setUploadFailed(true);
            responseInfo.setErrorMessage(ErrorCodeEnum.INVALID_UPLOAD_FILE_IOERROR.getErrorMessage());
            return responseInfo;
        }
        return responseInfo;
    }

    private void downloadInternal(String fullFileanme, String fileName, HttpServletResponse response) {
        File file = new File(fullFileanme);
        if (!file.exists()) {
            log.warn("Download file [" + fullFileanme + "] failed, no file found!");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        byte[] buff = new byte[2048];
        try (OutputStream os = response.getOutputStream();
             BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, i);
                os.flush();
                i = bis.read(buff);
            }
        } catch (IOException e) {
            log.error("Failed to call LocalUpDownloader.doDownload", e);
        }
    }
}
