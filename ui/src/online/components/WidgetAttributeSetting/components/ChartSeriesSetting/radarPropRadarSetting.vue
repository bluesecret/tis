<template>
  <el-form label-position="top" size="default" v-if="radarSetting" @submit.prevent>
    <el-form-item label="显示位置(X)">
      <el-slider v-model="formData.centerX" :min="0" :max="100" @change="onChange" />
    </el-form-item>
    <el-form-item label="显示位置(Y)">
      <el-slider v-model="formData.centerY" :min="0" :max="100" @change="onChange" />
    </el-form-item>
    <el-form-item label="形状">
      <el-radio-group v-model="formData.shape" @change="onChange">
        <el-radio-button value="polygon">多边形</el-radio-button>
        <el-radio-button value="circle">圆形</el-radio-button>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="雷达图尺寸">
      <el-input-number
        controls-position="right"
        clearable
        style="width: 100%"
        :min="0"
        v-model="formData.radius"
        @change="onChange"
      />
    </el-form-item>
    <el-form-item label="旋转角度">
      <el-input-number
        controls-position="right"
        clearable
        style="width: 100%"
        :min="0"
        :max="180"
        v-model="formData.startAngle"
        @change="onChange"
      />
    </el-form-item>
    <el-form-item label="指示器">
      <div
        v-for="(item, index) in valueColumnList"
        class="indicator-wrapper"
        :key="index"
        @click="onEditIndicatorItem(item)"
      >
        <span class="indicator-label">
          {{ item.showName }}
        </span>
        <span class="indicator-value">
          {{ item.max ? '最大值' + item.max : '' }} {{ item.min ? '最小值' + item.min : '' }}
        </span>
      </div>
    </el-form-item>
    <el-form-item label="顶点字体颜色">
      <el-color-picker v-model="formData.axisLine.lineStyle.color" @change="onChange" />
    </el-form-item>
    <el-form-item label="分隔线颜色">
      <el-color-picker v-model="formData.splitLine.lineStyle.color" @change="onChange" />
    </el-form-item>
    <el-form-item label="分割段数">
      <el-slider v-model="formData.splitNumber" :min="0" :max="10" @change="onChange" />
    </el-form-item>
    <el-form-item label="分隔区域配色">
      <ChartColorSelect
        v-model:value="formData.splitArea.areaStyle.color"
        :showAlpha="true"
        :allowCreate="true"
        @change="onChange"
      />
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { getDefaultRadar } from '@/components/Charts/chartOption';
import { ANY_OBJECT } from '@/types/generic';
import { Dialog } from '@/components/Dialog';
import ChartColorSelect from './radarColorSelect.vue';
import EditRadarIndicatorForm from './EditRadarIndicatorForm.vue';

const defaultRadarSetting = {
  ...getDefaultRadar(),
};

const emit = defineEmits<{ 'update:value': [ANY_OBJECT] }>();
const props = defineProps<{ value: ANY_OBJECT }>();
const formData = ref<ANY_OBJECT>({
  ...defaultRadarSetting,
  ...props.value,
  centerX: 50,
  centerY: 50,
});

const formConfig = inject('formConfig', () => {
  console.error('radarPropRadarSetting: formConfig not injected');
  return {} as ANY_OBJECT;
});

const widget = computed(() => {
  return formConfig().currentWidget;
});
const radarSetting = computed(() => {
  return widget.value ? (widget.value.props || {}).radarSetting : undefined;
});
const valueColumnList = computed<ANY_OBJECT[]>(() => {
  return widget.value.props.datasetInfo.valueColumnList;
});

const onChange = () => {
  formData.value.center = [formData.value.centerX + '%', formData.value.centerY + '%'];
  emit('update:value', formData.value);
};

watch(
  () => props.value,
  newValue => {
    formData.value = {
      ...defaultRadarSetting,
      ...newValue,
    };
  },
  {
    immediate: true,
  },
);

const onEditIndicatorItem = (row: ANY_OBJECT) => {
  Dialog.show<ANY_OBJECT>(
    '指示器',
    EditRadarIndicatorForm,
    {
      area: ['600px'],
    },
    {
      rowData: row,
    },
    {},
  )
    .then(() => {
      // TODO 指示器
      // let row1: ANY_OBJECT = valueColumnList.value.find(x => x.columnId === res.columnId);
      // row1.text = res.text;
      // row1.max = res.max;
      // row1.min = res.min;
      //this.$forceUpdate();
      onChange();
    })
    .catch(e => {
      console.warn(e);
    });
};
</script>

<style scoped lang="scss">
.indicator-wrapper {
  display: flex;
  justify-content: space-between;
  height: 32px;
  padding: 0 15px;
  padding-top: 4px;
  margin-bottom: 10px;
  font-size: 12px;
  color: #666;
  background-color: white;
  border: solid 1px #dcdfe6;
  border-radius: 6px;
  line-height: 32px;
  cursor: pointer;

  &:last-of-type {
    margin-bottom: 0;
  }

  .indicator-label {
    display: inline-block;
    height: 20px;
    line-height: 20px;

    &:hover {
      border-bottom: solid 1px #fca834;
    }
  }
  .indicator-value {
    display: inline-block;
    height: 20px;
    line-height: 20px;
  }
}
</style>
