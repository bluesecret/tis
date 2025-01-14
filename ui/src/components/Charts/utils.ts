import { nameTranslate } from '@/common/utils';
import { CriteriaFilterType, FilterValueKind, CalculateType } from '@/common/staticDict/report';
import { ANY_OBJECT } from '@/types/generic';

/**
 * 获取可用的过滤类型
 * @param {*} fieldType 字段类型
 * @returns
 */
export function getFilterTypeByFieldType(fieldType: string) {
  return CriteriaFilterType.getList().filter(item => {
    switch (fieldType) {
      case 'Boolean':
        return [CriteriaFilterType.EQ, CriteriaFilterType.NOT_EQ].indexOf(item.id) !== -1;
      case 'String':
        return (
          [
            CriteriaFilterType.EQ,
            CriteriaFilterType.NOT_EQ,
            CriteriaFilterType.LIKE,
            CriteriaFilterType.NOT_NULL,
            CriteriaFilterType.IS_NULL,
            CriteriaFilterType.IN,
            CriteriaFilterType.NOT_IN,
          ].indexOf(item.id) !== -1
        );
      case 'Date':
        return (
          [
            CriteriaFilterType.EQ,
            CriteriaFilterType.GE,
            CriteriaFilterType.GT,
            CriteriaFilterType.LE,
            CriteriaFilterType.LT,
            CriteriaFilterType.BETWEEN,
            CriteriaFilterType.NOT_NULL,
            CriteriaFilterType.IS_NULL,
          ].indexOf(item.id) !== -1
        );
      case 'Integer':
      case 'Long':
      case 'Double':
      case 'BigDecimal':
        return (
          [
            CriteriaFilterType.EQ,
            CriteriaFilterType.NOT_EQ,
            CriteriaFilterType.GE,
            CriteriaFilterType.GT,
            CriteriaFilterType.LE,
            CriteriaFilterType.LT,
            CriteriaFilterType.NOT_NULL,
            CriteriaFilterType.IS_NULL,
            CriteriaFilterType.IN,
            CriteriaFilterType.NOT_IN,
          ].indexOf(item.id) !== -1
        );
      default:
        return [];
    }
  });
}
/**
 * 获取过滤可用的参数值类型
 * @param {*} filterValueList 参数值类型列表
 * @param {*} filterType 过滤类型
 * @param {*} column 过滤字段
 * @returns
 */
export function getValidFilterValueTypeByFieldType(
  filterValueList: number[],
  filterType: number,
  column: ANY_OBJECT,
) {
  if (!Array.isArray(filterValueList)) return [];
  return filterValueList
    .filter(item => {
      switch (column.fieldType) {
        case 'Boolean':
          return (
            [
              FilterValueKind.FORM_PARAM,
              FilterValueKind.WIDGET_DATA,
              FilterValueKind.DICT_DATA,
              FilterValueKind.PRINT_INPUT_PARAM,
              FilterValueKind.INPUT_DATA,
            ].indexOf(item) !== -1
          );
        case 'String':
          return (
            [
              FilterValueKind.FORM_PARAM,
              FilterValueKind.WIDGET_DATA,
              FilterValueKind.DICT_DATA,
              FilterValueKind.COLUMN_DATA,
              FilterValueKind.PRINT_INPUT_PARAM,
              FilterValueKind.INPUT_DATA,
            ].indexOf(item) !== -1
          );
        case 'Date':
          return (
            [
              FilterValueKind.FORM_PARAM,
              FilterValueKind.WIDGET_DATA,
              FilterValueKind.DICT_DATA,
              FilterValueKind.COLUMN_DATA,
              FilterValueKind.PRINT_INPUT_PARAM,
              FilterValueKind.INNER_VARIABLE,
              FilterValueKind.INPUT_DATA,
            ].indexOf(item) !== -1
          );
        case 'Integer':
        case 'Long':
        case 'Double':
        case 'BigDecimal':
          return (
            [
              FilterValueKind.FORM_PARAM,
              FilterValueKind.WIDGET_DATA,
              FilterValueKind.DICT_DATA,
              FilterValueKind.COLUMN_DATA,
              FilterValueKind.PRINT_INPUT_PARAM,
              FilterValueKind.INPUT_DATA,
            ].indexOf(item) !== -1
          );
        default:
          return [];
      }
    })
    .filter(item => {
      // 包含和不包含类型只能使用字典和字段值过滤
      if (filterType === CriteriaFilterType.IN || filterType === CriteriaFilterType.NOT_IN) {
        return item === FilterValueKind.DICT_DATA || item === FilterValueKind.COLUMN_DATA;
      } else {
        return true;
      }
    });
}

export function getValueColumnName(calculateType: number, columnName: string) {
  let calculateName: string | null = null;
  if (columnName == null || columnName === '') return calculateName;
  switch (calculateType) {
    case CalculateType.SUM:
      calculateName = 'sumOf';
      break;
    case CalculateType.COUNT:
      calculateName = 'countOf';
      break;
    case CalculateType.AVG:
      calculateName = 'avgOf';
      break;
    case CalculateType.MIN_BY:
      calculateName = 'minOf';
      break;
    case CalculateType.MAX_BY:
      calculateName = 'maxOf';
      break;
    case CalculateType.STD_DEV:
      calculateName = 'stddevOf';
      break;
    case CalculateType.MEAN_DEV:
      calculateName = 'varpopOf';
      break;
  }
  return calculateName + nameTranslate(columnName, 1);
}

export const getRowDataByColumnName = (row: ANY_OBJECT, columnName: string) => {
  if (row == null) return null;
  if (!Array.isArray(columnName)) {
    const dictName = columnName + '__DictMap';
    return row[dictName] ? row[dictName].name : row[columnName];
  } else {
    let dataValue = columnName.length > 0 ? row : undefined;
    for (let i = 0; i < columnName.length; i++) {
      const name = columnName[i];
      if (name == null || dataValue == null) {
        dataValue = undefined;
        break;
      }
      dataValue = dataValue[name];
    }
    return dataValue;
  }
};

export const fixedValue = (value: number, fix: number) => {
  if (fix === undefined) return value;
  return Number(value.toFixed(fix));
};

export const getRowDataByValueColumns = (row: ANY_OBJECT, valueColumnList: ANY_OBJECT[]) => {
  const temp: number[] = [];
  valueColumnList.forEach(valueColumn => {
    temp.push(fixedValue(row[valueColumn.columnName], valueColumn.fixed));
  });
  return temp;
};

/*
 * @param {object} row  是当前行数据比如 {a: { b: 'xxxx' }}
 * @param {array} columnNameList 列名数组，如：['a','b'];
 * @returns {object} 取出的值
 */
export const getColumnValue = (row: ANY_OBJECT | undefined, columnNameList: string[] | string) => {
  if (!row) return undefined;
  if (Array.isArray(columnNameList)) {
    let dataValue: ANY_OBJECT | undefined = columnNameList.length > 0 ? row : undefined;

    for (let i = 0; i < columnNameList.length; i++) {
      const name = columnNameList[i];
      if (!name || !dataValue) {
        dataValue = undefined;
        break;
      }
      const dictName = name + '__DictMap';
      dataValue = dataValue[dictName] ? dataValue[dictName].name : dataValue[name];
    }
    return dataValue;
  } else {
    const columnName = columnNameList;
    const dictName = columnName + '__DictMap';
    return row[dictName] ? row[dictName].name : row[columnName];
  }
};

export const getColumnName = (columnName: string | string[]) => {
  return Array.isArray(columnName) ? columnName.join('__') : columnName;
};

export const getCategoryColumnValue = (row: ANY_OBJECT, categoryColumnList: ANY_OBJECT[]) => {
  const columnValue: string[] = [];
  categoryColumnList.forEach(categoryColumnItem => {
    columnValue.push(getColumnValue(row, categoryColumnItem.columnName));
  });

  return columnValue.join('\r\n');
};
