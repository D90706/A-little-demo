<!-- test.vue -->
<template>
    <div class="test">
        <div class="content">
            <form>
                <textarea placeholder="请输入发送的内容" v-model="userMessage"></textarea>
                <br>
                <button type="button" @click="sendRequest">发送</button>
            </form>
            <div v-if="data.isLoading">加载中...</div>
            <div v-if="data.response">{{ data.response }}</div>
            <div v-if="data.error">{{ data.error }}</div>
        </div>
    </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import AIrequest from '../utils/AIrequest';

const set = "你是一个精通艺术的大师,非常擅长苏联艺术";

const userMessage = ref('');

const data = reactive({
    error: '',
    response: '',
    isLoading: false,
    talk: {
        model: "deepseek-chat",
        messages: [
            { role: 'system', content: set }
        ],
        stream: false,
        temperature:1,
    }
});

const sendRequest = () => {
    if (userMessage.value.trim() === '') {
        alert('请输入内容');
        return;
    }
    
    data.talk.messages.push({ role: 'user', content: userMessage.value });
    data.isLoading = true;
    data.error = '';

    AIrequest.post("/", data.talk)
        .then(res => {
            data.response = res.choices[0].message.content; // 假设DeepSeek API返回的格式
        })
        .catch(err => {
            data.error = err.message;
            data.response = '';
        })
        .finally(() => {
            data.isLoading = false;
            userMessage.value = ''; // 清空输入框
        });
};
</script>

<style scoped>
textarea {
    width: 100%;
    height: 100px;
}
</style>
