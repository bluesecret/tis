package io.wangk.peekaboo.common.social.controller;

import com.alibaba.fastjson.JSON;
import io.wangk.peekaboo.common.core.object.ResponseResult;
import io.wangk.peekaboo.common.social.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 第三方验证接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@RestController
@RequestMapping("/oauth")
public class AuthController {

    /**
     * 跳转第三方平台授权页面
     *
     * @param url 第三方授权页面地址，该地址会重定向到指定的回调地址（需配置在第三方平台配置的回调地址）
     * @return 重定向的数据。
     */
    @GetMapping(value = "/redirect")
    public Object redirect(String url) throws Exception {
        return JSON.parse(HttpUtil.getResponseWithRedirect(url));
    }

    /**
     * 飞书是通过重定向获取code，所以需要单独处理。此方法目前只为飞书使用。
     *
     * @param code 授权码。
     * @return 授权码。
     */
    @GetMapping(value = "/getCode")
    public ResponseResult<String> getCode(@RequestParam String code) {
        return ResponseResult.success(code);
    }
}
