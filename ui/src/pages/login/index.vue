<template>
  <div class="login-form">
    <div class="logo-box logo-right">
      <img src="@/assets/img/login_logo2.png" style="height: 40px" alt="" />
      <span style="display: none">橙单低代码生成工具</span>
      <el-space direction="horizontal">
        <a href="/">橙单官网</a>
        <span>|</span>
        <a href="/">代码生成</a>
      </el-space>
    </div>
    <div class="login-box">
      <div class="img-box">
        <img class="img-title" style="height: 55px" src="@/assets/img/login_title.png" alt="" />
        <img :src="bkImg" style="flex-shrink: 0; height: 100%" />
      </div>
      <div class="login-input" v-if="thirdLogin.loginType === 'local'">
        <img src="@/assets/img/orange.png" style="width: 62px; margin-bottom: 9px" alt="" />
        <span class="title">欢迎登录</span>
        <span class="desc">橙单低代码生成演示工程</span>
        <el-form
          :model="dataForm"
          :rules="dataRule"
          label-position="top"
          ref="loginForm"
          @keyup.enter.stop="() => {}"
        >
          <el-col :span="24">
            <el-form-item prop="mobilePhone" label="" style="margin-top: 48px; margin-bottom: 25px">
              <el-input v-model="dataForm.mobilePhone" style="width: 100%" placeholder="请输入帐号">
                <!-- <template #prefix>
                  <img src="@/assets/img/login_username.png" alt="" />
                </template> -->
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item prop="password" label="">
              <el-input
                v-model="dataForm.password"
                style="width: 100%"
                type="password"
                placeholder="请输入密码"
                show-password
              >
              </el-input>
            </el-form-item>
          </el-col>
          <el-button
            class="login-btn-submit"
            type="primary"
            size="large"
            @click="dataFormSubmit()"
            style="width: 100%; margin-top: 48px"
          >
            登录
          </el-button>
          <el-space
            direction="horizontal"
            style="justify-content: center; width: 100%; margin-top: 12px"
          >
            <el-text class="mx-1">没有账号，</el-text>
            <el-link type="primary" style="font-size: 14px">立即注册</el-link>
          </el-space>
        </el-form>
        <div class="third-login">
          <div class="title">其他登录方式</div>
          <div class="login-icon-container">
            <div class="login-icon-item">
              <a
                style="font-size: 22px; color: #888"
                title="企业微信"
                @click="onThirdLoginClick('wework')"
              >
                <svg
                  focusable="false"
                  width="1em"
                  height="1em"
                  fill="currentColor"
                  aria-hidden="true"
                >
                  <use xlink:href="#icon-qiyeweixin3">
                    <symbol id="icon-qiyeweixin3" viewBox="0 0 1024 1024">
                      <path
                        d="M210.823529 30.117647h602.352942a180.705882 180.705882 0 0 1 180.705882 180.705882v602.352942a180.705882 180.705882 0 0 1-180.705882 180.705882H210.823529a180.705882 180.705882 0 0 1-180.705882-180.705882v-602.352942a180.705882 180.705882 0 0 1 180.705882-180.705882z m452.246589 685.598118a10.541176 10.541176 0 0 0 1.325176 16.203294c23.250824 21.443765 38.249412 50.356706 42.465882 81.679059a44.935529 44.935529 0 0 0 55.115295 31.081411 44.212706 44.212706 0 0 0 31.442823-54.39247 44.694588 44.694588 0 0 0-38.550588-32.346353 138.24 138.24 0 0 1-76.619294-42.164706 10.842353 10.842353 0 0 0-15.179294 0zM425.622588 207.691294c-80.414118 8.734118-153.359059 42.706824-205.763764 95.774118-20.419765 20.359529-37.345882 43.911529-50.296471 69.692235a240.158118 240.158118 0 0 0 17.347765 245.278118c14.275765 21.202824 37.586824 47.706353 59.030588 66.56l-9.758118 75.053176-1.024 3.132235c-0.301176 0.963765-0.301176 2.048-0.421647 2.95153l-0.240941 2.409412 0.240941 2.409411a24.576 24.576 0 0 0 36.803765 18.672942h0.421647l1.445647-1.084236 23.130353-11.444706 68.909176-34.153411c32.768 9.276235 66.680471 13.854118 100.773647 13.613176 42.164706 0.120471 83.907765-7.047529 123.602824-21.202823a44.152471 44.152471 0 0 1-30.479059-46.260706c-41.020235 13.010824-84.329412 17.227294-127.09647 12.468706l-6.866824-0.90353a312.139294 312.139294 0 0 1-45.658353-9.396706 31.503059 31.503059 0 0 0-24.455529 2.469647l-1.867294 0.963765-56.741647 32.888471-2.409412 1.445647c-1.385412 0.783059-2.048 1.024-2.710588 1.024a3.855059 3.855059 0 0 1-3.614118-3.97553l2.16847-8.613647 2.529883-9.396706 4.035765-15.540706 4.698352-17.227294a23.311059 23.311059 0 0 0-8.613647-26.081882c-22.287059-16.263529-41.562353-36.141176-57.223529-58.849882a189.199059 189.199059 0 0 1-13.974588-193.355294c10.480941-20.540235 24.094118-39.333647 40.357647-55.657412 42.947765-43.791059 103.424-71.619765 170.285176-78.787765a322.379294 322.379294 0 0 1 69.632 0c66.499765 7.529412 126.674824 35.779765 169.381647 79.269647 16.263529 16.504471 29.696 35.358118 39.936 56.018824 13.131294 26.081882 19.998118 54.934588 20.058353 84.148706 0 3.011765-0.301176 6.083765-0.481882 9.035294a45.176471 45.176471 0 0 1 55.115294 6.384941l1.987765 2.409412a238.953412 238.953412 0 0 0-24.45553-125.711059 270.576941 270.576941 0 0 0-49.694117-69.752471 339.124706 339.124706 0 0 0-204.980706-96.37647 383.397647 383.397647 0 0 0-83.064471-0.301177z m439.898353 426.405647a44.935529 44.935529 0 0 0-20.178823 11.504941h-0.120471a43.851294 43.851294 0 0 0-12.890353 26.624 135.529412 135.529412 0 0 1-42.586353 75.715765 10.541176 10.541176 0 0 0-0.240941 15.058824 10.842353 10.842353 0 0 0 16.624941-1.566118c21.925647-23.009882 51.2-37.827765 82.763294-41.803294a44.212706 44.212706 0 0 0 31.62353-54.332235 44.935529 44.935529 0 0 0-54.994824-31.201883z m-168.779294-44.15247l-0.783059 0.783058c-21.985882 23.853176-51.802353 39.213176-83.968 43.369412a43.971765 43.971765 0 0 0-19.877647 73.968941 45.296941 45.296941 0 0 0 63.488-0.12047 44.092235 44.092235 0 0 0 12.769883-26.50353c5.601882-29.274353 20.600471-55.898353 42.767058-75.776a10.541176 10.541176 0 0 0 0.783059-14.938353 10.842353 10.842353 0 0 0-15.179294-0.783058z m41.743059-80.594824a43.851294 43.851294 0 0 0-19.576471 73.968941c7.228235 7.168 16.745412 11.625412 26.864941 12.649412 29.515294 5.421176 56.380235 20.118588 76.739765 42.044235a10.842353 10.842353 0 0 0 15.179294 0.180706 10.480941 10.480941 0 0 0-1.445647-16.323765 135.348706 135.348706 0 0 1-42.526117-81.679058 44.935529 44.935529 0 0 0-55.235765-30.780236z"
                      ></path>
                    </symbol>
                  </use>
                </svg>
              </a>
            </div>
            <div class="login-icon-item">
              <a
                style="font-size: 22px; color: #888"
                title="钉钉"
                @click="onThirdLoginClick('dingtalk')"
              >
                <svg
                  focusable="false"
                  width="1em"
                  height="1em"
                  fill="currentColor"
                  aria-hidden="true"
                  viewBox="64 64 896 896"
                >
                  <path
                    d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm227 385.3c-1 4.2-3.5 10.4-7 17.8h.1l-.4.7c-20.3 43.1-73.1 127.7-73.1 127.7s-.1-.2-.3-.5l-15.5 26.8h74.5L575.1 810l32.3-128h-58.6l20.4-84.7c-16.5 3.9-35.9 9.4-59 16.8 0 0-31.2 18.2-89.9-35 0 0-39.6-34.7-16.6-43.4 9.8-3.7 47.4-8.4 77-12.3 40-5.4 64.6-8.2 64.6-8.2S422 517 392.7 512.5c-29.3-4.6-66.4-53.1-74.3-95.8 0 0-12.2-23.4 26.3-12.3 38.5 11.1 197.9 43.2 197.9 43.2s-207.4-63.3-221.2-78.7c-13.8-15.4-40.6-84.2-37.1-126.5 0 0 1.5-10.5 12.4-7.7 0 0 153.3 69.7 258.1 107.9 104.8 37.9 195.9 57.3 184.2 106.7z"
                  ></path>
                </svg>
              </a>
            </div>
            <div class="login-icon-item">
              <a
                style="font-size: 22px; color: #aca9a9"
                title="飞书"
                @click="onThirdLoginClick('feishu')"
              >
                <svg
                  width="1em"
                  height="1em"
                  viewBox="0 0 24 24"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <rect width="24" height="24" rx="6" fill="currentColor" />
                  <path
                    d="M19.001 5.15894L15.069 9.40828C15.0554 9.42285 15.0454 9.44046 15.0399 9.45967C15.0345 9.47887 15.0337 9.49911 15.0376 9.51869C15.0713 9.68192 15.0511 9.85169 14.98 10.0024C14.9089 10.1532 14.7908 10.2768 14.6434 10.3546C14.496 10.4324 14.3273 10.4602 14.1627 10.4338C13.9981 10.4074 13.8465 10.3283 13.7308 10.2083L10.5973 13.3413L10.9066 17.4813L14.4247 21L19.001 5.15894Z"
                    fill="#007FFF"
                  />
                  <path
                    d="M18.9584 5.26562L15.068 9.47017C15.0544 9.48484 15.0444 9.50257 15.039 9.52187C15.0337 9.54118 15.033 9.56149 15.0371 9.58111C15.0728 9.74328 15.0543 9.9127 14.9844 10.0633C14.9144 10.2139 14.797 10.3374 14.6501 10.4148C14.5031 10.4922 14.3348 10.5192 14.1711 10.4916C14.0073 10.464 13.8571 10.3834 13.7436 10.2622L10.6634 13.3424C10.6573 13.3487 10.6525 13.3563 10.6495 13.3645C10.6465 13.3728 10.6452 13.3816 10.6458 13.3904L10.9499 17.4563L18.7941 5.62298L18.9621 5.26776L18.9579 5.26616L18.9584 5.26562Z"
                    fill="#0069FF"
                  />
                  <path
                    d="M18.8421 5L14.5922 8.93092C14.5777 8.94476 14.56 8.95489 14.5407 8.96046C14.5213 8.96603 14.501 8.96687 14.4813 8.96293C14.3181 8.92935 14.1484 8.94967 13.9978 9.02083C13.8471 9.09198 13.7237 9.21013 13.646 9.3575C13.5682 9.50487 13.5405 9.67348 13.5669 9.83799C13.5932 10.0025 13.6723 10.154 13.7922 10.2697L10.6586 13.4037L6.52023 13.0923L3 9.57523L18.8421 5Z"
                    fill="#00EED4"
                  />
                  <path
                    d="M18.7349 5.04693L14.5293 8.93678C14.5149 8.95028 14.4974 8.96017 14.4784 8.96564C14.4594 8.97111 14.4394 8.97201 14.42 8.96825C14.2578 8.93235 14.0883 8.95074 13.9375 9.0206C13.7868 9.09046 13.6632 9.20792 13.5857 9.3549C13.5082 9.50188 13.4812 9.67024 13.5088 9.83408C13.5364 9.99792 13.617 10.1482 13.7383 10.2617L10.6576 13.3413C10.6512 13.3476 10.6436 13.3524 10.6353 13.3554C10.6269 13.3584 10.618 13.3596 10.6091 13.3589L6.54321 13.0549L18.3771 5.21121L18.7323 5.04266L18.7349 5.04693Z"
                    fill="#00D3B4"
                  />
                  <defs>
                    <linearGradient
                      id="paint0_linear"
                      x1="14.25"
                      y1="24"
                      x2="14.25"
                      y2="3"
                      gradientUnits="userSpaceOnUse"
                    >
                      <stop stop-color="#FBFBFB" />
                      <stop offset="1" stop-color="white" />
                    </linearGradient>
                  </defs>
                </svg>
              </a>
            </div>
            <div class="login-icon-item">
              <a
                style="font-size: 22px; color: #aca9a9"
                title="微信"
                @click="onThirdLoginClick('wechat')"
              >
                <svg
                  focusable="false"
                  width="1em"
                  height="1em"
                  fill="currentColor"
                  aria-hidden="true"
                  viewBox="64 64 896 896"
                >
                  <path
                    d="M690.1 377.4c5.9 0 11.8.2 17.6.5-24.4-128.7-158.3-227.1-319.9-227.1C209 150.8 64 271.4 64 420.2c0 81.1 43.6 154.2 111.9 203.6a21.5 21.5 0 019.1 17.6c0 2.4-.5 4.6-1.1 6.9-5.5 20.3-14.2 52.8-14.6 54.3-.7 2.6-1.7 5.2-1.7 7.9 0 5.9 4.8 10.8 10.8 10.8 2.3 0 4.2-.9 6.2-2l70.9-40.9c5.3-3.1 11-5 17.2-5 3.2 0 6.4.5 9.5 1.4 33.1 9.5 68.8 14.8 105.7 14.8 6 0 11.9-.1 17.8-.4-7.1-21-10.9-43.1-10.9-66 0-135.8 132.2-245.8 295.3-245.8zm-194.3-86.5c23.8 0 43.2 19.3 43.2 43.1s-19.3 43.1-43.2 43.1c-23.8 0-43.2-19.3-43.2-43.1s19.4-43.1 43.2-43.1zm-215.9 86.2c-23.8 0-43.2-19.3-43.2-43.1s19.3-43.1 43.2-43.1 43.2 19.3 43.2 43.1-19.4 43.1-43.2 43.1zm586.8 415.6c56.9-41.2 93.2-102 93.2-169.7 0-124-120.8-224.5-269.9-224.5-149 0-269.9 100.5-269.9 224.5S540.9 847.5 690 847.5c30.8 0 60.6-4.4 88.1-12.3 2.6-.8 5.2-1.2 7.9-1.2 5.2 0 9.9 1.6 14.3 4.1l59.1 34c1.7 1 3.3 1.7 5.2 1.7a9 9 0 006.4-2.6 9 9 0 002.6-6.4c0-2.2-.9-4.4-1.4-6.6-.3-1.2-7.6-28.3-12.2-45.3-.5-1.9-.9-3.8-.9-5.7.1-5.9 3.1-11.2 7.6-14.5zM600.2 587.2c-19.9 0-36-16.1-36-35.9 0-19.8 16.1-35.9 36-35.9s36 16.1 36 35.9c0 19.8-16.2 35.9-36 35.9zm179.9 0c-19.9 0-36-16.1-36-35.9 0-19.8 16.1-35.9 36-35.9s36 16.1 36 35.9a36.08 36.08 0 01-36 35.9z"
                  ></path>
                </svg>
              </a>
            </div>
          </div>
        </div>
      </div>
      <div class="login-input" v-else>
        <div class="login-back-local">
          <el-icon
            title="返回账号密码登录"
            style="font-size: 18px"
            @click="onThirdLoginClick('local')"
          >
            <Back />
          </el-icon>
          <span class="title">{{ thirdLoginTitle }}登录</span>
          <component
            :is="thirdLoginComponent"
            @success="handleOAuthSuccess"
            @error="handleOAuthError"
          ></component>
        </div>
      </div>
    </div>
    <div
      style="
        position: absolute;
        right: 0;
        bottom: 0;
        left: 0;
        height: 44px;
        font-size: 14px;
        color: #666;
        background-color: #f7fafe;
      "
    >
      <p style="text-align: center; line-height: 44px">
        Copyright © 2024 知视科技 津 ICP 备 20006136 号-1 津公网安备 12011602000454 号
      </p>
    </div>
  </div>
</template>

<script lang="ts">
export default {
  name: 'Login',
};
</script>

<script setup lang="ts">
import type { FormInstance } from 'element-plus';
import { Router, useRouter } from 'vue-router';
import bkImg from '@/assets/img/login_icon2.png';
import { encrypt, setToken, treeDataTranslate } from '@/common/utils/index';
import { loginParam } from '@/types/upms/login';
import { useLoginStore, useLayoutStore } from '@/store';
import LoginController from '@/api/system/LoginController';
import OAuthController from '@/api/system/OAuthController';
import FormulaEditor from '@/components/FormulaEditor/index.vue';
import DinkTalkLogin from '@/components/DingTalk/login.vue';
import FeishuLogin from '@/components/Feishu/login.vue';
import WeWorkLogin from '@/components/WeWork/login.vue';
import WeChatLogin from '@/components/WeChat/login.vue';

const loginForm = ref<FormInstance>();

const loginStore = useLoginStore();
const layoutStore = useLayoutStore();
const router: Router = useRouter();

const dataForm = reactive({
  mobilePhone: 'admin',
  password: '123456',
});

const dataRule = {
  mobilePhone: [{ required: true, message: '帐号不能为空', trigger: 'blur' }],
  password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
};

const thirdLogin = ref({
  // 第三方登录类型, local-本地，dingtalk-钉钉 feishu-飞书 wework-企业微信 wechat-微信（关注公众号）
  loginType: 'local',
  // oauth授权成功，但是未找到绑定的账号时，生成该token
  thirdLoginToken: null,
});

const thirdLoginComponent = computed(() => {
  switch (thirdLogin.value.loginType) {
    case 'dingtalk':
      return DinkTalkLogin;
    case 'feishu':
      return FeishuLogin;
    case 'wework':
      return WeWorkLogin;
    case 'wechat':
      return WeChatLogin;
    default:
      return 'div';
  }
});

const thirdLoginTitle = computed(() => {
  switch (thirdLogin.value.loginType) {
    case 'dingtalk':
      return '钉钉';
    case 'feishu':
      return '飞书';
    case 'wework':
      return '企业微信';
    case 'wechat':
      return '微信';
    default:
      return '';
  }
});

const onThirdLoginClick = (type: string) => {
  thirdLogin.value.loginType = type;
};

const handleOAuthSuccess = data => {
  console.log('第三方登录成功', data);
  layoutStore.setMenuList(treeDataTranslate(data.data.menuList, 'menuId', 'parentId'));
  delete data.data.menuList;

  loginStore.setUserInfo(data.data);
  setToken(data.data.tokenData);
  layoutStore.setCurrentMenu(null);
  router.replace({ name: 'main' });
};

const handleOAuthError = err => {
  console.error('第三方登录失败', err);
  if (err && err.errorCode === 'DATA_NOT_EXIST') {
    thirdLogin.value.thirdLoginToken = err.data.token;
    onThirdLoginClick('local');
  }
};

const loginAndBind = params => {
  params.token = thirdLogin.value.thirdLoginToken;
  thirdLogin.value.thirdLoginToken = null;
  OAuthController.doLoginAndBind(params, { showMask: false })
    .then(data => {
      layoutStore.setMenuList(treeDataTranslate(data.data.menuList, 'menuId', 'parentId'));
      delete data.data.menuList;

      loginStore.setUserInfo(data.data);
      setToken(data.data.tokenData);
      layoutStore.setCurrentMenu(null);
      router.replace({ name: 'main' });
    })
    .catch(e => {
      console.error(e);
    });
};

const dataFormSubmit = () => {
  loginForm.value?.validate((valid: boolean) => {
    if (valid) {
      login();
    }
  });
};

const login = function (verifyParams: { captchaVerification: string } | null = null) {
  let params: loginParam = {
    loginName: dataForm.mobilePhone,
    password: encrypt(dataForm.password),
    captchaVerification: verifyParams?.captchaVerification,
  };
  setToken(null);
  LoginController.login(params)
    .then(data => {
      //console.log('login >>>', data);
      if (data.data.menuList) {
        layoutStore.setMenuList(treeDataTranslate(data.data.menuList, 'menuId', 'parentId'));
        delete data.data.menuList;
      }
      setToken(data.data.tokenData);
      layoutStore.setCurrentMenu(null);
      layoutStore.clearAllTags();
      if (data.data.tokenData) {
        delete data.data.tokenData;
      }
      loginStore.setUserInfo(data.data);
      // 登录成功跳转页面
      router.replace({ name: 'main' });
    })
    .catch(e => {
      console.error(e);
    });
};
</script>

<style lang="scss">
.login-form {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  width: 100vw;
  height: 100vh;
  background: url('@/assets/img/login_bg2.png') center center;
  background-size: cover;
  .logo-box {
    position: absolute;
    top: 6.66vh;
    right: 5.5vw;
    left: 5.5vw;
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 28px;
    color: #333;
    font-weight: bold;
    img {
      margin-right: 11px;
    }
  }
  .login-box {
    display: flex;
    align-items: center;
    height: 499px;
    border-radius: 3px;
    .login-input {
      display: flex;
      width: 448px;
      padding: 46px 40px;
      background-color: #fff;
      border-radius: 16px;
      box-shadow: 0 2px 20px 1px rgb(79 79 79 / 10%);
      flex-direction: column;
      .title {
        margin-bottom: 5px;
        font-size: 32px;
        color: #333;
        font-weight: bold;
      }
      .desc {
        margin-bottom: 24px;
        font-size: 18px;
        color: #666;
      }
      .third-login {
        text-align: center;
        .title {
          color: #ddcbcb;
          font-size: 12px;
          font-weight: normal;
          margin-top: 25px;
          margin-bottom: 25px;
        }
        .login-icon-container {
          display: flex;
          align-items: center;
          .login-icon-item {
            flex: 1;
            a {
              cursor: pointer;
            }
          }
        }
      }
      .login-back-local {
        i {
          cursor: pointer;
        }
        .title {
          margin-left: 10px;
        }
      }
    }
    .img-box {
      position: relative;
      height: 100%;
      margin-right: 109px;
      .img-title {
        position: absolute;
        top: -80px;
        right: 0;
        left: 0;
        display: none;
        width: 375px;
        margin: 0 auto;
      }
    }
  }
  .el-form-item__label {
    color: #333;
  }
}

.login-form .el-input__inner {
  height: 40px !important;
}

.logo-right {
  a,
  span {
    font-size: 16px;
    font-weight: normal;
    color: #666;
  }
}
</style>
