<template>
  <div class="preview-print-spread-sheet">
    <el-header
      class="step-header"
      style="overflow: hidden; height: 72px; background: white; border-bottom: 1px solid #ebeef5"
    >
      <el-row type="flex" justify="space-between" align="middle" style="height: 100%">
        <div class="title" style="display: flex; width: 200px; height: 40px; line-height: 40px">
          <i class="header-logo logo online-icon icon-orange-icon" style="font-size: 40px" />
          <span style="font-size: 22px; color: #333; font-weight: bold">橙单打印预览</span>
        </div>
        <el-row class="operation" justify="end">
          <el-button size="default" type="primary" @click="downloadFile">下载</el-button>
          <el-button size="default" type="primary" @click="doPrint">打印</el-button>
          <el-button size="default" @click="onCancelClick">退出</el-button>
        </el-row>
      </el-row>
    </el-header>
    <el-main style="padding: 16px; background: #f6f6f6">
      <div
        id="print-container"
        :style="{
          width: printFileConfig.width + 'mm',
          height: printFileConfig.height + 'mm',
        }"
        v-show="isPrint"
      >
        <div
          :style="{
            width: printFileConfig.width + 'mm',
            height: printFileConfig.height + 'mm',
            paddingTop: printFileConfig.topMargin + 'mm',
            paddingRight: printFileConfig.rightMargin + 'mm',
            paddingBottom: printFileConfig.bottomMargin + 'mm',
            paddingLeft: printFileConfig.leftMargin + 'mm',
            margin: '0 auto',
            pageBreakAfter: 'always',
            display: 'flex',
            flexWrap: 'wrap',
            justifyContent: 'center',
            alignItems: 'flex-start',
            boxSizing: 'border-box',
          }"
        >
          <table
            id="print-table"
            style="border-collapse: collapse; position: relative; transform-origin: top center"
            :style="{ transform: `scale(${zoomX})` }"
          >
            <!--       item row    -->
            <tr v-for="itemR in apiData.rows" :key="itemR.rowIndex">
              <template v-if="itemR.emptyRow == true">
                <td :colspan="apiData.maxCol + 1" :height="itemR.rowHeight + 'px'"></td>
              </template>
              <template v-else>
                <template v-for="itemC in itemR.cells">
                  <!--       item cell    -->
                  <template v-if="!itemC.mergeCell">
                    <preview-print-cell
                      :rows="apiData.rows"
                      :item-cell="itemC"
                      :item-row="itemR"
                      :key="'row_' + itemR.rowIndex + 'col_' + itemC.colIndex"
                    ></preview-print-cell>
                  </template>

                  <template
                    v-else-if="
                      itemC.mergeCell && (itemC.mergeCellColSpan || itemC.mergeCellRowSpan)
                    "
                  >
                    <preview-print-cell
                      :rows="apiData.rows"
                      :item-cell="itemC"
                      :item-row="itemR"
                      :key="'row_' + itemR.rowIndex + 'col_' + itemC.colIndex"
                    ></preview-print-cell>
                  </template>
                </template>
              </template>
            </tr>
          </table>
        </div>
      </div>
    </el-main>
  </div>
</template>

<script setup lang="ts">
import axios from 'axios';
import { ElLoading, ElMessage } from 'element-plus';
import print from 'print-js';
import { getToken } from '@/common/utils';
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';
import { useUrlBuilder } from '@/common/hooks/useUrl';
import { API_CONTEXT } from '@/api/config';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { ReportPrintController } from '@/api/report';
import SpreadSheet from './SpreadSheet';
import previewPrintCell from './previewPrintCell.vue';

interface IProps extends ThirdProps {
  printId: string;
  printName: string;
  printParam?: ANY_OBJECT[];
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<boolean>;
}
const props = defineProps<IProps>();

const { thirdParams, onCloseThirdDialog } = useThirdParty(props);

const xs = ref<SpreadSheet>();
const sheetData: ANY_OBJECT = {
  styles: [],
  merges: [],
  rows: {},
  cols: {},
};
const fullscreenLoading = ref<ANY_OBJECT>();
const imgSrc = ref<ANY_OBJECT[]>([]);

const { buildGetUrl } = useUrlBuilder();

type ApiDataRowItem = {
  rowIndex: number;
  rowHeight: number;
  emptyRow: boolean;
  cells: ANY_OBJECT[];
};

type ApiDataType = {
  rows: ApiDataRowItem[];
  maxCol: number;
};

const apiData = ref<ApiDataType>({
  rows: [],
  maxCol: 0,
});
const printFileConfig = reactive({
  width: '',
  height: '',
  topMargin: '',
  leftMargin: '',
  rightMargin: '',
  bottomMargin: '',
  landscape: false,
});
const isPrint = ref(true);
const onPreview = async () => {
  /*
  fullscreenLoading.value = ElLoading.service({
    lock: true,
    text: '数据加载中...',
    background: 'rgba(0, 0, 0, 0.05)',
  });
  */
  await axios
    .post(
      buildGetUrl(API_CONTEXT + '/report/reportPrint/preview'),
      {
        printId: props.printId || thirdParams.value.printId,
        printParam: props.printParam || thirdParams.value.printParam,
        renderType: 3,
      },
      {
        headers: {
          Authorization: getToken(),
        },
      },
    )
    .then(res => {
      if (res.status === 200) {
        apiData.value = res.data.reportSheet;
        handleData(res.data);
      } else {
        // fullscreenLoading.value?.close();
        ElMessage.error('请求失败！');
      }
    })
    .catch(() => {
      // fullscreenLoading.value?.close();
      ElMessage.error('请求失败！');
    });
};
const initSheet = () => {
  xs.value = new SpreadSheet('#x-spread-sheet', {
    mode: 'read',
    view: {
      height: () => document.documentElement.clientHeight - 94,
      width: () => document.documentElement.clientWidth - 32,
    },
    showGrid: true, // 是否显示网格，默认为ture
    showToolbar: false, // 是否显示工具栏，默认为ture
    showContextmenu: false, // 是否显示右键菜单，默认为ture
    showBottomBar: false,
    disableToolbar: [],
    row: {
      len: 100, // 行数，默认为100
      height: 25, // 行高，默认为25，为什么不需要indexHeight? 因为它借用了height的值，所以不需要indexHeight，如果想分开设置，可以设置indexHeight
      indexHeight: 25, // 行索引高度，默认为25，就是 A B C D ... 的高度
      minHeight: 60,
    },
  });
  xs.value.setData([sheetData]);
  fullscreenLoading.value?.close();
};
// 处理返回值格式，转换xSpreadSheet格式
const handleData = async (val: ANY_OBJECT) => {
  console.log('data', val);
  const data = val.reportSheet?.rows;
  let styleIndex = -1;
  let cols: ANY_OBJECT = {};
  // 行数据
  data?.forEach((row: ANY_OBJECT, rowIdx: number) => {
    let cells: ANY_OBJECT = {};
    // 列数据
    row.cells.forEach(async (col: ANY_OBJECT, colIdx: number) => {
      if (rowIdx === 0) {
        cols[colIdx] = { width: col.colWidth };
      }
      // 类型判断
      let text = '';
      let type = 'text';
      let value = '';
      if (col.imageCell && col.data) {
        type = 'image';
        text = '';
        // todo 获取图片
        const url = buildGetUrl(col.data + '&Authorization=' + getToken());
        imgSrc.value.push({ url: url, row: rowIdx, col: colIdx });
      } else {
        text = col?.data;
      }
      // 单元格样式解析格式化
      if (!col.emptyCell) {
        styleIndex++;
        let style = {
          border: {
            bottom: col?.hasBottomBorder ? ['medium', col.bottomBorderColor] : null,
            top: col?.hasTopBorder ? ['medium', col.topBorderColor] : null,
            left: col?.hasLeftBorder ? ['medium', col.leftBorderColor] : null,
            right: col?.hasRightBorder ? ['medium', col.rightBorderColor] : null,
          },
          underline: col?.underline,
          valign: col?.verticalType === 0 ? 'middle' : col?.verticalType === 1 ? 'top' : 'bottom',
          align:
            col?.horizontalType === 0 ? 'center' : col?.horizontalType === 1 ? 'left' : 'right',
          font: {
            size: col.fontSize,
            bold: col.bold,
            italic: col.italic,
            name: '微软雅黑',
          },
        };
        sheetData.styles[styleIndex] = style;
        // 加载单元格
        cells[colIdx] = {
          text: text,
          type: type,
          value: value,
          style: styleIndex,
        };
        // 图片属性设置
        if (cells[colIdx].type === 'image') {
          cells[colIdx].imagewidth = cols[colIdx].width - 2;
          cells[colIdx].imageheight = row.rowHeight - 2;
          cells[colIdx].top = 0;
          cells[colIdx].left = 0;
        }
        // 单元格解析合并处理
        if (col.mergeCell && !col.emptyCell) {
          cells[colIdx].merge = [col.mergeCellRowSpan - 1, col.mergeCellColSpan - 1];
          let merge =
            matchLetterToNumber(colIdx) +
            (rowIdx + 1) +
            ':' +
            matchLetterToNumber(colIdx + col.mergeCellColSpan - 1) +
            (col.mergeCellRowSpan + col.mergeCellTopRow);
          sheetData.merges.push(merge.toString());
        }
      }
    });
    sheetData.rows[rowIdx] = { cells: cells, height: row.rowHeight };
  });
  // 处理图片转码，后续页面滚动无需再次请求图片接口
  await Promise.all(
    imgSrc.value.map(async item => {
      const url = await imgToBase64(item.url);
      sheetData.rows[item.row].cells[item.col].value = url;
      return url;
    }),
  );
  sheetData.cols = cols;
  // initSheet();
};
// 图片前端转码
const imgToBase64 = (url: string) => {
  return new Promise(resolve => {
    const image = new Image();
    image.crossOrigin = 'Anonymous';
    image.src = url;
    image.onload = () => {
      const canvas: ANY_OBJECT = document.createElement('CANVAS');
      canvas.width = image.width;
      canvas.height = image.height;
      canvas.getContext('2d').drawImage(image, 0, 0);
      const dataURL = canvas.toDataURL('image/jpeg');
      resolve(dataURL);
    };
    image.onerror = () => {
      resolve({ message: '相片处理失败' });
    };
  });
};
// 字母转换
const matchLetterToNumber = (letter: number) => {
  let number = String.fromCharCode(65 + letter);
  return number;
};
const onCancelClick = () => {
  if (props.dialog) {
    props.dialog.cancel();
  } else {
    onCloseThirdDialog(true);
  }
};
// 下载文件
const downloadFile = () => {
  axios
    .post<ANY_OBJECT>(
      buildGetUrl(API_CONTEXT + '/report/reportPrint/preview'),
      {
        printId: props.printId,
        printParam: props.printParam,
        renderType: 2,
      },
      {
        headers: {
          Authorization: getToken(),
        },
        responseType: 'blob',
      },
    )
    .then(res => {
      if (res.status === 200 && res.data instanceof Blob) {
        downloadContentFile(props.printName || thirdParams.value.printName, res.data);
      } else {
        ElMessage.error('下载文件失败');
      }
    })
    .catch(e => {
      if (e instanceof Blob) {
        let reader: FileReader = new FileReader();
        reader.onload = () => {
          ElMessage.error(
            reader.result ? JSON.parse(reader.result.toString()).errorMessage : '下载文件失败',
          );
        };
        reader.readAsText(e);
      } else {
        ElMessage.error('下载文件失败');
      }
    });
};
const downloadContentFile = (filename: string, text: Blob) => {
  let blob = new Blob([text], {
    type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
  });
  const element = document.createElement('a');
  const href = URL.createObjectURL(blob);
  element.href = href;
  element.setAttribute('download', filename + '.xls');
  element.style.display = 'none';
  element.click();
  URL.revokeObjectURL(href);
  element.remove();
};

const doPrint = () => {
  print({
    // 打印区域id
    printable: 'print-table',
    // 打印类型是html
    type: 'html',
    scanStyles: true,
    targetStyles: ['*'],
    documentTitle: props.printName,
    font_size: '10px',
    style: `@page {size: ${printFileConfig.width}mm ${printFileConfig.height}mm portrait};`,
    onPrintDialogClose() {
      // clearInterval(focuser);
      // _this.isPrint = false;
    },
  });
};
const getDpi = num => {
  const inch = document.createElement('div');
  inch.style.width = '1in';
  // 将div的尺寸设置为1英寸后，它会自动根据设备的分辨率进行缩放
  document.body.appendChild(inch);
  const dpi = inch.offsetWidth;
  document.body.removeChild(inch);
  const unit = Number(num);
  const inchs = unit / 25.4; // 将毫米转换为英寸,cm就除与2.54以此类推
  const px = inchs * dpi; // 将英寸转换为像素
  return Math.round(px); // 四舍五入取整数像素值
};
onMounted(async () => {
  await onPreview();
});
</script>

<style scoped>
.preview-print-spread-sheet {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 100;
  width: 100vw;
  height: 100vh;
  background: #f9f9f9;
}
</style>
