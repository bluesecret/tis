<template>
  <div ref="root" style="position: absolute; font-size: 14px" />
</template>

<script lang="ts">
const defaultOptions = {
  useWrapMode: false,
  enableSnippets: true,
  useWorker: false,
  enableLiveAutocompletion: true,
  enableBasicAutocompletion: true,
  tabSize: 2,
};
</script>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { edit, Ace } from 'ace-builds';
import { ANY_OBJECT } from '@/types/generic';
// 引入对应的mode
import 'ace-builds/src-noconflict/mode-javascript';
import 'ace-builds/src-noconflict/mode-sql';

// 引入对应的theme
//import 'ace-builds/src-noconflict/theme-xcode';
// 如果要有代码提示，下面这句话必须引入!!!
//import 'ace-builds/src-noconflict/ext-language_tools';
//import 'ace-builds/src-noconflict/worker-javascript';

const emit = defineEmits<{ 'update:value': [string] }>();

const props = withDefaults(defineProps<{ value?: string; type?: string; options?: ANY_OBJECT }>(), {
  type: 'javascript',
  options: () => {
    return defaultOptions;
  },
});
const root = ref();
let editor: Ace.Editor | null = null;

watch(
  () => props.value,
  newValue => {
    //console.log('value change', newValue);
    if (editor && editor.getValue() !== newValue) {
      editor.setValue(newValue || '');
      editor.moveCursorTo(0, 0);
    }
  },
  {
    deep: true,
    immediate: true,
  },
);

onMounted(() => {
  let finalOptions: ANY_OBJECT = {
    ...defaultOptions,
    ...props.options,
  };
  // TODO 语法检查与错误提示
  editor = edit(root.value, finalOptions);
  editor.getSession().setMode('ace/mode/' + props.type);
  //editor.session.setTabSize(finalOptions.tabSize || 2);
  //editor.session.setUseWrapMode(finalOptions.useWrapMode || false);
  editor.getSession().on('change', () => {
    if (editor) {
      emit('update:value', editor.getValue());
    }
  });
  //editor.setReadOnly(finalOptions.readOnly);
  if (props.value) editor.setValue(props.value);
  // editor.setOptions({
  //   enableBasicAutocompletion: true,
  //   enableSnippets: true,
  //   enableLiveAutocompletion: true,
  // });
});

onBeforeUnmount(() => {
  if (editor) {
    editor.destroy();
    editor.container.remove();
    editor = null;
  }
});
</script>
