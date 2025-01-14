package io.wangk.peekaboo.common.online.util;

import com.alibaba.fastjson.JSONObject;
import io.wangk.peekaboo.common.core.object.CallResult;
import io.wangk.peekaboo.common.core.util.ContextUtil;
import io.wangk.peekaboo.common.online.dto.OnlineFilterDto;
import io.wangk.peekaboo.common.online.exception.OnlineRuntimeException;
import io.wangk.peekaboo.common.online.model.OnlineTable;
import io.wangk.peekaboo.common.online.object.BaseOnlineExtendExecutor;
import io.wangk.peekaboo.common.online.service.OnlineTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 在线表单后台扩展执行接口工具类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Component
public class OnlineExtendExecutorUtil {

    @Autowired
    private OnlineTableService onlineTableService;
    @Autowired
    private OnlineOperationHelper onlineOperationHelper;

    /**
     * 设置后台扩展执行器到线程本地化对象。
     *
     * @param e 后台扩展执行器。
     */
    public static void setOnlineExtendExecutorToLocal(BaseOnlineExtendExecutor e) {
        ContextUtil.getHttpRequest().setAttribute(BaseOnlineExtendExecutor.class.getSimpleName(), e);
    }

    /**
     * 获取当前线程的后台扩展执行器。
     *
     * @return 当前线程的后台扩展执行器。
     */
    public static BaseOnlineExtendExecutor getOnlineExtendExecutorFromLocal() {
        return (BaseOnlineExtendExecutor) ContextUtil.getHttpRequest().getAttribute(BaseOnlineExtendExecutor.class.getSimpleName());
    }

    /**
     * 执行插入前操作。
     *
     * @param table 数据表对象。
     * @param data  表数据。
     */
    public void doBeforeInsert(OnlineTable table, JSONObject data) {
        BaseOnlineExtendExecutor executor = getOnlineExtendExecutorFromLocal();
        if (executor != null) {
            CallResult r = executor.beforeInsert(table, data);
            if (!r.isSuccess()) {
                throw new OnlineRuntimeException(r.getErrorMessage());
            }
        }
    }

    /**
     * 执行插入后操作。
     *
     * @param table 数据表对象。
     * @param data  表数据。
     */
    public void doAfterInsert(OnlineTable table, JSONObject data) {
        BaseOnlineExtendExecutor executor = getOnlineExtendExecutorFromLocal();
        if (executor != null) {
            executor.afterInsert(table, data);
        }
    }

    /**
     * 主从表数据级联插入之前执行的操作。
     *
     * @param masterTable          主表对象。
     * @param masterData           主表数据。
     * @param slaveTableAndDataMap 从表对象和数据的映射关系。
     */
    public void doBeforeInsertWithRelation(
            OnlineTable masterTable, JSONObject masterData, Map<OnlineTable, List<JSONObject>> slaveTableAndDataMap) {
        BaseOnlineExtendExecutor executor = getOnlineExtendExecutorFromLocal();
        if (executor != null) {
            CallResult r = executor.beforeInsertWithRelation(masterTable, masterData, slaveTableAndDataMap);
            if (!r.isSuccess()) {
                throw new OnlineRuntimeException(r.getErrorMessage());
            }
        }
    }

    /**
     * 主从表数据级联插入之后执行的操作。
     *
     * @param masterTable          主表对象。
     * @param masterData           主表数据。
     * @param slaveTableAndDataMap 从表对象和数据的映射关系。
     */
    public void doAfterInsertWithRelation(
            OnlineTable masterTable, JSONObject masterData, Map<OnlineTable, List<JSONObject>> slaveTableAndDataMap) {
        BaseOnlineExtendExecutor executor = getOnlineExtendExecutorFromLocal();
        if (executor != null) {
            executor.afterInsertWithRelation(masterTable, masterData, slaveTableAndDataMap);
        }
    }

    /**
     * 表数据更新之前执行的操作。
     *
     * @param table 表对象。
     * @param data  表数据。
     */
    public void doBeforeUpdate(OnlineTable table, JSONObject data) {
        BaseOnlineExtendExecutor executor = getOnlineExtendExecutorFromLocal();
        if (executor != null) {
            CallResult r = executor.beforeUpdate(table, data);
            if (!r.isSuccess()) {
                throw new OnlineRuntimeException(r.getErrorMessage());
            }
        }
    }

    /**
     * 表数据更新之后执行的操作。
     *
     * @param table 表对象。
     * @param data  表数据。
     */
    public void doAfterUpdate(OnlineTable table, JSONObject data) {
        BaseOnlineExtendExecutor executor = getOnlineExtendExecutorFromLocal();
        if (executor != null) {
            executor.afterUpdate(table, data);
        }
    }

    /**
     * 表数据批量部分更新之后执行的操作。
     *
     * @param table   表对象。
     * @param data    部分待更新的表数据。key是字段名，value是字段值。
     * @param dataIds 主键Id集合。
     */
    public void doAfterUpdateBatch(OnlineTable table, JSONObject data, List<Object> dataIds) {
        BaseOnlineExtendExecutor executor = getOnlineExtendExecutorFromLocal();
        if (executor != null) {
            executor.afterUpdateBatch(table, data, dataIds);
        }
    }

    /**
     * 主从表数据级联更新之前执行的操作。
     *
     * @param masterTable          主表对象。
     * @param masterData           主表数据。
     * @param slaveTableAndDataMap 从表对象和数据的映射关系。
     */
    public void doBeforeUpdateWithRelationn(
            OnlineTable masterTable, JSONObject masterData, Map<OnlineTable, List<JSONObject>> slaveTableAndDataMap) {
        BaseOnlineExtendExecutor executor = getOnlineExtendExecutorFromLocal();
        if (executor != null) {
            CallResult r = executor.beforeUpdateWithRelation(masterTable, masterData, slaveTableAndDataMap);
            if (!r.isSuccess()) {
                throw new OnlineRuntimeException(r.getErrorMessage());
            }
        }
    }

    /**
     * 主从表数据级联更新之后执行的操作。
     *
     * @param masterTable          主表对象。
     * @param masterData           主表数据。
     * @param slaveTableAndDataMap 从表对象和数据的映射关系。
     */
    public void doAfterUpdateWithRelationn(
            OnlineTable masterTable, JSONObject masterData, Map<OnlineTable, List<JSONObject>> slaveTableAndDataMap) {
        BaseOnlineExtendExecutor executor = getOnlineExtendExecutorFromLocal();
        if (executor != null) {
            executor.afterUpdateWithRelation(masterTable, masterData, slaveTableAndDataMap);
        }
    }

    /**
     * 表数据删除之前执行的操作。
     *
     * @param table  表对象。
     * @param dataId 表数据主键Id。
     */
    public void doBeforeDelete(OnlineTable table, Object dataId) {
        BaseOnlineExtendExecutor executor = getOnlineExtendExecutorFromLocal();
        if (executor != null) {
            CallResult r = executor.beforeDelete(table, dataId);
            if (!r.isSuccess()) {
                throw new OnlineRuntimeException(r.getErrorMessage());
            }
        }
    }

    /**
     * 表数据删除之后执行的操作。
     *
     * @param table  表对象。
     * @param dataId 表数据主键Id。
     */
    public void doAfterDelete(OnlineTable table, Object dataId) {
        BaseOnlineExtendExecutor executor = getOnlineExtendExecutorFromLocal();
        if (executor != null) {
            executor.afterDelete(table, dataId);
        }
    }

    /**
     * 表数据单条详情查询之后执行的操作。
     *
     * @param table  表对象。
     * @param result 即将返回给前端的结果数据。
     */
    public void doAfterSelectOne(OnlineTable table, Map<String, Object> result) {
        BaseOnlineExtendExecutor executor = getOnlineExtendExecutorFromLocal();
        if (executor != null) {
            executor.afterSelectOne(table, result);
        }
    }

    /**
     * 表数据列表查询之前执行的操作。
     *
     * @param table      表对象。
     * @param filterList 过滤条件，如果有新的过滤条件，可直接添加到该过滤列表。
     */
    public void doBeforeSelectList(OnlineTable table, List<OnlineFilterDto> filterList) {
        BaseOnlineExtendExecutor executor = getOnlineExtendExecutorFromLocal();
        if (executor != null) {
            executor.beforeSelectList(table, filterList);
        }
    }

    /**
     * 表数据列表查询之后执行的操作。
     *
     * @param table      表对象。
     * @param resultList 即将返回给前端的结果数据。
     */
    public void doAfterSelectList(OnlineTable table, List<Map<String, Object>> resultList) {
        BaseOnlineExtendExecutor executor = getOnlineExtendExecutorFromLocal();
        if (executor != null) {
            executor.afterSelectList(table, resultList);
        }
    }
}
