<template>
  <div>
    <MultiItemList
      v-model:data="data"
      :disabled="!data"
      :supportSort="true"
      :maxCount="1"
      :label="label"
      @add="onEditColumn()"
      @edit="onEditColumn"
      @delete="
        row => {
          onRemoveSelectedColumn(row, value);
        }
      "
      :prop="{
        label: function (item:ANY_OBJECT) {
          if (Array.isArray(item.columnName)) {
            return item.columnName.join(' / ');
          } else {
            return item.columnName;
          }
        },
        value: 'columnId',
      }"
    ></MultiItemList>
  </div>
</template>

<script setup lang="ts">
import { ElMessageBox } from 'element-plus';
import MultiItemList from '@/components/MultiItemList/index.vue';
import { ANY_OBJECT } from '@/types/generic';
import { Dialog } from '@/components/Dialog';
import { useLayoutStore } from '@/store';
import EditColumnBind from './editColumnBind.vue';

const props = defineProps<{
  value?: ANY_OBJECT | ANY_OBJECT[];
  label: string;
  dataset?: ANY_OBJECT;
  validColumnList?: ANY_OBJECT[];
  fieldKey: string;
}>();

const emit = defineEmits<{ 'update:value': [ANY_OBJECT | ANY_OBJECT[] | undefined] }>();
const layoutStore = useLayoutStore();

const data = computed({
  get() {
    return props.value;
  },
  set(val) {
    emit('update:value', val);
  },
});

const onEditColumn = (row: ANY_OBJECT | null = null) => {
  if (!props.value || !Array.isArray(props.validColumnList)) {
    return;
  }
  Dialog.show<ANY_OBJECT>(
    '内容字段',
    EditColumnBind,
    {
      area: ['600px'],
    },
    {
      dataset: props.dataset,
      rowData: row,
      columnList: Array.isArray(props.value) ? buildColumnList(props.value) : [props.value],
      path: 'editPropertyFieldBind/' + props.fieldKey,
    },
    {
      width: '600px',
      height: '500px',
      pathName: '/thirdParty/thirdEditWidgetColumnBind',
    },
  )
    .then(res => {
      if (Array.isArray(props.value)) {
        res = {
          columnId: res.columnId,
          columnName: res.columnName,
        };
        emit('update:value', [res]);
      } else {
        emit('update:value', res);
      }
    })
    .catch(e => {
      console.log(e);
    });
};
const onRemoveSelectedColumn = (
  row: ANY_OBJECT,
  columnList: ANY_OBJECT | ANY_OBJECT[] | undefined,
) => {
  if (!columnList || !Array.isArray(columnList)) {
    console.warn('columnList is not Array');
    return;
  }
  ElMessageBox.confirm('是否删除当前列？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      let delIndex = columnList.findIndex(x => x.columnId === row.columnId);
      columnList.splice(delIndex, 1);
    })
    .catch(e => {
      console.warn(e);
    });
};
const buildColumnList = (columnList: ANY_OBJECT[]) => {
  //console.log('============================ validColumnList', props.validColumnList);
  return props.validColumnList
    ?.map((item: ANY_OBJECT) => {
      let disabled = item.fieldType !== 'Object' && item.fieldType !== 'Array';
      disabled = disabled && columnList.map(item => item.columnId).indexOf(item.columnId) !== -1;
      return {
        ...item,
        disabled: disabled,
      };
    })
    .sort((val1: ANY_OBJECT, val2: ANY_OBJECT) => {
      return val1.disabled ? (val2.disabled ? 0 : 1) : -1;
    });
};
</script>
