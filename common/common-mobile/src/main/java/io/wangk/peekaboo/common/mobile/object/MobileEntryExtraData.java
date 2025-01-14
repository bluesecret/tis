package io.wangk.peekaboo.common.mobile.object;

import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 移动端入口扩展数据。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class MobileEntryExtraData {

    /**
     * 路由名称。
     */
    private String formRouterName;

    /**
     * 在线表单。
     */
    private Long onlineFormId;

    /**
     * 报表页面。
     */
    private Long reportPageId;

    /**
     * 流程。
     */
    private Long onlineFlowEntryId;

    /**
     * 移动端入口的权限字列表。仅当选择satoken权限框架时使用。
     */
    private List<String> permCodeList;

    /**
     * 权限地址集合。该字段仅在登录时使用，不会存储于数据库。
     */
    private Set<String> permUrls = new HashSet<>();
}
