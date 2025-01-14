package io.wangk.peekaboo.common.log.dao;

import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.log.model.SysOperationLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统操作日志对应的数据访问对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface SysOperationLogMapper extends BaseDaoMapper<SysOperationLog> {

    /**
     * 批量插入。
     *
     * @param operationLogList 操作日志列表。
     */
    void insertList(List<SysOperationLog> operationLogList);

    /**
     * 根据过滤条件和排序规则，查询操作日志。
     *
     * @param sysOperationLogFilter 操作日志的过滤对象。
     * @param orderBy               排序规则。
     * @return 查询列表。
     */
    List<SysOperationLog> getSysOperationLogList(
            @Param("sysOperationLogFilter") SysOperationLog sysOperationLogFilter,
            @Param("orderBy") String orderBy);
}