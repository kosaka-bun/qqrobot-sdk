<template>
  <div class="home">
    <el-row :gutter="16">
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>内存使用情况</span>
          </template>
          <div>
            <el-table :data="memoryUsage.table">
              <el-table-column prop="rowName" label="" />
              <el-table-column prop="heap" label="堆" />
              <el-table-column prop="nonHeap" label="非堆" />
            </el-table>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>开关</span>
          </template>
          <div class="options">
            <div class="option">
              <el-switch v-model="switcher.sendTestMessageOnRelogin.status"
                         :disabled="switcher.sendTestMessageOnRelogin.disabled"
                         @change="switchSendTestMessageOnRelogin" />
              <span class="switcher-label">重连时发送测试消息</span>
            </div>
            <div class="option">
              <el-switch v-model="switcher.resendOnSendFailed.status"
                         :disabled="switcher.resendOnSendFailed.disabled"
                         @change="switchResendOnSendFailed" />
              <span class="switcher-label">发送失败时重发</span>
            </div>
          </div>
        </el-card>
        <el-card style="margin-top: 16px">
          <template #header>
            <span>操作</span>
          </template>
          <div class="options">
            <div class="option">
              <el-button type="primary" :loading="button.relogin.loading"
                         @click="relogin">重新登录</el-button>
              <el-button type="primary" :loading="button.sendTestMessage.loading"
                         @click="sendTestMessage">发送测试消息</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import {
    getMainInfo,
    reloginApi,
    sendTestMessageApi,
    switchResendOnSendFailedApi,
    switchSendTestMessageOnReloginApi
} from '@/api/robot/home'
import ElementUI from 'element-ui'

export default {
  name: "home",
  data() {
    let memoryUsage = {
      initialSize: {
        rowName: '初始大小',
        heap: '',
        nonHeap: ''
      },
      inUse: {
        rowName: '已使用',
        heap: '',
        nonHeap: ''
      },
      applied: {
        rowName: '已申请内存',
        heap: '',
        nonHeap: ''
      },
      maxSize: {
        rowName: '最大内存',
        heap: '',
        nonHeap: 'N/A'
      },
      percentUsage: {
        rowName: '内存使用率',
        heap: '',
        nonHeap: ''
      }
    }
    return {
      memoryUsage: {
        data: memoryUsage,
        table: [
          memoryUsage.initialSize,
          memoryUsage.inUse,
          memoryUsage.applied,
          memoryUsage.maxSize,
          memoryUsage.percentUsage
        ]
      },
      switcher: {
        sendTestMessageOnRelogin: {
          status: false,
          disabled: true
        },
        resendOnSendFailed: {
          status: false,
          disabled: true
        },
      },
      button: {
        relogin: {
          loading: false
        },
        sendTestMessage: {
          loading: false
        }
      }
    }
  },
  methods: {
    refreshMainInfo() {
      getMainInfo().then(response => {
        let systemInfo = response.data.system_info;
        this.memoryUsage.data.initialSize.heap = systemInfo.heapInit;
        this.memoryUsage.data.initialSize.nonHeap = systemInfo.nonHeapInit;
        this.memoryUsage.data.inUse.heap = systemInfo.heapUsed;
        this.memoryUsage.data.inUse.nonHeap = systemInfo.nonHeapUsed;
        this.memoryUsage.data.applied.heap = systemInfo.heapCommited;
        this.memoryUsage.data.applied.nonHeap = systemInfo.nonHeapCommited;
        this.memoryUsage.data.maxSize.heap = systemInfo.heapMax;
        this.memoryUsage.data.percentUsage.heap = systemInfo.heapPercentUsage;
        this.memoryUsage.data.percentUsage.nonHeap = systemInfo.nonHeapPercentUsage;
        this.switcher.sendTestMessageOnRelogin.status = response.data
            .will_send_test_message_on_relogin;
        this.switcher.sendTestMessageOnRelogin.disabled = false;
        this.switcher.resendOnSendFailed.status = response.data
            .will_resend_on_send_failed;
        this.switcher.resendOnSendFailed.disabled = false;
      });
    },
    switchSendTestMessageOnRelogin() {
      this.switcher.sendTestMessageOnRelogin.disabled = true;
      switchSendTestMessageOnReloginApi().catch(error => {
        this.switcher.sendTestMessageOnRelogin.status =
            !this.switcher.sendTestMessageOnRelogin.status;
      }).finally(() => {
        this.switcher.sendTestMessageOnRelogin.disabled = false;
      });
    },
    switchResendOnSendFailed() {
      this.switcher.resendOnSendFailed.disabled = true;
      switchResendOnSendFailedApi().catch(error => {
        this.switcher.resendOnSendFailed.status =
          !this.switcher.resendOnSendFailed.status;
      }).finally(() => {
        this.switcher.resendOnSendFailed.disabled = false;
      });
    },
    doAction(name, button, action) {
      ElementUI.MessageBox.confirm(
        '确定执行' + name + '吗？', name,
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      ).then(() => {
        button.loading = true;
        action().then(response => {
          ElementUI.Message({
            type: 'success',
            message: name + '执行完成',
          });
        }).finally(() => {
          button.loading = false;
        });
      }).catch(reason => {});
    },
    relogin() {
      this.doAction('重新登录', this.button.relogin, reloginApi);
    },
    sendTestMessage() {
      this.doAction('发送测试信息', this.button.sendTestMessage,
          sendTestMessageApi);
    }
  },
  mounted() {
    this.refreshMainInfo();
  }
}
</script>

<style scoped>
.home {
  min-height: calc(100vh - 50px);
  background-color: #f0f2f5;
  padding: 1em;
}

.options {
  margin-top: -0.25em;
  margin-bottom: -0.25em;
}

.option {
  padding-top: 0.25em;
  padding-bottom: 0.25em;
}

.switcher-label {
  margin-left: 0.5em;
}
</style>
