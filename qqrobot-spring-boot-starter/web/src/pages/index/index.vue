<template>
    <div class="main">
        <h3 style="margin-top: 0;">QQ Robot Tester</h3>
        <div class="input-container">
            <span class="label">账号：</span>
            <el-input class="input"
                      v-model.number="form.qq"
                      :clearable="true"
                      :formatter="numberFormatter"
                      :disabled="status.connected"
                      maxlength="10" />
        </div>
        <div class="input-container">
            <span class="label">用户名：</span>
            <el-input class="input"
                      v-model="form.name"
                      :clearable="true"
                      :disabled="status.connected"
                      maxlength="10" />
        </div>
        <el-button @click="connect"
                   :loading="status.connecting"
                   :disabled="status.connected ||
                              form.name == null ||
                              form.name === ''">
            连接
        </el-button>
        <el-button @click="disconnect"
                   :loading="status.disconnecting"
                   :disabled="!status.connected" >
            断开
        </el-button>
        <div class="margin-top">
            <el-row :gutter="12">
                <el-col :span="12">
                    <span class="label">群聊消息：</span>
                    <div class="message-container">
                        <el-scrollbar class="message-list"
                                      ref="group-message-list">
                            <div class="message-list-content"
                                 ref="group-message-list-content">
                                <div v-for="message in messageList"
                                     class="message">
                                    <my-message v-if="message.name === form.name"
                                                :name="message.name">
                                        {{ messageContentToString(message.content) }}
                                    </my-message>
                                    <opposite-message
                                        v-else-if="message.name != null &&
                                                   message.name !== ''"
                                        :name="message.name">
                                        {{ messageContentToString(message.content) }}
                                    </opposite-message>
                                    <system-info v-else>
                                        {{ messageContentToString(message.content) }}
                                    </system-info>
                                </div>
                            </div>
                        </el-scrollbar>
                        <el-scrollbar class="message-input">
                            <el-input type="textarea"
                                      v-model="input.group"
                                      placeholder="回复"
                                      :autosize="{ minRows: 7 }"
                                      :rows="5"
                                      :formatter="noEndWrapFormatter"
                                      @keydown.enter="sendGroupMessage()"
                                      resize="none" />
                        </el-scrollbar>
                        <div class="message-footer">
                            <el-button @click="sendGroupMessage"
                                       :disabled="!status.connected ||
                                                  input.group == null ||
                                                  input.group === ''"
                                       :loading="status.group.sending">
                                发送
                            </el-button>
                        </div>
                    </div>
                </el-col>
                <el-col :span="12">
                    <span class="label">私聊消息：</span>
                    <div class="message-container">
                        <el-scrollbar class="message-list">
                            <div class="message-list-content">
                                <opposite-message name="abcdef"
                                                  content="abcdef" />
                            </div>
                        </el-scrollbar>
                        <el-scrollbar class="message-input">
                            <el-input type="textarea"
                                      v-model="input.private"
                                      placeholder="回复"
                                      :autosize="{ minRows: 7 }"
                                      :rows="5"
                                      resize="none" />
                        </el-scrollbar>
                        <div class="message-footer">
                            <el-button @click="sendPrivateMessage"
                                       :disabled="!status.connected ||
                                                  input.private == null ||
                                                  input.private === ''"
                                       :loading="status.private.sending">
                                发送
                            </el-button>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </div>
        <div class="margin-top" style="display: flex;">
            <div class="label" style="white-space: nowrap;">当前在线：</div>
            <div v-if="online.length <= 0" class="label">无</div>
            <el-scrollbar v-else class="online-container">
                <div style="display: flex;">
                    <span v-for="item in online" class="label">
                        {{ item.name }}&emsp;
                    </span>
                </div>
            </el-scrollbar>
        </div>
    </div>
</template>

<script>
import OppositeMessage from './component/OppositeMessage'
import MyMessage from './component/MyMessage'
import alertUtils from '@/util/alert-utils'
import SystemInfo from './component/SystemInfo'
import { reactive } from 'vue'
import TesterMessageType from '@/util/tester-message-type'
import RobotMessagePartType from '@/util/robot-message-part-type'

export default {
    name: 'index',
    components: { SystemInfo, OppositeMessage, MyMessage },
    data() {
        return {
            form: {
                qq: null,
                name: null
            },
            status: {
                connected: false,
                connecting: false,
                disconnecting: false,
                group: {
                    sending: false
                },
                private: {
                    sending: false
                }
            },
            numberFormatter: value => {
                return value.replaceAll(/\D/g, '');
            },
            noEndWrapFormatter: value => {
                return value.replaceAll(/\n$/g, '');
            },
            input: {
                group: null,
                private: null
            },
            serverUrl: `${process.env.baseWebsocketUrl}/tester-framework/server`,
            websocket: null,
            websocketMessageCallback: {},
            online: reactive([]),
            messageList: reactive([])
        }
    },
    methods: {
        connect() {
            this.status.connecting = true;
            //打开一个websocket
            this.websocket = new WebSocket(this.serverUrl);
            //建立连接
            this.websocket.onopen = () => {
                this.login();
            };
            //客户端接收服务端返回的数据
            this.websocket.onmessage = event => {
                let message = JSON.parse(event.data);
                console.log('WebSocket接收：', message);
                if(message.id != null) {
                    this.websocketMessageCallback[message.id](message);
                    delete this.websocketMessageCallback[message.id];
                } else {
                    this.exexuteWebSocketMessage(message);
                }
            };
            //发生错误时
            this.websocket.onerror = event => {
                console.log('WebSocket错误：', event);
            };
            //关闭连接
            this.websocket.onclose = event => {
                alertUtils.info('连接已断开');
                this.websocket = null;
                this.groupMessageListAppend({
                    name: null,
                    content: [
                        {
                            type: 'text',
                            content: '连接已断开'
                        }
                    ]
                });
                this.status.connected = false;
                this.status.connecting = false;
                this.status.disconnecting = false;
                this.online = reactive([]);
            };
        },
        disconnect() {
            this.status.disconnecting = true;
            this.websocket.close();
        },
        sendWebSocketMessage(message, callback, onSendFailed) {
            message.id = crypto.randomUUID();
            try {
                if(callback != null) {
                    this.websocketMessageCallback[message.id] = callback;
                }
                let str = JSON.stringify(message);
                this.websocket.send(str);
                console.log('WebSocket发送：', message);
            } catch(e) {
                console.error(e);
                console.error('WebSocket发送失败：', message);
                delete this.websocketMessageCallback[message.id];
                if(onSendFailed != null) {
                    onSendFailed();
                }
            }
        },
        exexuteWebSocketMessage(message) {
            switch(message.type) {
                case TesterMessageType.NEW_USER_LOGIN:
                    if(message.data.name !== this.form.name) {
                        this.online.push(message.data);
                    }
                    break;
                case TesterMessageType.UESR_LOGOUT:
                    let index = -1;
                    for(let i = 0; i < this.online.length; i++) {
                        let item = this.online[i];
                        if(item.name === message.data.name) {
                            index = i;
                            break;
                        }
                    }
                    if(index !== -1) {
                        this.online.splice(index, 1);
                    }
                    break;
                case TesterMessageType.GROUP_MESSAGE:
                    this.groupMessageListAppend(message.data);
                    break;
            }
        },
        login() {
            this.status.connecting = true;
            this.sendWebSocketMessage({
                type: TesterMessageType.LOGIN,
                data: this.form
            }, res => {
                this.status.connecting = false;
                let data = res.data;
                if(data.status === true) {
                    alertUtils.success('连接成功');
                    this.status.connected = true;
                    this.queryOnline();
                } else {
                    alertUtils.error(data.message);
                }
            }, () => {
                this.status.connecting = false;
            });
        },
        sendGroupMessage() {
            let input = this.input.group;
            if(input == null || input === '') return;
            if(this.websocket == null) return;
            this.status.group.sending = true;
            let content = [
                {
                    type: RobotMessagePartType.TEXT,
                    content: input
                }
            ];
            this.sendWebSocketMessage({
                type: TesterMessageType.GROUP_MESSAGE,
                data: {
                    content
                }
            }, res => {
                if(res.data.status === true) {
                    this.status.group.sending = false;
                    this.input.group = '';
                    this.groupMessageListAppend({
                        name: this.form.name,
                        content
                    });
                }
            }, () => {
                this.status.group.sending = false;
            });
        },
        sendPrivateMessage() {},
        queryOnline() {
            this.sendWebSocketMessage({
                type: TesterMessageType.QUERY_ONLINE
            }, res => {
                this.online = reactive(res.data.online);
            });
        },
        messageContentToString(messageContent) {
            let str = '';
            for(let part of messageContent) {
                str += part.content;
            }
            return str;
        },
        groupMessageListAppend(message) {
            this.messageList.push(message);
            this.scrollToEnd('group');
        },
        scrollToEnd(containerName) {
            setTimeout(() => {
                let scroll = this.$refs[`${containerName}-message-list`];
                let contentBox = this.$refs[`${containerName}-message-list-content`];
                scroll.setScrollTop(contentBox.scrollHeight);
            }, 100);
        }
    }
}
</script>

<style scoped>

</style>
