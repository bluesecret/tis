import Spreadsheet from '@/components/SpreadSheet/index.js';
import ZhCn from '@/components/SpreadSheet/locale/zh-cn.js';
import { ANY_OBJECT } from '@/types/generic';

Spreadsheet.locale('zh-cn', ZhCn);

class Sheet {
  private xs: Spreadsheet;
  constructor(el: string, options: ANY_OBJECT) {
    const op = {
      ...options,
    };
    this.xs = new Spreadsheet(el, op);
    //this.lastRange = null;
  }
  /**
   * 获取数据
   */
  getData() {
    return this.xs.getData();
  }
  /**
   * 设置数据
   */
  setData(data: ANY_OBJECT) {
    // this.xs.validate(data)
    this.xs.loadData(data);
  }
  /**
   * 设置文本内容
   */
  setCellText(ri: number, ci: number, d: string) {
    this.xs.cellText(ri, ci, d).reRender();
  }
  /**
   *
   * @param {int} ri 行号
   * @param {int} ci 列号
   * @param { object } data 设置当前cell的值  {text: '1', v: '源码值', merge: []}
   */
  setCellValue(ri: number, ci: number, data: ANY_OBJECT) {
    // setSelectedCellAttr
    console.log(ri, ci, data, 'in setCellValue');
    this.xs.sheet.data.rows.setCell(ri, ci, data);
    setTimeout(() => {
      this.xs.reRender();
    }, 0);
  }
  /**
   *  获取单元格内容
   * @param {int} ri 行号
   * @param {int} ci 列号
   * @param {int} sheetIndex sheet页index
   */
  getCellValue(ri: number, ci: number, sheetIndex = 0) {
    this.xs.sheet.cell(ri, ci, sheetIndex);
  }
  /**
   *
   * @param {int} sri 开始行下标
   * @param {int} sci 开始列下标
   * @param {int} eri 结束行下标
   * @param {int} eci 结束列下标
   */
  setRangeShow(range: ANY_OBJECT) {
    console.log(range, 'in set range show');
    range = range || {};
    if (!range || range.row.length < 2 || range.column.length < 2) {
      console.error('range illegal');
      return;
    }
    const { row, column } = range;
    if (row.includes(NaN) || column.includes(NaN)) {
      console.error('range illegal');
      return;
    }
    const sri = row[0];
    const sci = column[0];
    const eri = row[1];
    const eci = column[1];
    this.xs.sheet.selector.range.set(sri, sci, eri, eci);
    this.xs.sheet.selector.resetAreaOffset();
    this.xs.sheet.selector.el.show();
    this.xs.reRender();
  }
  /**
   * 清空选区
   */
  clearRange() {
    console.log('clear range');
    const range = this.xs.sheet.data.selector.range;
    this.xs.sheet.data.rows.deleteCells(range);
    this.xs.reRender();
  }
  /**
   *
   * @param {num} sri 起始行
   * @param {num} n 行数
   */
  addRow(sri: number, n = 1) {
    sri = sri || this.xs.sheet.data.rows.len;
    this.xs.sheet.data.rows.insert(+sri, +n);
    this.xs.sheet.reload();
    // this.xs.reRender();
  }
  resize() {
    console.log('... SpreadSheet resize ...');
  }
  on(name: string, fun: () => void) {
    return this.xs.on(name, fun);
  }
}

export default Sheet;
