<template>
    <div>
        <span class="label">{{ label }}</span>
        <div class="message-container">
            <el-scrollbar class="message-list" ref="message-list">
                <div class="message-list-content" ref="message-list-content">
                    <div v-for="message in messageList"
                         class="message">
                        <my-message v-if="message.name === name"
                                    :name="message.name">
                            <div v-html="messageContentToString(
                                            message.content)"></div>
                        </my-message>
                        <opposite-message
                            v-else-if="message.name != null &&
                                       message.name !== ''"
                            :name="message.name">
                            <div v-html="messageContentToString(
                                            message.content)"></div>
                        </opposite-message>
                        <system-info v-else>
                            <div v-html="messageContentToString(
                                            message.content)"></div>
                        </system-info>
                    </div>
                </div>
            </el-scrollbar>
            <el-scrollbar class="message-input">
                <el-input type="textarea"
                          v-model="input"
                          placeholder="回复"
                          :autosize="{ minRows: 7 }"
                          :rows="5"
                          :formatter="noEndWrapFormatter"
                          @keydown.enter="onEnterKey()"
                          resize="none" />
            </el-scrollbar>
            <div class="message-footer">
                <el-button @click="onEnterKey()"
                           :disabled="!connected ||
                                      input == null ||
                                      input === ''"
                           :loading="sending">
                    发送
                </el-button>
            </div>
        </div>
    </div>
</template>

<script>
import MyMessage from '@/pages/index/component/MyMessage'
import OppositeMessage from '@/pages/index/component/OppositeMessage'
import SystemInfo from '@/pages/index/component/SystemInfo'
import RobotMessagePartType from '@/util/robot-message-part-type'
import { reactive } from 'vue'

export default {
    name: 'MessageContainer',
    components: {
        MyMessage, OppositeMessage, SystemInfo
    },
    props: {
        label: null,
        messageType: null,
        name: null,
        websocket: null,
        connected: false,
        sendWebSocketMessage: null
    },
    data() {
        return {
            input: null,
            sending: false,
            messageList: reactive([]),
            noEndWrapFormatter: value => {
                return value.replaceAll(/\n$/g, '');
            }
        }
    },
    methods: {
        messageContentToString(messageContent) {
            let str = '';
            for(let part of messageContent) {
                str += part.content.replaceAll(/\n/g, '<br />');
            }
            return str;
        },
        scrollToEnd() {
            setTimeout(() => {
                let scroll = this.$refs['message-list'];
                let contentBox = this.$refs['message-list-content'];
                scroll.setScrollTop(contentBox.scrollHeight);
            }, 100);
        },
        onEnterKey() {
            let input = this.input;
            if(input == null || input === '') return;
            if(this.websocket == null) return;
            this.sending = true;
            let content = [
                {
                    type: RobotMessagePartType.TEXT,
                    content: input
                }
            ];
            this.sendWebSocketMessage({
                type: this.messageType,
                data: {
                    content
                }
            }, res => {
                if(res.data.status === true) {
                    this.sending = false;
                    this.input = '';
                    this.messageListAppend({
                        name: this.name,
                        content
                    });
                }
            }, () => {
                this.sending = false;
            });
        },
        messageListAppend(message) {
            this.messageList.push(message);
            this.scrollToEnd();
        }
    }
}
</script>

<style scoped>

</style>
