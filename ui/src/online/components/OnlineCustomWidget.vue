<template>
  <template
    v-if="
      pps.widget.bindData.dataType !== SysCustomWidgetBindDataType.Column ||
      pps.widget.column != null ||
      widget.widgetType === SysCustomWidgetType.Text ||
      form().pageCode != null ||
      form().mode === 'mobile'
    "
  >
    <component
      v-if="getWidgetVisible"
      v-bind="getWidgetProps"
      :ref="widget.variableName"
      :is="getComponent"
      :style="getWidgetStyle"
      :disabled="getDisabledStatus"
      :widget="widget"
      v-model="bindValue"
      :value="bindValue"
      @input="onValueInput"
      @change="onValueChange"
      :change="onValueChange"
      @widgetClick="onWidgetClick"
    >
      <template
        v-if="form().mode === 'pc' && widget.widgetType !== SysCustomWidgetType.Cascader"
        v-slot="scope"
      >
        <template v-if="widget.widgetType === SysCustomWidgetType.Radio">
          <el-radio v-for="item in getAllDropdownData" :key="item.id" :value="item.id">
            {{ item.name }}
          </el-radio>
        </template>
        <template
          v-else-if="
            widget.widgetType === SysCustomWidgetType.Date ||
            widget.widgetType === SysCustomWidgetType.DateRange
          "
        >
          <div class="el-date-table-cell">
            <span class="el-date-table-cell__text">{{ scope.text }}</span>
          </div>
        </template>
        <template v-else-if="widget.widgetType === SysCustomWidgetType.CheckBox">
          <el-checkbox v-for="item in getAllDropdownData" :key="item.id" :label="item.id">
            {{ item.name }}
          </el-checkbox>
        </template>
        <template v-else-if="widget.widgetType === SysCustomWidgetType.Select">
          <el-option
            v-for="item in getAllDropdownData"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </template>
        <template v-if="widget.widgetType === SysCustomWidgetType.Link">
          <span>{{ widget.props.showText || widget.showName }}</span>
        </template>
      </template>
      <template
        v-if="form().mode === 'mobile' && widget.widgetType !== SysCustomWidgetType.Input"
        #input
      >
        <RadioGroup
          v-bind="getWidgetProps"
          v-if="SysCustomWidgetType.Radio === widget.widgetType"
          v-model="tempValue"
        >
          <Radio
            :class="{
              'van-vertical-item': getWidgetProps.direction === 'vertical',
            }"
            v-for="item in getAllDropdownData"
            :key="item.id"
            :name="item.id"
          >
            {{ item.name }}
          </Radio>
        </RadioGroup>
        <CheckboxGroup
          v-bind="getWidgetProps"
          v-if="SysCustomWidgetType.CheckBox === widget.widgetType"
          v-model:value="tempValue"
        >
          <Checkbox
            v-bind="getWidgetProps"
            :class="{
              'van-vertical-item': getWidgetProps.direction === 'vertical',
            }"
            v-for="item in getAllDropdownData"
            :key="item.id"
            :name="item.id"
            shape="square"
          >
            {{ item.name }}
          </Checkbox>
        </CheckboxGroup>
        <Rate
          v-bind="getWidgetProps"
          v-model:value="tempValue"
          v-if="SysCustomWidgetType.Rate === widget.widgetType"
        />
        <Switch
          v-bind="getWidgetProps"
          v-model:value="tempValue"
          v-if="SysCustomWidgetType.Switch === widget.widgetType"
        />
        <Uploader
          v-bind="getWidgetProps"
          v-if="SysCustomWidgetType.Upload === widget.widgetType && isUploadImage"
          :deletable="true"
        />
        <Button
          v-if="SysCustomWidgetType.Upload === widget.widgetType && !isUploadImage"
          icon="plus"
          type="default"
          size="small"
        >
          上传文件
        </Button>
        <Stepper
          v-bind="getWidgetProps"
          v-model:value="tempValue"
          v-if="SysCustomWidgetType.Stepper === widget.widgetType"
        />
      </template>
      <template v-if="form().mode === 'mobile' && showRightIcon" #right-icon>
        <i class="van-icon van-icon-arrow van-cell__right-icon" />
      </template>
    </component>
  </template>
  <template v-else>
    <component
      v-if="getWidgetVisible"
      :ref="widget.variableName"
      :is="getComponent"
      :style="getWidgetStyle"
      :disabled="getDisabledStatus"
      :widget="widget"
      v-model="bindValue"
      :value="bindValue"
      @change="onValueChange"
      @input="onValueInput"
      :change="onValueChange"
      @widgetClick="onWidgetClick"
    />
  </template>
</template>

<script setup lang="ts">
import {
  ElCascader,
  ElCheckboxGroup,
  ElDatePicker,
  ElInput,
  ElInputNumber,
  ElLink,
  ElRadioGroup,
  ElSelect,
  ElSwitch,
} from 'element-plus';
import {
  Field as VanField,
  RadioGroup,
  Radio,
  Checkbox,
  CheckboxGroup,
  Rate,
  Switch,
  Button,
  Stepper,
} from 'vant';
import OnlineCustomLabel from './OnlineCustomLabel.vue';
import OnlineCustomUpload from './OnlineCustomUpload.vue';
import OnlineCustomTree from './OnlineCustomTree.vue';
import OnlineCustomChart from './OnlineCustomChart.vue';
import OnlineCustomDataSelect from './OnlineCustomDataSelect/index.vue';
import OnlineCustomText from './OnlineCustomText.vue';
import OnlineCustomImage from './OnlineCustomImage.vue';
import OnlineMobileSelectFilter from './mobile/MobileSelectFilter.vue';
import OnlineMobileInputFilter from './mobile/MobileInputFilter.vue';
import OnlineMobileSwitchFilter from './mobile/MobileSwitchFilter.vue';
import OnlineMobileDateRangeFilter from './mobile/MobileDateRangeFilter.vue';
import OnlineMobileNumbrRangeFilter from './mobile/MobileNumberRangeFilter.vue';
import { WidgetEmit, WidgetProps } from './types/widget';
import { useWidget } from './hooks/widget';
import InputNumberRange from '@/components/InputNumberRange/index.vue';
import DeptSelect from '@/components/DeptSelect/index.vue';
import UserSelect from '@/components/UserSelect/index.vue';
import RichEditor from '@/components/RichEditor/index.vue';
import {
  treeDataTranslate,
  findTreeNode,
  findItemFromList,
  findTreeNodePath,
  formatDate,
} from '@/common/utils';
import { SysOnlineColumnFilterType, SysOnlineFieldKind } from '@/common/staticDict/online';
import {
  SysCustomWidgetBindDataType,
  OnlineFormEventType,
  SysCustomWidgetType,
  SysOnlineFormType,
  SysOnlineDictType,
} from '@/common/staticDict';
import { ANY_OBJECT } from '@/types/generic';

type ValueType = string | boolean | Date | number | ANY_OBJECT | Array<ANY_OBJECT>;

interface IEmit extends WidgetEmit {
  (event: 'change', value: ANY_OBJECT | undefined, params: ANY_OBJECT | null): void;
  (event: 'update:value', value: ValueType | undefined): void;
  (event: 'update:modelValue', value: ValueType | undefined): void;
  (event: 'update:widget', value: ANY_OBJECT): void;
  (event: 'input', value: ValueType | undefined): void;
}
const emit = defineEmits<IEmit>();

interface IProps extends WidgetProps {
  value?: ValueType;
  widget: ANY_OBJECT;
}
const pps = withDefaults(defineProps<IProps>(), {});

const form = inject('form', () => {
  return { isEdit: false } as ANY_OBJECT;
});
const parentWidget = inject<ANY_OBJECT | null>('parentWidget', null);

const { propsWidget, onWidgetClick } = useWidget(pps, emit);

const dictDataList = ref<ANY_OBJECT[]>([]);
const tempValue = ref();

const multiSelect = computed(() => {
  if (
    [
      SysCustomWidgetType.Select,
      SysCustomWidgetType.CheckBox,
      SysCustomWidgetType.Cascader,
      SysCustomWidgetType.Tree,
    ].indexOf(pps.widget.widgetType) === -1
  ) {
    return false;
  }
  if (pps.widget.widgetType === SysCustomWidgetType.CheckBox) return true;
  if (
    form().formType === SysOnlineFormType.QUERY ||
    form().formType === SysOnlineFormType.ADVANCE_QUERY
  ) {
    if (
      pps.widget.column &&
      pps.widget.column.filterType === SysOnlineColumnFilterType.MULTI_SELECT_FILTER
    ) {
      return true;
    } else {
      return false;
    }
  } else if (form().formType === SysOnlineFormType.REPORT) {
    return SysCustomWidgetType.CheckBox === pps.widget.widgetType;
  } else {
    return pps.widget.column && pps.widget.column.fieldKind === SysOnlineFieldKind.MULTI_SELECT;
  }
});
const isMobileFilter = computed(() => {
  return (
    [
      SysCustomWidgetType.MobileRadioFilter,
      SysCustomWidgetType.MobileCheckBoxFilter,
      SysCustomWidgetType.MobileInputFilter,
      SysCustomWidgetType.MobileDateRangeFilter,
      SysCustomWidgetType.MobileNumberRangeFilter,
      SysCustomWidgetType.MobileSwitchFilter,
    ].indexOf(pps.widget.widgetType) !== -1
  );
});
const getAllDropdownData = computed(() => {
  if (pps.widget.props.supportAll) {
    return [
      {
        id: '',
        name: '全部',
      },
      ...dictDataList.value,
    ];
  } else {
    return dictDataList.value;
  }
});
const getWidgetStyle = computed(() => {
  return {
    width: pps.widget.widgetType !== SysCustomWidgetType.Link ? '100%' : undefined,
  };
});
const getComponent = computed(() => {
  if (
    [
      SysCustomWidgetType.Text,
      SysCustomWidgetType.Image,
      SysCustomWidgetType.Upload,
      SysCustomWidgetType.Link,
    ].indexOf(pps.widget.widgetType) === -1 &&
    form().readOnly
  ) {
    return OnlineCustomLabel;
  }
  let mode = form().mode;

  switch (pps.widget.widgetType) {
    case SysCustomWidgetType.Label:
      return mode !== 'pc' ? VanField : OnlineCustomLabel;
    case SysCustomWidgetType.Input:
      return mode !== 'pc' ? VanField : ElInput;
    case SysCustomWidgetType.NumberInput:
      return ElInputNumber;
    case SysCustomWidgetType.NumberRange:
      return InputNumberRange;
    case SysCustomWidgetType.Switch:
      return mode !== 'pc' ? VanField : ElSwitch;
    case SysCustomWidgetType.Radio:
      return mode !== 'pc' ? VanField : ElRadioGroup;
    case SysCustomWidgetType.CheckBox:
      return mode !== 'pc' ? VanField : ElCheckboxGroup;
    case SysCustomWidgetType.Select:
      return mode !== 'pc' ? VanField : ElSelect;
    case SysCustomWidgetType.Cascader:
      return mode !== 'pc' ? VanField : ElCascader;
    case SysCustomWidgetType.Date:
      return mode !== 'pc' ? VanField : ElDatePicker;
    case SysCustomWidgetType.DateRange:
      return ElDatePicker;
    case SysCustomWidgetType.Upload:
      return mode !== 'pc' ? VanField : OnlineCustomUpload;
    case SysCustomWidgetType.RichEditor:
      return RichEditor;
    case SysCustomWidgetType.Link:
      return ElLink;
    case SysCustomWidgetType.UserSelect:
      return mode !== 'pc' ? VanField : UserSelect;
    case SysCustomWidgetType.DeptSelect:
      return mode !== 'pc' ? VanField : DeptSelect;
    case SysCustomWidgetType.DataSelect:
      return mode !== 'pc' ? VanField : OnlineCustomDataSelect;
    case SysCustomWidgetType.Tree:
      return OnlineCustomTree;
    case SysCustomWidgetType.Rate:
      return VanField;
    case SysCustomWidgetType.Stepper:
      return VanField;
    case SysCustomWidgetType.Calendar:
      return VanField;
    case SysCustomWidgetType.LineChart:
    case SysCustomWidgetType.PieChart:
    case SysCustomWidgetType.BarChart:
    case SysCustomWidgetType.ScatterChart:
    case SysCustomWidgetType.PivotTable:
    case SysCustomWidgetType.DataViewTable:
    case SysCustomWidgetType.Carousel:
    case SysCustomWidgetType.RichText:
    case SysCustomWidgetType.GaugeChart:
    case SysCustomWidgetType.FunnelChart:
    case SysCustomWidgetType.RadarChart:
    case SysCustomWidgetType.ProgressBar:
    case SysCustomWidgetType.ProgressCircle:
    case SysCustomWidgetType.DataCard:
    case SysCustomWidgetType.DataProgressCard:
    case SysCustomWidgetType.CommonList:
      return OnlineCustomChart;
    case SysCustomWidgetType.Text:
      return OnlineCustomText;
    case SysCustomWidgetType.Image:
      return OnlineCustomImage;
    case SysCustomWidgetType.MobileRadioFilter:
    case SysCustomWidgetType.MobileCheckBoxFilter:
      return OnlineMobileSelectFilter;
    case SysCustomWidgetType.MobileInputFilter:
      return OnlineMobileInputFilter;
    case SysCustomWidgetType.MobileSwitchFilter:
      return OnlineMobileSwitchFilter;
    case SysCustomWidgetType.MobileDateRangeFilter:
      return OnlineMobileDateRangeFilter;
    case SysCustomWidgetType.MobileNumberRangeFilter:
      return OnlineMobileNumbrRangeFilter;
    default:
      console.warn('widget getComponent 未知类型');
      return 'div';
  }
});
const getLinkHerf = computed(() => {
  let temp = pps.widget.widgetType === SysCustomWidgetType.Link ? pps.widget.props.href : undefined;
  if (
    !form().isEdit &&
    pps.widget.eventInfo &&
    typeof pps.widget.eventInfo[OnlineFormEventType.LINK_HERF] === 'function'
  ) {
    temp = pps.widget.eventInfo[OnlineFormEventType.LINK_HERF](
      pps.widget.props.href,
      form().instanceData(),
    );
  }
  return temp;
});
const isDictWidget = computed(() => {
  return (
    [
      SysCustomWidgetType.Select,
      SysCustomWidgetType.CheckBox,
      SysCustomWidgetType.Radio,
      SysCustomWidgetType.Cascader,
      SysCustomWidgetType.Tree,
      SysCustomWidgetType.MobileCheckBoxFilter,
      SysCustomWidgetType.MobileRadioFilter,
    ].indexOf(pps.widget.widgetType) !== -1
  );
});

const getColumnDataType = computed(() => {
  if (pps.widget == null || pps.widget.column == null) return undefined;
  switch (pps.widget.column.objectFieldType) {
    case 'String':
      return 'String';
    case 'Date':
      return 'Date';
    case 'Boolean':
      return 'Boolean';
    case 'Integer':
    case 'Long':
    case 'Float':
    case 'Double':
    case 'BigDecimal':
      return 'Number';
    default:
      return undefined;
  }
});
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const bindValue = computed<string | number | boolean | any[] | ANY_OBJECT | Date | undefined>({
  get() {
    let tempValue = pps.value === undefined ? '' : pps.value;
    if (isDictWidget.value) tempValue = tempValue == null ? '' : tempValue + '';
    if (multiSelect.value && pps.value && typeof tempValue === 'string') {
      tempValue = tempValue.split(',');
      if (Array.isArray(tempValue)) {
        tempValue = tempValue.filter(item => {
          return item != null && item !== '';
        });
      }
    }

    let bindColumnType = getColumnDataType.value;
    if (pps.widget.widgetType === SysCustomWidgetType.Text) {
      // Text组件
      if (
        pps.widget &&
        pps.widget.column &&
        pps.widget.column.objectFieldType === 'Date' &&
        !form().isEdit
      ) {
        return formatDate(tempValue, pps.widget.props.dateType || 'YYYY-MM-DD HH:mm:ss');
      } else {
        return tempValue;
      }
    } else if (pps.widget.widgetType === SysCustomWidgetType.Switch) {
      // 如果开关组件的字段绑定了字典，并且是只读显示，那么显示字典的值
      let dictInfo = (pps.widget.column || {}).dictId;
      if ((form().readOnly || pps.widget.props.readOnly) && dictInfo != null) {
        return tempValue;
      }
      // 根据绑定字段的类型，转换开关组件的值
      if (bindColumnType === 'Number') {
        tempValue = tempValue !== 0 && tempValue != null;
      } else if (bindColumnType === 'String') {
        tempValue = tempValue === 'true';
      } else if (bindColumnType !== 'Boolean') {
        tempValue = false;
      }
      return tempValue;
    }
    if (pps.widget.widgetType === SysCustomWidgetType.CheckBox) {
      return form().mode === 'mobile' ? undefined : tempValue || [];
    } else if (pps.widget.widgetType === SysCustomWidgetType.Cascader) {
      if (multiSelect.value) {
        // 只读返回原始值
        if (form().readOnly) {
          return tempValue;
        }
        // 多选
        if (Array.isArray(tempValue)) {
          return tempValue
            .map(item => {
              let temp = findTreeNodePath(dictDataList.value, item);
              if (Array.isArray(temp) && temp.length > 0) return temp;
              return null;
            })
            .filter(item => item != null);
        } else {
          let temp = findTreeNodePath(dictDataList.value, tempValue.toString());
          return temp || [];
        }
      } else {
        // 单选
        let temp = findTreeNodePath(dictDataList.value, tempValue.toString());
        return temp.length > 0 ? temp : tempValue;
      }
    } else if (pps.widget.widgetType === SysCustomWidgetType.NumberInput) {
      return tempValue as number;
    } else {
      return tempValue;
    }
  },
  set(val) {
    onValueInput(val as ANY_OBJECT | undefined);
  },
});
const isUploadImage = computed(() => {
  if (pps.widget.widgetType !== SysCustomWidgetType.Upload) return false;
  let column = pps.widget.bindData.column || pps.widget.column;
  return column ? column.fieldKind === SysOnlineFieldKind.UPLOAD_IMAGE : false;
});
const showRightIcon = computed(() => {
  return (
    [
      SysCustomWidgetType.Select,
      SysCustomWidgetType.Cascader,
      SysCustomWidgetType.Date,
      SysCustomWidgetType.UserSelect,
      SysCustomWidgetType.DeptSelect,
      SysCustomWidgetType.Calendar,
    ].indexOf(pps.widget.widgetType) !== -1
  );
});

const getWidgetProps = computed(() => {
  let props = {
    ...(pps.widget.props || {}),
  };
  // 日期组件，根据类型设置format
  if (
    pps.widget.widgetType === SysCustomWidgetType.Date ||
    pps.widget.widgetType === SysCustomWidgetType.DateRange
  ) {
    let valueFormat = 'YYYY-MM-DD';
    switch (pps.widget.props.type) {
      case 'date':
        valueFormat = 'YYYY-MM-DD';
        break;
      case 'month':
        valueFormat = 'YYYY-MM';
        break;
      case 'year':
        valueFormat = 'YYYY';
        break;
      case 'datetime':
        valueFormat = 'YYYY-MM-DD HH:mm:ss';
        break;
      default:
        valueFormat = 'YYYY-MM-DD HH:mm:ss';
        break;
    }
    props['format'] = valueFormat;
    props['value-format'] = 'YYYY-MM-DD HH:mm:ss';
  }

  return {
    ...props,
    clearable: true,
    filterable: true,
    readonly:
      form().mode === 'mobile' && SysCustomWidgetType.Label === pps.widget.widgetType
        ? true
        : undefined,
    readOnly:
      pps.widget.widgetType === SysCustomWidgetType.Upload
        ? form().readOnly || props.readOnly
        : undefined,
    options:
      pps.widget.widgetType === SysCustomWidgetType.Cascader ? dictDataList.value : undefined,
    props:
      pps.widget.widgetType === SysCustomWidgetType.Cascader
        ? {
            label: 'name',
            value: 'id',
            multiple: multiSelect.value,
            checkStrictly: true,
          }
        : undefined,
    'disabled-date':
      pps.widget.widgetType === SysCustomWidgetType.Date
        ? (pps.widget.eventInfo || {})[OnlineFormEventType.DISABLED_DATE]
        : undefined,
    operationList: pps.widget.operationList,
    multiple: multiSelect.value,
    'collapse-tags': multiSelect.value,
    dataList: pps.widget.widgetType === SysCustomWidgetType.Tree ? dictDataList.value : undefined,
    dictDataList: isMobileFilter.value ? getAllDropdownData.value : undefined,
    href: getLinkHerf.value,
    label:
      form().mode === 'mobile' && (getComponent.value === VanField || isMobileFilter.value)
        ? pps.widget.showName
        : undefined,
    widget: pps.widget,
    extraData: pps.widget.widgetType === SysCustomWidgetType.DataSelect ? form().formData : {},
  };
});
const getDisabledStatus = ref(false);
const calcDisabledStatus = () => {
  const formWidgetAuth: ANY_OBJECT | null =
    form().formAuth && form().formAuth() && form().formAuth().pc
      ? form().formAuth().pc[pps.widget.variableName]
      : null;
  if (formWidgetAuth && formWidgetAuth.disabled) {
    getDisabledStatus.value = true;
  } else if (pps.widget.eventInfo && pps.widget.eventInfo[OnlineFormEventType.DISABLE]) {
    getDisabledStatus.value = pps.widget.eventInfo[OnlineFormEventType.DISABLE](
      form().instanceData(),
    );
  } else {
    getDisabledStatus.value = pps.widget.props.disabled;
  }
};
const getWidgetVisible = ref(true);
const calcWidgetVisible = () => {
  getWidgetVisible.value = form().getWidgetVisible(pps.widget);
};
watch(
  () => {
    return form().instanceData ? form().instanceData() : null;
  },
  () => {
    calcDisabledStatus();
    calcWidgetVisible();
  },
  {
    immediate: true,
    deep: true,
  },
);

const parseValue = (val: ValueType): ANY_OBJECT | number | boolean | string => {
  let bindColumnType = getColumnDataType.value;
  if (pps.widget?.widgetType === SysCustomWidgetType.Switch) {
    if (bindColumnType === 'Number') {
      return val ? 1 : 0;
    } else if (bindColumnType === 'String') {
      return val ? 'true' : 'false';
    } else if (bindColumnType !== 'Boolean') {
      return false;
    } else {
      return val;
    }
  }
  if (pps.widget.widgetType === SysCustomWidgetType.Cascader) {
    if (multiSelect.value && Array.isArray(val)) {
      return val
        .map((item: ANY_OBJECT) => {
          return item[item.length - 1];
        })
        .filter((item: ANY_OBJECT) => item != null);
    } else {
      return Array.isArray(val) ? val[val.length - 1] : undefined;
    }
  } else {
    return val as ANY_OBJECT;
  }
};
const onValueInput = (val: ANY_OBJECT | undefined) => {
  let tempValue: ValueType | undefined = undefined;
  if (val) {
    if (val instanceof InputEvent || val instanceof Event) {
      return;
    } else {
      tempValue = parseValue(val);
    }

    if (multiSelect.value && Array.isArray(tempValue)) {
      if (tempValue.length === 0) {
        tempValue = '';
      } else {
        tempValue = tempValue.join(',') + ',';
      }
    }
  } else {
    tempValue = val;
  }
  emit('update:value', tempValue);
  emit('update:modelValue', tempValue);
  // ElDatePicker(DateRange)有了这个事件才能更新显示
  emit('input', tempValue);
};
const onValueChange = (
  val: ANY_OBJECT | undefined,
  selectRow: ANY_OBJECT | undefined = undefined,
) => {
  let tempVal: ValueType | undefined = undefined;
  let dictData = null;
  tempVal = parseValue(val);
  if (val != null) {
    if (multiSelect.value) {
      dictData = val
        .map((item: string) => {
          if (pps.widget.widgetType === SysCustomWidgetType.Cascader) {
            if (Array.isArray(item)) {
              item = item[item.length - 1];
            }
            return findTreeNode(dictDataList.value, item, 'id', 'children');
          } else {
            return findItemFromList(dictDataList.value, item, 'id');
          }
        })
        .filter((item: ANY_OBJECT) => item != null);
    } else {
      if (pps.widget.widgetType === SysCustomWidgetType.Cascader) {
        if (Array.isArray(val)) {
          val = val[val.length - 1];
        }
        dictData = findTreeNode(dictDataList.value, val.toString(), 'id', 'children');
      } else {
        dictData = findItemFromList(dictDataList.value, val.toString(), 'id');
      }
    }
  }
  emit('change', tempVal, {
    dictData: dictData,
    selectRow: selectRow,
  });
};

const loadDropdownData = () => {
  if (pps.widget == null || !isDictWidget.value) {
    return;
  }
  dictDataList.value = [];
  setTimeout(() => {
    let dictInfo = (pps.widget.props.dictInfo || {}).dict;
    if (dictInfo && form().getDictDataList) {
      let dictCall;
      let params = form().getDropdownParams(pps.widget);
      // 如果是树形字典绑定在Select组件上，并且没有参数过滤，那么只返回顶层数据
      let rootDataWhenNoParentId = false;
      if (
        dictInfo.dictType === SysOnlineDictType.TABLE &&
        dictInfo.treeFlag &&
        pps.widget.widgetType === SysCustomWidgetType.Select &&
        Object.keys(params).length === 0
      ) {
        rootDataWhenNoParentId = true;
      }
      if (form().pageCode != null) {
        // 报表字典
        dictCall = form().getDictDataList(dictInfo, params, rootDataWhenNoParentId);
      } else {
        // 在线表单字典
        dictCall = form().getDictDataList(dictInfo, params, rootDataWhenNoParentId);
      }
      dictCall
        .then((res: ANY_OBJECT[]) => {
          res.forEach((item: ANY_OBJECT) => {
            item.id = item.id + '';
            if (item.parentId) item.parentId = item.parentId + '';
          });
          // 级联组件将列表转换成树
          if (pps.widget.widgetType === SysCustomWidgetType.Cascader) {
            res = treeDataTranslate(res, 'id', 'parentId');
          }
          if (pps.widget.eventInfo && pps.widget.eventInfo[OnlineFormEventType.DROPDOWN_CHANGE]) {
            res = pps.widget.eventInfo[OnlineFormEventType.DROPDOWN_CHANGE](res);
          }
          dictDataList.value = res;
        })
        .catch((e: Error) => {
          console.log(e);
        });
    }
  }, 30);
};

const instance = getCurrentInstance();

const getWidgetRef = (widgetName: string) => {
  const proxy = instance?.proxy;
  if (proxy) {
    return proxy.$refs[widgetName];
  } else {
    return null;
  }
};

const getHtml = () => {
  if (pps.widget.widgetType === SysCustomWidgetType.RichEditor) {
    const widgetRef = getWidgetRef(pps.widget.variableName);
    if (widgetRef && typeof widgetRef.getHtml === 'function') {
      return widgetRef.getHtml();
    }
  }
  return undefined;
};
const reset = () => {
  onValueInput(undefined);
  onValueChange(undefined);
  nextTick(() => {
    loadDropdownData();
  });
};
const refresh = () => {
  const widgetRef = getWidgetRef(pps.widget.variableName);
  if (widgetRef && typeof widgetRef.refresh === 'function') {
    widgetRef.refresh();
  }
};

defineExpose({
  getHtml,
  reset,
  refresh,
});

watch(
  () => pps.widget,
  () => {
    if (pps.widget) loadDropdownData();
  },
  {
    deep: true,
    immediate: true,
  },
);

// watch(
//   () => pps.widget.props.dictInfo,
//   () => {
//     if (pps.widget) loadDropdownData();
//   },
//   {
//     deep: true,
//     immediate: true,
//   },
// );

onMounted(() => {
  propsWidget.value.hasParent = parentWidget != null;
  propsWidget.value.widgetImpl = {
    getHtml,
    reset,
    refresh,
  };
});
</script>

<style scoped>
.van-vertical-item + .van-vertical-item {
  margin-top: 8px;
}
</style>
