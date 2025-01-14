package io.wangk.peekaboo.webadmin.interceptor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.wangk.peekaboo.common.core.cache.CacheConfig;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.constant.DataPermRuleType;
import io.wangk.peekaboo.common.core.constant.ErrorCodeEnum;
import io.wangk.peekaboo.common.core.exception.MyRuntimeException;
import io.wangk.peekaboo.common.core.object.ResponseResult;
import io.wangk.peekaboo.common.core.object.TokenData;
import io.wangk.peekaboo.common.core.util.ApplicationContextHolder;
import io.wangk.peekaboo.common.core.util.RedisKeyUtil;
import io.wangk.peekaboo.common.satoken.util.SaTokenUtil;
import io.wangk.peekaboo.webadmin.config.ThirdPartyAuthConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 登录用户Token验证、生成和权限验证的拦截器。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final ThirdPartyAuthConfig thirdPartyAuthConfig =
            ApplicationContextHolder.getBean("thirdPartyAuthConfig");

    private final RedissonClient redissonClient = ApplicationContextHolder.getBean(RedissonClient.class);
    private final CacheManager cacheManager = ApplicationContextHolder.getBean("caffeineCacheManager");

    private final SaTokenUtil saTokenUtil =
            ApplicationContextHolder.getBean("saTokenUtil");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String appCode = this.getAppCodeFromRequest(request);
        if (StrUtil.isNotBlank(appCode)) {
            return this.handleThirdPartyRequest(appCode, request);
        }
        ResponseResult<Void> result = saTokenUtil.handleAuthIntercept(request, handler);
        if (!result.isSuccess()) {
            ResponseResult.output(result.getHttpStatus(), result);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // 这里需要空注解，否则sonar会不happy。
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // 这里需要空注解，否则sonar会不happy。
    }

    private String getTokenFromRequest(HttpServletRequest request, String appCode) {
        ThirdPartyAuthConfig.AuthProperties prop = thirdPartyAuthConfig.getApplicationMap().get(appCode);
        String token = request.getHeader(prop.getTokenHeaderKey());
        if (StrUtil.isBlank(token)) {
            token = request.getParameter(prop.getTokenHeaderKey());
        }
        if (StrUtil.isBlank(token)) {
            token = request.getHeader(ApplicationConstant.HTTP_HEADER_INTERNAL_TOKEN);
        }
        return token;
    }

    private String getAppCodeFromRequest(HttpServletRequest request) {
        String appCode = request.getHeader("AppCode");
        if (StrUtil.isBlank(appCode)) {
            appCode = request.getParameter("AppCode");
        }
        return appCode;
    }

    private boolean handleThirdPartyRequest(String appCode, HttpServletRequest request) throws IOException {
        String token = this.getTokenFromRequest(request, appCode);
        ThirdPartyAuthConfig.AuthProperties authProps = thirdPartyAuthConfig.getApplicationMap().get(appCode);
        if (authProps == null) {
            String msg = StrFormatter.format("请求的 appCode[{}] 信息，在当前服务中尚未配置！", appCode);
            ResponseResult.output(ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, msg));
            return false;
        }
        ResponseResult<TokenData> result = this.getAndCacheThirdPartyTokenData(authProps, token);
        if (!result.isSuccess()) {
            ResponseResult.output(result.getHttpStatus(),
                    ResponseResult.error(ErrorCodeEnum.UNAUTHORIZED_LOGIN, result.getErrorMessage()));
            return false;
        }
        TokenData tokenData = result.getData();
        tokenData.setAppCode(appCode);
        tokenData.setSessionId(this.prependAppCode(authProps.getAppCode(), tokenData.getSessionId()));
        TokenData.addToRequest(tokenData);
        String url = request.getRequestURI();
        if (Boolean.FALSE.equals(tokenData.getIsAdmin())
                && !this.hasThirdPartyPermission(authProps, tokenData, url)) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN, ResponseResult.error(ErrorCodeEnum.NO_OPERATION_PERMISSION));
            return false;
        }
        return true;
    }

    private ResponseResult<TokenData> getAndCacheThirdPartyTokenData(
            ThirdPartyAuthConfig.AuthProperties authProps, String token) {
        if (authProps.getTokenExpiredSeconds() == 0) {
            return this.getThirdPartyTokenData(authProps, token);
        }
        String tokeKey = this.prependAppCode(authProps.getAppCode(), RedisKeyUtil.makeSessionIdKey(token));
        RBucket<String> sessionData = redissonClient.getBucket(tokeKey);
        if (sessionData.isExists()) {
            return ResponseResult.success(JSON.parseObject(sessionData.get(), TokenData.class));
        }
        ResponseResult<TokenData> responseResult = this.getThirdPartyTokenData(authProps, token);
        if (responseResult.isSuccess()) {
            sessionData.set(JSON.toJSONString(responseResult.getData()), authProps.getTokenExpiredSeconds(), TimeUnit.SECONDS);
        }
        return responseResult;
    }

    private String prependAppCode(String appCode, String key) {
        return appCode.toUpperCase() + ":" + key;
    }

    private ResponseResult<TokenData> getThirdPartyTokenData(
            ThirdPartyAuthConfig.AuthProperties authProps, String token) {
        try {
            String resultData = this.invokeThirdPartyUrl(authProps.getBaseUrl() + "/getTokenData", token);
            return JSON.parseObject(resultData, new TypeReference<ResponseResult<TokenData>>() {});
        } catch (MyRuntimeException ex) {
            return ResponseResult.error(ErrorCodeEnum.FAILED_TO_INVOKE_THIRDPARTY_URL, ex.getMessage());
        }
    }

    private ResponseResult<ThirdPartyAppPermData> getThirdPartyPermData(
            ThirdPartyAuthConfig.AuthProperties authProps, String token) {
        try {
            String resultData = this.invokeThirdPartyUrl(authProps.getBaseUrl() + "/getPermData", token);
            return JSON.parseObject(resultData, new TypeReference<ResponseResult<ThirdPartyAppPermData>>() {});
        } catch (MyRuntimeException ex) {
            return ResponseResult.error(ErrorCodeEnum.FAILED_TO_INVOKE_THIRDPARTY_URL, ex.getMessage());
        }
    }

    private String invokeThirdPartyUrl(String url, String token) {
        Map<String, String> headerMap = new HashMap<>(1);
        headerMap.put("Authorization", token);
        StringBuilder fullUrl = new StringBuilder(128);
        fullUrl.append(url).append("?token=").append(token);
        HttpResponse httpResponse = HttpUtil.createGet(fullUrl.toString()).addHeaders(headerMap).execute();
        if (!httpResponse.isOk()) {
            String msg = StrFormatter.format(
                    "Failed to call [{}] with ERROR HTTP Status [{}] and [{}].",
                    url, httpResponse.getStatus(), httpResponse.body());
            log.error(msg);
            throw new MyRuntimeException(msg);
        }
        return httpResponse.body();
    }

    @SuppressWarnings("unchecked")
    private boolean hasThirdPartyPermission(
            ThirdPartyAuthConfig.AuthProperties authProps, TokenData tokenData, String url) {
        // 为了提升效率，先检索Caffeine的一级缓存，如果不存在，再检索Redis的二级缓存，并将结果存入一级缓存。
        String permKey = RedisKeyUtil.makeSessionPermIdKey(tokenData.getSessionId());
        Cache cache = cacheManager.getCache(CacheConfig.CacheEnum.USER_PERMISSION_CACHE.name());
        Assert.notNull(cache, "Cache USER_PERMISSION_CACHE can't be NULL");
        Cache.ValueWrapper wrapper = cache.get(permKey);
        if (wrapper != null) {
            Object cachedData = wrapper.get();
            if (cachedData != null) {
                return ((Set<String>) cachedData).contains(url);
            }
        }
        Set<String> localPermSet;
        RSet<String> permSet = redissonClient.getSet(permKey);
        if (permSet.isExists()) {
            localPermSet = permSet.readAll();
            cache.put(permKey, localPermSet);
            return localPermSet.contains(url);
        }
        ResponseResult<ThirdPartyAppPermData> responseResult = this.getThirdPartyPermData(authProps, tokenData.getToken());
        this.cacheThirdPartyDataPermData(authProps, tokenData, responseResult.getData().getDataPerms());
        if (CollUtil.isEmpty(responseResult.getData().urlPerms)) {
            return false;
        }
        permSet.addAll(responseResult.getData().urlPerms);
        permSet.expire(authProps.getPermExpiredSeconds(), TimeUnit.SECONDS);
        localPermSet = new HashSet<>(responseResult.getData().urlPerms);
        cache.put(permKey, localPermSet);
        return localPermSet.contains(url);
    }

    private void cacheThirdPartyDataPermData(
            ThirdPartyAuthConfig.AuthProperties authProps, TokenData tokenData, List<ThirdPartyAppDataPermData> dataPerms) {
        if (CollUtil.isEmpty(dataPerms)) {
            return;
        }
        Map<Integer, List<ThirdPartyAppDataPermData>> dataPermMap =
                dataPerms.stream().collect(Collectors.groupingBy(ThirdPartyAppDataPermData::getRuleType));
        Map<Integer, List<ThirdPartyAppDataPermData>> normalizedDataPermMap = new HashMap<>(dataPermMap.size());
        for (Map.Entry<Integer, List<ThirdPartyAppDataPermData>> entry : dataPermMap.entrySet()) {
            List<ThirdPartyAppDataPermData> ruleTypeDataPermDataList;
            if (entry.getKey().equals(DataPermRuleType.TYPE_DEPT_AND_CHILD_DEPT)) {
                ruleTypeDataPermDataList =
                        normalizedDataPermMap.computeIfAbsent(DataPermRuleType.TYPE_CUSTOM_DEPT_LIST, k -> new LinkedList<>());
            } else {
                ruleTypeDataPermDataList =
                        normalizedDataPermMap.computeIfAbsent(entry.getKey(), k -> new LinkedList<>());
            }
            ruleTypeDataPermDataList.addAll(entry.getValue());
        }
        Map<Integer, String> resultDataPermMap = new HashMap<>(normalizedDataPermMap.size());
        for (Map.Entry<Integer, List<ThirdPartyAppDataPermData>> entry : normalizedDataPermMap.entrySet()) {
            if (entry.getKey().equals(DataPermRuleType.TYPE_CUSTOM_DEPT_LIST)) {
                String deptIds = entry.getValue().stream()
                        .map(ThirdPartyAppDataPermData::getDeptIds).collect(Collectors.joining(","));
                resultDataPermMap.put(entry.getKey(), deptIds);
            } else {
                resultDataPermMap.put(entry.getKey(), "null");
            }
        }
        Map<String, Map<Integer, String>> menuDataPermMap = new HashMap<>(1);
        menuDataPermMap.put(ApplicationConstant.DATA_PERM_ALL_MENU_ID, resultDataPermMap);
        String dataPermSessionKey = RedisKeyUtil.makeSessionDataPermIdKey(tokenData.getSessionId());
        RBucket<String> bucket = redissonClient.getBucket(dataPermSessionKey);
        bucket.set(JSON.toJSONString(menuDataPermMap), authProps.getPermExpiredSeconds(), TimeUnit.SECONDS);
    }

    @Data
    public static class ThirdPartyAppPermData {
        /**
         * 当前用户会话可访问的url接口地址列表。
         */
        private List<String> urlPerms;
        /**
         * 当前用户会话的数据权限列表。
         */
        private List<ThirdPartyAppDataPermData> dataPerms;
    }

    @Data
    public static class ThirdPartyAppDataPermData {
        /**
         * 数据权限的规则类型。需要按照橙单的约定返回。具体值可参考DataPermRuleType常量类。
         */
        private Integer ruleType;
        /**
         * 部门Id集合，多个部门Id之间逗号分隔。
         * 注意：仅当ruleType为3、4、5时需要包含该字段值。
         */
        private String deptIds;
    }
}
