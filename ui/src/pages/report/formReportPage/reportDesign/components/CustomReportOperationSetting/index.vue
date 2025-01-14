<template>
  <el-form
    :size="layoutStore.defaultFormItemSize"
    label-position="top"
    style="padding: 10px 16px"
    @submit.prevent
  >
    <MultiItemList
      label="操作"
      :data="data"
      addText="添加"
      :supportAdd="false"
      @add="onEditOperate()"
      @edit="onEditOperate"
      @delete="onDeleteOperate"
      :prop="{
        label: 'name',
        value: 'name',
        disabled: function (item:ANY_OBJECT) {
          return item.builtin;
        },
      }"
    >
      <template v-slot:right="scope">
        <template v-if="scope.data != null">
          <el-tag size="default" :type="scope.data?.enabled ? 'success' : 'danger'">
            {{ scope.data?.enabled ? '启动' : '禁用' }}
          </el-tag>
        </template>
      </template>
    </MultiItemList>
  </el-form>
</template>

<script setup lang="ts">
import { ElMessageBox } from 'element-plus';
import { ANY_OBJECT } from '@/types/generic';
import MultiItemList from '@/components/MultiItemList/index.vue';
import { SysCustomWidgetOperationType } from '@/common/staticDict/index';
import { DatasetType } from '@/common/staticDict/report';
import { Dialog } from '@/components/Dialog';
import { useLayoutStore } from '@/store';
import EditReportOperation from './editReportOperation.vue';
import EditRouteOperation from './editRouteOperation.vue';

const emit = defineEmits<{ 'update:value': [ANY_OBJECT[]] }>();
const props = withDefaults(defineProps<{ value?: ANY_OBJECT[]; groupList?: ANY_OBJECT[] }>(), {
  value: () => [],
  groupList: () => [],
});
const formConfig = inject('formConfig', () => {
  console.error('CustomReportOperationSetting: formConfig not injected');
  return {} as ANY_OBJECT;
});
const layoutStore = useLayoutStore();

//console.log('000000000000000000000000000000000000', props);
const data = computed({
  get() {
    console.log('data 0>>>', props.value);
    return props.value;
  },
  set(val) {
    emit('update:value', val);
  },
});

const onDeleteOperate = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除此操作？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      emit(
        'update:value',
        props.value.filter(item => item.id !== row.id),
      );
    })
    .catch(e => {
      console.warn(e);
    });
};
const handlerEditOperate = (row: ANY_OBJECT | null, res: ANY_OBJECT) => {
  let tempList: ANY_OBJECT[] = [];
  if (row == null) {
    res.id = new Date().getTime();
    tempList = props.value.concat(res);
  } else {
    tempList = props.value.map(item => {
      return item.id === res.id ? res : item;
    });
  }
  emit('update:value', tempList);
};
const getDrillColumnList = () => {
  let widget = formConfig().currentWidget;
  if (widget != null) {
    if (widget && widget.props && widget.props.datasetInfo) {
      let categroyColumnList = widget.props.datasetInfo.categroyColumnList;
      let valueColumnList = widget.props.datasetInfo.valueColumnList;
      return (categroyColumnList || []).concat(
        (valueColumnList || []).map((item: ANY_OBJECT) => {
          return {
            columnId: item.columnId,
            showName: item.showName,
          };
        }),
      );
    }
  }
  return [];
};
const getApiColumnTree = (widget: ANY_OBJECT) => {
  let datasetInfo = widget.props.datasetInfo;
  if (datasetInfo == null || datasetInfo.bindColumnId == null || datasetInfo.bindColumnId === '') {
    return [];
  }
  let columnList = widget.dataset.columnList || widget.columnList;
  // 临时Object字段列表，只有有子字段的Object字段，才会加入到最终的显示
  let tempObjectFieldMap = new Map();
  // 父字段列表，只有父字段在此列表中的字段，才会显示
  let parentColumnIdList: string[] = [];
  // 最终显示字段列表
  let tempList: ANY_OBJECT[] = [];
  (columnList || []).forEach((column: ANY_OBJECT) => {
    // 只有绑定字段下的非数组字段可以显示
    if (column.fieldType !== 'Array' || column.columnId === datasetInfo.bindColumnId) {
      if (
        parentColumnIdList.indexOf(column.parentId) !== -1 ||
        column.columnId === datasetInfo.bindColumnId
      ) {
        parentColumnIdList.push(column.columnId);
        if (column.fieldType !== 'Object') {
          tempList.push(column);
        } else {
          if (column.parentId === datasetInfo.bindColumnId) {
            // 父字段是绑定字段，放入显示列表
            tempList.push(column);
          } else {
            // 放入临时Object字段缓存，用于判断是否这个Object下有子字段
            tempObjectFieldMap.set(column.columnId, column);
          }
        }
        // 把父字段添加到显示列表
        let parentColumn = column;
        do {
          parentColumn = tempObjectFieldMap.get(parentColumn.parentId);
          if (parentColumn != null && tempList.indexOf(parentColumn) === -1) {
            tempList.push(parentColumn);
          }
        } while (parentColumn != null);
      }
    }
  });
  // 如果只有绑定字段，那么返回空
  return tempList.length > 1 ? tempList : [];
};
const getRouterBindColumnList = () => {
  let widget = formConfig().currentWidget;

  // console.log(
  //   'getRouterBindColumnList',
  //   widget,
  //   getDatasetType() === DatasetType.API,
  //   getApiColumnTree(widget),
  // );
  if (widget && widget.dataset) {
    if (getDatasetType() === DatasetType.API) {
      return getApiColumnTree(widget);
    } else {
      //return widget.dataset.columnList;
      return widget.columnList;
    }
  }
  return [];
};
const getDatasetType = () => {
  let widget = formConfig().currentWidget;
  if (widget && widget.dataset) {
    return widget.dataset.datasetType;
  }
  return null;
};
const onEditOperate = (row: ANY_OBJECT | null = null) => {
  // TODO 这个条件判断有问题
  console.log('onEditOperate', row);
  if (row && row.type === SysCustomWidgetOperationType.ROUTE) {
    Dialog.show<ANY_OBJECT>(
      row == null ? '添加操作' : '编辑操作',
      EditRouteOperation,
      {
        area: '1000px',
        offset: '50px',
      },
      {
        rowData: row,
        activeMode: formConfig().activeMode,
        columnList: getRouterBindColumnList()?.map((item: ANY_OBJECT) => {
          return {
            parentId: item.parentId,
            columnId: item.columnId,
            columnName: item.columnName,
          };
        }),
        isTreeColumn: getDatasetType() === DatasetType.API,
        path: 'thirdEditReportFormOperate',
      },
      {
        height: '600px',
        width: '1000px',
        pathName: '/thirdParty/thirdEditReportRouterOperate',
      },
    )
      .then(res => {
        handlerEditOperate(row, res);
      })
      .catch(e => {
        console.log(e);
      });
  } else {
    Dialog.show<ANY_OBJECT>(
      row == null ? '添加操作' : '编辑操作',
      EditReportOperation,
      {
        area: '1000px',
        offset: '50px',
      },
      {
        rowData: row,
        activeMode: formConfig().activeMode,
        groupList: props.groupList,
        formParamList: (formConfig().form.paramList || []).map(
          (item: ANY_OBJECT) => item.variableName,
        ),
        reportDictList: formConfig().dictList,
        widgetList: formConfig().allWidgetList.map((widget: ANY_OBJECT) => {
          return {
            id: widget.widgetId,
            showName: widget.showName,
          };
        }),
        formConfig: formConfig(),
        widgetDrillColumnList: getDrillColumnList(),
        path: 'thirdEditReportFormOperate',
      },
      {
        height: '600px',
        width: '1000px',
        pathName: '/thirdParty/thirdEditReportFormOperate',
      },
    )
      .then(res => {
        handlerEditOperate(row, res);
      })
      .catch(e => {
        console.log(e);
      });
  }
};
</script>
