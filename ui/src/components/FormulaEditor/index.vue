<template>
  <div class="expression-editor">
    <div class="expression-box">
      <div class="expression-con">
        <div style="display: inline">
          <span class="expression-filed">计算公式</span>
          <span class="expression-filed">=</span>
        </div>
        <el-scrollbar style="flex-grow: 1; width: 100px">
          <div class="formula-edit-box">
            <div class="formula-item">
              <span class="expression-filed" @click="onClickItem(-1)"></span>
              <span v-if="currentIndex === 0" class="cursor" />
            </div>
            <div
              class="formula-item"
              v-for="(fitem, index) in fieldList"
              :key="fitem.itemName + '_' + index"
            >
              <span
                :style="{
                  'background-color': fitem.itemKind == FormulaItemKind.VARABLE ? '#eee' : '',
                  padding: fitem.itemKind == FormulaItemKind.VARABLE ? '2px 8px' : '2px 0',
                }"
                class="expression-filed"
                @click="onClickItem(index)"
              >
                {{ fitem.itemName }}
              </span>
              <span v-if="currentIndex === index + 1" class="cursor" />
            </div>
          </div>
        </el-scrollbar>
      </div>
      <div class="expression-toolbar">
        <el-button link :icon="Back" @click="deleteField()" />
        <el-button link :icon="Delete" @click="clearField()" />
      </div>
    </div>
    <div class="expression-tips">
      编辑计算公式可用来完成审批单内数据的自动结算，例如：采购单内设置计算公式“合计=单价x数量”，发起人填写单价、数量后，组件将自动计算出合计金额，免手动计算
    </div>
    <div class="expression-form">
      <div class="expression-form-row">
        <div class="expression-form-label">计算对象：</div>
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
      <div class="expression-form-row">
        <div class="expression-form-label">计算符号：</div>
        <div class="expression-form-inline">
          <span
            v-for="(eItem, eIndex) in operatorList"
            :key="eIndex"
            class="expression-form-value"
            @click="addField(eItem)"
          >
            {{ eItem.itemName }}
          </span>
        </div>
      </div>
      <div class="expression-form-row">
        <div class="expression-form-label">数字键盘：</div>
        <div class="expression-form-inline">
          <template v-for="(eItem, eIndex) in numberList" :key="eIndex">
            <span class="expression-form-value" @click="addField(eItem)">{{ eItem.itemName }}</span>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script name="expression" setup lang="ts">
import { ref, Ref, defineEmits, watch } from 'vue';
import { Back, Delete } from '@element-plus/icons-vue';
import { FormulaItemKind } from '@/common/staticDict/index';
import { FormulaItem, FormulaEditorOptions } from './types';

interface IProps {
  modelValue?: string;
  options?: FormulaEditorOptions;
}
const props = defineProps<IProps>();

const emit = defineEmits('update:modelValue', 'change');

// 运算符号
let defaultOperator: Ref<Array<FormulaItem>> = ref([
  { itemName: '+', itemCode: '+', itemKind: FormulaItemKind.OPERATOR },
  { itemName: '-', itemCode: '-', itemKind: FormulaItemKind.OPERATOR },
  { itemName: '×', itemCode: '*', itemKind: FormulaItemKind.OPERATOR },
  { itemName: '÷', itemCode: '/', itemKind: FormulaItemKind.OPERATOR },
  { itemName: '(', itemCode: '(', itemKind: FormulaItemKind.OPERATOR },
  { itemName: ')', itemCode: ')', itemKind: FormulaItemKind.OPERATOR },
]);
// 数字键盘
let defaultNumberKeyword: Ref<Array<FormulaItem>> = ref([
  { itemName: '1', itemCode: '1', itemKind: FormulaItemKind.NUMBER },
  { itemName: '2', itemCode: '2', itemKind: FormulaItemKind.NUMBER },
  { itemName: '3', itemCode: '3', itemKind: FormulaItemKind.NUMBER },
  { itemName: '4', itemCode: '4', itemKind: FormulaItemKind.NUMBER },
  { itemName: '5', itemCode: '5', itemKind: FormulaItemKind.NUMBER },
  { itemName: '6', itemCode: '6', itemKind: FormulaItemKind.NUMBER },
  { itemName: '7', itemCode: '7', itemKind: FormulaItemKind.NUMBER },
  { itemName: '8', itemCode: '8', itemKind: FormulaItemKind.NUMBER },
  { itemName: '9', itemCode: '9', itemKind: FormulaItemKind.NUMBER },
  { itemName: '0', itemCode: '0', itemKind: FormulaItemKind.NUMBER },
  { itemName: '.', itemCode: '.', itemKind: FormulaItemKind.NUMBER },
]);

// 运算符列表
const operatorList = computed(() => {
  return props.options?.operatorList || defaultOperator.value;
});
// 数字键盘列表
const numberList = computed(() => {
  return props.options?.numberList || defaultNumberKeyword.value;
});
// 输入变量列表
const variableList = computed(() => {
  return (props.options?.variableList || []).map(item => {
    return {
      ...item,
      itemKind: FormulaItemKind.VARABLE,
    };
  });
});

// 计算公式
const fieldList: Ref<Array<FormulaItem>> = ref([]);
// 当前计算公式项
const currentIndex = ref(0);

const currentField = computed(() => {
  return fieldList.value[currentIndex.value - 1];
});

function escapeRegExp(string) {
  return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // $& 表示整个匹配的字符串
}
// 默认的解析方法
/*
const defaultParse = (value: string): Array<FormulaItem> => {
  if (value == null || value === '') return [];
  // 去掉所有空格
  let tempValue = value.replace(/\s+/g, '');
  // 获取所有操作符
  let allKeyword = operatorList.value.map(item => item.itemCode);
  let patternStr = allKeyword.map(item => escapeRegExp(item)).join('|');
  let keyPatternString = patternStr + "|''(\\w+)'|(\\d*(\\.\\d+)?)";
  console.log(keyPatternString, tempValue);
  // 解析字符串、数字以及运算符
  let pattern = new RegExp(keyPatternString, 'g');
  tempValue = tempValue.replace(pattern, (match, p1) => {
    if (match == null || match === '') return '';
    let value = p1 || match;
    console.log('www', value);
    // 寻找是否是运算符
    let tempItem = operatorList.value.find(item => item.itemCode === value);
    if (tempItem == null) {
      // 寻找是否是数字
      if (!isNaN(Number(value))) {
        tempItem = { itemName: value, itemCode: value, itemKind: FormulaItemKind.NUMBER };
      } else {
        tempItem = { itemName: value, itemCode: value, itemKind: FormulaItemKind.STRING };
      }
    }
    if (tempItem != null) {
      return JSON.stringify(tempItem) + ',';
    } else {
      return (p1 || match) + ',';
    }
  });
  // 解析变量
  let variablePatternString = '\\$\\{(\\w+)\\}';
  let variablePattern = new RegExp(variablePatternString, 'g');
  tempValue = tempValue.replace(variablePattern, (match, p1) => {
    let tempItem = variableList.value.find(item => item.itemCode === p1);
    if (tempItem != null) {
      return JSON.stringify(tempItem);
    } else {
      return JSON.stringify({
        itemName: p1 || match,
        itemCode: p1 || match,
        itemKind: FormulaItemKind.VARABLE,
        errMsg: '未知变量',
      });
    }
  });
  // 如果tempValue最后一个字符是逗号，去掉
  if (tempValue[tempValue.length - 1] === ',') {
    tempValue = tempValue.slice(0, tempValue.length - 1);
  }
  tempValue = '[' + tempValue + ']';
  console.log('tempValue', tempValue);
  let result = [];
  try {
    result = JSON.parse(tempValue);
  } catch (e) {
    console.error('parse error', e);
  }
  console.log(result);
  if (Array.isArray(result)) {
    let defaultTime = new Date().getTime();
    result.forEach(item => {
      item.id = defaultTime++;
    });
  }
  return result;
};
// 默认的格式化方法
const defaultFormat = (data: Array<FormulaItem>): string => {
  if (Array.isArray(data) && data.length > 0) {
    return data.reduce((retObj, item) => {
      let temp = '';
      if (item.itemKind === FormulaItemKind.OPERATOR) {
        // 运算符
        temp = item.itemCode;
      } else if (item.itemKind === FormulaItemKind.NUMBER) {
        // 数字
        temp = item.itemName;
      } else if (item.itemKind === FormulaItemKind.STRING) {
        // 字符串
        temp = "'" + item.itemName + "'";
      } else if (item.itemKind === FormulaItemKind.VARABLE) {
        // 变量
        temp = '${' + item.itemCode + '}';
      }
      if (temp !== '' && retObj !== '') {
        retObj += ' ';
      }
      return retObj + temp;
    }, '');
  } else {
    return '';
  }
};
*/

// 默认解析方法
const defaultParse = (value: string): Array<FormulaItem> => {
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
// 默认格式化方法
const defaultFormat = (data: Array<FormulaItem>): string => {
  if (Array.isArray(data) && data.length > 0) {
    return JSON.stringify(data);
  } else {
    return '';
  }
};

// 删除元素
const deleteField = () => {
  if (currentIndex.value === 0) return;
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
};
// 清空元素
const clearField = () => {
  fieldList.value = [];
  currentIndex.value = 0;
};
// 插入元素
const appendField = (item: FormulaItem) => {
  if (item == null) return;
  fieldList.value.splice(currentIndex.value, 0, item);
  currentIndex.value = currentIndex.value + 1;
};
// 添加元素
const addField = (item: FormulaItem) => {
  item = {
    ...item,
  };
  if (item == null) return;
  // 如果是空只能允许(以及非操作元素
  if (fieldList.value.length === 0) {
    if (item.itemCode === '(' || item.itemKind !== FormulaItemKind.OPERATOR) {
      currentIndex.value = currentIndex.value + 1;
      fieldList.value.push(item);
    }
    return;
  }
  if (currentIndex.value === 0) {
    // 往头部加元素
    appendField(item);
    return;
  }
  if (currentField.value.itemKind === FormulaItemKind.VARABLE) {
    // 变量元素后只能跟操作元素
    if (item.itemKind === FormulaItemKind.OPERATOR) {
      appendField(item);
    }
  } else if (currentField.value.itemKind === FormulaItemKind.NUMBER) {
    // 数字元素后面只能跟数字元素、字符串元素或者操作元素
    if (item.itemKind === FormulaItemKind.OPERATOR) {
      appendField(item);
    } else if (
      item.itemKind === FormulaItemKind.NUMBER ||
      item.itemKind === FormulaItemKind.STRING
    ) {
      currentField.value.itemName += item.itemCode;
      // 如果拼接上字符串，元素类型变成字符串元素
      if (item.itemKind === FormulaItemKind.STRING)
        currentField.value.itemKind = FormulaItemKind.STRING;
    }
  } else if (currentField.value.itemKind === FormulaItemKind.STRING) {
    // 字符串元素后面只能跟数字元素、字符串元素以及操作元素
    if (item.itemKind === FormulaItemKind.OPERATOR) {
      appendField(item);
    } else if (
      item.itemKind === FormulaItemKind.NUMBER ||
      item.itemKind === FormulaItemKind.STRING
    ) {
      currentField.value.itemName += item.itemCode;
    }
  } else if (currentField.value.itemKind === FormulaItemKind.OPERATOR) {
    // 括号操作符后面只能跟括号操作以及非操作元素，非括号操作后面只能跟非操作元素
    if (item.itemKind !== FormulaItemKind.OPERATOR) {
      appendField(item);
    } else {
      if (currentField.value.itemCode === '(') {
        // 左括号后面可以跟左括号
        if (item.itemCode === currentField.value.itemCode) {
          appendField(item);
        }
      } else if (currentField.value.itemCode === ')') {
        // 有括号后面不能跟左括号
        if (item.itemCode !== '(') {
          appendField(item);
        }
      } else {
        // 其他操作符后面可以跟左括号
        if (item.itemCode === '(') {
          appendField(item);
        }
      }
    }
  }
  fieldList.value = [...fieldList.value];
};
const currentValue = ref();
// 当公式变化的时候
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
// 点击选中某项元素
const onClickItem = index => {
  currentIndex.value = index + 1;
};

watch(
  () => props.modelValue,
  () => {
    if (currentValue.value === props.modelValue) return;
    if (props.options?.parse != null) {
      fieldList.value = props.options.parse(props.modelValue);
    } else {
      fieldList.value = defaultParse(props.modelValue);
    }
    currentIndex.value = fieldList.value.length;
  },
  {
    immediate: true,
  },
);

watch(
  () => fieldList.value,
  () => {
    onChange();
  },
  {
    immediate: true,
    deep: true,
  },
);
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

.formula-edit-box {
  display: flex;
  flex-wrap: wrap;
  width: 100%;
}

.formula-item {
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
