<template>
  <div class="online-query-form" :style="{ height: height ? height : '100%' }" v-show="isReady">
    <van-form
      ref="form"
      class="full-width-input"
      style="width: 100%; height: 100%"
      :label-width="(form2.labelWidth || 100) + 'px'"
      @submit.prevent
    >
      <OnlineMobileWorkOrderList
        :widget="queryTable"
        :isEdit="isEdit"
        @widgetClick="onWidgetClick"
      />
    </van-form>
  </div>
</template>

<script setup lang="ts">
import { Form as VanForm } from 'vant';
import { ANY_OBJECT } from '@/types/generic';
import { OnlineFormEventType } from '@/common/staticDict';
import OnlineMobileWorkOrderList from '@/online/components/OnlineMobileWorkOrderList.vue';
import widgetData from '@/online/config/index';
import { post, get } from '@/common/http/request';
import { useLoginStore } from '@/store';
import { useDict } from '../../hooks/useDict';
import { useForm } from '../hooks/useForm';
import { useFormExpose } from '../hooks/useFormExpose';

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
  form: form2,
  formData,
  getWidgetValue,
  getWidgetVisible,
  onValueChange,
  onWidgetValueChange,
  getWidgetProp,
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
    isEdit: props.isEdit,
    readOnly: props.readOnly,
    formData: formData,
    getWidgetValue: getWidgetValue,
    getWidgetVisible: getWidgetVisible,
    getWidgetProp: getWidgetProp,
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

const queryTable = computed(() => {
  return form2.value.tableWidget;
});

const onWidgetClick = (widget: ANY_OBJECT | null) => {
  console.log('OnlineMobileWorkOrderForm onWidgetClick', widget);
  emit('widgetClick', widget);
};

onMounted(() => {
  isReady.value = false;
  if (!props.isEdit) {
    if (
      form2.value.eventInfo &&
      typeof form2.value.eventInfo[OnlineFormEventType.AFTER_CREATE_FORM] === 'function'
    ) {
      form2.value.eventInfo[OnlineFormEventType.AFTER_CREATE_FORM](useFormExpose(formData, props));
    }
    //initFormData();
    initWidgetLinkage();
  }
  isReady.value = true;
});
</script>
