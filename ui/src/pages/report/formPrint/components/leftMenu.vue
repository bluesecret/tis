<template>
  <div class="left-menu">
    <div class="left-menu-item active">
      <i class="online-icon icon-component" />
      <span style="margin-top: 4px">模版</span>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.left-menu {
  width: 48px;
  height: 100%;
  padding: 23px 0;
  background: #2d3039;
  .left-menu-item {
    display: flex;
    font-size: 14px;
    text-align: center;
    color: white;
    flex-direction: column;
    justify-items: center;
    font-weight: 400;
    cursor: pointer;

    i {
      width: 34px;
      height: 34px;
      margin-left: 7px;
      font-size: 20px;
      color: white;
      background: $color-primary;
      border-radius: 4px;
      line-height: 34px;
    }
  }
  .left-menu-item.active {
    color: $color-primary;
  }
}
</style>
