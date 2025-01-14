package io.wangk.peekaboo.common.ext.config;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * common-ext配置属性类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@ConfigurationProperties(prefix = "common-ext")
public class CommonExtProperties implements InitializingBean {

    /**
     * 上传存储类型。具体值可参考枚举 UploadStoreTypeEnum。默认0为本地存储。
     */
    @Value("${common-ext.uploadStoreType:0}")
    private Integer uploadStoreType;

    /**
     * 仅当uploadStoreType等于0的时候，该配置值生效。
     */
    @Value("${common-ext.uploadFileBaseDir:./zz-resource/upload-files/commonext}")
    private String uploadFileBaseDir;

    private List<AppProperties> apps;

    private Map<String, AppProperties> applicationMap;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (CollUtil.isEmpty(apps)) {
            applicationMap = new HashMap<>(1);
        } else {
            applicationMap = apps.stream().collect(Collectors.toMap(AppProperties::getAppCode, c -> c));
        }
    }

    @Data
    public static class AppProperties {
        /**
         * 应用编码。
         */
        private String appCode;
        /**
         * 通用业务组件数据源属性列表。
         */
        private List<BizWidgetDatasourceProperties> bizWidgetDatasources;
    }

    @Data
    public static class BizWidgetDatasourceProperties {
        /**
         * 通用业务组件的数据源类型。多个类型之间逗号分隔，如：upms_user,upms_dept。
         */
        private String types;
        /**
         * 列表数据接口地址。格式为完整的url，如：http://xxxxx
         */
        private String listUrl;
        /**
         * 详情数据接口地址。格式为完整的url，如：http://xxxxx
         */
        private String viewUrl;
    }
}
