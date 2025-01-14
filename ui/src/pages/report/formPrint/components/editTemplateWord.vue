<template>
  <el-row type="flex" justify="center" class="edit-template-word">
    <div class="main-box">
      <vxe-table
        :data="getTemplateDatasetList"
        :size="layoutStore.defaultFormItemSize"
        :row-config="{ isHover: true }"
        style="width: 100%"
        header-cell-class-name="table-header-gray"
      >
        <vxe-column title="数据名称" field="showName" />
        <vxe-column title="数据标识" field="variableName" />
        <vxe-column title="数据集" field="datasetPath" />
        <vxe-column title="是否迭代" field="loop" width="80px">
          <template v-slot="scope">
            <el-tag size="default" :type="scope.row.loop ? 'success' : 'danger'">
              {{ scope.row.loop ? '是' : '否' }}
            </el-tag>
          </template>
        </vxe-column>
        <vxe-column title="操作" width="200px">
          <template v-slot="scope">
            <el-button size="default" type="primary" link @click="onEditTemplateDataset(scope.row)">
              编辑
            </el-button>
            <el-button
              size="default"
              type="primary"
              link
              @click="onEditTemplateDatasetColumn(scope.row)"
            >
              字段管理
            </el-button>
            <el-button
              size="default"
              type="primary"
              link
              @click="onDeleteTemplateDataset(scope.row)"
            >
              删除
            </el-button>
          </template>
        </vxe-column>
      </vxe-table>
      <el-button
        style="width: 100%; margin-top: 10px; border: 1px dashed #ebeef5"
        :size="layoutStore.defaultFormItemSize"
        icon="el-icon-plus"
        @click="onEditTemplateDataset()"
      >
        新增数据集
      </el-button>
      <div class="upload-box">
        <el-upload
          name="uploadFile"
          drag
          :headers="getUploadHeaders"
          accept=".doc,.docx"
          :file-list="wordWidgetFileList"
          :show-file-list="false"
          :action="getUploadActionUrl('/admin/report/reportPrint/upload')"
          :data="{ fieldName: 'wordTemplate', asImage: false }"
          :on-success="onWordUploadSuccess"
          :on-error="onUploadError"
        >
          <el-icon class="el-icon-upload" size="67px" color="#C0C4CC"><UploadFilled /></el-icon>
          <div v-if="wordWidgetFileList.length <= 0" class="el-upload__text">
            将模版文件拖到此处，或<em>点击上传</em>
          </div>
          <a
            v-else
            class="el-upload__text"
            :href="wordWidgetFileList[0].url"
            target="_blank"
            style="display: block"
          >
            {{ wordWidgetFileList[0].name || wordWidgetFileList[0].filename }}
          </a>
        </el-upload>
      </div>
    </div>
  </el-row>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, defineProps, withDefaults } from 'vue';
import { VxeColumn, VxeTable } from 'vxe-table';
import { UploadFilled } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox, UploadFile } from 'element-plus';
import { useLayoutStore } from '@/store';
import { useCommon } from '@/common/hooks/useCommon';
import { DialogProp } from '@/components/Dialog/types';
import { ANY_OBJECT } from '@/types/generic';
import { treeDataTranslate } from '@/common/utils';
import { useUpload } from '@/common/hooks/useUpload';
import { useUploadWidget } from '@/common/hooks/useUploadWidget';
import { ReportDatasetGroupController } from '@/api/report';
import EditTemplateWordDataset from './editTemplateWordDataset.vue';
import EditTemplateWordColumn from './editTemplateWordColumn.vue';

const layoutStore = useLayoutStore();
const { getUploadHeaders, getUploadActionUrl, fileListToJson, parseUploadData, getPictureList } =
  useUpload();
const {
  Delete,
  Search,
  Edit,
  Plus,
  Refresh,
  Picture,
  Dialog,
  mainContextHeight,
  clientHeight,
  checkPermCodeExist,
  parseParams,
  parseArrayParams,
  formatDateByStatsType,
  getDateRangeFilter,
} = useCommon();

type PrintInfo = {
  printId: string | undefined;
  wordTemplate: string | undefined;
  fragmentJson: ANY_OBJECT[];
};
const props = withDefaults(
  defineProps<{
    printInfo?: PrintInfo;
    // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
    dialog?: DialogProp<ANY_OBJECT[]>;
  }>(),
  {
    printInfo: (): PrintInfo => {
      return {
        printId: undefined,
        wordTemplate: undefined,
        fragmentJson: [],
      };
    },
    dialog: undefined,
  },
);

const formData = reactive({
  datasetGroupList: [] as ANY_OBJECT[],
  datasetList: [] as ANY_OBJECT[],
});

const wordUploadWidget = useUploadWidget(1);
const { fileList: wordWidgetFileList, maxCount: wordWidgetMaxCount } = wordUploadWidget;

const getTemplateDatasetList = computed(() => {
  return props.printInfo.fragmentJson.filter(item => {
    let dataset = formData.datasetList.find(ds => ds.datasetId === item.datasetId);
    console.log(dataset);
    item.datasetPath = (dataset || {}).datasetName || '';
    return true;
  });
});
const getDatasetTree = computed(() => {
  let groupMap = new Map();
  formData.datasetGroupList.forEach(item => {
    groupMap.set(item.groupId, item);
  });
  let allUsedIdList: Array<string | number> = [];
  formData.datasetList.forEach(dataset => {
    let id = dataset.parentId;
    while (id) {
      allUsedIdList.push(id);
      let group = groupMap.get(id);
      if (group) {
        id = group.parentId;
      } else {
        id = null;
      }
    }
  });
  let allNodeList = formData.datasetGroupList
    .filter(item => allUsedIdList.indexOf(item.id) !== -1)
    .concat(formData.datasetList);
  allNodeList = treeDataTranslate(allNodeList, 'id', 'parentId');
  console.log(allNodeList);
  return allNodeList;
});

const loadAllDatasetInfo = () => {
  ReportDatasetGroupController.listAll({})
    .then(res => {
      formData.datasetGroupList = (res.data.reportDatasetGroupList || []).map(item => {
        return {
          ...item,
          id: item.groupId,
          name: item.groupName,
        };
      });
      formData.datasetList = (res.data.reportDatasetList || []).map(item => {
        try {
          let info = JSON.parse(item.datasetInfo);
          item.datasetParamList = info.paramList;
        } catch (e) {
          item.datasetParamList = [];
        }
        return {
          ...item,
          parentId: item.groupId,
          id: item.datasetId,
          name: item.datasetName,
          columnList: undefined,
          isDataset: true,
        };
      });
    })
    .catch(e => {
      console.error(e);
    });
};

const onEditTemplateDataset = (row: ANY_OBJECT | undefined) => {
  Dialog.show<ANY_OBJECT>(
    row ? '编辑数据集' : '新增数据集',
    EditTemplateWordDataset,
    {
      area: ['500px', '600px'],
    },
    {
      rowData: row,
      printInfo: props.printInfo,
      datasetTree: getDatasetTree.value,
      path: 'thirdPrintEditTemplateWord',
    },
    {
      width: '500px',
      height: '600px',
      pathName: '/thirdParty/thirdPrintEditTemplateWord',
    },
  )
    .then(res => {
      if (row) {
        // eslint-disable-next-line vue/no-mutating-props
        props.printInfo.fragmentJson = props.printInfo.fragmentJson.map((item: ANY_OBJECT) => {
          if (item === row) {
            return res;
          }
          return item;
        });
      } else {
        // eslint-disable-next-line vue/no-mutating-props
        props.printInfo.fragmentJson.push(res);
      }
    })
    .catch(e => {
      console.error(e);
    });
};

const onDeleteTemplateDataset = row => {
  ElMessageBox.confirm('确定删除该数据集吗？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      // eslint-disable-next-line vue/no-mutating-props
      props.printInfo.fragmentJson = props.printInfo.fragmentJson.filter(item => item !== row);
    })
    .catch(() => {
      // do nothing
    });
};

const onEditTemplateDatasetColumn = row => {
  let dataset = formData.datasetList.find(ds => ds.datasetId === row.datasetId);
  Dialog.show<ANY_OBJECT>(
    '字段管理',
    EditTemplateWordColumn,
    {
      area: ['1400px', '800px'],
    },
    {
      datasetInfo: {
        datasetId: row.datasetId,
        name: row.datasetPath,
      },
      datasetRelationList: ((dataset || {}).relationList || []).map(relation => {
        return {
          datasetId: relation.slaveDatasetId,
          relationId: relation.relationId,
          name: relation.variableName,
        };
      }),
      datasetColumnInfo: row.datasetColumnInfo,
      path: 'thirdPrintEditTemplateWordColumn',
    },
    {
      width: '1400px',
      height: '800px',
      pathName: '/thirdParty/thirdPrintEditTemplateWordColumn',
    },
  )
    .then(res => {
      row.datasetColumnInfo = res;
    })
    .catch(e => {
      console.error(e);
    });
};

const onWordUploadSuccess = (response: ANY_OBJECT, file: UploadFile, fileList: UploadFile[]) => {
  if (response.success) {
    file.downloadUri = response.data.downloadUri;
    file.filename = response.data.filename;
    file.uploadPath = response.data.uploadPath;
    file.url = URL.createObjectURL(file.raw);
    wordUploadWidget.onFileChange(file, [file]);
    // eslint-disable-next-line vue/no-mutating-props
    props.printInfo.wordTemplate = fileListToJson(wordWidgetFileList.value);
  } else {
    ElMessage.error(response.message);
  }
};

const onUploadError = () => {
  ElMessage.error('文件上传失败');
};

onMounted(() => {
  loadAllDatasetInfo();
  let downloadParams = {
    printId: props.printInfo.printId,
    fieldName: 'wordTemplate',
    asImage: false,
  };
  wordUploadWidget.fileList.value = parseUploadData(props.printInfo.wordTemplate, downloadParams);
});
</script>

<style scoped>
.edit-template-word {
  width: 100%;
  height: 100%;
  padding: 15px;
}
.main-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 900px;
  height: 100%;
  background-color: #fff;
  padding: 15px;
}
.upload-box {
  width: 100%;
  border: 1px dashed #d9d9d9;
  border-radius: 5px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 15px 0px;
}
.upload-box /deep/ .el-upload-dragger {
  border: none;
}
.add-btn {
  width: 100%;
  margin-top: 15px;
}
</style>
