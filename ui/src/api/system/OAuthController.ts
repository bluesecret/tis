import { BaseController } from '@/api/BaseController';
import { RequestOption } from '@/common/http/types';
import { ANY_OBJECT } from '@/types/generic';
import { API_CONTEXT } from '../config';

export default class OAuthController extends BaseController {
  // 飞书需要通过飞书的接口重定向
  static redirect(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.get('oauth/redirect', params, httpOptions);
  }
  // 第三方oauth登录
  static login(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.get(API_CONTEXT + '/upms/login/doLoginByAuth', params, httpOptions);
  }
  // 绑定第三方账号
  static bind(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.get(API_CONTEXT + '/upms/login/bindSocialData', params, httpOptions);
  }
  // 用户名密码登录并绑定第三方账号
  static doLoginAndBind(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/upms/login/doLoginAndBindSocialData', params, httpOptions);
  }
  // 获取第三方登录绑定信息
  static getBindInfo(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.get(API_CONTEXT + '/upms/login/listSocialData', params, httpOptions);
  }
  // 获取微信公众号二维码
  static getWechatQrCode(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.get('/wechat/mp/generateQrCode', params, httpOptions);
  }
  // 检查微信公众号扫码状态
  static checkWechatStatus(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post('/wechat/mp/checkStatus', params, httpOptions);
  }
}
