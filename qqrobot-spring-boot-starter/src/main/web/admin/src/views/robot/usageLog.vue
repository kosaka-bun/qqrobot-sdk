<template>
  <div class="usage-log">
    <el-table :data="usageLog" stripe :empty-text="emptyText">
      <el-table-column prop="index" label="序号" min-width="20" />
      <el-table-column prop="datetime" label="时间" width="160" />
      <el-table-column prop="groupName" label="群名" min-width="50"
                       show-overflow-tooltip />
      <el-table-column prop="qq" label="QQ号" width="120" />
      <el-table-column prop="username" label="群名片或昵称" min-width="50"
                       show-overflow-tooltip />
      <el-table-column prop="msg" label="处理的信息" min-width="80"
                       show-overflow-tooltip />
      <el-table-column prop="reply" label="回复信息" min-width="150"
                       show-overflow-tooltip />
    </el-table>
    <div class="pagination-bar">
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        layout="prev, pager, next, jumper"
        :total="pageSize * maxPage"
        @current-change="onCurrentChange" />
    </div>
  </div>
</template>

<script>
import { getUsageLogApi } from '@/api/robot/usageLog'

export default {
  name: 'usageLog',
  data() {
    return {
      usageLog: [],
      currentPage: 1,
      pageSize: 10,
      maxPage: 0,
      emptyText: ''
    }
  },
  methods: {
    onCurrentChange(page) {
      let oldPage = this.currentPage;
      this.currentPage = page;
      this.emptyText = '加载中……';
      getUsageLogApi(page).then(response => {
        this.pageSize = response.data.PAGE_SIZE;
        this.maxPage = response.data.maxPage;
        let list = response.data.list;
        list.forEach((item, index) => {
          item.index = index + 1 + (page - 1) * this.pageSize;
        });
        this.usageLog = list;
      }).catch(error => {
        this.currentPage = oldPage;
      }).finally(() => {
        this.emptyText = '';
      });
    }
  },
  mounted() {
    this.onCurrentChange(this.currentPage);
  }
}
</script>

<style scoped>
.usage-log {
  padding: 1em;
}

.pagination-bar {
  margin-top: 0.5em;
}
</style>
