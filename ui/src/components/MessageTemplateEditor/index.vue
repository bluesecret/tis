<template>
  <div class="expression-editor">
    <div class="expression-box" @click="handleEditorClick">
      <div class="expression-con">
        <div style="display: inline">
          <span class="expression-filed">消息模版</span>
          <span class="expression-filed">=</span>
        </div>
        <el-scrollbar style="flex-grow: 1; width: 100px">
          <div class="template-edit-box">
            <div class="template-item">
              <span class="expression-filed" @click="onClickItem(-1)"></span>
            </div>
            <div class="template-item">
              <!-- <span v-if="currentIndex === 0 && focus" class="cursor" /> -->
              <input
                v-if="currentIndex === 0"
                style="border: none; width: 3px"
                ref="inputRef0"
                v-model="inputValue"
                @input="handleInput"
                @blur="handleBlur"
              />
            </div>
            <div
              class="template-item"
              v-for="(fitem, index) in fieldList"
              :key="fitem.itemName + '_' + index"
              :style="itemStyle(fitem)"
            >
              <span
                :style="{
                  'background-color': fitem.itemKind == FormulaItemKind.VARABLE ? '#eee' : '',
                  padding: fitem.itemKind == FormulaItemKind.VARABLE ? '2px 8px' : '2px 0',
                }"
                class="expression-filed"
                :class="{ 'expression-fild-string': fitem.itemKind == FormulaItemKind.STRING }"
                @click="onClickItem(index)"
              >
                {{ fitem.itemName }}
              </span>
              <!-- <span v-if="currentIndex === index + 1 && focus" class="cursor" /> -->
              <input
                v-if="currentIndex === index + 1"
                style="border: none; width: 3px"
                ref="inputRef1"
                v-model="inputValue"
                @input="handleInput"
                @blur="handleBlur"
                @keydown="handleDelete"
              />
            </div>
          </div>
        </el-scrollbar>
      </div>
      <div class="expression-toolbar">
        <el-button type="text" icon="el-icon-back" @click="deleteField()" />
        <el-button type="text" icon="el-icon-delete" @click="clearField()" />
      </div>
    </div>
    <div class="expression-tips">可以从下方变量列表中选择需要的内容填入模版中</div>
    <div class="expression-form">
      <div class="expression-form-row">
        <div class="expression-form-label">变量：</div>
        <div class="expression-form-inline">
          <span
            v-for="(eItem, eIndex) in variableList"
            :key="eIndex"
            class="expression-form-value"
            @click="addField(eItem)"
          >
            {{ eItem.itemName }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { FormulaItemKind } from '@/common/staticDict/index';
import { ANY_OBJECT } from '@/types/generic';

defineOptions({
  name: 'MessageTemplateEditor',
});

interface IProps {
  modelValue?: string;
  options?: ANY_OBJECT;
}
const props = withDefaults(defineProps<IProps>(), {
  modelValue: '',
  options: () => ({}),
});
const emit = defineEmits(['uodate:modelValue', 'change']);
const fieldList = ref([]);
const currentIndex = ref(0);
const currentValue = ref(undefined);
const inputValue = ref(undefined);
const focus = ref(false);
const variableList = computed(() => props.options?.variableList || []);
const operatorList = computed(() => props.options?.operatorList);
const numberList = computed(() => props.options?.numberList);
const currentField = computed(() => fieldList.value[currentIndex.value - 1]);
const inputRef0 = ref(null);
const inputRef1 = ref(null);

// 默认解析
const defaultParse = value => {
  if (value == null || value === '') return [];
  let result = [];
  try {
    result = JSON.parse(value);
  } catch (e) {
    console.error('parse error', e);
  }
  if (Array.isArray(result)) {
    let defaultTime = new Date().getTime();
    result.forEach(item => {
      item.id = defaultTime++;
    });
  }
  return result;
};

// 默认格式化
const defaultFormat = data => {
  if (Array.isArray(data) && data.length > 0) {
    return JSON.stringify(data);
  } else {
    return '';
  }
};

// 删除元素
const deleteField = () => {
  if (currentIndex.value === 0 || currentField.value == null) return;
  if (
    currentField.value.itemKind === FormulaItemKind.NUMBER ||
    currentField.value.itemKind === FormulaItemKind.STRING
  ) {
    if (currentField.value.itemName.length > 1) {
      currentField.value.itemName = currentField.value.itemName.slice(
        0,
        currentField.value.itemName.length - 1,
      );
      return;
    }
  }
  fieldList.value.splice(currentIndex.value - 1, 1);
  currentIndex.value = currentIndex.value - 1;
  editorFocus();
};

// 清空元素
const clearField = () => {
  fieldList.value = [];
  currentIndex.value = 0;
  editorFocus();
};

// 插入元素
const appendField = item => {
  if (item == null) return;
  fieldList.value.splice(currentIndex.value, 0, item);
  currentIndex.value = currentIndex.value + 1;
  editorFocus();
};

// 添加元素
const addField = item => {
  if (item == null) return;
  item = {
    ...item,
  };
  if (currentField.value == null) {
    appendField(item);
    return;
  }
  if (currentField.value.itemKind === FormulaItemKind.NUMBER) {
    if (item.itemKind === FormulaItemKind.NUMBER || item.itemKind === FormulaItemKind.STRING) {
      currentField.value.itemName += item.itemCode;
      // 如果拼接上字符串，元素类型变成字符串元素
      if (item.itemKind === FormulaItemKind.STRING)
        currentField.value.itemKind = FormulaItemKind.STRING;
      editorFocus();
      return;
    }
  } else if (currentField.value.itemKind === FormulaItemKind.STRING) {
    if (item.itemKind === FormulaItemKind.NUMBER || item.itemKind === FormulaItemKind.STRING) {
      currentField.value.itemName += item.itemCode;
      editorFocus();
      return;
    }
  }
  appendField(item);
};

// 选中元素
const onClickItem = index => {
  currentIndex.value = index + 1;
};

// 当公式变化时
const onChange = () => {
  let temp;
  if (props.options?.stringify != null) {
    temp = props.options?.stringify(fieldList.value);
  } else {
    temp = defaultFormat(fieldList.value);
  }
  currentValue.value = temp;
  emit('update:modelValue', temp);
  emit('change', temp);
};

const handleEditorClick = () => {
  editorFocus();
};

const editorFocus = () => {
  setTimeout(() => {
    if (currentIndex.value === 0) {
      inputRef0.value.focus();
    } else {
      inputRef1.value[inputRef1.value.length - 1].focus();
    }
    focus.value = true;
  }, 50);
};

const handleBlur = e => {
  console.log(e, currentIndex.value, inputRef0.value, inputRef1.value);
  if (currentIndex.value === 0) {
    focus.value = e.target === inputRef0.value;
  } else {
    if (inputRef1.value == null) {
      focus.value = false;
    } else {
      focus.value = e.target === inputRef1.value[inputRef1.value.length - 1];
    }
  }
};

const handleInput = e => {
  // console.log('inputValue', inputValue.value, e);
  if (inputValue.value) {
    const item = {
      itemName: inputValue.value,
      itemCode: inputValue.value,
      itemKind: FormulaItemKind.STRING,
    };
    inputValue.value = undefined;
    addField(item);
  }
};

const handleDelete = e => {
  if (e.keyCode === 8) {
    deleteField();
    // 虽然delteField里面会调用聚焦方法，但是删除一次之后会失去焦点
    // 这里重新设置一次才生效
    editorFocus();
  }
};

const itemStyle = item => {
  return item.itemKind === FormulaItemKind.STRING ? { marginRight: 0 } : null;
};

watch(
  () => props.modelValue,
  value => {
    if (currentValue.value === value) return;
    if (props.options?.parse != null) {
      fieldList.value = props.options.parse(value);
    } else {
      fieldList.value = defaultParse(value);
    }
    currentIndex.value = fieldList.value.length;
  },
  { immediate: true },
);

watch(
  () => fieldList.value,
  () => {
    onChange();
  },
  { deep: true, immediate: true },
);

onMounted(() => {
  editorFocus();
});
</script>

<style lang="css" scoped>
.expression-editor {
  --border: 1px solid #dedede;
  --border-radius: 5px;
  background: white;
  display: flex;
  flex-direction: column;
}

.expression-editor {
  width: 100%;
  max-width: 1000px;
  height: 550px;
  margin: 0 auto;
  box-sizing: border-box;
  font-size: 14px;
  /*background-color: #fff;*/
}
.expression-editor * {
  box-sizing: border-box;
}

.expression-box {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 200px;
  border: var(--border);
  border-radius: var(--border-radius);
  padding: 10px 20px 1px 20px;
}

.expression-con {
  display: flex;
  width: 100%;
  height: calc(100% - 40px);
  overflow-y: auto;
  flex-wrap: wrap;
}

.expression-filed {
  display: inline-flex;
  font-size: 12px;
  color: #242424;
  border-radius: 4px;
  cursor: pointer;
  min-height: 26px;
  min-width: 2px;
  align-items: center;
  justify-content: center;
  margin-right: 5px;
  margin-bottom: 5px;
}

.expression-fild-string {
  margin-right: 0 !important;
}

.expression-toolbar {
  width: 100%;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex-direction: row;
}

.expression-btn {
  color: #000;
  padding: 5px 10px;
  cursor: pointer;
}

.expression-tips {
  padding: 10px 0;
  line-height: 1.5;
  font-size: 12px;
  color: #666;
}

.expression-form-row {
  width: 100%;
  display: flex;
  align-items: flex-start;
  justify-content: flex-start;
  flex-direction: row;
  margin: 10px 0;
}

.expression-form-inline {
  display: inline-block;
  flex-wrap: wrap;
  flex-grow: 1;
}

.expression-form-label {
  color: #242424;
  font-size: 12px;
  flex-shrink: 0;
}
.expression-form-value {
  display: inline-flex;
  padding: 2px 8px;
  background-color: #eee;
  font-size: 12px;
  color: #242424;
  border-radius: 4px;
  cursor: pointer;
  min-width: 36px;
  min-height: 36px;
  align-items: center;
  justify-content: center;
  margin: 0 15px 15px 0;
}

.expression-form-value:hover {
  background-color: #dddddd;
}

.btn-save {
  display: inline-block;
  margin-right: 15px;
  padding: 5px 15px;
  background-color: #3b64da;
  color: #fff;
  width: 100px;
  text-align: center;
  font-size: 20px;
  line-height: 1.5;
  cursor: pointer;
}

.filedString {
  line-height: 2;
  font-size: 16px;
  color: orange;
}

.filedString span {
  color: red;
}

.template-edit-box {
  display: flex;
  flex-wrap: wrap;
  width: 100%;
}

.template-item {
  display: inline-block;
  position: relative;
  height: 26px;
  line-height: 26px;
  margin-bottom: 16px;
}

.cursor {
  display: inline-block;
  position: absolute;
  width: 1.5px; /* 光标的宽度 */
  height: 12px; /* 光标的高度，通常与文本行高一致 */
  background-color: black; /* 光标的颜色 */
  animation: blink 1s step-end infinite; /* 使用动画 */
  right: 2px;
  top: 9px;
}

@keyframes blink {
  50% {
    opacity: 0;
  }
}
</style>
