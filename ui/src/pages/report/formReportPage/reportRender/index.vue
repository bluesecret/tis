<template>
  <div v-show="isReady">
    <ReportForm :height="mainContextHeight - 80 + 'px'" :formConfig="formConfig" />
  </div>
</template>

<script setup lang="ts">
import { ReportPageController } from '@/api/report';
import { ANY_OBJECT } from '@/types/generic';
import ReportForm from './ReportForm/index.vue';
const props = defineProps<{ pageId: string }>();

const mainContextHeight = inject('mainContextHeight', 200);
const isReady = ref(false);
const formConfig = ref<ANY_OBJECT>();

const loadReportPage = () => {
  isReady.value = false;
  ReportPageController.view({
    pageId: props.pageId,
  })
    .then(res => {
      let temp = {
        pageId: res.data.pageId,
        pageName: res.data.pageName,
        pageCode: res.data.pageCode,
        groupId: res.data.groupId,
        pageJson: res.data.pageJson,
        formInfo: res.data.pageJson ? JSON.parse(res.data.pageJson) : {},
      };
      let formInfo = temp.formInfo.pc;
      if (formInfo != null) {
        formConfig.value = {
          pageId: temp.pageId,
          pageName: temp.pageName,
          pageCode: temp.pageCode,
          gutter: formInfo.gutter || 20,
          labelWidth: formInfo.labelWidth || 100,
          labelPosition: formInfo.labelPosition || 'left',
          customFieldList: formInfo.customFieldList || [],
          filterItemWidth: formInfo.filterItemWidth || 350,
          widgetList: formInfo.widgetList || [],
          paramList: formInfo.paramList || [],
        };
        isReady.value = true;
      }
    })
    .catch(e => {
      console.warn(e);
    });
};

watch(
  () => props.pageId,
  () => {
    loadReportPage();
  },
  {
    immediate: true,
  },
);
</script>
