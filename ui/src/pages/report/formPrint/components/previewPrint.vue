<template>
  <el-row
    class="form-single-fragment print-preview third-party-dlg"
    style="position: relative; flex-direction: row; flex-wrap: wrap !important"
  >
    <el-col class="form-box" :span="24">
      <vxe-table
        :data="printParamList"
        size="default"
        height="255px"
        :row-config="{ isHover: true }"
        header-cell-class-name="table-header-gray"
      >
        <vxe-column type="seq" title="序号" width="55px" />
        <vxe-column title="参数名" field="paramName" width="150px" />
        <vxe-column title="参数值" field="paramValue">
          <template v-slot="scope">
            <el-input v-model="scope.row.paramValue" size="default" style="width: 250px" />
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
    <el-col class="menu-box" :span="24" style="margin-top: 15px">
      <el-row type="flex" justify="end">
        <el-button :size="itemSize" :plain="true" @click="onCancel()"> 取消 </el-button>
        <el-button type="primary" :size="itemSize" @click="onPreview()">
          {{ dialogParams.templateType === PrintTemplateType.WORD ? '下载' : '预览' }}
        </el-button>
      </el-row>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { VxeColumn, VxeTable } from 'vxe-table';
import { download } from '@/common/http/request';
import { PrintTemplateType } from '@/common/staticDict/report';
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';
import { Dialog } from '@/components/Dialog';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
import PreviewPrintSpreadSheet from './previewPrintSpreadSheet.vue';

const layoutStore = useLayoutStore();
interface IProps extends ThirdProps {
  printId?: string;
  printName?: string;
  paramList?: ANY_OBJECT[];
  templateType?: number;
  defaultFormItemSize: Ref<'' | 'default' | 'small' | 'large'>;
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<boolean>;
}
const props = defineProps<IProps>();
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);
const itemSize = computed(() => {
  return layoutStore.defaultFormItemSize || thirdParams.value.defaultFormItemSize?.value;
});

const dialogParams = computed(() => {
  return {
    printId: props.printId || thirdParams.value.printId,
    printName: props.printName || thirdParams.value.printName,
    paramList: props.paramList || thirdParams.value.paramList || [],
    templateType: props.templateType == null ? thirdParams.value.templateType : props.templateType,
  };
});
const printParamList = computed(() => {
  if (Array.isArray(dialogParams.value.paramList)) {
    return dialogParams.value.paramList.map(item => {
      return {
        paramName: item.variableName,
        paramValue: '1774289798584864768',
      };
    });
  } else {
    return [];
  }
});

const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  } else {
    onCloseThirdDialog(false);
  }
};
const onPreview = () => {
  if (dialogParams.value.templateType === PrintTemplateType.WORD) {
    // 下载word文件
    download(
      'admin/report/reportPrint/previewWithUrl',
      {
        printId: dialogParams.value.printId,
        printParam: printParamList.value,
        renderType: 4,
      },
      dialogParams.value.printName + '.docx',
    ).catch(e => {
      console.error(e);
    });
  } else {
    Dialog.show(
      'x-spread-sheet预览',
      PreviewPrintSpreadSheet,
      {
        area: ['100vw', '100vh'],
        skin: 'fullscreen-dialog',
      },
      {
        printId: dialogParams.value.printId,
        printParam: printParamList.value,
        printName: dialogParams.value.printName,
        path: 'thirdPrintSpreadSheet',
      },
      {
        fullscreen: true,
        pathName: '/thirdParty/thirdPrintSpreadSheet',
      },
    ).catch(e => {
      console.warn(e);
    });
    if (props.dialog) {
      props.dialog.submit(true);
    } else {
      onCloseThirdDialog(true);
    }
  }
};
</script>

<style>
.print-preview .layui-layer-content {
  padding: 0 0 20px !important;
}
</style>
