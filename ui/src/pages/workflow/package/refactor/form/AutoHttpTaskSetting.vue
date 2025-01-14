<template>
  <div class="autotask-http-settting">
    <el-form-item label="API Url">
      <el-input
        placeholder="请输入请求URL"
        v-model="formData.httpRequestInfo.url"
        @change="onChange"
      >
        <template #prepend>
          <el-select
            class="url-select"
            style="width: 100px"
            v-model="formData.httpRequestInfo.httpMethod"
            placeholder="请选择"
            @change="onChange"
          >
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
          </el-select>
        </template>
      </el-input>
    </el-form-item>
    <MultiItemList
      label="请求头设置"
      :data="formData.httpRequestInfo.headerList"
      addText="添加"
      @add="onEditHttpHeader()"
      @edit="onEditHttpHeader"
      @delete="onDeleteHttpHeader"
      :prop="{
        label: 'key',
        value: 'key',
      }"
    >
      <template v-slot="scope">
        <span>{{ (scope.data || {}).key }}</span>
        <span style="margin: 0px 10px">等于</span>
        <span style="margin: 0px 10px 0px 0px">{{
          AutoTaskValueType.getValue(scope.data.type)
        }}</span>
        <span>
          {{ (scope.data || {}).srcValue }}
        </span>
      </template>
    </MultiItemList>
    <MultiItemList
      label="URL参数设置"
      :data="formData.httpRequestInfo.urlParamList"
      addText="添加"
      @add="onEditUrlParam()"
      @edit="onEditUrlParam"
      @delete="onDeleteUrlParam"
      :prop="{
        label: 'name',
        value: 'id',
      }"
    >
      <template v-slot="scope">
        <span>{{ (scope.data || {}).key }}</span>
        <span style="margin: 0px 10px">等于</span>
        <span style="margin: 0px 10px 0px 0px">{{
          AutoTaskValueType.getValue(scope.data.type)
        }}</span>
        <span>
          {{ (scope.data || {}).srcValue }}
        </span>
      </template>
    </MultiItemList>
    <el-form-item labe="请求Body类型">
      <el-radio-group v-model="formData.httpRequestInfo.bodyType" @change="onChange">
        <el-radio v-for="item in BodyType.getList()" :key="item.id" :label="item.id">
          <el-row type="flex" align="middle">
            <span>{{ item.name }}</span>
            <el-dropdown
              v-if="formData.httpRequestInfo.bodyType === BodyType.RAW && item.id === BodyType.RAW"
              style="margin-left: 10px"
              @command="cmd => (formData.httpRequestInfo.rawType = cmd)"
            >
              <el-link>{{ RawType.getValue(formData.httpRequestInfo.rawType) }}</el-link>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item
                    v-for="item in RawType.getList()"
                    :key="item.id"
                    :command="item.id"
                  >
                    {{ item.name }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </el-row>
        </el-radio>
      </el-radio-group>
    </el-form-item>
    <MultiItemList
      v-if="formData.httpRequestInfo.bodyType === BodyType.FORM_DATA"
      label="Body参数设置"
      :data="formData.httpRequestInfo.formDataList"
      addText="添加"
      @add="onEditBodyParam()"
      @edit="onEditBodyParam"
      @delete="onDeleteBodyParam"
      :prop="{
        label: 'name',
        value: 'id',
      }"
    >
      <template v-slot="scope">
        <span>{{ (scope.data || {}).key }}</span>
        <span style="margin: 0px 10px">等于</span>
        <span style="margin: 0px 10px 0px 0px">{{
          AutoTaskValueType.getValue(scope.data.type)
        }}</span>
        <span>
          {{ (scope.data || {}).srcValue }}
        </span>
      </template>
    </MultiItemList>
    <el-form-item label="Body参数设置" v-if="formData.httpRequestInfo.bodyType === BodyType.RAW">
      <el-input
        v-model="formData.httpRequestInfo.bodyData"
        type="textarea"
        rows="5"
        clearable
        placeholder="请输入请求Body"
        @change="onChange"
      />
    </el-form-item>
    <el-form-item label="请求成功的HTTP状态码">
      <el-input
        v-model="formData.httpResponnseData.successStatusCode"
        clearable
        placeholder="示例：200,201 (多个状态码使用英文逗号分隔)"
        @change="onChange"
      />
    </el-form-item>
    <el-form-item label="请求成功字段">
      <el-input
        v-model="formData.httpResponnseData.successBodyField"
        clearable
        placeholder="示例：data.isSuccess 请求成功情况下，进一步判断是否成功字段"
        @change="onChange"
      />
    </el-form-item>
    <el-form-item label="失败信息字段">
      <el-input
        v-model="formData.httpResponnseData.errorMessageBodyField"
        clearable
        placeholder="示例：data.message 请求成功情况下，返回的失败信息字段"
        @change="onChange"
      />
    </el-form-item>
    <el-form-item label="请求失败处理方式">
      <el-radio-group v-model="formData.httpResponnseData.failHandleType" @change="onChange">
        <el-radio v-for="item in FailHandleType.getList()" :key="item.id" :label="item.id">
          {{ item.name }}
        </el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="响应数据">
      <el-table
        :show-header="false"
        :data="responseDataTree"
        size="mini"
        row-key="id"
        :tree-props="{ children: 'children' }"
      >
        <el-table-column prop="fieldName" label="字段名" />
        <el-table-column prop="fieldType" label="字段类型" width="100px" />
        <el-table-column label="操作" width="120px">
          <template v-slot="scope">
            <el-button type="primary" link size="small" @click="onEditResponseData(scope.row)">
              编辑
            </el-button>
            <el-button type="primary" link size="small" @click="onDeleteResponseData(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-button
        type="primary"
        plain
        size="small"
        icon="el-icon-plus"
        style="width: 100%"
        @click="onEditResponseData()"
      >
        添加响应字段
      </el-button>
    </el-form-item>
  </div>
</template>

<script setup lang="ts">
import { ElMessageBox } from 'element-plus';
import { defineProps, defineEmits } from 'vue';
import EditHttpHeader from './editHttpHeader.vue';
import EditHttpParameter from './editHttpParameter.vue';
import EditResponseData from './editResponseData.vue';
import { treeDataTranslate } from '@/common/utils/index';
import { DictionaryBase } from '@/common/staticDict/types';
import MultiItemList from '@/components/MultiItemList/index.vue';
import { Dialog } from '@/components/Dialog';
import { AutoTaskActionType, AutoTaskValueType } from '@/common/staticDict/flow';
import { ANY_OBJECT } from '@/types/generic';

const emit = defineEmits(['update:modelValue', 'change']);
const props = defineProps<{
  modelValue: string;
}>();
const flowEntry = inject('flowEntry', () => {
  return {} as ANY_OBJECT;
});
const formList = inject('formList', () => {
  return {} as ANY_OBJECT;
});
const prefix = inject('prefix');
const getAllAutoVariableList = inject('getAllAutoVariableList', () => {
  return [];
});
type FormDataType = {
  actionType?: number;
  httpRequestInfo: {
    url: string;
    httpMethod: string;
    headerList: Array<ANY_OBJECT>;
    bodyType: string;
    urlParamList: Array<ANY_OBJECT>;
    formDataList: Array<ANY_OBJECT>;
    rawType: string;
    bodyData: string;
  };
  httpResponnseData: {
    successStatusCode: string;
    successBodyField: string;
    errorMessageBodyField: string;
    failHandleType: string;
    httpResponseBody: Array<ANY_OBJECT>;
  };
};
const formData = ref<FormDataType>({
  actionType: AutoTaskActionType.HTTP,
  httpRequestInfo: {
    url: '',
    httpMethod: 'GET',
    headerList: [],
    bodyType: 'formData',
    urlParamList: [],
    formDataList: [],
    rawType: 'json',
    bodyData: '',
  },
  httpResponnseData: {
    successStatusCode: '',
    successBodyField: '',
    errorMessageBodyField: '',
    failHandleType: 'stop',
    httpResponseBody: [],
  },
});

const BodyType = new DictionaryBase('Body类型', [
  {
    id: 'formData',
    name: 'form-data',
    symbol: 'FORM_DATA',
  },
  {
    id: 'raw',
    name: 'raw',
    symbol: 'RAW',
  },
]);

const RawType = new DictionaryBase('Raw类型', [
  {
    id: 'text',
    name: 'text',
    symbol: 'TEXT',
  },
  {
    id: 'json',
    name: 'json',
    symbol: 'JSON',
  },
]);

const FailHandleType = new DictionaryBase('失败处理方式', [
  {
    id: 'stop',
    name: '流程中止执行',
    symbol: 'EXIT',
  },
  {
    id: 'continue',
    name: '继续执行',
    symbol: 'CONTINUE',
  },
]);

const responseDataTree = computed(() => {
  let dataList = formData.value.httpResponnseData.httpResponseBody.map(item => {
    return {
      ...item,
      disabled: item.fieldType !== 'Object' && item.fieldType !== 'Array',
    };
  });
  return treeDataTranslate(dataList, 'id', 'parentId');
});

const onChange = () => {
  let tempData = {
    ...formData.value,
  };
  emit('update:modelValue', JSON.stringify(tempData));
  emit('change', JSON.stringify(tempData));
};

const updateHttpHeader = (row, data) => {
  if (row == null) {
    // 新建
    if (formData.value.httpRequestInfo.headerList == null) {
      formData.value.httpRequestInfo.headerList = [];
    }
    formData.value.httpRequestInfo.headerList.push(data);
  } else {
    // 编辑
    formData.value.httpRequestInfo.headerList = formData.value.httpRequestInfo.headerList.map(
      item => {
        return item.key === data.key ? data : item;
      },
    );
  }
  onChange();
};

const onEditHttpHeader = (row?: ANY_OBJECT) => {
  Dialog.show(
    '编辑请求头',
    EditHttpHeader,
    {
      area: ['500px', '300px'],
    },
    {
      entryId: flowEntry().entryId,
      data: row,
      usedKeyList: formData.value.httpRequestInfo.headerList.map(item => item.key),
      flowVariableList: getAllAutoVariableList(),
      path: 'thirdEditHttpHeader',
    },
    {
      width: '500px',
      height: '300px',
      pathName: '/thirdParty/thirdEditHttpHeader',
    },
  )
    .then(res => {
      updateHttpHeader(row, res);
    })
    .catch(e => {
      console.log(e);
    });
};

const onDeleteHttpHeader = row => {
  ElMessageBox.confirm('是否删除此请求头？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      formData.value.httpRequestInfo.headerList = formData.value.httpRequestInfo.headerList.filter(
        item => item.key !== row.key,
      );
      onChange();
    })
    .catch(e => {
      console.log(e);
    });
};

const updateUrlParam = (row, data) => {
  if (row == null) {
    // 新建
    if (formData.value.httpRequestInfo.urlParamList == null) {
      formData.value.httpRequestInfo.urlParamList = [];
    }
    formData.value.httpRequestInfo.urlParamList.push(data);
  } else {
    // 编辑
    formData.value.httpRequestInfo.urlParamList = formData.value.httpRequestInfo.urlParamList.map(
      item => {
        return item.key === data.key ? data : item;
      },
    );
  }
  onChange();
};

const onEditUrlParam = (row?: ANY_OBJECT) => {
  Dialog.show(
    '编辑URL参数',
    EditHttpParameter,
    {
      area: ['500px', '400px'],
    },
    {
      entryId: flowEntry().entryId,
      data: row,
      usedParamList: formData.value.httpRequestInfo.urlParamList.map(item => item.key),
      flowVariableList: getAllAutoVariableList(),
      path: 'thirdEditHttpUrlParam',
    },
    {
      width: '500px',
      height: '400px',
      pathName: '/thirdParty/thirdEditHttpRequestParam',
    },
  )
    .then(res => {
      updateUrlParam(row, res);
    })
    .catch(e => {
      console.log(e);
    });
};

const onDeleteUrlParam = row => {
  ElMessageBox.confirm('是否删除此URL参数？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(res => {
      formData.value.httpRequestInfo.urlParamList =
        formData.value.httpRequestInfo.urlParamList.filter(item => item.key !== row.key);
      onChange();
    })
    .catch(e => {
      console.log(e);
    });
};

const updateBodyParam = (row, res) => {
  if (formData.value.httpRequestInfo.formDataList == null) {
    formData.value.httpRequestInfo.formDataList = [];
  }
  if (row == null) {
    // 新建
    formData.value.httpRequestInfo.formDataList.push(res);
  } else {
    // 编辑
    formData.value.httpRequestInfo.formDataList = formData.value.httpRequestInfo.formDataList.map(
      item => {
        return item.key === res.key ? res : item;
      },
    );
  }
  onChange();
};

const onEditBodyParam = (row?: ANY_OBJECT) => {
  Dialog.show(
    '编辑Body参数',
    EditHttpParameter,
    {
      area: ['500px', '400px'],
    },
    {
      entryId: flowEntry().entryId,
      data: row,
      usedParamList: formData.value.httpRequestInfo.formDataList.map(item => item.key),
      flowVariableList: getAllAutoVariableList(),
      path: 'thirdEditHttpBodyParam',
    },
    {
      width: '500px',
      height: '400px',
      pathName: '/thirdParty/thirdEditHttpRequestParam',
    },
  )
    .then(res => {
      updateBodyParam(row, res);
    })
    .catch(e => {
      console.log(e);
    });
};

const onDeleteBodyParam = row => {
  ElMessageBox.confirm('是否删除此Body参数？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(res => {
      formData.value.httpRequestInfo.formDataList =
        formData.value.httpRequestInfo.formDataList.filter(item => item.key !== row.key);
      onChange();
    })
    .catch(e => {
      console.log(e);
    });
};

const updateResponseData = (row, res) => {
  function buildChildId(node, parentId) {
    if (parentId == null || parentId === '') {
      node.id = parentId + '.' + node.fieldName;
    } else {
      node.id = node.fieldName;
    }
    if (Array.isArray(node.children)) {
      node.children.forEach(child => {
        buildChildId(child, node.id);
      });
    }
  }
  if (formData.value.httpResponnseData.httpResponseBody == null) {
    formData.value.httpResponnseData.httpResponseBody = [];
  }
  if (row == null) {
    // 新建
    formData.value.httpResponnseData.httpResponseBody.push(res);
  } else {
    // 编辑
    formData.value.httpResponnseData.httpResponseBody =
      formData.value.httpResponnseData.httpResponseBody.map(item => {
        if (item.id === res.oldIId) {
          buildChildId(res, res.parentId);
          return res;
        } else {
          return item;
        }
      });
  }
  onChange();
};

const onEditResponseData = (row?: ANY_OBJECT) => {
  Dialog.show(
    '编辑响应字段',
    EditResponseData,
    {
      area: ['500px', '400px'],
    },
    {
      entryId: flowEntry().entryId,
      data: row,
      usedFieldList: responseDataTree.value,
      path: 'thirdEditResponseData',
    },
    {
      width: '500px',
      height: '400px',
      pathName: '/thirdParty/thirdEditResponseData',
    },
  )
    .then(res => {
      updateResponseData(row, res);
    })
    .catch(e => {
      console.log(e);
    });
};

const onDeleteResponseData = row => {
  ElMessageBox.confirm('是否删除此响应字段？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(res => {
      formData.value.httpResponnseData.httpResponseBody =
        formData.value.httpResponnseData.httpResponseBody.filter(item => item.id !== row.id);
      onChange();
    })
    .catch(e => {
      console.log(e);
    });
};

watch(
  () => props.modelValue,
  val => {
    let taskInfo = val && val !== '' ? JSON.parse(val) : {};
    if (taskInfo.httpRequestInfo == null) {
      taskInfo.httpRequestInfo = {};
    }
    if (taskInfo.httpResponnseData == null) {
      taskInfo.httpResponnseData = {};
    }
    formData.value = {
      actionType: AutoTaskActionType.HTTP,
      httpRequestInfo: {
        url: taskInfo.httpRequestInfo.url || '',
        httpMethod: taskInfo.httpRequestInfo.httpMethod || 'GET',
        headerList: taskInfo.httpRequestInfo.headerList || [],
        bodyType: taskInfo.httpRequestInfo.bodyType || 'formData',
        urlParamList: taskInfo.httpRequestInfo.urlParamList || [],
        formDataList: taskInfo.httpRequestInfo.formDataList || [],
        rawType: taskInfo.httpRequestInfo.rawType || 'json',
        bodyData: taskInfo.httpRequestInfo.bodyData || '',
      },
      httpResponnseData: {
        successStatusCode: taskInfo.httpResponnseData.successStatusCode || '',
        successBodyField: taskInfo.httpResponnseData.successBodyField || '',
        errorMessageBodyField: taskInfo.httpResponnseData.errorMessageBodyField || '',
        failHandleType: taskInfo.httpResponnseData.failHandleType || 'stop',
        httpResponseBody: taskInfo.httpResponnseData.httpResponseBody || [],
      },
    };
  },
  { immediate: true },
);
</script>

<style scoped>
.url-select {
  width: 100px;
}
.url-select /deep/ .el-input--suffix {
  width: 100px;
}
.url-select /deep/ .el-input__inner {
  padding: 0px 15px;
}
</style>
