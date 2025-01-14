<template>
  <el-row
    class="form-single-fragment third-party-dlg edit-template-word-column"
    style="position: relative"
  >
    <el-col class="form-box">
      <vxe-table
        class="column-list"
        :data="tableData"
        height="100%"
        :size="layoutStore.defaultFormItemSize"
        :row-config="{ isHover: true }"
        style="width: 100%"
        :tree-config="{
          transform: false,
          rowField: 'id',
          parentField: 'parentId',
          expandAll: true,
        }"
        header-cell-class-name="table-header-gray"
      >
        <vxe-column title="字段名称" field="name" tree-node width="150px" />
        <vxe-column title="字段类型" field="type" width="150px" />
        <vxe-column title="显示类型" field="dataType" width="150px">
          <template v-slot="scope">
            <el-select
              v-if="scope.row.isColumn"
              size="default"
              v-model="scope.row.dataType"
              placeholder="显示类型"
              style="width: 100%"
              @change="onCellTypeChange(scope.row)"
            >
              <el-option
                v-for="item in PrintCellType.getList()"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </template>
        </vxe-column>
        <vxe-column title="显示文字" field="barCodeShowWords" width="100px">
          <template v-slot="scope">
            <el-switch
              v-if="scope.row.isColumn"
              v-model="scope.row.barCodeShowWords"
              size="default"
              :disabled="scope.row.dataType !== PrintCellType.BARCODE"
            />
          </template>
        </vxe-column>
        <vxe-column title="字段设置">
          <template v-slot="scope">
            <!-- 条形码设置 -->
            <el-select
              v-if="scope.row.dataType === PrintCellType.BARCODE"
              size="default"
              v-model="scope.row.barCodeType"
              placeholder="条形码类型"
              style="margin-right: 10px"
            >
              <el-option
                v-for="item in PrintBarCodeType.getList()"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
            <!-- 二维码设置 -->
            <el-select
              v-if="scope.row.dataType === PrintCellType.QRCODE"
              size="default"
              v-model="scope.row.barCodeType"
              placeholder="二维码类型"
              style="margin-right: 10px"
            >
              <el-option
                v-for="item in PrintQRCodeType.getList()"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
            <!-- 图片设置 -->
            <el-select
              v-if="scope.row.dataType != null && scope.row.dataType !== PrintCellType.COLUMN"
              size="default"
              v-model="scope.row.autoSize"
              style="margin-right: 10px"
            >
              <el-option
                :disabled="scope.row.dataType !== PrintCellType.IMAGE"
                label="自动调整图片大小"
                :value="true"
              />
              <el-option label="指定图片大小" :value="false" />
            </el-select>
            <el-tag
              v-if="
                scope.row.autoSize &&
                scope.row.dataType != null &&
                scope.row.dataType !== PrintCellType.COLUMN
              "
              size="default"
              type="warning"
            >
              仅适用于图片位于表格内
            </el-tag>
            <el-input-number
              v-if="
                scope.row.dataType != null &&
                scope.row.dataType !== PrintCellType.COLUMN &&
                !scope.row.autoSize
              "
              size="default"
              v-model="scope.row.imageWidth"
              controls-position="right"
              placeholder="图片宽度(px)"
              style="width: 150px"
            />
            <el-input-number
              v-if="
                scope.row.dataType != null &&
                scope.row.dataType !== PrintCellType.COLUMN &&
                !scope.row.autoSize
              "
              size="default"
              v-model="scope.row.imageHeight"
              controls-position="right"
              placeholder="图片高度(px)"
              style="width: 150px; margin-left: 10px"
            />
            <!-- 字段设置 -->
            <el-select
              v-if="scope.row.dataType === PrintCellType.COLUMN"
              size="default"
              :disabled="scope.row.fieldType !== 'Date'"
              v-model="scope.row.dateFormat"
              placeholder="日期显示格式"
            >
              <el-option label="日期显示格式 - 数字格式" :value="0" />
              <el-option label="日期显示格式 - 中文格式" :value="1" />
            </el-select>
          </template>
        </vxe-column>
      </vxe-table>
    </el-col>
    <el-col class="menu-box" :span="24" style="margin-top: 15px; flex-basis: 0">
      <el-row type="flex" justify="end">
        <el-button :size="layoutStore.defaultFormItemSize" :plain="true" @click="onCancel(false)">
          取消
        </el-button>
        <el-button type="primary" :size="layoutStore.defaultFormItemSize" @click="onSubmit()">
          确定
        </el-button>
      </el-row>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { VxeColumn, VxeTable } from 'vxe-table';
import { PrintCellType, PrintQRCodeType, PrintBarCodeType } from '@/common/staticDict/report';
import { DialogProp } from '@/components/Dialog/types';
import { useDialog } from '@/components/Dialog/useDialog';
import { ANY_OBJECT } from '@/types/generic';
import { ReportDatasetController } from '@/api/report';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';

type DatasetInfo = {
  datasetId: string | number | undefined;
  name: string | undefined;
};

type RelationInfo = {
  datasetId: string | number | undefined;
  relationId: string | number | undefined;
  name: string | undefined;
};

type DatasetColumnInfo = {
  datasetId: string | number | undefined;
  relationId: string | number | undefined;
  columnName: string | undefined;
  dataType: number | undefined;
  barCodeShowWords?: boolean | undefined;
  barCodeType?: number | undefined;
  autoSize?: boolean | undefined;
  imageWidth?: number | undefined;
  imageHeight?: number | undefined;
  dateFormat?: number | undefined;
};

type TableItem = {
  parentId: string | number | undefined;
  id: string | number | undefined;
  name: string | undefined;
  type: string | undefined;
  isColumn: boolean | undefined;
  relationId: string | number | undefined;
  datasetId: string | number | undefined;
  dataType?: number | undefined;
  barCodeShowWords?: boolean | undefined;
  barCodeType?: number | undefined;
  autoSize?: boolean | undefined;
  imageWidth?: number | undefined;
  imageHeight?: number | undefined;
  dateFormat?: number | undefined;
  children?: Array<ANY_OBJECT> | undefined;
};

interface IProps extends ThirdProps {
  datasetInfo: DatasetInfo;
  datasetRelationList: Array<RelationInfo>;
  datasetColumnInfo: Array<DatasetColumnInfo>;
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT[]>;
}

const layoutStore = useLayoutStore();
const Dialog = useDialog();
const props = defineProps<IProps>();
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);

const dialogParams = computed(() => {
  return {
    datasetInfo: props.datasetInfo || thirdParams.value.datasetInfo,
    datasetRelationList: props.datasetRelationList || thirdParams.value.datasetRelationList || [],
    datasetColumnInfo: props.datasetColumnInfo || thirdParams.value.datasetColumnInfo,
  };
});

const tableData = ref<Array<TableItem>>([]);

const getColumnInfo = computed(() => {
  return Array.isArray(dialogParams.value.datasetColumnInfo)
    ? dialogParams.value.datasetColumnInfo
    : [];
});

const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  } else {
    onCloseThirdDialog(false);
  }
};

const onSubmit = () => {
  let tempData: Array<DatasetColumnInfo> = [];
  tableData.value.forEach((item: TableItem) => {
    if (item.children) {
      item.children.forEach(child => {
        let temp: DatasetColumnInfo = {
          datasetId: item.datasetId,
          relationId: item.relationId,
          columnName: child.id,
          dataType: child.dataType,
        };
        if (child.dataType === PrintCellType.COLUMN) {
          if (child.fieldType === 'Date') {
            temp.dateFormat = child.dateFormat;
            tempData.push(temp);
          }
        } else {
          temp.barCodeShowWords = child.barCodeShowWords;
          temp.barCodeType = child.barCodeType;
          temp.autoSize = child.autoSize;
          temp.imageWidth = child.imageWidth;
          temp.imageHeight = child.imageHeight;
          tempData.push(temp);
        }
      });
    }
  });
  console.log(tempData);
  if (props.dialog) {
    props.dialog.submit(tempData);
  } else {
    onCloseThirdDialog(true, dialogParams.value.datasetColumnInfo, tempData);
  }
};

const onCellTypeChange = (row: TableItem) => {
  row.barCodeShowWords = false;
  row.autoSize = true;
  row.imageWidth = undefined;
  row.imageHeight = undefined;
  row.dateFormat = 0;
  switch (row.dataType) {
    case PrintCellType.COLUMN:
      row.barCodeType = PrintBarCodeType.CODE_128;
      break;
    case PrintCellType.BARCODE:
      row.barCodeType = PrintBarCodeType.CODE_128;
      row.autoSize = false;
      break;
    case PrintCellType.QRCODE:
      row.barCodeType = PrintQRCodeType.QR_CODE;
      row.autoSize = false;
      break;
    case PrintCellType.IMAGE:
      row.barCodeType = PrintBarCodeType.CODE_128;
      break;
    default:
      row.barCodeType = PrintBarCodeType.CODE_128;
      break;
  }
};

type DatasetColumnResponse = {
  columnList: Array<ANY_OBJECT>;
  datasetInfo?: DatasetInfo;
  relationInfo?: RelationInfo;
};

const loadDatasetColumnList = (datasetInfo?: DatasetInfo, relationInfo?: RelationInfo) => {
  return new Promise<DatasetColumnResponse>((resolve, reject) => {
    ReportDatasetController.view({
      datasetId: (datasetInfo || {}).datasetId || (relationInfo || {}).datasetId,
    })
      .then(res => {
        res.data.columnList.forEach(item => {
          item.id = item.columnName;
          item.name = item.columnName;
        });
        resolve({
          columnList: res.data.columnList.map(column => {
            return {
              ...column,
              id: column.columnName,
            };
          }),
          datasetInfo: datasetInfo,
          relationInfo: relationInfo,
        });
      })
      .catch(e => {
        reject(e);
      });
  });
};

const initFormData = () => {
  tableData.value = [];
  let datasetPrmise = [
    loadDatasetColumnList(dialogParams.value.datasetInfo, undefined),
    ...dialogParams.value.datasetRelationList.map(relationInfo => {
      return loadDatasetColumnList(undefined, relationInfo);
    }),
  ];
  console.log(datasetPrmise);
  Promise.all<DatasetColumnResponse>(datasetPrmise)
    .then(res => {
      tableData.value = res.map((columnInfo: DatasetColumnResponse): TableItem => {
        return {
          parentId: columnInfo.relationInfo
            ? columnInfo.relationInfo.datasetId || dialogParams.value.datasetInfo.datasetId
            : columnInfo.datasetInfo?.datasetId,
          id: columnInfo.relationInfo
            ? columnInfo.relationInfo.relationId
            : columnInfo.datasetInfo?.datasetId,
          name: columnInfo.relationInfo
            ? columnInfo.relationInfo.name
            : columnInfo.datasetInfo?.name,
          type: columnInfo.relationInfo ? '一对一关联' : '主数据表',
          isColumn: false,
          relationId: columnInfo.relationInfo ? columnInfo.relationInfo.relationId : undefined,
          datasetId: columnInfo.relationInfo ? undefined : columnInfo.datasetInfo?.datasetId,
          children: (columnInfo.columnList || []).map(column => {
            let bindColumnInfo = getColumnInfo.value.find(item => {
              if (columnInfo.relationInfo) {
                return (
                  item.relationId === columnInfo.relationInfo.relationId &&
                  item.columnName === column.columnName
                );
              } else {
                return (
                  item.datasetId === columnInfo.datasetInfo?.datasetId &&
                  item.columnName === column.columnName
                );
              }
            });
            return {
              parentId: columnInfo.relationInfo
                ? columnInfo.relationInfo.relationId
                : columnInfo.datasetInfo?.datasetId,
              id: column.columnName,
              name: column.columnName,
              fieldType: column.fieldType,
              columnInfo: column,
              type: '表字段',
              dataType: bindColumnInfo ? bindColumnInfo.dataType : PrintCellType.COLUMN,
              barCodeShowWords: bindColumnInfo ? bindColumnInfo.barCodeShowWords : false,
              barCodeType: bindColumnInfo ? bindColumnInfo.barCodeType : PrintBarCodeType.CODE_128,
              autoSize: bindColumnInfo ? bindColumnInfo.autoSize : true,
              imageWidth: bindColumnInfo ? bindColumnInfo.imageWidth : undefined,
              imageHeight: bindColumnInfo ? bindColumnInfo.imageHeight : undefined,
              dateFormat: bindColumnInfo ? bindColumnInfo.dateFormat : 0,
              isColumn: true,
            };
          }),
        };
      });
      console.log(tableData.value);
    })
    .catch(e => {
      console.log(e);
    });
};

onMounted(() => {
  initFormData();
});
</script>

<style scoped>
.edit-template-word-column {
  height: 100%;
}
.column-list .el-select {
  width: 210px;
}
</style>
