<template>
  <div class="custom-query-group">
    <div
      class="group-item"
      v-for="(filterItem, filterItemIndex) in modelValue.filterList"
      :key="filterItem.columnName"
    >
      <!-- 每一个过滤项的逻辑关系 -->
      <div class="item-logic-operator">
        <el-dropdown
          :disabled="filterItemIndex === 0"
          trigger="click"
          @command="value => handlerFilterItemLogicOperatorChange(filterItem, value)"
        >
          <span style="height: 32px; line-height: 32px; color: #606266">
            {{ LogicOperatorType.getValue(filterItem.logicOperator) }}
            <el-icon v-if="filterItemIndex !== 0"><CaretBottom /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item
                v-for="item in LogicOperatorType.getList()"
                :key="item.id"
                :command="item.id"
              >
                <span :class="{ 'el-dropdown-item-active': item.id == modelValue.logicOperator }">
                  {{ item.name }}
                </span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <!-- 每一个过滤项的内容 -->
      <div class="group-item-content">
        <div class="title">
          <!-- 过滤字段选择 -->
          <el-popover
            placement="bottom-start"
            trigger="click"
            popper-class="custom-query-group-field-select-popper"
          >
            <el-cascader-panel
              :modelValue="[filterItem.tableName, filterItem.columnName]"
              :options="fieldList"
              :props="{
                value: 'id',
                label: 'name',
                children: 'children',
              }"
              @change="val => handlerFiledSelect(filterItem, val)"
            />
            <template #reference>
              <span style="color: #606266; font-size: 14px; cursor: pointer">
                {{ getFilterItemBindColumnShowName(filterItem) }}
              </span>
            </template>
          </el-popover>
          <!-- 过滤逻辑选择 -->
          <el-dropdown
            trigger="click"
            style="margin-left: 10px"
            @command="val => handleFilterTypeChange(filterItem, val)"
          >
            <span class="el-dropdown-link">
              {{ CriteriaFilterType.getValue(filterItem.filterType) || '等于' }}
              <el-icon><CaretBottom /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                  v-for="item in getValidateFilterTypeList(filterItem)"
                  :key="item.id"
                  :disabled="item.disabled"
                  :command="item.id"
                >
                  <span
                    class="el-dropdown-item-select"
                    :class="{
                      'el-dropdown-item-active': item.id == filterItem.filterType,
                    }"
                  >
                    {{ item.name }}
                  </span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <div style="width: 100px; flex-grow: 1; flex-shrink: 1" />
          <!-- 过滤项删除 -->
          <div class="rules-tool" @click="deleteFilterItem(filterItem)">
            <el-icon><Close /></el-icon>
          </div>
        </div>
        <!-- 过滤值选择 -->
        <div
          class="filter-value-select"
          v-if="
            filterItem.filterType !== CriteriaFilterType.NOT_NULL &&
            filterItem.filterType !== CriteriaFilterType.IS_NULL
          "
        >
          <!-- 字典值类型选择 -->
          <el-select
            v-model="filterItem.valueType"
            :size="defaultFormItemSize"
            style="margin-right: 10px; width: 250px"
          >
            <el-option
              :label="CustomQueryFilterValueType.getValue(CustomQueryFilterValueType.FIXED)"
              :value="CustomQueryFilterValueType.FIXED"
              :disabled="!supportValueTypeFixed(filterItem)"
            />
            <el-option
              :label="CustomQueryFilterValueType.getValue(CustomQueryFilterValueType.DICT)"
              :value="CustomQueryFilterValueType.DICT"
            />
          </el-select>
          <!-- 字典值 -->
          <el-cascader
            v-if="filterItem.valueType == CustomQueryFilterValueType.DICT"
            :modelValue="filterItem.filterItemValue"
            :size="defaultFormItemSize"
            popper-class="custom-query-group-casecade-popper"
            style="width: 100%"
            :props="{
              multiple: isMultiSelect(filterItem),
              value: 'id',
              label: 'name',
              checkStrictly: true,
              lazy: true,
              lazyLoad: (node, resolve) => lazyLoadDict(filterItem, node, resolve),
            }"
            clearable
            collapse-tags
            collapse-tags-tooltip
            @update:modelValue="val => onDictValueChange(filterItem, val)"
          />
          <template v-else>
            <!-- 固定值 -->
            <el-input-number
              v-if="getColumnType(filterItem) === 'Number'"
              v-model="filterItem.filterItemValue"
              :size="defaultFormItemSize"
              style="width: 100%"
              clearable
              placeholder="请输入过滤值"
            />
            <el-date-picker
              v-else-if="getColumnType(filterItem) === 'Date'"
              v-model="filterItem.filterItemValue"
              :size="defaultFormItemSize"
              type="datetime"
              style="width: 100%"
              clearable
              value-format="YYYY-MM-DD HH:mm:ss"
              format="YYYY-MM-DD HH:mm:ss"
              placeholder="请输入过滤值"
            />
            <el-switch
              v-else-if="getColumnType(filterItem) === 'Boolean'"
              v-model="filterItem.filterItemValue"
              style="width: 100%"
              :size="defaultFormItemSize"
            />
            <el-input
              v-else
              v-model="filterItem.filterItemValue"
              :size="defaultFormItemSize"
              style="width: 100%"
              clearable
              placeholder="请输入过滤值"
            />
          </template>
        </div>
        <!-- 过滤项工具栏 -->
        <div class="filter-item-tool" v-if="filterItemIndex === modelValue.filterList.length - 1">
          <el-icon @click="addFilterItem()"><CirclePlus /></el-icon>
        </div>
      </div>
    </div>
    <!-- 每个分组之间的过滤逻辑 -->
    <div class="group-logic-operator">
      <el-divider>
        <template #default>
          <el-dropdown trigger="click" @command="handlerGroupLogicOperatorChange">
            <span class="el-dropdown-link">
              {{ LogicOperatorType.getValue(modelValue.logicOperator) || '且' }}
              <el-icon><CaretBottom /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                  v-for="item in LogicOperatorType.getList()"
                  :key="item.id"
                  :command="item.id"
                >
                  <span :class="{ 'el-dropdown-item-active': item.id == modelValue.logicOperator }">
                    {{ item.name }}
                  </span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </el-divider>
      <div class="group-tool">
        <el-icon @click="addGroup()"><CirclePlus /></el-icon>
        <el-icon style="margin-left: 5px" @click="deleteGroup()"><Delete /></el-icon>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';
import { treeDataTranslate, traverseTree } from '@/common/utils';
import {
  CustomQueryFilterValueType,
  CriteriaFilterType,
  LogicOperatorType,
} from '@/common/staticDict/index';
import {
  CustomQueryItem,
  CustomQueryGroup,
  CustomQueryFieldItem,
  CustomQueryDictItem,
} from '../types';
import { CaretBottom, Close, CirclePlus, Delete } from '@element-plus/icons-vue';

defineOptions({
  name: 'CustomQueryGroup',
});

const emit = defineEmits(['update:modelValue', 'add', 'delete']);

type IProps = {
  modelValue?: CustomQueryGroup;
  fieldList?: CustomQueryFieldItem[];
  dictList?: CustomQueryDictItem[];
  getDictDataList?: (
    dictInfo: ANY_OBJECT,
    params: ANY_OBJECT,
    rootDataWhenNoParentId?: boolean,
  ) => Promise<ANY_OBJECT>;
};

const props = withDefaults(defineProps<IProps>(), {
  fieldList: () => [],
  dictList: () => [],
});

const onChange = (value: CustomQueryGroup) => {
  emit('update:modelValue', value);
};

const isMultiSelect = (filterItem: CustomQueryItem) => {
  return (
    filterItem.valueType === CustomQueryFilterValueType.DICT &&
    (filterItem.filterType === CriteriaFilterType.IN ||
      filterItem.filterType === CriteriaFilterType.NOT_IN)
  );
};

const getValidateFilterTypeList = filterItem => {
  let bindColumn = getFilterItemBindColumn(filterItem);
  return CriteriaFilterType.getList()
    .filter(item => {
      return item.id !== CriteriaFilterType.BETWEEN;
    })
    .map(item => {
      let disabled = false;
      if (item.id === CriteriaFilterType.IN || item.id === CriteriaFilterType.NOT_IN) {
        disabled = !(bindColumn && bindColumn.dictInfo);
      }
      return {
        ...item,
        disabled,
      };
    });
};

const supportValueTypeFixed = filterItem => {
  let bindColumn = getFilterItemBindColumn(filterItem);
  if (bindColumn == null) return true;
  return (
    filterItem.filterType !== CriteriaFilterType.IN &&
    filterItem.filterType !== CriteriaFilterType.NOT_IN
  );
};

const getFilterItemBindColumnShowName = (filterItem: CustomQueryItem) => {
  const bindTable = props.fieldList.find(item => item.id === filterItem.tableName);
  if (bindTable) {
    const bindColumn = bindTable.children.find(item => item.id === filterItem.columnName);
    return bindTable.name + ' / ' + bindColumn.name;
  }
  return 'T 描述';
};

const getFilterItemBindColumn = (filterItem?: CustomQueryItem) => {
  const bindTable = props.fieldList.find(item => item.id === filterItem.tableName);
  if (bindTable) {
    return bindTable.children.find(item => item.id === filterItem.columnName);
  } else {
    return undefined;
  }
};

const onDictValueChange = (filterItem: CustomQueryItem, val: ANY_OBJECT) => {
  if (isMultiSelect(filterItem)) {
    filterItem.filterItemValue = (val || []).filter(item => {
      return item.length > 1;
    });
  } else {
    filterItem.filterItemValue = val && val.length > 1 ? val : undefined;
  }
  // onChange(props.modelValue);
};

const getFilterItemDictList = (filterItem: CustomQueryItem) => {
  let tempList = [];
  if (filterItem.valueType === CustomQueryFilterValueType.DICT) {
    let bindColumn = getFilterItemBindColumn(filterItem);
    if (bindColumn && bindColumn.dictInfo) {
      tempList = props.dictList.filter(item => item.id === bindColumn.dictInfo.dictId);
    }
    if (tempList.length <= 0) {
      tempList = props.dictList;
    }
    return tempList;
  }
  return [];
};

const lazyLoadDict = (filterItem, node, resolve) => {
  let bindColumn = getFilterItemBindColumn(filterItem);
  if (node.level === 0) {
    let tempList = getFilterItemDictList(filterItem);
    return resolve(tempList);
  }
  const dictInfo = (bindColumn || {}).dictInfo;
  const parentNode = node.data;
  const children = parentNode.children;
  if (node.level > 1) {
    resolve(children);
    return;
  }
  if (props.getDictDataList == null) {
    resolve([]);
    return;
  }
  if (children && children.length > 0) {
    resolve(children);
    return;
  }
  props
    .getDictDataList(parentNode, {}, false)
    .then(data => {
      if (parentNode && parentNode.treeFlag) {
        data = treeDataTranslate(data);
      }
      traverseTree(data, node => {
        node.leaf = !node.children || node.children.length <= 0;
      });
      parentNode.children = data;
      resolve(data);
    })
    .catch(() => {
      resolve([]);
    });
};

const getColumnDataType = objectFieldType => {
  switch (objectFieldType) {
    case 'String':
      return 'String';
    case 'Date':
      return 'Date';
    case 'Boolean':
      return 'Boolean';
    case 'Integer':
    case 'Long':
    case 'Float':
    case 'Double':
    case 'BigDecimal':
      return 'Number';
    default:
      return undefined;
  }
};

const getColumnType = (filterItem: CustomQueryItem) => {
  const bindColumn = getFilterItemBindColumn(filterItem);
  if (bindColumn) {
    return getColumnDataType(bindColumn.objectFieldType);
  } else {
    return 'String';
  }
};

const handlerFilterItemLogicOperatorChange = (filterItem: CustomQueryItem, val: string) => {
  filterItem.logicOperator = val;
  let tempValue = {
    ...props.modelValue,
    filterList: props.modelValue.filterList,
  };
  onChange(tempValue);
};

const addFilterItem = () => {
  let tempValue = {
    ...props.modelValue,
  };
  tempValue.filterList.push({
    tableName: undefined,
    columnName: undefined,
    valueType: CustomQueryFilterValueType.FIXED,
    filterType: CriteriaFilterType.EQ,
    logicOperator: LogicOperatorType.AND,
    filterItemValue: '',
  });
  onChange(tempValue);
};

const deleteFilterItem = (filterItem: CustomQueryItem) => {
  let tempValue = {
    ...props.modelValue,
  };
  if (tempValue.filterList.length === 1) {
    tempValue.filterList = [];
    tempValue.filterList.push({
      tableName: undefined,
      columnName: undefined,
      valueType: CustomQueryFilterValueType.FIXED,
      filterType: CriteriaFilterType.EQ,
      logicOperator: LogicOperatorType.AND,
      filterItemValue: '',
    });
  } else {
    tempValue.filterList = props.modelValue.filterList.filter(item => {
      return item !== filterItem;
    });
  }
  onChange(tempValue);
};

const handlerFiledSelect = (filterItem: CustomQueryItem, val) => {
  filterItem.tableName = val[0];
  filterItem.columnName = val[1];
  filterItem.filterType = CriteriaFilterType.EQ;
  let bindColumn = getFilterItemBindColumn(filterItem);
  // Boolean 类型默认值为 false
  if (bindColumn && getColumnType(filterItem) === 'Boolean') {
    filterItem.filterItemValue = false;
  } else {
    filterItem.filterItemValue = '';
  }
  // 绑定字段支持字典，则默认值类型为字典
  if (bindColumn && bindColumn.dictInfo) {
    filterItem.valueType = CustomQueryFilterValueType.DICT;
  } else {
    filterItem.valueType = CustomQueryFilterValueType.FIXED;
  }
  let tempValue = {
    ...props.modelValue,
  };
  onChange(tempValue);
};

const handlerGroupLogicOperatorChange = val => {
  let tempValue = {
    ...props.modelValue,
    logicOperator: val,
  };
  onChange(tempValue);
};

const addGroup = () => {
  emit('add');
};

const deleteGroup = () => {
  emit('delete', props.modelValue);
};

const handleFilterTypeChange = (filterItem, val) => {
  filterItem.filterType = val;
  filterItem.filterItemValue = '';
  onChange(props.modelValue);
};
</script>

<style scoped>
.custom-query-group {
  display: flex;
  flex-direction: column;
}
.group-item {
  display: flex;
}
.group-item + .group-item {
  margin-top: 10px;
}
.item-logic-operator {
  width: 40px;
  height: 32px;
  line-height: 32px;
  flex-grow: 0;
  flex-shrink: 0;
}
.group-item-content {
  width: 200px;
  flex-grow: 1;
  flex-shrink: 1;
}
.group-item-content .title {
  display: flex;
  align-items: center;
  width: 100%;
  height: 32px;
  line-height: 32px;
  flex-grow: 1;
  flex-shrink: 1;
  margin-bottom: 10px;
}
.filter-value-select {
  display: flex;
  align-items: center;
  width: 100%;
}
.group-logic-operator {
  position: relative;
  width: 100%;
}
.group-tool {
  position: absolute;
  top: 13px;
  right: 0;
  display: flex;
  align-items: center;
  font-size: 16px;
  background: white;
  height: 24px;
  padding-left: 6px;
}
.group-tool .el-icon {
  cursor: pointer;
}
.filter-item-tool {
  height: 32px;
  line-height: 32px;
  font-size: 16px;
  margin-top: 5px;
}
.filter-item-tool .el-icon {
  cursor: pointer;
}
.rules-tool {
  width: 24px;
  height: 24px;
  font-size: 16px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
.rules-tool:hover {
  background-color: rgba(0, 0, 0, 0.1);
}
.custom-query-group /deep/ .el-dropdown {
  cursor: pointer;
}
</style>

<style>
.custom-query-group-casecade-popper .el-cascader-panel .el-cascader-menu:first-child .el-checkbox {
  display: none;
}
.custom-query-group-casecade-popper .el-cascader-panel .el-cascader-menu:first-child .el-radio {
  display: none;
}
.custom-query-group-field-select-popper {
  width: auto !important;
  padding: 0px !important;
}
</style>
