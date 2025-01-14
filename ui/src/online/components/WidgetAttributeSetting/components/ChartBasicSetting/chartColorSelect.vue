<template>
  <el-popover placement="bottom-start" width="400" trigger="click" :visible="visible">
    <el-row>
      <el-form
        label-position="left"
        label-width="70px"
        :size="layoutStore.defaultFormItemSize"
        @submit.prevent
      >
        <el-form-item label="系统方案">
          <el-select v-model="currentSysColorId" placeholder="" @change="onSysConfigChange">
            <el-option
              v-for="item in SysChartColorsList"
              :key="item.id"
              :value="item.id"
              :label="item.name"
            >
              <el-row type="flex" align="middle">
                <div
                  class="chart-color-item"
                  v-for="(color, index) in item.colorList"
                  :key="index + color"
                  :style="{ 'background-color': color }"
                />
                <span style="margin-left: 10px">{{ item.name }}</span>
              </el-row>
            </el-option>
          </el-select>
          <el-button link style="margin-left: 5px" @click="onResetColor">重置</el-button>
        </el-form-item>
        <el-form-item label="自定义" style="margin-bottom: 0">
          <el-row>
            <el-color-picker
              v-for="(item, index) in value"
              :key="index + (item || '')"
              v-model="colorList[index]"
              style="margin-right: 5px"
              @focus="visible = true"
              @blur="visible = null"
              @change="val => onColorItemChange(val || undefined, index)"
            />
          </el-row>
        </el-form-item>
      </el-form>
    </el-row>
    <template #reference>
      <el-row>
        <div
          class="chart-color-item"
          v-for="(item, index) in value"
          :key="index + (item || '')"
          :style="{ 'background-color': item }"
        />
      </el-row>
    </template>
  </el-popover>
</template>

<script lang="ts">
const SysChartColorsList = [
  {
    id: 0,
    name: '默认',
    colorList: [
      '#5470C6',
      '#91CC75',
      '#FAC858',
      '#EE6666',
      '#73C0DE',
      '#3BA272',
      '#FC8452',
      '#9A60B4',
      '#EA7CCC',
    ],
  },
  {
    id: 1,
    name: '复古',
    colorList: [
      '#0780CF',
      '#765005',
      '#FA6D1D',
      '#0E2C82',
      '#B6B51F',
      '#DA1F18',
      '#701866',
      '#F47A75',
      '#009DB2',
    ],
  },
  {
    id: 2,
    name: '淡雅',
    colorList: [
      '#95A2FF',
      '#FA8080',
      '#FFC076',
      '#FAE768',
      '#87E885',
      '#3CB9FC',
      '#73ABF5',
      '#CB9BFF',
      '#434348',
    ],
  },
];
</script>

<script setup lang="ts">
import { useLayoutStore } from '@/store';

const layoutStore = useLayoutStore();

const props = withDefaults(
  defineProps<{
    value: (string | undefined)[];
  }>(),
  {
    value: () => {
      return [...SysChartColorsList[0].colorList];
    },
  },
);

const visible = ref<boolean | null>(null);
const currentSysColorId = ref<number>();
const colorList = ref([...props.value]);

const currentSysColor = computed(() => {
  if (currentSysColorId.value || currentSysColorId.value === 0) {
    for (let i = 0; i < SysChartColorsList.length; i++) {
      if (SysChartColorsList[i].id === currentSysColorId.value) {
        return SysChartColorsList[i];
      }
    }
  }

  return null;
});

const emit = defineEmits<{ 'update:value': [(string | undefined)[]] }>();
const onResetColor = () => {
  if (currentSysColor.value !== null) {
    colorList.value = [...currentSysColor.value.colorList];
    emit('update:value', [...currentSysColor.value.colorList]);
  }
};
const onSysConfigChange = () => {
  onResetColor();
};
const onColorItemChange = (color: string | undefined, index: number) => {
  let temp: (string | undefined)[] = [...props.value];
  temp[index] = color;
  //console.log('color change', index, color, temp);
  colorList.value = temp;
  emit('update:value', temp);
  visible.value = null;
};
</script>

<style scoped>
.chart-color-item {
  display: inline-block;
  width: 28px;
  height: 28px;
  margin: 4px 0;
  cursor: pointer;
}
</style>
