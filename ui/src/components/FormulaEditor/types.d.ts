export type FormulaItem = {
  // 公式项唯一id
  id?: string | number;
  itemCode: string;
  itemName: string;
  itemType?: string;
  itemKind?: string;
  errMsg?: string;
  extentData?: ANY_OBJECT;
};

export type FormulaEditorOptions = {
  // 解析公式
  parse?: (formula: string) => Array<FormulaItem>;
  // 序列化公式
  stringify?: (formula: Array<FormulaItem>) => string;
  // 公式可使用变量列表
  variableList?: Array<FormulaItem>;
  // 公式可用操作符列表
  operatorList?: Array<FormulaItem>;
  // 公式可用数字键
  numberList?: Array<FormulaItem>;
  // 公式可用字符键
  keywordList?: Array<FormulaItem>;
};
