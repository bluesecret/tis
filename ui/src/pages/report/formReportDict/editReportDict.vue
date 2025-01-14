<template>
  <el-row class="form-single-fragment third-party-dlg">
    <el-form
      ref="form"
      :model="formData"
      class="full-width-input form-box"
      :rules="rules"
      style="width: 100%"
      label-width="110px"
      :size="formItemSize"
      label-position="right"
      @submit.prevent
    >
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="字典类型" prop="ReportDict.dictType">
            <el-select
              class="input-item"
              v-model="formData.ReportDict.dictType"
              placeholder="字典类型"
              @change="onDictTypeValueChange"
            >
              <el-option
                v-for="item in ReportDictType.getList()"
                :key="item.id"
                :value="item.id"
                :label="item.name"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="字典名称" prop="ReportDict.dictName">
            <el-input
              class="input-item"
              v-model="formData.ReportDict.dictName"
              :clearable="true"
              placeholder="字典名称"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="ReportDictType.TABLE === formData.ReportDict.dictType">
          <el-form-item label="数据库链接" prop="ReportDict.dblinkId">
            <el-select
              class="input-item"
              v-model="formData.ReportDict.dblinkId"
              :clearable="true"
              filterable
              placeholder="数据库链接"
              :loading="formEditReportDict.dblinkId.impl.loading"
              @visible-change="formEditReportDict.dblinkId.impl.onVisibleChange"
              @change="onDblinkIdValueChange"
            >
              <el-option
                v-for="item in formEditReportDict.dblinkId.impl.dropdownList"
                :key="item.id"
                :value="item.id"
                :label="item.name"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="ReportDictType.TABLE === formData.ReportDict.dictType">
          <el-form-item label="数据表" prop="ReportDict.tableName">
            <el-select
              class="input-item"
              v-model="formData.ReportDict.tableName"
              :clearable="true"
              filterable
              placeholder="数据表"
              :loading="formEditReportDict.tableName.impl.loading"
              @visible-change="formEditReportDict.tableName.impl.onVisibleChange"
              @change="onTableNameValueChange"
            >
              <el-option
                v-for="item in formEditReportDict.tableName.impl.dropdownList"
                :key="item.id"
                :value="item.id"
                :label="item.name"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="ReportDictType.TABLE === formData.ReportDict.dictType">
          <el-form-item label="树状字典" prop="ReportDict.treeFlag">
            <el-switch class="input-item" v-model="formData.ReportDict.treeFlag" />
          </el-form-item>
        </el-col>
        <el-col
          :span="24"
          v-if="
            ReportDictType.TABLE === formData.ReportDict.dictType && formData.ReportDict.treeFlag
          "
        >
          <el-form-item label="字典父字段" prop="ReportDict.parentKeyColumnName">
            <el-select
              v-model="formData.ReportDict.parentKeyColumnName"
              clearable
              placeholder="选择字典父字段"
              filterable
              allow-create
              default-first-option
            >
              <el-option
                v-for="column in tableColumnList"
                :key="column"
                :label="column"
                :value="column"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="ReportDictType.TABLE === formData.ReportDict.dictType">
          <el-form-item label="字典键字段" prop="ReportDict.keyColumnName">
            <el-select
              v-model="formData.ReportDict.keyColumnName"
              clearable
              placeholder="字典键字段"
              filterable
              allow-create
              default-first-option
            >
              <el-option
                v-for="column in tableColumnList"
                :key="column"
                :label="column"
                :value="column"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="ReportDictType.TABLE === formData.ReportDict.dictType">
          <el-form-item label="字典值字段" prop="ReportDict.valueColumnName">
            <el-select
              v-model="formData.ReportDict.valueColumnName"
              clearable
              placeholder="字典值字段"
              filterable
              allow-create
              default-first-option
            >
              <el-option
                v-for="column in tableColumnList"
                :key="column"
                :label="column"
                :value="column"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="ReportDictType.TABLE === formData.ReportDict.dictType">
          <el-form-item label="逻辑删除字段" prop="ReportDict.deletedColumnName">
            <el-select
              v-model="formData.ReportDict.deletedColumnName"
              clearable
              placeholder="逻辑删除字段"
              filterable
              allow-create
              default-first-option
            >
              <el-option
                v-for="column in tableColumnList"
                :key="column"
                :label="column"
                :value="column"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <!-- 字典参数 -->
        <el-col :span="24" v-if="ReportDictType.TABLE === formData.ReportDict.dictType">
          <el-form-item label="字典参数">
            <el-select
              v-model="dictParamList"
              placeholder="请添加字典获取参数"
              multiple
              filterable
              allow-create
              default-first-option
            >
              <el-option v-for="item in tableColumnList" :key="item" :value="item" :label="item">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <!-- 字典数据 -->
        <el-col
          :span="24"
          v-if="ReportDictType.CUSTOM === formData.ReportDict.dictType"
          style="margin-bottom: 15px"
        >
          <vxe-table
            :data="dictData"
            :size="formItemSize"
            :row-config="{ isHover: true }"
            header-cell-class-name="table-header-gray"
            height="300px"
          >
            <vxe-column title="字典键数据" field="id" />
            <vxe-column title="字典值数据" field="name" />
            <vxe-column title="操作" width="100px" fixed="right" align="right">
              <template v-slot:header>
                <el-row type="flex" justify="end" align="middle">
                  <EditDictDataButton
                    :defaultFormItemSize="formItemSize"
                    width="200px"
                    btnText="新建"
                    :dictList="dictData"
                    @save="onAddDictData"
                  />
                </el-row>
              </template>
              <template v-slot="scope">
                <EditDictDataButton
                  :defaultFormItemSize="formItemSize"
                  width="200px"
                  :value="scope.row"
                  btnText="编辑"
                  :dictList="dictData"
                  @save="onEditDictData"
                />
                <el-button
                  style="margin-left: 10px"
                  link
                  type="danger"
                  :size="formItemSize"
                  @click="onDeleteDictData(scope.row)"
                  >删除</el-button
                >
              </template>
            </vxe-column>
            <template v-slot:empty>
              <div class="table-empty unified-font">
                <img src="@/assets/img/empty.png" />
                <span>暂无数据</span>
              </div>
            </template>
          </vxe-table>
        </el-col>
        <!-- 编码字典 -->
        <el-col :span="24" v-if="ReportDictType.CODE === formData.ReportDict.dictType">
          <el-form-item label="选择编码字典">
            <el-row justify="space-between" style="flex-wrap: nowrap">
              <el-input v-model="filter.dictCode" :clearable="true" placeholder="输入字典编码" />
              <el-button
                type="primary"
                :plain="true"
                :size="formItemSize"
                style="margin-left: 15px"
                @click="onSearch"
                >查询</el-button
              >
            </el-row>
          </el-form-item>
        </el-col>
        <el-col
          :span="24"
          v-if="ReportDictType.CODE === formData.ReportDict.dictType"
          style="margin-bottom: 15px"
        >
          <el-radio-group
            class="radio-table"
            v-model="formData.ReportDict.dictCode"
            style="width: 100%"
          >
            <vxe-table
              ref="globalDictTable"
              :data="formEditReportDict.globalDictTableWidget.impl.dataList"
              :size="formItemSize"
              :row-config="{ isHover: true }"
              header-cell-class-name="table-header-gray"
              height="300px"
            >
              <vxe-column title="选择" header-align="center" align="center" width="60px">
                <template v-slot="scope">
                  <el-radio :value="scope.row.dictCode"> </el-radio>
                </template>
              </vxe-column>
              <vxe-column title="字典名称" field="dictName" />
              <vxe-column title="字典编码" field="dictCode" />
              <template v-slot:empty>
                <div class="table-empty unified-font">
                  <img src="@/assets/img/empty.png" />
                  <span>暂无数据</span>
                </div>
              </template>
            </vxe-table>
          </el-radio-group>
          <el-row type="flex" justify="end" style="margin-top: 16px">
            <el-pagination
              :total="formEditReportDict.globalDictTableWidget.impl.totalCount"
              :current-page="formEditReportDict.globalDictTableWidget.impl.currentPage"
              :page-size="formEditReportDict.globalDictTableWidget.impl.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, prev, pager, next, sizes"
              @current-change="formEditReportDict.globalDictTableWidget.impl.onCurrentPageChange"
              @size-change="formEditReportDict.globalDictTableWidget.impl.onPageSizeChange"
            />
          </el-row>
        </el-col>
      </el-row>
    </el-form>
    <el-row class="menu-box" type="flex" justify="end" style="width: 100%">
      <el-button :size="formItemSize" :plain="true" @click="onCancel"> 取消 </el-button>
      <el-button type="primary" :size="formItemSize" @click="onSubmit"> 保存 </el-button>
    </el-row>
  </el-row>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { VxeTable, VxeColumn } from 'vxe-table';
import { findItemFromList } from '@/common/utils';
import { ReportDictController, ReportDblinkController } from '@/api/report';
import { DictionaryController } from '@/api/system';
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';
import { ReportDictType } from '@/common/staticDict/report';
import { useDropdown } from '@/common/hooks/useDropdown';
import { DropdownOptions, ListData } from '@/common/types/list';
import { useTable } from '@/common/hooks/useTable';
import { TableOptions } from '@/common/types/pagination';
import { useLayoutStore } from '@/store';
import EditDictDataButton from '@/pages/report/formReportDict/editDictDataButton.vue';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
const layoutStore = useLayoutStore();

interface IProps extends ThirdProps {
  rowData?: ANY_OBJECT;
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<boolean>;
}
const props = defineProps<IProps>();
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize || thirdParams.value.defaultFormItemSize?.value;
});

const form = ref();
const globalDictTable = ref();

const formData = ref<ANY_OBJECT>({
  ReportDict: {
    dictId: undefined,
    dictName: undefined,
    dictType: ReportDictType.TABLE,
    dblinkId: undefined,
    tableName: undefined,
    keyColumnName: undefined,
    parentKeyColumnName: undefined,
    valueColumnName: undefined,
    deletedColumnName: undefined,
    treeFlag: false,
    dictListUrl: undefined,
    dictDataJson: undefined,
    dictCode: undefined,
    isDatasourceInit: false,
  },
});
const filter = ref<ANY_OBJECT>({
  dictCode: undefined,
});
const tableColumnList = ref<string[]>([]);
const dictParamList = ref<ANY_OBJECT[]>([]);
const dictData = ref<ANY_OBJECT[]>([]);
const rules = {
  'ReportDict.treeFlag': [{ required: true, message: '请输入是否树形标记', trigger: 'blur' }],
  'ReportDict.dictName': [{ required: true, message: '请输入字典名称', trigger: 'blur' }],
  'ReportDict.dictType': [{ required: true, message: '请输入字典类型', trigger: 'blur' }],
  'ReportDict.parentKeyColumnName': [
    { required: true, message: '请输入字典父字段', trigger: 'blur' },
  ],
  'ReportDict.keyColumnName': [{ required: true, message: '请输入字典键字段', trigger: 'blur' }],
  'ReportDict.valueColumnName': [{ required: true, message: '请输入字典值字段', trigger: 'blur' }],
};

/**
 * 数据库链接下拉数据获取函数
 */
const loadDblinkIdDropdownList = () => {
  return new Promise<ListData<ANY_OBJECT>>((resolve, reject) => {
    let params = {};
    DictionaryController.dictReportDblink(params)
      .then(res => {
        resolve({ dataList: res.getList() });
      })
      .catch(e => {
        reject(e);
      });
  });
};
const dropdownDblinkIdOptions: DropdownOptions<ANY_OBJECT> = {
  loadData: loadDblinkIdDropdownList,
};
/**
 * 数据表下拉数据获取函数
 */
const loadTableNameDropdownList = () => {
  return new Promise<ListData<ANY_OBJECT>>((resolve, reject) => {
    if (formData.value.ReportDict.dblinkId == null || formData.value.ReportDict.dblinkId === '') {
      reject('请选择数据库链接！');
      return;
    }
    let params = {
      dblinkId: formData.value.ReportDict.dblinkId,
    };
    ReportDblinkController.listAllTables(params)
      .then(res => {
        resolve({
          dataList: res.data.map((item: ANY_OBJECT) => {
            return {
              id: item,
              name: item,
            };
          }),
        });
      })
      .catch(e => {
        reject(e);
      });
  });
};
const dropdownTableNameOptions: DropdownOptions<ANY_OBJECT> = {
  loadData: loadTableNameDropdownList,
};
const loadGlobalDictList = (params: ANY_OBJECT) => {
  if (params == null) params = {};
  params = {
    ...params,
    globalDictDtoFilter: {
      dictCode: filter.value.dictCode === '' ? undefined : filter.value.dictCode,
    },
  };

  return new Promise((resolve, reject) => {
    ReportDictController.listAllGlobalDict(params)
      .then(res => {
        if (Array.isArray(res.data.dataList) && formData.value.ReportDict.dictCode !== null) {
          let currentRow = findItemFromList(
            res.data.dataList,
            formData.value.ReportDict.dictCode,
            'dictCode',
          );
          if (currentRow) {
            globalDictTable.value.setCurrentRow({ ...currentRow });
          }
        }
        resolve({
          dataList: res.data.dataList.map(item => {
            return { ...item } as ANY_OBJECT;
          }),
          totalCount: res.data.totalCount,
        });
      })
      .catch(e => {
        console.log(e);
        reject(e);
      });
  });
};
const loadGlobalDictListVerify = () => {
  return true;
};
const tableOptions: TableOptions<ANY_OBJECT> = {
  loadTableData: loadGlobalDictList,
  verifyTableParameter: loadGlobalDictListVerify,
  paged: true,
};
const formEditReportDict = reactive<ANY_OBJECT>({
  formFilter: {},
  formFilterCopy: {},
  dblinkId: {
    impl: useDropdown(dropdownDblinkIdOptions),
  },
  tableName: {
    impl: useDropdown(dropdownTableNameOptions),
  },
  globalDictTableWidget: {
    impl: useTable(tableOptions),
  },
  menuBlock: {
    isInit: false,
  },
  isInit: false,
});
const isEdit = computed(() => {
  return formData.value.ReportDict.dictId != null;
});

const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  } else {
    onCloseThirdDialog(false);
  }
};
/**
 * 保存
 */
const onSubmit = () => {
  form.value.validate((valid: boolean) => {
    if (!valid) return;
    if (
      formData.value.ReportDict.dictType === ReportDictType.CODE &&
      (formData.value.ReportDict.dictCode == null || formData.value.ReportDict.dictCode === '')
    ) {
      ElMessage.error('请选择编码字典！');
      return;
    }
    let params = {
      reportDictDto: {
        dictId: formData.value.ReportDict.dictId,
        dictName: formData.value.ReportDict.dictName,
        dictType: formData.value.ReportDict.dictType,
        dblinkId: formData.value.ReportDict.dblinkId,
        tableName: formData.value.ReportDict.tableName,
        keyColumnName: formData.value.ReportDict.keyColumnName,
        parentKeyColumnName: formData.value.ReportDict.parentKeyColumnName,
        valueColumnName: formData.value.ReportDict.valueColumnName,
        deletedColumnName: formData.value.ReportDict.deletedColumnName,
        treeFlag: formData.value.ReportDict.treeFlag,
        dictCode: formData.value.ReportDict.dictCode,
        dictDataJson: JSON.stringify({
          dictData: dictData.value,
          paramList: dictParamList.value.map(item => {
            return {
              dictParamName: item,
            };
          }),
        }),
      },
    };
    let httpCall = isEdit.value
      ? ReportDictController.update(params)
      : ReportDictController.add(params);
    httpCall
      .then(() => {
        ElMessage.success('保存成功');
        if (props.dialog) {
          props.dialog.submit(true);
        } else {
          onCloseThirdDialog(true, true);
        }
      })
      .catch(e => {
        console.warn(e);
      });
  });
};

const onSearch = () => {
  formData.value.ReportDict.dictCode = undefined;
  onSearchGlobalDict();
};
const onSearchGlobalDict = () => {
  formEditReportDict.globalDictTableWidget.impl.refreshTable(true, 1);
};
/**
 * 字典数据
 */
const onAddDictData = (data: ANY_OBJECT) => {
  if (data != null) dictData.value.push(data);
};
const onEditDictData = (newValue: ANY_OBJECT, oldValue: ANY_OBJECT | undefined) => {
  if (newValue != null && oldValue != null) {
    dictData.value = dictData.value.map(item => {
      return item.id === oldValue.id ? newValue : item;
    });
  }
};
const onDeleteDictData = (row: ANY_OBJECT) => {
  if (row != null) {
    dictData.value = dictData.value.filter(item => {
      return item.id !== row.id;
    });
  }
};
/**
 * 获取选中字典表字段列表
 */
const loadTableColumns = () => {
  tableColumnList.value = [];
  if (
    formData.value.ReportDict.dblinkId == null ||
    formData.value.ReportDict.dblinkId === '' ||
    formData.value.ReportDict.tableName == null ||
    formData.value.ReportDict.tableName === ''
  ) {
    return;
  }

  let params = {
    dblinkId: formData.value.ReportDict.dblinkId,
    tableName: formData.value.ReportDict.tableName,
  };

  ReportDblinkController.listTableColumn(params)
    .then(res => {
      tableColumnList.value = (res.data || []).map((item: ANY_OBJECT) => item.columnName);
    })
    .catch(e => {
      console.warn(e);
    });
};
/**
 * 字典类型选中值改变
 */
const onDictTypeValueChange = () => {
  formData.value.ReportDict.dictCode = undefined;
  dictParamList.value = [];
  onSearch();
};

/**
 * 数据库链接选中值改变
 */
const onDblinkIdValueChange = () => {
  formData.value.ReportDict.tableName = undefined;
  formEditReportDict.tableName.impl.setDirty(true);
  onTableNameValueChange();
};

/**
 * 数据表选择值改变
 */
const onTableNameValueChange = () => {
  formData.value.ReportDict.keyColumnName = undefined;
  formData.value.ReportDict.parentKeyColumnName = undefined;
  formData.value.ReportDict.valueColumnName = undefined;
  formData.value.ReportDict.deletedColumnName = undefined;
  loadTableColumns();
};
/**
 * 更新编辑报表字典
 */
const refreshFormEditReportDict = () => {
  formEditReportDict.isInit = true;
};
const formInit = () => {
  refreshFormEditReportDict();
};

onMounted(() => {
  // 初始化页面数据
  formInit();
  // 初始化级联数据
  let rowData = props.rowData;
  if (rowData != null) {
    formEditReportDict.dblinkId.impl
      .onVisibleChange(true)
      .finally(() => {
        formData.value.ReportDict = {
          ...rowData,
        };
        // 自定义字典
        if (
          formData.value.ReportDict.dictDataJson != null &&
          formData.value.ReportDict.dictDataJson !== ''
        ) {
          let jsonObject = null;
          try {
            jsonObject = JSON.parse(formData.value.ReportDict.dictDataJson);
          } catch (e) {
            console.error(e);
          }
          dictData.value = (jsonObject || {}).dictData || [];
          if (jsonObject && Array.isArray(jsonObject.paramList)) {
            dictParamList.value = jsonObject.paramList.map((item: ANY_OBJECT) => {
              return item.dictParamName;
            });
          }
        }
        // 编码字典
        if (formData.value.ReportDict.dictType === ReportDictType.CODE) {
          filter.value.dictCode = formData.value.ReportDict.dictCode;
          onSearchGlobalDict();
        }
        loadTableColumns();
      })
      .catch((e: Error) => {
        console.warn(e);
      });
  }
});
</script>

<style scoped>
.radio-table :deep(.el-radio__label) {
  display: none;
}
</style>
