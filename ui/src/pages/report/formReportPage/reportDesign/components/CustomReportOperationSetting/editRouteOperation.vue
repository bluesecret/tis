<template>
  <div class="form-edit-report-operation third-party-dlg" style="position: relative">
    <div class="form-box">
      <el-form
        ref="form"
        :model="formData"
        class="full-width-input"
        :rules="rules"
        style="width: 100%"
        label-width="80px"
        :inline="true"
        :size="formItemSize"
        label-position="left"
        @submit.prevent
      >
        <el-form-item label="路由名称" prop="routeName">
          <el-input v-model="formData.routeName" clearable />
        </el-form-item>
        <el-form-item label="是否启用">
          <el-switch v-model="formData.enabled" />
        </el-form-item>
        <el-form-item style="float: right; text-align: right">
          <el-button type="success" :disabled="hasEditParam" @click="onAddRow()">
            添加参数
          </el-button>
          <el-button type="primary" :disabled="hasEditParam" @click="onSubmit()"> 保存 </el-button>
        </el-form-item>
      </el-form>
      <el-col :span="24" style="margin-bottom: 18px">
        <el-table
          :data="formData.routeParams"
          size="default"
          border
          height="400px"
          header-cell-class-name="table-header-gray"
        >
          <el-table-column label="参数名" prop="paramName" width="200px">
            <template v-slot="scope">
              <span v-if="!scope.row.isEdit">{{ scope.row.paramName }}</span>
              <el-input v-else class="table-input" size="default" v-model="scope.row.paramName" />
            </template>
          </el-table-column>
          <el-table-column label="参数值类型" prop="valueType" width="300px">
            <template v-slot="scope">
              <span v-if="!scope.row.isEdit">{{ getParamValueTypeName(scope.row.valueType) }}</span>
              <el-radio-group
                v-else
                v-model="scope.row.valueType"
                size="default"
                @change="onParamValueTypeChange(scope.row)"
              >
                <el-radio value="fixed">固定值</el-radio>
                <el-radio value="bind">字段绑定</el-radio>
              </el-radio-group>
            </template>
          </el-table-column>
          <el-table-column label="参数值">
            <template v-slot="scope">
              <span v-if="!scope.row.isEdit">{{ getParamValue(scope.row) }}</span>
              <template v-else>
                <el-input
                  v-if="scope.row.valueType === 'fixed'"
                  v-model="scope.row.paramValue"
                  size="default"
                />
                <template v-else-if="scope.row.valueType === 'bind'">
                  <el-select
                    v-if="!isTreeColumn"
                    size="default"
                    clearable
                    placeholder=""
                    v-model="scope.row.paramValue"
                  >
                    <el-option
                      v-for="item in columnTree"
                      :key="item.columnId"
                      :label="item.columnName"
                      :value="item.columnId"
                    />
                  </el-select>
                  <el-cascader
                    v-else
                    size="default"
                    placeholder=""
                    clearable
                    :options="columnList"
                    :value="getParamPath(scope.row.paramValue)"
                    @input="(val:string) => onParamValueChange(scope.row, val)"
                    :props="{ label: 'columnName', value: 'columnId' }"
                  />
                </template>
              </template>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="110px" align="center">
            <template v-slot="scope">
              <el-button
                v-if="!scope.row.isEdit"
                link
                :disabled="(editRow || {}).id === scope.row.id"
                @click="onEditRow(scope.row)"
              >
                编辑
              </el-button>
              <el-button
                v-if="!scope.row.isEdit"
                link
                :disabled="(editRow || {}).id === scope.row.id"
                @click="onDeleteRow(scope.row)"
              >
                删除
              </el-button>
              <el-button v-if="scope.row.isEdit" link @click="onSaveRow(scope.row)">
                保存
              </el-button>
              <el-button v-if="scope.row.isEdit" link @click="onCancelRow(scope.row)">
                取消
              </el-button>
            </template>
          </el-table-column>
          <template v-slot:empty>
            <div class="table-empty unified-font">
              <img src="@/assets/img/empty.png" />
              <span>暂无数据</span>
            </div>
          </template>
        </el-table>
      </el-col>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ElMessageBox, ElMessage } from 'element-plus';
import { treeDataTranslate, findTreeNodeObjectPath, findTreeNodePath } from '@/common/utils';
import { DialogProp } from '@/components/Dialog/types';
import { ANY_OBJECT } from '@/types/generic';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

interface IProps extends ThirdProps {
  rowData?: ANY_OBJECT;
  activeMode?: string;
  operationItem?: ANY_OBJECT;
  isTreeColumn?: boolean;
  columnList?: ANY_OBJECT[];
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT>;
}
const props = defineProps<IProps>();
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize || thirdParams.value.defaultFormItemSize?.value;
});

const form = ref();
const formData = ref<ANY_OBJECT>({
  routeName: undefined,
  enabled: false,
  routeParams: [],
});
const editRow = ref<ANY_OBJECT>();
const rules = {
  routeName: [{ required: true, message: '路由名称不能为空', trigger: 'blur' }],
};
const hasEditParam = computed(() => {
  return (formData.value.routeParams || []).filter((item: ANY_OBJECT) => item.isEdit).length > 0;
});
const dialogParams = computed(() => {
  return {
    rowData: props.rowData || thirdParams.value.rowData,
    activeMode: props.activeMode || thirdParams.value.activeMode,
    columnList: props.columnList || thirdParams.value.columnList || [],
    isTreeColumn: props.isTreeColumn || thirdParams.value.isTreeColumn,
  };
});
const columnTree = computed(() => {
  return props.isTreeColumn
    ? treeDataTranslate(dialogParams.value.columnList, 'columnId')
    : dialogParams.value.columnList;
});

const onAddRow = () => {
  formData.value.routeParams.push({
    id: new Date().getTime(),
    paramName: undefined,
    valueType: 'fixed',
    paramValue: undefined,
    isNew: true,
    isEdit: true,
  });
};
const onEditRow = (row: ANY_OBJECT) => {
  editRow.value = {
    ...row,
  };
  row.isEdit = true;
};
const onDeleteRow = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除此参数？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      formData.value.routeParams = formData.value.routeParams.filter((item: ANY_OBJECT) => {
        return item !== row;
      });
    })
    .catch(e => {
      console.warn(e);
    });
};
const onCancelRow = (row: ANY_OBJECT) => {
  if (row.isNew) {
    // 取消新建
    formData.value.routeParams = formData.value.routeParams.filter((item: ANY_OBJECT) => {
      return item !== row;
    });
  } else {
    // 取消编辑
    formData.value.routeParams = formData.value.routeParams.map((item: ANY_OBJECT) => {
      return item === row ? editRow.value : item;
    });
  }
};
const onSaveRow = (row: ANY_OBJECT) => {
  if (row.paramName == null || row.paramName === '') {
    ElMessage.error('参数名不能为空！');
    return;
  }
  if (row.paramValue == null || row.paramValue === '') {
    ElMessage.error('参数值不能为空！');
    return;
  }
  row.isEdit = false;
  row.isNew = false;
  formData.value.routeParams = [...formData.value.routeParams];
};
const getParamPath = (val: string) => {
  return findTreeNodePath(columnTree.value, val, 'columnId');
};
const onParamValueChange = (row: ANY_OBJECT, val: string) => {
  row.paramValue = Array.isArray(val) ? val[val.length - 1] : undefined;
};
const onParamValueTypeChange = (row: ANY_OBJECT) => {
  row.paramValue = undefined;
};
const getParamValue = (row: ANY_OBJECT) => {
  if (row.valueType === 'bind') {
    let path = findTreeNodeObjectPath(columnTree.value, row.paramValue, 'columnId');
    if (Array.isArray(path) && path.length > 0) {
      return path.map(item => item.columnName).join(' / ');
    }
  } else {
    return row.paramValue;
  }
};
const getParamValueTypeName = (valueType: string) => {
  return valueType === 'fixed' ? '固定值' : '绑定字段';
};
const onSubmit = () => {
  form.value.validate((valid: boolean) => {
    if (!valid) return;
    const data = {
      ...formData.value,
      routeParams: formData.value.routeParams.map((item: ANY_OBJECT) => {
        return {
          ...item,
          isEdit: undefined,
          isNew: undefined,
        };
      }),
    };
    if (props.dialog) {
      props.dialog.submit(data);
    } else {
      onCloseThirdDialog(false, dialogParams.value.rowData, data);
    }
  });
};

onMounted(() => {
  if (dialogParams.value.rowData) {
    formData.value = {
      ...dialogParams.value.rowData,
      routeParams: (dialogParams.value.rowData.routeParams || []).map((item: ANY_OBJECT) => {
        return {
          ...item,
          isEdit: false,
          isNew: false,
        };
      }),
    };
  }
});
</script>
