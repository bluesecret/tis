<template>
  <div class="form-single-fragment third-party-dlg" style="position: relative">
    <el-form
      ref="form"
      :model="formData"
      class="full-width-input form-box"
      :rules="rules"
      style="width: 100%"
      label-width="100px"
      :size="formItemSize"
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
              :props="{ value: 'groupId', label: 'groupName', checkStrictly: true }"
              @visible-change="formEditPageGroup.parentId.impl.onVisibleChange"
              @change="onParentIdValueChange"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="分组名称" prop="ReportPageGroup.groupName">
            <el-input
              class="input-item"
              v-model="formData.ReportPageGroup.groupName"
              :clearable="true"
              placeholder="分组名称"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <el-row class="menu-box" type="flex" justify="end">
      <el-button :size="formItemSize" :plain="true" @click="onCancel"> 取消 </el-button>
      <el-button type="primary" :size="formItemSize" @click="onUpdateClick()"> 保存 </el-button>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { CascaderValue, ElMessage } from 'element-plus';
import { DialogProp } from '@/components/Dialog/types';
import { ANY_OBJECT } from '@/types/generic';
import { findTreeNodePath } from '@/common/utils';
import { ReportPageGroupController } from '@/api/report';
import { useDropdown } from '@/common/hooks/useDropdown';
import { DropdownOptions, ListData } from '@/common/types/list';
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
const formData = ref<ANY_OBJECT>({
  ReportPageGroup: {
    groupId: undefined,
    groupName: undefined,
    parentId: undefined,
    isDatasourceInit: false,
  },
});
const rules = {
  'ReportPageGroup.groupName': [{ required: true, message: '请输入分组名称', trigger: 'blur' }],
};

const dialogParams = computed(() => {
  return {
    groupId: props.groupId || thirdParams.value.groupId,
    groupList: props.groupList || thirdParams.value.groupList || [],
  };
});
const isEdit = computed(() => {
  return dialogParams.value.groupId != null;
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
  idKey: 'id',
  parentIdKey: 'parentId',
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
const onUpdateClick = () => {
  form.value.validate((valid: boolean) => {
    if (!valid) return;
    let params = {
      reportPageGroupDto: {
        groupId: dialogParams.value.groupId,
        groupName: formData.value.ReportPageGroup.groupName,
        parentId: formData.value.ReportPageGroup.parentId,
        appId: formData.value.ReportPageGroup.appId,
      },
    };
    let httpCall = isEdit.value
      ? ReportPageGroupController.update(params)
      : ReportPageGroupController.add(params);
    httpCall
      .then(() => {
        ElMessage.success('保存成功');
        if (props.dialog) {
          props.dialog.submit(true);
        } else {
          onCloseThirdDialog(true, true);
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
  formData.value.ReportPageGroup.parentId = Array.isArray(value)
    ? value[value.length - 1]
    : undefined;
};
/**
 * 更新编辑数据集分组
 */
const refreshFormEditPageGroup = () => {
  loadReportPageGroupData()
    .then(() => {
      if (!formEditPageGroup.isInit) {
        // 初始化下拉数据
        formEditPageGroup.parentId.impl
          .onVisibleChange(true)
          .then((res: ANY_OBJECT) => {
            formEditPageGroup.parentId.value = findTreeNodePath(
              res,
              formData.value.ReportPageGroup.parentId,
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
const loadReportPageGroupData = () => {
  return new Promise((resolve, reject) => {
    if (!formData.value.ReportPageGroup.isDatasourceInit && isEdit.value) {
      if (dialogParams.value.groupId == null) {
        resetFormData();
        reject();
        return;
      }
      let params = {
        groupId: dialogParams.value.groupId,
      };
      ReportPageGroupController.view(params)
        .then(res => {
          formData.value.ReportPageGroup = { ...res.data, isDatasourceInit: true };
          resolve(true);
        })
        .catch(e => {
          reject(e);
        });
    } else {
      // TODO CourseSection.isDatasourceInit 属性未知作用
      //formData.value.CourseSection.isDatasourceInit = true;
      formData.value.ReportPageGroup.isDatasourceInit = true;
      resolve(true);
    }
  });
};
/**
 * 重置表单数据
 */
const resetFormData = () => {
  formData.value = {
    ReportPageGroup: {
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
