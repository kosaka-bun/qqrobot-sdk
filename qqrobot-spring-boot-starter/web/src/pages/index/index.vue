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
                      @keydown.enter="connect"
                      maxlength="10" />
        </div>
        <div class="input-container">
            <span class="label">用户名：</span>
            <el-input class="input"
                      v-model="form.name"
                      :clearable="true"
                      :disabled="status.connected"
                      @keydown.enter="connect"
                      maxlength="10" />
        </div>
        <el-button @click="connect"
                   :loading="status.connecting"
                   :disabled="status.connected || !checkQqAndName()">
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
                    <message-container
                        ref="group-message-container"
                        label="群聊消息："
                        :message-type="messageType.group"
                        :name="form.name"
                        :websocket="websocket"
                        :connected="status.connected"
                        :online="online"
                        :send-web-socket-message="sendWebSocketMessage" />
                </el-col>
                <el-col :span="12">
                    <message-container
                        ref="private-message-container"
                        label="私聊消息："
                        :message-type="messageType.private"
                        :name="form.name"
                        :websocket="websocket"
                        :connected="status.connected"
                        :online="online"
                        :send-web-socket-message="sendWebSocketMessage" />
                </el-col>
            </el-row>
        </div>
        <div class="margin-top" style="display: flex;">
            <div class="label" style="white-space: nowrap;">当前在线：</div>
            <div v-if="online.length <= 0" class="label">无</div>
            <el-scrollbar v-else class="online-container">
                <div style="display: flex;">
                    <span v-for="item in online" class="label online-user">
                        <el-link type="primary" @click="appendAt(item.name)">
                            {{ `${item.name}(${item.data.qq})` }}
                        </el-link>
                        &nbsp;
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
import MessageContainer from '@/pages/index/component/MessageContainer'

export default {
    name: 'index',
    components: {
        MessageContainer, SystemInfo, OppositeMessage, MyMessage
    },
    data() {
        return {
            form: {
                qq: 12345,
                name: 'abcde'
            },
            status: {
                connected: false,
                connecting: false,
                disconnecting: false
            },
            messageType: {
                group: TesterMessageType.GROUP_MESSAGE,
                private: TesterMessageType.PRIVATE_MESSAGE
            },
            numberFormatter: value => {
                return value.replaceAll(/\D/g, '');
            },
            serverUrl: `${process.env.baseWebsocketUrl}/server`,
            websocket: null,
            websocketMessageCallback: {},
            online: reactive([]),
        }
    },
    methods: {
        connect() {
            if(!this.checkQqAndName()) {
                alertUtils.error('账号和用户名不能为空');
                return;
            }
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
                this.status.connected = false;
                this.status.connecting = false;
                this.status.disconnecting = false;
                this.online = reactive([]);
                let groupContainer = this.$refs['group-message-container'];
                let privateContainer = this.$refs['private-message-container'];
                groupContainer.messageListAppend({
                    name: null,
                    content: [
                        {
                            type: 'text',
                            content: '连接已断开'
                        }
                    ]
                });
                groupContainer.sending = false;
                privateContainer.sending = false;
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
                        this.$refs['group-message-container'].messageListAppend({
                            name: null,
                            content: [
                                {
                                    type: 'text',
                                    content: `${message.data.name}(${
                                        message.data.qq})加入会话`
                                }
                            ]
                        });
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
                        let user = this.online[index];
                        this.$refs['group-message-container'].messageListAppend({
                            name: null,
                            content: [
                                {
                                    type: 'text',
                                    content: `${user.name}(${user.data.qq})已离开`
                                }
                            ]
                        });
                        this.online.splice(index, 1);
                    }
                    break;
                case TesterMessageType.GROUP_MESSAGE:
                    this.$refs['group-message-container']
                        .messageListAppend(message.data);
                    break;
                case TesterMessageType.PRIVATE_MESSAGE:
                    this.$refs['private-message-container']
                        .messageListAppend(message.data);
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
        queryOnline() {
            this.sendWebSocketMessage({
                type: TesterMessageType.QUERY_ONLINE
            }, res => {
                this.online = reactive(res.data.online);
            });
        },
        appendAt(name) {
            let input = this.$refs['group-message-container'].input;
            if(input == null) {
                input = `@${name} `;
            } else {
                input += `@${name} `;
            }
            this.$refs['group-message-container'].input = input;
        },
        checkQqAndName() {
            let arr = [ this.form.qq, this.form.name ];
            for(let item of arr) {
                if(item == null || item === '') return false;
            }
            return true;
        }
    }
}
</script>

<style scoped>

</style>
