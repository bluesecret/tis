<template>
  <div class="custom-query">
    <el-scrollbar class="group-list custom-scroll" style="height: 600px">
      <TableCustomQueryGroup
        v-for="(group, index) in groupList"
        :key="index"
        :fieldList="fieldList"
        :dictList="dictList"
        :historyList="queryHistoryList"
        :getDictDataList="getDictDataList"
        :modelValue="group"
        @update:modelValue="val => updateGroup(group, val)"
        @add="addGroup"
        @delete="removeGroup(group)"
      />
    </el-scrollbar>
    <div class="btn-group">
      <span style="font-size: 14px; padding-right: 10px">历史查询记录</span>
      <el-select
        v-model="currentHistory"
        :size="defaultFormItemSize"
        placeholder="历史查询记录"
        style="width: 250px"
      >
        <el-option-group label="历史查询记录(最多10条)" v-if="queryHistoryList.length > 0">
          <el-option
            v-for="item in queryHistoryList"
            :key="item.id"
            :label="item.name"
            :value="item.data"
          />
        </el-option-group>
      </el-select>
      <div style="flex: 1"></div>
      <el-button :plain="true" :size="defaultFormItemSize" @click="cancel">取消</el-button>
      <el-button :plain="true" type="primary" :size="defaultFormItemSize" @click="reset">
        重置
      </el-button>
      <el-button type="primary" :size="defaultFormItemSize" @click="submit">查询</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  CustomQueryItem,
  CustomQueryGroup,
  CustomQueryFieldItem,
  CustomQueryDictItem,
} from '../types';

import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';
import {
  CustomQueryFilterValueType,
  CriteriaFilterType,
  LogicOperatorType,
} from '@/common/staticDict/index';
import TableCustomQueryGroup from './CustomQueryGroup.vue';

defineOptions({
  name: 'CustomQuery',
});

type IProps = {
  data: CustomQueryGroup[];
  dictList: CustomQueryDictItem[];
  fieldList: CustomQueryFieldItem[];
  customQueryHistory: ANY_OBJECT[];
  getDictDataList?: (
    dictInfo: ANY_OBJECT,
    params: ANY_OBJECT,
    rootDataWhenNoParentId?: boolean,
  ) => Promise<ANY_OBJECT>;
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT | ANY_OBJECT[] | undefined>;
};

const props = withDefaults(defineProps<IProps>(), {
  data: () => [],
  dictList: () => [],
  fieldList: () => [],
  customQueryHistory: () => [],
  getDictDataList: undefined,
});

const groupList = ref<CustomQueryGroup[]>([]);
const currentHistory = ref();

const queryHistoryList = computed(() => {
  console.log('props.customQueryHistory', props.customQueryHistory);
  return props.customQueryHistory.map((item: ANY_OBJECT) => {
    return {
      id: item.id,
      name: item.name,
      data: item.data,
    };
  });
});

const addGroup = () => {
  groupList.value.push({
    id: new Date().getTime(),
    logicOperator: LogicOperatorType.AND,
    filterList: [
      {
        tableName: undefined,
        columnName: undefined,
        valueType: CustomQueryFilterValueType.FIXED,
        filterType: CriteriaFilterType.EQ,
        logicOperator: LogicOperatorType.AND,
        filterItemValue: '',
      },
    ],
  });
};

const updateGroup = (group: CustomQueryGroup, val: CustomQueryGroup) => {
  const index = groupList.value.findIndex(item => item === group);
  groupList.value.splice(index, 1, val);
};

const reset = () => {
  groupList.value = [];
  addGroup();
};

const removeGroup = (group: CustomQueryGroup) => {
  if (groupList.value.length === 1) {
    reset();
  } else {
    const index = groupList.value.findIndex(item => item === group);
    groupList.value.splice(index, 1);
  }
};

const submit = () => {
  if (props.dialog) {
    props.dialog.submit(groupList.value);
  }
};

const cancel = () => {
  if (props.dialog) {
    props.dialog.cancel(false);
  }
};

watch(
  () => queryHistoryList.value,
  val => {
    if (Array.isArray(val) && val.length > 0) {
      currentHistory.value = val[0].data;
    } else {
      currentHistory.value = undefined;
    }
  },
  { immediate: true, deep: true },
);

watch(
  () => currentHistory.value,
  val => {
    if (val == null || val === '') {
      reset();
    } else {
      setTimeout(() => {
        groupList.value = JSON.parse(val);
      }, 10);
    }
  },
  { immediate: true },
);
</script>

<style scoped>
.custom-query {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  background-color: #ffff;
}

.group-list {
  flex-grow: 1;
  flex-shrink: 0;
  height: 100px;
}

.btn-group {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding-top: 15px;
}
</style>
