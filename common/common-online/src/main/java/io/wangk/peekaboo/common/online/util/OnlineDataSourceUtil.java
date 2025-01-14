package io.wangk.peekaboo.common.online.util;

import io.wangk.peekaboo.common.core.exception.MyRuntimeException;
import io.wangk.peekaboo.common.dbutil.provider.DataSourceProvider;
import io.wangk.peekaboo.common.dbutil.util.DataSourceUtil;
import io.wangk.peekaboo.common.online.model.OnlineDblink;
import io.wangk.peekaboo.common.online.service.OnlineDblinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 在线表单模块动态加载的数据源工具类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Component
public class OnlineDataSourceUtil extends DataSourceUtil {

    @Autowired
    private OnlineDblinkService dblinkService;

    @Override
    public int getDblinkTypeByDblinkId(Long dblinkId) {
        DataSourceProvider provider = this.dblinkProviderMap.get(dblinkId);
        if (provider != null) {
            return provider.getDblinkType();
        }
        OnlineDblink dblink = dblinkService.getById(dblinkId);
        if (dblink == null) {
            throw new MyRuntimeException("Online DblinkId [" + dblinkId + "] doesn't exist!");
        }
        this.dblinkProviderMap.put(dblinkId, this.getProvider(dblink.getDblinkType()));
        return dblink.getDblinkType();
    }

    @Override
    public String getDblinkConfigurationByDblinkId(Long dblinkId) {
        OnlineDblink dblink = dblinkService.getById(dblinkId);
        if (dblink == null) {
            throw new MyRuntimeException("Online DblinkId [" + dblinkId + "] doesn't exist!");
        }
        return dblink.getConfiguration();
    }
}
