<template>
  <div class="form-single-fragment third-party-dlg" style="position: relative">
    <el-form
      ref="form"
      :model="formData"
      class="full-width-input form-box"
      :rules="rules"
      style="width: 100%"
      label-width="100px"
      :size="itemSize"
      label-position="right"
      @submit.prevent
    >
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="所属分组">
            <el-cascader
              class="input-item"
              v-model="formEditPageGroup.parentId.value"
              :disabled="isEdit"
              :options="formEditPageGroup.parentId.impl.dropdownList"
              filterable
              :clearable="true"
              :show-all-levels="false"
              placeholder="所属分组"
              :props="{
                value: 'groupId',
                label: 'groupName',
                checkStrictly: true,
              }"
              @visible-change="formEditPageGroup.parentId.impl.onVisibleChange"
              @change="onParentIdValueChange"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="分组名称" prop="ReportPrintManageGroup.groupName">
            <el-input
              class="input-item"
              v-model="formData.ReportPrintManageGroup.groupName"
              :clearable="true"
              placeholder="分组名称"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <el-row class="menu-box">
      <el-col :span="24">
        <el-row class="no-scroll flex-box" type="flex" justify="end">
          <el-button :size="itemSize" :plain="true" @click="onCancel"> 取消 </el-button>
          <el-button type="primary" :size="itemSize" @click="onSubmit()"> 保存 </el-button>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { CascaderValue, ElMessage } from 'element-plus';
import { findTreeNodePath } from '@/common/utils';
import { ReportPrintGroupController } from '@/api/report';
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';
import { useDropdown } from '@/common/hooks/useDropdown';
import { DropdownOptions, ListData } from '@/common/types/list';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

interface IProps extends ThirdProps {
  groupId?: string;
  groupList?: ANY_OBJECT[];
  defaultFormItemSize: Ref<'' | 'default' | 'small' | 'large'>;
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<boolean>;
}
const props = defineProps<IProps>();
const itemSize = computed(() => {
  return layoutStore.defaultFormItemSize || thirdParams.value.defaultFormItemSize?.value;
});

const { thirdParams, onCloseThirdDialog } = useThirdParty(props);

const form = ref();
const formData = ref<ANY_OBJECT>({
  ReportPrintManageGroup: {
    groupId: undefined,
    groupName: undefined,
    parentId: undefined,
    isDatasourceInit: false,
  },
});
const rules = {
  'ReportPrintManageGroup.groupName': [
    { required: true, message: '请输入分组名称', trigger: 'blur' },
  ],
};
const isEdit = computed(() => {
  return dialogParams.value.groupId != null;
});
const dialogParams = computed(() => {
  return {
    groupId: props.groupId || thirdParams.value.groupId,
    groupList: props.groupList || thirdParams.value.groupList || [],
  };
});
/**
 * 所属分组下拉数据获取函数
 */
const loadParentIdDropdownList = (): Promise<ListData<ANY_OBJECT>> => {
  return Promise.resolve({ dataList: dialogParams.value.groupList });
};
const dropdownOptions: DropdownOptions<ANY_OBJECT> = {
  loadData: loadParentIdDropdownList,
  isTree: true,
};
const formEditPageGroup = reactive<ANY_OBJECT>({
  formFilter: {},
  formFilterCopy: {},
  menuBlock: {
    isInit: false,
  },
  parentId: {
    impl: useDropdown(dropdownOptions),
    value: [],
  },
  isInit: false,
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
    let params = {
      reportPrintGroupDto: {
        groupId: dialogParams.value.groupId,
        groupName: formData.value.ReportPrintManageGroup.groupName,
        parentId: formData.value.ReportPrintManageGroup.parentId,
        appId: formData.value.ReportPrintManageGroup.appId,
      },
    };
    let httpCall = isEdit.value
      ? ReportPrintGroupController.update(params)
      : ReportPrintGroupController.add(params);
    httpCall
      .then(() => {
        ElMessage.success('保存成功');
        if (props.dialog) {
          props.dialog.submit(true);
        } else {
          onCloseThirdDialog(true);
        }
      })
      .catch(e => {
        console.warn(e);
      });
  });
};
/**
 * 所属分组选中值改变
 */
const onParentIdValueChange = (value: CascaderValue) => {
  formData.value.ReportPrintManageGroup.parentId = Array.isArray(value)
    ? value[value.length - 1]
    : undefined;
};
/**
 * 更新编辑数据集分组
 */
const refreshFormEditPageGroup = () => {
  loadReportPrintManageGroupData()
    .then(() => {
      if (!formEditPageGroup.isInit) {
        // 初始化下拉数据
        formEditPageGroup.parentId.impl
          .onVisibleChange(true)
          .then((res: ANY_OBJECT) => {
            formEditPageGroup.parentId.value = findTreeNodePath(
              res,
              formData.value.ReportPrintManageGroup.parentId,
              'groupId',
            );
          })
          .catch((e: Error) => {
            console.warn(e);
          });
      }
      formEditPageGroup.isInit = true;
    })
    .catch(e => {
      console.warn(e);
    });
};
/**
 * 获取数据集分组详细信息
 */
const loadReportPrintManageGroupData = () => {
  return new Promise((resolve, reject) => {
    if (!formData.value.ReportPrintManageGroup.isDatasourceInit && isEdit.value) {
      if (dialogParams.value.groupId == null) {
        resetFormData();
        reject();
        return;
      }
      let params = {
        groupId: dialogParams.value.groupId,
      };
      ReportPrintGroupController.view(params)
        .then(res => {
          formData.value.ReportPrintManageGroup = {
            ...res.data,
            isDatasourceInit: true,
          };
          resolve(true);
        })
        .catch(e => {
          reject(e);
        });
    } else {
      formData.value.CourseSection && (formData.value.CourseSection.isDatasourceInit = true);
      resolve(true);
    }
  });
};
/**
 * 重置表单数据
 */
const resetFormData = () => {
  formData.value = {
    ReportPrintManageGroup: {
      groupId: undefined,
      groupName: undefined,
      parentId: undefined,
      isDatasourceInit: false,
    },
  };
};
const formInit = () => {
  refreshFormEditPageGroup();
};

onMounted(() => {
  // 初始化页面数据
  formInit();
});
</script>
