package io.wangk.peekaboo.common.core.upload;

import lombok.Data;

/**
 * 上传数据存储信息对象。这里之所以使用对象，主要是便于今后扩展。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class UploadStoreInfo {

    /**
     * 是否支持上传。
     */
    private boolean supportUpload;
    /**
     * 上传数据存储类型。
     */
    private UploadStoreTypeEnum storeType;
}
