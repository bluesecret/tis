export default {
  props: {
    // 标题
    title: {
      type: String,
      default: ''
    },
    // 数据
    data: {
      type: Array,
      default: () => []
    },
    // 指标字段名数组
    indexList: {
      type: Array,
      default: () => []
    },
    // 图例显示名格式化函数
    legendFormatter: {
      type: [Function, String],
      default: '{name}'
    },
    // y轴显示格式化函数
    yAxisFormatter: {
      type: [Function, String],
      default: '{value}'
    },
    // 维度设置，fieldName 字段名，type类型，category类目轴，value数值轴，formatter 指标显示格式化函数
    dimension: {
      type: Object,
      default: () => {
        return {
          fieldName: undefined,
          type: 'category',
          formatter: '{value}'
        };
      }
    },
    // 是否显示提示
    tooltip: {
      type: Boolean,
      default: false
    },
    // 图表配置项（会覆盖上面的选项）
    options: {
      type: Object,
      default: () => {}
    }
  }
};
