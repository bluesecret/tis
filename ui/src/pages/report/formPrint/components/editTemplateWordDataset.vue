<template>
  <el-row
    class="form-single-fragment third-party-dlg edit-template-word-dataset"
    style="position: relative"
  >
    <el-col class="form-box">
      <el-form
        ref="form"
        label-width="70px"
        label-position="top"
        :model="formData"
        :rules="rules"
        :size="layoutStore.defaultFormItemSize"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="数据集" prop="dataset">
              <el-cascader
                style="width: 100%"
                v-model="formData.bindPath"
                :options="datasetTree"
                :props="{ value: 'id', label: 'name' }"
                @change="onDatasetChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否迭代">
              <el-select v-model="formData.loop" style="width: 100%">
                <el-option label="是" :value="true" />
                <el-option label="否" :value="false" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数据名称" prop="showName">
              <el-input v-model="formData.showName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数据标识" prop="variableName">
              <el-input v-model="formData.variableName" />
            </el-form-item>
          </el-col>
          <el-col
            :span="24"
            v-if="
              (bindDataset || {}).datasetType === DatasetType.SQL ||
              (bindDataset || {}).datasetType === DatasetType.API
            "
          >
            <MultiItemList
              label="数据集参数"
              :data="formDaata.datasetFilterParams"
              :disabled="bindDataset == null"
              @add="onSetDatasetParamList()"
              @edit="onSetDatasetParamList()"
              @delete="onRemoveDatasetParam"
              :prop="{
                label: 'paramName',
                value: 'paramName',
              }"
            >
              <template v-slot="scope">
                <span>{{ scope.data.paramName }}</span>
                <span style="margin: 0px 10px">等于</span>
                <span>{{ scope.data.paramValue }}</span>
              </template>
            </MultiItemList>
          </el-col>
          <el-col :span="24" v-if="(bindDataset || {}).datasetType !== DatasetType.API">
            <MultiItemList
              label="过滤器"
              :data="formData.filterParams"
              :disabled="bindDataset == null"
              @add="onEditFilter()"
              @edit="onEditFilter"
              @delete="onRemoveFilter"
              :prop="{
                label: 'paramName',
                value: 'paramId',
              }"
            >
              <template v-slot="scope">
                <span>{{ (scope.data || {}).paramName }}</span>
                <span style="margin: 0px 10px">{{
                  CriteriaFilterType.getValue((scope.data || {}).filterType)
                }}</span>
                <span>
                  {{
                    (scope.data || {}).filterValueType === FilterValueKind.INNER_VARIABLE
                      ? CustomDateValueType.getValue((scope.data || {}).paramValue)
                      : (scope.data || {}).paramValue
                  }}
                </span>
              </template>
            </MultiItemList>
          </el-col>
          <el-col :span="24" v-if="(bindDataset || {}).datasetType !== DatasetType.API">
            <MultiItemList
              label="排序"
              :data="formData.orderParam"
              :disabled="bindDataset == null"
              @add="onEditOrder()"
              @edit="onEditOrder"
              @delete="onRemoveOrder"
              :prop="{
                label: 'fieldName',
                value: 'fieldName',
              }"
            >
              <template v-slot:right="scope">
                <i
                  v-if="scope.data.asc"
                  class="el-icon-sort-up"
                  style="color: #878d9f; margin-right: 5px; font-size: 12px"
                >
                  正序
                </i>
                <i
                  v-if="!scope.data.asc"
                  class="el-icon-sort-down"
                  style="color: #878d9f; margin-right: 5px; font-size: 12px"
                >
                  倒序
                </i>
              </template>
            </MultiItemList>
          </el-col>
        </el-row>
      </el-form>
    </el-col>
    <el-col class="menu-box" :span="24" style="margin-top: 15px; flex-basis: 0">
      <el-row type="flex" justify="end">
        <el-button :size="layoutStore.defaultFormItemSize" :plain="true" @click="onCancel(false)">
          取消
        </el-button>
        <el-button type="primary" :size="layoutStore.defaultFormItemSize" @click="onSubmit()">
          确定
        </el-button>
      </el-row>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { ref, defineProps, computed, onMounted } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus';
import { DialogProp } from '@/components/Dialog/types';
import { useDialog } from '@/components/Dialog/useDialog';
import { ANY_OBJECT } from '@/types/generic';
import { findTreeNodeObjectPath } from '@/common/utils';
import MultiItemList from '@/components/MultiItemList/index.vue';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { ReportDatasetController } from '@/api/report';
import { useLayoutStore } from '@/store';
import {
  CriteriaFilterType,
  DatasetType,
  FilterValueKind,
  ReportRelationType,
  CustomDateValueType,
} from '@/common/staticDict/report';
import EditReportColumnOrder from '../../components/ReportColumnOrder/editReportColumnOrder.vue';
import EditReportColumnFilter from '../../components/ReportColumnFilter/editReportColumnFilter.vue';
import SetReportDatasetParam from '../../components/SetReportDatasetParam.vue';

const layoutStore = useLayoutStore();
const Dialog = useDialog();

interface IProps extends ThirdProps {
  printInfo?: ANY_OBJECT;
  rowData?: ANY_OBJECT;
  datasetTree: ANY_OBJECT[];
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT[]>;
}

const props = defineProps<IProps>();
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);

const dialogParams = computed(() => {
  return {
    printInfo: props.printInfo || thirdParams.value.printInfo,
    rowData: props.rowData || thirdParams.value.rowData,
    datasetTree: props.datasetTree || thirdParams.value.datasetTree || [],
  };
});

const formData = ref<ANY_OBJECT>({
  showName: '',
  variableName: '',
  bindPath: [],
  datasetId: undefined,
  loop: false,
  datasetFilterParams: [],
  filterParams: [],
  orderParam: [],
});

const rules = ref({
  showName: [{ required: true, message: '请输入数据名称', trigger: 'blur' }],
  variableName: [{ required: true, message: '请输入数据标识', trigger: 'blur' }],
  bindPath: [{ required: true, message: '请选择数据集', trigger: 'blur' }],
});

const bindDataset = computed(() => {
  if (formData.value.datasetId == null) return null;
  let path = findTreeNodeObjectPath(dialogParams.value.datasetTree, formData.value.datasetId);
  return path[path.length - 1];
});

const form = ref();

const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  } else {
    onCloseThirdDialog(false);
  }
};

const onSubmit = () => {
  form.value.validate(valid => {
    if (valid) {
      if (props.dialog) {
        props.dialog.submit(formData.value);
      } else {
        onCloseThirdDialog(false, dialogParams.value.rowData, formData.value);
      }
    }
  });
};

const onDatasetChange = value => {
  formData.value.datasetId = value[value.length - 1];
};

const loadDatasetColumnList = () => {
  return new Promise((resolve, reject) => {
    if (bindDataset.value == null) return resolve([]);
    if (Array.isArray(bindDataset.value.columnList) && bindDataset.value.columnList.length > 0) {
      resolve(bindDataset.value.columnList);
      return;
    }
    ReportDatasetController.view({
      datasetId: bindDataset.value.id,
    })
      .then(res => {
        res.data.columnList.forEach(item => {
          item.id = item.columnName;
          item.name = item.columnName;
        });
        bindDataset.value.columnList = res.data.columnList;
        resolve(res.data.columnList);
      })
      .catch(e => {
        reject(e);
      });
  });
};

const handlerSetDatasetParamList = res => {
  formData.value.datasetFilterParams = (res || []).map(item => {
    return {
      paramName: item.paramName,
      filterValueType: item.filterValueType,
      paramValue: item.paramValue,
    };
  });
};

const handlerEditFilter = (row: ANY_OBJECT | null, res: ANY_OBJECT) => {
  if (row == null) {
    // 新增过滤
    formData.value.filterParams.push({
      paramId: res.paramId,
      required: res.required,
      paramName: res.paramName,
      filterType: res.filterType,
      filterValueType: res.filterValueType,
      paramDictId: res.paramDictId,
      paramValue: res.paramValue,
      relationId: res.relationId,
    });
  } else {
    formData.value.filterParams = formData.value.filterParams.map(item => {
      if (item.paramId === res.paramId) {
        return {
          paramId: res.paramId,
          required: res.required,
          paramName: res.paramName,
          filterType: res.filterType,
          filterValueType: res.filterValueType,
          paramDictId: res.paramDictId,
          paramValue: res.paramValue,
          relationId: res.relationId,
        };
      } else {
        return item;
      }
    });
  }
};

const handlerEditOrder = (row: ANY_OBJECT | null, res: ANY_OBJECT) => {
  if (row == null) {
    formData.value.orderParam.push({
      ...res,
    });
  } else {
    formData.value.orderParam = formData.value.orderParam.map(item => {
      if (item.fieldName === row.fieldName) {
        return {
          ...res,
        };
      } else {
        return item;
      }
    });
  }
  formData.value.orderParam = formData.value.orderParam.sort((val1, val2) => {
    return val1.showOrder - val2.showOrder;
  });
};

const onSetDatasetParamList = () => {
  loadDatasetColumnList()
    .then(columnList => {
      return Dialog.show<ANY_OBJECT>(
        '数据集参数',
        SetReportDatasetParam,
        {
          area: ['900px', '650px'],
        },
        {
          datasetParamList: (bindDataset.value || {}).datasetParamList,
          datasetFilterParams: formData.value.datasetFilterParams,
          formParamList: props.printInfo.paramJson.map(item => {
            return item.variableName;
          }),
          validFilterValueType: [FilterValueKind.PRINT_INPUT_PARAM, FilterValueKind.INPUT_DATA],
          path: 'thirdSetReportDatasetParam',
        },
        {
          width: '900px',
          height: '700px',
          pathName: '/thirdParty/thirdSetReportDatasetParam',
        },
      );
    })
    .then(res => {
      handlerSetDatasetParamList(res);
    })
    .catch(e => {
      console.error(e);
    });
};

const onRemoveDatasetParam = row => {
  ElMessageBox.confirm('是否删除此数据集参数？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(res => {
      formData.value.datasetFilterParams = formData.value.datasetFilterParams.filter(item => {
        return item.paramName !== row.paramName;
      });
    })
    .catch(e => {
      console.error(e);
    });
};

const onEditFilter = row => {
  loadDatasetColumnList()
    .then(columnList => {
      return Dialog.show<ANY_OBJECT>(
        '过滤字段',
        EditReportColumnFilter,
        {
          area: ['600px'],
        },
        {
          rowData: row,
          columnList: columnList,
          formParamList: props.printInfo.paramJson.map(item => {
            return item.variableName;
          }),
          validFilterValueType: [
            FilterValueKind.DICT_DATA,
            FilterValueKind.COLUMN_DATA,
            FilterValueKind.PRINT_INPUT_PARAM,
            FilterValueKind.INNER_VARIABLE,
            FilterValueKind.INPUT_DATA,
          ],
          path: 'thirdEditReportColumnFilter',
        },
        {
          width: '600px',
          height: '500px',
          pathName: '/thirdParty/thirdEditReportColumnFilter',
        },
      );
    })
    .then(res => {
      handlerEditFilter(row, res);
    })
    .catch(e => {
      console.error(e);
    });
};

const onRemoveFilter = row => {
  ElMessageBox.confirm('是否删除此过滤条件？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(res => {
      formData.value.filterParams = formData.value.filterParams.filter(item => {
        return item !== row;
      });
    })
    .catch(e => {
      console.error(e);
    });
};

const onEditOrder = row => {
  loadDatasetColumnList()
    .then(columnList => {
      return Dialog.show<ANY_OBJECT>(
        '排序',
        EditReportColumnOrder,
        {
          area: ['600px'],
        },
        {
          rowData: row,
          columnList: columnList,
          usedColumnList: (formData.value.orderParam || [])
            .filter(item => {
              return row ? item.fieldName !== row.fieldName : true;
            })
            .map(item => item.fieldName),
          maxOrder: (formData.value.orderParam || []).reduce((maxOrder, item) => {
            return Math.max(maxOrder, item.showOrder || 0);
          }, 0),
          path: 'thirdEditReportColumnOrder',
        },
        {
          width: '600px',
          height: '500px',
          pathName: '/thirdParty/thirdEditReportColumnOrder',
        },
      );
    })
    .then(res => {
      handlerEditOrder(row, res);
    })
    .catch(e => {
      console.error(e);
    });
};

const onRemoveOrder = row => {
  ElMessageBox.confirm('是否删除此排序？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(res => {
      formData.value.orderParam = formData.value.orderParam.filter(item => {
        return item !== row;
      });
    })
    .catch(e => {
      console.error(e);
    });
};

onMounted(() => {
  if (props.rowData) {
    formData.value = {
      ...formData.value,
      ...props.rowData,
    };
  }
});
</script>

<style scoped>
.edit-template-word-dataset {
  height: 100%;
}

.edit-template-word-dataset /deep/ .el-form--label-top .el-form-item__label {
  padding-bottom: 0px;
}
</style>
