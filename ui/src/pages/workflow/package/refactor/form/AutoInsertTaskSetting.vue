<template>
  <div class="autotask-insert-settting">
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
    <el-form-item label="目标表">
      <el-row :gutter="8" style="width: 100%">
        <el-col :span="12">
          <!-- 目标表数据库链接选择 -->
          <el-select
            v-model="formData.destDblinkId"
            clearable
            placeholder="请选择数据库链接"
            @change="onDestDblinkChange"
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
          <!-- 目标表表选择 -->
          <el-select
            v-model="formData.destTableName"
            clearable
            filterable
            placeholder="请选择数据目标表"
            @change="onDestTableChange"
          >
            <el-option v-for="item in destTableList" :key="item" :label="item" :value="item" />
          </el-select>
        </el-col>
      </el-row>
    </el-form-item>
    <MultiItemList
      label="数据插入对象"
      :data="formData.insertDataList"
      addText="添加"
      :disabled="formData.destTableName == null || formData.destTableName === ''"
      @add="onEditInsertData()"
      @edit="onEditInsertData"
      @delete="onDeleteInsertData"
      :prop="{
        label: 'destColumnName',
        value: 'destColumnName',
      }"
    >
      <template v-slot="scope">
        <span>{{ scope.data.destColumnName }}</span>
        <span style="margin: 0px 10px">等于</span>
        <span style="margin: 0px 10px 0px 0px">{{
          AutoTaskValueType.getValue(scope.data.type)
        }}</span>
        <span>{{ scope.data.srcValue }}</span>
      </template>
    </MultiItemList>
  </div>
</template>

<script setup lang="ts">
import { ElMessageBox } from 'element-plus';
import { defineProps, defineEmits } from 'vue';
import EditInsertData from './editInsertData.vue';
import EditSrcTableFilter from './editSrcTableFilter.vue';
import { CriteriaFilterType } from '@/common/staticDict/index';
import { Dialog } from '@/components/Dialog';
import { AutoTaskActionType, AutoTaskValueType } from '@/common/staticDict/flow';
import { ANY_OBJECT } from '@/types/generic';
import MultiItemList from '@/components/MultiItemList/index.vue';
import { FlowDblinkController } from '@/api/flow';

const emit = defineEmits(['update:modelValue', 'change']);
const props = defineProps<{
  modelValue: string;
  flowDblinkList: ANY_OBJECT[];
}>();
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
  srcFilterType: string;
  srcFilterSql: string;
  srcDblinkId?: number;
  srcDblinkType?: string;
  srcTableName: string;
  destDblinkId?: number;
  destDblinkType?: string;
  destTableName: string;
  srcFilterList: Array<ANY_OBJECT>;
  insertDataList: Array<ANY_OBJECT>;
};
const formData = ref<FormDataType>({
  actionType: AutoTaskActionType.INSERT,
  srcFilterType: 'field',
  srcFilterSql: '',
  srcDblinkId: undefined,
  srcDblinkType: undefined,
  srcTableName: '',
  destDblinkId: undefined,
  destTableName: '',
  destDblinkType: undefined,
  srcFilterList: [],
  insertDataList: [],
});
const srcTableList = ref<string[]>([]);
const destTableList = ref<string[]>([]);

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

const updateInsertData = (data, res) => {
  if (data == null) {
    // 新建
    formData.value.insertDataList.push(res);
  } else {
    // 编辑
    formData.value.insertDataList = formData.value.insertDataList.map(item => {
      return item.destColumnName === res.destColumnName ? res : item;
    });
  }
  onChange();
};

const onEditInsertData = (data?: ANY_OBJECT) => {
  Dialog.show(
    '编辑插入字段',
    EditInsertData,
    {
      area: ['500px', '300px'],
    },
    {
      entryId: flowEntry().entryId,
      data,
      dblinkId: formData.value.destDblinkId,
      tableName: formData.value.destTableName,
      srcDblinkId: formData.value.srcDblinkId,
      srcTableName: formData.value.srcTableName,
      flowVariableList: getAllAutoVariableList(),
      path: 'thirdEditInsertData',
    },
    {
      width: '500px',
      height: '300px',
      pathName: '/thirdParty/thirdEditFieldUpdateData',
    },
  )
    .then(res => {
      updateInsertData(data, res);
    })
    .catch(e => {
      console.log(e);
    });
};

const onDeleteInsertData = data => {
  ElMessageBox.confirm('是否删除此字段？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      formData.value.insertDataList = formData.value.insertDataList.filter(
        item => item.destColumnName !== data.destColumnName,
      );
      onChange();
    })
    .catch(e => {
      console.log(e);
    });
};

const updateSrcFilter = (data, res) => {
  if (data == null) {
    // 新建
    formData.value.srcFilterList.push(res);
  } else {
    // 编辑
    formData.value.srcFilterList = formData.value.srcFilterList.map(item => {
      return item.filterColumnName === res.filterColumnName ? res : item;
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
      path: 'thirdEditSrcFilter',
    },
    {
      width: '500px',
      height: '400px',
      pathName: '/thirdParty/thirdEditTableFilter',
    },
  )
    .then(res => {
      updateSrcFilter(data, res);
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

const onSrcDblinkChange = val => {
  formData.value.srcTableName = '';
  formData.value.srcDblinkType = (props.flowDblinkList.find(item => item.id === val) || {}).type;
  formData.value.srcFilterSql = '';
  formData.value.srcFilterList = [];
  onChange();
};

const onSrcTableChange = val => {
  formData.value.srcFilterSql = '';
  formData.value.srcFilterList = [];
  onChange();
};

const onDestDblinkChange = val => {
  formData.value.destTableName = '';
  formData.value.destDblinkType = (props.flowDblinkList.find(item => item.id === val) || {}).type;
  formData.value.insertDataList = [];
  onChange();
};

const onDestTableChange = val => {
  formData.value.insertDataList = [];
  onChange();
};

watch(
  () => props.modelValue,
  val => {
    if (val) {
      let taskInfo = val && val !== '' ? JSON.parse(val) : {};
      formData.value = {
        actionType: taskInfo.actionType || 0,
        srcFilterType: taskInfo.srcFilterType || 'field',
        srcFilterSql: taskInfo.srcFilterSql || '',
        srcDblinkId: taskInfo.srcDblinkId,
        srcTableName: taskInfo.srcTableName || '',
        srcDblinkType: taskInfo.srcDblinkType,
        destDblinkId: taskInfo.destDblinkId,
        destDblinkType: taskInfo.destDblinkType,
        destTableName: taskInfo.destTableName || '',
        insertDataList: taskInfo.insertDataList || [],
        srcFilterList: taskInfo.srcFilterList || [],
      };
    }
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
  () => formData.value.destDblinkId,
  val => {
    if (val) {
      loadDblinkTableList(val).then(tableList => {
        destTableList.value = tableList;
      });
    } else {
      destTableList.value = [];
    }
  },
  { immediate: true },
);
</script>

<style></style>
