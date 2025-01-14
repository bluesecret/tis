package io.wangk.peekaboo.common.social.util;

import io.wangk.peekaboo.common.social.config.CommonSocialProperties;
import io.wangk.peekaboo.common.social.constant.AuthSourceType;
import io.wangk.peekaboo.common.social.object.AuthUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 第三方授权的工具类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Component
public class AuthUtil {

    @Autowired
    private CommonSocialProperties prop;
    @Autowired
    private DingTalkUtil dingTalkUtil;
    @Autowired
    private FeiShuUtil feiShuUtil;
    @Autowired
    private WeworkUtil weworkUtil;
    @Autowired
    private WechatUtil wechatUtil;

    public AuthUserInfo getAuthUserInfo(String source, String code) {
        AuthUserInfo userInfo = null;
        switch (source.toLowerCase()) {
            case AuthSourceType.DING_TALK:
                userInfo = dingTalkUtil.getUserInfo(code);
                break;
            case AuthSourceType.FEI_SHU:
                userInfo = feiShuUtil.getUserInfo(code);
                break;
            case AuthSourceType.WE_WORK:
                userInfo = weworkUtil.getUserInfo(code);
                break;
            case AuthSourceType.WE_CHAT:
                userInfo = wechatUtil.getWechatUserInfo(code);
                break;
            default:
                break;
        }
        return userInfo;
    }
}
