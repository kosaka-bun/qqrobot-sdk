<template>
  <div>
    <div class="console-content" v-html="contentHtml">{{ contentHtml }}</div>
    <div style="padding: 0.5em">
      <el-button type="primary" @click="refresh" :icon="refreshBtn.icon"
                 :loading="refreshBtn.loading">刷新</el-button>
    </div>
  </div>
</template>

<script>
import { getConsole } from '@/api/robot/console'

export default {
  name: "console",
  data() {
    return {
      contentHtml: '',
      refreshBtn: {
        icon: 'el-icon-refresh',
        loading: false
      }
    }
  },
  methods: {
    refresh() {
      this.refreshBtn.icon = '';
      this.refreshBtn.loading = true;
      getConsole().then(response => {
        this.contentHtml = '<div class="box">' + response.data + '</div>';
      }).finally(() => {
        this.refreshBtn.icon = 'el-icon-refresh';
        this.refreshBtn.loading = false;
        window.scrollTo(0, document.documentElement.scrollHeight);
      });
    }
  },
  mounted() {
    this.refresh();
  }
}
</script>

<style scoped>
.console-content >>> .box {
  padding: 0.8em;
  background-color: #2B2B2B;
  margin: 0;
  line-height: 1.5em;
}

.console-content >>> pre {
  display: inline;
  font-size: 1.1em;
  font-family: Microsoft YaHei Mono, sans-serif;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
