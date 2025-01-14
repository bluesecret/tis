package io.wangk.peekaboo.common.report.service;

import io.wangk.peekaboo.common.core.object.MyPageData;
import io.wangk.peekaboo.common.core.object.MyPageParam;
import io.wangk.peekaboo.common.report.model.ReportDataset;
import io.wangk.peekaboo.common.report.object.ReportFilterParam;
import io.wangk.peekaboo.common.report.object.view.ViewDimensionData;
import io.wangk.peekaboo.common.report.object.view.ViewIndexData;
import io.wangk.peekaboo.common.report.object.view.ViewOrderData;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 在线报表运行时操作的数据服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface ReportOperationService {

    /**
     * 获取分组计算数据列表。
     *
     * @param dataset             数据集对象。
     * @param dimensionDataList   维度数据列表。
     * @param indexDataList       指标数据列表。
     * @param datasetFilterParams SQL数据集过滤参数列表。
     * @param filterParams        过滤参数列表。
     * @param orderDataList       排序数据列表。
     * @return 查询结果列表。
     */
    List<Map<String, Object>> getDataListWithGroup(
            ReportDataset dataset,
            List<ViewDimensionData> dimensionDataList,
            List<ViewIndexData> indexDataList,
            List<ReportFilterParam> datasetFilterParams,
            List<ReportFilterParam> filterParams,
            List<ViewOrderData> orderDataList);

    /**
     * 查询分页数据列表。
     *
     * @param dataset             数据集对象。
     * @param datasetFilterParams SQL数据集过滤参数列表。
     * @param filterParams        过滤参数列表。
     * @param orderDataList       排序数据列表。
     * @param pageParam           分页对象。
     * @return 分页数据对象。
     */
    MyPageData<Map<String, Object>> getDataListWithPage(
            ReportDataset dataset,
            List<ReportFilterParam> datasetFilterParams,
            List<ReportFilterParam> filterParams,
            List<ViewOrderData> orderDataList,
            MyPageParam pageParam);

    /**
     * 计算报表页面关联的权限数据集合。
     *
     * @param pageIdSet 页面Id集合。
     * @return 关联的权限集合。
     */
    Map<Long, Set<String>> calculatePermData(Set<Long> pageIdSet);
}
