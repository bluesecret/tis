package io.wangk.peekaboo.common.satoken.util;

import cn.dev33.satoken.stp.StpInterface;
import io.wangk.peekaboo.common.core.cache.CacheConfig;
import io.wangk.peekaboo.common.core.object.TokenData;
import io.wangk.peekaboo.common.core.util.RedisKeyUtil;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 自定义权限加载接口实现类
 *
 * @author wangk
 * @date 2025-01-14
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    private RedissonClient redissonClient;
    @Resource(name = "caffeineCacheManager")
    private CacheManager cacheManager;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        TokenData tokenData = TokenData.takeFromRequest();
        String permCodeKey = RedisKeyUtil.makeSessionPermCodeKey(tokenData.getSessionId());
        Cache cache = cacheManager.getCache(CacheConfig.CacheEnum.USER_PERM_CODE_CACHE.name());
        Assert.notNull(cache, "Cache USER_PERM_CODE_CACHE can't be NULL");
        Cache.ValueWrapper wrapper = cache.get(permCodeKey);
        if (wrapper != null) {
            return (List<String>) wrapper.get();
        }
        RSet<String> permCodeSet = redissonClient.getSet(permCodeKey);
        Set<String> localPermCodeSet = permCodeSet.readAll();
        List<String> permCodeList = new ArrayList<>(localPermCodeSet);
        cache.put(permCodeKey, permCodeList);
        return permCodeList;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return new ArrayList<>();
    }
}
