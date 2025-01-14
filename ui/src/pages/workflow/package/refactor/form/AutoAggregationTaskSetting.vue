<template>
  <div class="autotask-aggregation-settting">
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
        <el-radio label="field">字段过滤</el-radio>
        <el-radio label="sql">自定义SQL</el-radio>
      </el-radio-group>
    </el-form-item>
    <MultiItemList
      v-if="formData.srcFilterType === 'field'"
      label="源表过滤条件"
      :data="formData.srcFilterList"
      addText="添加"
      :disabled="formData.srcTableName == null || formData.srcTableName === ''"
      @add="onEditSrcFilter()"
      @edit="onEditSrcFilter"
      @delete="onDeleteSrcFilter"
      :prop="{
        label: 'filterColumnName',
        value: 'id',
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
    <MultiItemList
      label="聚合字段"
      :data="formData.aggregationDataList"
      addText="添加"
      :disabled="formData.srcTableName == null || formData.srcTableName === ''"
      @add="onEditAggregationData()"
      @edit="onEditAggregationData"
      @delete="onDeleteAggregationData"
      :prop="{
        label: 'aggregationFunction',
        value: 'id',
      }"
    >
      <template v-slot="scope">
        <span>{{ scope.data.alias }}</span>
        <span style="margin: 0px 10px">等于</span>
        <span>{{ scope.data.aggregationFunction + '(' + scope.data.aggregationColumn + ')' }}</span>
      </template>
    </MultiItemList>
  </div>
</template>

<script setup lang="ts">
import { ElMessageBox } from 'element-plus';
import { defineProps, defineEmits } from 'vue';
import EditSrcTableFilter from './editSrcTableFilter.vue';
import EditAggregationData from './editAggregationData.vue';
import { CriteriaFilterType } from '@/common/staticDict/index';
import { Dialog } from '@/components/Dialog';
import { AutoTaskActionType, AutoTaskValueType } from '@/common/staticDict/flow';
import { ANY_OBJECT } from '@/types/generic';
import MultiItemList from '@/components/MultiItemList/index.vue';
import { FlowDblinkController } from '@/api/flow';

const emit = defineEmits(['update:modelValue', 'change']);
type IProps = {
  modelValue: string;
  flowDblinkList: Array<ANY_OBJECT>;
};
const props = defineProps<IProps>();
const flowEntry = inject('flowEntry', () => {
  return {} as ANY_OBJECT;
});
const formList = inject('formList', () => {
  return {} as ANY_OBJECT;
});
const prefix = inject('prefix');
const getAllAutoVariableList = inject('getAllAutoVariableList', () => {
  return [];
});

type FormDataType = {
  actionType?: number;
  srcDblinkId?: string;
  srcDblinkType?: string | number;
  srcTableName?: string;
  srcFilterType?: string;
  srcFilterList: Array<ANY_OBJECT>;
  srcFilterSql?: string;
  aggregationDataList: Array<ANY_OBJECT>;
};

const formData = ref<FormDataType>({
  actionType: AutoTaskActionType.AGGREGATE,
  srcDblinkId: undefined,
  srcDblinkType: undefined,
  srcTableName: undefined,
  srcFilterType: 'field',
  srcFilterList: [],
  srcFilterSql: undefined,
  aggregationDataList: [],
});

const srcColumnList = ref<Array<ANY_OBJECT>>([]);
const srcTableList = ref<Array<string>>([]);

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
    formData.value.srcTableName === '' ||
    formData.value.srcDblinkId === ''
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
  formData.value.srcDblinkType = (props.flowDblinkList.find(item => item.id === val) || {}).type;
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
      return item.srcTableName === res.srcTableName ? res : item;
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
      path: 'thirdEditAggregationFilter',
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

const updateAggregationData = (data, res) => {
  if (data == null) {
    // 新建
    formData.value.aggregationDataList.push(res);
  } else {
    // 编辑
    formData.value.aggregationDataList = formData.value.aggregationDataList.map(item => {
      return item.id === res.id ? res : item;
    });
  }
  onChange();
};

const onEditAggregationData = (data?: ANY_OBJECT) => {
  Dialog.show(
    '编辑聚合字段',
    EditAggregationData,
    {
      area: ['500px', '400px'],
    },
    {
      data,
      srcDblinkId: formData.value.srcDblinkId,
      srcTableName: formData.value.srcTableName,
      flowVariableList: getAllAutoVariableList(),
      path: 'thirdEditAggregationData',
    },
    {
      width: '500px',
      height: '400px',
      pathName: '/thirdParty/thirdEditAggregationData',
    },
  )
    .then(res => {
      updateAggregationData(data, res);
    })
    .catch(e => {
      console.log(e);
    });
};

const onDeleteAggregationData = data => {
  ElMessageBox.confirm('是否删除此聚合字段？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      formData.value.aggregationDataList = formData.value.aggregationDataList.filter(
        item => item.id !== data.id,
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
      actionType: AutoTaskActionType.AGGREGATE,
      srcFilterType: taskInfo.srcFilterType || 'field',
      srcFilterSql: taskInfo.srcFilterSql || '',
      srcDblinkId: taskInfo.srcDblinkId,
      srcTableName: taskInfo.srcTableName || '',
      srcDblinkType: taskInfo.srcDblinkType,
      srcFilterList: taskInfo.srcFilterList || [],
      aggregationDataList: taskInfo.aggregationDataList || [],
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
  { immediate: true },
);

watch(
  () => formData.value.srcTableName,
  val => {
    getColumnList();
  },
  { immediate: true },
);
</script>

<style></style>
