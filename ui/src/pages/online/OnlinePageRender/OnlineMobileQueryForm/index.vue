<template>
  <div class="online-query-form" :style="{ height: height ? height : '100%' }" v-show="isReady">
    <van-form
      ref="form"
      class="full-width-input"
      style="width: 100%; height: 100%"
      :label-width="(form2.labelWidth || 100) + 'px'"
      @submit.prevent
    >
      <OnlineCustomQueryList
        :widget="queryTable"
        :isEdit="isEdit"
        :isTree="props.formConfig.advanceQuery"
        :leftWidget="leftWidget"
        @widgetClick="onWidgetClick"
      />
    </van-form>
  </div>
</template>

<script setup lang="ts">
import { Form as VanForm } from 'vant';
import { ANY_OBJECT } from '@/types/generic';
import OnlineCustomQueryList from '@/online/components/OnlineCustomQueryList.vue';
import { OnlineFormEventType } from '@/common/staticDict';
import widgetData from '@/online/config/index';
import { post, get } from '@/common/http/request';
import { useLoginStore } from '@/store';
import { useDict } from '../../hooks/useDict';
import { useForm } from '../hooks/useForm';
import { useFormExpose } from '../hooks/useFormExpose';

const loginStore = useLoginStore();
const emit = defineEmits<{
  widgetClick: [ANY_OBJECT | null];
  listClick: [ANY_OBJECT | null];
}>();

const props = withDefaults(
  defineProps<{
    formConfig: ANY_OBJECT;
    height?: string;
    // 是否表单编辑模式
    isEdit?: boolean;
    readOnly?: boolean;
    // 当前选中组件
    currentWidget?: ANY_OBJECT | null;
    // 是否全屏弹窗
    fullscreen?: boolean;
    mode: string;
  }>(),
  {
    isEdit: false,
    readOnly: false,
    fullscreen: false,
    mode: 'mobile',
  },
);

const { getDictDataList } = useDict();
const {
  isReady,
  dialogParams,
  form: form2,
  formData,
  getWidgetValue,
  getWidgetProp,
  getWidgetVisible,
  onValueChange,
  onWidgetValueChange,
  getDropdownParams,
  checkOperationPermCode,
  checkOperationDisabled,
  checkOperationVisible,
  cloneWidget,
  handlerOperation,
  loadOnlineFormConfig,
  initPage,
  initFormWidgetList,
  initWidgetLinkage,
} = useForm(props);

provide('form', () => {
  return {
    ...form2.value,
    mode: props.mode || 'pc',
    isEdit: dialogParams.value.isEdit,
    readOnly: dialogParams.value.readOnly,
    formData: formData,
    getWidgetValue: getWidgetValue,
    getWidgetProp: getWidgetProp,
    getWidgetVisible: getWidgetVisible,
    onValueChange: onValueChange,
    onWidgetValueChange: onWidgetValueChange,
    getDropdownParams: getDropdownParams,
    checkOperationPermCode: checkOperationPermCode,
    checkOperationDisabled: checkOperationDisabled,
    checkOperationVisible: checkOperationVisible,
    cloneWidget: cloneWidget,
    handlerOperation: handlerOperation,
    getDictDataList: getDictDataList,
    loadOnlineFormConfig: loadOnlineFormConfig,
    isActive: (widget: ANY_OBJECT) => {
      return props.currentWidget === widget;
    },
    getWidgetObject: widgetData.getWidgetObject,
    instanceData: () => useFormExpose(formData, props),
  };
});

const queryTable = computed<ANY_OBJECT>(() => {
  return form2.value.tableWidget;
});
const leftWidget = computed<ANY_OBJECT>(() => {
  return form2.value.leftWidget;
});

const onWidgetClick = (widget: ANY_OBJECT) => {
  emit('widgetClick', widget);
};

onMounted(() => {
  isReady.value = false;
  if (!dialogParams.value.isEdit) {
    if (
      form2.value.eventInfo &&
      typeof form2.value.eventInfo[OnlineFormEventType.AFTER_CREATE_FORM] === 'function'
    ) {
      form2.value.eventInfo[OnlineFormEventType.AFTER_CREATE_FORM](useFormExpose(formData, props));
    }
    initWidgetLinkage();
  }
  isReady.value = true;
});
</script>
