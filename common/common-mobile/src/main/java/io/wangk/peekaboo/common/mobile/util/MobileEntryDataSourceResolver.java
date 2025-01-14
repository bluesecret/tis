package io.wangk.peekaboo.common.mobile.util;

import io.wangk.peekaboo.common.core.object.TokenData;
import io.wangk.peekaboo.common.core.util.DataSourceResolver;
import org.springframework.stereotype.Component;

/**
 * 移动端所在的数据源类型值的解析器。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Component
public class MobileEntryDataSourceResolver implements DataSourceResolver {

    @Override
    public Integer resolve(String arg, Integer intArg, String methodName, Object[] methodArgs) {
        //如果登录Token中没有设置数据源类型(目前而言，租户运营服务会设置该值)，就会继续使用服务默认的数据源。
        TokenData tokenData = TokenData.takeFromRequest();
        if (tokenData == null) {
            return null;
        }
        return tokenData.getDatasourceType();
    }
}