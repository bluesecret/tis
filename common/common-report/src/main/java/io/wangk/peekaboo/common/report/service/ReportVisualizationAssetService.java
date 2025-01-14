package io.wangk.peekaboo.common.report.service;

import io.wangk.peekaboo.common.core.base.service.IBaseService;
import io.wangk.peekaboo.common.report.model.ReportVisualizationAsset;

import java.util.List;

/**
 * 报表可视化素材数据操作服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface ReportVisualizationAssetService extends IBaseService<ReportVisualizationAsset, Long> {

    /**
     * 保存新增对象。
     *
     * @param asset 新增对象。
     * @return 返回新增对象。
     */
    ReportVisualizationAsset saveNew(ReportVisualizationAsset asset);

    /**
     * 更新数据对象。
     *
     * @param asset         更新的对象。
     * @param originalAsset 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(ReportVisualizationAsset asset, ReportVisualizationAsset originalAsset);

    /**
     * 删除指定数据。
     *
     * @param assetId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long assetId);

    /**
     * 获取可视化素材查询结果。默认主键倒排。
     *
     * @param filter 过滤对象。
     * @return 查询结果集。
     */
    List<ReportVisualizationAsset> getReportVisualizationAssetList(ReportVisualizationAsset filter);
}
