package io.wangk.peekaboo.webadmin.upms.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.wangk.peekaboo.common.core.annotation.MyRequestBody;
import io.wangk.peekaboo.common.core.object.*;
import io.wangk.peekaboo.common.core.util.RedisKeyUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 在线用户控制器对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Tag(name = "在线用户接口")
@Slf4j
@RestController
@RequestMapping("/admin/upms/loginUser")
public class LoginUserController {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 显示在线用户列表。
     *
     * @param loginName 登录名过滤。
     * @param pageParam 分页参数。
     * @return 登录用户信息列表。
     */
    @SaCheckPermission("loginUser.view")
    @PostMapping("/list")
    public ResponseResult<MyPageData<LoginUserInfo>> list(
            @MyRequestBody String loginName, @MyRequestBody MyPageParam pageParam) {
        int skipCount = (pageParam.getPageNum() - 1) * pageParam.getPageSize();
        String patternKey;
        if (StrUtil.isBlank(loginName)) {
            patternKey = RedisKeyUtil.getSessionIdPrefix() + "*";
        } else {
            patternKey = RedisKeyUtil.getSessionIdPrefix(loginName) + "*";
        }
        List<LoginUserInfo> loginUserInfoList = new LinkedList<>();
        Iterable<String> keys = redissonClient.getKeys().getKeysByPattern(patternKey);
        for (String key : keys) {
            loginUserInfoList.add(this.buildTokenDataByRedisKey(key));
        }
        loginUserInfoList.sort((o1, o2) -> (int) (o2.getLoginTime().getTime() - o1.getLoginTime().getTime()));
        int toIndex = Math.min(skipCount + pageParam.getPageSize(), loginUserInfoList.size());
        List<LoginUserInfo> resultList = loginUserInfoList.subList(skipCount, toIndex);
        return ResponseResult.success(new MyPageData<>(resultList, (long) loginUserInfoList.size()));
    }

    /**
     * 强制下线指定登录会话。
     *
     * @param sessionId 待强制下线的SessionId。
     * @return 应答结果对象。
     */
    @SaCheckPermission("loginUser.delete")
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody String sessionId) {
        RBucket<String> sessionData = redissonClient.getBucket(sessionId);
        TokenData tokenData = JSON.parseObject(sessionData.get(), TokenData.class);
        StpUtil.kickoutByTokenValue(tokenData.getToken());
        sessionData.delete();
        return ResponseResult.success();
    }

    private LoginUserInfo buildTokenDataByRedisKey(String key) {
        RBucket<String> sessionData = redissonClient.getBucket(key);
        TokenData tokenData = JSON.parseObject(sessionData.get(), TokenData.class);
        LoginUserInfo userInfo = BeanUtil.copyProperties(tokenData, LoginUserInfo.class);
        userInfo.setSessionId(tokenData.getMySessionId());
        return userInfo;
    }
}
