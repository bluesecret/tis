package io.wangk.peekaboo.common.report.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 统计报表的配置对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@ConfigurationProperties(prefix = "common-report")
public class ReportProperties {

    /**
     * 统计表单业务操作的URL前缀。
     */
    private String urlPrefix;
    /**
     * 统计表单查看权限的URL列表。
     */
    private List<String> viewUrlList;
    /**
     * 下载图片数据的uri路径，如：http://localhost:8082/admin/app/downloadDirectly。
     */
    private String imageDownloadUrl;
    /**
     * 数据权限中，获取指定部门Ids的全部下级子部门的Url。
     * 如：http://localhost:8082/admin/upms/sysDept/listAllChildDeptIdByParentIds。
     */
    private String dataPermAllChildrenDeptIdUrl;
    /**
     * 是否打开menuId和当前url的匹配关系的验证。
     */
    private Boolean enableMenuPermVerify = true;
    /**
     * 业务表和在线表单内置表是否跨库。
     */
    private Boolean enabledMultiDatabaseWrite = true;
    /**
     * 上传文件的基础目录
     */
    private String uploadFileBaseDir;
    /**
     * 当前服务是否为可视化大屏服务。
     */
    private Boolean isVisualization = false;
    /**
     * 大屏文件的存放目录。
     */
    private String visualizationImagePath;
}
