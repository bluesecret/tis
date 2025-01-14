<template>
  <div class="autotask-load-cache-settting">
    <el-form-item label="缓存键前缀">
      <template #label>
        <span>缓存键前缀 </span>
        <el-tooltip class="item" effect="dark" content="缓存键前缀，推荐以冒号结尾" placement="right-start">
          <el-icon><Warning /></el-icon>
        </el-tooltip>
      </template>
      <el-input v-model="formData.cacheKeyPrefix" @change="onChange" placeholder="缓存键前缀，推荐以冒号结尾"/>
    </el-form-item>
    <el-form-item label="缓存数据来自结果集">
      <el-switch v-model="formData.cacheKeyFromSql" @change="onChange"/>
    </el-form-item>
    <div class="cache-key-from-sql"  v-if="formData.cacheKeyFromSql">
      <el-form-item label="源表">
        <el-row :gutter="8" style="width: 100%">
          <el-col :span="12">
            <!-- 源表数据库链接选择 -->
            <el-select
              v-model="formData.srcDblinkId"
              clearable
              placeholder="请选择数据库链接"
              @change="onSrcDblinkChange"
            >
              <el-option
                v-for="item in flowDblinkList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-col>
          <el-col :span="12">
            <!-- 源表选择 -->
            <el-select
              v-model="formData.srcTableName"
              clearable
              filterable
              placeholder="请选择数据源表"
              @change="onSrcTableChange"
            >
              <el-option v-for="item in srcTableList" :key="item" :label="item" :value="item" />
            </el-select>
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="源表过滤类型">
        <el-radio-group v-model="formData.srcFilterType" @change="onChange">
          <el-radio value="field">字段过滤</el-radio>
          <el-radio value="sql">自定义SQL</el-radio>
        </el-radio-group>
      </el-form-item>
      <MultiItemList
        v-if="formData.srcFilterType == 'field'"
        label="源表过滤条件"
        :data="formData.srcFilterList"
        addText="添加"
        :disabled="formData.srcTableName == null || formData.srcTableName == ''"
        @add="onEditSrcFilter()"
        @edit="onEditSrcFilter"
        @delete="onDeleteSrcFilter"
        :prop="{
          label: 'filterColumnName',
          value: 'filterColumnName',
        }"
      >
        <template v-slot="scope">
          <span>{{ (scope.data || {}).filterColumnName }}</span>
          <span style="margin: 0px 10px">{{
            CriteriaFilterType.getValue((scope.data || {}).filterType)
          }}</span>
          <span style="margin: 0px 10px 0px 0px">{{
            AutoTaskValueType.getValue(scope.data.valueType)
          }}</span>
          <span>
            {{ (scope.data || {}).filterValue }}
          </span>
        </template>
      </MultiItemList>
      <el-input
        v-else
        v-model="formData.srcFilterSql"
        type="textarea"
        rows="5"
        placeholder="重要！过滤值使用流程变量时需要手动输入，变量名的两边一定不要添加任何引号，如：name = ${variableName}"
        @change="onChange"
      />
      <el-form-item label="查询字段">
        <el-select
          v-model="formData.selectFieldList"
          clearable
          filterable
          multiple
          placeholder="查询仅返回选择的字段，如不选则返回所有字段"
          @change="onChange"
        >
          <el-option
            v-for="item in srcColumnList"
            :key="item.columnName"
            :label="item.columnName"
            :value="item.columnName"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="缓存键字段">
        <el-select
          v-model="formData.cacheKeyFieldNames"
          clearable
          filterable
          multiple
          placeholder="根据所选顺序组成缓存键"
          @change="onChange"
        >
          <el-option
            v-for="filed in formData.selectFieldList"
            :key="filed"
            :label="filed"
            :value="filed"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="缓存值字段">
        <el-select
          v-model="formData.cacheValueFieldNames"
          clearable
          filterable
          multiple
          @change="onChange"
        >
          <el-option
            v-for="filed in formData.selectFieldList"
            :key="filed"
            :label="filed"
            :value="filed"
          />
        </el-select>
      </el-form-item>
    </div>
    <div class="cache-key-not-from-sql" v-else>
      <el-form-item label="缓存键">
        <template #label>
          <span>缓存键 </span>
          <el-tooltip class="item" effect="dark" content="缓存键支持使用变量，变量名可用 ${} 表示" placement="right-start">
            <el-icon><Warning /></el-icon>
          </el-tooltip>
        </template>
        <el-input v-model="formData.cacheKey" @change="onChange" placeholder="缓存键支持使用变量，变量名可用 ${} 表示"/>
      </el-form-item>
      <el-form-item label="缓存值">
        <template #label>
          <span>缓存值 </span>
          <el-tooltip class="item" effect="dark" content="缓存值支持使用变量，变量名可用 ${} 表示" placement="right-start">
            <el-icon><Warning /></el-icon>
          </el-tooltip>
        </template>
        <el-input v-model="formData.cacheValue" @change="onChange" placeholder="缓存值支持使用变量，变量名可用 ${} 表示"/>
      </el-form-item>
    </div>
    <el-form-item label="缓存过期时间 (分钟)">
      <el-input-number
        v-model="formData.cacheExpireMinutes"
        controls-position="right"
        style="width: 100%"
        :min="1"
        :max="99999"
        placeholder="缓存过期时间 (分钟)"
        @change="onChange"
      />
    </el-form-item>
  </div>
</template>

<script setup lang="ts">
import { Warning } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';
import { defineProps, defineEmits } from 'vue';
import EditSrcTableFilter from './editSrcTableFilter.vue';
import { AutoTaskActionType, AutoTaskCacheValueType, AutoTaskValueType } from '@/common/staticDict/flow';
import { CriteriaFilterType } from '@/common/staticDict/index';
import { ANY_OBJECT } from '@/types/generic';
import { Dialog } from '@/components/Dialog';
import { FlowDblinkController } from '@/api/flow';

const emit = defineEmits(['update:modelValue', 'change']);
type IProps = {
  modelValue: string;
  flowDblinkList: Array<ANY_OBJECT>;
};
const props = defineProps<IProps>();

type FormDataType = {
  actionType?: number;
  cacheKeyPrefix: string;
  cacheKeyFromSql: boolean;
  cacheKey?: string;
  cacheValue?: string;
  srcDblinkId?: string;
  srcDblinkType?: string | number;
  srcTableName: string | null;
  srcFilterType?: string;
  srcFilterList: ANY_OBJECT[];
  srcFilterSql: string;
  selectFieldList: string[];
  cacheKeyFieldNames: string[];
  cacheValueFieldNames: string[];
  cacheExpireMinutes?: number;
  cacheValueType: string;
  cacheMapKey?: string;
};
const formData = ref<FormDataType>({
  actionType: AutoTaskActionType.LOAD_CACHE,
  cacheKeyPrefix: '',
  cacheKeyFromSql: false,
  srcFilterType: 'field',
  srcFilterList: [],
  srcTableName: null,
  srcFilterSql: '',
  selectFieldList: [],
  selectFieldList: [],
  cacheKeyFieldNames: [],
  cacheValueFieldNames: [],
  cacheValueType: AutoTaskCacheValueType.STRING,
});

const srcColumnList = ref<Array<ANY_OBJECT>>([]);
const srcTableList = ref<Array<string>>([]);

const flowEntry = inject('flowEntry', () => {
  return {} as ANY_OBJECT;
});

const getAllAutoVariableList = inject('getAllAutoVariableList', () => {
  return [];
});

const onChange = () => {
  let tempData = {
    ...formData.value,
  };
  emit('update:modelValue', JSON.stringify(tempData));
  emit('change', JSON.stringify(tempData));
};

const loadDblinkTableList = async dblinkId => {
  let res = await FlowDblinkController.listDblinkTables({
    dblinkId,
  });

  return Array.isArray(res.data) ? res.data.map(item => item.tableName) : [];
};

const getColumnList = () => {
  if (
    formData.value.srcDblinkId == null ||
    formData.value.srcTableName == null ||
    formData.value.srcTableName == '' ||
    formData.value.srcDblinkId == ''
  ) {
    srcColumnList.value = [];
    return;
  }
  FlowDblinkController.listDblinkTableColumns({
    dblinkId: formData.value.srcDblinkId,
    tableName: formData.value.srcTableName,
  }).then(res => {
    srcColumnList.value = res.data;
  });
};

const onSrcDblinkChange = val => {
  formData.value.srcTableName = '';
  formData.value.srcDblinkType = (props.flowDblinkList.find(item => item.id == val) || {}).type;
  formData.value.srcFilterSql = '';
  formData.value.srcFilterList = [];
  onChange();
};

const onSrcTableChange = () => {
  formData.value.srcFilterSql = '';
  formData.value.srcFilterList = [];
  onChange();
};

const updateSrcTableFilter = (data, res) => {
  if (data == null) {
    // 新建
    formData.value.srcFilterList.push(res);
  } else {
    // 编辑
    formData.value.srcFilterList = formData.value.srcFilterList.map(item => {
      return item.srcTableName == res.srcTableName ? res : item;
    });
  }
  onChange();
};

const onEditSrcFilter = (data?: ANY_OBJECT) => {
  Dialog.show(
    '编辑过滤字段',
    EditSrcTableFilter,
    {
      area: ['500px', '400px'],
    },
    {
      entryId: flowEntry().entryId,
      data,
      dblinkId: formData.value.srcDblinkId,
      tableName: formData.value.srcTableName,
      flowVariableList: getAllAutoVariableList(),
      path: 'thirdEditQuerySingleFilter',
    },
    {
      width: '500px',
      height: '400px',
      pathName: '/thirdParty/thirdEditTableFilter',
    },
  )
    .then(res => {
      updateSrcTableFilter(data, res);
    })
    .catch(e => {
      console.log(e);
    });
};

const onDeleteSrcFilter = data => {
  ElMessageBox.confirm('是否删除此过滤字段？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      formData.value.srcFilterList = formData.value.srcFilterList.filter(
        item => item.filterColumnName !== data.filterColumnName,
      );
      onChange();
    })
    .catch(e => {
      console.log(e);
    });
};

watch(
  () => props.modelValue,
  val => {
    let taskInfo = val && val !== '' ? JSON.parse(val) : {};
    formData.value = {
      cacheKeyPrefix: taskInfo.cacheKeyPrefix || '',
      cacheKeyFromSql: taskInfo.cacheKeyFromSql || false,
      cacheKey: taskInfo.cacheKey,
      cacheValue: taskInfo.cacheValue,
      actionType: AutoTaskActionType.LOAD_CACHE,
      srcFilterType: taskInfo.srcFilterType || 'field',
      srcFilterSql: taskInfo.srcFilterSql || '',
      srcDblinkId: taskInfo.srcDblinkId,
      srcTableName: taskInfo.srcTableName || '',
      srcDblinkType: taskInfo.srcDblinkType,
      srcFilterList: taskInfo.srcFilterList || [],
      selectFieldList: taskInfo.selectFieldList || [],
      cacheKeyFieldNames: taskInfo.cacheKeyFieldNames || [],
      cacheValueFieldNames: taskInfo.cacheValueFieldNames || [],
      cacheExpireMinutes: taskInfo.cacheExpireMinutes,
      cacheValueType: taskInfo.cacheValueType || AutoTaskCacheValueType.STRING,
      cacheMapKey: taskInfo.cacheMapKey
    };
  },
  { immediate: true },
);

watch(
  () => formData.value.srcDblinkId,
  val => {
    if (val) {
      loadDblinkTableList(val).then(tableList => {
        srcTableList.value = tableList;
      });
    } else {
      srcTableList.value = [];
    }
  },
);

watch(
  () => formData.value.srcTableName,
  () => {
    getColumnList();
  },
  { immediate: true },
);
</script>

<style scoped></style>
