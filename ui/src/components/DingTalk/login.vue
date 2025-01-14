<template>
  <div class="dingtalk-login">
    <div id="login_qrcode_container"></div>
    <div class="login_qrcode_text">请使用钉钉扫描二维码登录</div>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { OAuthController } from '@/api/system/index';
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';

defineOptions({
  name: 'DingTalkLogin',
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

const ddInstance = ref();
const emit = defineEmits(['success', 'error']);

const r = (e, t) => {
  const r = e.match(new RegExp('[?&]' + t + '=([^&]+)'));
  return r ? r[1] : null;
};

const DTFrameLogin = (e, t, n, o) => {
  const i = t;
  const u = (e.id && document.getElementById(e.id)) || null;
  const c = document.createElement('iframe');

  if (t.client_id && t.redirect_uri && t.response_type && t.scope) {
    if (u) {
      u.innerHTML = '';
      u.appendChild(c);
      if (c && c.contentWindow && c.contentWindow.postMessage && window.addEventListener) {
        c.src =
          'https://' +
          (i.isPre ? 'pre-login' : 'login') +
          '.dingtalk.com/oauth2/auth?iframe=true&redirect_uri=' +
          i.redirect_uri +
          '&response_type=' +
          i.response_type +
          '&client_id=' +
          i.client_id +
          '&scope=' +
          i.scope +
          (i.prompt ? '&prompt=' + i.prompt : '') +
          (i.state ? '&state=' + i.state : '') +
          (i.org_type ? '&org_type=' + i.org_type : '') +
          (i.corpId ? '&corpId=' + i.corpId : '') +
          (i.exclusiveLogin ? '&exclusiveLogin=' + i.exclusiveLogin : '') +
          (i.exclusiveCorpId ? '&exclusiveCorpId=' + i.exclusiveCorpId : '');
        c.width = '' + (e.width || 300);
        c.height = '' + (e.height || 300);
        c.frameBorder = '0';
        c.scrolling = 'no';

        const handleMessage = event => {
          const t = event.data;
          const i = event.origin;
          if (/login\.dingtalk\.com/.test(i) && t) {
            console.log('钉钉回调', event);
            if (t.success && t.redirectUrl) {
              const u = t.redirectUrl;
              const c = r(u, 'authCode') || '';
              const d = r(u, 'state') || '';
              const s = r(u, 'error') || '';
              console.log(u, c, d, s);
              c ? n && n({ redirectUrl: u, authCode: c, state: d }) : o && o(s);
            } else {
              o && o(t.errorMsg);
            }
          }
        };

        console.log('绑定钉钉消息监听');
        window.addEventListener('message', handleMessage, false);
        return {
          destroy: () => {
            window.removeEventListener('message', handleMessage, false);
          },
        };
      } else {
        o && o('Browser not support');
      }
    } else {
      o && o('Element not found');
    }
  } else {
    o && o('Missing parameters');
  }
};

onMounted(() => {
  const redirectUri = encodeURIComponent(process.env.VUE_APP_DINGTALK_REDIRECT_URI);
  ddInstance.value = DTFrameLogin(
    {
      id: 'login_qrcode_container',
      width: 350,
      height: 280,
    },
    {
      redirect_uri: redirectUri,
      client_id: process.env.VUE_APP_DINGTALK_CLIENT_ID,
      scope: 'openid',
      response_type: 'code',
      state: '123456', // 该值可由后台获取
      prompt: 'consent',
    },
    loginResult => {
      const { redirectUrl, authCode, state } = loginResult;
      // redirectUrl中有code和authCode，值好像是一样的，若不同，须重新组装链接，将authCode赋值给code参数，使后台可以使用统一的参数code来处理鉴权逻辑
      if (props.bindSocial) {
        OAuthController.bind({ source: 'dingtalk', code: authCode, state })
          .then(res => {
            console.log('钉钉绑定成功', res);
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
        OAuthController.login({ source: 'dingtalk', code: authCode, state })
          .then(res => {
            console.log('钉钉登录成功', res);
            if (props.dialog) {
              props.dialog.submit(res);
            }
            emit('success', res);
          })
          .catch(e => {
            console.warn('钉钉登录失败');
            emit('error', e);
          });
      }
    },
    errorMsg => {
      // 这里一般需要展示登录失败的具体原因,可以使用toast等轻提示
      ElMessage.error(errorMsg);
    },
  );
});

onUnmounted(() => {
  if (ddInstance.value) ddInstance.value.destroy();
  ddInstance.value = null;
});
</script>

<style scoped>
.login_qrcode_text {
  text-align: center;
  color: #898d90;
  font-size: 14px;
}
</style>
