<template>
  <div class="login-form">
    <div class="logo-box logo-right">
      <span style="display: none"></span>
      <el-space direction="horizontal">
      </el-space>
    </div>
    <div class="login-box">
      <div class="img-box">
<!--        <img :src="bkImg" style="flex-shrink: 0; height: 100%" />-->
      </div>
      <div class="login-input" v-if="thirdLogin.loginType === 'local'">
<!--        <img src="@/assets/img/orange.png" style="width: 62px; margin-bottom: 9px" alt="" />-->
        <span class="title">Welcome to Login</span>
        <span class="desc">TIS System</span>
        <el-form
          :model="dataForm"
          :rules="dataRule"
          label-position="top"
          ref="loginForm"
          @keyup.enter.stop="() => {}"
        >
          <el-col :span="24">
            <el-form-item prop="mobilePhone" label="" style="margin-top: 48px; margin-bottom: 25px">
              <el-input v-model="dataForm.mobilePhone" style="width: 100%" placeholder="Enter your account">
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
                placeholder="Enter your password"
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
            Login
          </el-button>
          <el-space
            direction="horizontal"
            style="justify-content: center; width: 100%; margin-top: 12px"
          >
          </el-space>
        </el-form>
      </div>
      <div class="login-input" v-else>
        <div class="login-back-local">
          <el-icon
            title="Back to account password login"
            style="font-size: 18px"
            @click="onThirdLoginClick('local')"
          >
            <Back />
          </el-icon>
          <span class="title">{{ thirdLoginTitle }} Login</span>
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
  mobilePhone: '',
  password: '',
});

const dataRule = {
  mobilePhone: [{ required: true, message: 'Account cannot be empty', trigger: 'blur' }],
  password: [{ required: true, message: 'Password cannot be empty', trigger: 'blur' }],
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
      return 'DingTalk';
    case 'feishu':
      return 'Feishu';
    case 'wework':
      return 'WeWork';
    case 'wechat':
      return 'WeChat';
    default:
      return '';
  }
});

const onThirdLoginClick = (type: string) => {
  thirdLogin.value.loginType = type;
};

const handleOAuthSuccess = data => {
  console.log('Third-party login success', data);
  layoutStore.setMenuList(treeDataTranslate(data.data.menuList, 'menuId', 'parentId'));
  delete data.data.menuList;

  loginStore.setUserInfo(data.data);
  setToken(data.data.tokenData);
  layoutStore.setCurrentMenu(null);
  router.replace({ name: 'main' });
};

const handleOAuthError = err => {
  console.error('Third-party login failed', err);
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