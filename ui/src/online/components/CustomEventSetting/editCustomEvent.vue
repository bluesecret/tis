<template>
  <div class="form-single-fragment third-party-dlg" style="position: relative">
    <el-form
      ref="form"
      class="full-width-input form-box"
      :model="formData"
      :rules="rules"
      style="width: 100%"
      label-width="80px"
      :size="formItemSize"
      label-position="left"
      @submit.prevent
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="脚本类型" prop="eventType">
            <el-select
              v-model="formData.eventType"
              :clearable="true"
              placeholder="脚本类型"
              style="width: 200px"
              @change="onEventTypeChange"
            >
              <el-option
                v-for="item in dialogParams.eventList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
                :disabled="item.disabled"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="16">
          <el-form-item>
            <el-row class="no-scroll flex-box" style="width: 100%" justify="end">
              <el-button type="primary" :size="formItemSize" @click="onSubmit"> 保存 </el-button>
            </el-row>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="15" justify="space-between">
        <el-col :span="11" style="height: 640px">
          <el-row>
            <ScriptEditor
              :value="formScriptString"
              title="表单数据"
              :options="{ readOnly: true }"
              style="width: 100%; height: 640px; border: 1px solid #dcdcdc"
            />
          </el-row>
        </el-col>
        <el-col :span="13" style="height: 640px">
          <el-row>
            <ScriptEditor
              v-model:value="formData.scriptString"
              style="width: 100%; height: 640px; border: 1px solid #dcdcdc"
              :title="currentEvent ? currentEvent.functionName : '事件脚本'"
            />
          </el-row>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { render } from 'ejs';
import { findItemFromList } from '@/common/utils';
import ScriptEditor from '@/components/ScriptEditor/index.vue';
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';
import { OnlineFormEventType, SysOnlineFormType } from '@/common/staticDict';
import { SysOnlineRelationType } from '@/common/staticDict/online';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
import onlineFormDataTemplate from './onlineFormDataTemplate';
import { OnlineFormEventList, OnlineMobileFormEventList } from './event';
const layoutStore = useLayoutStore();

interface IProps extends ThirdProps {
  rowData?: ANY_OBJECT;
  eventList: Array<ANY_OBJECT>;
  formConfig: ANY_OBJECT;
  mode: string;
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT>;
}
const props = defineProps<IProps>();
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize;
});

const { thirdParams, onCloseThirdDialog } = useThirdParty(props);

const form = ref();
const formData = ref<ANY_OBJECT>({
  eventType: '',
  scriptString: '',
});
const formScriptString = ref('');
const rules = {
  eventType: {
    required: true,
    message: '请选择脚本类型',
    trigger: 'blur',
  },
};

const dialogParams = computed(() => {
  return {
    rowData: props.rowData || thirdParams.value.rowData,
    eventList: props.eventList || thirdParams.value.eventList,
    formConfig: props.formConfig || thirdParams.value.formConfig,
    mode: props.mode || thirdParams.value.mode,
  };
});
const currentEvent = computed(() => {
  return findItemFromList(
    dialogParams.value.mode === 'pc' ? OnlineFormEventList : OnlineMobileFormEventList,
    formData.value.eventType,
    'id',
  );
});

const onSubmit = () => {
  let params = {
    ...formData.value,
    showName: OnlineFormEventType.getValue(formData.value.eventType),
  };

  form.value.validate((valid: boolean) => {
    if (!valid) return;
    if (props.dialog) {
      props.dialog.submit(params);
    } else {
      onCloseThirdDialog(true, dialogParams.value.rowData, params);
    }
  });
};

const onEventTypeChange = () => {
  formData.value.scriptString = currentEvent.value ? currentEvent.value.comment.trim() : '';
};

onMounted(() => {
  let param = {
    tableList: [] as ANY_OBJECT[],
    customFieldList: dialogParams.value.formConfig.form.customFieldList,
  };
  if (dialogParams.value.formConfig.getMasterTable) {
    if (dialogParams.value.formConfig.getMasterTable.relationType == null) {
      if (dialogParams.value.formConfig.form.formType === SysOnlineFormType.QUERY) {
        // 主表查询页面
        param.tableList = dialogParams.value.formConfig.getAllTableList
          .map((table: ANY_OBJECT) => {
            return table.relationType !== SysOnlineRelationType.ONE_TO_MANY
              ? {
                  showName: `${table.tag.datasourceName || table.tag.relationName} (${
                    SysOnlineRelationType.getValue(table.relationType) || '主表'
                  })`,
                  variableName: table.tag.variableName,
                  columnList: table.columnList.filter(
                    (column: ANY_OBJECT) => column.filterType !== 0,
                  ),
                  isObject: true,
                }
              : undefined;
          })
          .filter((item: ANY_OBJECT) => item != null);
      } else {
        // 主表编辑页面
        param.tableList = dialogParams.value.formConfig.getAllTableList
          .map((table: ANY_OBJECT) => {
            return {
              showName: `${table.tag.datasourceName || table.tag.relationName} (${
                SysOnlineRelationType.getValue(table.relationType) || '主表'
              })`,
              variableName: table.tag.variableName,
              columnList: table.columnList,
              isObject: table.relationType !== SysOnlineRelationType.ONE_TO_MANY,
            };
          })
          .filter((item: ANY_OBJECT) => item != null);
      }
    } else {
      let table: ANY_OBJECT = props.formConfig.getMasterTable;
      param.tableList = [
        {
          showName: `${table.tag.datasourceName || table.tag.relationName} (${
            SysOnlineRelationType.getValue(table.relationType) || '主表'
          })`,
          variableName: table.tag.variableName,
          columnList:
            props.formConfig.form.formType === SysOnlineFormType.QUERY
              ? table.columnList.filter((column: ANY_OBJECT) => column.filterType !== 0)
              : table.columnList,
          isObject: true,
        },
      ];
    }
  }

  formScriptString.value = render(onlineFormDataTemplate.queryFrom.trim(), {
    form: param,
  });
  if (dialogParams.value.rowData) {
    formData.value = {
      ...dialogParams.value.rowData,
    };
  }
});
</script>
