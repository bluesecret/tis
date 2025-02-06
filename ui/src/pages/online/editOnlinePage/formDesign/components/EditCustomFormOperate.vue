<template>
  <div class="form-single-fragment third-party-dlg" style="position: relative">
    <div class="form-box">
      <el-form
        ref="form"
        :model="formData"
        class="full-width-input"
        :rules="rules"
        style="width: 100%"
        label-width="100px"
        :size="formItemSize"
        label-position="right"
        @submit.prevent
      >
        <el-row>
          <el-col :span="24">
            <el-form-item label="操作类型" prop="type">
              <el-select
                v-model="formData.type"
                :disabled="formData.builtin || isEdit"
                @change="onFormTypeChange"
              >
                <el-option
                  v-for="item in getValidOperationType"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="操作名称" prop="name">
              <el-input
                class="input-item"
                v-model="formData.name"
                :clearable="true"
                placeholder="操作按钮名称"
              />
            </el-form-item>
          </el-col>
          <el-col
            :span="12"
            v-if="
              formData.type === SysCustomWidgetOperationType.PRINT ||
              formData.type === SysCustomWidgetOperationType.UPDATE ||
              formData.type === SysCustomWidgetOperationType.UPDATE_DIALOG
            "
          >
            <el-form-item label="表格行操作" prop="rowOperation">
              <el-switch
                v-model="formData.rowOperation"
                :disabled="
                  dialogParams.formConfig.form.formType === SysOnlineFormType.FORM ||
                  dialogParams.formConfig.form.formType === SysOnlineFormType.WORK_ORDER
                "
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否启用">
              <el-switch class="input-item" v-model="formData.enabled" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="!formData.rowOperation">
            <el-form-item label="按钮类型" prop="btnType">
              <el-select v-model="formData.btnType">
                <el-option label="primary" value="primary" />
                <el-option label="success" value="success" />
                <el-option label="warning" value="warning" />
                <el-option label="danger" value="danger" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="!formData.rowOperation">
            <el-form-item label="镂空模式" prop="btnType">
              <el-switch v-model="formData.plain" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="formData.type === SysCustomWidgetOperationType.IMPORT">
            <el-form-item label="跳过标题行">
              <el-switch v-model="formData.skipHeader" />
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="formData.rowOperation">
            <el-form-item label="操作按钮类名">
              <el-input
                class="input-item"
                v-model="formData.btnClass"
                :clearable="true"
                placeholder="操作按钮类名"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="formData.type === SysCustomWidgetOperationType.START_FLOW">
            <el-form-item label="启动流程" prop="processDefinitionKey">
              <el-cascader
                class="input-item"
                v-model="bindFlowPath"
                :options="flowTree"
                :props="{ value: 'id', label: 'name' }"
                placeholder="请选择启动流程"
                clearablebindFlowPath
                @change="flowPathChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="formData.type === SysCustomWidgetOperationType.START_FLOW">
            <el-form-item label="流程变量">
              <div class="variable-list">
                <el-tag
                  class="variable-item"
                  v-for="item in formData.taskVariableList"
                  :key="item.variableName"
                  size="large"
                  closable
                  @close="onRemoveVariable(item)"
                >
                  {{ item.variableName }}
                </el-tag>
                <el-popover
                  ref="variablePoper"
                  trigger="click"
                  width="500"
                  placement="bottom-start"
                  :modelValue="showAddVariable"
                >
                  <el-form
                    ref="addVariableForm"
                    :rules="variableRules"
                    :model="currentVariableData"
                    label-width="100px"
                    label-position="top"
                    :size="formItemSize"
                  >
                    <el-form-item label="变量类型" prop="variableType">
                      <el-select
                        v-model="currentVariableData.variableType"
                        placeholder="变量类型"
                        style="width: 100%"
                      >
                        <el-option label="固定值" :value="0" />
                        <el-option label="字段值" :value="1" />
                      </el-select>
                    </el-form-item>
                    <el-form-item label="变量名称" prop="variableName">
                      <el-input
                        v-model="currentVariableData.variableName"
                        style="width: 100%"
                        placeholder="变量名称"
                      />
                    </el-form-item>
                    <el-form-item
                      v-if="currentVariableData.variableType === 0"
                      label="变量值类型"
                      prop="valueType"
                    >
                      <el-select
                        v-model="currentVariableData.valueType"
                        style="width: 100%"
                        placeholder="变量值类型"
                      >
                        <el-option label="字符串" value="String" />
                        <el-option label="数值型" value="Number" />
                        <el-option label="日期类型" value="Date" />
                      </el-select>
                    </el-form-item>
                    <el-form-item label="变量值" prop="variableValue">
                      <template v-if="currentVariableData.variableType === 0">
                        <el-input
                          v-if="currentVariableData.valueType === 'String'"
                          style="width: 100%"
                          v-model="currentVariableData.variableValue"
                          placeholder="变量值"
                        />
                        <el-input-number
                          v-else-if="currentVariableData.valueType === 'Number'"
                          style="width: 100%"
                          v-model="currentVariableData.variableValue"
                          placeholder="变量值"
                        />
                        <el-date-picker
                          v-else-if="currentVariableData.valueType === 'Date'"
                          style="width: 100%"
                          v-model="currentVariableData.variableValue"
                          type="datetime"
                          format="yyyy-MM-dd HH:mm:ss"
                          placeholder="选择日期"
                        />
                      </template>
                      <template v-else>
                        <el-select
                          v-model="currentVariableData.variableValue"
                          placeholder="选择字段"
                          style="width: 100%"
                          clearable
                          filterable
                        >
                          <el-option
                            v-for="column in masterTableColumnList"
                            :key="column.columnId"
                            :label="column.columnName"
                            :value="column.columnName"
                          />
                        </el-select>
                      </template>
                    </el-form-item>
                    <el-row type="flex" justify="end" align="middle">
                      <el-button type="default" @click="onCancelVariable"> 取消 </el-button>
                      <el-button type="primary" @click="onAddVariable"> 确定 </el-button>
                    </el-row>
                  </el-form>
                  <template #reference>
                    <el-button class="variable-item" @click="onEditVariable()"> 添加 </el-button>
                  </template>
                </el-popover>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="按钮显示顺序">
              <el-input-number
                class="input-item"
                v-model="formData.showOrder"
                controls-position="right"
                placeholder="按钮显示顺序"
              />
            </el-form-item>
          </el-col>
          <el-col
            :span="formData.type === SysCustomWidgetOperationType.FORM ? 16 : 24"
            v-if="showSelectForm"
          >
            <el-form-item label="操作表单" prop="formId">
              <el-select
                class="input-item"
                v-model="formData.formId"
                placeholder="选择操作按钮触发表单"
                clearable
              >
                <el-option
                  v-for="form in getFormList"
                  :key="form.formId"
                  :label="form.formName"
                  :value="form.formId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8" v-if="formData.type === SysCustomWidgetOperationType.FORM">
            <el-form-item label="只读表单">
              <el-switch v-model="formData.readOnly" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="formData.type === SysCustomWidgetOperationType.PRINT">
            <el-form-item label="打印模版分组" placeholder="请选择打印模版分组">
              <el-select
                v-model="printGroupId"
                placeholder=""
                clearable
                @change="formData.printTemplateId = undefined"
              >
                <el-option
                  v-for="item in printGroupList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="formData.type === SysCustomWidgetOperationType.PRINT">
            <el-form-item label="打印模版" prop="printTemplateId" placeholder="请选择打印模版">
              <el-select
                v-model="formData.printTemplateId"
                placeholder=""
                clearable
                @change="onPrintTemplateChange"
              >
                <el-option
                  v-for="item in validPrintTemplageList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <!-- 更新字段配置 -->
          <el-col :span="24" v-if="formData.type === SysCustomWidgetOperationType.UPDATE">
            <el-form-item label="更新字段" style="margin-bottom: 20px">
              <el-row type="flex">
                <el-form
                  ref="formUpdateColumn"
                  :model="currentUpdateColumn"
                  :rules="updateColumnRules"
                  label-position="left"
                  label-width="80px"
                >
                  <el-popover
                    width="325px"
                    trigger="click"
                    v-for="updateColumn in formData.updateColumnList"
                    :key="updateColumn.updateColumnName"
                    :visible="
                      currentUpdateColumn &&
                      currentUpdateColumn.updateColumnName === updateColumn.updateColumnName &&
                      currentUpdateColumn.isEdit === true
                    "
                  >
                    <template #reference>
                      <el-tag
                        class="update-column-item"
                        size="default"
                        type="info"
                        closable
                        @click="currentUpdateColumn = { ...updateColumn }"
                        @close="onRemoveUpdateColumn(updateColumn)"
                      >
                        {{ updateColumn.updateColumnName }}
                      </el-tag>
                    </template>
                    <el-row style="width: 300px">
                      <el-col :span="24">
                        <el-form-item label="更新字段" prop="updateColumnName">
                          <el-select
                            v-model="currentUpdateColumn.updateColumnName"
                            placeholder="请选择更新字段"
                            style="width: 100%"
                            :disabled="currentUpdateColumn.isEdit"
                            clearable
                            @change="onUpdateColumnChange"
                          >
                            <el-option
                              v-for="column in dialogParams.formConfig.getMasterTable.columnList"
                              :key="column.columnId"
                              :label="column.columnName"
                              :value="column.columnName"
                            />
                          </el-select>
                        </el-form-item>
                      </el-col>
                      <el-col :span="24">
                        <el-form-item label="新字段值" prop="updateValue">
                          <el-switch
                            v-if="updateTableColumn.objectFieldType === 'Boolean'"
                            v-model="currentUpdateColumn.updateValue"
                          />
                          <el-input-number
                            v-else-if="
                              ['Integer', 'Long', 'BigDecimal', 'Double'].indexOf(
                                updateTableColumn.objectFieldType,
                              ) !== -1
                            "
                            v-model="currentUpdateColumn.updateValue"
                            placeholder="请输入更新字段值"
                            style="width: 100%"
                            clearable
                          />
                          <el-date-picker
                            v-else-if="updateTableColumn.objectFieldType === 'Date'"
                            v-model="currentUpdateColumn.updateValue"
                            type="datetime"
                            placeholder="选择日期"
                            style="width: 100%"
                            clearable
                          />
                          <el-input
                            v-else
                            v-model="currentUpdateColumn.updateValue"
                            placeholder="请输入更新字段值"
                            style="width: 100%"
                            clearable
                          />
                        </el-form-item>
                      </el-col>
                      <el-col :span="24">
                        <el-row type="flex" justify="end">
                          <el-button
                            :size="layoutStore.defaultFormItemSize"
                            type="default"
                            @click="onCancelUpdateColumn"
                            >取消</el-button
                          >
                          <el-button
                            :size="layoutStore.defaultFormItemSize"
                            type="primary"
                            @click="onSaveUpdateColumn"
                            >保存</el-button
                          >
                        </el-row>
                      </el-col>
                    </el-row>
                  </el-popover>
                  <el-popover
                    width="325px"
                    :visible="currentUpdateColumn && currentUpdateColumn.isEdit === false"
                    trigger="click"
                  >
                    <template #reference>
                      <el-button
                        class="update-column-item"
                        size="small"
                        type="default"
                        plain
                        @click="onAddUpdateColumn"
                      >
                        添加字段
                      </el-button>
                    </template>
                    <el-row style="width: 300px">
                      <el-col :span="24">
                        <el-form-item label="更新字段" prop="updateColumnName">
                          <el-select
                            v-model="currentUpdateColumn.updateColumnName"
                            placeholder="请选择更新字段"
                            style="width: 100%"
                            clearable
                            @change="onUpdateColumnChange"
                          >
                            <el-option
                              v-for="column in dialogParams.formConfig.getMasterTable.columnList"
                              :key="column.columnId"
                              :label="column.columnName"
                              :value="column.columnName"
                            />
                          </el-select>
                        </el-form-item>
                      </el-col>
                      <el-col :span="24">
                        <el-form-item label="新字段值" prop="updateValue">
                          <el-switch
                            v-if="updateTableColumn.objectFieldType === 'Boolean'"
                            v-model="currentUpdateColumn.updateValue"
                          />
                          <el-input-number
                            v-else-if="
                              ['Integer', 'Long', 'BigDecimal', 'Double'].indexOf(
                                updateTableColumn.objectFieldType,
                              ) !== -1
                            "
                            v-model="currentUpdateColumn.updateValue"
                            placeholder="请输入更新字段值"
                            style="width: 100%"
                            clearable
                          />
                          <el-date-picker
                            v-else-if="updateTableColumn.objectFieldType === 'Date'"
                            v-model="currentUpdateColumn.updateValue"
                            type="datetime"
                            placeholder="选择日期"
                            style="width: 100%"
                            clearable
                          />
                          <el-input
                            v-else
                            v-model="currentUpdateColumn.updateValue"
                            placeholder="请输入更新字段值"
                            style="width: 100%"
                            clearable
                          />
                        </el-form-item>
                      </el-col>
                      <el-col :span="24">
                        <el-row type="flex" justify="end">
                          <el-button
                            :size="layoutStore.defaultFormItemSize"
                            type="default"
                            @click="onCancelUpdateColumn"
                            >取消</el-button
                          >
                          <el-button
                            :size="layoutStore.defaultFormItemSize"
                            type="primary"
                            @click="onSaveUpdateColumn"
                            >保存</el-button
                          >
                        </el-row>
                      </el-col>
                    </el-row>
                  </el-popover>
                </el-form>
              </el-row>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <CustomEventSetting
        v-if="allowEventList.length > 0"
        label="操作脚本"
        v-model:value="formData.eventList"
        :formConfig="dialogParams.formConfig"
        labelPosition="right"
        labelWidth="100px"
        :allowEventList="allowEventList"
      />
      <!-- 导出配置 -->
      <el-col
        :span="24"
        v-show="
          formData.type === SysCustomWidgetOperationType.EXPORT ||
          formData.type === SysCustomWidgetOperationType.IMPORT
        "
        style="margin-bottom: 20px"
      >
        <vxe-table
          :data="tableColumnTree"
          ref="exportColumnTable"
          border
          show-overflow="title"
          show-header-overflow="title"
          :row-config="{ height: 35, isHover: true }"
          :tree-config="{
            transform: false,
            rowField: 'id',
            parentField: 'parentId',
            expandAll: true,
          }"
          :checkbox-config="{ checkMethod: enableChecked }"
          height="350px"
          @checkbox-all="onExportColumnChange"
          @checkbox-change="onExportColumnChange"
        >
          <vxe-column type="checkbox" width="60" />
          <vxe-column
            :title="formData.type === SysCustomWidgetOperationType.EXPORT ? '导出字段' : '导入字段'"
            field="variableName"
            tree-node
          >
            <template #default="{ row }">
              <span>{{ row.variableName }}</span>
              <el-tag
                style="margin-left: 10px"
                size="default"
                type="danger"
                v-if="row.aggregationColumnId"
                >聚合字段</el-tag
              >
              <el-tag
                style="margin-left: 10px"
                size="default"
                type="success"
                v-if="row.isTable && row.relationType != null"
                >一对一关联</el-tag
              >
            </template>
          </vxe-column>
          <vxe-column title="显示名称" field="showName">
            <template #default="{ row }">
              <el-input
                v-if="row.isColumn"
                size="default"
                :disabled="
                  !exportColumnIsSelected(row) ||
                  formData.type === SysCustomWidgetOperationType.IMPORT
                "
                v-model="row.showName"
              />
            </template>
          </vxe-column>
          <vxe-column title="显示顺序" field="showOrder">
            <template #default="{ row }">
              <el-input-number
                v-if="row.isColumn"
                size="default"
                :disabled="!exportColumnIsSelected(row)"
                v-model="row.showOrder"
                controls-position="right"
              />
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
      <!-- 打印参数配置 -->
      <el-col
        :span="24"
        v-show="formData.type === SysCustomWidgetOperationType.PRINT"
        style="margin-bottom: 20px"
      >
        <vxe-table
          :data="formData.printParamList"
          border
          show-overflow="title"
          show-header-overflow="title"
          :row-config="{ height: 35, isHover: true }"
          height="250px"
        >
          <vxe-column type="seq" title="序号" :width="60" />
          <vxe-column title="参数名称" field="paramName" />
          <vxe-column title="参数值">
            <template #default="{ row }">
              <el-select
                v-model="row.paramValue"
                clearable
                placeholder="请选择参数值"
                size="default"
              >
                <el-option
                  v-for="item in validPrintParamValueList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
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
    </div>
    <el-row class="no-scroll menu-box" type="flex" justify="end">
      <el-button :size="formItemSize" :plain="true" @click="onCancel"> 取消 </el-button>
      <el-button type="primary" :size="formItemSize" @click="onSubmit"> 保存 </el-button>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { VxeTable, VxeColumn } from 'vxe-table';
import { CascaderOption, CascaderValue, ElMessage } from 'element-plus';
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';
import CustomEventSetting from '@/online/components/CustomEventSetting/index.vue';
import {
  OnlineFormEventType,
  SysCustomWidgetOperationType,
  SysOnlineFormType,
  SysCustomWidgetType,
} from '@/common/staticDict';
import { findItemFromList, findTreeNodePath } from '@/common/utils';
import { OnlineOperationController } from '@/api/online';
import { FlowEntryController } from '@/api/flow';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

interface IProps extends ThirdProps {
  rowData: ANY_OBJECT | null;
  formList: Array<ANY_OBJECT>;
  tableList: Array<ANY_OBJECT>;
  formConfig: ANY_OBJECT;
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT>;
}
const props = defineProps<IProps>();
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize || thirdParams.value.defaultFormItemSize?.value;
});
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);

const exportColumnTable = ref();
const form = ref();
const formData = ref<ANY_OBJECT>({
  type: SysCustomWidgetOperationType.FORM,
  name: undefined,
  btnType: undefined,
  plain: false,
  enabled: true,
  builtin: false,
  rowOperation: true,
  readOnly: false,
  btnClass: 'table-btn primary',
  showOrder: 0,
  formId: undefined,
  exportColumnList: [] as (ANY_OBJECT | null)[],
  printTemplateId: '',
  printTemplateType: undefined,
  processDefinitionKey: undefined,
  skipHeader: true,
  printParamList: [] as ANY_OBJECT[],
  eventList: [] as ANY_OBJECT[],
  updateColumnList: [] as ANY_OBJECT[],
  taskVariableList: [] as ANY_OBJECT[],
});
// 当前更新字段
const currentUpdateColumn = ref({
  updateColumnName: undefined,
  updateValue: undefined,
  isEdit: undefined,
});
// 是否显示流程变量编辑
const showAddVariable = ref(false);
// 当前流程变量
const currentVariableData = ref({
  id: undefined,
  variableType: 0,
  variableName: undefined,
  valueType: 'String',
  variableValue: undefined,
});

const rules = {
  name: [{ required: true, message: '操作按钮名称不能为空', trigger: 'blur' }],
  formId: [{ required: true, message: '请选择按钮触发表单', trigger: 'blur' }],
  printTemplateId: [{ required: true, message: '请选择打印模版', trigger: 'blur' }],
  processDefinitionKey: [{ required: true, message: '请选择启动流程', trigger: 'blur' }],
};
const variableRules = {
  variableType: [{ required: true, message: '请选择变量类型', trigger: 'blur' }],
  variableName: [{ required: true, message: '请输入变量名称', trgger: 'blur' }],
  valueType: [{ required: true, message: '请选择变量值类型', trigger: 'blur' }],
  variableValue: [{ required: true, message: '请输入变量值', trigger: 'blur' }],
};

const updateColumnRules = {
  updateColumnName: [{ required: true, message: '请选择更新字段', trigger: 'blur' }],
  updateValue: [{ required: true, message: '请输入更新字段值', trigger: 'blur' }],
};

const bindFlowPath = ref<CascaderValue>([]);
const flowTree = ref<CascaderOption[]>([]);
const tableColumnTree = ref<ANY_OBJECT[]>([]);
const exportColumn = ref<ANY_OBJECT[]>([]);
const printGroupId = ref();
const printGroupList = ref<ANY_OBJECT[]>([]);
const printTemplateList = ref<ANY_OBJECT[]>([]);
const dialogParams = computed(() => {
  return {
    rowData: props.rowData || thirdParams.value.rowData,
    formList: props.formList || thirdParams.value.formList,
    tableList: props.tableList || thirdParams.value.tableList,
    formConfig: props.formConfig || thirdParams.value.formConfig,
  };
});
const isEdit = computed(() => {
  return dialogParams.value.rowData != null;
});
const allowEventList = computed(() => {
  if (dialogParams.value.formConfig.form.formType === SysOnlineFormType.WORK_ORDER) {
    return [];
  } else {
    return [OnlineFormEventType.OPERATION_VISIBLE, OnlineFormEventType.OPERATION_DISABLED];
  }
});
const getFormList = computed(() => {
  return dialogParams.value.formList.filter(item => {
    if (formData.value.type === SysCustomWidgetOperationType.FORM) {
      return true;
    } else {
      return item.formType === SysOnlineFormType.FORM;
    }
  });
});
const getValidOperationType = computed(() => {
  let tempList = SysCustomWidgetOperationType.getList().filter(item => {
    if (isEdit.value) return true;
    if (dialogParams.value.formConfig.form.formType === SysOnlineFormType.FORM) {
      return (
        item.id === SysCustomWidgetOperationType.PRINT ||
        item.id === SysCustomWidgetOperationType.FORM
      );
    } else if (dialogParams.value.formConfig.form.formType === SysOnlineFormType.WORK_ORDER) {
      return item.id === SysCustomWidgetOperationType.PRINT;
    } else {
      return (
        [
          SysCustomWidgetOperationType.FORM,
          SysCustomWidgetOperationType.PRINT,
          SysCustomWidgetOperationType.COPY,
          SysCustomWidgetOperationType.START_FLOW,
        ].indexOf(item.id) !== -1
      );
    }
  });

  if (
    currentWidget.value &&
    currentWidget.value.widgetType === SysCustomWidgetType.Table &&
    !isEdit.value
  ) {
    // 添加行内编辑操作
    tempList.push({
      id: SysCustomWidgetOperationType.INLINE_ADD,
      name: SysCustomWidgetOperationType.getValue(SysCustomWidgetOperationType.INLINE_ADD),
    });
    tempList.push({
      id: SysCustomWidgetOperationType.INLINE_EDIT,
      name: SysCustomWidgetOperationType.getValue(SysCustomWidgetOperationType.INLINE_EDIT),
    });
    if (
      dialogParams.value.formConfig.form.formType === SysOnlineFormType.QUERY ||
      dialogParams.value.formConfig.form.formType === SysOnlineFormType.ADVANCE_QUERY ||
      dialogParams.value.formConfig.form.formType === SysOnlineFormType.GROUP_QUERY
    ) {
      // 查询页面和左树右表页面，添加部分更新操作
      tempList.push({
        id: SysCustomWidgetOperationType.UPDATE,
        name: SysCustomWidgetOperationType.getValue(SysCustomWidgetOperationType.UPDATE),
      });
      tempList.push({
        id: SysCustomWidgetOperationType.UPDATE_DIALOG,
        name: SysCustomWidgetOperationType.getValue(SysCustomWidgetOperationType.UPDATE_DIALOG),
      });
    }
  }

  return tempList;
});

const validPrintParamValueList = computed(() => {
  let temp =
    dialogParams.value.formConfig.getMasterTable == null
      ? []
      : (dialogParams.value.formConfig.getMasterTable.columnList || []).map((item: ANY_OBJECT) => {
          return {
            id: item.columnId,
            name: item.columnName,
          };
        });
  if (dialogParams.value.formConfig.form.formType === SysOnlineFormType.WORK_ORDER) {
    temp.unshift({
      id: 'workOrderId',
      name: 'workOrderId',
    });
  }
  return temp;
});

const validPrintTemplageList = computed(() => {
  return printTemplateList.value.filter(item => {
    return item.groupId === printGroupId.value;
  });
});

const showSelectForm = computed(() => {
  return (
    formData.value.type === SysCustomWidgetOperationType.ADD ||
    formData.value.type === SysCustomWidgetOperationType.EDIT ||
    formData.value.type === SysCustomWidgetOperationType.FORM ||
    formData.value.type === SysCustomWidgetOperationType.COPY ||
    formData.value.type === SysCustomWidgetOperationType.UPDATE_DIALOG
  );
});

const updateTableColumn = computed(() => {
  if (currentUpdateColumn.value == null) return {};
  let temp = dialogParams.value.formConfig.getMasterTable.columnList.find(
    column => column.columnName === currentUpdateColumn.value.updateColumnName,
  );
  return temp || {};
});

const currentWidget = computed(() => {
  return dialogParams.value.formConfig.currentWidget;
});

const masterTableColumnList = computed(() => {
  let masterTable = dialogParams.value.tableList[0];
  if (masterTable != null) {
    return masterTable.columnList;
  } else {
    return [];
  }
});

const flowPathChange = () => {
  if (Array.isArray(bindFlowPath.value)) {
    formData.value.processDefinitionKey = bindFlowPath.value[bindFlowPath.value.length - 1];
  }
};

const onEditVariable = item => {
  if (item == null) {
    currentVariableData.value = {
      id: undefined,
      variableType: 0,
      variableName: undefined,
      valueType: 'String',
      variableValue: undefined,
    };
  } else {
    currentVariableData.value = { ...item };
  }
  showAddVariable.value = true;
};

const addVariableForm = ref();
const variablePoper = ref();
const onRemoveVariable = item => {
  formData.value.taskVariableList = formData.value.taskVariableList.filter(
    variable => variable.id !== item.id,
  );
};

const onAddVariable = () => {
  addVariableForm.value.validate(valid => {
    if (!valid) return;
    if (currentVariableData.value.id == null) {
      currentVariableData.value.id = new Date().getTime();
      formData.value.taskVariableList.push({ ...currentVariableData.value });
    } else {
      let index = formData.value.taskVariableList.findIndex(
        variable => variable.id === currentVariableData.value.id,
      );
      formData.value.taskVariableList.splice(index, 1, {
        ...currentVariableData.value,
      });
    }
    onCancelVariable();
  });
};

const onCancelVariable = () => {
  addVariableForm.value.clearValidate();
  currentVariableData.value = {
    id: undefined,
    variableType: 0,
    variableName: undefined,
    valueType: 'String',
    variableValue: undefined,
  };
  variablePoper.value.hide();
  showAddVariable.value = false;
};
const onCancel = () => {
  if (
    formData.value.type === SysCustomWidgetOperationType.EXPORT ||
    formData.value.type === SysCustomWidgetOperationType.IMPORT
  ) {
    formData.value.exportColumnList = (exportColumn.value || [])
      .map(column => {
        return column && column.isColumn
          ? {
              tableId: column.table.tableId,
              columnId: column.aggregationColumn ? undefined : column.columnId,
              virtualColumnId: column.aggregationColumnId,
              showName: column.showName,
              showOrder: column.showOrder,
            }
          : undefined;
      })
      .filter(item => item != null);
  } else {
    formData.value.exportColumnList = [];
  }
  if (props.dialog) {
    props.dialog.cancel();
  } else {
    onCloseThirdDialog(false);
  }
};

const onSubmit = () => {
  form.value.validate((valid: boolean) => {
    if (!valid) return;
    if (showSelectForm.value && !formData.value.formId) {
      ElMessage.error('请选择操作按钮触发表单');
      return;
    }
    if (
      formData.value.type === SysCustomWidgetOperationType.EXPORT ||
      formData.value.type === SysCustomWidgetOperationType.IMPORT
    ) {
      formData.value.exportColumnList = (exportColumn.value || [])
        .map(column => {
          return column && column.isColumn
            ? {
                tableId: column.table.tableId,
                columnId: column.aggregationColumn ? undefined : column.columnId,
                virtualColumnId: column.aggregationColumnId,
                showName: column.showName,
                showOrder: column.showOrder,
              }
            : undefined;
        })
        .filter(item => item != null);
    } else {
      formData.value.exportColumnList = [];
    }
    if (props.dialog) {
      props.dialog.submit(formData.value);
    } else {
      onCloseThirdDialog(true, dialogParams.value.rowData, formData.value);
    }
  });
};

const onFormTypeChange = () => {
  formData.value.rowOperation = formData.value.type !== SysCustomWidgetOperationType.INLINE_ADD;
  formData.value.readOnly = false;
  formData.value.formId = undefined;
  formData.value.printTemplateId = '';
  formData.value.printTemplateType = undefined;
  formData.value.btnType =
    formData.value.type !== SysCustomWidgetOperationType.INLINE_ADD ? undefined : 'primary';
};
const onExportColumnChange = () => {
  exportColumn.value = exportColumnTable.value.getCheckboxRecords(true);
};
const exportColumnIsSelected = (row: ANY_OBJECT) => {
  if (Array.isArray(exportColumn.value)) {
    return exportColumn.value.indexOf(row) !== -1;
  } else {
    return false;
  }
};
const enableChecked = ({ row }: { row: ANY_OBJECT }) => {
  return row.isColumn;
};

const getTableColumnTree = () => {
  tableColumnTree.value = [];
  let selectedRows: ANY_OBJECT[] = [];
  if (Array.isArray(dialogParams.value.tableList)) {
    let tempTableList = dialogParams.value.tableList;
    // 如果是导入只取主表，也就是第一个表
    if (formData.value.type === SysCustomWidgetOperationType.IMPORT && tempTableList.length > 0) {
      tempTableList = [tempTableList[0]];
    }
    tableColumnTree.value = tempTableList.map(item => {
      return {
        variableName: item.tableName,
        id: item.tableId,
        isTable: true,
        relationType: item.relationType,
        children: (item.columnList || []).map((column: ANY_OBJECT) => {
          let columnInfo = findItemFromList(
            formData.value.exportColumnList,
            column.aggregationColumnId,
            'virtualColumnId',
          );
          if (columnInfo == null)
            columnInfo = findItemFromList(
              formData.value.exportColumnList,
              column.columnId,
              'columnId',
            );
          let temp = {
            ...column,
            id: column.aggregationColumnId || column.columnId,
            table: item,
            variableName: column.columnName,
            showName: (columnInfo || {}).showName || column.columnComment,
            showOrder: (columnInfo || {}).showOrder || 0,
            isColumn: true,
          };
          if (columnInfo != null) selectedRows.push(temp);
          return temp;
        }),
      };
    });
  }
  return selectedRows;
};

const loadFlowTree = () => {
  FlowEntryController.listAll({})
    .then(res => {
      let flowCategoryList = res.data.flowCategoryList;
      let flowEntryList = res.data.flowEntryList;
      flowTree.value = flowCategoryList.map((category: ANY_OBJECT) => {
        return {
          id: category.categoryId,
          name: category.name,
          children: flowEntryList
            .filter((entry: ANY_OBJECT) => entry.categoryId === category.categoryId)
            .map((entry: ANY_OBJECT) => {
              return {
                id: entry.processDefinitionKey,
                name: entry.processDefinitionName,
              };
            }),
        };
      });
      if (formData.value.processDefinitionKey) {
        bindFlowPath.value = findTreeNodePath(
          flowTree.value,
          formData.value.processDefinitionKey,
          'id',
        );
      }
    })
    .catch(e => {
      console.error(e);
    });
};

const printTemplate = computed(() => {
  return findItemFromList(printTemplateList.value, formData.value.printTemplateId, 'id');
});
const loadPrintTemplate = () => {
  OnlineOperationController.getPrintTemplate({})
    .then(res => {
      printGroupList.value = res.data.allPrintGroupList.map((item: ANY_OBJECT) => {
        return {
          id: item.groupId,
          name: item.groupName,
        };
      });
      printTemplateList.value = res.data.allPrintList.map((item: ANY_OBJECT) => {
        let printJosn = item.printJson ? JSON.parse(item.printJson) : {};
        let templateType = printJosn.templateType || 0;
        return {
          id: item.printId,
          groupId: item.groupId,
          name: item.printName,
          templateType: templateType,
          paramList: item.paramJson ? JSON.parse(item.paramJson) : [],
        };
      });
      printGroupId.value = (printTemplate.value || {}).groupId;
      onPrintTemplateChange(true);
    })
    .catch(e => {
      console.warn(e);
    });
};

const onPrintTemplateChange = (init = false) => {
  formData.value.printParamList = [];
  formData.value.printTemplateType = (printTemplate.value || {}).templateType;
  if (printTemplate.value != null && Array.isArray(printTemplate.value.paramList)) {
    formData.value.printParamList = printTemplate.value.paramList.map((item: ANY_OBJECT) => {
      // return {
      //   paramName: item.variableName,
      //   paramValue: undefined,
      // };
      if (init && dialogParams.value.rowData != null) {
        let tempValue = (dialogParams.value.rowData.printParamList || []).find(
          (param: ANY_OBJECT) => param.paramName === item.variableName,
        );
        return {
          paramName: item.variableName,
          paramValue: (tempValue || {}).paramValue,
        };
      } else {
        return {
          paramName: item.variableName,
          paramValue: undefined,
        };
      }
    });
  }
};
// 更新字段配置
const formUpdateColumn = ref();
const onAddUpdateColumn = () => {
  currentUpdateColumn.value = {
    updateColumnName: undefined,
    updateValue: undefined,
    isEdit: false,
  };
};

const onCancelUpdateColumn = () => {
  currentUpdateColumn.value = {
    updateColumnName: undefined,
    updateValue: undefined,
    isEdit: undefined,
  };
  if (formUpdateColumn.value) formUpdateColumn.value.clearValidate();
};

const onSaveUpdateColumn = () => {
  formUpdateColumn.value.validate(valid => {
    if (!valid) return;
    const bFind = formData.value.updateColumnList.some(
      column => column.updateColumnName === currentUpdateColumn.value.updateColumnName,
    );
    if (bFind) {
      // 更新字段
      formData.value.updateColumnList = formData.value.updateColumnList.map(item => {
        return item.updateColumnName === currentUpdateColumn.value.updateColumnName
          ? currentUpdateColumn.value
          : item;
      });
    } else {
      // 新增字段
      currentUpdateColumn.value.isEdit = true;
      formData.value.updateColumnList.push({ ...currentUpdateColumn.value });
    }
    formUpdateColumn.value.clearValidate();
    onCancelUpdateColumn();
  });
};

const onRemoveUpdateColumn = item => {
  formData.value.updateColumnList = formData.value.updateColumnList.filter(
    column => column.updateColumnName !== item.updateColumnName,
  );
};

const onUpdateColumnChange = () => {
  currentUpdateColumn.value.updateValue = undefined;
  if (formUpdateColumn.value) formUpdateColumn.value.clearValidate();
};

onMounted(() => {
  exportColumn.value = [];
  if (dialogParams.value.rowData != null) {
    formData.value = {
      ...formData.value,
      ...dialogParams.value.rowData,
    };
    if (
      formData.value.type === SysCustomWidgetOperationType.EXPORT ||
      formData.value.type === SysCustomWidgetOperationType.IMPORT
    ) {
      exportColumn.value = getTableColumnTree();
      setTimeout(() => {
        exportColumnTable.value.setCheckboxRow(exportColumn.value, true);
      }, 100);
    }
  } else {
    switch (dialogParams.value.formConfig.form.formType) {
      case SysOnlineFormType.FORM:
      case SysOnlineFormType.WORK_ORDER:
        formData.value.type = SysCustomWidgetOperationType.PRINT;
        formData.value.rowOperation =
          dialogParams.value.formConfig.form.formType === SysOnlineFormType.WORK_ORDER;
        break;
      default:
        formData.value.type = SysCustomWidgetOperationType.FORM;
        break;
    }
  }
  loadPrintTemplate();
  loadFlowTree();
});
</script>

<style scoped>
.update-column-item {
  margin-right: 8px;
}
.variable-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  align-items: center;
}
.variable-item {
  margin-right: 10px;
  margin-bottom: 10px;
  cursor: pointer;
}
</style>
