<template>
  <td
    :colspan="itemCell.mergeCellColSpan || 1"
    :rowspan="itemCell.mergeCellRowSpan || 1"
    :style="{
      'border-top': itemCell.hasTopBorder ? '1px solid' + itemCell.topBorderColor : '0',
      'border-right': itemCell.hasRightBorder ? '1px solid' + itemCell.rightBorderColor : '0',
      'border-bottom': itemCell.hasBottomBorder ? '1px solid' + itemCell.bottomBorderColor : '0',
      'border-left': itemCell.hasLeftBorder ? '1px solid' + itemCell.leftBorderColor : '0',
      'background-color': itemCell.backgroundColor
        ? itemCell.backgroundColor + ' !important'
        : undefined,
    }"
  >
    <div
      style="
        display: flex;
        align-items: center;
        justify-content: center;
        word-break: break-all;
        overflow: hidden;
      "
      :style="{
        width:
          itemCell.mergeCell && itemCell.mergeCellColSpan > 1
            ? getWidth(itemCell.colIndex, itemCell.mergeCellColSpan) + 'px'
            : itemCell.colWidth + 'px',
        height:
          itemCell.imageCell && itemCell.mergeCellRowSpan > 1
            ? getHeight(itemRow.rowIndex, itemCell.mergeCellRowSpan) + 'px'
            : itemRow.rowHeight + 'px',
        'font-weight': itemCell.bold ? 'bold' : 'normal',
        'font-style': itemCell.italic ? 'italic' : 'normal',
        'font-size': itemCell.fontSize ? itemCell.fontSize + 'px' : undefined,
        'text-decoration': getTextdecoration(itemCell),
        color: itemCell.fontColor,
        display: 'flex',
        'justify-content': justifyContent[itemCell.horizontalType] || 'center',
        'align-items': alignItems[itemCell.verticalType] || 'center',
      }"
    >
      <template v-if="itemCell.imageCell">
        <img :src="getImgUrl(itemCell.data)" alt="" style="width: 100%; height: 100%" />
      </template>
      <template v-else>
        {{ itemCell.data }}
      </template>
      <!-- 水印图片 -->
      <template
        v-if="
          itemCell.cellValue && itemCell.cellValue.watermark && itemCell.cellValue.watermarkData
        "
      >
        <img
          :src="getImgUrl(itemCell.cellValue.watermarkData)"
          alt=""
          style="z-index: 1"
          :style="{
            width: itemCell.cellValue.watermarkWidth + 'px',
            height: itemCell.cellValue.watermarkHeight + 'px',
            position: 'absolute',
            top: getWatermarkTop(itemCell),
            left: getWatermarkLeft(itemCell),
          }"
        />
      </template>
    </div>
  </td>
</template>

<script>
import { getToken } from '@/common/utils';
export default {
  name: 'previewPrintCell.vue',
  props: {
    itemCell: {
      type: Object,
      require: true,
    },
    itemRow: {
      type: Object,
      require: true,
    },
    rows: {
      type: Array,
      require: true,
    },
  },
  data() {
    return {
      justifyContent: {
        0: 'center',
        1: 'flex-start',
        2: 'flex-end',
      },
      alignItems: {
        0: 'center',
        1: 'flex-start',
        2: 'flex-end',
      },
    };
  },
  methods: {
    getImgUrl(actionName) {
      if (actionName != null && actionName !== '' && actionName.substr(0, 1) === '/') {
        actionName = actionName.substr(1);
      }
      let url = process.env.VUE_APP_SERVER_HOST + actionName + '&Authorization=' + getToken();
      return url;
    },
    getWidth(index, colSpan) {
      let width = 0;
      for (let i = index; i < index + colSpan; i++) {
        width += Number(this.itemRow.cells[i].colWidth);
      }
      return width;
    },
    getHeight(index, rowSpan) {
      let height = 0;
      for (let i = index; i < index + rowSpan; i++) {
        height += Number(this.rows[i].rowHeight);
      }
      // console.log(height, 'height');
      return height;
    },
    getTextdecoration(cell) {
      if (cell.cancelLine && cell.underline) {
        return 'line-through underline';
      } else if (cell.cancelLine) {
        return 'line-through';
      } else if (cell.underline) {
        return 'underline';
      } else {
        return undefined;
      }
    },
    getCellX(itemCell) {
      let width = 0;
      for (let i = 0; i < itemCell.colIndex; i++) {
        width += Number(this.itemRow.cells[i].colWidth) + 3;
      }
      return width;
    },
    getCellY(itemCell) {
      let height = 0;
      for (let i = 0; i < itemCell.rowIndex; i++) {
        height += Number(this.rows[i].rowHeight) + 3;
      }
      return height;
    },
    getWatermarkTop(itemCell) {
      let width = this.getCellY(itemCell);
      return width + itemCell.cellValue.watermarkOffsetY + 'px';
    },
    getWatermarkLeft(itemCell) {
      let height = this.getCellX(itemCell);
      return height + itemCell.cellValue.watermarkOffsetX + 'px';
    },
  },
  mounted() {
    // console.log(this.itemCell.data, this.itemCell, 'itemCell');
  },
};
</script>

<style scoped></style>
