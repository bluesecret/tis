import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { OnlineFormController, OnlineDictController } from '@/api/online';
import { usePermissions } from '@/common/hooks/usePermission';
import {
  OnlineFormEventType,
  OnlineSystemVariableType,
  SysCustomWidgetBindDataType,
  SysCustomWidgetOperationType,
  SysCustomWidgetType,
  SysOnlineFormType,
  SysOnlineRuleType,
} from '@/common/staticDict';
import {
  SysOnlineFieldKind,
  SysOnlineParamValueType,
  SysOnlineRelationType,
} from '@/common/staticDict/online';
import { formatDate } from '@/common/utils';
import { Dialog } from '@/components/Dialog';
import { useLoginStore } from '@/store';
import { ANY_OBJECT } from '@/types/generic';
import OnlineQueryForm from '@/pages/online/OnlinePageRender/OnlineQueryForm/index.vue';
import OnlineAdcanceQueryForm from '@/pages/online/OnlinePageRender/OnlineAdvanceQueryForm/index.vue';
import OnlineGroupQueryForm from '@/pages/online/OnlinePageRender/OnlineGroupQueryForm/index.vue';
import OnlineEditForm from '@/pages/online/OnlinePageRender/OnlineEditForm/index.vue';
import { useFormConfig } from '@/pages/online/hooks/useFormConfig';
import widgetData from '@/online/config/index';
import { eventFunction } from '@/online/components/CustomEventSetting/event';
import combinedDict from '@/common/staticDict/combined';
import { pattern } from '@/common/utils/validate';
import { post, download } from '@/common/http/request';
import { API_CONTEXT } from '@/api/config';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { buildRuleItem } from '@/online/utils/index.js';
import { useFormExpose } from './useFormExpose';

const StaticDict = { ...combinedDict };

export const useForm = (props: ANY_OBJECT, formRef: Ref<FormInstance> | null = null) => {
  const { buildFormConfig } = useFormConfig();
  const { thirdParams } = useThirdParty(props);

  const isReady = ref(false);

  const dialogParams = computed(() => {
    return {
      formConfig: props.formConfig || thirdParams.value.formConfig,
      rowData: props.rowData || thirdParams.value.rowData,
      masterTableData: props.masterTableData || thirdParams.value.masterTableData,
      isEdit: props.isEdit || thirdParams.value.isEdit || false,
      isCopy: props.isCopy || thirdParams.value.isCopy || false,
      isUpdate: props.isUpdate || thirdParams.value.isUpdate || false,
      updateIdList: props.updateIdList || thirdParams.value.updateIdList || [],
      readOnly: props.readOnly || thirdParams.value.readOnly || false,
      fullscreen: props.fullscreen || thirdParams.value.fullscreen || false,
      saveData:
        (thirdParams.value || {}).saveData == null ? props.saveData : thirdParams.value.saveData,
    };
  });

  const loginStore = useLoginStore();

  const formReadOnly = computed(() => {
    //if (this.dialogParams == null || this.dialogParams.readOnly == null)
    return props.readOnly || false;
    //return this.dialogParams.readOnly;
  });

  const { checkPermCodeExist } = usePermissions();

  const masterTable = computed(() => {
    return dialogParams.value.formConfig.tableMap.get(dialogParams.value.formConfig.masterTableId);
  });
  const isRelation = computed(() => {
    return masterTable.value?.relation != null;
  });
  const formData = reactive<ANY_OBJECT>({
    // 数据字段会根据render信息去初始化
    // 自定义字段
    customField: {},
  });
  const operationCallback = ref<(() => void) | null>(null);

  const getSystemVariableValue = (systemVariableType: number) => {
    switch (systemVariableType) {
      case OnlineSystemVariableType.CURRENT_USER:
        return loginStore.userInfo?.showName;
      case OnlineSystemVariableType.CURRENT_DEPT:
        return loginStore.userInfo?.deptName;
      case OnlineSystemVariableType.CURRENT_DATE:
        return formatDate(new Date(), 'YYYY-MM-DD');
      case OnlineSystemVariableType.CURRENT_TIME:
        return formatDate(new Date(), 'YYYY-MM-DD HH:mm:ss');
      case OnlineSystemVariableType.FLOW_CREATE_USER:
        return (props.flowInfo || {}).processInstanceInitiator || loginStore.userInfo?.showName;
    }
    return undefined;
  };
  const getWidgetValue = (widget: ANY_OBJECT) => {
    if (props.isEdit && widget.bindData.dataType !== SysCustomWidgetBindDataType.SYSTEM_VARIABLE)
      return;
    if (widget.bindData.dataType === SysCustomWidgetBindDataType.Column && widget.column) {
      // 绑定从表字段
      if (widget.relation && formData[widget.relation.variableName]) {
        if (formReadOnly.value || widget.widgetType === SysCustomWidgetType.Label) {
          const dictObj =
            formData[widget.relation.variableName][widget.column.columnName + 'DictMap'];
          if (dictObj != null && dictObj.name) return dictObj.name;
          const dictArray =
            formData[widget.relation.variableName][widget.column.columnName + 'DictMapList'];
          if (Array.isArray(dictArray) && dictArray.length > 0)
            return dictArray.map(item => item.name).join(',');
        }
        return formData[widget.relation.variableName][widget.column.columnName];
      }
      // 绑定主表字段
      if (widget.datasource && formData[widget.datasource.variableName]) {
        if (formReadOnly.value || widget.widgetType === SysCustomWidgetType.Label) {
          const dictObj =
            formData[widget.datasource.variableName][widget.column.columnName + 'DictMap'];
          if (dictObj != null && dictObj.name) return dictObj.name;
          const dictArray =
            formData[widget.datasource.variableName][widget.column.columnName + 'DictMapList'];
          if (Array.isArray(dictArray) && dictArray.length > 0) {
            const temp = dictArray.map(item => item.name).join(',');
            return temp;
          }
        }
        return formData[widget.datasource.variableName][widget.column.columnName];
      }
    } else if (
      widget.bindData.dataType === SysCustomWidgetBindDataType.Custom &&
      widget.bindData.formFieldName
    ) {
      return formData.customField[widget.bindData.formFieldName];
    } else if (
      widget.bindData.dataType === SysCustomWidgetBindDataType.SYSTEM_VARIABLE &&
      widget.bindData.systemVariableType != null
    ) {
      // 系统内置变量
      return getSystemVariableValue(widget.bindData.systemVariableType);
    }
  };
  const getWidgetVisible = widget => {
    const formWidgetAuth: ANY_OBJECT | null =
      formAuth.value && formAuth.value.pc ? formAuth.value.pc[widget.variableName] : null;
    if (formWidgetAuth && formWidgetAuth.hide) return false;
    const fun = getScriptFunction(widget.eventInfo, OnlineFormEventType.VISIBLE);
    if (fun) return fun(useFormExpose(formData, props));
    return true;
  };
  const onValueChange = (widget: ANY_OBJECT, value: ANY_OBJECT) => {
    if (props.isEdit || !isReady.value) return;
    if (widget.bindData.dataType === SysCustomWidgetBindDataType.Column && widget.column) {
      // 绑定从表字段
      if (widget.relation) {
        formData[widget.relation.variableName][widget.column.columnName] = value;
        return;
      }
      // 绑定主表字段
      if (widget.datasource) {
        formData[widget.datasource.variableName][widget.column.columnName] = value;
      }
    } else if (
      widget.bindData.dataType === SysCustomWidgetBindDataType.Custom &&
      widget.bindData.formFieldName
    ) {
      formData.customField[widget.bindData.formFieldName] = value;
    }
  };
  const getScriptFunction = (eventInfo: ANY_OBJECT, eventType: string) => {
    if (eventInfo && typeof eventInfo[eventType] === 'function') {
      return eventInfo[eventType];
    } else {
      return null;
    }
  };
  const onWidgetValueChange = (
    widget: ANY_OBJECT,
    value: ANY_OBJECT | undefined,
    detail: ANY_OBJECT | null,
  ) => {
    if (props.isEdit || !isReady.value) return;
    const dictData = (detail || {}).dictData;
    // 更新字典数据
    if (dictData != null) {
      if (widget.bindData.dataType === SysCustomWidgetBindDataType.Column && widget.column) {
        // 绑定从表字段
        if (widget.relation) {
          if (Array.isArray(dictData)) {
            formData[widget.relation.variableName][widget.column.columnName + 'DictMapList'] =
              dictData;
          } else {
            formData[widget.relation.variableName][widget.column.columnName + 'DictMap'] = dictData;
          }
          const fun = getScriptFunction(widget.eventInfo, OnlineFormEventType.CHANGE);
          fun && fun(value, useFormExpose(formData, props));
          return;
        }
        // 绑定主表字段
        if (widget.datasource) {
          if (Array.isArray(dictData)) {
            formData[widget.datasource.variableName][widget.column.columnName + 'DictMapList'] =
              dictData;
          } else {
            formData[widget.datasource.variableName][widget.column.columnName + 'DictMap'] =
              dictData;
          }
        }
      } else if (
        widget.bindData.dataType === SysCustomWidgetBindDataType.Custom &&
        widget.bindData.formFieldName
      ) {
        if (Array.isArray(dictData)) {
          formData.customField[widget.bindData.formFieldName + 'DictMapList'] = dictData;
        } else {
          formData.customField[widget.bindData.formFieldName + 'DictMap'] = dictData;
        }
      }
    }
    // 一对一关联选择组件
    if (
      widget.widgetType === SysCustomWidgetType.DataSelect &&
      (dialogParams.value.formConfig.formType === SysOnlineFormType.FORM ||
        dialogParams.value.formConfig.formType === SysOnlineFormType.FLOW)
    ) {
      const selectRow = (detail || {}).selectRow;
      const relationId = (widget.props.relativeTable || {}).relationId;
      const relation = dialogParams.value.formConfig.relationMap.get(relationId);
      if (relation != null) {
        if (selectRow != null) {
          Object.keys(selectRow).forEach(key => {
            formData[relation.variableName][key] = selectRow[key];
          });
        } else {
          formData[relation.variableName] = Object.create({});
        }
      }
    }
    const fun = getScriptFunction(widget.eventInfo, OnlineFormEventType.CHANGE);
    fun && fun(value, useFormExpose(formData, props));
  };
  const getPrimaryData = (widget: ANY_OBJECT | null) => {
    let primaryKey;
    if (widget && widget.table && Array.isArray(widget.table.columnList)) {
      widget.table.columnList.forEach((column: ANY_OBJECT) => {
        if (column.primaryKey) primaryKey = column.columnName;
      });
    }
    if (widget && primaryKey != null) {
      if (widget.relation != null) {
        return formData[widget.relation.variableName][primaryKey];
      } else if (widget.datasource != null) {
        return formData[widget.datasource.variableName][primaryKey];
      }
    }
    return undefined;
  };
  const getWidgetValueByColumn = (column: ANY_OBJECT) => {
    if (column == null) return undefined;
    const table = column ? dialogParams.value.formConfig.tableMap.get(column.tableId) : undefined;
    if (table == null || table.datasource == null) return undefined;
    return table.relation == null
      ? formData[table.datasource.variableName][column.columnName]
      : formData[table.relation.variableName][column.columnName];
  };
  const getParamValue = (valueType: number, valueData: string) => {
    switch (valueType) {
      case SysOnlineParamValueType.TABLE_COLUMN: {
        const column = dialogParams.value.formConfig.columnMap
          ? dialogParams.value.formConfig.columnMap.get(valueData)
          : null;
        return column ? getWidgetValueByColumn(column) : undefined;
      }
      case SysOnlineParamValueType.STATIC_DICT:
        return Array.isArray(valueData) ? valueData[1] : undefined;
      case SysOnlineParamValueType.INPUT_VALUE:
        return valueData;
    }
  };
  const getDropdownParams = (widget: ANY_OBJECT) => {
    if (Array.isArray(widget.props.dictInfo.paramList)) {
      const params: ANY_OBJECT = {};
      for (let i = 0; i < widget.props.dictInfo.paramList.length; i++) {
        const dictParam = widget.props.dictInfo.paramList[i];
        if (dictParam.dictValue == null || dictParam.dictValueType == null) continue;
        params[dictParam.dictParamName] = getParamValue(
          dictParam.dictValueType,
          dictParam.dictValue,
        );
      }

      return params;
    } else {
      return {};
    }
  };
  const getOperationPermCode = (operation: ANY_OBJECT | null) => {
    let temp = 'view';
    switch (operation?.type) {
      case SysCustomWidgetOperationType.ADD:
      case SysCustomWidgetOperationType.EDIT:
      case SysCustomWidgetOperationType.DELETE:
      case SysCustomWidgetOperationType.BATCH_DELETE:
      case SysCustomWidgetOperationType.UPDATE:
      case SysCustomWidgetOperationType.UPDATE_DIALOG:
        temp = 'edit';
        break;
      default:
        temp = 'view';
    }
    if (masterTable.value && masterTable.value.datasource) {
      return 'online:' + masterTable.value.datasource.variableName + ':' + temp;
    } else {
      return '';
    }
  };
  const checkOperationPermCode = (operation: ANY_OBJECT | null) => {
    if (dialogParams.value.formConfig.formType !== SysOnlineFormType.QUERY || props.isEdit)
      return true;
    return checkPermCodeExist(getOperationPermCode(operation));
  };
  const checkOperationDisabled = (
    operation: ANY_OBJECT | null,
    rowData: ANY_OBJECT | null = null,
  ) => {
    if (props.isEdit) return false;
    if (operation == null) return true;
    const fun = getScriptFunction(operation.eventInfo, OnlineFormEventType.OPERATION_DISABLED);
    return fun ? fun(rowData, useFormExpose(formData, props)) : false;
  };
  const checkOperationVisible = (
    operation: ANY_OBJECT | null,
    rowData: ANY_OBJECT | null = null,
  ) => {
    if (props.isEdit) return true;
    if (operation == null) return false;
    const fun = getScriptFunction(operation.eventInfo, OnlineFormEventType.OPERATION_VISIBLE);
    return fun ? fun(rowData, useFormExpose(formData, props)) : true;
  };
  const cloneWidget = (widget: ANY_OBJECT) => {
    const attribute = widgetData.getWidgetAttribute(widget.widgetType);
    if (attribute == null) {
      return null;
    }
    const temp = widgetData.getWidgetObject(attribute);
    temp.showName = widget.showName;
    temp.props = {
      ...widget.props,
    };
    return temp;
  };
  const loadOnlineFormConfig = (formId: string) => {
    return new Promise<ANY_OBJECT>((resolve, reject) => {
      OnlineFormController.render({
        formId: formId,
      })
        .then(res => {
          const onlineForm = res.data.onlineForm;
          let formConfigData = JSON.parse(onlineForm.widgetJson);
          formConfigData = formConfigData.pc;
          const formConfig: ANY_OBJECT = {
            rawData: res.data,
            formName: onlineForm.formName,
            formType: onlineForm.formType,
            formKind: onlineForm.formKind,
            masterTableId: onlineForm.masterTableId,
            labelWidth: formConfigData.labelWidth,
            labelPosition: formConfigData.labelPosition,
            filterItemWidth: formConfigData.filterItemWidth,
            gutter: formConfigData.gutter,
            height: formConfigData.height,
            width: formConfigData.width,
            fullscreen: formConfigData.fullscreen,
            advanceQuery: formConfigData.advanceQuery,
            groupQuery: formConfigData.groupQuery,
            widgetList: formConfigData.widgetList,
            operationList: (formConfigData.operationList || []).sort(
              (value1: ANY_OBJECT, value2: ANY_OBJECT) => {
                return (value1.showOrder || 0) - (value2.showOrder || 0);
              },
            ),
            tableWidget: formConfigData.tableWidget,
            leftWidget: formConfigData.leftWidget,
            groupWidget: formConfigData.groupWidget,
            customFieldList: formConfigData.customFieldList,
            formEventList: formConfigData.formEventList,
            maskFieldList: formConfigData.maskFieldList,
            mode: 'pc',
          };
          resolve(formConfig);
        })
        .catch(e => {
          reject(e);
        });
    });
  };
  const getCompoment = (formConfig: ANY_OBJECT, widget: ANY_OBJECT) => {
    if (widget != null && widget.widgetType === SysCustomWidgetType.Table) return OnlineEditForm;
    if (formConfig.formType === SysOnlineFormType.QUERY) return OnlineQueryForm;
    if (formConfig.formKind === SysOnlineFormType.ADVANCE_QUERY) return OnlineAdcanceQueryForm;
    if (formConfig.formKind === SysOnlineFormType.GROUP_QUERY) return OnlineGroupQueryForm;
    return OnlineEditForm;
  };
  /**
   * 执行操作
   * @param {*} operation 操作
   * @param {*} options 配置项
   * @param {*} options.isEdit 是否编辑
   * @param {*} options.saveData 是否把数据保存到数据库
   * @param {*} options.widget 触发组件
   * @param {*} options.rowData 行数据
   * @param {*} options.masterTableData 主表数据
   * @param {*} options.callback 回调
   */
  const handlerOperation = (operation: ANY_OBJECT, operationParams: ANY_OBJECT) => {
    const { isEdit, saveData, widget, rowData, masterTableData, callback } = operationParams;
    loadOnlineFormConfig(operation.formId)
      .then((formConfig: ANY_OBJECT) => {
        let dlgOptions;
        if (formConfig.fullscreen) {
          dlgOptions = {
            area: ['100vw', '100vh'],
            skin: 'fullscreen-dialog',
          };
        } else {
          dlgOptions = {
            area: [
              (formConfig.width ? formConfig.width : 600) + 'px',
              (formConfig.height ? formConfig.height : 500) + 'px',
            ],
          };
        }
        const dlgComponent = getCompoment(formConfig, widget);
        if (dlgComponent == null) {
          return Promise.reject(new Error('错误的操作组件！！！'));
        } else {
          const thirdPath = `thirdOnlineEditForm${widget == null ? '' : '/' + widget.variableName}`;
          operationCallback.value = callback;
          return Dialog.show(
            formConfig.formName,
            dlgComponent,
            dlgOptions,
            {
              formConfig: formConfig,
              rowData: rowData,
              formData: formData,
              masterTableData: masterTableData,
              isEdit: isEdit,
              isCopy: operation.type === SysCustomWidgetOperationType.COPY,
              isUpdate: operationParams.isUpdate,
              updateIdList: operationParams.updateIdList,
              readOnly: operation.readOnly,
              fullscreen: formConfig.fullscreen,
              saveData: saveData,
              path: thirdPath,
            },
            {
              fullscreen: formConfig.fullscreen,
              width: dlgOptions.area[0],
              height: dlgOptions.area[1],
              pathName: '/thirdParty/thirdOnlineEditForm',
            },
          );
        }
      })
      .then(res => {
        if (callback && typeof callback === 'function') {
          callback(res);
        }
        operationCallback.value = null;
      })
      .catch((e: Error) => {
        console.log(e);
      });
  };

  const getTableData = (widget: ANY_OBJECT) => {
    return widget.relation ? formData[widget.relation.variableName] : [];
  };
  const setTableData = (widget: ANY_OBJECT, dataList: ANY_OBJECT[]) => {
    if (widget == null) return;
    if (widget.relation) {
      formData[widget.relation.variableName] = dataList;
    }
  };

  const initPage = () => {
    dialogParams.value.formConfig.tableMap.forEach((table: ANY_OBJECT) => {
      if (table.relation == null) {
        // 主表
        const tempObj = Array.isArray(table.columnList)
          ? table.columnList.reduce((retObj, column) => {
              retObj[column.columnName] = column.objectFieldType === 'Boolean' ? false : undefined;
              return retObj;
            }, {})
          : {};
        formData[table.datasource.variableName] = tempObj;
      } else {
        if (table.relation.relationType === SysOnlineRelationType.ONE_TO_ONE) {
          // 一对一关联从表
          const tempObj = Array.isArray(table.columnList)
            ? table.columnList.reduce((retObj, column) => {
                retObj[column.columnName] =
                  column.objectFieldType === 'Boolean' ? false : undefined;
                return retObj;
              }, {})
            : {};
          formData[table.relation.variableName] = tempObj;
        } else if (table.relation.relationType === SysOnlineRelationType.ONE_TO_MANY) {
          // 一对多关联从表
          if (
            masterTable.value &&
            masterTable.value.relation != null &&
            masterTable.value.relation.relationId === table.relation.relationId
          ) {
            // 表单主表是当前一对多从表
            const tempObj = Array.isArray(table.columnList)
              ? table.columnList.reduce((retObj, column) => {
                  retObj[column.columnName] = undefined;
                  return retObj;
                }, {})
              : {};
            formData[table.relation.variableName] = tempObj;
          } else {
            formData[table.relation.variableName] = [];
          }
        }
      }
    });
    // 初始化自定义字段
    if (Array.isArray(dialogParams.value.formConfig.customFieldList)) {
      dialogParams.value.formConfig.customFieldList.forEach(field => {
        formData.customField[field.fieldName] = undefined;
      });
    }
  };

  let errorMessage: ANY_OBJECT[] = [];
  const richEditWidgetList = reactive<ANY_OBJECT[]>([]);
  const dropdownWidgetList = reactive<ANY_OBJECT[]>([]);
  const tableWidgetList = reactive<ANY_OBJECT[]>([]);

  const getWidgetProp = (widget: ANY_OBJECT) => {
    if (widget.bindData.dataType === SysCustomWidgetBindDataType.Column && widget.column) {
      if (widget.relation && formData[widget.relation.variableName]) {
        return widget.relation.variableName + '.' + widget.column.columnName;
      } else if (widget.datasource && formData[widget.datasource.variableName]) {
        return widget.datasource.variableName + '.' + widget.column.columnName;
      }
    } else if (
      widget.bindData.dataType === SysCustomWidgetBindDataType.Custom &&
      widget.bindData.formFieldName
    ) {
      return 'customField.' + widget.bindData.formFieldName;
    }
  };
  const initWidget = (widget: ANY_OBJECT) => {
    if (widget != null) {
      if (widget.bindData.tableId)
        widget.table = dialogParams.value.formConfig.tableMap.get(widget.bindData.tableId);
      if (widget.bindData.columnId)
        widget.column = dialogParams.value.formConfig.columnMap.get(widget.bindData.columnId);
      if (widget.bindData.dataType === SysCustomWidgetBindDataType.Custom) {
        if (widget.props.dictId != null) {
          widget.dictInfo = dialogParams.value.formConfig.dictMap.get(widget.props.dictId);
        } else {
          // TODO 这里与原代码不一致，原代码走不到这一步
          widget.dictInfo = (widget.column || {}).dictInfo;
        }
      }
      if (widget.table) {
        if (widget.table.datasource) widget.datasource = widget.table.datasource;
        if (widget.table.relation) widget.relation = widget.table.relation;
      }
      if (widget.widgetType === SysCustomWidgetType.RichEditor) {
        richEditWidgetList.push(widget);
      }
      widget.propString = getWidgetProp(widget);

      // 初始化组件下拉字典参数
      if (widget.props.dictInfo && Array.isArray(widget.props.dictInfo.paramList)) {
        widget.props.dictInfo.paramList.forEach((param: ANY_OBJECT) => {
          if (param.dictValueType === SysOnlineParamValueType.STATIC_DICT) {
            let errorItem: ANY_OBJECT | null = null;
            if (Array.isArray(param.dictValue) && param.dictValue.length === 2) {
              const dicts = StaticDict as ANY_OBJECT;
              const staticDict = dicts[param.dictValue[0]];
              if (staticDict == null) {
                errorItem = {
                  widget: widget,
                  message:
                    '组件字典参数' +
                    param.dictParamName +
                    '绑定的静态字典 [' +
                    param.dictValue[0] +
                    '] 并不存在！',
                };
              } else {
                if (staticDict.getValue(param.dictValue[1]) == null) {
                  errorItem = {
                    widget: widget,
                    message:
                      '组件字典参数' +
                      param.dictParamName +
                      '绑定的静态字典值并不属于静态字段 [' +
                      param.dictValue[0] +
                      '] ！',
                  };
                }
              }
            } else {
              errorItem = {
                widget: widget,
                message: '组件字典参数' + param.dictParamName + '绑定的静态字典错误！',
              };
            }
            if (errorItem != null) errorMessage.push(errorItem);
          }
        });
      }
      if (widget.props.dictInfo && widget.props.dictInfo.dictId) {
        widget.props.dictInfo.dict = dialogParams.value.formConfig.dictMap.get(
          widget.props.dictInfo.dictId,
        );
      }
      if (widget.column && widget.column.dictInfo != null) {
        dropdownWidgetList.push(widget);
      }
      // 初始化表格列
      if (widget.widgetType === SysCustomWidgetType.Table) {
        // 寻找表格主键
        widget.primaryColumnName = undefined;
        if (widget.table && Array.isArray(widget.table.columnList)) {
          for (let i = 0; i < widget.table.columnList.length; i++) {
            if (widget.table.columnList[i].primaryKey) {
              widget.primaryColumnName = widget.table.columnList[i].columnName;
              break;
            }
          }
        }
        if (Array.isArray(widget.props.tableColumnList)) {
          widget.props.tableColumnList.forEach((tableColumn: ANY_OBJECT) => {
            tableColumn.table = dialogParams.value.formConfig.tableMap.get(tableColumn.tableId);
            tableColumn.column = dialogParams.value.formConfig.columnMap.get(tableColumn.columnId);
            tableColumn.relation = dialogParams.value.formConfig.relationMap.get(
              tableColumn.relationId,
            );
            if (tableColumn.table == null || tableColumn.column == null) {
              errorMessage.push({
                widget: widget,
                message: '表格列 [' + tableColumn.showName + '] 绑定的字段不存在！',
              });
            }
          });
        }
        // 操作排序
        if (Array.isArray(widget.operationList)) {
          widget.operationList = (widget.operationList || []).sort((value1, value2) => {
            return (value1.showOrder || 0) - (value2.showOrder || 0);
          });
          widget.operationList.forEach((operation: ANY_OBJECT) => {
            operation.eventInfo = (operation.eventList || []).reduce(
              (retObj: ANY_OBJECT, event: ANY_OBJECT) => {
                const fun = eventFunction(event);
                if (fun) retObj[event.eventType] = fun; //fun.bind(this);
                return retObj;
              },
              {},
            );
          });
        }
        tableWidgetList.push(widget);
      }

      if (Array.isArray(widget.childWidgetList)) {
        widget.childWidgetList.forEach(subWidget => {
          initWidget(subWidget);
        });
      }

      if (widget.props && widget.props.dictInfo) {
        if (Array.isArray(widget.props.dictInfo.paramList)) {
          widget.props.dictInfo.paramList.forEach((dictParam: ANY_OBJECT) => {
            if (dictParam.dictValueType === SysOnlineParamValueType.TABLE_COLUMN) {
              let linkageItem = dialogParams.value.formConfig.linkageMap.get(dictParam.dictValue);
              if (linkageItem == null) {
                linkageItem = [];
                dialogParams.value.formConfig.linkageMap.set(dictParam.dictValue, linkageItem);
              }
              if (linkageItem.indexOf(widget) === -1) linkageItem.push(widget);
            }
          });
        }
      }

      if (Array.isArray(widget.eventList)) {
        widget.eventInfo = widget.eventList.reduce((retObj, event) => {
          const fun = eventFunction(event);
          if (fun) retObj[event.eventType] = fun; //fun.bind(this);
          return retObj;
        }, {});
      } else {
        widget.eventInfo = {};
      }
    }
  };

  const initFormWidgetList = () => {
    if (Array.isArray(dialogParams.value.formConfig.operationList)) {
      dialogParams.value.formConfig.operationList.forEach(operation => {
        operation.eventInfo = (operation.eventList || []).reduce(
          (retObj: ANY_OBJECT, event: ANY_OBJECT) => {
            const fun = eventFunction(event);
            if (fun) retObj[event.eventType] = fun; //fun.bind(this);
            return retObj;
          },
          {},
        );
      });
    }
    if (Array.isArray(dialogParams.value.formConfig.formEventList)) {
      dialogParams.value.formConfig.eventInfo = dialogParams.value.formConfig.formEventList.reduce(
        (retObj, event) => {
          const fun = eventFunction(event);
          if (fun) retObj[event.eventType] = fun; //fun.bind(this);
          return retObj;
        },
        {},
      );
    } else {
      dialogParams.value.formConfig.eventInfo = {};
    }
    if (Array.isArray(dialogParams.value.formConfig.widgetList)) {
      dialogParams.value.formConfig.widgetList.forEach(widget => {
        initWidget(widget);
      });
    }
    errorMessage = [];
    if (dialogParams.value.formConfig.tableWidget)
      initWidget(dialogParams.value.formConfig.tableWidget);
    if (dialogParams.value.formConfig.leftWidget)
      initWidget(dialogParams.value.formConfig.leftWidget);
    if (dialogParams.value.formConfig.groupWidget)
      initWidget(dialogParams.value.formConfig.groupWidget);
    if (errorMessage.length > 0) {
      console.error(errorMessage);
    }
  };
  const buildWidgetRule = (widget: ANY_OBJECT, rules: ANY_OBJECT) => {
    if (widget != null) {
      let widgetRuleKey = '';
      if (widget.bindData.dataType === SysCustomWidgetBindDataType.Custom) {
        // 自定义字段
        widgetRuleKey = 'customField.' + widget.bindData.formFieldName;
      } else if (widget.bindData.dataType === SysCustomWidgetBindDataType.Column && widget.column) {
        // 绑定字段
        widgetRuleKey =
          (widget.relation ? widget.relation.variableName : widget.datasource.variableName) +
          '.' +
          widget.column.columnName;
      }
      // 必填字段以及设置了验证规则的字段
      if (
        widgetRuleKey &&
        (widget.props.required || (widget.column && Array.isArray(widget.column.ruleList)))
      ) {
        if (rules) {
          rules[widgetRuleKey] = [];
          // 必填验证
          if (widget.props.required) {
            rules[widgetRuleKey].push({
              required: true,
              message: widget.showName + '不能为空！',
              trigger: 'change',
            });
          }
          // 其他验证
          if (widget.column && Array.isArray(widget.column.ruleList)) {
            widget.column.ruleList.forEach((rule: ANY_OBJECT) => {
              const ruleItem = buildRuleItem(widget.column, rule, 'change');
              if (ruleItem) rules[widgetRuleKey].push(ruleItem);
            });
          }
        }
      }
      if (Array.isArray(widget.childWidgetList)) {
        widget.childWidgetList.forEach(subWidget => {
          buildWidgetRule(subWidget, rules);
        });
      }
    }
  };

  const rules = ref({});
  const initWidgetRule = () => {
    if (!rules.value) {
      rules.value = {};
    }
    dialogParams.value.formConfig.widgetList.forEach((widget: ANY_OBJECT) => {
      buildWidgetRule(widget, rules.value);
    });
    nextTick(() => {
      if (formRef) formRef.value.clearValidate();
    });
  };

  // 获取表忽略脱敏字段列表
  const getTableIgnoreFieldList = (table: ANY_OBJECT, ignoreList: string[] = []) => {
    if (table == null) return;
    if (Array.isArray(table.columnList) && table.columnList.length > 0) {
      table.columnList.forEach(column => {
        if (!column.supportMaskField && column.fieldKind === SysOnlineFieldKind.FIELD_MASK) {
          ignoreList.push(table.tableName + '.' + column.columnName);
        }
      });
    }
  };
  const getIgnoreMaskFields = (widget: ANY_OBJECT | null = null) => {
    const tempList: string[] = [];
    if (widget == null) {
      // 返回所有忽略字段
      dialogParams.value.formConfig.tableMap.forEach((table: ANY_OBJECT) => {
        getTableIgnoreFieldList(table, tempList);
      });
    } else {
      if (widget.relation == null) {
        // 组件绑定的主表，返回主表以及一对一从表忽略字段
        getTableIgnoreFieldList(widget.datasource.masterTable, tempList);
        if (widget.datasource && Array.isArray(widget.datasource.relationList)) {
          widget.datasource.relationList.forEach((relation: ANY_OBJECT) => {
            if (relation.relationType === SysOnlineRelationType.ONE_TO_ONE) {
              getTableIgnoreFieldList(relation.slaveTable, tempList);
            }
          });
        }
      } else {
        // 组件绑定的从表，仅返回从表的忽略字段
        getTableIgnoreFieldList(widget.relation.slaveTable, tempList);
      }
    }
    return tempList.length > 0 ? tempList.join(',') : undefined;
  };

  // TODO initWidgetLinkage
  const resetWidget = widget => {
    if (widget && widget.widgetImpl && typeof widget.widgetImpl.reset === 'function') {
      widget.widgetImpl.reset();
    }
  };
  const initWidgetLinkage = () => {
    dialogParams.value.formConfig.linkageMap.forEach((widgetList: ANY_OBJECT[], key: string) => {
      const column = dialogParams.value.formConfig.columnMap.get(key);
      const table = column ? dialogParams.value.formConfig.tableMap.get(column.tableId) : undefined;
      watch(
        () => {
          return table.relation == null
            ? formData[table.datasource.variableName][column.columnName]
            : formData[table.relation.variableName][column.columnName];
        },
        newValue => {
          if (Array.isArray(widgetList)) {
            widgetList.forEach(widget => {
              resetWidget(widget);
            });
          }
        },
      );
    });
  };
  const getPrintParamItem = (row, printParamList) => {
    let param;
    if (Array.isArray(printParamList)) {
      param = printParamList
        .map(item => {
          const columnId = item.paramValue;
          if (columnId != null) {
            const column = dialogParams.value.formConfig.columnMap.get(columnId);
            const value = row ? (row || {})[column.columnName] : getWidgetValueByColumn(column);
            if (item.paramName != null && value != null) {
              return {
                paramName: item.paramName,
                paramValue: value,
              };
            }
          }
          return null;
        })
        .filter(item => item != null);
    }

    return param;
  };

  // TODO onPrint
  const onPrint = (
    operation: ANY_OBJECT,
    row: ANY_OBJECT | null,
    selectRows: ANY_OBJECT[] | undefined | null,
    fileName: string,
  ) => {
    if (operation == null) return;
    let printParam;
    if (row != null) {
      const temp = getPrintParamItem(row, operation.printParamList);
      printParam = temp ? [temp] : [];
    } else {
      if (selectRows == null || selectRows?.length <= 0) {
        ElMessage.error('请选择要打印的数据！');
        return;
      }
      printParam = selectRows
        .map(row => {
          return getPrintParamItem(row, operation.printParamList);
        })
        .filter(item => item != null);
    }
    const params = {
      datasourceId: masterTable.value.datasource.datasourceId,
      printId: operation.printTemplateId,
      printParams: printParam,
    };
    let tempFileName = fileName || '打印文件';
    tempFileName += operation.printTemplateType === 1 ? '.docx' : '.xlsx';
    post(
      API_CONTEXT + '/online/onlineOperation/print/' + masterTable.value.datasource.variableName,
      params,
    )
      .then(res => {
        const downloadUrl = res.data;
        download(downloadUrl as string, {}, tempFileName, 'get').catch(e => {
          ElMessage.error(e);
        });
      })
      .catch(e => {
        console.error(e);
      });
  };

  const masterTablePrimaryKey = computed(() => {
    if (masterTable.value == null) return null;
    if (Array.isArray(masterTable.value.columnList)) {
      for (let i = 0; i < masterTable.value.columnList.length; i++) {
        if (masterTable.value.columnList[i].primaryKey) {
          return masterTable.value.columnList[i].columnName;
        }
      }
    }
    return null;
  });

  const onStartFlow = (operation: ANY_OBJECT | null, row: ANY_OBJECT | null) => {
    ElMessageBox.confirm('是否要启动流程？', '', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info',
    })
      .then(() => {
        if (
          operation == null ||
          operation.processDefinitionKey == null ||
          masterTablePrimaryKey.value == null ||
          row == null ||
          row[masterTablePrimaryKey.value] == null
        ) {
          ElMessage.error('启动流程失败，缺少必要参数！');
          return;
        }
        const params = {
          id: row[masterTablePrimaryKey.value],
          processDefinitionKey: operation.processDefinitionKey,
          taskVariableData: {},
        };
        // 任务变量
        params.taskVariableData = (operation.taskVariableList || []).reduce((retObj, item) => {
          if (item.variableType === 0) {
            // 固定值
            retObj[item.variableName] = item.variableValue;
          } else if (item.variableType === 1) {
            // 字段值
            retObj[item.variableName] = row[item.variableValue];
          }
          return retObj;
        }, {});
        post(API_CONTEXT + '/flow/flowOnlineOperation/startWithBusinessKey', params)
          .then(() => {
            ElMessage.success('启动成功！');
          })
          .catch(e => {
            console.error(e);
          });
      })
      .catch(e => {
        console.error(e);
      });
  };

  const loadOnlineDictList = () => {
    return new Promise((resolve, reject) => {
      OnlineDictController.list({})
        .then(res => {
          res.data.dataList.forEach(item => {
            item.id = item.dictId;
            item.name = item.dictName;
            item.dictData = item.dictDataJson ? JSON.parse(item.dictDataJson) : undefined;
          });
          resolve(res.data.dataList);
        })
        .catch(e => {
          reject(e);
        });
    });
  };

  const formAuth = ref();
  const form = ref();

  watch(
    () => dialogParams.value.formConfig,
    () => {
      buildFormConfig(dialogParams.value.formConfig);
      if (!dialogParams.value.isEdit) {
        initPage();
        initFormWidgetList();
      }
      form.value = dialogParams.value.formConfig;
    },
    {
      immediate: true,
    },
  );

  return {
    rules,
    isReady,
    dialogParams,
    form,
    formData,
    formAuth,
    masterTable,
    isRelation,
    tableWidgetList,
    richEditWidgetList,
    getWidgetValue,
    getWidgetProp,
    getWidgetVisible,
    onValueChange,
    onWidgetValueChange,
    getPrimaryData,
    getDropdownParams,
    checkOperationPermCode,
    checkOperationDisabled,
    checkOperationVisible,
    cloneWidget,
    handlerOperation,
    loadOnlineFormConfig,
    getTableData,
    setTableData,
    initPage,
    initFormWidgetList,
    initWidgetRule,
    getIgnoreMaskFields,
    initWidgetLinkage,
    onPrint,
    onStartFlow,
    loadOnlineDictList,
  };
};
