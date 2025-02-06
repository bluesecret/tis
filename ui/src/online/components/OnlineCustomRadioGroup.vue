<template>
  <div class="online-custom-radio-group">
    <el-tabs v-if="(props.dataList || []).length > 0" v-model="activeFragmentId">
      <el-tab-pane v-if="supportAll" name="temp_all" label="全部" style="width: 100%" />
      <el-tab-pane
        v-for="item in props.dataList"
        :key="item.id"
        :name="item.id"
        :label="item.name + ' (' + getGroupCount(item.id) + ')'"
        style="width: 100%"
      />
    </el-tabs>
    <div v-else class="empty-box">
      <el-icon
        style="
          text-align: center;
          height: 40px;
          width: 40px;
          line-height: 40px;
          border: 1px dashed #d9dbdd;
          font-size: 24px;
          margin-right: 8px;
          color: #d9dbdd;
          border-radius: 6px;
        "
      >
        <UploadFilled />
      </el-icon>
      <span>请点击进行编辑</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { UploadFilled } from '@element-plus/icons-vue';
import { ANY_OBJECT } from '@/types/generic';

defineOptions({
  name: 'OnlineCustomRadioGroup',
});

const emit = defineEmits(['update:modelValue', 'change']);

const props = withDefaults(
  defineProps<{
    modelValue: string | number;
    widget: ANY_OBJECT;
    supportAll: boolean;
    dataList: ANY_OBJECT[];
  }>(),
  {
    supportAll: true,
    dataList: () => [],
  },
);

const form = inject('form', () => {
  return { isEdit: false } as ANY_OBJECT;
});

const activeFragmentId = ref('temp_all');
const groupCountData = ref({});

const setGroupData = (value: ANY_OBJECT) => {
  groupCountData.value = value;
};

const getGroupCount = (key: string) => {
  return (groupCountData.value || {})[key] || 0;
};

watch(
  () => props.modelValue,
  val => {
    activeFragmentId.value = val == null || val === '' ? 'temp_all' : val;
  },
  { immediate: true },
);

watch(
  () => props.dataList,
  val => {
    if (val && val.length > 0) {
      const tempData = val.find(item => item.id === activeFragmentId.value);
      if (tempData == null) {
        activeFragmentId.value = props.supportAll ? 'temp_all' : val[0].id;
      }
    } else {
      activeFragmentId.value = 'temp_all';
    }
  },
  { immediate: true },
);

watch(
  () => activeFragmentId.value,
  val => {
    const dictData = props.dataList.find(item => item.id === val);
    emit('update:modelValue', (dictData || {}).id);
    emit('change', (dictData || {}).id, dictData);
  },
);

defineExpose({
  setGroupData,
});
</script>

<style scoped>
.empty-box {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 40px;
  width: 100%;
  color: #d9dbdd;
}
</style>
