<template>
  <el-form label-position="top" size="default" @submit.prevent>
    <el-form-item label="表单 ID">
      <el-row type="flex" justify="space-between" align="middle">
        <el-input readonly :value="formConfig().form.pageId" />
        <el-button
          class="formShareBtn"
          link
          icon="el-icon-share"
          style="margin-left: 8px"
          title="复制接入信息"
          :data-clipboard-text="shareInfo"
          @click="onShareForm"
        />
      </el-row>
    </el-form-item>
    <el-form-item label="标签位置" v-if="formConfig().form.labelPosition != null">
      <el-radio-group v-model="formConfig().form.labelPosition" size="default">
        <el-radio-button value="left">居左</el-radio-button>
        <el-radio-button value="right">居右</el-radio-button>
        <el-radio-button value="top">顶部</el-radio-button>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="标签宽度" v-if="formConfig().form.labelWidth != null">
      <el-input-number
        v-model="formConfig().form.labelWidth"
        controls-position="right"
        style="width: 100%"
      />
    </el-form-item>
    <el-form-item label="栅格间距" v-if="formConfig().form.gutter != null">
      <el-input-number
        v-model="formConfig().form.gutter"
        controls-position="right"
        style="width: 100%"
      />
    </el-form-item>
    <MultiItemList
      label="表单参数"
      v-model:data="formConfig().form.paramList"
      @add="onEditFormParam()"
      @edit="onEditFormParam"
      @delete="onRemoveFormParam"
      :prop="{
        label: 'variableName',
        value: 'variableName',
      }"
    />
  </el-form>
</template>

<script setup lang="ts">
import { ElMessageBox, ElMessage } from 'element-plus';
import Clipboard from 'clipboard';
import { Dialog } from '@/components/Dialog';
import { ANY_OBJECT } from '@/types/generic';
import MultiItemList from '@/components/MultiItemList/index.vue';
import { findItemFromList } from '@/common/utils';
import EditFormParam from './editFormParam.vue';

const formConfig = inject('formConfig', () => {
  console.error('CustomFormSetting: formConfig not injected');
  return {} as ANY_OBJECT;
});

const shareInfo = computed(() => {
  let info = {
    url: window.location.origin + '/#/thirdParty/thirdReport?pageId=' + formConfig().form.pageId,
  };

  return JSON.stringify(info);
});

const handlerEditParam = (row: ANY_OBJECT, paramName: string) => {
  if (Array.isArray(formConfig().form.paramList)) {
    if (row == null) {
      let temp = findItemFromList(formConfig().form.paramList, paramName, 'variableName');
      if (temp != null) {
        ElMessage.error('此参数已存在！');
      } else {
        formConfig().form.paramList.push({
          variableName: paramName,
        });
      }
    } else {
      if (row.variableName !== paramName) {
        // 修改了参数名
        let temp = findItemFromList(formConfig().form.paramList, paramName, 'variableName');
        if (temp != null) {
          ElMessage.error('此参数已存在！');
        } else {
          formConfig().form.paramList = formConfig().form.paramList.map(item => {
            if (item.variableName === row.variableName) {
              return {
                variableName: paramName,
              };
            } else {
              return {
                ...item,
              };
            }
          });
        }
      }
    }
  } else {
    formConfig().form.paramList = [
      {
        variableName: paramName,
      },
    ];
  }
};
const onShareForm = () => {
  let clipboard = new Clipboard('.formShareBtn');
  clipboard.on('success', e => {
    ElMessage.success('接入信息复制成功！');
    clipboard.destroy();
  });
  clipboard.on('error', e => {
    ElMessage.error('浏览器不支持复制，请手动复制接入信息：' + shareInfo.value);
    clipboard.destroy();
  });
};

const onEditFormParam = (row: ANY_OBJECT) => {
  Dialog.show(
    row ? '编辑参数' : '添加参数',
    EditFormParam,
    {
      area: '600px',
    },
    {
      rowData: row,
      path: 'thirdEditReportFormParam',
    },
    {
      height: '200px',
      width: '600px',
      pathName: '/thirdParty/thirdEditReportFormParam',
    },
  )
    .then(paramName => {
      handlerEditParam(row, paramName);
    })
    .catch(e => {
      console.warn(e);
    });
};
const onRemoveFormParam = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否移除此字段？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      formConfig().form.paramList = formConfig().form.paramList.filter(item => {
        return item !== row;
      });
    })
    .catch(e => {
      console.warn(e);
    });
};
</script>
