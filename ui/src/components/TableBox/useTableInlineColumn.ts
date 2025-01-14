import { Ref, computed, inject } from 'vue';
import { DictData } from '@/common/staticDict/types';
import { ANY_OBJECT } from '@/types/generic';
import { InlineTableEditConifg } from './types';

const useTableInlineColumn = <T, R>(
  multiple: boolean,
  emit: (event: string, ...args: unknown[]) => void,
  inputWidget: Ref,
  getFieldValue?: (value: ANY_OBJECT) => ANY_OBJECT | undefined,
  getFieldDictData?: (value: ANY_OBJECT) => DictData | Array<DictData> | undefined,
) => {
  const attrs = useAttrs();
  const inlineEditConfig: InlineTableEditConifg<T, R> | undefined = inject('inlineEditConfig');
  const editConfig = computed(() => {
    return inlineEditConfig;
  });
  // 获取组件绑定的数据
  const getRowData = computed(() => {
    const row: T | undefined = (editConfig.value || {}).rowCopy;
    if (row == null || attrs.field == null) {
      return undefined;
    }
    const path = (attrs.field as string).split('.');
    let temp = row;
    for (let i = 0; i < path.length; i++) {
      temp = temp[path[i]];
      if (temp == null) break;
    }
    return temp;
  });
  // 是否在编辑状态
  const isEdit = (row: T) => {
    return editConfig.value && editConfig.value.rowData === row;
  };
  const reset = () => {
    // 重置组件数据
  };
  // 更新组件数据
  const setRowData = (value?: ANY_OBJECT, dictData?: DictData | Array<DictData>) => {
    if (attrs.field != null && attrs.field !== '' && editConfig.value != null) {
      editConfig.value.isDirty = true;
      const path = (attrs.field as string).split('.');
      let temp = editConfig.value.rowCopy;
      for (let i = 0; i < path.length; i++) {
        const key: string = path[i];
        if (i !== path.length - 1) {
          if (temp[key] == null) {
            temp[key] = {};
          }
        } else {
          temp[key] = value;
          if (multiple) {
            temp[key + 'DictMapList'] = dictData;
          } else {
            temp[key + 'DictMap'] = dictData;
          }
        }
        temp = temp[key];
      }
    }
  };
  // 获取行字段数据
  const getValue = (value: ANY_OBJECT): ANY_OBJECT | undefined => {
    return value;
  };
  // 如果绑定了字典，获取字典数据
  const getDictData = (value: ANY_OBJECT): DictData | Array<DictData> | undefined => {
    return undefined;
  };
  // 当组件数据发生变化时
  const onChange = (value: ANY_OBJECT) => {
    const finalValue: ANY_OBJECT | undefined = getFieldValue
      ? getFieldValue(value)
      : getValue(value);
    const dictData: DictData | Array<DictData> | undefined = getFieldDictData
      ? getFieldDictData(value)
      : getDictData(value);
    setRowData(finalValue, dictData);
    emit('change', finalValue, editConfig.value?.rowCopy);
  };
  // 获取焦点
  const focus = () => {
    if (inputWidget.value) inputWidget.value.focus();
  };
  // 获取数据
  const getRowValue = (row: T) => {
    const path = (attrs.field as string).split('.');
    let temp = row;
    for (let i = 0; i < path.length; i++) {
      temp = temp[path[i]];
      if (temp == null) break;
    }
    return temp;
  };

  return {
    editConfig,
    isEdit,
    getRowData,
    getRowValue,
    reset,
    setRowData,
    onChange,
    focus,
  };
};

export default useTableInlineColumn;
