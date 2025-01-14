package io.wangk.peekaboo.common.satoken.util;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.wangk.peekaboo.common.core.cache.CacheConfig;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.constant.ErrorCodeEnum;
import io.wangk.peekaboo.common.core.object.LoginUserInfo;
import io.wangk.peekaboo.common.core.object.ResponseResult;
import io.wangk.peekaboo.common.core.object.TokenData;
import io.wangk.peekaboo.common.core.util.AopTargetUtil;
import io.wangk.peekaboo.common.core.util.MyCommonUtil;
import io.wangk.peekaboo.common.core.util.RedisKeyUtil;
import io.wangk.peekaboo.common.satoken.annotation.SaTokenDenyAuth;
import org.redisson.api.RMap;
import org.redisson.api.RSet;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 通用工具方法。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Component
public class SaTokenUtil {

    @Autowired
    private RedissonClient redissonClient;
    @Resource(name = "caffeineCacheManager")
    private CacheManager cacheManager;

    @Value("${spring.application.name}")
    private String applicationName;

    public static final String SA_TOKEN_PERM_CODES_KEY = "SaTokenPermCodes";
    public static final String SA_TOKEN_PERM_CODES_PUBLISH_TOPIC = "SaTokenPermCodesTopic";

    /**
     * 处理免验证接口。目前仅用于微服务的业务服务。
     */
    public void handleNoAuthIntercept() {
        if (!StpUtil.isLogin()) {
            return;
        }
        SaSession session = StpUtil.getTokenSession();
        if (session != null) {
            TokenData tokenData = JSON.toJavaObject(
                    (JSONObject) session.get(TokenData.REQUEST_ATTRIBUTE_NAME), TokenData.class);
            TokenData.addToRequest(tokenData);
            tokenData.setToken(session.getToken());
        }
    }

    /**
     * 处理权限验证，通常在拦截器中调用。用于微服务中业务服务。
     *
     * @param request 当前请求。
     * @param handler 拦截器中的处理器。
     * @return 拦截验证处理结果。
     */
    public ResponseResult<Void> handleAuthInterceptEx(HttpServletRequest request, Object handler) {
        String appCode = MyCommonUtil.getAppCodeFromRequest();
        if (StrUtil.isNotBlank(appCode)) {
            String token = request.getHeader(TokenData.REQUEST_ATTRIBUTE_NAME);
            if (StrUtil.isBlank(token)) {
                String errorMessage = "第三方登录没有包含Token信息！";
                return ResponseResult.error(
                        HttpServletResponse.SC_UNAUTHORIZED, ErrorCodeEnum.UNAUTHORIZED_LOGIN, errorMessage);
            }
            TokenData tokenData = JSON.parseObject(token, TokenData.class);
            TokenData.addToRequest(tokenData);
            return ResponseResult.success();
        }
        String dontAuth = request.getHeader(ApplicationConstant.HTTP_HEADER_DONT_AUTH);
        if (BooleanUtil.toBoolean(dontAuth)) {
            this.handleNoAuthIntercept();
            return ResponseResult.success();
        }
        return this.handleAuthIntercept(request, handler);
    }

    /**
     * 处理权限验证，通常在拦截器中调用。通常用于单体服务。
     *
     * @param request 当前请求。
     * @param handler 拦截器中的处理器。
     * @return 拦截验证处理结果。
     */
    public ResponseResult<Void> handleAuthIntercept(HttpServletRequest request, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return ResponseResult.success();
        }
        Method method = ((HandlerMethod) handler).getMethod();
        String errorMessage;
        //如果没有登录则直接交给satoken注解去验证。
        if (!StpUtil.isLogin()) {
            // 如果此 Method 或其所属 Class 标注了 @SaIgnore，则忽略掉鉴权
            if (BooleanUtil.isTrue(SaStrategy.instance.isAnnotationPresent.apply(method, SaIgnore.class))) {
                return ResponseResult.success();
            }
            errorMessage = "非免登录接口必须包含Token信息！";
            return ResponseResult.error(HttpServletResponse.SC_UNAUTHORIZED, ErrorCodeEnum.UNAUTHORIZED_LOGIN, errorMessage);
        }
        //对于已经登录的用户一定存在session对象。
        SaSession session = StpUtil.getTokenSession();
        if (session == null) {
            errorMessage = "用户会话已过期，请重新登录！";
            return ResponseResult.error(HttpServletResponse.SC_UNAUTHORIZED, ErrorCodeEnum.UNAUTHORIZED_LOGIN, errorMessage);
        }
        TokenData tokenData = JSON.toJavaObject(
                (JSONObject) session.get(TokenData.REQUEST_ATTRIBUTE_NAME), TokenData.class);
        TokenData.addToRequest(tokenData);
        //将最初前端请求使用的token数据赋值给TokenData对象，以便于再次调用其他API接口时直接使用。
        tokenData.setToken(session.getToken());
        //如果是管理员可以直接跳过验证了。
        //基于橙单内部的权限规则优先验证，主要用于内部的白名单接口，以及在线表单和工作流那些动态接口的权限验证。
        if (Boolean.TRUE.equals(tokenData.getIsAdmin())
                || this.hasPermission(tokenData.getSessionId(), request.getRequestURI())) {
            return ResponseResult.success();
        }
        //对于应由白名单鉴权的接口，都会添加SaTokenDenyAuth注解，因此这里需要判断一下，
        //对于此类接口无需SaToken验证了，而是直接返回未授权，因为基于url的鉴权在上面的hasPermission中完成了。
        if (method.getAnnotation(SaTokenDenyAuth.class) != null) {
            return ResponseResult.error(HttpServletResponse.SC_UNAUTHORIZED, ErrorCodeEnum.NO_OPERATION_PERMISSION);
        }
        try {
            //执行基于stoken的注解鉴权。
            SaStrategy.instance.checkMethodAnnotation.accept(method);
        } catch (SaTokenException e) {
            return ResponseResult.error(HttpServletResponse.SC_UNAUTHORIZED, ErrorCodeEnum.NO_OPERATION_PERMISSION);
        }
        return ResponseResult.success();
    }

    /**
     * 构建satoken的登录Id。
     *
     * @return 拼接后的完整登录Id。
     */
    public static String makeLoginId(LoginUserInfo userInfo) {
        StringBuilder sb = new StringBuilder(128);
        sb.append("SATOKEN_LOGIN:");
        if (userInfo.getTenantId() != null) {
            sb.append(userInfo.getTenantId()).append(":");
        }
        sb.append(userInfo.getLoginName()).append(":").append(userInfo.getUserId());
        return sb.toString();
    }

    /**
     * 获取所有的权限字列表数据。
     *
     * @return 所有的权限字列表数据。
     */
    public List<String> getAllPermCodes() {
        RMap<String, Set<String>> permCodeMap = redissonClient.getMap(SA_TOKEN_PERM_CODES_KEY);
        if (!permCodeMap.isExists()) {
            return Collections.emptyList();
        }
        Set<String> permCodeSet = new TreeSet<>();
        for (RMap.Entry<String, Set<String>> entry : permCodeMap.entrySet()) {
            CollUtil.addAll(permCodeSet, permCodeMap.get(entry.getKey()));
        }
        return new LinkedList<>(permCodeSet);
    }

    /**
     * 获取所有租户运营应用的权限字列表数据。
     *
     * @return 所有的权限字列表数据。
     */
    public List<String> getAllTenantPermCodes() {
        RMap<String, Set<String>> permCodeMap = redissonClient.getMap(SA_TOKEN_PERM_CODES_KEY);
        if (!permCodeMap.isExists()) {
            return Collections.emptyList();
        }
        Set<String> permCodeSet = new TreeSet<>();
        for (RMap.Entry<String, Set<String>> entry : permCodeMap.entrySet()) {
            if (!entry.getKey().equals(ApplicationConstant.TENANT_ADMIN_APP_NAME)) {
                CollUtil.addAll(permCodeSet, permCodeMap.get(entry.getKey()));
            }
        }
        return new LinkedList<>(permCodeSet);
    }

    /**
     * 获取所有租户管理应用的权限字列表数据。
     *
     * @return 所有的权限字列表数据。
     */
    public List<String> getAllTenantAdminPermCodes() {
        RMap<String, Set<String>> permCodeMap = redissonClient.getMap(SA_TOKEN_PERM_CODES_KEY);
        if (!permCodeMap.isExists()) {
            return Collections.emptyList();
        }
        Set<String> permCodeSet = new TreeSet<>();
        for (RMap.Entry<String, Set<String>> entry : permCodeMap.entrySet()) {
            if (entry.getKey().equals(ApplicationConstant.TENANT_ADMIN_APP_NAME)) {
                CollUtil.addAll(permCodeSet, permCodeMap.get(entry.getKey()));
            }
        }
        return new LinkedList<>(permCodeSet);
    }

    /**
     * 收集当前服务的SaToken权限字列表，并缓存到Redis，便于统一查询。
     *
     * @param event 服务应用的启动事件。
     */
    public void collectPermCodes(ApplicationReadyEvent event) {
        redissonClient.getTopic(SA_TOKEN_PERM_CODES_PUBLISH_TOPIC)
                .addListener(String.class, (channel, message) -> this.doCollect(event));
        this.doCollect(event);
    }

    /**
     * 向所有已启动的服务发送权限字同步事件。
     */
    public void publishCollectPermCodes() {
        RTopic topic = redissonClient.getTopic(SA_TOKEN_PERM_CODES_PUBLISH_TOPIC);
        topic.publish(null);
    }

    private void doCollect(ApplicationReadyEvent event) {
        Map<String, Object> controllerMap = event.getApplicationContext().getBeansWithAnnotation(RestController.class);
        Set<String> permCodes = new HashSet<>();
        for (Map.Entry<String, Object> entry : controllerMap.entrySet()) {
            Object targetBean = AopTargetUtil.getTarget(entry.getValue());
            Method[] methods = ReflectUtil.getPublicMethods(targetBean.getClass());
            Arrays.stream(methods)
                    .map(m -> m.getAnnotation(SaCheckPermission.class))
                    .filter(Objects::nonNull)
                    .forEach(anno -> Collections.addAll(permCodes, anno.value()));
        }
        RMap<String, Set<String>> permCodeMap = redissonClient.getMap(SA_TOKEN_PERM_CODES_KEY);
        permCodeMap.put(applicationName, permCodes);
    }

    @SuppressWarnings("unchecked")
    private boolean hasPermission(String sessionId, String url) {
        // 为了提升效率，先检索Caffeine的一级缓存，如果不存在，再检索Redis的二级缓存，并将结果存入一级缓存。
        Set<String> localPermSet;
        String permKey = RedisKeyUtil.makeSessionPermIdKey(sessionId);
        Cache cache = cacheManager.getCache(CacheConfig.CacheEnum.USER_PERMISSION_CACHE.name());
        Assert.notNull(cache, "Cache USER_PERMISSION_CACHE can't be NULL.");
        Cache.ValueWrapper wrapper = cache.get(permKey);
        if (wrapper == null || CollUtil.isEmpty((Set<String>) wrapper.get())) {
            RSet<String> permSet = redissonClient.getSet(permKey);
            localPermSet = permSet.readAll();
            cache.put(permKey, localPermSet);
        } else {
            localPermSet = (Set<String>) wrapper.get();
        }
        return CollUtil.contains(localPermSet, url);
    }
}
