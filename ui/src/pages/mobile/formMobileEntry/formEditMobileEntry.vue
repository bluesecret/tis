<template>
  <div class="form-single-fragment" style="position: relative">
    <el-form
      ref="form"
      :model="formData"
      class="full-width-input"
      :rules="rules"
      style="width: 100%"
      label-width="90px"
      :size="formItemSize"
      label-position="right"
      @submit.prevent
    >
      <el-row :gutter="16">
        <el-col :span="12" v-if="entryType === MobileEntryType.SUDOKU">
          <el-form-item label="所属分组" prop="parentId">
            <el-select v-model="formData.parentId">
              <el-option
                v-for="group in groupList"
                :key="group.id"
                :label="group.name"
                :value="group.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="显示名称" prop="entryName">
            <el-input
              class="input-item"
              v-model="formData.entryName"
              :clearable="true"
              placeholder="显示名称"
            />
          </el-form-item>
        </el-col>
        <template v-if="entryType !== MobileEntryType.GROUP">
          <el-col :span="12">
            <el-form-item label="绑定类型">
              <el-select
                v-model="formData.bindType"
                plaaceholder="绑定类型"
                :disabled="isEdit"
                @change="onBindTypeChange"
              >
                <el-option
                  v-for="item in SysMenuBindType.getList().filter(
                    item => item !== SysMenuBindType.THRID_URL,
                  )"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="formData.bindType === SysMenuBindType.ROUTER">
            <el-form-item label="菜单路由">
              <el-input v-model="formData.formRouterName" placeholder="菜单路由" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="formData.bindType === SysMenuBindType.ONLINE_FORM">
            <el-form-item label="在线表单" prop="onlineFormId">
              <el-cascader
                v-model="onlineFormPath"
                :options="formTreeData"
                filterable
                :clearable="true"
                placeholder="选择绑定的在线表单"
                :props="{ value: 'id', label: 'name' }"
                @change="onOnlineFormChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="formData.bindType === SysMenuBindType.WORK_ORDER">
            <el-form-item label="流程工单" prop="onlineFlowEntryId">
              <el-cascader
                v-model="onlineFlowPath"
                :options="entryTreeData as CascaderOption[]"
                filterable
                :clearable="true"
                placeholder="选择绑定的工单"
                :props="{ value: 'id', label: 'name' }"
                @change="onOnlineEntryChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="formData.bindType === SysMenuBindType.REPORT">
            <el-form-item label="报表页面" prop="reportPageId">
              <el-cascader
                v-model="reportPagePath"
                :options="reportPageTreeData"
                filterable
                :clearable="true"
                placeholder="选择绑定的报表页面"
                :props="reportPageProps"
                @change="onReportPageChange"
              >
                <template v-slot="{ data }">
                  <el-row type="flex" justify="space-between" align="middle">
                    <span>{{ data.name }}</span>
                    <el-tag size="default" :type="data.isGroup ? 'primary' : 'success'">
                      {{ data.isGroup ? '分组' : '页面' }}
                    </el-tag>
                  </el-row>
                </template>
              </el-cascader>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="可见性" prop="commonEntry">
              <el-radio-group
                v-model="formData.commonEntry"
                @change="formData.roleIdList = undefined"
              >
                <el-radio-button :value="true">所有人可见</el-radio-button>
                <el-radio-button :value="false">指定角色可见</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="!formData.commonEntry">
            <el-form-item label="用户角色" prop="roleIdList">
              <el-select v-model="formData.roleIdList" multiple placeholder="绑定用户角色">
                <el-option
                  v-for="role in roleList"
                  :key="role.roleId"
                  :label="role.roleName"
                  :value="role.roleId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </template>
        <el-col :span="12">
          <el-form-item label="显示顺序" prop="showOrder">
            <el-input-number
              class="input-item"
              v-model="formData.showOrder"
              controls-position="right"
              style="width: 100%"
              :clearable="true"
              placeholder="显示顺序"
            />
          </el-form-item>
        </el-col>
        <template v-if="entryType !== MobileEntryType.GROUP">
          <el-col :span="12">
            <el-form-item label="显示图片" prop="imageData">
              <el-upload
                class="upload-image-item"
                :show-file-list="false"
                :headers="getUploadHeaders"
                name="uploadFile"
                accept="image/*"
                :action="getActionUrl"
                :on-success="onImageDataUploadSuccess"
                :on-remove="onImageDataRemoveFile"
              >
                <div v-if="formData.imageData != null" style="position: relative">
                  <img class="upload-image-show" :src="getImageUrl" />
                  <div class="upload-img-del el-icon-close" @click.stop="onImageDataRemoveFile" />
                </div>
                <el-icon v-else><Plus /></el-icon>
              </el-upload>
            </el-form-item>
          </el-col>
        </template>
        <el-col :span="24">
          <el-form-item label="权限字列表">
            <el-select
              v-model="formData.permCodeList"
              multiple
              filterable
              allow-create
              default-first-option
              placeholder="请从列表中选择权限字或输入后按回车添加新的权限字"
            >
              <el-option v-for="item in permCodeList" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-row class="no-scroll flex-box" type="flex" justify="end">
            <el-button type="primary" :size="formItemSize" :plain="true" @click="onCancel">
              取消
            </el-button>
            <el-button type="primary" :size="formItemSize" @click="onSubmit"> 保存 </el-button>
          </el-row>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { Plus, Search } from '@element-plus/icons-vue';
import { CascaderOption, CascaderValue, ElMessage } from 'element-plus';
import { OnlinePageController } from '@/api/online';
import { FlowOperationController } from '@/api/flow';
import { ReportPageGroupController } from '@/api/report';
import { treeDataTranslate, findTreeNodePath, findItemFromList } from '@/common/utils';
import { useUpload } from '@/common/hooks/useUpload';
import { SystemRoleController, MobileEntryController, PermCodeController } from '@/api/system';
import { ANY_OBJECT } from '@/types/generic';
import { API_CONTEXT } from '@/api/config';
import { DialogProp } from '@/components/Dialog/types';
import {
  MobileEntryType,
  SysMenuBindType,
  SysOnlineFormType,
  SysPermCodeType,
} from '@/common/staticDict';
import { SysOnlineFormKind } from '@/common/staticDict/online';
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

const { getUploadFileUrl, getUploadHeaders, getUploadActionUrl } = useUpload();

const props = withDefaults(
  defineProps<{
    entryType: number;
    entryId?: string;
    parentId?: string;
    rowData?: ANY_OBJECT;
    groupList?: ANY_OBJECT[];
    // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
    dialog?: DialogProp<boolean>;
  }>(),
  {
    entryId: undefined,
    entryType: 0,
    groupList: () => [],
    dialog: undefined,
  },
);
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize;
});

const form = ref();
const formData = ref<ANY_OBJECT>({
  entryId: undefined,
  parentId: undefined,
  entryName: undefined,
  entryType: undefined,
  formRouterName: undefined,
  onlineFormId: undefined,
  reportPageId: undefined,
  onlineFlowEntryId: undefined,
  bindType: SysMenuBindType.ROUTER,
  extraData: undefined,
  imageData: undefined,
  showOrder: undefined,
  roleIdList: undefined,
  commonEntry: true,
  permCodeList: [],
});
const onlineFormPath = ref<CascaderValue>([]);
const formTreeData = ref<ANY_OBJECT[]>([]);
const onlineFlowPath = ref<CascaderValue>([]);
const entryTreeData = ref<(ANY_OBJECT | null)[]>([]);
const reportPageTreeData = ref<ANY_OBJECT[]>([]);
const reportPagePath = ref<CascaderValue>([]);
const roleList = ref<ANY_OBJECT[]>([]);
const reportPageProps = {
  label: 'name',
  value: 'id',
};
const rules = {
  entryName: [{ required: true, message: '请输入显示名称', trigger: 'blur' }],
  imageData: [{ required: true, message: '请选择显示图片', trigger: 'blur' }],
  showOrder: [{ required: true, message: '请输入显示顺序', trigger: 'blur' }],
  bindType: [{ required: true, message: '请选择绑定类型', trigger: 'blur' }],
  onlineFormId: [{ required: true, message: '请选择绑定的在线表单', trigger: 'blur' }],
  onlineFlowEntryId: [{ required: true, message: '请选择菜单绑定的工单', trigger: 'blur' }],
  reportPageId: [{ required: true, message: '请选择绑定的报表页面', trigger: 'blur' }],
  formRouterName: [{ required: true, message: '请输入路由名称', trigger: 'blur' }],
  roleIdList: [{ required: true, message: '请选择绑定用户角色', trigger: 'blur' }],
};

const isEdit = computed(() => {
  return props.rowData != null;
});
const getImageUrl = computed<string | undefined>(() => {
  return (
    getUploadFileUrl(formData.value.imageData, {
      filename: formData.value.imageData.filename,
    }) || undefined
  );
});

const getActionUrl = computed(() => {
  return getUploadActionUrl(API_CONTEXT + '/mobile/mobileEntry/uploadImage');
});
const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  }
};
/**
 * 编辑
 */
const onSubmit = () => {
  form.value.validate((valid: boolean) => {
    if (!valid) return;
    let params: ANY_OBJECT = {
      mobileEntryDto: {
        entryId: props.entryId,
        parentId: formData.value.parentId,
        entryName: formData.value.entryName,
        imageData: formData.value.imageData,
        showOrder: formData.value.showOrder,
        commonEntry: formData.value.commonEntry,
        entryType: formData.value.entryType,
      },
      roleIdListString: Array.isArray(formData.value.roleIdList)
        ? formData.value.roleIdList.join(',')
        : undefined,
    };

    params.mobileEntryDto.extraData = JSON.stringify({
      bindType: formData.value.bindType,
      onlineFormId: formData.value.onlineFormId,
      onlineFlowEntryId: formData.value.onlineFlowEntryId,
      reportPageId: formData.value.reportPageId,
      formRouterName: formData.value.formRouterName,
      targetUrl: formData.value.targetUrl,
      permCodeList: formData.value.permCodeList,
    });

    let httpCall = isEdit.value
      ? MobileEntryController.update(params)
      : MobileEntryController.add(params);

    httpCall
      .then(() => {
        ElMessage.success('保存成功');
        if (props.dialog) {
          props.dialog.submit(true);
        }
      })
      .catch(e => {
        console.warn(e);
      });
  });
};

/**
 * 获取移动端首页显示管理详细信息
 */
const loadMobileEntryData = () => {
  return new Promise((resolve, reject) => {
    if (!isEdit.value) {
      resolve(true);
      return;
    }
    let params = {
      entryId: props.entryId,
    };
    MobileEntryController.view(params)
      .then(res => {
        let extraData = null;
        if (res.data.extraData != null && res.data.extraData !== '') {
          extraData = JSON.parse(res.data.extraData);
        }
        try {
          res.data.imageData = JSON.parse(res.data.imageData);
        } catch (e) {
          res.data.imageData = undefined;
        }

        formData.value = {
          ...formData.value,
          ...res.data,
          ...extraData,
        };
        if (Array.isArray(formData.value.mobileEntryRoleList)) {
          formData.value.roleIdList = formData.value.mobileEntryRoleList.map(item => item.roleId);
        }
        resolve(true);
      })
      .catch(e => {
        reject(e);
      });
  });
};
const onBindTypeChange = () => {
  formData.value.onlineFormId = undefined;
  formData.value.formRouterName = undefined;
  formData.value.reportPageId = undefined;
  formData.value.onlineFlowEntryId = undefined;
};
const onOnlineFormChange = (values: CascaderValue) => {
  if (Array.isArray(values)) {
    formData.value.onlineFormId = values[1];
  }
};
const onOnlineEntryChange = (values: CascaderValue) => {
  if (Array.isArray(values)) {
    formData.value.onlineFlowEntryId = values[0];
    formData.value.onlineFormId = values[1];
  }
};
const loadPageAndForms = () => {
  OnlinePageController.listAllPageAndForm({})
    .then(res => {
      let formList = res.data.formList;
      let pageList = res.data.pageList;
      if (!Array.isArray(formList) || !Array.isArray(pageList)) {
        formTreeData.value = [];
        return;
      }
      formTreeData.value = pageList.map(page => {
        let children = formList
          .filter((form: ANY_OBJECT) => {
            return (
              form.pageId === page.pageId &&
              form.formKind === SysOnlineFormKind.PAGE &&
              (form.formType === SysOnlineFormType.QUERY ||
                form.formType === SysOnlineFormType.ADVANCE_QUERY)
            );
          })
          .map((form: ANY_OBJECT) => {
            return {
              id: form.formId,
              name: form.formName,
            };
          });

        return {
          id: page.pageId,
          name: page.pageName,
          disabled: !page.published,
          children,
        };
      });
      if (formData.value.onlineFormId) {
        onlineFormPath.value = findTreeNodePath(
          formTreeData.value,
          formData.value.onlineFormId,
          'id',
        );
      }
    })
    .catch(e => {
      console.warn(e);
    });
};
const loadFlowEntryForms = () => {
  FlowOperationController.listFlowEntryForm({})
    .then(res => {
      if (Array.isArray(res.data)) {
        entryTreeData.value = res.data
          .map(entry => {
            let formList: ANY_OBJECT[] = [];
            if (Array.isArray(entry.formList)) {
              formList = entry.formList
                .filter((form: ANY_OBJECT) => form.formType === SysOnlineFormType.WORK_ORDER)
                .map((form: ANY_OBJECT) => {
                  return {
                    id: form.formId,
                    name: form.formName,
                  };
                });
            }
            if (formList.length > 0) {
              return {
                id: entry.entryId,
                name: entry.processDefinitionName,
                children: formList,
              };
            }
            return null;
          })
          .filter(entry => entry != null);
      }
      if (formData.value.onlineFlowEntryId) {
        onlineFlowPath.value = [formData.value.onlineFlowEntryId, formData.value.onlineFormId];
      }
    })
    .catch(e => {
      console.warn(e);
    });
};
const onReportPageChange = (values: CascaderValue) => {
  formData.value.reportPageId = Array.isArray(values) ? values[values.length - 1] : undefined;
};
const loadReportPageList = () => {
  ReportPageGroupController.listAll({})
    .then(res => {
      let parentIdSet = new Set();
      (res.data.reportPageGroupList || []).forEach((item: ANY_OBJECT) => {
        if (item.parentId != null) parentIdSet.add(item.parentId);
      });
      (res.data.reportPageList || []).forEach((item: ANY_OBJECT) => {
        if (item.groupId != null) parentIdSet.add(item.groupId);
      });
      let tempList = (res.data.reportPageGroupList || [])
        .map((item: ANY_OBJECT) => {
          return {
            id: item.groupId,
            parentId: item.parentId,
            name: item.groupName,
            isGroup: true,
            disabled: !parentIdSet.has(item.groupId),
          };
        })
        .concat(
          (res.data.reportPageList || []).map((item: ANY_OBJECT) => {
            return {
              id: item.pageId,
              parentId: item.groupId,
              name: item.pageName,
              isGroup: false,
            };
          }),
        );
      reportPageTreeData.value = treeDataTranslate(tempList);
      reportPagePath.value = findTreeNodePath(
        reportPageTreeData.value,
        formData.value.reportPageId,
        'id',
      );
    })
    .catch(e => {
      console.warn(e);
    });
};
const loadRole = () => {
  SystemRoleController.getRoleList({})
    .then(res => {
      roleList.value = res.data.dataList;
    })
    .catch(e => {
      console.warn(e);
    });
};
const initFormData = () => {
  formData.value.entryType = props.entryType;
  formData.value.parentId = props.parentId;
  loadMobileEntryData()
    .then(() => {
      loadPageAndForms();
      loadFlowEntryForms();
      loadReportPageList();
      loadRole();
      loadPermCodeList();
    })
    .catch(e => {
      console.warn(e);
    });
};
/**
 * 显示图片上传成功
 */
const onImageDataUploadSuccess = (response: ANY_OBJECT) => {
  if (response.success) {
    formData.value.imageData = response.data;
    // this.setHeadImage(response.data);
  } else {
    ElMessage.error(response.message);
  }
};
/**
 * 移除显示图片
 */
const onImageDataRemoveFile = () => {
  formData.value.imageData = undefined;
};
const permCodeList = ref<string[]>([]);
const loadPermCodeList = () => {
  PermCodeController.getPermCodeList({})
    .then(res => {
      permCodeList.value = res.data;
    })
    .catch(e => {
      console.log(e);
    });
};

/**
 * 重置表单数据
 */
const resetFormData = () => {
  formData.value = {
    entryId: undefined,
    parentId: props.parentId,
    entryName: undefined,
    entryType: undefined,
    formRouterName: undefined,
    onlineFormId: undefined,
    reportPageId: undefined,
    onlineFlowEntryId: undefined,
    bindType: SysMenuBindType.ROUTER,
    extraData: undefined,
    imageData: undefined,
    showOrder: undefined,
    roleIdList: undefined,
    commonEntry: true,
    permCodeList: [],
  };
};
defineExpose({ resetFormData });
const formInit = () => {
  initFormData();
};

onMounted(() => {
  // 初始化页面数据
  formInit();
});
</script>
