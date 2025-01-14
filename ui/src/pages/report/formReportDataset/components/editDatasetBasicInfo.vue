<template>
  <div class="form-single-fragment edit-dataset-basic" style="position: relative">
    <el-form
      ref="basicInfo"
      :model="formData"
      :rules="rules"
      style="width: 100%"
      label-width="110px"
      :size="formItemSize"
      label-position="top"
      @submit.prevent
    >
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="数据集类型" prop="datasetType">
            <el-select
              class="input-item"
              v-model="formData.datasetType"
              :disabled="formData.datasetId != null"
              clearable
              filterable
              placeholder="数据集类型"
              @change="onDatasetTypeChange"
            >
              <el-option
                v-for="item in DatasetType.getList()"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="数据集名称" prop="datasetName">
            <el-input
              class="input-item"
              v-model="formData.datasetName"
              clearable
              placeholder="数据集名称"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="所属分组" prop="groupId">
            <el-cascader
              class="input-item"
              v-model="formData.groupPath"
              :options="groupList"
              filterable
              :clearable="true"
              :show-all-levels="false"
              placeholder="所属分组"
              :props="{ value: 'groupId', label: 'groupName', checkStrictly: true }"
              @change="onGroupIdValueChange"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="DatasetType.API !== formData.datasetType">
          <el-form-item label="数据库" prop="dblinkId">
            <el-select
              class="input-item"
              v-model="formData.dblinkId"
              :clearable="true"
              filterable
              placeholder="数据库"
              :loading="dblinkIdWidget.loading"
              @visible-change="dblinkIdWidget.onVisibleChange"
              @change="onDblinkIdChange"
            >
              <el-option
                v-for="item in dblinkIdWidget.dropdownList"
                :key="item.id"
                :value="item.id"
                :label="item.name"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="DatasetType.TABLE === formData.datasetType">
          <el-form-item label="数据表" prop="tableName">
            <el-select
              class="input-item"
              v-model="formData.tableName"
              :clearable="true"
              filterable
              placeholder="数据表"
              :loading="tableNameWidget.loading"
              @visible-change="tableNameWidget.onVisibleChange"
            >
              <el-option
                v-for="item in tableNameWidget.dropdownList"
                :key="item.tableName"
                :value="item.tableName"
                :label="item.tableName"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="DatasetType.API === formData.datasetType">
          <el-form-item label="接口URL" prop="datasetInfo.url">
            <el-input
              class="input-item"
              v-model="formData.datasetInfo.url"
              :clearable="true"
              filterable
              placeholder="接口URL"
              :loading="tableNameWidget.loading"
              @visible-change="tableNameWidget.onVisibleChange"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="DatasetType.API === formData.datasetType">
          <el-form-item label="请求方式" prop="datasetInfo.method">
            <template #label>
              <el-row
                type="flex"
                justify="space-between"
                align="middle"
                style="display: inline-flex; width: 550px"
              >
                <span style="width: 120px">请求方式</span>
                <el-radio-group class="input-item" v-model="formData.datasetInfo.method">
                  <el-radio value="get">GET</el-radio>
                  <el-radio value="post">POST</el-radio>
                </el-radio-group>
              </el-row>
            </template>
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="DatasetType.API === formData.datasetType">
          <el-form-item label="请求参数">
            <template #label>
              <el-row style="display: inline-block; width: 550px">
                <span>请求参数</span>
                <el-button
                  type="primary"
                  size="default"
                  link
                  style="float: right; margin-left: 10px"
                  @click="onAddDatasetParam"
                  >添加参数</el-button
                >
              </el-row>
            </template>
            <el-row style="width: 100%">
              <DatasetParamList
                v-model:datasetParamList="formData.paramList"
                height="300px"
                :supportDelete="true"
                @delete="onDeleteDatasetParam"
              />
            </el-row>
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="DatasetType.SQL === formData.datasetType">
          <el-form-item label="SQL语句" prop="sql">
            <template #label>
              <el-row style="display: inline-block; width: 550px">
                <span>SQL 语句</span>
                <el-button
                  type="primary"
                  size="default"
                  link
                  style="float: right; margin-left: 10px"
                  @click="onSetDatasetParam"
                  >参数设置</el-button
                >
              </el-row>
            </template>
            <div style="height: 300px">
              <ScriptEditor
                v-model:value="formData.sql"
                type="sql"
                style="width: 100%; height: 300px; border: 1px solid #f0f0f0"
                :options="{ useWrapMode: true }"
              />
            </div>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { CascaderValue } from 'element-plus';
import { findItemFromList } from '@/common/utils';
import { DatasetType } from '@/common/staticDict/report';
import { DictionaryController } from '@/api/system';
import { ReportDblinkController } from '@/api/report';
import { ANY_OBJECT } from '@/types/generic';
import { Dialog } from '@/components/Dialog';
import ScriptEditor from '@/components/ScriptEditor/index.vue';
import { useDropdown } from '@/common/hooks/useDropdown';
import { DropdownOptions, ListData } from '@/common/types/list';
import { useLayoutStore } from '@/store';
import EditDatasetParam from './editDatasetParam.vue';
import DatasetParamList from './datasetParamList.vue';
const layoutStore = useLayoutStore();

const props = withDefaults(
  defineProps<{
    dataset?: ANY_OBJECT;
    groupList?: ANY_OBJECT[];
  }>(),
  {
    groupList: () => [],
  },
);
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize;
});

const basicInfo = ref();

/**
 * 数据库链接下拉数据
 */
const loadReportDblinkDropdownList = (): Promise<ListData<ANY_OBJECT>> => {
  return new Promise((resolve, reject) => {
    DictionaryController.dictReportDblink({})
      .then(res => {
        resolve({ dataList: res.getList() });
      })
      .catch(e => {
        reject(e);
      });
  });
};
const dropdownDblinkOptions: DropdownOptions<ANY_OBJECT> = {
  loadData: loadReportDblinkDropdownList,
};
const dblinkIdWidget = reactive(useDropdown(dropdownDblinkOptions));

/**
 * 获取数据库下所有数据表
 */
const loadTableDropdownList = (): Promise<ListData<ANY_OBJECT>> => {
  return new Promise((resolve, reject) => {
    if (formData.value.dblinkId == null || formData.value.dblinkId === '') {
      resolve({ dataList: [] });
      return;
    }
    ReportDblinkController.listAllTables({
      dblinkId: formData.value.dblinkId,
    })
      .then(res => {
        resolve({
          dataList: res.data.map((item: ANY_OBJECT) => {
            return {
              tableName: item,
            };
          }),
        });
      })
      .catch(e => {
        reject(e);
      });
  });
};
const dropdownTableOptions: DropdownOptions<ANY_OBJECT> = {
  loadData: loadTableDropdownList,
};
const tableNameWidget = reactive(useDropdown(dropdownTableOptions));

const formData = ref<ANY_OBJECT>({
  datasetId: undefined,
  datasetName: undefined,
  groupId: undefined,
  dblinkId: undefined,
  datasetType: DatasetType.TABLE,
  datasetInfo: {},
  tableName: undefined,
  groupPath: [],
  sql: undefined,
  paramList: [],
});
const rules = {
  datasetName: [{ required: true, message: '数据集名称不能为空', trigger: 'change' }],
  groupId: [{ required: true, message: '请选择所属分组', trigger: 'change' }],
  dblinkId: [{ required: true, message: '请选择数据库链接', trigger: 'change' }],
  datasetType: [{ required: true, message: '请选择数据集类型', trigger: 'change' }],
  tableName: [{ required: true, message: '请选择数据表', trigger: 'change' }],
  sql: [{ required: true, message: '请输入SQL语句', trigger: 'change' }],
  'datasetInfo.url': [{ required: true, message: '请输入接口URL', trigger: 'change' }],
  'datasetInfo.method': [{ required: true, message: '请选择请求方式', trigger: 'change' }],
};

const onDblinkIdChange = () => {
  tableNameWidget.refresh(false);
  formData.value.tableName = undefined;
};
const onGroupIdValueChange = (val: CascaderValue) => {
  formData.value.groupId = Array.isArray(val) ? val[val.length - 1] : undefined;
};
/**
 * 获得打印模版基础信息
 */
const getBasicInfo = () => {
  return new Promise((resolve, reject) => {
    basicInfo.value.validate((valid: boolean) => {
      if (!valid) {
        reject();
        return;
      }
      if (formData.value.datasetType === DatasetType.SQL) parseSQLParamList();
      resolve(formData.value);
    });
  });
};
const clearValidate = () => {
  if (basicInfo.value) basicInfo.value.clearValidate();
};
/**
 * 初始化页面数据
 */
const formInit = () => {
  formData.value = Object.keys(formData.value).reduce((retObj: ANY_OBJECT, key: string) => {
    retObj[key] = props.dataset ? props.dataset[key] : undefined;
    return retObj;
  }, {});
  formData.value.datasetType = formData.value.datasetType || DatasetType.TABLE;
  if (Array.isArray(formData.value.paramList)) {
    formData.value.paramList.forEach(item => {
      item.isEdit = formData.value.datasetType === DatasetType.API;
    });
  }
  dblinkIdWidget.onVisibleChange(true).catch(e => {
    console.warn(e);
  });
};
const parseSQLParamList = () => {
  let tempList = [];
  if (formData.value.datasetType === DatasetType.SQL) {
    if (formData.value.sql != null && formData.value.sql !== '') {
      let reg = /\$\{(.+?)\}/g;
      let sqlParamList = formData.value.sql.match(reg) || [];
      if (Array.isArray(sqlParamList) && sqlParamList.length > 0) {
        let nameSet: Set<string> | null = new Set();
        sqlParamList = sqlParamList.filter((name: string) => {
          if (nameSet?.has(name)) return false;
          nameSet?.add(name);
          return true;
        });
        nameSet = null;
        sqlParamList = sqlParamList.map((name: string) => {
          return name.substring(2, name.length - 1);
        });
      }

      tempList = sqlParamList.map((name: string) => {
        let oldParam = findItemFromList(formData.value.paramList, name, 'paramName');
        return oldParam != null
          ? oldParam
          : {
              paramName: name,
              paramType: 'String',
              defaultValue: undefined,
            };
      });
    }
  }
  formData.value.paramList = tempList;
};
const onSetDatasetParam = () => {
  parseSQLParamList();
  Dialog.show(
    '参数设置',
    EditDatasetParam,
    {
      area: ['900px', '600px'],
    },
    {
      paramList: formData.value.paramList,
      path: 'thirdEditDatasetParam',
    },
    {
      width: '900px',
      height: '600px',
      pathName: '/thirdParty/thirdEditDatasetParam',
    },
  )
    .then(res => {
      formData.value.paramList = res;
    })
    .catch(e => {
      console.warn(e);
    });
};
const onDatasetTypeChange = (val: ANY_OBJECT) => {
  formData.value.paramList = [];
  if (val === DatasetType.API) {
    formData.value.datasetInfo = {
      sql: undefined,
      method: 'get',
    };
  } else {
    formData.value.datasetInfo = {};
  }
};
const onAddDatasetParam = () => {
  if (formData.value.paramList == null) formData.value.paramList = [];
  formData.value.paramList.push({
    paramName: undefined,
    paramType: 'String',
    defaultValue: undefined,
    isEdit: true,
  });
};
const onDeleteDatasetParam = (row: ANY_OBJECT) => {
  formData.value.paramList = formData.value.paramList.filter((item: ANY_OBJECT) => item !== row);
};

defineExpose({ getBasicInfo, clearValidate });

watch(
  () => props.dataset,
  () => {
    formInit();
  },
  {
    immediate: true,
  },
);
</script>

<style scoped>
.edit-dataset-basic {
  width: 600px;
  height: 100%;
  padding: 20px;
  background: white;
}
</style>
