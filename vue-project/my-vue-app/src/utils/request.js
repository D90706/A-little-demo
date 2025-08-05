import axios from "axios";
import router from '../router/index' // 导入路由实例，用于跳转登录页

// 创建axios实例
const request = axios.create({
    baseURL: 'http://localhost:8080', // 请求基础路径
    timeout: 30000  // 超时时间30秒
})

// 从localStorage获取Token
const getToken = () => {
    return localStorage.getItem('token')
}

// request 拦截器
request.interceptors.request.use(
    config => {
        // 设置默认请求头
        config.headers['Content-Type'] = 'application/json;charset=utf-8';
        
        // 携带Token，就将其添加到请求头中
        const token = getToken()
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`
        }
        
        return config
    },
    error => {
        console.error('请求准备阶段错误:', error)
        return Promise.reject(error)
    }
);

// response 拦截器
request.interceptors.response.use(
    {
        onFulfilled: response => {
            let res = response.data;
            // 兼容字符串类型响应
            if (typeof res === 'string') {
                try {
                    res = JSON.parse(res)
                } catch (e) {
                    console.warn('响应数据不是有效的JSON格式')
                }
            }
            return res
        },
        onRejected: error => {
            let errorMsg = '请求处理异常'
            
            if (error.response) {
                // 根据后端定义的状态码返回对应提示
                const { status } = error.response
                
                switch (status) {
                    case 404:
                        // 后端定义404表示接口错误（不存在）
                        errorMsg = '接口错误：请求的接口不存在或路径错误'
                        break
                    case 500:
                        // 后端定义500表示请求失败（服务器处理出错）
                        errorMsg = '请求失败：服务器处理请求时发生错误'
                        break
                    case 401:
                        // 身份验证失败（Token无效或未登录）
                        errorMsg = '身份验证失败，请重新登录'
                        localStorage.removeItem('token')
                        router.push('/login')
                        break
                    default:
                        errorMsg = `请求异常（状态码：${status}）`
                }
            } else if (error.request) {
                // 网络错误（无响应）
                errorMsg = '网络连接异常，请检查网络设置'
            } else {
                // 请求配置错误
                errorMsg = `请求配置错误：${error.message}`
            } 
            // 显示错误提示
            alert(errorMsg)
            console.error('请求错误详情:', error)
            return Promise.reject(error)
        }
    }
)

export default request;
