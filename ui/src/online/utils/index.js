import * as rules from '@/common/utils/validate';
import { SysOnlineRuleType } from '@/common/staticDict/index';

export function buildRuleItem(column, ruleItem, trigger = 'blur') {
  if (ruleItem.propDataJson) ruleItem.data = JSON.parse(ruleItem.propDataJson);
  if (column != null && ruleItem != null) {
    switch (ruleItem.onlineRule.ruleType) {
      case SysOnlineRuleType.INTEGER_ONLY:
        return {
          type: 'integer',
          message: ruleItem.data.message,
          trigger: trigger,
          transform: value => Number(value),
        };
      case SysOnlineRuleType.DIGITAL_ONLY:
        return {
          type: 'number',
          message: ruleItem.data.message,
          trigger: trigger,
          transform: value => Number(value),
        };
      case SysOnlineRuleType.LETTER_ONLY:
        return {
          type: 'string',
          pattern: rules.pattern.english,
          message: ruleItem.data.message,
          trigger: trigger,
        };
      case SysOnlineRuleType.EMAIL:
        return { type: 'email', message: ruleItem.data.message, trigger: trigger };
      case SysOnlineRuleType.MOBILE:
        return {
          type: 'string',
          pattern: rules.pattern.mobie,
          message: ruleItem.data.message,
          trigger: trigger,
        };
      case SysOnlineRuleType.RANGE:
        if (column) {
          let isNumber = ['Boolean', 'Date', 'String'].indexOf(column.objectFieldType) === -1;
          return {
            type: isNumber ? 'number' : 'string',
            min: ruleItem.data.min,
            max: ruleItem.data.max,
            message: ruleItem.data.message,
            trigger: trigger,
          };
        }
        break;
      case SysOnlineRuleType.CUSTOM:
        return {
          type: 'string',
          pattern: new RegExp(ruleItem.onlineRule.pattern),
          message: ruleItem.data.message,
          trigger: trigger,
        };
    }
  }
}

export function buildColumnRules(column, name, trigger = 'blur') {
  let rules = [];
  if (column == null) return rules;
  if (!column.nullable) {
    rules.push({ required: true, message: name + '不能为空', trigger: trigger });
  }
  if (column.ruleList != null && column.ruleList.length > 0) {
    column.ruleList.forEach(ruleItem => {
      let rule = buildRuleItem(column, ruleItem, trigger);
      if (rule) rules.push(rule);
    });
  }
  return rules;
}
