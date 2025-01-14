<template>
  <div>
    <div id="login_qrcode_container">
      <img class="wwLogin_qrcode_img" :src="qrCodeUrl" />
      <div class="wwLogin_qrcode_tips" v-if="expired">
        <div class="wwLogin_qrcode_tips_cnt">
          <img
            src="https://wwcdn.weixin.qq.com/node/wework/images/IconWarn.ebf07b08cd.svg"
            class="wwLogin_qrcode_tips_icon"
          />
          <h4>二维码已过期</h4>
          <a href="javascript:;" @click="refreshQrCode">刷新</a>
        </div>
      </div>
      <div class="wwLogin_qrcode_tips" v-if="scaned">
        <div class="wwLogin_qrcode_tips_cnt">
          <img
            src="https://wwcdn.weixin.qq.com/node/wework/images/IconSuc.75ddbc3336.svg"
            class="wwLogin_qrcode_tips_icon"
          />
          <h4>扫码成功</h4>
          <p>正在登录...</p>
        </div>
      </div>
    </div>
    <div class="login_qrcode_text">请使用微信扫描二维码登录</div>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { OAuthController } from '@/api/system/index';
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';

defineOptions({
  name: 'WeChatLogin',
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

const expired = ref(false);
const scaned = ref(false);
const qrCodeUrl = ref<string | null>(null);
const ticket = ref<string | null>(null);
const timer = ref<number | null>(null);

const refreshQrCode = () => {
  stopCheckStatu();
  OAuthController.getWechatQrCode().then(res => {
    if (res.success) {
      qrCodeUrl.value = res.data.qrCodeUrl;
      ticket.value = res.data.ticket;
      expired.value = false;
      setTimeout(() => {
        expired.value = true;
        stopCheckStatu();
      }, (res.data.expireSeconds - 2 || 58) * 1000);
      checkStatus();
    }
  });
};

const checkStatus = () => {
  if (ticket.value) {
    timer.value = setInterval(() => {
      OAuthController.checkWechatStatus(
        { ticket: ticket.value },
        {
          showMask: false,
        },
      ).then(res => {
        scaned.value = res.data;
        if (scaned.value) {
          doLogin();
          stopCheckStatu();
        }
      });
    }, 5000);
  }
};

const stopCheckStatu = () => {
  if (timer.value) {
    clearInterval(timer.value);
    ticket.value = null;
    console.log('stopCheckStatu');
  }
};

const doLogin = () => {
  if (props.bindSocial) {
    OAuthController.bind({ source: 'wechat', code: ticket.value })
      .then(res => {
        console.log('微信绑定成功', res);
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
    OAuthController.login({ source: 'wechat', code: ticket.value })
      .then(res => {
        console.log('微信登录成功', res);
        if (props.dialog) {
          props.dialog.submit(res);
        }
        emit('success', res);
      })
      .catch(e => {
        console.warn('微信登录失败');
        emit('error', e);
      });
  }
};

onMounted(() => {
  refreshQrCode();
});

onUnmounted(() => {
  stopCheckStatu();
});
</script>

<style lang="scss" scoped>
#login_qrcode_container {
  height: 280px;
  width: 100%;
  position: relative;

  .wwLogin_qrcode_img {
    width: 215px;
    height: 215px;
    position: absolute;
    right: 0;
    left: 0;
    top: 0;
    bottom: 0;
    margin: auto;
    padding: 0;
    border: 1px solid rgba(6, 15, 26, 0.07);
    border-radius: 8px;
  }

  .wwLogin_qrcode_tips {
    align-items: center;
    bottom: 0;
    display: flex;
    justify-content: center;
    left: 0;
    position: absolute;
    right: 0;
    top: 0;
    background: rgba(255, 255, 255, 0.9);

    .wwLogin_qrcode_tips_cnt {
      align-items: center;
      display: flex;
      flex-direction: column;
      font-size: 13px;
      font-weight: 400;
      justify-content: center;
      line-height: 18px;
      z-index: 1;

      .wwLogin_qrcode_tips_icon {
        display: block;
        margin: 0 auto;
        z-index: 1;
      }
      .wwLogin_qrcode_tips_cnt h4 {
        color: #10141a;
        font-size: 14px;
        line-height: 20px;
        margin: 12px auto;
      }

      .wwLogin_qrcode_tips_cnt a {
        color: #267ef0;
        display: block;
        text-align: center;
      }
    }
  }
}
.login_qrcode_text {
  text-align: center;
  color: #898d90;
  font-size: 14px;
}
</style>
