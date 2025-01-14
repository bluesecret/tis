<template>
  <div class="feishu-login">
    <div id="login_qrcode_container"></div>
    <div class="login_qrcode_text">请使用飞书扫描二维码登录</div>
  </div>
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

const fsInstance = ref();
const emit = defineEmits(['success', 'error']);

onMounted(() => {
  // STATE可以根据需要替换为自己的值，并在后台做校验
  const goto =
    'https://passport.feishu.cn/suite/passport/oauth/authorize?client_id=' +
    process.env.VUE_APP_FEISHU_CLIENT_ID +
    '&redirect_uri=' +
    process.env.VUE_APP_FEISHU_REDIRECT_URI +
    '&response_type=code&state=STATE';

  const QRLoginObj = window.QRLogin({
    id: 'login_qrcode_container',
    goto: goto,
    width: '350',
    height: '280',
    // 可选的，二维码html标签的style属性
    style: 'border:none;',
  });

  const handleMessage = event => {
    // 使用 matchOrigin 和 matchData 方法来判断 message 和来自的页面 url 是否合法
    if (QRLoginObj.matchOrigin(event.origin) && QRLoginObj.matchData(event.data)) {
      console.log('飞书登录回调', event);
      OAuthController.redirect({ url: `${goto}&tmp_code=${event.data.tmp_code}` })
        .then(res => {
          console.log('飞书CODE', res);
          if (res) {
            if (props.bindSocial) {
              OAuthController.bind({ source: 'feishu', code: res.data })
                .then(r => {
                  console.log('飞书绑定成功', r);
                  if (props.dialog) {
                    ElMessage.success('绑定成功');
                    props.dialog.submit(r);
                  }
                  emit('success', r);
                })
                .catch(e => {
                  emit('error', e);
                });
            } else {
              OAuthController.login({ source: 'feishu', code: res.data })
                .then(r => {
                  console.log('飞书登录成功', r);
                  if (props.dialog) {
                    props.dialog.submit(r);
                  }
                  emit('success', r);
                })
                .catch(e => {
                  console.warn('飞书登录失败');
                  emit('error', e);
                });
            }
          }
        })
        .catch(e => {
          console.warn('飞书登录失败', e);
          emit('error', e);
        });
    }
  };
  window.addEventListener('message', handleMessage);
  fsInstance.value = {
    destroy: () => {
      window.removeEventListener('message', handleMessage);
    },
  };
});

onUnmounted(() => {
  if (fsInstance.value) fsInstance.value.destroy();
  fsInstance.value = null;
});
</script>

<style scoped>
.login_qrcode_text {
  text-align: center;
  color: #898d90;
  font-size: 14px;
}
</style>
