<template>
  <div class="autotask-delete-settting">
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
              v-for="item in props.flowDblinkList"
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
    <el-form-item label="逻辑删除字段">
      <el-select
        v-model="formData.logicDeleteField"
        clearable
        filterable
        placeholder="请选择逻辑删除字段"
        @change="onChange"
      >
        <el-option
          v-for="item in destColumnList"
          :key="item.columnName"
          :label="item.columnName"
          :value="item.columnName"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="目标表过滤类型">
      <el-radio-group v-model="formData.destFilterType" @change="onChange">
        <el-radio label="field">字段过滤</el-radio>
        <el-radio label="sql">自定义SQL</el-radio>
      </el-radio-group>
    </el-form-item>
    <MultiItemList
      v-if="formData.destFilterType === 'field'"
      label="目标表过滤条件"
      :data="formData.destFilterList"
      addText="添加"
      :disabled="formData.destTableName == null || formData.destTableName === ''"
      @add="onEditDestFilter()"
      @edit="onEditDestFilter"
      @delete="onDeleteDestFilter"
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
    <el-input v-else v-model="formData.destFilterSql" type="textarea" rows="5" @change="onChange" />
  </div>
</template>

<script setup lang="ts">
import { ElMessageBox } from 'element-plus';
import { defineProps, defineEmits } from 'vue';
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
  destDblinkId?: string;
  destDblinkType?: string;
  destTableName: string | null;
  destFilterType: string;
  destFilterList: ANY_OBJECT[];
  destFilterSql: string;
  logicDeleteField: string;
};
const formData = ref<FormDataType>({
  actionType: AutoTaskActionType.DELETE,
  destDblinkId: undefined,
  destDblinkType: undefined,
  destTableName: null,
  destFilterType: 'field',
  destFilterList: [],
  destFilterSql: '',
  logicDeleteField: '',
});
const destTableList = ref<string[]>([]);
const destColumnList = ref<ANY_OBJECT[]>([]);

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

const updateTableFilter = (row, data) => {
  if (row == null) {
    // 新建
    formData.value.destFilterList.push(data);
  } else {
    // 编辑
    formData.value.destFilterList = formData.value.destFilterList.map(item => {
      return item.filterColumnName === data.filterColumnName ? data : item;
    });
  }
  onChange();
};

const onEditDestFilter = (data?: ANY_OBJECT) => {
  Dialog.show(
    '编辑过滤字段',
    EditSrcTableFilter,
    {
      area: ['500px', '400px'],
    },
    {
      entryId: flowEntry().entryId,
      data,
      dblinkId: formData.value.destDblinkId,
      tableName: formData.value.destTableName,
      flowVariableList: getAllAutoVariableList(),
      path: 'thirdEditDeleteFilter',
    },
    {
      width: '500px',
      height: '400px',
      pathName: '/thirdParty/thirdEditTableFilter',
    },
  )
    .then(res => {
      updateTableFilter(data, res);
    })
    .catch(e => {
      console.log(e);
    });
};

const onDeleteDestFilter = data => {
  ElMessageBox.confirm('是否删除此过滤字段？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      formData.value.destFilterList = formData.value.destFilterList.filter(
        item => item.filterColumnName !== data.filterColumnName,
      );
      onChange();
    })
    .catch(e => {
      console.log(e);
    });
};

const onDestDblinkChange = val => {
  formData.value.destTableName = '';
  formData.value.destDblinkType = (
    (props.flowDblinkList || []).find(item => item.id === val) || {}
  ).type;
  onChange();
};

const onDestTableChange = val => {
  onChange();
};

watch(
  () => props.modelValue,
  val => {
    if (val) {
      let taskInfo = val && val !== '' ? JSON.parse(val) : {};
      formData.value = {
        actionType: AutoTaskActionType.DELETE,
        destDblinkId: taskInfo.destDblinkId,
        destDblinkType: taskInfo.destDblinkType,
        destTableName: taskInfo.destTableName,
        destFilterType: taskInfo.destFilterType || 'field',
        destFilterList: taskInfo.destFilterList || [],
        destFilterSql: taskInfo.destFilterSql || '',
        logicDeleteField: taskInfo.logicDeleteField || '',
      };
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

watch(
  () => formData.value.destTableName,
  val => {
    if (val) {
      FlowDblinkController.listDblinkTableColumns({
        dblinkId: formData.value.destDblinkId,
        tableName: val,
      }).then(res => {
        destColumnList.value = res.data;
      });
    } else {
      destColumnList.value = [];
    }
  },
);
</script>

<style></style>
