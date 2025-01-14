<template>
  <div id="login_qrcode_container" :class="weComLoginClass"></div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { OAuthController } from '@/api/system/index';
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';

defineOptions({
  name: 'FeishuLogin',
});

const props = withDefaults(
  defineProps<{
    bindSocial: boolean;
    // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
    dialog?: DialogProp<ANY_OBJECT | ANY_OBJECT[] | undefined>;
  }>(),
  {
    bindSocial: false,
  },
);

const emit = defineEmits(['success', 'error']);
const isWeComLogin = ref(false);
const weComLoginClass = computed(() => {
  return isWeComLogin.value ? 'we_com_login' : '';
});

onMounted(() => {
  // 初始化登录组件
  const wwLogin = window.ww.createWWLoginPanel({
    el: '#login_qrcode_container',
    params: {
      login_type: 'CorpApp',
      appid: process.env.VUE_APP_WEWORK_CORP_ID,
      agentid: process.env.VUE_APP_WEWORK_AGENT_ID,
      redirect_uri: process.env.VUE_APP_WEWORK_REDIRECT_URI,
      state: 'loginState',
      redirect_type: 'callback',
    },
    onCheckWeComLogin: res => {
      console.log('企业微信客户端在线', res);
      isWeComLogin.value = res.isWeComLogin;
    },
    onLoginSuccess: res => {
      console.log('企业微信登录成功', res);
      if (props.bindSocial) {
        OAuthController.bind({ source: 'wework', code: res.code })
          .then(res => {
            console.log('企业微信绑定成功', res);
            if (props.dialog) {
              ElMessage.success('绑定成功');
              props.dialog.submit(res);
            }
            emit('success', res);
          })
          .catch(e => {
            emit('error', e);
          });
      } else {
        OAuthController.login({ source: 'wework', code: res.code })
          .then(res => {
            console.log('企业微信登录成功', res);
            if (props.dialog) {
              props.dialog.submit(res);
            }
            emit('success', res);
          })
          .catch(e => {
            console.warn('企业微信登录失败', e);
            emit('error', e);
          });
      }
    },
    onLoginFail: err => {
      console.log('企业微信登录失败', err);
      emit('error', err);
    },
    onOpenInWecom(res) {
      console.log('在企业微信中打开了', res);
    },
  });

  if (wwLogin) {
    wwLogin.el.style.width = '100%';
    wwLogin.el.style.height = '380px';
  }
});
</script>

<style lang="scss" scoped>
#login_qrcode_container {
  display: block;
  overflow: hidden;

  ::v-deep iframe {
    margin-top: -65px;
  }
}
.we_com_login {
  ::v-deep iframe {
    margin-top: auto !important;
  }
}
</style>
