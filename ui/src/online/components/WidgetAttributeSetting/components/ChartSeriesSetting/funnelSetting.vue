<template>
  <el-form-item class="view-attribute-item" label="左侧距离">
    <el-input
      placeholder=""
      v-model="formData.left"
      @input="val => onInput('left', val)"
      @change="onChange"
    >
      <template v-slot:append>%</template>
    </el-input>
  </el-form-item>
  <el-form-item class="view-attribute-item" label="组件宽度">
    <el-input
      placeholder=""
      v-model="formData.width"
      @input="val => onInput('width', val)"
      @change="onChange"
    >
      <template v-slot:append>%</template>
    </el-input>
  </el-form-item>
  <el-form-item class="view-attribute-item" label="上部距离">
    <el-input-number
      controls-position="right"
      clearable
      style="width: 100%"
      v-model="formData.top"
      @change="onChange"
    />
  </el-form-item>
  <el-form-item class="view-attribute-item" label="下部距离">
    <el-input-number
      controls-position="right"
      clearable
      style="width: 100%"
      v-model="formData.bottom"
      @change="onChange"
    />
  </el-form-item>
  <el-form-item class="view-attribute-item" label="最小宽度">
    <el-input
      placeholder=""
      v-model="formData.minSize"
      @input="val => onInput('minSize', val)"
      @change="onChange"
    >
      <template v-slot:append>%</template>
    </el-input>
  </el-form-item>
  <el-form-item class="view-attribute-item" label="最大宽度">
    <el-input
      placeholder=""
      v-model="formData.maxSize"
      @input="val => onInput('maxSize', val)"
      @change="onChange"
    >
      <template v-slot:append>%</template>
    </el-input>
  </el-form-item>
  <el-form-item class="view-attribute-item" label="排序">
    <el-radio-group v-model="formData.sort" @change="onChange">
      <el-radio-button value="descending">降序</el-radio-button>
      <el-radio-button value="ascending">升序</el-radio-button>
    </el-radio-group>
  </el-form-item>
  <el-form-item class="view-attribute-item" label="间距">
    <el-slider
      style="width: 95%; margin-left: 5px"
      v-model="formData.gap"
      :min="0"
      :max="10"
      @change="onChange"
    />
  </el-form-item>
  <el-form-item class="view-attribute-item" label="边框颜色">
    <el-color-picker v-model="formData.itemStyle.borderColor" @change="onChange" />
  </el-form-item>
  <el-form-item class="view-attribute-item" label="边框宽度">
    <el-slider
      style="width: 95%; margin-left: 5px"
      v-model="formData.itemStyle.borderWidth"
      :min="0"
      :max="10"
      size="default"
      @change="onChange"
    />
  </el-form-item>
</template>

<script setup lang="ts">
import { getDefaultSerieFunnel } from '@/components/Charts/chartOption';
import { ANY_OBJECT } from '@/types/generic';

const defaultFunnelSetting = {
  ...getDefaultSerieFunnel(),
};

const emit = defineEmits<{ 'update:value': [ANY_OBJECT] }>();
const props = defineProps<{ value: ANY_OBJECT }>();
const formData = ref<ANY_OBJECT>({
  ...defaultFunnelSetting,
});

const onChange = () => {
  emit('update:value', formData.value);
};

const onInput = (name: string, val: string) => {
  const data: ANY_OBJECT = { ...formData.value };
  data[name] = val.replace(/[^\d]/g, '');
  formData.value = data;
};

watch(
  () => props.value,
  newValue => {
    formData.value = {
      ...defaultFunnelSetting,
      ...newValue,
    };
  },
  {
    immediate: true,
  },
);
</script>
