<template>
    <div>
        <span class="label">{{ label }}</span>
        <div class="message-container">
            <el-scrollbar class="message-list" ref="message-list">
                <div class="message-list-content" ref="message-list-content">
                    <div v-for="message in messageList"
                         class="message">
                        <my-message v-if="message.isMine === true"
                                    :name="message.name">
                            <span v-for="part in message.content">
                                <div v-if="part.content !== '\n'" class="message-part">
                                    <el-image v-if="part.type === 'image'"
                                              :src="getImagePath(part.content)"
                                              :preview-src-list="[
                                                  getImagePath(part.content)
                                              ]"
                                              fit="scale-down" />
                                    <span v-else v-html="translateToHtml(
                                        part.content)">
                                    </span>
                                </div>
                                <br v-else />
                            </span>
                        </my-message>
                        <opposite-message v-else-if="message.isMine === false"
                                          :name="message.name">
                            <span v-for="part in message.content">
                                <div v-if="part.content !== '\n'" class="message-part">
                                    <el-image v-if="part.type === 'image'"
                                              :src="getImagePath(part.content)"
                                              :preview-src-list="[
                                                  getImagePath(part.content)
                                              ]"
                                              fit="scale-down" />
                                    <span v-else v-html="translateToHtml(
                                        part.content)">
                                    </span>
                                </div>
                                <br v-else />
                            </span>
                        </opposite-message>
                        <system-info v-else>
                            <span v-for="part in message.content"
                                  v-html="translateToHtml(part.content)">
                            </span>
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
                          @keydown.enter="sendMessage()"
                          resize="none" />
            </el-scrollbar>
            <div class="message-footer">
                <el-button @click="sendMessage()"
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
import { Picture as IconPicture } from '@element-plus/icons-vue'

export default {
    name: 'MessageContainer',
    components: {
        MyMessage, OppositeMessage, SystemInfo, IconPicture
    },
    props: {
        label: null,
        messageType: null,
        name: null,
        websocket: null,
        connected: false,
        sendWebSocketMessage: null,
        online: null
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
        scrollToEnd() {
            setTimeout(() => {
                let scroll = this.$refs['message-list'];
                let contentBox = this.$refs['message-list-content'];
                scroll.setScrollTop(contentBox.scrollHeight + 100);
            }, 100);
        },
        sendMessage() {
            let input = this.input;
            if(input == null || input === '') return;
            if(this.websocket == null) return;
            this.sending = true;
            let content = [];
            let pattern = /@[^@\s]+\s/g;
            let texts = input.split(pattern);
            let ats = input.match(pattern);
            for(let i = 0; i < texts.length; i++) {
                if(texts[i] != null && texts[i] !== '') {
                    content.push({
                        type: RobotMessagePartType.TEXT,
                        content: texts[i]
                    });
                }
                if(ats != null && i < ats.length) {
                    let aOnline = this.findInOnline(ats[i].substring(
                        1, ats[i].length - 1));
                    let qq = aOnline.data.qq;
                    content.push({
                        type: RobotMessagePartType.AT,
                        content: ats[i],
                        extras: {
                            qq
                        }
                    });
                }
            }
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
            if(message.name === this.name) {
                message.isMine = true;
            } else if(message.name != null) {
                message.isMine = false;
            }
            this.messageList.push(message);
            this.scrollToEnd();
        },
        translateToHtml(str) {
            return str.replaceAll(/ /g, '&nbsp;')
            .replaceAll(/\n/g, '<br />');
        },
        getImagePath(name) {
            return process.env.baseUrl + '/image?name=' + name;
        },
        findInOnline(name) {
            for(let aOnline of this.online) {
                if(aOnline.name === name) return aOnline;
            }
            return null;
        }
    }
}
</script>

<style scoped>

</style>
