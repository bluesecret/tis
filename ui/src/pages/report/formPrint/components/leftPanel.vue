<template>
  <div class="left-panel">
    <el-card
      class="base-card"
      shadow="never"
      :body-style="{ padding: '0px' }"
      style="border: none; flex-grow: 1"
    >
      <template #header>
        <div class="base-card-header">
          <el-row
            type="flex"
            justify="space-between"
            align="middle"
            style="flex-wrap: nowrap; width: 100%"
          >
            <el-select
              v-model="currentFragmentId"
              style="width: 100%; margin-right: 10px"
              size="default"
              placeholder="请选择段落"
              :disabled="isCreateFragment"
            >
              <el-option
                v-for="fragment in printInfo.fragmentJson"
                :key="fragment.fragmentId"
                :label="fragment.showName"
                :value="fragment.fragmentId"
              />
            </el-select>
            <el-button
              v-if="!isCreateFragment"
              size="default"
              :icon="Plus"
              circle
              title="新建"
              @click="onAddFragment"
            />
            <el-button
              v-if="!isCreateFragment"
              size="default"
              :icon="Document"
              circle
              title="编辑"
              @click="isCreateFragment = true"
            />
            <el-button
              v-if="!isCreateFragment"
              size="default"
              :icon="Delete"
              circle
              title="删除"
              @click="onDeleteCurrentFragment"
            />
            <el-button
              v-if="isCreateFragment"
              size="default"
              :icon="Check"
              circle
              title="保存"
              @click="onSaveCurrentFragment"
            />
            <el-button
              v-if="isCreateFragment"
              size="default"
              :icon="Close"
              circle
              title="取消"
              @click="onCancelCreateFragment"
            />
          </el-row>
        </div>
      </template>
      <el-row class="edit-box" style="width: 100%; margin-right: 10px">
        <el-col :span="24" style="padding: 16px">
          <!-- 段落设置 -->
          <el-form
            class="chart-attribute"
            label-position="top"
            label-width="70px"
            :disabled="!isCreateFragment"
            size="default"
            @submit.prevent
          >
            <el-scrollbar
              :style="{ height: 'calc(100vh - 200px)', width: '100%' }"
              class="custom-scroll"
            >
              <el-row :gutter="10">
                <el-col :span="24">
                  <el-form-item label="段落名称">
                    <el-input v-model="currentFragment.showName" />
                  </el-form-item>
                </el-col>
                <el-col :span="24">
                  <el-form-item label="是否迭代">
                    <el-switch
                      v-model="currentFragment.loop"
                      :disabled="currentFragment.fragmentId != null"
                      @change="onLoopChange"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="24">
                  <el-form-item label="数据分组">
                    <el-cascader
                      v-model="currentFragment.bindGroupPath"
                      :key="'groud_' + currentFragment.fragmentId"
                      style="width: 100%"
                      :disabled="currentFragment.fragmentId != null"
                      :options="getGroupTree"
                      :props="bindDataProps"
                      @change="onDatasetGroupChange"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="24">
                  <el-form-item label="绑定数据">
                    <el-cascader
                      v-model="currentFragment.bindPath"
                      :key="'data_' + currentFragment.fragmentId"
                      style="width: 100%"
                      :disabled="currentFragment.fragmentId != null"
                      :options="getDatasetRelationTree"
                      :props="bindDataProps"
                      @change="onBindFragmentDataChange"
                    />
                  </el-form-item>
                </el-col>
                <el-col
                  :span="24"
                  v-if="
                    (currentFragmentDataset || {}).datasetType === DatasetType.SQL ||
                    (currentFragmentDataset || {}).datasetType === DatasetType.API
                  "
                >
                  <MultiItemList
                    label="数据集参数"
                    :data="currentFragment.datasetFilterParams"
                    :disabled="!isCreateFragment"
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
                      <span style="margin: 0 10px">等于</span>
                      <span>{{ scope.data.paramValue }}</span>
                    </template>
                  </MultiItemList>
                </el-col>
                <!-- 循环段落绑定API结果集的Array字段 -->
                <el-col
                  :span="24"
                  v-if="
                    (currentFragmentDataset || {}).datasetType === DatasetType.API &&
                    (currentFragment || {}).loop
                  "
                >
                  <el-form-item label="绑定字段">
                    <el-cascader
                      v-model="currentFragment.bindColumnPath"
                      :key="'column_' + currentFragment.fragmentId"
                      style="width: 100%"
                      :options="getLoopFragmentColumnTree"
                      :props="bindColumnProps"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="起始行">
                    <el-input-number
                      v-model="currentFragment.startRow"
                      controls-position="right"
                      :min="1"
                      style="width: 100%"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="行跨度">
                    <el-input-number
                      v-model="currentFragment.rowSpan"
                      controls-position="right"
                      :min="1"
                      style="width: 100%"
                    />
                  </el-form-item>
                </el-col>
                <el-col
                  :span="24"
                  v-if="(currentFragmentDataset || {}).datasetType !== DatasetType.API"
                >
                  <MultiItemList
                    label="过滤器"
                    :data="currentFragment.filterParams"
                    :disabled="!isCreateFragment"
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
                      <span style="margin: 0 10px">{{
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
                <el-col
                  :span="24"
                  v-if="(currentFragmentDataset || {}).datasetType !== DatasetType.API"
                >
                  <MultiItemList
                    label="排序"
                    :data="currentFragment.orderParam"
                    :disabled="!isCreateFragment"
                    @add="onEditOrder()"
                    @edit="onEditOrder"
                    @delete="onRemoveOrder"
                    :prop="{
                      label: 'fieldName',
                      value: 'fieldName',
                    }"
                  >
                    <template v-slot:right="scope">
                      <el-row
                        v-if="scope.data.asc"
                        type="flex"
                        align="middle"
                        style="margin-right: 5px; font-size: 12px; color: #878d9f"
                      >
                        <el-icon><SortUp /></el-icon>
                        <span>正序</span>
                      </el-row>
                      <el-row
                        v-if="!scope.data.asc"
                        type="flex"
                        align="middle"
                        style="margin-right: 5px; font-size: 12px; color: #878d9f"
                      >
                        <el-icon><SortDown /></el-icon>
                        <span>倒序</span>
                      </el-row>
                    </template>
                  </MultiItemList>
                </el-col>
              </el-row>
            </el-scrollbar>
          </el-form>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { SortUp, SortDown, Plus, Delete, Close, Check, Document } from '@element-plus/icons-vue';
import { ElMessageBox, ElMessage } from 'element-plus';
import MultiItemList from '@/components/MultiItemList/index.vue';
import { findItemFromList, treeDataTranslate } from '@/common/utils';
import { ANY_OBJECT } from '@/types/generic';
import {
  CriteriaFilterType,
  DatasetType,
  FilterValueKind,
  ReportRelationType,
  CustomDateValueType,
} from '@/common/staticDict/report';
import { useDialog } from '@/components/Dialog/useDialog';
import SetReportDatasetParam from '../../components/SetReportDatasetParam.vue';
import EditReportColumnFilter from '../../components/ReportColumnFilter/editReportColumnFilter.vue';
import EditReportColumnOrder from '../../components/ReportColumnOrder/editReportColumnOrder.vue';
import { buildColumnTree, getFragmentFilterAndOrderColumn, loadDatasetColumnList } from './utils';

const Dialog = useDialog();
const props = withDefaults(
  defineProps<{
    printInfo: ANY_OBJECT;
    xs: ANY_OBJECT;
    datasetGroupList: ANY_OBJECT[];
    datasetList: ANY_OBJECT[];
    datasetRelationList: ANY_OBJECT[];
  }>(),
  {
    xs: () => {
      return {};
    },
    datasetGroupList: () => [],
    datasetList: () => [],
    datasetRelationList: () => [],
  },
);

// 是否新建段落
const isCreateFragment = ref(false);
// 当前选中段落ID
const currentFragmentId = ref<string>();
// 当前选中/新建段落的复制
const currentFragment = ref<ANY_OBJECT>({});
const bindDataProps = {
  value: 'id',
  label: 'name',
  disabled: 'disabled',
  checkStrictly: true,
};
const bindColumnProps = {
  value: 'columnName',
  label: 'name',
  disabled: 'disabled',
};

/**
 * 数据集分组
 */
const getGroupTree = computed(() => {
  return treeDataTranslate(
    props.datasetGroupList.map((item: ANY_OBJECT) => {
      return {
        ...item,
      };
    }),
    'id',
    'parentId',
  );
});
/**
 * 当前段落绑定数据转换成树形结构
 */
const getDatasetRelationTree = computed(() => {
  return treeDataTranslate(
    getDatasetRelation.value.map((item: ANY_OBJECT) => {
      return {
        ...item,
      };
    }),
    'id',
    'parentId',
  );
});
/**
 * 获取当前段落可绑定的数据（非循环段落只能绑定到数据集，循环段落可以绑定到数据集以及一对多关联上）
 */
const getDatasetRelation = computed(() => {
  if (getValidDatasetList.value.length <= 0) return [];
  if (currentFragment.value && !currentFragment.value.loop) {
    // 不循环迭代的段落，只能绑定数据集
    return getValidDatasetList.value;
  } else {
    // 循环迭代的段落，可以绑定数据集或者数据集下一对多关联
    let datasetIdSet = new Set();
    getValidDatasetList.value.forEach((item: ANY_OBJECT) => {
      datasetIdSet.add(item.datasetId);
    });
    return getValidDatasetList.value.concat(
      props.datasetRelationList.filter((item: ANY_OBJECT) => {
        item.parentId = item.masterDatasetId;
        return (
          datasetIdSet.has(item.masterDatasetId) &&
          item.relationType === ReportRelationType.ONE_TO_MANY
        );
      }),
    );
  }
});
/**
 * 当前段落绑定API数据集字段选择（只有循环段落可以选其中的Array字段）
 */
const getLoopFragmentColumnTree = computed(() => {
  return buildColumnTree(currentFragmentDataset.value?.columnList, 'Array', false);
});
/**
 * 根据选择的数据集分组，计算可用的数据集
 */
const getValidDatasetList = computed(() => {
  if (
    currentFragment.value == null ||
    !Array.isArray(currentFragment.value.bindGroupPath) ||
    currentFragment.value.bindGroupPath.length <= 0
  )
    return [];
  let selectGroupId =
    currentFragment.value.bindGroupPath[currentFragment.value.bindGroupPath.length - 1];
  return (props.datasetList || []).filter((item: ANY_OBJECT) => {
    // return item.groupId === selectGroupId && item.datasetType !== this.DatasetType.API;
    return item.groupId === selectGroupId;
  });
});
/**
 * 当前段落绑定的数据集
 */
const currentFragmentDataset = computed(() => {
  if (
    currentFragment.value == null ||
    currentFragment.value.datasetId == null ||
    currentFragment.value.datasetId === ''
  )
    return null;
  return findItemFromList(getDatasetRelation.value, currentFragment.value.datasetId, 'id');
});
/**
 * 当前段落绑定的关联
 */
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const currentFragmentRelation = computed(() => {
  if (currentFragmentDataset.value == null) return null;
  if (
    currentFragment.value &&
    currentFragment.value.loop &&
    currentFragment.value.relationId != null &&
    currentFragment.value.relationId !== ''
  ) {
    return findItemFromList(getDatasetRelation.value, currentFragment.value.relationId, 'id');
  } else {
    return null;
  }
});

onMounted(() => {
  // 获取数据集以及数据关联信息
  loadAllDatasetInfo();
});
/**
 * 获取数据集以及关联数据
 */
const loadAllDatasetInfo = () => {
  // ReportDatasetGroupController.listAll({}).then(res => {
  //   console.log(res, '=========in old leftpanel')
  //   props.datasetGroupList = (res.data.reportDatasetGroupList || []).map((item:ANY_OBJECT) => {
  //     return {
  //       ...item,
  //       id: item.groupId,
  //       name: item.groupName
  //     }
  //   });
  //   props.datasetList = (res.data.reportDatasetList || []).map((item:ANY_OBJECT) => {
  //     try {
  //       let info = JSON.parse(item.datasetInfo);
  //       item.datasetParamList = info.paramList;
  //     } catch (e) {
  //       item.datasetParamList = [];
  //     }
  //     return {
  //       ...item,
  //       id: item.datasetId,
  //       name: item.datasetName,
  //       columnList: undefined
  //     }
  //   });
  //   props.datasetRelationList = (res.data.reportDatasetRelationList || []).map((item:ANY_OBJECT) => {
  //     return {
  //       ...item,
  //       id: item.relationId,
  //       name: item.variableName,
  //       columnList: undefined
  //     }
  //   });
  // TODO 必要时加watch
  if (Array.isArray(props.printInfo.fragmentJson) && props.printInfo.fragmentJson.length > 0) {
    currentFragmentId.value = props.printInfo.fragmentJson[0].fragmentId;
  }
  // }).catch(e=>{console.warn(e)});
};
const handlerSetDatasetParamList = (res: ANY_OBJECT) => {
  if (!currentFragment.value) return {};
  currentFragment.value.datasetFilterParams = (res || []).map((item: ANY_OBJECT) => {
    return {
      paramName: item.paramName,
      filterValueType: item.filterValueType,
      paramValue: item.paramValue,
    };
  });
};
const handlerEditFilter = (row: ANY_OBJECT | null, res: ANY_OBJECT) => {
  if (!currentFragment.value) {
    console.warn('currentFragment.value is null');
    return;
  }
  if (row == null) {
    // 新增过滤
    currentFragment.value.filterParams.push({
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
    currentFragment.value.filterParams = currentFragment.value.filterParams.map(
      (item: ANY_OBJECT) => {
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
      },
    );
  }
};
const handlerEditOrder = (row: ANY_OBJECT | null, res: ANY_OBJECT) => {
  if (!currentFragment.value) {
    console.warn('currentFragment.value is null');
    return;
  }
  if (!res) {
    console.warn('res is null or undefined. may be callback with thirdparty by cacel');
    return;
  }
  if (row == null) {
    currentFragment.value.orderParam.push({
      ...res,
    });
  } else {
    currentFragment.value.orderParam = currentFragment.value.orderParam.map((item: ANY_OBJECT) => {
      if (item.fieldName === row.fieldName) {
        return {
          ...res,
        };
      } else {
        return item;
      }
    });
  }
  currentFragment.value.orderParam = currentFragment.value.orderParam.sort(
    (val1: ANY_OBJECT, val2: ANY_OBJECT) => {
      return val1.showOrder - val2.showOrder;
    },
  );
};
/**
 * 新建段落
 */
const onAddFragment = () => {
  currentFragment.value = {
    fragmentId: undefined,
    showName: undefined,
    loop: false,
    bindGroupPath: [],
    bindPath: [],
    bindColumnPath: [],
    datasetId: undefined,
    relationId: undefined,
    startRow: undefined,
    rowSpan: undefined,
    filterParams: [],
    orderParam: [],
    datasetFilterParams: [],
  };
  isCreateFragment.value = true;
};
/**
 * 保存当前片段
 */
const onSaveCurrentFragment = () => {
  let isNew = currentFragment.value?.fragmentId == null;
  if (!checkOnSaveFragment(currentFragment.value, isNew)) return;
  if (checkFragmentRowRang(currentFragment.value)) {
    ElMessageBox.confirm(
      '段落占据行区域变化会导致失效的区域中单元格绑定的数据被清空，是否继续保存？',
      '',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    )
      .then(() => {
        saveCurrentFragmentImpl(true);
      })
      .catch(e => {
        console.warn(e);
      });
  } else {
    saveCurrentFragmentImpl();
  }
};
/**
 * 当前段落是否迭代改变
 */
const onLoopChange = () => {
  if (currentFragment.value) {
    currentFragment.value.bindGroupPath = [];
  }
  onDatasetGroupChange();
};
/**
 * 段落绑定的数据集分组变化
 */
const onDatasetGroupChange = () => {
  if (currentFragment.value) {
    currentFragment.value.bindPath = [];
    currentFragment.value.bindColumnPath = [];
  }
  onBindFragmentDataChange();
};
/**
 * 段落绑定的数据变化
 */
const onBindFragmentDataChange = () => {
  if (currentFragment.value) {
    currentFragment.value.datasetId = currentFragment.value.bindPath[0];
    currentFragment.value.relationId = currentFragment.value.bindPath[1];
    currentFragment.value.bindColumnPath = [];
    currentFragment.value.orderParam = [];
    currentFragment.value.filterParams = [];
  }
};
/**
 * 保存当前段落信息
 * needClearCellValue：是否需要清空失效的单元格数据
 */
const saveCurrentFragmentImpl = (needClearCellValue = false) => {
  let isNew = currentFragment.value?.fragmentId == null;
  // 清除失效单元格数据
  if (needClearCellValue && !isNew) {
    let invalidRange = clearFragmentInvalidCell(currentFragment.value);
    if (Array.isArray(invalidRange)) {
      invalidRange.forEach(range => {
        props.xs.setRangeShow(range);
        props.xs.clearRange();
      });
    }
  }
  const printInfo = props.printInfo;
  if (isNew) {
    if (!Array.isArray(printInfo.fragmentJson)) printInfo.fragmentJson = [];
    if (currentFragment.value) {
      currentFragment.value.fragmentId = new Date().getTime();
      printInfo.fragmentJson.push(cloneFragment(currentFragment.value));
      currentFragmentId.value = currentFragment.value.fragmentId;
    }
  } else {
    printInfo.fragmentJson = props.printInfo.fragmentJson.map((item: ANY_OBJECT) => {
      if (currentFragment.value && item.fragmentId === currentFragment.value.fragmentId) {
        return cloneFragment(currentFragment.value);
      } else {
        return item;
      }
    });
  }
  isCreateFragment.value = false;
  ElMessage.success('段落保存成功');
};
/**
 * 删除段落
 */
const onDeleteCurrentFragment = () => {
  ElMessageBox.confirm('删除段落会清空所有段落内部单元格，是否删除此段落？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      const printInfo = props.printInfo;
      printInfo.fragmentJson = props.printInfo.fragmentJson.filter((item: ANY_OBJECT) => {
        return item.fragmentId !== currentFragment.value?.fragmentId;
      });
      let range = getFragmentRange(currentFragment.value);
      props.xs.setRangeShow(range);
      props.xs.clearRange();
      currentFragmentId.value = undefined;
    })
    .catch((e: Error) => {
      console.warn(e);
    });
};
/**
 * 取消新建段落
 */
const onCancelCreateFragment = () => {
  isCreateFragment.value = false;
  let temp = findItemFromList(props.printInfo.fragmentJson, currentFragmentId.value, 'fragmentId');
  currentFragment.value = temp == null ? {} : cloneFragment(temp) || {};
};
/**
 * 复制段落
 */
const cloneFragment = (fragment: ANY_OBJECT) => {
  return fragment == null
    ? null
    : {
        ...fragment,
        orderParam: (fragment.orderParam || []).map((item: ANY_OBJECT) => {
          return {
            ...item,
          };
        }),
        filterParams: (fragment.filterParams || []).map((item: ANY_OBJECT) => {
          return {
            ...item,
          };
        }),
        datasetFilterParams: (fragment.datasetFilterParams || []).map((item: ANY_OBJECT) => {
          return {
            ...item,
          };
        }),
      };
};
/**
 * 检测段落占用行区域变化是否会导致绑定的单元格字段失效
 */
const checkFragmentRowRang = (fragment: ANY_OBJECT | null) => {
  if (fragment && Array.isArray(props.printInfo.fragmentJson)) {
    let oldFragment = findItemFromList(
      props.printInfo.fragmentJson,
      fragment.fragmentId,
      'fragmentId',
    );
    if (oldFragment != null) {
      let startRow = fragment.startRow;
      let endRow = fragment.startRow + fragment.rowSpan - 1;
      let oldStartRow = oldFragment.startRow;
      let oldEndRow = oldFragment.startRow + oldFragment.rowSpan - 1;
      if (startRow > oldStartRow || endRow < oldEndRow) {
        return true;
      }
    }
  }

  return false;
};
/**
 * 当段落起始行以及行跨度改变后，获取无效的行列表
 */
const clearFragmentInvalidCell = (fragment: ANY_OBJECT | null) => {
  if (fragment && Array.isArray(props.printInfo.fragmentJson)) {
    let oldFragment = findItemFromList(
      props.printInfo.fragmentJson,
      fragment.fragmentId,
      'fragmentId',
    );
    if (oldFragment != null) {
      let startRow = fragment.startRow;
      let endRow = fragment.startRow + fragment.rowSpan - 1;
      let oldStartRow = oldFragment.startRow;
      let oldEndRow = oldFragment.startRow + oldFragment.rowSpan - 1;
      if (startRow > oldStartRow || endRow < oldEndRow) {
        let range = [];
        if (startRow > oldStartRow) {
          range.push({
            row: [oldStartRow - 1, startRow - 2],
            column: [0, getColumnCount() - 1],
          });
        }
        if (endRow < oldEndRow) {
          range.push({
            row: [endRow, oldEndRow - 1],
            column: [0, getColumnCount() - 1],
          });
        }
        return range;
      }
    }
  }
};
const onSetDatasetParamList = () => {
  Dialog.show<ANY_OBJECT>(
    '数据集参数',
    SetReportDatasetParam,
    {
      area: ['900px', '650px'],
    },
    {
      datasetParamList: (currentFragmentDataset.value || {}).datasetParamList,
      datasetFilterParams: currentFragment.value?.datasetFilterParams,
      formParamList: props.printInfo.paramJson.map((item: ANY_OBJECT) => {
        return item.variableName;
      }),
      validFilterValueType: [FilterValueKind.PRINT_INPUT_PARAM, FilterValueKind.INPUT_DATA],
      path: 'thirdSetReportDatasetParam',
    },
    {
      width: '900px',
      height: '400px',
      pathName: '/thirdParty/thirdSetReportDatasetParam',
    },
  )
    .then(res => {
      handlerSetDatasetParamList(res);
    })
    .catch(e => {
      console.warn(e);
    });
};
const onRemoveDatasetParam = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除此数据集参数？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      if (currentFragment.value) {
        currentFragment.value.datasetFilterParams =
          currentFragment.value.datasetFilterParams.filter((item: ANY_OBJECT) => {
            return item.paramName !== row.paramName;
          });
      }
    })
    .catch(e => {
      console.warn(e);
    });
};
/**
 * 移除排序字段
 */
const onRemoveOrder = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除此排序？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      if (currentFragment.value) {
        currentFragment.value.orderParam = currentFragment.value.orderParam.filter(
          (item: ANY_OBJECT) => {
            return item !== row;
          },
        );
      }
    })
    .catch(e => {
      console.warn(e);
    });
};
/**
 * 编辑过滤字段
 */
const onEditFilter = (row: ANY_OBJECT | null = null) => {
  getFragmentFilterAndOrderColumn(
    currentFragmentDataset.value,
    currentFragmentRelation.value,
    currentFragment.value?.loop,
  )
    .then(res => {
      Dialog.show<ANY_OBJECT>(
        '过滤字段',
        EditReportColumnFilter,
        {
          area: ['600px'],
        },
        {
          rowData: row,
          columnList: res.filterColumnTree,
          formParamList: props.printInfo.paramJson.map((item: ANY_OBJECT) => {
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
      )
        .then(res => {
          if (res) {
            handlerEditFilter(row, res);
          }
        })
        .catch(e => {
          console.warn(e);
        });
    })
    .catch(e => {
      console.warn(e);
    });
};
/**
 * 获取段落占据的行区域数据
 */
const getFragmentRange = (fragment: ANY_OBJECT | null) => {
  return fragment == null
    ? null
    : {
        row: [
          Number.parseInt(fragment.startRow) - 1,
          Number.parseInt(fragment.startRow) + Number.parseInt(fragment.rowSpan) - 2,
        ],
        column: [0, getColumnCount() - 1],
      };
};
/**
 * 移除过滤字段
 */
const onRemoveFilter = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除此过滤条件？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      if (currentFragment.value) {
        currentFragment.value.filterParams = currentFragment.value.filterParams.filter(
          (item: ANY_OBJECT) => {
            return item !== row;
          },
        );
      }
    })
    .catch(e => {
      console.warn(e);
    });
};
/**
 * 编辑排序字段
 */
const onEditOrder = (row: ANY_OBJECT | null = null) => {
  getFragmentFilterAndOrderColumn(
    currentFragmentDataset.value,
    currentFragmentRelation.value,
    currentFragment.value?.loop,
  )
    .then(res => {
      Dialog.show<ANY_OBJECT>(
        '排序',
        EditReportColumnOrder,
        {
          area: ['600px'],
        },
        {
          rowData: row,
          columnList: res.orderColumnList,
          usedColumnList: (currentFragment.value?.orderParam || [])
            .filter((item: ANY_OBJECT) => {
              return row ? item.fieldName !== row.fieldName : true;
            })
            .map((item: ANY_OBJECT) => item.fieldName),
          maxOrder: (currentFragment.value?.orderParam || []).reduce(
            (maxOrder: number, item: ANY_OBJECT) => {
              return Math.max(maxOrder, item.showOrder || 0);
            },
            0,
          ),
          path: 'thirdEditReportColumnOrder',
        },
        {
          width: '600px',
          height: '500px',
          pathName: '/thirdParty/thirdEditReportColumnOrder',
        },
      )
        .then(res => {
          handlerEditOrder(row, res);
        })
        .catch(e => {
          console.warn(e);
        });
    })
    .catch(e => {
      console.warn(e);
    });
};
/**
 * 获取列数
 */
const getColumnCount = () => {
  // 初始化时为26
  return 26;
  // let sheetData = this.getSheetData();
  // console.log(sheetData, '-----------------')
  // if (Array.isArray(sheetData) && sheetData.length > 0) {
  //   return (Array.isArray(sheetData[0].data) && sheetData[0].data.length > 0) ? sheetData[0].data[0].length : sheetData[0].column;
  // }
};
/**
 * 检测段落输入的标识以及段落占据的行区域是否合法
 */
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const checkOnSaveFragment = (fragment: ANY_OBJECT | null, isNew: boolean) => {
  if (!fragment || fragment.showName == null || fragment.showName === '') {
    ElMessage.error('段落名称不能为空！');
    return false;
  }

  if (!Array.isArray(fragment.bindPath) || fragment.bindPath.length <= 0) {
    ElMessage.error('段落绑定数据不能为空！');
    return false;
  }

  // 如果段落绑定了API结果集，并且是循环段落，需要选择绑定的字段
  if (
    fragment.loop &&
    (currentFragmentDataset.value || {}).datasetType === DatasetType.API &&
    (!Array.isArray(fragment.bindColumnPath) || fragment.bindColumnPath.length <= 0)
  ) {
    ElMessage.error('段落绑定字段不能为空！');
    return false;
  }

  if (fragment.startRow == null || fragment.startRow === '') {
    ElMessage.error('起始行不能为空！');
    return false;
  }

  if (fragment.rowSpan == null || fragment.rowSpan === '') {
    ElMessage.error('行跨度不能为空！');
    return false;
  }

  if (Array.isArray(props.printInfo.fragmentJson)) {
    let startRow = fragment.startRow;
    let endRow = fragment.startRow + fragment.rowSpan - 1;
    for (let i = 0; i < props.printInfo.fragmentJson.length; i++) {
      let temp = props.printInfo.fragmentJson[i];
      // 判断段落区域是否重合
      if (temp.fragmentId === fragment.fragmentId) continue;
      let fragmentEndRow = temp.startRow + temp.rowSpan - 1;
      if (!(endRow < temp.startRow || startRow > fragmentEndRow)) {
        ElMessage.error('当前段落所需行区域与段落【' + temp.showName + '】重叠，请重新输入！');
        return false;
      }
    }
  }

  return true;
};
// 检测是否可以保存
const checkValidate = () => {
  if (isCreateFragment.value) {
    ElMessage.error('段落正在编辑，不能保存！');
    return false;
  }
  return true;
};

watch(
  () => currentFragmentId.value,
  () => {
    let temp = findItemFromList(
      props.printInfo.fragmentJson,
      currentFragmentId.value,
      'fragmentId',
    );
    currentFragment.value = temp == null ? {} : cloneFragment(temp) || {};
    let fragmentRange = getFragmentRange(currentFragment.value);
    if (fragmentRange) {
      setTimeout(() => {
        props.xs.setRangeShow(fragmentRange);
      }, 50);
    }
  },
);
watch(
  () => currentFragmentDataset.value,
  () => {
    if (currentFragmentDataset.value) loadDatasetColumnList(currentFragmentDataset.value);
  },
);

defineExpose({ checkValidate });
</script>

<style lang="scss" scoped>
.left-panel {
  display: flex;
  flex-direction: column;
  width: 288px;
  height: 100%;
  .base-card-header {
    height: 57px;
    line-height: 57px;
  }
}
</style>
