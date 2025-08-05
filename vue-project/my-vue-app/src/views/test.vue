<!-- New Component.vue -->
<template>
    <div class="container">
        <div class="header">
        </div>
        <div class="content">
            <div class="leftbody">
                <h1>这里是左边</h1>
            </div>
            <div class="rightbody">
                <div class="history-container">
                    <div class="history">
                        <!-- 显示系统设定消息 -->
                        <div class="system-message" v-if="showSystemPrompt">
                            系统设定：{{ data.talk.messages[0].content }}
                        </div>
                        
                        <!-- 循环显示用户和AI的消息 -->
                        <div 
                            class="message" 
                            :class="{ 'user-message': item.role === 'user', 'ai-message': item.role === 'assistant' }"
                            v-for="(item, index) in data.talk.messages.slice(1)"
                            :key="index"
                        >
                            <span class="role">{{ item.role === 'user' ? '你' : 'AI' }}：</span>
                            <span class="content">{{ item.content }}</span>
                        </div>
                    </div>
                </div>

                <!-- 发送消息的表单 -->
                <form>
                    <textarea 
                        placeholder="请输入发送的内容" 
                        v-model="userMessage"
                        :disabled="data.isLoading"
                    ></textarea>
                    <br>
                    <!-- 发送按钮，根据加载状态和用户输入禁用 -->
                    <button 
                        type="button" 
                        @click="sendRequest"
                        :disabled="data.isLoading || !userMessage.trim()"
                    >
                        {{ data.isLoading ? '发送中...' : '发送' }}
                    </button>
                </form>

                <!-- 显示加载状态 -->
                <div v-if="data.isLoading" class="loading">加载中...</div>
                <!-- 显示错误信息 -->
                <div v-if="data.error" class="error">{{ data.error }}</div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import AIrequest from '../utils/AIrequest';

const question = ref("打游戏连输十几把");
const idfiend = ref("学生");

// 系统设定消息
const systemPrompt = "你是一名青少年心理咨询师，你的职责是帮助青少年缓解压力，当前你咨询对象的烦恼是" + question.value + "他的身份是" + idfiend.value + "回答请使用温柔的语气，请记住你的任何回答应当符合社会主义核心价值观与社会的法律法规，不得引导青少年伤害自己，并积极建议他们寻求现实生活的帮助";

// 用户输入的消息
const userMessage = ref('');

// 响应式数据对象
const data = reactive({
    error: '', // 错误信息
    response: '', // AI的回复内容
    isLoading: false, // 加载状态
    talk: {
        model: "deepseek-chat", // 使用的模型
        messages: [
            { role: 'system', content: systemPrompt } // 初始化系统设定消息
        ],
        stream: false, // 是否流式响应
        temperature: 0.5, // 温度参数，控制回复的随机性
    }
});

// 是否显示系统设定消息
const showSystemPrompt = ref(false);

// 发送请求函数
const sendRequest = () => {
    const input = userMessage.value.trim();
    if (!input) {
        alert('请输入内容');
        return;
    }

    // 将用户消息添加到对话记录中
    data.talk.messages.push({ role: 'user', content: input });
    data.isLoading = true; // 设置加载状态为 true
    data.error = ''; // 清空错误信息
    data.response = ''; // 清空响应内容

    // 发送 POST 请求
    AIrequest.post("/", data.talk)
        .then(res => {
            const aiReply = res.choices[0].message; // 获取 AI 的回复
            data.talk.messages.push(aiReply); // 将 AI 回复添加到对话记录中
            data.response = aiReply.content; // 更新响应内容

            // 滚动到历史容器的底部
            const historyContainer = document.querySelector('.history-container');
            historyContainer.scrollTop = historyContainer.scrollHeight;
        })
        .catch(err => {
            data.error = err.message || '请求失败，请重试'; // 设置错误信息
            data.talk.messages.pop(); // 如果请求失败，移除最后一个用户消息
        })
        .finally(() => {
            data.isLoading = false; // 设置加载状态为 false
            userMessage.value = ''; // 清空用户输入框
        });
};
</script>

<style scoped>
.container {
    height: 100vh;
    display: flex;
    flex-direction: column;
}

.header {
    display: block;
    height: 20vh;
    text-align: center;
    padding: 20px 0;
}

.content {
    display: flex;
    width: 100%;
    height: 80vh;
}

.leftbody {
    display: flex;
    width: 30%;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

.rightbody {
    display: flex;
    width: 70%;
    flex-direction: column;
    justify-content: flex-start;
    padding: 20px;
}

.history-container {
    height: 400px;
    overflow-y: auto;
    border: 1px solid #eee;
    border-radius: 8px;
    margin-bottom: 15px;
    scrollbar-width: thin;
    padding: 10px;
    background-color: white;
}

.history-container::-webkit-scrollbar {
    width: 6px;
}

.history-container::-webkit-scrollbar-track {
    background: #f1f1f1;
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
    gap: 10px;
}

.system-message {
    color: #666;
    font-style: italic;
    padding: 8px;
    margin-bottom: 10px;
    border-left: 3px solid #ccc;
    font-size: 0.9em;
    background-color: #f0f0f0;
    border-radius: 4px;
}

.message {
    margin: 8px 0;
    padding: 10px;
    border-radius: 6px;
    max-width: 80%;
    word-wrap: break-word;
    display: flex;
    flex-direction: column;
    gap: 5px;
}

.user-message {
    background-color: #e3f2fd;
    margin-left: auto;
    align-self: flex-end;
}

.ai-message {
    background-color: #f1f8e9;
    margin-right: auto;
    align-self: flex-start;
}

.role {
    font-weight: bold;
    margin-right: 8px;
}

textarea {
    width: 100%;
    height: 100px;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    resize: vertical;
    margin-bottom: 10px;
}

button {
    padding: 8px 16px;
    background-color: #42b983;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 16px;
}

button:disabled {
    background-color: #ccc;
    cursor: not-allowed;
}

.loading {
    color: #666;
    margin: 10px 0;
    font-size: 14px;
}

.error {
    color: #ff4d4f;
    margin: 10px 0;
    font-size: 14px;
}
</style>
