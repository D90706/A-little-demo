import axios from "axios";
import { ElMessage } from "element-plus";

//创建axios实例
const request = axios.create({
    baseURL: 'http://localhost:8080',//请求路径
    timeout: 30000  // 后台接口超时时间
})

// request 拦截器
// 可以自请求发送前对请求做一些处理
request.interceptors.request.use(
    config => {
        //请求配置对象
        config.headers['Content-Type'] = 'application/json;charset=utf-8';//添加统一的请求头
        return config
    },
    error => {
        //请求失败会走到这里
        return Promise.reject(error)
    }
);
// response 拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
  {
    //响应成功后回调这里
    onFulfilled: response => {
      let res = response.data;//拿到接口返回的数据
      // 兼容服务端返回的字符串数据
      if (typeof res === 'string') {//判断数据是不是字符串类型的
        res = res ? JSON.parse(res) : res//如果是字符串类型则将其转换为json对象
      }
      return res;
    },
    //响应失败后回调这里
    onRejected: error => {
        //判断失败类型，给出错误提示
      if (error.response.status === 404) {
        //路径有问题
        ElMessage.error({ options: '未找到请求接口' })
      } else if (error.response.status === 500) {
        //后端代码有问题
        ElMessage.error({ options: '系统异常，请查看后端控制台报错' })
      } else {
        console.error(error.message)
      }
      return Promise.reject(error)
    }
  }
)
export default request;