import { ANY_OBJECT } from '@/types/generic';

export interface InlineTableEditConifg<T, R> {
  // 正在编辑的数据
  rowData: T;
  // 当前行数据备份
  rowCopy: T;
  // 是否行数据发生了变化
  isDirty: boolean;
  // 是否自动新建/编辑下一行
  autoEditNext: boolean;
  // 下一行数据
  nextRow?: T | null;
  // 是否新建
  isNew: boolean;
}

export interface CustomQueryItem {
  tableName?: string;
  columnName?: string;
  valueType: number;
  filterType: string;
  logicOperator: string;
  filterItemValue?: string | number | boolean | Array<string | number>;
}

export interface CustomQueryGroup {
  id: number;
  logicOperator: string;
  filterList: CustomQueryItem[];
}

export interface CustomQueryFieldItem {
  id: number | string;
  name: string;
  dictInfo?: ANY_OBJECT;
  children: CustomQueryFieldItem[];
}

export interface CustomQueryDictItem {
  id: number | string;
  name: string;
  children: ANY_OBJECT[];
}
