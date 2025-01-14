<template>
  <el-row style="width: 100%">
    <el-form
      style="width: 100%"
      :label-position="labelPosition"
      :label-width="labelWidth"
      :size="layoutStore.defaultFormItemSize"
      @submit.prevent
    >
      <MultiItemList
        v-if="labelPosition === 'top'"
        :label="label"
        v-model:data="data"
        addText="添加"
        @add="onEditEvent(null)"
        @edit="onEditEvent"
        @delete="onRemoveEvent"
        :prop="{
          label: 'showName',
          value: 'eventType',
        }"
      />
      <el-form-item v-else :label="label">
        <MultiItemBox
          v-model:data="data"
          addText="添加"
          @add="onEditEvent(null)"
          @edit="onEditEvent"
          @delete="onRemoveEvent"
          :prop="{
            label: 'showName',
            value: 'eventType',
          }"
        />
      </el-form-item>
    </el-form>
  </el-row>
</template>

<script setup lang="ts">
import { ElMessageBox } from 'element-plus';
import { EpPropMergeType } from 'element-plus/es/utils';
import MultiItemList from '@/components/MultiItemList/index.vue';
import MultiItemBox from '@/components/MultiItemBox/index.vue';
import { ANY_OBJECT } from '@/types/generic';
import { Dialog } from '@/components/Dialog';
import { OnlineFormEventType } from '@/common/staticDict';
import { useLayoutStore } from '@/store';
import EditCustomEvent from './editCustomEvent.vue';

const emit = defineEmits<{ 'update:value': [ANY_OBJECT] }>();

const props = withDefaults(
  defineProps<{
    value: Array<ANY_OBJECT>;
    allowEventList?: Array<string>;
    label?: string;
    labelPosition?:
      | EpPropMergeType<StringConstructor, 'top' | 'right' | 'left', unknown>
      | undefined;
    labelWidth?: string;
    formConfig: ANY_OBJECT;
    mode: string;
  }>(),
  {
    label: '表单脚本',
    labelPosition: 'top',
    labelWidth: '110px',
    allowEventList: () => [],
    mode: 'pc',
  },
);

const data = computed({
  get() {
    return props.value;
  },
  set(val) {
    emit('update:value', val);
  },
});

const layoutStore = useLayoutStore();

const eventList = computed(() => {
  let usedList = (props.value || []).map(eventItem => eventItem.eventType);
  return props.allowEventList.map(item => {
    return {
      id: item,
      name: OnlineFormEventType.getValue(item),
      disabled: usedList.indexOf(item) !== -1,
    };
  });
});

const handlerEditOperate = (row: ANY_OBJECT | null, res: ANY_OBJECT) => {
  let temp = [];
  if (row == null) {
    temp = [...props.value, res];
  } else {
    temp = (props.value || []).map(item => {
      return row.eventType === item.eventType ? res : item;
    });
  }
  emit('update:value', temp);
};
const onEditEvent = (row: ANY_OBJECT | null) => {
  Dialog.show<ANY_OBJECT>(
    row ? '编辑脚本' : '添加脚本',
    EditCustomEvent,
    {
      area: ['1200px', '792px'],
    },
    {
      rowData: row,
      eventList: eventList.value,
      formConfig: props.formConfig,
      mode: props.mode,
      //path: 'thirdEditCustomEvent',
    },
    {
      width: '1200px',
      height: '800px',
      pathName: '/thirdParty/thirdEditCustomEvent',
    },
  )
    .then(res => {
      handlerEditOperate(row, res);
    })
    .catch(e => {
      console.log(e);
    });
};
const onRemoveEvent = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否移除此脚本？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    emit(
      'update:value',
      (props.value || []).filter(item => {
        return item.eventType !== row.eventType;
      }),
    );
  });
};
</script>
