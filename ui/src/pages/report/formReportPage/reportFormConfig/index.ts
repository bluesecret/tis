const reportFormConfig = {
  pc: {
    gutter: 20,
    labelWidth: 100,
    labelPosition: 'left',
    customFieldList: [],
    widgetList: [],
    paramList: [],
  },
  mobile: {
    customFieldList: [],
    widgetList: [],
    paramList: [],
  },
};

function getReportFormConfig() {
  return JSON.parse(JSON.stringify(reportFormConfig));
}

export { getReportFormConfig };
