package io.wangk.peekaboo.common.report.util;

import io.wangk.peekaboo.common.core.exception.MyRuntimeException;
import io.wangk.peekaboo.common.dbutil.provider.DataSourceProvider;
import io.wangk.peekaboo.common.dbutil.util.DataSourceUtil;
import io.wangk.peekaboo.common.report.model.ReportDblink;
import io.wangk.peekaboo.common.report.service.ReportDblinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 报表打印模块动态加载的数据源工具类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Component
public class ReportDataSourceUtil extends DataSourceUtil {

    @Autowired
    private ReportDblinkService dblinkService;

    @Override
    public int getDblinkTypeByDblinkId(Long dblinkId) {
        DataSourceProvider provider = this.dblinkProviderMap.get(dblinkId);
        if (provider != null) {
            return provider.getDblinkType();
        }
        ReportDblink dblink = dblinkService.getById(dblinkId);
        if (dblink == null) {
            throw new MyRuntimeException("Report DblinkId [" + dblinkId + "] doesn't exist!");
        }
        this.dblinkProviderMap.put(dblinkId, this.getProvider(dblink.getDblinkType()));
        return dblink.getDblinkType();
    }

    @Override
    public String getDblinkConfigurationByDblinkId(Long dblinkId) {
        ReportDblink dblink = dblinkService.getById(dblinkId);
        if (dblink == null) {
            throw new MyRuntimeException("Report DblinkId [" + dblinkId + "] doesn't exist!");
        }
        return dblink.getConfiguration();
    }
}
