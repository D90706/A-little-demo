<template>
    <div class="user">
        <div class="header">
            <h2 class="header-title">青少年心理疏导助手</h2>
        </div>
        <div class="content">
            <div class="left">
                <div class="setting-box">
                    <button type="button" class="setting-btn">修改提示词</button>
                    <button type="button" class="setting-btn exit-btn">退出系统</button>
                    <div v-if="data.isLoading" class="loading">
                        <span class="loading-spinner">●</span> 加载中...
                    </div>
                    <div v-if="data.error" class="error-msg">{{ data.error }}</div>
                </div>
            </div>
            <div class="right">
                <div class="history-container">
                    <div class="history">
                        <div class="system-message" v-if="showSystemPrompt">
                            系统设定：{{ systemPrompt }}
                        </div>
                        <!-- 循环显示用户和AI的消息 -->
                        <div class="message"
                            :class="{ 'user-message': item.role === 'user', 'ai-message': item.role === 'assistant' }"
                            v-for="(item, index) in data.talk.messages.slice(1)" :key="index">
                            <div class="message-header">
                                <span class="role">{{ item.role === 'user' ? '你' : 'AI' }}</span>
                                <span class="time">{{ formatTime() }}</span>
                            </div>
                            <span class="talk-content">{{ item.content }}</span>
                        </div>
                    </div>
                </div>

                <div class="input-container">
                    <form>
                        <textarea dir="ltr" rows="1" autocomplete="off" cols="20" placeholder="请输入想聊的内容..."
                            ref="textareaRef" @input="adjustTextareaHeight" class="chat-textarea" v-model="userMessage"
                            :disabled="data.isLoading"></textarea>
                        <button type="button" class="send-btn" @click="sendRequest"
                            :disabled="data.isLoading || !userMessage.trim()">
                            {{ data.isLoading ? '发送中...' : '发送' }}
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { reactive, ref } from "vue"
import AIrequest from "../utils/AIrequest";

// 系统提示词
const difficulty = "打游戏连输十几把"
const identity = "普通中学生"
const systemPrompt = "你是一名青少年心理咨询师，你的职责是帮助青少年缓解压力，当前你咨询对象的烦恼是" + difficulty + "他的身份是" + identity + "回答时口语化一点并使用温柔的语气，请记住你的任何回答应当符合社会主义核心价值观与社会的法律法规，不得引导青少年伤害自己，并积极建议他们寻求现实生活的帮助";

// 响应式数据
const showSystemPrompt = ref(false);
const userMessage = ref('');
const data = reactive({
    error: '',
    response: '',
    isLoading: false,
    talk: {
        model: "deepseek-chat",
        messages: [{ role: 'system', content: systemPrompt }],
        stream: false,
        temperature: 0.5,
    }
});

// 格式化时间（用于消息显示）
const formatTime = () => {
    const date = new Date();
    return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
};

// 发送请求
const sendRequest = () => {
    const input = userMessage.value.trim();
    if (!input) {
        alert('请输入内容');
        return;
    }

    data.talk.messages.push({ role: 'user', content: input });
    data.isLoading = true;
    data.error = '';
    data.response = '';

    AIrequest.post("/", data.talk)
        .then(res => {
            const aiReply = res.choices[0].message;
            data.talk.messages.push(aiReply);
            data.response = aiReply.content;

            // 滚动到底部
            const historyContainer = document.querySelector('.history-container');
            historyContainer.scrollTop = historyContainer.scrollHeight;
        })
        .catch(err => {
            data.error = err.message || '请求失败，请重试';
            data.talk.messages.pop();
        })
        .finally(() => {
            data.isLoading = false;
            userMessage.value = '';
        });
};

// 文本域自动增高
const textareaRef = ref(null)
const adjustTextareaHeight = () => {
    if (textareaRef.value) {
        textareaRef.value.style.height = '48px';
        textareaRef.value.style.height = textareaRef.value.scrollHeight + 'px';
    }
};
</script>

<style scoped>
/* 全局样式 */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.user {
    display: flex;
    flex-direction: column;
    height: 100vh;
    background-color: #f0f2f5;
    overflow: hidden;
}

/* 头部样式 */
.header {
    height: 60px;
    background-color: #2c3e50;
    color: #fff;
    display: flex;
    justify-content: center;
    align-items: center;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    z-index: 10;
}

.header-title {
    font-size: 1.4em;
    font-weight: 500;
    letter-spacing: 0.5px;
}

/* 内容区布局 */
.content {
    display: flex;
    height: calc(100vh - 60px);
    flex: 1;
}

/* 左侧栏样式 */
.left {
    width: 240px;
    background-color: #2c3e50;
    padding: 20px 10px;
    display: flex;
    flex-direction: column;
}

.setting-box {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.setting-btn {
    height: 42px;
    border: none;
    border-radius: 8px;
    background-color: #34495e;
    color: #ecf0f1;
    font-size: 1em;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
}

.setting-btn:hover {
    background-color: #3d5a7f;
    transform: translateY(-2px);
}

.exit-btn {
    margin-top: 20px;
    background-color: #e74c3c;
}

.exit-btn:hover {
    background-color: #c0392b;
}

/* 加载和错误提示 */
.loading {
    color: #ecf0f1;
    margin-top: 20px;
    padding: 10px;
    font-size: 0.9em;
    display: flex;
    align-items: center;
    gap: 8px;
}

.loading-spinner {
    animation: spin 1.5s infinite;
    font-size: 1.2em;
}

@keyframes spin {
    0% {
        transform: rotate(0deg);
    }

    100% {
        transform: rotate(360deg);
    }
}

.error-msg {
    color: #e74c3c;
    background-color: rgba(231, 76, 60, 0.1);
    padding: 10px;
    border-radius: 6px;
    font-size: 0.9em;
    margin-top: 20px;
}

/* 右侧聊天区样式 */
.right {
    flex: 1;
    background-color: #ecf0f1;
    padding: 20px;
    display: flex;
    flex-direction: column;
}

/* 聊天历史容器 */
.history-container {
    flex: 1;
    background-color: #fff;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    overflow-y: auto;
    margin-bottom: 20px;
}

.history-container::-webkit-scrollbar {
    width: 6px;
}

.history-container::-webkit-scrollbar-track {
    background: #f5f5f5;
    border-radius: 10px;
}

.history-container::-webkit-scrollbar-thumb {
    background: #ccc;
    border-radius: 10px;
}

.history-container::-webkit-scrollbar-thumb:hover {
    background: #aaa;
}

.history {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

/* 系统消息样式 */
.system-message {
    color: #7f8c8d;
    font-style: italic;
    padding: 12px 16px;
    border-left: 4px solid #bdc3c7;
    font-size: 0.9em;
    background-color: #f8f9fa;
    border-radius: 0 8px 8px 0;
    align-self: flex-start;
    max-width: 90%;
}

/* 消息气泡样式 */
.message {
    padding: 14px 16px;
    border-radius: 12px;
    max-width: 75%;
    word-wrap: break-word;
    position: relative;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.user-message {
    background-color: #3498db;
    color: #fff;
    align-self: flex-end;
    border-bottom-right-radius: 4px;
}

.ai-message {
    background-color: #ecf0f1;
    color: #2c3e50;
    align-self: flex-start;
    border-bottom-left-radius: 4px;
}

/* 消息头部（角色+时间） */
.message-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 6px;
    font-size: 0.85em;
}

.role {
    font-weight: 600;
}

.time {
    opacity: 0.7;
    font-size: 0.8em;
}

.talk-content {
    line-height: 1.6;
    font-size: 0.95em;
}

/* 输入区域样式 */
.input-container {
    display: flex;
    align-items: center;
}

form {
    display: flex;
    width: 100%;
    gap: 12px;
}

.chat-textarea {
    flex: 1;
    min-height: 48px;
    max-height: 180px;
    padding: 12px 16px;
    border: 1px solid #bdc3c7;
    border-radius: 24px;
    resize: none;
    font-size: 0.95em;
    transition: all 0.3s ease;
    box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.05);
}

.chat-textarea:focus {
    outline: none;
    border-color: #3498db;
    box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
}

.send-btn {
    height: 48px;
    padding: 0 24px;
    background-color: #3498db;
    color: #fff;
    border: none;
    border-radius: 24px;
    cursor: pointer;
    font-size: 0.95em;
    font-weight: 500;
    transition: all 0.3s ease;
    white-space: nowrap;
}

.send-btn:hover:not(:disabled) {
    background-color: #2980b9;
    transform: translateY(-2px);
}

.send-btn:disabled {
    background-color: #bdc3c7;
    cursor: not-allowed;
}
</style>