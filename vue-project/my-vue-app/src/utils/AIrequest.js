
// const apiKey = 'sk-edbaf7c34c894548bf3d0744db5b5b49'; // 环境变量名需要根据实际情况定义

// test.js
import axios from "axios";

// 创建axios实例
const AIrequest = axios.create({
    baseURL: 'https://api.deepseek.com/chat/completions', // 请求基础路径
    timeout: 300000  // 超时时间30秒
});

// 从环境变量中获取API密钥
const apiKey = 'sk-edbaf7c34c894548bf3d0744db5b5b49'; // 环境变量名需要根据实际情况定义

// request 拦截器
AIrequest.interceptors.request.use(
    config => {
        // 设置默认请求头
        config.headers['Content-Type'] = 'application/json;charset=utf-8';
        // 携带apikey
        config.headers['Authorization'] = `Bearer ${apiKey}`;
        return config;
    },
    error => {
        console.error('请求准备阶段错误:', error);
        return Promise.reject(error);
    }
);

// response 拦截器
AIrequest.interceptors.response.use(
    response => {
        let res = response.data;
        // 兼容字符串类型响应
        if (typeof res === 'string') {
            try {
                res = JSON.parse(res);
            } catch (e) {
                console.warn('响应数据不是有效的JSON格式');
            }
        }
        return res;
    },
    error => {
        let errorMsg = '请求处理异常';

        if (error.response) {
            // 根据后端定义的状态码返回对应提示
            const { status, data } = error.response;
            errorMsg = `请求失败，状态码: ${status}，错误信息: ${data.error || data}`;
        } else if (error.request) {
            // 网络错误（无响应）
            errorMsg = '网络连接异常，请检查网络设置';
        } else {
            // 请求配置错误
            errorMsg = `请求配置错误：${error.message}`;
        }
        // 显示错误提示
        alert(errorMsg);
        console.error('请求错误详情:', error);
        return Promise.reject(error);
    }
);

export default AIrequest;
