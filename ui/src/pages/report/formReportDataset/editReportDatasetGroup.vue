<template>
  <div class="form-single-fragment third-party-dlg" style="position: relative">
    <el-form
      ref="form"
      :model="formData"
      class="full-width-input form-box"
      :rules="rules"
      style="width: 100%"
      label-width="80px"
      :size="formItemSize"
      label-position="right"
      @submit.prevent
    >
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="所属分组">
            <el-cascader
              class="input-item"
              v-model="formEditDatasetGroup.parentId.value"
              :disabled="isEdit"
              :options="formEditDatasetGroup.parentId.impl.dropdownList"
              filterable
              :clearable="true"
              :show-all-levels="false"
              placeholder="所属分组"
              :props="{ value: 'groupId', label: 'groupName', checkStrictly: true }"
              @visible-change="formEditDatasetGroup.parentId.impl.onVisibleChange"
              @change="onParentIdValueChange"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="分组名称" prop="ReportDatasetGroup.groupName">
            <el-input
              class="input-item"
              v-model="formData.ReportDatasetGroup.groupName"
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
          <el-button :size="formItemSize" :plain="true" @click="onCancel"> 取消 </el-button>
          <el-button type="primary" :size="formItemSize" @click="onSubmit"> 保存 </el-button>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { CascaderValue, ElMessage } from 'element-plus';
import { DialogProp } from '@/components/Dialog/types';
import { ANY_OBJECT } from '@/types/generic';
import { useDropdown } from '@/common/hooks/useDropdown';
import { DropdownOptions, ListData } from '@/common/types/list';
import { findTreeNodePath } from '@/common/utils';
import { ReportDatasetGroupController } from '@/api/report';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

interface IProps extends ThirdProps {
  groupId?: string;
  groupList?: ANY_OBJECT[];
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<boolean>;
}
const props = defineProps<IProps>();
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize || thirdParams.value.defaultFormItemSize?.value;
});

const form = ref();
const rules = {
  'ReportDatasetGroup.groupName': [{ required: true, message: '请输入分组名称', trigger: 'blur' }],
};
const formData = ref<ANY_OBJECT>({
  ReportDatasetGroup: {
    groupId: undefined,
    groupName: undefined,
    parentId: undefined,
    isDatasourceInit: false,
  },
});

const dialogParams = computed(() => {
  return {
    groupId: props.groupId || thirdParams.value.groupId,
    groupList: props.groupList || thirdParams.value.groupList || [],
  };
});
const isEdit = computed(() => {
  return !!dialogParams.value.groupId;
});

/**
 * 所属分组下拉数据获取函数
 */
const loadParentIdDropdownList = (): Promise<ListData<ANY_OBJECT>> => {
  return Promise.resolve({ dataList: dialogParams.value.groupList || [] });
};
const dropdownOptions: DropdownOptions<ANY_OBJECT> = {
  loadData: loadParentIdDropdownList,
  isTree: true,
  idKey: 'id',
  parentIdKey: 'parentId',
};
const formEditDatasetGroup = reactive<ANY_OBJECT>({
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
 * 所属分组选中值改变
 */
const onParentIdValueChange = (value: CascaderValue) => {
  formData.value.ReportDatasetGroup.parentId = Array.isArray(value)
    ? value[value.length - 1]
    : undefined;
};
/**
 * 更新编辑数据集分组
 */
const refreshFormEditDatasetGroup = () => {
  loadReportDatasetGroupData()
    .then(() => {
      if (!formEditDatasetGroup.isInit) {
        // 初始化下拉数据
        formEditDatasetGroup.parentId.impl
          .onVisibleChange(true)
          .then((res: ANY_OBJECT[]) => {
            formEditDatasetGroup.parentId.value = findTreeNodePath(
              res,
              formData.value.ReportDatasetGroup.parentId,
              'groupId',
            );
          })
          .catch((e: Error) => {
            console.warn(e);
          });
      }
      formEditDatasetGroup.isInit = true;
    })
    .catch(e => {
      console.warn(e);
    });
};
/**
 * 保存
 */
const onSubmit = () => {
  form.value.validate((valid: boolean) => {
    if (!valid) return;
    let params = {
      reportDatasetGroupDto: {
        groupId: dialogParams.value.groupId,
        groupName: formData.value.ReportDatasetGroup.groupName,
        parentId: formData.value.ReportDatasetGroup.parentId,
        appId: formData.value.ReportDatasetGroup.appId,
      },
    };
    let httpCall = isEdit.value
      ? ReportDatasetGroupController.update(params)
      : ReportDatasetGroupController.add(params);
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
 * 获取数据集分组详细信息
 */
const loadReportDatasetGroupData = () => {
  return new Promise((resolve, reject) => {
    if (!formData.value.ReportDatasetGroup.isDatasourceInit && isEdit.value) {
      if (dialogParams.value.groupId == null) {
        resetFormData();
        reject();
        return;
      }
      let params = {
        groupId: dialogParams.value.groupId,
      };
      ReportDatasetGroupController.view(params)
        .then(res => {
          formData.value.ReportDatasetGroup = { ...res.data, isDatasourceInit: true };
          resolve(res);
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
    ReportDatasetGroup: {
      groupId: undefined,
      groupName: undefined,
      parentId: undefined,
      appId: undefined,
      createTime: undefined,
      createUserId: undefined,
      updateTime: undefined,
      updateUserId: undefined,
      isDatasourceInit: false,
    },
  };
};
const formInit = () => {
  refreshFormEditDatasetGroup();
};

onMounted(() => {
  // 初始化页面数据
  formInit();
});
</script>
