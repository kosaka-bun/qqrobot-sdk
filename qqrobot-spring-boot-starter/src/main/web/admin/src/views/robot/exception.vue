<template>
  <div class="exception">
    <p>显示最近10条异常记录：</p>
    <p :style="`display: ${loading ? 'block' : 'none'};`">加载中……</p>
    <div class="content">
      <el-card class="card" v-for="(item, index) in exceptionList">
        <template #header>
          <div style="line-height: 1.5em">
            <span>ID：{{item.id}}</span><br />
            <span>{{item.datetime}}</span>
          </div>
        </template>
        <div class="exception-item" v-html="item.exceptionText"></div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { getExceptionApi } from '@/api/robot/exception'

export default {
  name: "exception",
  data() {
    return {
      loading: false,
      exceptionList: []
    }
  },
  methods: {
    refreshExceptions() {
      this.loading = true;
      getExceptionApi().then(response => {
        response.list.forEach((item, index) => {
          item.exceptionText = item.exceptionText.replace(/\</g, "&lt;")
              .replace(/\>/g, "&gt;").replace(/\n/g, "<br>");
        });
        this.exceptionList = response.list;
      }).finally(() => {
        this.loading = false;
      })
    }
  },
  mounted() {
    this.refreshExceptions();
  }
}
</script>

<style scoped>
.exception {
  min-height: calc(100vh - 50px);
  background-color: #f0f2f5;
  padding: 1em;
}

p {
  margin-top: 0;
}

.exception-item {
  font-family: Consolas, serif;
  line-height: 1.25em;
  word-break: break-word;
}

.card {
  margin-bottom: 1em;
}
</style>
