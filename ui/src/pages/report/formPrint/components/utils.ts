import { ReportDatasetController } from '@/api/report';
import { treeDataTranslate } from '@/common/utils';
import { ANY_OBJECT } from '@/types/generic';

/**
 * 获取字段树结构
 * @param {*} columnList 字段列表
 * @param {*} allowFieldType 允许返回的字段类型（Object，Array）
 * @param {*} includeChildren 是否返回子字段
 */
const buildColumnTree = (
  columnList: ANY_OBJECT[],
  allowFieldType: string | null,
  includeChildren: boolean,
): ANY_OBJECT[] => {
  if (!Array.isArray(columnList)) return [];
  let allowColumnList: ANY_OBJECT[] = [];
  if (allowFieldType != null && allowFieldType !== '') {
    const columnMap = new Map();
    let root: ANY_OBJECT | null = null;
    let hasRoot = false;
    const childColumnMap: ANY_OBJECT = {};
    // 获取所有允许返回类型的字段
    const tempList = columnList
      .filter(column => {
        columnMap.set(column.columnId, column);
        if (column.parentId != null && column.parentId !== '') {
          if (childColumnMap[column.parentId] == null) childColumnMap[column.parentId] = [];
          childColumnMap[column.parentId].push(column.columnId);
        }
        if (column.parentId == null || column.parentId === '') {
          root = column;
          hasRoot = column.fieldType === allowFieldType;
        }
        return column.fieldType === allowFieldType;
      })
      .map(item => item.columnId);
    if (tempList.length === 0) return [];

    // 如果根字段是非对象字段，并且需要返回对象字段树，那么返回空
    if (!hasRoot && allowFieldType === 'Object') return [];
    // 如果需要返回数组字段，那么过滤掉嵌套的数组字段
    if (allowFieldType === 'Array') {
      if (hasRoot && root && (root as ANY_OBJECT).columId) {
        // 根字段就是数组字段，只返回根字段
        allowColumnList = [(root as ANY_OBJECT).columId];
      } else {
        // 根字段是非数组字段，过滤掉嵌套的数组字段
        tempList.forEach(id => {
          const column = columnMap.get(id);
          // 判断字段父字段是否在列表中
          let bFind = false;
          const parentColumnPath = [column.columnId];
          let parentId = column.parentId;
          while (parentId != null) {
            const parent = columnMap.get(parentId);
            if (tempList.indexOf(parent.columnId) !== -1) {
              bFind = true;
              break;
            }
            parentColumnPath.push(parent.columnId);
            parentId = parent.parentId;
          }
          if (!bFind) {
            parentColumnPath.forEach(columnId => {
              if (allowColumnList.indexOf(columnId) === -1) allowColumnList.push(columnId);
            });
            // 添加子字段
            if (includeChildren) {
              const childColumnList = childColumnMap[column.columnId];
              if (Array.isArray(childColumnList)) {
                allowColumnList = allowColumnList.concat(childColumnList);
              }
            }
          }
        });
      }
    } else {
      tempList.forEach(id => {
        allowColumnList.push(id);
        if (includeChildren) {
          const childColumnList = childColumnMap[id];
          if (Array.isArray(childColumnList)) {
            allowColumnList = allowColumnList.concat(childColumnList);
          }
        }
      });
    }
    allowColumnList = allowColumnList.map(columnId => {
      return columnMap.get(columnId);
    });
    columnMap.clear();
  }
  // 返回树形结构字段
  return treeDataTranslate(
    allowColumnList.map(item => {
      return {
        ...item,
      };
    }),
    'columnId',
    'parentId',
  );
};

/**
 * 获取数据集字段列表
 * @param {*} node 数据集节点
 * @returns
 */
const loadDatasetColumnList = (node: ANY_OBJECT) => {
  return new Promise<ANY_OBJECT[]>((resolve, reject) => {
    if (Array.isArray(node.columnList) && node.columnList.length > 0) {
      resolve(node.columnList);
      return;
    }
    ReportDatasetController.view({
      datasetId: node.slaveDatasetId || node.id,
    })
      .then(res => {
        res.data.columnList.forEach((item: ANY_OBJECT) => {
          item.id = item.columnName;
          item.name = item.columnName;
        });
        node.columnList = res.data.columnList;
        resolve(res.data.columnList);
      })
      .catch(e => {
        reject(e);
      });
  });
};

/**
 * 获取段落绑定数据的过滤字段列表和排序字段列表
 * @param {*} dataset 段落绑定的数据集
 * @param {*} relation 段落绑定的关联
 * @param {*} loop 是否是迭代段落
 * @returns
 */
const getFragmentFilterAndOrderColumn = (
  dataset: ANY_OBJECT | null,
  relation: ANY_OBJECT | null,
  loop: boolean,
) => {
  return new Promise<ANY_OBJECT>((resolve, reject) => {
    let filterColumnTree: ANY_OBJECT[] = [];
    let orderColumnList: ANY_OBJECT[] = [];
    if (dataset) {
      loadDatasetColumnList(dataset)
        .then(columnList => {
          filterColumnTree = [...columnList];
          if (relation) {
            loadDatasetColumnList(relation)
              .then(relationColumnList => {
                const relationNode = {
                  ...relation,
                  children: relationColumnList,
                };
                filterColumnTree.splice(0, 0, relationNode);
                if (loop) orderColumnList = [...relationColumnList];
                resolve({
                  filterColumnTree,
                  orderColumnList,
                });
              })
              .catch(e => {
                reject(e);
              });
          } else {
            if (loop) orderColumnList = [...columnList];
            resolve({
              filterColumnTree,
              orderColumnList,
            });
          }
        })
        .catch(e => {
          reject(e);
        });
    } else {
      resolve({
        filterColumnTree,
        orderColumnList,
      });
    }
  });
};

export { buildColumnTree, loadDatasetColumnList, getFragmentFilterAndOrderColumn };
