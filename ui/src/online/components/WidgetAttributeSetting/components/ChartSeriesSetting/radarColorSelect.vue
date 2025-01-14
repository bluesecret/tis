<template>
  <Draggable
    draggable=".item-draggable"
    v-model="colorList"
    group="item-draggable"
    style="display: inline-block; height: 100%"
    :disabled="!supportSort"
  >
    <div class="chart-color-item item-draggable" v-for="(item, index) in value" :key="index">
      <el-color-picker
        v-model="colorList[index]"
        :predefine="predefineColors"
        :showAlpha="showAlpha"
        @change="val => onColorItemChange(val, index)"
      />
      <div class="chart-color-item-delete" @click="removeColor(index)">
        <el-icon><Minus /></el-icon>
      </div>
    </div>
    <div class="chart-color-item chart-color-create" @click="addColor">
      <div class="chart-color-item-color" style="padding-top: 3px">
        <el-icon><Plus /></el-icon>
      </div>
    </div>
  </Draggable>
</template>

<script setup lang="ts">
import { Plus, Minus } from '@element-plus/icons-vue';
import { VueDraggable as Draggable } from 'vue-draggable-plus';

const PredefineColorsList = [
  '#ff4500',
  '#ff8c00',
  '#ffd700',
  '#90ee90',
  '#00ced1',
  '#1e90ff',
  '#c71585',
  '#c7158577',
  'rgba(255, 69, 0, 0.68)',
  'rgb(255, 120, 0)',
  'hsv(51, 100, 98)',
  'hsva(120, 40, 94, 0.5)',
  'hsl(181, 100%, 37%)',
  'hsla(209, 100%, 56%, 0.73)',
];

const emit = defineEmits<{ 'update:value': [string[]] }>();
const props = withDefaults(
  defineProps<{
    value?: string[];
    allowCreate?: boolean;
    supportSort?: boolean;
    showAlpha?: boolean;
  }>(),
  {
    value: () => [],
    allowCreate: false,
    supportSort: true,
    showAlpha: false,
  },
);

const predefineColors = ref<string[]>(PredefineColorsList);

// 不能直接修改props属性，需要通过事件进行更新
const colorList = computed({
  get: () => {
    return props.value;
  },
  set: (val: string[]) => {
    console.log('colorList change', val);
    emit('update:value', val);
  },
});

const addColor = () => {
  const values = props.value;
  values.push('rgba(230,230,230,0.8)');
  emit('update:value', values);
};
const removeColor = (index: number) => {
  colorList.value.splice(index, 1);
};
const onColorItemChange = (color: string | null, index: number) => {
  console.log('color chage', color, index);
  emit('update:value', props.value);
};
</script>

<style lang="scss" scoped>
.chart-color-wrapper {
  display: flex;
  flex-wrap: wrap;
  padding-top: 5px;
}
.chart-color-item {
  position: relative;
  display: inline-block;
  float: left;
  width: 32px;
  height: 32px;
  margin-right: 5px;
  margin-bottom: 5px;
  border-radius: 4px;
  cursor: pointer;
  box-sizing: border-box;

  &.chart-color-create {
    padding: 4px;
    border: solid 1px #e6e6e6;
  }

  .chart-color-item-delete {
    position: absolute;
    top: -3px;
    right: -3px;
    display: none;
    width: 12px;
    height: 12px;
    background-color: #fe7600;
    border-radius: 50%;

    .el-icon-minus {
      display: inline-block;
      float: left;
      width: 8px;
      height: 8px;
      margin-top: 1.8px;
      margin-left: 0.5px;
      font-size: 2px;
      color: #fff;
      transform: scale(50%);
    }
  }

  &:hover {
    .chart-color-item-delete {
      display: inline-block;
    }
  }

  &-color {
    display: inlineb-lock;
    width: 100%;
    height: 100%;
    text-align: center;
    color: #888;
    background-color: #eee;
    border: 1px solid #ddd;
    border-radius: 2px;
    box-sizing: border-box;
    line-height: 150%;
  }
}

.chart-color-form {
  width: 100%;

  &-label {
    display: inline-block;
    width: 80px;
    height: 40px;
    vertical-align: middle;
  }
  &-color {
    display: inline-block;
    height: 40px;
    vertical-align: middle;
  }
}
</style>
